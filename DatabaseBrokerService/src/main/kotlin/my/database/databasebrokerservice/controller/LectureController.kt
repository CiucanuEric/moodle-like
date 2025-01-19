package my.database.databasebrokerservice.controller

import my.database.databasebrokerservice.annotations.RequiresRole
import my.database.databasebrokerservice.controller.StudentController.Companion.ADMIN
import my.database.databasebrokerservice.controller.StudentController.Companion.PROFESSOR
import my.database.databasebrokerservice.controller.StudentController.Companion.STUDENT
import my.database.databasebrokerservice.dto.Lecture
import my.database.databasebrokerservice.dto.Professor
import my.database.databasebrokerservice.repositories.LectureRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@RestController
@RequestMapping("/api/academia/lectures")
class LectureController {

    companion object
    {
        const val BASE_URL="http://localhost:15000/"
    }

    @Autowired
    private lateinit var _lectureRepository: LectureRepository
    @RequiresRole(STUDENT, ADMIN, PROFESSOR)
    @RequestMapping(value=["/{id}"],method= [RequestMethod.GET])
    fun getLectureById(@PathVariable(value = "id") id: Long): ResponseEntity<EntityModel<Lecture>> {

        var lecture= _lectureRepository.findById(id);

        if (lecture.isPresent) {
            val lecture_dto = lecture.get()
            val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
            val userRole = request.getAttribute("userRole") as? String

            val model=EntityModel.of(lecture_dto,
                linkTo(methodOn(LectureController::class.java).getLectureById(id)).withSelfRel(),
                Link.of(BASE_URL+id).withRel("mongo"))
            if(userRole==ADMIN)
            {
                val placeholderLecture = Lecture(
                    id = id,
                    nume_disciplina = "",
                    cod = "",
                    an_studiu = 0,
                    tip_disciplina = "",
                    categorie_disciplina = "",
                    tip_examinare = "",
                    id_titular = Professor(
                        id = id,
                        nume = "",
                        prenume = "",
                        email = "",
                        gradDidactic = "",
                        tip_asociere = "",
                        afiliere = "",
                        assignedLectures = emptyList()
                    ),
                    assignedStudents = emptyList(),
                    assignedProfessors = emptyList()
                )
                model.add(linkTo(methodOn(LectureController::class.java).deleteLecture(id)).withRel("delete").withType("DELETE"),
                    linkTo(methodOn(LectureController::class.java).updateLecture(id,placeholderLecture)).withRel("update").withType("PUT"),
                    linkTo(methodOn(LectureController::class.java).createLecture(placeholderLecture)).withRel("create").withType("POST"),
                    linkTo(methodOn(LectureController::class.java).patchLecture(id, emptyMap())).withRel("patch").withType("PATCH")
                )
            }


            return ResponseEntity.ok(model)
        } else {
            return ResponseEntity.notFound().build()
        }
    }


    @RequiresRole(ADMIN)
    @RequestMapping(path = ["/{id}"], method = [RequestMethod.PUT])
    fun updateLecture(@PathVariable id: Long, @RequestBody receivedEntity: Lecture): ResponseEntity<EntityModel<Lecture>> {

        if (receivedEntity.nume_disciplina.isBlank() || receivedEntity.cod.isBlank() || receivedEntity.tip_disciplina.isBlank() ||
            receivedEntity.categorie_disciplina.isBlank() || receivedEntity.tip_examinare.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build()
        }
//        if (!receivedEntity.email.contains("@")) {
//            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build()
//        }

        val existingEntity = _lectureRepository.findById(id)

        return if (existingEntity.isPresent) {
            try {
                val currentEntity = existingEntity.get()
                val updatedEntity = Lecture(
                    id = currentEntity.id,
                    nume_disciplina = receivedEntity.nume_disciplina,
                    cod = receivedEntity.cod,
                    an_studiu = receivedEntity.an_studiu,
                    tip_disciplina = receivedEntity.tip_disciplina,
                    categorie_disciplina = receivedEntity.categorie_disciplina,
                    tip_examinare = receivedEntity.tip_examinare,
                    id_titular = receivedEntity.id_titular,
                    assignedStudents = receivedEntity.assignedStudents,
                    assignedProfessors = receivedEntity.assignedProfessors
                )

                val savedEntity = _lectureRepository.save(updatedEntity)
                val model = EntityModel.of(
                    savedEntity,
                    linkTo(methodOn(LectureController::class.java).getLectureById(savedEntity.id)).withSelfRel()
                )
                ResponseEntity.status(HttpStatus.NO_CONTENT).body(model)
            } catch (e: DataIntegrityViolationException) {
                ResponseEntity.status(HttpStatus.CONFLICT).build()
            }
        } else {
            try {
                val newEntity = Lecture(
                    id = 0,
                    nume_disciplina = receivedEntity.nume_disciplina,
                    cod = receivedEntity.cod,
                    an_studiu = receivedEntity.an_studiu,
                    tip_disciplina = receivedEntity.tip_disciplina,
                    categorie_disciplina = receivedEntity.categorie_disciplina,
                    tip_examinare = receivedEntity.tip_examinare,
                    id_titular = receivedEntity.id_titular,
                    assignedStudents = receivedEntity.assignedStudents,
                    assignedProfessors = receivedEntity.assignedProfessors
                )

                val savedEntity = _lectureRepository.save(newEntity)
                val model = EntityModel.of(savedEntity,
                    linkTo(methodOn(LectureController::class.java).getLectureById(savedEntity.id)).withSelfRel())

                ResponseEntity.status(HttpStatus.CREATED).body(model)
            } catch (e: DataIntegrityViolationException) {
                ResponseEntity.status(HttpStatus.CONFLICT).build()
            }
        }
    }

    @RequiresRole(ADMIN)
    @RequestMapping(method = [RequestMethod.POST])
    fun createLecture(@RequestBody receivedEntity: Lecture): ResponseEntity<EntityModel<Lecture>> {

        if (receivedEntity.nume_disciplina.isBlank() || receivedEntity.cod.isBlank() || receivedEntity.tip_disciplina.isBlank() ||
            receivedEntity.categorie_disciplina.isBlank() || receivedEntity.tip_examinare.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build()
        }

//        if (!receivedEntity.email.contains("@")) {
//            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build()
//        }

        return try {
            val savedEntity = _lectureRepository.save(receivedEntity)

            val model = EntityModel.of(savedEntity,
                linkTo(methodOn(LectureController::class.java).getLectureById(savedEntity.id)).withSelfRel())
            ResponseEntity.status(HttpStatus.CREATED).body(model)
        } catch (e: DataIntegrityViolationException) {
            ResponseEntity.status(HttpStatus.CONFLICT).build()
        }
    }

    @RequiresRole(ADMIN)
    @RequestMapping(path = ["/{id}"], method = [RequestMethod.DELETE])
    fun deleteLecture(@PathVariable id: Long): ResponseEntity<EntityModel<Lecture>> {
        val existingEntity = _lectureRepository.findById(id)

        return if (existingEntity.isPresent) {
            val lecture = existingEntity.get()

            val modelLecture = Lecture(
                id = lecture.id,
                nume_disciplina = lecture.nume_disciplina,
                cod = lecture.cod,
                an_studiu = lecture.an_studiu,
                tip_disciplina = lecture.tip_disciplina,
                categorie_disciplina = lecture.categorie_disciplina,
                tip_examinare = lecture.tip_examinare,
                id_titular = lecture.id_titular,
                assignedStudents = lecture.assignedStudents,
                assignedProfessors = lecture.assignedProfessors
            )
            val model = EntityModel.of(
                modelLecture,
                linkTo(methodOn(LectureController::class.java).getLectureById(id)).withSelfRel()
            )
            _lectureRepository.delete(lecture)

            ResponseEntity.ok(model)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @RequiresRole(STUDENT,ADMIN,PROFESSOR)
    @RequestMapping(path=[""], method = [RequestMethod.GET], params = ["page","items_per_page"])
    fun getLectureByPage(@RequestParam(required = true,name="page") page:Long, @RequestParam(required = true,name="items_per_page") itemsPerPage:Long): ResponseEntity<CollectionModel<Lecture>> {
        val professors=_lectureRepository.findByIdGreaterThanAndIdLessThanEqual(page*itemsPerPage, (page+1)*itemsPerPage);
        if(professors.isEmpty())
            return ResponseEntity.notFound().build()
        val methodC = methodOn(LectureController::class.java)
        val model = CollectionModel.of(professors,linkTo(methodC.getLectureByPage(page,itemsPerPage)).withSelfRel())
        if(_lectureRepository.count() > (page.toInt()+1)*itemsPerPage)
        {
            model.add(linkTo(methodC.getLectureByPage(page+1,itemsPerPage)).withRel("next"))
        }
        if(page.toInt() != 0)
        {
            model.add(linkTo(methodC.getLectureByPage(page-1,itemsPerPage)).withRel("prev"))
        }
        model.add(linkTo(methodC.getLectureByPage(page,5)).withRel("size5"))
        model.add(linkTo(methodC.getLectureByPage(page,10)).withRel("size10"))
        model.add(linkTo(methodC.getLectureByPage(page,25)).withRel("size25"))
        model.add(linkTo(methodC.getLectureByPage(page,50)).withRel("size50"))
        return ResponseEntity.ok(model);
    }

    @RequiresRole(ADMIN)
    @RequestMapping(path = ["/{id}"], method = [RequestMethod.PATCH])
    fun patchLecture(@PathVariable id: Long, @RequestBody patchFields: Map<String, Any>): ResponseEntity<EntityModel<Lecture>> {
        val existingLectureOptional = _lectureRepository.findById(id)

        return if (existingLectureOptional.isPresent) {
            val entity = existingLectureOptional.get()

            val updatedEntity = Lecture(
                id = entity.id,
                nume_disciplina = patchFields["nume_disciplina"] as? String ?: entity.nume_disciplina,
                cod = patchFields["cod"] as? String ?: entity.cod,
                an_studiu = (patchFields["an_studiu"] as? Number)?.toInt() ?: entity.an_studiu,
                tip_disciplina = patchFields["tip_disciplina"] as? String ?: entity.tip_disciplina,
                categorie_disciplina = patchFields["categorie_disciplina"] as? String ?: entity.categorie_disciplina,
                tip_examinare = patchFields["tip_examinare"] as? String ?: entity.tip_examinare,
                id_titular = patchFields["id_titular"]?.let {

                    Professor(
                        id = (it as Map<String, Any>)["id"] as Long,
                        nume = entity.id_titular.nume,
                        prenume = entity.id_titular.prenume,
                        email = entity.id_titular.email,
                        gradDidactic = entity.id_titular.gradDidactic,
                        tip_asociere = entity.id_titular.tip_asociere,
                        afiliere = entity.id_titular.afiliere,
                        assignedLectures = entity.id_titular.assignedLectures
                    )
                } ?: entity.id_titular,
                assignedStudents = entity.assignedStudents,
                assignedProfessors = entity.assignedProfessors
            )

            _lectureRepository.save(updatedEntity)

            val model = EntityModel.of(
                entity,
                linkTo(methodOn(LectureController::class.java).getLectureById(id)).withSelfRel()
            )
            ResponseEntity.ok(model)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }
}