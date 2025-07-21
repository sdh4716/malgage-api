package com.darong.malgage_api.domain.category.repository;

import com.darong.malgage_api.domain.record.Record;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Record, Long> {
}