package mongodb.lecturesservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LecturesServiceApplication

fun main(args: Array<String>) {
    runApplication<LecturesServiceApplication>(*args)
}
