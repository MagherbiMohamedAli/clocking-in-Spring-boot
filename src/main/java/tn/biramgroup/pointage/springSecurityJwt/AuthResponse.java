package tn.biramgroup.pointage.springSecurityJwt;

import lombok.Getter;
import lombok.Setter;
import tn.biramgroup.pointage.model.Role;
import tn.biramgroup.pointage.model.Status;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class AuthResponse {
    private String email;
    private String accessToken;
    private Long id;
    private String username;
    private Set<Role> roles;
    private String nom;
    private String prenom;
    private Status status;
    private LocalDateTime modified;
    private Long totalMinutesWorked;
    public AuthResponse(String email, String accessToken, Long id, String username, Set<Role> roles, String nom, String prenom, Status status, LocalDateTime modified, Long totalMinutesWorked) {
        this.email = email;
        this.accessToken = accessToken;
        this.id = id;
        this.username = username;
        this.roles = roles;
        this.nom = nom;
        this.prenom = prenom;
        this.status = status;
        this.modified = modified;
        this.totalMinutesWorked = totalMinutesWorked;
    }

}
