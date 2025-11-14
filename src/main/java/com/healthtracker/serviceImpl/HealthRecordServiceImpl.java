package com.healthtracker.serviceImpl;

import com.healthtracker.dto.HealthRecordDto;
import com.healthtracker.exception.ResourceNotFoundException;
import com.healthtracker.model.HealthRecord;
import com.healthtracker.model.User;
import com.healthtracker.repository.HealthRecordRepository;
import com.healthtracker.service.AlertService;
import com.healthtracker.service.HealthRecordService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HealthRecordServiceImpl implements HealthRecordService {

    @Autowired
    private HealthRecordRepository healthRecordRepository;

    @Autowired
    private ModelMapper modelMapper; // @Bean in config

    @Autowired
    private AlertService alertService;

    @Override
    public HealthRecordDto create(HealthRecordDto dto, User user) {
        HealthRecord record = modelMapper.map(dto, HealthRecord.class);
        record.setUser(user);
        HealthRecord saved = healthRecordRepository.save(record);
        // Trigger alert generation after save
        alertService.generateAlerts(user);
        return modelMapper.map(saved, HealthRecordDto.class);
    }

    @Override
    public HealthRecordDto update(Long id, HealthRecordDto dto, User user) {
        HealthRecord record = healthRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found: " + id));
        if (record.getUser() == null ||!record.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to record");
        }

        // Manual field copy (bypasses ModelMapper for user/ID; prevents alteration)
        record.setBpSystolic(dto.getBpSystolic());
        record.setBpDiastolic(dto.getBpDiastolic());
        record.setSugarLevel(dto.getSugarLevel());
        record.setWeight(dto.getWeight());
        record.setHeartRate(dto.getHeartRate());
        record.setDate(dto.getDate());
        record.setNotes(dto.getNotes());
        // No setUser() or setId()—preserves existing

        HealthRecord updated = healthRecordRepository.save(record);
        return modelMapper.map(updated, HealthRecordDto.class);

    }

    @Override
    public void delete(Long id, User user) {
        HealthRecord record = healthRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found: " + id));
        if (!record.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        healthRecordRepository.delete(record);
    }

    @Override
    public List<HealthRecordDto> getAllByUser(User user) {
        List<HealthRecord> records = healthRecordRepository.findByUserOrderByDateDesc(user);
        return records.stream()
                .map(r -> modelMapper.map(r, HealthRecordDto.class))  // Map each to DTO
                .collect(Collectors.toList());
    }

    @Override
    public List<HealthRecordDto> getByDate(User user, LocalDate date) {
        List<HealthRecord> records = healthRecordRepository.findByUserAndDate(user, date);
        return records.stream()
                .map(r -> modelMapper.map(r, HealthRecordDto.class))  // Map to DTO
                .collect(Collectors.toList());
    }

    @Override
    public List<HealthRecordDto> exportByUser(User user) {
        return getAllByUser(user);  // Reuse mapped list
    }
}
