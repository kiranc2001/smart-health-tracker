package com.healthtracker.config;

import com.healthtracker.dto.AlertDto;
import com.healthtracker.dto.HealthRecordDto;
import com.healthtracker.dto.UserSummaryDto;
import com.healthtracker.model.Alert;
import com.healthtracker.model.HealthRecord;
import com.healthtracker.model.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        // Skip ID mapping for HealthRecord updates (prevents overwriting PK)
        mapper.addMappings(new PropertyMap<HealthRecordDto, HealthRecord>() {
            @Override
            protected void configure() {
                skip(destination.getId()); // Ignore DTO.id when mapping to entity.id
            }
        });

        // Skip ID mapping for Alert updates (prevents overwriting PK)
        mapper.addMappings(new PropertyMap<AlertDto, Alert>() {
            @Override
            protected void configure() {
                skip(destination.getId()); // Ignore DTO.id when mapping to entity.id
            }
        });

        // Map User to UserSummaryDto (auto-maps common fields, ignores extras like passwordHash/createdAt)
        mapper.createTypeMap(User.class, UserSummaryDto.class);

        return mapper;
    }
}