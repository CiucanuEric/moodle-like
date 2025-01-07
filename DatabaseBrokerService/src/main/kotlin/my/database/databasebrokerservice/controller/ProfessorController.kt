package my.database.databasebrokerservice.controller

import my.database.databasebrokerservice.dto.Lecture
import my.database.databasebrokerservice.dto.Professor
import my.database.databasebrokerservice.repositories.ProfessorRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping("/api/academia/professors")
class ProfessorController {
    @Autowired
    private lateinit var _professorRepository: ProfessorRepository

    @RequestMapping(path = ["/{id}"], method = [RequestMethod.GET])
    fun getProfessorById(@PathVariable id: Long): ResponseEntity<EntityModel<Optional<Professor>>> {
        var professor = _professorRepository.findById(id)
        var httpStatus = HttpStatus.OK
        var links:Link=linkTo(methodOn(ProfessorController::class.java).getProfessorById(id)).withSelfRel()
//        if(professor.)
//        {
//            httpStatus = HttpStatus.NOT_FOUND
//        }
        return ResponseEntity(EntityModel.of(professor,linkTo(methodOn(ProfessorController::class.java).getProfessorByName(professor.get().nume)).withRel("Name")),httpStatus);
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

    @RequestMapping(path=[""], method = [RequestMethod.GET], params = ["page","items_per_page"])
    fun getProfessorByPage(@RequestParam(required = true,name="page") page:Long, @RequestParam(required = true,name="items_per_page") itemsPerPage:Long): ResponseEntity<List<Professor>> {
        val professors=_professorRepository.findByIdGreaterThanAndIdLessThanEqual(page*itemsPerPage, (page+1)*itemsPerPage);
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
}