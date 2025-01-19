package my.database.databasebrokerservice.dto

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import lombok.Getter
import lombok.Setter


@Entity
@Table(name = "profesori")
@Getter
@Setter
class Professor(
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name="id")
    val id: Long,

    @Basic
    @Column(name="nume")
    val nume: String,

    @Basic
    @Column(name="prenume")
    val prenume: String,

    @Basic
    @Column(name="email")
    val email: String,

    @Basic
    @Column(name="grad_Didactic")
    val gradDidactic: String,

    @Basic
    @Column(name="tip_Asociere")
    val tip_asociere: String,

    @Basic
    @Column(name="afiliere")
    val afiliere: String = "",
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name="profesori_discipline",
        joinColumns = [JoinColumn(name = "id_professor")],
        inverseJoinColumns = [JoinColumn(name = "id_disciplina")]
    )
    @JsonIgnoreProperties("assignedStudents","assignedProfessors")
    val assignedLectures:List<Lecture>

)
