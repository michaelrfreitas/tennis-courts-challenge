package com.tenniscourts.schedules;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByTennisCourt_IdOrderByStartDateTime(Long id);

    @Query("SELECT s FROM Schedule s " +
           "WHERE NOT EXISTS(SELECT s1.id FROM Schedule s1 " +
           "                 LEFT JOIN s1.reservations r1 " +
           "                 WHERE s.id = s1.id " +
           "                   AND s1.startDateTime > ?1 " +
           "                   AND r1.reservationStatus IN (?2)) " +
           "  AND s.startDateTime > ?1 " +
           "ORDER BY s.startDateTime ")

    List<Schedule> findByStartDateAndEndDate(LocalDateTime startDate, LocalDateTime endDate);
}