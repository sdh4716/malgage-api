package com.darong.malgage_api.domain.record;

import com.darong.malgage_api.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {

    List<Record> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);

}