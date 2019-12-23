package com.volcano.island.booking.controller;

import com.volcano.island.booking.model.Reservation;
import com.volcano.island.booking.payload.ReservationRequest;
import com.volcano.island.booking.repository.ReservationRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
public class ReservationControllerConcurrencyTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    public void testMultipleUsersReservingSamePeriod() throws InterruptedException {
        // Send multiple threads in order to generate reservations to same day
        runMultithreaded(
                new Runnable() {
                    public void run() {
                        try {
                            testRestTemplate.postForEntity("/api/reservations",
                                    new ReservationRequest("Test", "User", "test.user@gmail.com",
                                            LocalDate.now().plusDays(1), LocalDate.now().plusDays(4)), Reservation.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                10
        );

        // Check only one reservation went through
        List<Reservation> createdReservations = reservationRepository.findAll();
        Assert.assertEquals(1, createdReservations.size());
    }


    /**
     * Runs the provided runnable in multiple parallel threads.
     *
     * @param runnable
     * @param threadCount
     * @throws InterruptedException
     */
    private void runMultithreaded(Runnable runnable, int threadCount) throws InterruptedException {
        List<Thread> threadList = new LinkedList<Thread>();
        for (int i = 0; i < threadCount; i++) {
            threadList.add(new Thread(runnable));
        }
        for (Thread t : threadList) {
            t.start();
        }
        for (Thread t : threadList) {
            t.join();
        }
    }
}
