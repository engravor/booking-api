package com.volcano.island.booking.controller;

import com.volcano.island.booking.exception.ResourceNotFoundException;
import com.volcano.island.booking.model.Reservation;
import com.volcano.island.booking.model.StatusName;
import com.volcano.island.booking.payload.ReservationRequest;
import com.volcano.island.booking.repository.ReservationRepository;
import com.volcano.island.booking.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationService reservationService;


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
    public ResponseEntity<Reservation> bookReservation(@Valid @RequestBody ReservationRequest reservationRequest) {

        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.create(reservationRequest));
    }

    /**
     * Get Reservation by id
     *
     * @param reservationId
     * @return Reservation
     */
    @GetMapping("/{reservationId}")
    public ResponseEntity<Optional<Reservation>> bookReservation(@PathVariable("reservationId") Long reservationId) {

        return ResponseEntity.ok().body(reservationRepository.findById(reservationId));
    }

    /**
     * Update checkin or checkout (or both) reservation's dates for the reservationId provided
     *
     * @param reservationId
     * @param reservationRequest
     * @return Reservation
     */
    @PatchMapping("/{reservationId}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable("reservationId") Long reservationId, @RequestBody ReservationRequest reservationRequest) {

        return ResponseEntity.ok().body(reservationService.update(reservationId, reservationRequest));
    }

    /**
     * Cancell reservation for provided reservationId
     *
     * @param reservationId
     * @return Reservation
     */
    @DeleteMapping("/{reservationId}/cancel")
    public ResponseEntity<Reservation> cancelReservation(@PathVariable("reservationId") Long reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "reservationId", reservationId));

        reservation.setStatus(StatusName.CANCELLED);

        return ResponseEntity.ok().body(reservationRepository.save(reservation));
    }

}
