package my.database.databasebrokerservice.controller

import my.database.databasebrokerservice.annotations.RequiresRole
import my.database.databasebrokerservice.dto.Lecture
import my.database.databasebrokerservice.dto.Student
import my.database.databasebrokerservice.repositories.StudentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes


@RestController
@RequestMapping("/api/academia/students")
class StudentController {


    companion object {
        const val STUDENT="student"
        const val PROFESSOR="professor"
        const val ADMIN="admin"
    }
    @Autowired
    private lateinit var _studentRepository: StudentRepository
    @RequiresRole(STUDENT, PROFESSOR, ADMIN)
    @RequestMapping(path=["{id}"], method = [RequestMethod.GET])
    fun getStudentById(@PathVariable id: Long): ResponseEntity<EntityModel<Student>> {

            val student = _studentRepository.findById(id)
            if (student.isPresent) {
                val student_dto = student.get()
                val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
                val userRole = request.getAttribute("userRole") as? String

                val model=EntityModel.of(student_dto,
                    linkTo(methodOn(StudentController::class.java).getStudentById(id)).withSelfRel(),
                    linkTo(methodOn(StudentController::class.java).getStudentLectures(id)).withRel("student_lectures"))
                    if(userRole==ADMIN)
                    {
                        val placeholderStudent = Student(
                            id = id,
                            nume = "",
                            prenume = "",
                            email = "",
                            ciclu_studiu = "",
                            an_studiu = 0,
                            grupa = "",
                            assignedLectures = emptyList()
                        )
                        model.add(linkTo(methodOn(StudentController::class.java).deleteStudent(id)).withRel("delete").withType("DELETE"),
                            linkTo(methodOn(StudentController::class.java).updateStudent(id,placeholderStudent)).withRel("update").withType("PUT"),
                            linkTo(methodOn(StudentController::class.java).createStudent(placeholderStudent)).withRel("create").withType("POST"),
                            linkTo(methodOn(StudentController::class.java).patchStudent(id, emptyMap())).withRel("patch").withType("PATCH"))
                    }


                return ResponseEntity.ok(model)
            } else {
                return ResponseEntity.notFound().build()
            }
    }
    @RequiresRole(STUDENT)
    @RequestMapping(path=["/{id}/lectures"], method = [RequestMethod.GET])
    fun getStudentLectures(@PathVariable id: Long): ResponseEntity<CollectionModel<Lecture>> {
            val lectures = _studentRepository.findById(id).get().assignedLectures

            if (lectures.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
            else {
                val model = CollectionModel.of(
                    lectures,
                    linkTo(methodOn(StudentController::class.java).getStudentLectures(id)).withSelfRel()
                )
                for (lecture in lectures) {
                    model.add(
                        linkTo(methodOn(LectureController::class.java).getLectureById(lecture.id)).withRel("lecture_" + lecture.id)
                    )
                }
                return ResponseEntity.ok(model)
            }

    }
    @RequestMapping(path=["/username/{username}"],method = [RequestMethod.GET])
    @RequiresRole(STUDENT)
    fun getStudentByUsername(@PathVariable username: String): ResponseEntity<EntityModel<Student>> {

            val student = _studentRepository.findByEmail(username)
            if (!student.isPresent) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
            } else {
                val methodController = methodOn(StudentController::class.java)
                val student_dto = student.get()

                println(username)
                val model = EntityModel.of(
                    student_dto,
                    linkTo(methodController.getStudentByUsername(username)).withSelfRel(),
                    linkTo(methodController.getStudentById(student_dto.id)).withRel("student_id"),
                    linkTo(methodController.getStudentLectures(student_dto.id)).withRel("student_lectures")
                )
                return ResponseEntity.ok(model)
            }
    }

    @RequiresRole(ADMIN)
    @RequestMapping(path = ["/{id}"], method = [RequestMethod.PUT])
    fun updateStudent(@PathVariable id: Long, @RequestBody receivedEntity: Student): ResponseEntity<EntityModel<Student>> {

        if (receivedEntity.nume.isBlank() || receivedEntity.prenume.isBlank() || receivedEntity.email.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build()
        }
        if (!receivedEntity.email.contains("@")) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build()
        }

        val existingEntity = _studentRepository.findById(id)

        return if (existingEntity.isPresent) {
            try {
                val currentEntity = existingEntity.get()
                val updatedEntity = Student(
                    id = currentEntity.id,
                    nume = receivedEntity.nume,
                    prenume = receivedEntity.prenume,
                    email = receivedEntity.email,
                    ciclu_studiu = receivedEntity.ciclu_studiu,
                    an_studiu = receivedEntity.an_studiu,
                    grupa = receivedEntity.grupa,
                    assignedLectures = receivedEntity.assignedLectures
                )

                val savedEntity = _studentRepository.save(updatedEntity)
                val model = EntityModel.of(
                    savedEntity,
                    linkTo(methodOn(StudentController::class.java).getStudentById(savedEntity.id)).withSelfRel()
                )
                ResponseEntity.status(HttpStatus.NO_CONTENT).body(model)
            } catch (e: DataIntegrityViolationException) {
                ResponseEntity.status(HttpStatus.CONFLICT).build()
            }
        } else {
            try {
                val newEntity = Student(
                    id = 0,
                    nume = receivedEntity.nume,
                    prenume = receivedEntity.prenume,
                    email = receivedEntity.email,
                    ciclu_studiu = receivedEntity.ciclu_studiu,
                    an_studiu = receivedEntity.an_studiu,
                    grupa = receivedEntity.grupa,
                    assignedLectures = receivedEntity.assignedLectures
                )

                val savedEntity = _studentRepository.save(newEntity)
                val model = EntityModel.of(savedEntity,
                    linkTo(methodOn(StudentController::class.java).getStudentById(savedEntity.id)).withSelfRel())

                ResponseEntity.status(HttpStatus.CREATED).body(model)
            } catch (e: DataIntegrityViolationException) {
                ResponseEntity.status(HttpStatus.CONFLICT).build()
            }
        }
    }

    @RequiresRole(ADMIN)
    @RequestMapping(method = [RequestMethod.POST])
    fun createStudent(@RequestBody receivedEntity: Student): ResponseEntity<EntityModel<Student>> {

        if (receivedEntity.nume.isBlank() || receivedEntity.prenume.isBlank() || receivedEntity.email.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build()
        }

        if (!receivedEntity.email.contains("@")) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build()
        }

        return try {
            val savedEntity = _studentRepository.save(receivedEntity)

            val model = EntityModel.of(savedEntity,
                linkTo(methodOn(StudentController::class.java).getStudentById(savedEntity.id)).withSelfRel())
            ResponseEntity.status(HttpStatus.CREATED).body(model)
        } catch (e: DataIntegrityViolationException) {
            ResponseEntity.status(HttpStatus.CONFLICT).build()
        }
    }

    @RequiresRole(ADMIN)
    @RequestMapping(path = ["/{id}"], method = [RequestMethod.DELETE])
    fun deleteStudent(@PathVariable id: Long): ResponseEntity<EntityModel<Student>> {
        val existingEntity = _studentRepository.findById(id)
        println(existingEntity)
        return if (existingEntity.isPresent) {
            val student = existingEntity.get()

            val modelStudent= Student(
                id = student.id,
                nume = student.nume,
                prenume = student.prenume,
                email = student.email,
                ciclu_studiu = student.ciclu_studiu,
                an_studiu = student.an_studiu,
                grupa = student.grupa,
                assignedLectures = student.assignedLectures
            )
            val model = EntityModel.of(
                modelStudent,
                linkTo(methodOn(StudentController::class.java).getStudentById(id)).withSelfRel()
            )
            _studentRepository.delete(student)

            ResponseEntity.ok(model)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @RequiresRole(ADMIN)
    @RequestMapping(path = ["/{id}"], method = [RequestMethod.PATCH])
    fun patchStudent(@PathVariable id: Long, @RequestBody patchFields: Map<String, Any>): ResponseEntity<EntityModel<Student>> {
        val existingStudentOptional = _studentRepository.findById(id)

        return if (existingStudentOptional.isPresent) {
            val student = existingStudentOptional.get()

            // Create a new Student object with updated fields
            val updatedStudent = Student(
                id = student.id, // Preserve the ID
                nume = patchFields["nume"] as? String ?: student.nume,
                prenume = patchFields["prenume"] as? String ?: student.prenume,
                email = patchFields["email"] as? String ?: student.email,
                ciclu_studiu = patchFields["ciclu_studiu"] as? String ?: student.ciclu_studiu,
                an_studiu = (patchFields["an_studiu"] as? Number)?.toInt() ?: student.an_studiu,
                grupa = patchFields["grupa"] as? String ?: student.grupa,
                assignedLectures = student.assignedLectures // Immutable field, keep unchanged
            )

            _studentRepository.save(updatedStudent)

            val model = EntityModel.of(
                updatedStudent,
                linkTo(methodOn(LectureController::class.java).getLectureById(id)).withSelfRel()
            )
            ResponseEntity.ok(model)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

}
