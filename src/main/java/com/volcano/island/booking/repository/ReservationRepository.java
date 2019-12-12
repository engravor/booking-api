package com.volcano.island.booking.repository;

import com.volcano.island.booking.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * Search BOOKED reservations for a provided period of time.
     *
     * @param from
     * @param to
     * @return List<Reservation>
     */
    @Query("SELECT r FROM Reservation r " +
            "WHERE r.status = 'BOOKED' AND :to > r.checkIn AND r.checkOut > :from" )
    List<Reservation> findReservationBetweenDays(@Param("from") LocalDate from,
                                   @Param("to") LocalDate to);
}
