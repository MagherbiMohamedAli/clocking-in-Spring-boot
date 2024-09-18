package tn.biramgroup.pointage.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.biramgroup.pointage.model.ERole;
import tn.biramgroup.pointage.model.Role;

import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRole(ERole role);
    boolean existsByRole(String role);

}