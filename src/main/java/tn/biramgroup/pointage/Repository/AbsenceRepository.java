package tn.biramgroup.pointage.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.biramgroup.pointage.model.Absence;

import java.util.List;

@Repository
public interface AbsenceRepository extends JpaRepository<Absence, Long> {
    List<Absence> findByAcceptedIsNull();
    List<Absence> findByAccepted(Boolean accepted);
}
