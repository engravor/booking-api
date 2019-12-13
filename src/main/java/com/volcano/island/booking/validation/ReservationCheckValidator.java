package com.volcano.island.booking.validation;


import com.volcano.island.booking.payload.ReservationRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

public class ReservationCheckValidator implements ConstraintValidator<PeriodDateValidator, ReservationRequest> {

    @Override
    public void initialize(PeriodDateValidator date) {
        // Nothing here
    }

    /**
     * This validates that Reservation params are valid in order to its creation.
     * It validates: that the check-out date is greater than check-in dates. That period of reservation is not superior to
     * 3 days and that reservation is minimum 1 day(s) ahead of arrival and up to 1 month in advance.
     *
     * @param reservationRequest
     * @param constraintValidatorContext
     * @return boolean
     */
    @Override
    public boolean isValid(ReservationRequest reservationRequest, ConstraintValidatorContext constraintValidatorContext) {
        LocalDate checkInDate = reservationRequest.getCheckInDate();
        LocalDate checkoutDate = reservationRequest.getCheckoutDate();

        if (checkInDate != null && checkoutDate != null && checkInDate.isAfter(checkoutDate)) {
            setNewErrorMessage("Check-in date can not be after Check-out date.", constraintValidatorContext);
            return false;
        }

        long reservation_days = DAYS.between(checkInDate, checkoutDate);
        if (reservation_days < 1 || reservation_days > 3) {
            setNewErrorMessage("Reservation period should be between 1 to 3 days.", constraintValidatorContext);
            return false;
        }

        long arrival = DAYS.between(LocalDate.now(), checkInDate);
        long maxArrivalDays = DAYS.between(LocalDate.now(), LocalDate.now().plusMonths(1));
        if (arrival < 1 || arrival > maxArrivalDays) {
            setNewErrorMessage("Reservations should be minimum 1 day(s) ahead of arrival and up to 1 month in advance.",
                    constraintValidatorContext);
            return false;
        }

        return true;
    }


    public static void setNewErrorMessage(String newErrorMessage, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(newErrorMessage)
                .addConstraintViolation();
    }
}