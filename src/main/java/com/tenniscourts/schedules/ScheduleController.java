package com.tenniscourts.schedules;

import com.tenniscourts.config.BaseRestController;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@AllArgsConstructor
public class ScheduleController extends BaseRestController implements ScheduleAPI {

    private final ScheduleService scheduleService;

    public ResponseEntity<Void> addScheduleTennisCourt(CreateScheduleRequestDTO createScheduleRequestDTO) {
        return ResponseEntity.created(locationByEntity(scheduleService.addSchedule(createScheduleRequestDTO.getTennisCourtId(), createScheduleRequestDTO).getId())).build();
    }

    public ResponseEntity<List<ScheduleDTO>> findSchedulesByDates(LocalDate startDate,
                                                                  LocalDate endDate) {
        return ResponseEntity.ok(scheduleService.findSchedulesByDates(LocalDateTime.of(startDate, LocalTime.of(0, 0)), LocalDateTime.of(endDate, LocalTime.of(23, 59))));
    }

    public ResponseEntity<ScheduleDTO> findByScheduleId(Long scheduleId) {
        return ResponseEntity.ok(scheduleService.findSchedule(scheduleId));
    }
}
