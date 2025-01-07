package database.lecturesservice.models

import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import lombok.ToString

enum class Type_material(val type:Int){
    LABORATOR(1), CURS(2), DIVERSE(3)
}

@AllArgsConstructor
@NoArgsConstructor
@ToString
data class Material(
    val type: Type_material,
    val content:String
    )
