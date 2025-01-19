package my.database.databasebrokerservice.controller

import my.database.databasebrokerservice.annotations.RequiresRole
import my.database.databasebrokerservice.controller.StudentController.Companion.ADMIN
import my.database.databasebrokerservice.dto.Lecture
import my.database.databasebrokerservice.dto.Professor
import my.database.databasebrokerservice.dto.Student
import my.database.databasebrokerservice.repositories.ProfessorRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
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
@RequestMapping("/api/academia/professors")
class ProfessorController {
    @Autowired
    private lateinit var _professorRepository: ProfessorRepository

    @RequestMapping(path = ["/{id}"], method = [RequestMethod.GET])
    fun getProfessorById(@PathVariable id: Long): ResponseEntity<EntityModel<Professor>> {
        var professor = _professorRepository.findById(id)
        if (professor.isPresent) {
            val professor_dto = professor.get()
            val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
            val userRole = request.getAttribute("userRole") as? String

            val model=EntityModel.of(professor_dto,
                linkTo(methodOn(ProfessorController::class.java).getProfessorById(id)).withSelfRel(),
                linkTo(methodOn(ProfessorController::class.java).getProfessorLectures(id)).withRel("professor_lectures"))
            if(userRole==ADMIN)
            {
                val placeholderProfessor = Professor(
                    id = id,
                    nume = "",
                    prenume = "",
                    email = "",
                    gradDidactic = "",
                    tip_asociere = "",
                    afiliere = "",
                    assignedLectures = emptyList()
                )
                model.add(linkTo(methodOn(ProfessorController::class.java).deleteProfessor(id)).withRel("delete").withType("DELETE"),
                    linkTo(methodOn(ProfessorController::class.java).updateProfessor(id,placeholderProfessor)).withRel("update").withType("PUT"),
                    linkTo(methodOn(ProfessorController::class.java).createProfessor(placeholderProfessor)).withRel("create").withType("POST"),
                    linkTo(methodOn(ProfessorController::class.java).patchProfessor(id, emptyMap())).withRel("patch").withType("PATCH"))
            }


            return ResponseEntity.ok(model)
        } else {
            return ResponseEntity.notFound().build()
        }
    }

    @RequestMapping(path=["/{id}/lectures"], method = [RequestMethod.GET])
    fun getProfessorLectures(@PathVariable id: Long): ResponseEntity<List<Lecture>> {
        val lectures=_professorRepository.findById(id).get().assignedLectures
        var httpStatus = HttpStatus.OK
        if(lectures.isEmpty())
            httpStatus = HttpStatus.NOT_FOUND
        return ResponseEntity(lectures,httpStatus);
    }

    @RequestMapping(path=[""], method = [RequestMethod.GET],params=["acad_rank"])
    fun getProfessorByAcademicRank(@RequestParam(required = true,name="acad_rank") acad_rank:String): ResponseEntity<List<Professor>> {
        val professors=_professorRepository.findByGradDidactic(acad_rank);
        var httpStatus = HttpStatus.OK
        if(professors.isEmpty())
            httpStatus = HttpStatus.NOT_FOUND
        return ResponseEntity(professors,httpStatus);
    }

    @RequestMapping(path=[""], method = [RequestMethod.GET], params = ["name"])
    fun getProfessorByName(@RequestParam(required = true,name="name") search:String): ResponseEntity<List<Professor>> {
        val professors=_professorRepository.findByNumeContainsOrPrenumeContains(search,search);
        var httpStatus = HttpStatus.OK
        if(professors.isEmpty())
            httpStatus = HttpStatus.NOT_FOUND
        return ResponseEntity(professors,httpStatus);
    }


    @RequiresRole(ADMIN)
    @RequestMapping(path = ["/{id}"], method = [RequestMethod.PUT])
    fun updateProfessor(@PathVariable id: Long, @RequestBody receivedEntity: Professor): ResponseEntity<EntityModel<Professor>> {

        if (receivedEntity.nume.isBlank() || receivedEntity.prenume.isBlank() || receivedEntity.email.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build()
        }
        if (!receivedEntity.email.contains("@")) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build()
        }

        val existingEntity = _professorRepository.findById(id)

        return if (existingEntity.isPresent) {
            try {
                val currentEntity = existingEntity.get()
                val updatedEntity = Professor(
                    id = currentEntity.id,
                    nume = receivedEntity.nume,
                    prenume = receivedEntity.prenume,
                    email = receivedEntity.email,
                    gradDidactic = receivedEntity.gradDidactic,
                    tip_asociere = receivedEntity.tip_asociere,
                    afiliere = receivedEntity.afiliere,
                    assignedLectures = receivedEntity.assignedLectures
                )

                val savedEntity = _professorRepository.save(updatedEntity)
                val model = EntityModel.of(
                    savedEntity,
                    linkTo(methodOn(ProfessorController::class.java).getProfessorById(savedEntity.id)).withSelfRel()
                )
                ResponseEntity.status(HttpStatus.NO_CONTENT).body(model)
            } catch (e: DataIntegrityViolationException) {
                ResponseEntity.status(HttpStatus.CONFLICT).build()
            }
        } else {
            try {
                val newEntity = Professor(
                    id = 0,
                    nume = receivedEntity.nume,
                    prenume = receivedEntity.prenume,
                    email = receivedEntity.email,
                    gradDidactic = receivedEntity.gradDidactic,
                    tip_asociere = receivedEntity.tip_asociere,
                    afiliere = receivedEntity.afiliere,
                    assignedLectures = receivedEntity.assignedLectures
                )

                val savedEntity = _professorRepository.save(newEntity)
                val model = EntityModel.of(savedEntity,
                    linkTo(methodOn(ProfessorController::class.java).getProfessorById(savedEntity.id)).withSelfRel())

                ResponseEntity.status(HttpStatus.CREATED).body(model)
            } catch (e: DataIntegrityViolationException) {
                ResponseEntity.status(HttpStatus.CONFLICT).build()
            }
        }
    }

    @RequiresRole(ADMIN)
    @RequestMapping(method = [RequestMethod.POST])
    fun createProfessor(@RequestBody receivedEntity: Professor): ResponseEntity<EntityModel<Professor>> {

        if (receivedEntity.nume.isBlank() || receivedEntity.prenume.isBlank() || receivedEntity.email.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build()
        }

        if (!receivedEntity.email.contains("@")) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build()
        }

        return try {
            val savedEntity = _professorRepository.save(receivedEntity)

            val model = EntityModel.of(savedEntity,
                linkTo(methodOn(ProfessorController::class.java).getProfessorById(savedEntity.id)).withSelfRel())
            ResponseEntity.status(HttpStatus.CREATED).body(model)
        } catch (e: DataIntegrityViolationException) {
            ResponseEntity.status(HttpStatus.CONFLICT).build()
        }
    }

    @RequiresRole(ADMIN)
    @RequestMapping(path = ["/{id}"], method = [RequestMethod.DELETE])
    fun deleteProfessor(@PathVariable id: Long): ResponseEntity<EntityModel<Professor>> {
        val existingEntity = _professorRepository.findById(id)

        return if (existingEntity.isPresent) {
            val professor = existingEntity.get()
            val modelProfessor=Professor(
                id = professor.id,
                nume = professor.nume,
                prenume = professor.prenume,
                email = professor.email,
                gradDidactic = professor.gradDidactic,
                tip_asociere = professor.tip_asociere,
                afiliere = professor.afiliere,
                assignedLectures = professor.assignedLectures
            )

            val model = EntityModel.of(
                modelProfessor,
                linkTo(methodOn(ProfessorController::class.java).getProfessorById(id)).withSelfRel()
            )
            _professorRepository.delete(professor)

            ResponseEntity.ok(model)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @RequiresRole(ADMIN)
    @RequestMapping(path = ["/{id}"], method = [RequestMethod.PATCH])
    fun patchProfessor(@PathVariable id: Long, @RequestBody patchFields: Map<String, Any>): ResponseEntity<EntityModel<Professor>> {
        val existingProfessorOptional = _professorRepository.findById(id)

        return if (existingProfessorOptional.isPresent) {
            val professor = existingProfessorOptional.get()

            val updatedProfessor = Professor(
                id = professor.id,
                nume = patchFields["nume"] as? String ?: professor.nume,
                prenume = patchFields["prenume"] as? String ?: professor.prenume,
                email = patchFields["email"] as? String ?: professor.email,
                gradDidactic = patchFields["gradDidactic"] as? String ?: professor.gradDidactic,
                tip_asociere = patchFields["tip_asociere"] as? String ?: professor.tip_asociere,
                afiliere = patchFields["afiliere"] as? String ?: professor.afiliere,
                assignedLectures = professor.assignedLectures
            )

            // Save the updated Professor
            _professorRepository.save(updatedProfessor)

            // Return the updated resource
            val model = EntityModel.of(
                updatedProfessor,
                linkTo(methodOn(ProfessorController::class.java).patchProfessor(id, emptyMap())).withSelfRel()
            )
            ResponseEntity.ok(model)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }
}