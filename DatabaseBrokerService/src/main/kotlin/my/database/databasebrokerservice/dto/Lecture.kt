package my.database.databasebrokerservice.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import lombok.Getter
import lombok.Setter

@Entity
@Table(name="discipline")
@Getter
@Setter

class Lecture(
    @Basic
    @Column(name="id")
    @Id
    val id: Long = 0,

    @Basic
    @Column(name="nume_disciplina")
    val nume_disciplina: String,

    @Basic
    @Column(name="cod")
    val cod: String,

    @Basic
    @Column(name="an_studiu")
    val an_studiu: Int,

    @Basic
    @Column(name="tip_disciplina")
    val tip_disciplina: String,

    @Basic
    @Column(name="categorie_disciplina")
    val categorie_disciplina: String,

    @Basic
    @Column(name="tip_examinare")
    val tip_examinare: String,

    @OneToOne(cascade = [(CascadeType.ALL)])
    @JoinColumn(name="id_titular",referencedColumnName = "id")
    @JsonIgnoreProperties("assignedLectures")
    val id_titular: Professor,

    @ManyToMany(mappedBy = "assignedLectures",fetch = FetchType.EAGER)
    @JsonIgnoreProperties("assignedLectures")
    val assignedStudents:List<Student>,

    @ManyToMany(mappedBy = "assignedLectures", fetch = FetchType.EAGER)
    @JsonIgnoreProperties("assignedLectures")
    val assignedProfessors:List<Professor>

)
