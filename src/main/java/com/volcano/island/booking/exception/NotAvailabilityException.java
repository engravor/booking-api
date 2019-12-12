package com.volcano.island.booking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class NotAvailabilityException extends RuntimeException {

    private static final String NO_AVAILABILITY_MESSAGE = "There is almost one reservation already booked for the days provided. Try again with another dates.";

    public NotAvailabilityException() {
        super(NO_AVAILABILITY_MESSAGE);
    }
}
