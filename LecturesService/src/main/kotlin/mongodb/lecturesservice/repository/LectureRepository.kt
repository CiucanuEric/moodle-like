package mongodb.lecturesservice.repository

import mongodb.lecturesservice.models.Lecture
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository


@Repository
interface LectureRepository: MongoRepository<Lecture, Long> {
}