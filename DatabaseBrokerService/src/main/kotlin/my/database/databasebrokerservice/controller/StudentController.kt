package my.database.databasebrokerservice.controller

import my.database.databasebrokerservice.dto.Lecture
import my.database.databasebrokerservice.dto.Student
import my.database.databasebrokerservice.repositories.StudentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.util.*


@RestController
@RequestMapping("/api/academia/students")
class StudentController {

    @Autowired
    private lateinit var _studentRepository: StudentRepository

    @RequestMapping(path=["{id}"], method = [RequestMethod.GET])
    fun getStudentById(@PathVariable id: Long): ResponseEntity<Optional<Student>> {
        var student = _studentRepository.findById(id)
        var httpStatus = HttpStatus.OK
        if(student.isEmpty)
            httpStatus = HttpStatus.NOT_FOUND
        return ResponseEntity(student, httpStatus)
    }

    @RequestMapping(path=["/{id}/lectures"], method = [RequestMethod.GET])
    fun getStudentLectures(@PathVariable id: Long): ResponseEntity<List<Lecture>> {
        val lectures=_studentRepository.findById(id).get().assignedLectures
        var httpStatus = HttpStatus.OK
        if(lectures.isEmpty())
            httpStatus = HttpStatus.NOT_FOUND
        return ResponseEntity(lectures,httpStatus);
    }
}