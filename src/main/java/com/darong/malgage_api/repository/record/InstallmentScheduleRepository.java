package com.darong.malgage_api.repository.record;

import com.darong.malgage_api.domain.record.InstallmentSchedule;
import com.darong.malgage_api.domain.record.Record;
import com.darong.malgage_api.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface InstallmentScheduleRepository extends JpaRepository<InstallmentSchedule, Long> {
    void deleteByRecord(Record record);
    List<InstallmentSchedule> findByRecord(Record record);
}
