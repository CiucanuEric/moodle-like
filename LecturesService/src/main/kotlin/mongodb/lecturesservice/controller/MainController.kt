package mongodb.lecturesservice.controller

import com.sun.tools.javac.Main
import mongodb.lecturesservice.models.Lecture
import mongodb.lecturesservice.repository.LectureRepository
import my.database.databasebrokerservice.annotations.RequiresRole
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/")
class MainController {


    companion object {
        const val STUDENT="student"
        const val PROFESSOR="professor"
        const val ADMIN="admin"
    }

    @Autowired
    private lateinit var _lectureRepository: LectureRepository

    @RequiresRole(STUDENT, ADMIN, PROFESSOR)
    @RequestMapping(value=["/{id}"],method= [RequestMethod.GET])
    fun getLectureById(@PathVariable(value = "id") id: Long): ResponseEntity<EntityModel<Lecture>> {

        var lecture= _lectureRepository.findById(id);

        if(lecture.isEmpty){
            return ResponseEntity.notFound().build()
        }
        val model = EntityModel.of(lecture.get(), linkTo(methodOn(MainController::class.java).getLectureById(id)).withSelfRel())
        model.add(Link.of(""))
        return ResponseEntity.ok(model);
    }

    @RequiresRole(ADMIN,PROFESSOR)
    @RequestMapping(path = ["/{id}"], method = [RequestMethod.PUT])
    fun updateLecture(@PathVariable id: Long, @RequestBody receivedEntity: Lecture): ResponseEntity<EntityModel<Lecture>> {

        for(probe in receivedEntity.Probes) {
            if (probe.content.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build()
            }
            var sum:Float=0f
            sum=sum+probe.percentage
            if(sum>1)
            {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build()
            }
        }
        for(material in receivedEntity.Materials)
        {
            if(material.content.isEmpty())
            {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build()
            }
        }

        val existingEntity = _lectureRepository.findById(id)

        return if (existingEntity.isPresent) {
            try {
                val currentEntity = existingEntity.get()
                val updatedEntity = Lecture(
                    id = currentEntity.id,
                    Probes = receivedEntity.Probes,
                    Materials = receivedEntity.Materials,
                )

                val savedEntity = _lectureRepository.save(updatedEntity)
                val model = EntityModel.of(
                    savedEntity,
                    linkTo(methodOn(MainController::class.java).getLectureById(savedEntity.id)).withSelfRel()
                )
                ResponseEntity.status(HttpStatus.NO_CONTENT).body(model)
            } catch (e: DataIntegrityViolationException) {
                ResponseEntity.status(HttpStatus.CONFLICT).build()
            }
        } else {
            try {
                val newEntity = Lecture(
                    id = 0,
                    Probes = receivedEntity.Probes,
                    Materials = receivedEntity.Materials,
                )

                val savedEntity = _lectureRepository.save(newEntity)
                val model = EntityModel.of(savedEntity,
                    linkTo(methodOn(MainController::class.java).getLectureById(savedEntity.id)).withSelfRel())

                ResponseEntity.status(HttpStatus.CREATED).body(model)
            } catch (e: DataIntegrityViolationException) {
                ResponseEntity.status(HttpStatus.CONFLICT).build()
            }
        }


    }
    @RequiresRole(ADMIN,PROFESSOR)
    @RequestMapping(method = [RequestMethod.POST])
    fun createLecture(@RequestBody receivedEntity: Lecture): ResponseEntity<EntityModel<Lecture>> {

        for(probe in receivedEntity.Probes) {
            if (probe.content.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build()
            }
            var sum:Float=0f
            sum=sum+probe.percentage
            if(sum>1)
            {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build()
            }
        }
        for(material in receivedEntity.Materials)
        {
            if(material.content.isEmpty())
            {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build()
            }
        }

//        if (!receivedEntity.email.contains("@")) {
//            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build()
//        }

        return try {
            val savedEntity = _lectureRepository.save(receivedEntity)

            val model = EntityModel.of(savedEntity,
                linkTo(methodOn(MainController::class.java).getLectureById(savedEntity.id)).withSelfRel())
            ResponseEntity.status(HttpStatus.CREATED).body(model)
        } catch (e: DataIntegrityViolationException) {
            ResponseEntity.status(HttpStatus.CONFLICT).build()
        }
    }

    @RequiresRole(ADMIN,PROFESSOR)
    @RequestMapping(path = ["/{id}"], method = [RequestMethod.DELETE])
    fun deleteLecture(@PathVariable id: Long): ResponseEntity<EntityModel<Lecture>> {
        val existingEntity = _lectureRepository.findById(id)

        return if (existingEntity.isPresent) {
            val lecture = existingEntity.get()

            val modelLecture = Lecture(
                id = lecture.id,
                Probes = lecture.Probes,
                Materials = lecture.Materials
            )
            val model = EntityModel.of(
                modelLecture,
                linkTo(methodOn(MainController::class.java).getLectureById(id)).withSelfRel()
            )
            _lectureRepository.delete(lecture)

            ResponseEntity.ok(model)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }
}