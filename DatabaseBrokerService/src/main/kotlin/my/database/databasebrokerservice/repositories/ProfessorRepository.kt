package my.database.databasebrokerservice.repositories

import my.database.databasebrokerservice.dto.Professor
import org.springframework.data.jpa.repository.JpaRepository

interface ProfessorRepository: JpaRepository<Professor,Long> {
    fun findByGradDidactic(GRAD_DIDACTIC:String):List<Professor>
    fun findByIdGreaterThanAndIdLessThanEqual(left:Long, right:Long):List<Professor>
    fun findByNumeContainsOrPrenumeContains(searchName:String,searchPrenume:String):List<Professor>
}