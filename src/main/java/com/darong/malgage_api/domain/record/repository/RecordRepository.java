package com.darong.malgage_api.domain.record.repository;

import com.darong.malgage_api.domain.record.Record;
import com.darong.malgage_api.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {

    List<Record> findByUserAndDateBetween(User user, LocalDateTime startDate, LocalDateTime endDate);
}