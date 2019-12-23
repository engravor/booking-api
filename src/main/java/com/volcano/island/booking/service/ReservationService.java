package com.volcano.island.booking.service;

import com.volcano.island.booking.exception.BadRequestException;
import com.volcano.island.booking.exception.NotAvailabilityException;
import com.volcano.island.booking.exception.ResourceNotFoundException;
import com.volcano.island.booking.model.Guest;
import com.volcano.island.booking.model.Reservation;
import com.volcano.island.booking.model.StatusName;
import com.volcano.island.booking.payload.ReservationRequest;
import com.volcano.island.booking.repository.ReservationRepository;
import com.volcano.island.booking.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    /**
     * Creates a new Reservation in the database.
     * <p>
     * Concurrent requests:
     * In order to allow concurrent requests avoiding overlaping over reservations, the key word synchronized is used. This will
     * help to avoid that multiple threads try to create a reservation at the same time. Nevertheless if this app is run with multiple
     * instances, this will not avoid the overlaping. For that it will be needed a shared lock, for example using Distributed locks with Redis
     * or something like that.
     *
     * @param reservationRequest
     * @return Reservation
     */
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

    /**
     * Updates Reservation for the provided ID in the database.
     * <p>
     * Concurrent requests:
     * In order to allow concurrent requests avoiding overlaping over reservations, the key word synchronized is used. This will
     * help to avoid that multiple threads try to create a reservation at the same time. Nevertheless if this app is run with multiple
     * instances, this will not avoid the overlaping. For that it will be needed a shared lock, for example using Distributed locks with Redis
     * or something like that.
     *
     * @param reservationRequest
     * @return Reservation
     */
    public synchronized Reservation update(Long reservationId, final ReservationRequest reservationRequest) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "reservationId", reservationId));

        if (reservation.getStatus() == StatusName.CANCELLED) {
            throw new BadRequestException("The reservation is already Cancelled. It cannot be updated.");
        }

        List<Reservation> reservations = reservationRepository.findReservationBetweenDays(reservationRequest.getCheckInDate(), reservationRequest.getCheckoutDate());
        if (reservations.size() > 0) {
            throw new NotAvailabilityException();
        }

        if (reservationRequest.getCheckInDate() != null) {
            reservation.setCheckIn(reservationRequest.getCheckInDate());
        }
        if (reservationRequest.getCheckoutDate() != null) {
            reservation.setCheckOut(reservationRequest.getCheckoutDate());
        }
        if (reservationRequest.getEmail() != null) {
            reservation.getGuest().setEmail(reservationRequest.getEmail());
        }
        if (reservationRequest.getGuestFirstName() != null) {
            reservation.getGuest().setFirstName(reservationRequest.getGuestFirstName());
        }
        if (reservationRequest.getGuestLastName() != null) {
            reservation.getGuest().setLastName(reservationRequest.getGuestLastName());
        }

        return reservationRepository.save(reservation);
    }

    /**
     * Retrieve a list of available dates to make reservations for a provided period of time. If the period is not
     * provided it will return the available dates for 1 month.
     *
     * @param from
     * @param to
     * @return List<LocalDate>
     */
    public List<LocalDate> findAvailableDates(LocalDate from, LocalDate to) {
        if (from == null) {
            from = LocalDate.now().plusDays(1);
        }

        if (to == null) {
            to = from.plusMonths(1);
        }

        if (from.isAfter(to)) {
            throw new BadRequestException("Check-in date can not be after Check-out date.");
        }

        List<List<LocalDate>> reservedDates = reservationRepository.findReservationBetweenDays(from, to)
                .stream().map(r -> DateUtil.getDateRange(r.getCheckIn(), r.getCheckOut())).collect(Collectors.toList());

        List<LocalDate> monthDates = DateUtil.getDateRange(from, to);

        for (List<LocalDate> dates : reservedDates) {
            monthDates.removeAll(dates);
        }

        return monthDates;
    }
}
