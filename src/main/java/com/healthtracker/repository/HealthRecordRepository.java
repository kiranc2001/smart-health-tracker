package com.healthtracker.repository;

import com.healthtracker.model.HealthRecord;
import com.healthtracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {
    List<HealthRecord> findByUserOrderByDateDesc(User user);
    List<HealthRecord> findByUserAndDate(User user, java.time.LocalDate date);
}