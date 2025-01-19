package my.database.databasebrokerservice.repositories

import my.database.databasebrokerservice.dto.Student
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface StudentRepository:JpaRepository<Student,Long> {
    fun findByEmail(email: String): Optional<Student>
}