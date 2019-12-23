package com.volcano.island.booking.controller;


import com.volcano.island.booking.model.Reservation;
import com.volcano.island.booking.model.StatusName;
import com.volcano.island.booking.payload.ReservationRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
public class ReservationControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Before
    public void setUp() {
        testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    @Test
    public void testGetReservations() {
        ResponseEntity<String> response = testRestTemplate.getForEntity("/api/reservations", String.class);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void testBookReservation() {
        ResponseEntity<Reservation> reservationResponse = testRestTemplate.postForEntity("/api/reservations",
                new ReservationRequest("Test", "User", "test.user@gmail.com",
                        LocalDate.now().plusDays(1), LocalDate.now().plusDays(4)), Reservation.class);

        Assert.assertEquals(HttpStatus.CREATED, reservationResponse.getStatusCode());
        Assert.assertNotNull(reservationResponse.getBody().getId());

    }

    @Test
    public void testCancelReservation() {
        ResponseEntity<Reservation> reservationResponse = testRestTemplate.postForEntity("/api/reservations",
                new ReservationRequest("Test", "User", "test.user@gmail.com",
                        LocalDate.now().plusDays(1), LocalDate.now().plusDays(4)), Reservation.class);

        Assert.assertEquals(HttpStatus.CREATED, reservationResponse.getStatusCode());
        Assert.assertNotNull(reservationResponse.getBody().getId());
        Long reservationId = reservationResponse.getBody().getId();

        testRestTemplate.delete("/api/reservations/" + reservationId + "/cancel");

        ResponseEntity<Reservation> cancelledReservation = testRestTemplate.getForEntity("/api/reservations/" + reservationId, Reservation.class);

        Assert.assertEquals(
                StatusName.CANCELLED, cancelledReservation.getBody().getStatus()
        );

    }

    @Test
    public void testUpdateReservation() {
        ResponseEntity<Reservation> reservationResponse = testRestTemplate.postForEntity("/api/reservations",
                new ReservationRequest("Test", "User", "test.user@gmail.com",
                        LocalDate.now().plusDays(1), LocalDate.now().plusDays(4)), Reservation.class);

        Long reservationId = reservationResponse.getBody().getId();

        ReservationRequest payload = new ReservationRequest();
        payload.setCheckInDate(LocalDate.now().plusDays(2));
        payload.setGuestFirstName("Change Name");

        ReservationRequest reservationRequest = new ReservationRequest();

        reservationRequest.setCheckInDate(LocalDate.now().plusDays(2));

        testRestTemplate.patchForObject("/api/reservations/" + reservationId, reservationRequest, Reservation.class);

        ResponseEntity<Reservation> updatedReservation = testRestTemplate.getForEntity("/api/reservations/" + reservationId, Reservation.class);

        Assert.assertNotEquals(reservationResponse.getBody().getCheckIn(), updatedReservation.getBody().getCheckIn());

    }


}
