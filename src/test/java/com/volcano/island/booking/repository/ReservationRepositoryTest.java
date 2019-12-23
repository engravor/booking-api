package com.volcano.island.booking.repository;

import com.volcano.island.booking.model.Guest;
import com.volcano.island.booking.model.Reservation;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testFindReservationBetweenDays() {
        Guest testUser = new Guest("Test", "User", "test_user@gmail.com");
        Reservation testUserReservation = new Reservation(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
        testUserReservation.setGuest(testUser);
        entityManager.persistAndFlush(testUserReservation);

        Assert.assertEquals(1, reservationRepository.findAll().size());

        List<Reservation> reservationBetweenDays = reservationRepository.findReservationBetweenDays(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));

        Assert.assertEquals(1, reservationBetweenDays.size());
    }


}
