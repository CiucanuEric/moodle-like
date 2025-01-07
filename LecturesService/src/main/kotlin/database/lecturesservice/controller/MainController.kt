package database.lecturesservice.controller

import database.lecturesservice.repository.LectureRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/")
class MainController {
    @Autowired
    private lateinit var _lectureRepository: LectureRepository


}