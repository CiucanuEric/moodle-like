package my.database.databasebrokerservice

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DatabaseBrokerServiceApplication

fun main(args: Array<String>) {
	runApplication<DatabaseBrokerServiceApplication>(*args)
}
