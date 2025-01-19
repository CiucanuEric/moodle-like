package my.database.databasebrokerservice.dto

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import lombok.Getter
import lombok.Setter

@Entity
@Table(name = "students")
@Getter
@Setter
class Student(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id:Long,
    @Basic
    @Column(name = "nume")
    val nume:String,
    @Basic
    @Column(name = "prenume")
    val prenume:String,
    @Basic
    @Column(name="email")
    val email:String,
    @Basic
    @Column(name="ciclu_studiu")
    val ciclu_studiu:String,
    @Basic
    @Column(name="an_studiu")
    val an_studiu:Int,
    @Basic
    @Column(name="grupa")
    val grupa:String,
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name="studenti_discipline",
        joinColumns = [JoinColumn(name = "id_student")],
        inverseJoinColumns = [JoinColumn(name = "id_disciplina")]
    )
    @JsonIgnoreProperties("assignedStudents","assignedProfessors")
    val assignedLectures: List<Lecture>,
)
