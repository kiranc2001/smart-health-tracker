package com.healthtracker.service;

import com.healthtracker.dto.HealthRecordDto;
import com.healthtracker.model.User;
import java.time.LocalDate;
import java.util.List;

public interface HealthRecordService {
    HealthRecordDto create(HealthRecordDto dto, User user);
    HealthRecordDto update(Long id, HealthRecordDto dto, User user);
    void delete(Long id, User user);
    List<HealthRecordDto> getAllByUser(User user);
    List<HealthRecordDto> getByDate(User user, LocalDate date);
    List<HealthRecordDto> exportByUser(User user);
}