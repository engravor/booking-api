package com.volcano.island.booking.controller;

import com.volcano.island.booking.exception.ResourceNotFoundException;
import com.volcano.island.booking.model.Guest;
import com.volcano.island.booking.model.Reservation;
import com.volcano.island.booking.model.StatusName;
import com.volcano.island.booking.payload.ReservationRequest;
import com.volcano.island.booking.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;


    /**
     * Retrieve all reservations
     *
     * @return List<Reservation>
     */
    @GetMapping()
    public List<Reservation> getReservations() {
        return reservationRepository.findAll();
    }

    /**
     * Create a new Reservation
     *
     * @param reservationRequest
     * @return Reservation
     */
    @PostMapping()
    public Reservation bookReservation(@Valid @RequestBody ReservationRequest reservationRequest) {

        Reservation reservation = new Reservation(reservationRequest.getCheckInDate(),
                reservationRequest.getCheckoutDate());

        Guest guest = new Guest(reservationRequest.getGuestFirstName(), reservationRequest.getGuestLastName(),
                reservationRequest.getEmail());

        reservation.setGuest(guest);

        reservation = reservationRepository.save(reservation);
        return reservation;
    }

    /**
     * Update checkin or checkout (or both) reservation's dates for the reservationId provided
     *
     * @param reservationId
     * @param reservationRequest
     * @return Reservation
     */
    @PatchMapping("/{reservationId}")
    public Reservation updateReservation(@PathVariable("reservationId") Long reservationId, @RequestBody ReservationRequest reservationRequest) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "reservationId", reservationId));

        if (reservationRequest.getCheckInDate() != null) {
            reservation.setCheckIn(reservationRequest.getCheckInDate());
        }

        if (reservationRequest.getCheckoutDate() != null) {
            reservation.setCheckIn(reservationRequest.getCheckoutDate());
        }

        Reservation updatedReservation = reservationRepository.save(reservation);
        return updatedReservation;
    }

    /**
     * Cancell reservation for provided reservationId
     *
     * @param reservationId
     * @return Reservation
     */
    @DeleteMapping("/{reservationId}/cancel")
    public Reservation cancelReservation(@PathVariable("reservationId") Long reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "reservationId", reservationId));

        reservation.setStatus(StatusName.CANCELLED);

        Reservation updatedReservation = reservationRepository.save(reservation);
        return reservation;
    }

}