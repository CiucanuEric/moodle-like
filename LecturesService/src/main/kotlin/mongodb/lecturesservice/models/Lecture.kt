package mongodb.lecturesservice.models

import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import lombok.ToString
import org.springframework.data.annotation.Id


@AllArgsConstructor
@NoArgsConstructor
@ToString

data class Lecture (
    @Id
    var id: Long = 0,
    var Probes: List<Probe>,
    var Materials: List<Material>,

)