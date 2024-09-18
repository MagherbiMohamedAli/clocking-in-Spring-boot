package tn.biramgroup.pointage.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Month;

@Entity
@Table(name = "monthly_work_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyWorkRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Long totalMinutesWorked;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Month month;

    public MonthlyWorkRecord(User user, Long totalMinutesWorked, int year, Month month) {
        this.user = user;
        this.totalMinutesWorked = totalMinutesWorked;
        this.year = year;
        this.month = month;
    }
}