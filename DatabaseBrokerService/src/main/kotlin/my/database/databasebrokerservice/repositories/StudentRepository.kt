package my.database.databasebrokerservice.repositories

import my.database.databasebrokerservice.dto.Student
import org.springframework.data.jpa.repository.JpaRepository

interface StudentRepository:JpaRepository<Student,Long> {
}