package tn.biramgroup.pointage.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.biramgroup.pointage.model.ERole;
import tn.biramgroup.pointage.model.Role;
import tn.biramgroup.pointage.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    @Query("SELECT u FROM User u WHERE u.status.id = :statusId")
    List<User> findByStatusId(@Param("statusId") Long statusId);
    User findByVerificationToken(String token);
    List<User> findByRoles(Set<Role> roles);
}
