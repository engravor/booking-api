package com.volcano.island.booking.service;

import com.volcano.island.booking.exception.NotAvailabilityException;
import com.volcano.island.booking.model.Guest;
import com.volcano.island.booking.model.Reservation;
import com.volcano.island.booking.payload.ReservationRequest;
import com.volcano.island.booking.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    public synchronized Reservation create(final ReservationRequest reservationRequest) {
        List<Reservation> reservations = reservationRepository.findReservationBetweenDays(reservationRequest.getCheckInDate(), reservationRequest.getCheckoutDate());
        if (reservations.size() > 0) {
            throw new NotAvailabilityException();
        }

        Reservation reservation = new Reservation(reservationRequest.getCheckInDate(),
                reservationRequest.getCheckoutDate());

        Guest guest = new Guest(reservationRequest.getGuestFirstName(), reservationRequest.getGuestLastName(),
                reservationRequest.getEmail());

        reservation.setGuest(guest);

        return reservationRepository.save(reservation);

    }
}
