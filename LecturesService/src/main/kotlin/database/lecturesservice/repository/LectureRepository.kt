package database.lecturesservice.repository

import database.lecturesservice.models.Lecture
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository


@Repository
interface LectureRepository: MongoRepository<Lecture, Int> {
}