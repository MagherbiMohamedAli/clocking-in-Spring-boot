package tn.biramgroup.pointage.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.biramgroup.pointage.model.MonthlyWorkRecord;
import tn.biramgroup.pointage.model.User;

import java.time.Month;
import java.util.List;
import java.util.Optional;

@Repository
public interface MonthlyWorkRecordRepository extends JpaRepository<MonthlyWorkRecord, Long> {
    Optional<MonthlyWorkRecord> findByUserAndYearAndMonth(User user, int year, Month month);

    List<MonthlyWorkRecord> findByYearAndMonth(int year, Month month);

    List<MonthlyWorkRecord> findByYear(int year);



}
