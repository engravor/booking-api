package com.volcano.island.booking.service;

import com.volcano.island.booking.exception.BadRequestException;
import com.volcano.island.booking.exception.NotAvailabilityException;
import com.volcano.island.booking.model.Reservation;
import com.volcano.island.booking.model.StatusName;
import com.volcano.island.booking.payload.ReservationRequest;
import com.volcano.island.booking.repository.ReservationRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ReservationService reservationService;

    @Before
    public void init() {
        reservationService = new ReservationService(reservationRepository);
    }

    @Test
    public void testCreateReservationValidation() {
        LocalDate checkInDay = LocalDate.now().plusDays(1);
        LocalDate checkoutDate = LocalDate.now().plusDays(2);
        Reservation reservation = new Reservation(checkInDay, checkoutDate);
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(reservation);
        Mockito.when(reservationRepository.findReservationBetweenDays(checkInDay, checkoutDate)).thenReturn(reservations);

        // Try to generate new reservation for same days
        ReservationRequest reservationRequest = new ReservationRequest();
        reservationRequest.setCheckInDate(checkInDay);
        reservationRequest.setCheckoutDate(checkoutDate);
        reservationRequest.setEmail("a_guest@gmail.com");
        reservationRequest.setGuestFirstName("A");
        reservationRequest.setGuestLastName("Guest");

        thrown.expect(NotAvailabilityException.class);
        thrown.expectMessage("There is almost one reservation already booked for the days provided. Try again with another dates.");
        reservationService.create(reservationRequest);
    }

    @Test
    public void testUpdateReservationCancelledValidations() {
        Long reservationId = 1L;
        LocalDate checkInDay = LocalDate.now().plusDays(1);
        LocalDate checkoutDate = LocalDate.now().plusDays(2);
        Reservation reservation = new Reservation(checkInDay, checkoutDate);
        reservation.setStatus(StatusName.CANCELLED);
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(reservation);

        Mockito.when(reservationRepository.findById(reservationId)).thenReturn(Optional.ofNullable(reservation));

        thrown.expect(BadRequestException.class);
        thrown.expectMessage("The reservation is already Cancelled. It cannot be updated.");

        ReservationRequest reservationRequest = new ReservationRequest();
        reservationService.update(reservationId, reservationRequest);
    }

    @Test
    public void testUpdateReservationDatesValidations() {
        Long reservationId = 1L;
        LocalDate checkInDay = LocalDate.now().plusDays(1);
        LocalDate checkoutDate = LocalDate.now().plusDays(2);
        Reservation reservation = new Reservation(checkInDay, checkoutDate);
        reservation.setStatus(StatusName.BOOKED);
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(reservation);

        Mockito.when(reservationRepository.findById(reservationId)).thenReturn(Optional.ofNullable(reservation));
        Mockito.when(reservationRepository.findReservationBetweenDays(checkInDay, checkoutDate)).thenReturn(reservations);

        thrown.expect(NotAvailabilityException.class);
        thrown.expectMessage("There is almost one reservation already booked for the days provided. Try again with another dates.");

        ReservationRequest reservationRequest = new ReservationRequest();
        reservationRequest.setCheckInDate(checkInDay);
        reservationRequest.setCheckoutDate(checkoutDate);
        reservationService.update(reservationId, reservationRequest);
    }

    @Test
    public void testFindAvailableDates() {
        LocalDate checkInDay = LocalDate.now().plusDays(1);
        LocalDate checkoutDate = LocalDate.now().plusDays(3);

        Reservation reservation = new Reservation(checkInDay, checkoutDate);
        reservation.setStatus(StatusName.BOOKED);
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(reservation);

        Mockito.when(reservationRepository.findReservationBetweenDays(checkInDay, checkoutDate.plusDays(2))).thenReturn(reservations);

        List<LocalDate> availableDates = reservationService.findAvailableDates(checkInDay, checkoutDate.plusDays(2));

        Assert.assertEquals(2, availableDates.size());
        Assert.assertTrue(availableDates.contains(checkoutDate));
        Assert.assertTrue(availableDates.contains(checkoutDate.plusDays(1)));
    }

}
