package com.tenniscourts.reservations;

import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.schedules.Schedule;
import com.tenniscourts.schedules.ScheduleRepository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static com.tenniscourts.reservations.ReservationStatus.READY_TO_PLAY;

@Service
@AllArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final ScheduleRepository scheduleRepository;

    @Transactional
    public ReservationDTO bookReservation(CreateReservationRequestDTO createReservationRequestDTO) {
        Schedule schedule = this.scheduleRepository.findById(createReservationRequestDTO.getScheduleId())
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found."));

        this.validateBookReservation(schedule);

        Reservation reservation = Reservation.builder().schedule(schedule).reservationStatus(READY_TO_PLAY)
                .value(BigDecimal.valueOf(10)).build();

        reservation = this.reservationRepository.save(reservation);
        schedule.addReservation(reservation);
        this.scheduleRepository.save(schedule);

        return reservationMapper.map(reservation);
    }

    private void validateBookReservation(Schedule schedule) {
        schedule.getReservations().stream()
    			.findFirst()
    			.ifPresent(reservation -> {throw new IllegalArgumentException("Schedule already reserved.");});

		if (schedule.getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Can schedule future date.");
		}
    }

    public ReservationDTO findReservation(Long reservationId) {
        return reservationRepository.findById(reservationId).map(reservationMapper::map).orElseThrow(() -> {
            throw new EntityNotFoundException("Reservation not found.");
        });
    }

    public ReservationDTO cancelReservation(Long reservationId) {
        return reservationMapper.map(this.cancel(reservationId));
    }

    private Reservation cancel(Long reservationId) {
        return reservationRepository.findById(reservationId).map(reservation -> {

            this.validateCancellation(reservation);

            BigDecimal refundValue = getRefundValue(reservation);
            return this.updateReservation(reservation, refundValue, ReservationStatus.CANCELLED);

        }).orElseThrow(() -> {
            throw new EntityNotFoundException("Reservation not found.");
        });
    }

    private Reservation updateReservation(Reservation reservation, BigDecimal refundValue, ReservationStatus status) {
        reservation.setReservationStatus(status);
        reservation.setValue(reservation.getValue().subtract(refundValue));
        reservation.setRefundValue(refundValue);

        return reservationRepository.save(reservation);
    }

    private void validateCancellation(Reservation reservation) {
        if (!READY_TO_PLAY.equals(reservation.getReservationStatus())) {
            throw new IllegalArgumentException("Cannot cancel/reschedule because it's not in ready to play status.");
        }

        if (reservation.getSchedule().getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Can cancel/reschedule only future dates.");
        }
    }

    public BigDecimal getRefundValue(Reservation reservation) {
        long hours = ChronoUnit.HOURS.between(LocalDateTime.now(), reservation.getSchedule().getStartDateTime());

        if (hours >= 24) {
            return reservation.getValue();
        }

        return BigDecimal.ZERO;
    }

    @Transactional
    public ReservationDTO rescheduleReservation(Long previousReservationId, Long scheduleId) {
        Reservation previousReservation = cancel(previousReservationId);

        if (scheduleId.equals(previousReservation.getSchedule().getId())) {
            throw new IllegalArgumentException("Cannot reschedule to the same slot.");
        }

        previousReservation.setReservationStatus(ReservationStatus.RESCHEDULED);
        reservationRepository.save(previousReservation);

        ReservationDTO newReservation = bookReservation(CreateReservationRequestDTO.builder()
                .guestId(previousReservation.getGuest().getId()).scheduleId(scheduleId).build());
        newReservation.setPreviousReservation(reservationMapper.map(previousReservation));
        return newReservation;
    }
}
