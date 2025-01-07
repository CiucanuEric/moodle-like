package my.database.databasebrokerservice.dto

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import lombok.Getter
import lombok.Setter

@Entity
@Table(name="discipline")
@Getter
@Setter

data class Lecture(
    @Basic
    @Column(name="id")
    @Id
    val id: Long = 0,

    @Basic
    @Column(name="nume_disciplina")
    val nume_disciplina: String?=null,

    @Basic
    @Column(name="cod")
    val cod: String? = null,

    @Basic
    @Column(name="an_studiu")
    val an_studiu: Int? = null,

    @Basic
    @Column(name="tip_disciplina")
    val tip_disciplina: String? = null,

    @Basic
    @Column(name="categorie_disciplina")
    val categorie_disciplina: String? = null,

    @Basic
    @Column(name="tip_examinare")
    val tip_examinare: String? = null,

    @OneToOne(cascade = [(CascadeType.ALL)])
    @JoinColumn(name="id_titular",referencedColumnName = "id")
    @JsonIgnoreProperties("assignedLectures")
    val id_titular: Professor? = null,

    @ManyToMany(mappedBy = "assignedLectures")
    @JsonIgnoreProperties("assignedLectures")
    val assignedStudents:List<Student>,

    @ManyToMany(mappedBy = "assignedLectures")
    @JsonIgnoreProperties("assignedLectures")
    val assignedProfessors:List<Professor>

)
