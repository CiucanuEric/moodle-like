package my.database.databasebrokerservice.controller

import my.database.databasebrokerservice.dto.Lecture
import my.database.databasebrokerservice.repositories.LectureRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/academia/lectures")
class LectureController {
    @Autowired
    private lateinit var _lectureRepository: LectureRepository
    @RequestMapping(value=["/{id}"],method= [RequestMethod.GET])
    fun getLectureById(@PathVariable(value = "id") id: Long): ResponseEntity<Optional<Lecture>> {

        var lecture= _lectureRepository.findById(id);
        var httpStatus = HttpStatus.OK
        if(lecture.isEmpty){
            httpStatus = HttpStatus.NOT_FOUND
        }
        return ResponseEntity(lecture, httpStatus);
    }
}