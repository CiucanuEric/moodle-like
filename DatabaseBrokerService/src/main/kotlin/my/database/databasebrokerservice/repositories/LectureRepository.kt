package my.database.databasebrokerservice.repositories

import my.database.databasebrokerservice.dto.Lecture
import my.database.databasebrokerservice.dto.Professor
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface LectureRepository: JpaRepository<Lecture, Long> {
    fun findByIdGreaterThanAndIdLessThanEqual(left:Long, right:Long):List<Lecture>
}