package com.volcano.island.booking.controller;

import com.volcano.island.booking.payload.AvailabilityRequest;
import com.volcano.island.booking.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/availability")
public class AvailabilityController {

    @Autowired
    private ReservationService reservationService;

    /**
     * Search available dates for a provided range of dates. If dates not provided the default period is 1 month.
     *
     * @param availabilityRequest
     * @return ResponseEntity<List<LocalDate>>
     */
    @PostMapping
    public ResponseEntity<List<LocalDate>> searchAvailableDates(@RequestBody AvailabilityRequest availabilityRequest) {
        List<LocalDate> daysAvailable = reservationService.findAvailableDates(availabilityRequest.getCheckInDate(),
                availabilityRequest.getCheckoutDate());

        return ResponseEntity.ok().body(daysAvailable);
    }

}
