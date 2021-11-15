package com.tenniscourts.schedules;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.tenniscourts.TennisCourtRepository;

@Service
@AllArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    private final ScheduleMapper scheduleMapper;

    private final TennisCourtRepository tennisCourtRepository;

    public ScheduleDTO addSchedule(Long tennisCourtId, CreateScheduleRequestDTO createScheduleRequestDTO) {
        return this.tennisCourtRepository.findById(tennisCourtId).map(tennisCourt -> {
            Schedule schedule = Schedule.builder().startDateTime(createScheduleRequestDTO.getStartDateTime())
                    .endDateTime(createScheduleRequestDTO.getStartDateTime().plusHours(1)).tennisCourt(tennisCourt)
                    .build();
            return this.scheduleMapper.map(schedule);
        }).orElseThrow(() -> new EntityNotFoundException("Tennis Court is not found."));
    }

    public List<ScheduleDTO> findSchedulesByDates(LocalDateTime startDate, LocalDateTime endDate) {
        return this.scheduleMapper.map(this.scheduleRepository.findByStartDateAndEndDate(startDate, endDate));
    }

    public ScheduleDTO findSchedule(Long scheduleId) {
        return this.scheduleRepository.findById(scheduleId).map(this.scheduleMapper::map)
                .orElseThrow(() -> new EntityNotFoundException("Schedule is not found"));
    }

    public List<ScheduleDTO> findSchedulesByTennisCourtId(Long tennisCourtId) {
        return scheduleMapper.map(scheduleRepository.findByTennisCourt_IdOrderByStartDateTime(tennisCourtId));
    }
}
