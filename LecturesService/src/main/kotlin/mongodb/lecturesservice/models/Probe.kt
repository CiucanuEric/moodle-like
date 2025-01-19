package mongodb.lecturesservice.models

import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import lombok.ToString

enum class Type_Probe(type:Int){
    EXAMEN(1), COLOCVIU(2), PARTIAL(3)
}

@AllArgsConstructor
@NoArgsConstructor
@ToString
data class Probe(
    var type: Type_Probe,
    var percentage:Float,
    var content:String
)
