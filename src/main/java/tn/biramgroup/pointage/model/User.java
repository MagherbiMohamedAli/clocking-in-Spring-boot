package tn.biramgroup.pointage.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank
    @NotEmpty
    @Email
    private String email;

    @Column(nullable = false)
    @NotBlank
    @NotEmpty
    private String nom;

    @Column(nullable = false)
    @NotBlank
    @NotEmpty
    private String prenom;

    @NotBlank
    @Column(nullable = false)
    @NotEmpty
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "role_id", nullable = false)
    )
    @JsonIgnoreProperties({"users"})
    private Set<Role> roles = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private EWorkMode workMode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "status_id")
    private Status status;

    @LastModifiedDate
    @Column(name = "modified", nullable = false)
    private LocalDateTime modified;

//    @Column(nullable = false)
//    private boolean enabled = false;

    @Column(name = "verification_token")
    private String verificationToken;

    @Column(name = "total_minutes_worked")
    private Long totalMinutesWorked = 0L;

    @Column(name = "entry_time")
    private LocalDateTime entryTime;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.email;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Long getUserId() {
        return id;
    }

    public void setUserId(Long userId) {
        this.id = userId;
    }
}
