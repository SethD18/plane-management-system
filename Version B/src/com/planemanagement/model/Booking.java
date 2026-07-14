package com.planemanagement.model;

import com.planemanagement.model.enums.BookingStatus;
import com.planemanagement.model.enums.SeatClass;

import java.util.Objects;

public class Booking {
    private final String bookingId;
    private final Passenger passenger;
    private final Flight flight;
    private final SeatClass seatClass;
    private final String seatNumber;
    private BookingStatus status;

    public Booking(String bookingId, Passenger passenger, Flight flight, SeatClass seatClass, String seatNumber) {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            throw new IllegalArgumentException("Booking ID cannot be null or empty.");
        }
        this.bookingId = bookingId;
        this.passenger = Objects.requireNonNull(passenger, "Passenger cannot be null.");
        this.flight = Objects.requireNonNull(flight, "Flight cannot be null.");
        this.seatClass = Objects.requireNonNull(seatClass, "Seat class cannot be null.");
        this.seatNumber = Objects.requireNonNull(seatNumber, "Seat number cannot be null.");
        this.status = BookingStatus.ACTIVE;
    }

    public String getBookingId() {
        return bookingId;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public Flight getFlight() {
        return flight;
    }

    public SeatClass getSeatClass() {
        return seatClass;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void checkIn() {
        if (this.status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Cannot check in a cancelled booking.");
        }
        this.status = BookingStatus.CHECKED_IN;
    }

    public void cancel() {
        if (this.status == BookingStatus.CHECKED_IN) {
            throw new IllegalStateException("Cannot cancel an already checked-in booking.");
        }
        this.status = BookingStatus.CANCELLED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(bookingId, booking.bookingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId);
    }

    @Override
    public String toString() {
        return String.format("Booking ID: %s | Passenger: %s | Seat: %s | Class: %s | Status: %s", 
            bookingId, passenger.getName(), seatNumber, seatClass, status);
    }
}
