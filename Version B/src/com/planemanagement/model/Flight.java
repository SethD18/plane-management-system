package com.planemanagement.model;

import com.planemanagement.model.enums.FlightStatus;
import com.planemanagement.model.enums.SeatClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Flight {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final String flightNumber;
    private final String origin;
    private final String destination;
    private final LocalDateTime departureTime;
    private final LocalDateTime arrivalTime;
    private FlightStatus status;
    private final Aircraft aircraft;
    private final List<Booking> bookings;

    public Flight(String flightNumber, String origin, String destination, 
                  LocalDateTime departureTime, LocalDateTime arrivalTime, Aircraft aircraft) {
        if (flightNumber == null || flightNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Flight number cannot be null or empty.");
        }
        if (origin == null || origin.trim().isEmpty()) {
            throw new IllegalArgumentException("Origin cannot be null or empty.");
        }
        if (destination == null || destination.trim().isEmpty()) {
            throw new IllegalArgumentException("Destination cannot be null or empty.");
        }
        if (departureTime == null || arrivalTime == null) {
            throw new IllegalArgumentException("Departure and arrival times cannot be null.");
        }
        if (arrivalTime.isBefore(departureTime)) {
            throw new IllegalArgumentException("Arrival time cannot be before departure time.");
        }
        this.flightNumber = flightNumber.trim().toUpperCase();
        this.origin = origin.trim();
        this.destination = destination.trim();
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.aircraft = Objects.requireNonNull(aircraft, "Aircraft cannot be null.");
        this.status = FlightStatus.SCHEDULED;
        this.bookings = new ArrayList<>();
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = Objects.requireNonNull(status, "Status cannot be null.");
    }

    public Aircraft getAircraft() {
        return aircraft;
    }

    public List<Booking> getBookings() {
        return new ArrayList<>(bookings); // Return defensive copy
    }

    public void addBooking(Booking booking) {
        this.bookings.add(booking);
    }

    /**
     * Finds active bookings in a specific seat class using modern Java Streams.
     */
    public List<Booking> getActiveBookingsForClass(SeatClass seatClass) {
        return bookings.stream()
                .filter(b -> b.getSeatClass() == seatClass)
                .filter(b -> b.getStatus() != com.planemanagement.model.enums.BookingStatus.CANCELLED)
                .collect(Collectors.toList());
    }

    /**
     * Gets the count of active bookings for a seat class.
     */
    public int getActiveCountForClass(SeatClass seatClass) {
        return (int) bookings.stream()
                .filter(b -> b.getSeatClass() == seatClass)
                .filter(b -> b.getStatus() != com.planemanagement.model.enums.BookingStatus.CANCELLED)
                .count();
    }

    /**
     * Checks if a seat number is currently occupied by an active booking.
     */
    public boolean isSeatOccupied(String seatNumber) {
        return bookings.stream()
                .filter(b -> b.getStatus() != com.planemanagement.model.enums.BookingStatus.CANCELLED)
                .anyMatch(b -> b.getSeatNumber().equalsIgnoreCase(seatNumber));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flight flight = (Flight) o;
        return Objects.equals(flightNumber, flight.flightNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flightNumber);
    }

    @Override
    public String toString() {
        return String.format("Flight %s | %s -> %s | Departs: %s | Status: %s | Aircraft: %s", 
            flightNumber, origin, destination, departureTime.format(FORMATTER), status, aircraft.getModel());
    }
}
