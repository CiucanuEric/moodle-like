//package my.database.databasebrokerservice.controller
//
//import my.database.databasebrokerservice.dto.Lecture
//import my.database.databasebrokerservice.dto.Professor
//import my.database.databasebrokerservice.dto.Student
//import my.database.databasebrokerservice.repositories.LectureRepository
//import my.database.databasebrokerservice.repositories.ProfessorRepository
//import my.database.databasebrokerservice.repositories.StudentRepository
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.http.HttpStatus
//import org.springframework.http.ResponseEntity
//import org.springframework.web.bind.annotation.PathVariable
//import org.springframework.web.bind.annotation.RequestMapping
//import org.springframework.web.bind.annotation.RequestMethod
//import org.springframework.web.bind.annotation.RequestParam
//import org.springframework.web.bind.annotation.RestController
//import java.util.*
//
//@RestController
//@RequestMapping("api/academia")
//class MainController {
//    @Autowired
//    private lateinit var _lectureRepository: LectureRepository
//    @Autowired
//    private lateinit var _studentRepository: StudentRepository
//    @Autowired
//    private lateinit var _professorRepository: ProfessorRepository
//
//
////===================================================================
////----------------------- STUDENT -----------------------------------
////===================================================================
//
//
//    @RequestMapping(path=["/students/{id}"], method = [RequestMethod.GET])
//    fun getStudentById(@PathVariable id: Long): ResponseEntity<Optional<Student>> {
//        var student = _studentRepository.findById(id)
//        var httpStatus = HttpStatus.OK
//        if(student.isEmpty)
//            httpStatus = HttpStatus.NOT_FOUND
//        return ResponseEntity(student, httpStatus)
//    }
//
//    @RequestMapping(path=["/students/{id}/lectures"], method = [RequestMethod.GET])
//    fun getStudentLectures(@PathVariable id: Long): ResponseEntity<List<Lecture>> {
//        val lectures=_studentRepository.findById(id).get().assignedLectures
//        var httpStatus = HttpStatus.OK
//        if(lectures.isEmpty())
//            httpStatus = HttpStatus.NOT_FOUND
//        return ResponseEntity(lectures,httpStatus);
//    }
//
//
//
//
////===================================================================
////------------------------ PROFESSOR --------------------------------
////===================================================================
//
//
//    @RequestMapping(path = ["/professors/{id}"], method = [RequestMethod.GET])
//    fun getProfessorById(@PathVariable id: Long): ResponseEntity<Optional<Professor>> {
//        var professor = _professorRepository.findById(id)
//        var httpStatus = HttpStatus.OK
//        if(professor.isEmpty){
//            httpStatus = HttpStatus.NOT_FOUND
//        }
//        return ResponseEntity(professor,httpStatus);
//    }
//
//    @RequestMapping(path=["/professors/{id}/lectures"], method = [RequestMethod.GET])
//    fun getProfessorLectures(@PathVariable id: Long): ResponseEntity<List<Lecture>> {
//        val lectures=_professorRepository.findById(id).get().assignedLectures
//        var httpStatus = HttpStatus.OK
//        if(lectures.isEmpty())
//            httpStatus = HttpStatus.NOT_FOUND
//        return ResponseEntity(lectures,httpStatus);
//    }
//
//    @RequestMapping(path=["/professors"], method = [RequestMethod.GET],params=["acad_rank"])
//    fun getProfessorByAcademicRank(@RequestParam(required = true,name="acad_rank") acad_rank:String): ResponseEntity<List<Professor>> {
//        val professors=_professorRepository.findByGradDidactic(acad_rank);
//        var httpStatus = HttpStatus.OK
//        if(professors.isEmpty())
//            httpStatus = HttpStatus.NOT_FOUND
//        return ResponseEntity(professors,httpStatus);
//    }
//
//    @RequestMapping(path=["/professors"], method = [RequestMethod.GET], params = ["page","items_per_page"])
//    fun getProfessorByPage(@RequestParam(required = true,name="page") page:Long,@RequestParam(required = true,name="items_per_page") itemsPerPage:Long): ResponseEntity<List<Professor>> {
//        val professors=_professorRepository.findByIdGreaterThanAndIdLessThanEqual(page*itemsPerPage, (page+1)*itemsPerPage);
//        var httpStatus = HttpStatus.OK
//        if(professors.isEmpty())
//            httpStatus = HttpStatus.NOT_FOUND
//        return ResponseEntity(professors,httpStatus);
//    }
//
//
////===================================================================
////----------------------- LECTURE -----------------------------------
////===================================================================
//
//
//    @RequestMapping(value=["/lectures/{id}"],method= [RequestMethod.GET])
//    fun getLectureById(@PathVariable(value = "id") id: Long): ResponseEntity<Optional<Lecture>> {
//
//        var lecture= _lectureRepository.findById(id);
//        var httpStatus = HttpStatus.OK
//        if(lecture.isEmpty){
//            httpStatus = HttpStatus.NOT_FOUND
//        }
//        return ResponseEntity(lecture, httpStatus);
//    }
//
//
////===================================================================
////------------------------- DEBUG -----------------------------------
////===================================================================
//
//
//    @RequestMapping(value=["/hello"],method= [RequestMethod.GET])
//    fun hello(): ResponseEntity<String> {
//
//    val httpStatus = HttpStatus.OK
//    return ResponseEntity("Hello World", httpStatus);
//    }
//}