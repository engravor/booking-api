package com.volcano.island.booking.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.volcano.island.booking.validation.PeriodDateValidator;

import javax.validation.constraints.Email;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@PeriodDateValidator(message = "Invalid reservation days.")
public class ReservationRequest {

    @NotBlank
    private String guestFirstName;

    @NotBlank
    private String guestLastName;

    @NotBlank
    @Email
    private String email;

    @NotNull
    @Future
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate checkInDate;

    @NotNull
    @Future
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate checkoutDate;

    public ReservationRequest() {
    }

    public ReservationRequest(@NotBlank String guestFirstName, @NotBlank String guestLastName, @NotBlank @Email String email, @NotNull @Future LocalDate checkInDate, @NotNull @Future LocalDate checkoutDate) {
        this.guestFirstName = guestFirstName;
        this.guestLastName = guestLastName;
        this.email = email;
        this.checkInDate = checkInDate;
        this.checkoutDate = checkoutDate;
    }

    public String getGuestFirstName() {
        return guestFirstName;
    }

    public void setGuestFirstName(String guestFirstName) {
        this.guestFirstName = guestFirstName;
    }

    public String getGuestLastName() {
        return guestLastName;
    }

    public void setGuestLastName(String guestLastName) {
        this.guestLastName = guestLastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckoutDate() {
        return checkoutDate;
    }

    public void setCheckoutDate(LocalDate checkoutDate) {
        this.checkoutDate = checkoutDate;
    }
}
