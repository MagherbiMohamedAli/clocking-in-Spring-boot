package tn.biramgroup.pointage.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Absence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    @Column(length = 5000)
    private String description;

    private Boolean accepted;

    @Temporal(TemporalType.DATE)
    private LocalDate dateStart;

    @Temporal(TemporalType.DATE)
    private LocalDate dateEnd;

    @OneToMany(mappedBy = "absence")
    @JsonManagedReference
    private List<User> users;

}
