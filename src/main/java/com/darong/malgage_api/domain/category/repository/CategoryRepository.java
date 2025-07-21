package com.darong.malgage_api.domain.record.repository;

import com.darong.malgage_api.domain.record.Record;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<Record, Long> {
}