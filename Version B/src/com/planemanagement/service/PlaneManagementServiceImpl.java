package com.planemanagement.service;

import com.planemanagement.exception.*;
import com.planemanagement.model.Aircraft;
import com.planemanagement.model.Booking;
import com.planemanagement.model.Flight;
import com.planemanagement.model.Passenger;
import com.planemanagement.model.enums.BookingStatus;
import com.planemanagement.model.enums.FlightStatus;
import com.planemanagement.model.enums.SeatClass;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class PlaneManagementServiceImpl implements PlaneManagementService {
    private final Map<String, Aircraft> aircraftMap = new HashMap<>();
    private final Map<String, Passenger> passengerMap = new HashMap<>();
    private final Map<String, Flight> flightMap = new HashMap<>();
    private final Map<String, Booking> bookingMap = new HashMap<>();
    private final AtomicInteger bookingIdGenerator = new AtomicInteger(10001);

    @Override
    public void addAircraft(Aircraft aircraft) throws PlaneManagementException {
        Objects.requireNonNull(aircraft, "Aircraft cannot be null.");
        if (aircraftMap.containsKey(aircraft.getId())) {
            throw new PlaneManagementException("Aircraft with ID " + aircraft.getId() + " already exists.");
        }
        aircraftMap.put(aircraft.getId(), aircraft);
    }

    @Override
    public void registerPassenger(Passenger passenger) throws PlaneManagementException {
        Objects.requireNonNull(passenger, "Passenger cannot be null.");
        if (passengerMap.containsKey(passenger.getPassportNumber())) {
            throw new PlaneManagementException("Passenger with Passport " + passenger.getPassportNumber() + " already registered.");
        }
        passengerMap.put(passenger.getPassportNumber(), passenger);
    }

    @Override
    public void scheduleFlight(Flight flight) throws PlaneManagementException {
        Objects.requireNonNull(flight, "Flight cannot be null.");
        if (flightMap.containsKey(flight.getFlightNumber())) {
            throw new PlaneManagementException("Flight with Number " + flight.getFlightNumber() + " is already scheduled.");
        }
        // Verify aircraft exists in system
        if (!aircraftMap.containsKey(flight.getAircraft().getId())) {
            throw new EntityNotFoundException("Aircraft with ID " + flight.getAircraft().getId() + " does not exist in the system registry.");
        }
        flightMap.put(flight.getFlightNumber(), flight);
    }

    @Override
    public Booking bookSeat(String passportNumber, String flightNumber, SeatClass seatClass) throws PlaneManagementException {
        Objects.requireNonNull(passportNumber, "Passport number cannot be null.");
        Objects.requireNonNull(flightNumber, "Flight number cannot be null.");
        Objects.requireNonNull(seatClass, "Seat class cannot be null.");

        String passportKey = passportNumber.trim().toUpperCase();
        String flightKey = flightNumber.trim().toUpperCase();

        Passenger passenger = passengerMap.get(passportKey);
        if (passenger == null) {
            throw new EntityNotFoundException("Passenger with Passport " + passportNumber + " not found.");
        }

        Flight flight = flightMap.get(flightKey);
        if (flight == null) {
            throw new EntityNotFoundException("Flight " + flightNumber + " not found.");
        }

        // Rule: Only allow bookings if flight status is SCHEDULED
        if (flight.getStatus() != FlightStatus.SCHEDULED) {
            throw new FlightNotScheduledException("Cannot book seat on flight " + flightNumber + 
                ". Bookings are only allowed when status is SCHEDULED. Current status: " + flight.getStatus());
        }

        // Rule: Prevent duplicate bookings (passenger cannot have two active bookings on same flight)
        boolean hasActiveBooking = flight.getBookings().stream()
                .filter(b -> b.getStatus() != BookingStatus.CANCELLED)
                .anyMatch(b -> b.getPassenger().equals(passenger));
        if (hasActiveBooking) {
            throw new DuplicateBookingException("Passenger " + passenger.getName() + 
                " already has an active booking on flight " + flightNumber);
        }

        // Determine capacity and check limits
        Aircraft aircraft = flight.getAircraft();
        int capacity = 0;
        switch (seatClass) {
            case ECONOMY:
                capacity = aircraft.getEconomyCapacity();
                break;
            case BUSINESS:
                capacity = aircraft.getBusinessCapacity();
                break;
            case FIRST:
                capacity = aircraft.getFirstClassCapacity();
                break;
        }

        int activeBookingsCount = flight.getActiveCountForClass(seatClass);
        if (activeBookingsCount >= capacity) {
            throw new NoSeatsAvailableException("No seats available in " + seatClass + " class on flight " + flightNumber);
        }

        // Sequential seat assignment: find lowest unoccupied seat number (e.g. E1, E2, B1...)
        String seatPrefix = seatClass.getPrefix();
        String seatNumber = null;
        for (int i = 1; i <= capacity; i++) {
            String testSeat = seatPrefix + i;
            if (!flight.isSeatOccupied(testSeat)) {
                seatNumber = testSeat;
                break;
            }
        }

        if (seatNumber == null) {
            // Fallback (should not happen if capacities are tracked correctly)
            throw new NoSeatsAvailableException("Failed to allocate a seat number, class capacities may be fully booked.");
        }

        // Create booking
        String bookingId = "BKG-" + bookingIdGenerator.getAndIncrement();
        Booking booking = new Booking(bookingId, passenger, flight, seatClass, seatNumber);
        flight.addBooking(booking);
        bookingMap.put(bookingId, booking);

        return booking;
    }

    @Override
    public void cancelBooking(String bookingId) throws PlaneManagementException {
        Booking booking = getBooking(bookingId);
        booking.cancel();
    }

    @Override
    public void checkInPassenger(String bookingId) throws PlaneManagementException {
        Booking booking = getBooking(bookingId);
        
        // Ensure flight is not cancelled or completed
        FlightStatus flightStatus = booking.getFlight().getStatus();
        if (flightStatus == FlightStatus.CANCELLED) {
            throw new PlaneManagementException("Cannot check in. Flight is CANCELLED.");
        }
        if (flightStatus == FlightStatus.COMPLETED) {
            throw new PlaneManagementException("Cannot check in. Flight is already COMPLETED.");
        }

        booking.checkIn();
    }

    @Override
    public void updateFlightStatus(String flightNumber, FlightStatus status) throws PlaneManagementException {
        Flight flight = getFlight(flightNumber);
        
        // Rule checks on status transitions if any
        FlightStatus oldStatus = flight.getStatus();
        if (oldStatus == FlightStatus.COMPLETED || oldStatus == FlightStatus.CANCELLED) {
            throw new PlaneManagementException("Cannot change status of a flight that is already " + oldStatus);
        }

        flight.setStatus(status);

        // If flight is cancelled, cancel all active bookings
        if (status == FlightStatus.CANCELLED) {
            flight.getBookings().stream()
                    .filter(b -> b.getStatus() != BookingStatus.CANCELLED)
                    .forEach(b -> {
                        try {
                            b.cancel();
                        } catch (Exception ignored) {}
                    });
        }
    }

    @Override
    public List<Flight> searchFlightsByRoute(String origin, String destination) {
        if (origin == null || destination == null) {
            return Collections.emptyList();
        }
        String orig = origin.trim();
        String dest = destination.trim();
        return flightMap.values().stream()
                .filter(f -> f.getOrigin().equalsIgnoreCase(orig) && f.getDestination().equalsIgnoreCase(dest))
                .collect(Collectors.toList());
    }

    @Override
    public List<Aircraft> getAircrafts() {
        return new ArrayList<>(aircraftMap.values());
    }

    @Override
    public List<Passenger> getPassengers() {
        return new ArrayList<>(passengerMap.values());
    }

    @Override
    public List<Flight> getFlights() {
        return new ArrayList<>(flightMap.values());
    }

    @Override
    public List<Booking> getBookings() {
        return new ArrayList<>(bookingMap.values());
    }

    @Override
    public Aircraft getAircraft(String id) throws PlaneManagementException {
        if (id == null) throw new IllegalArgumentException("Aircraft ID cannot be null.");
        Aircraft aircraft = aircraftMap.get(id.trim().toUpperCase());
        if (aircraft == null) {
            throw new EntityNotFoundException("Aircraft with ID " + id + " not found.");
        }
        return aircraft;
    }

    @Override
    public Passenger getPassenger(String passportNumber) throws PlaneManagementException {
        if (passportNumber == null) throw new IllegalArgumentException("Passport number cannot be null.");
        Passenger passenger = passengerMap.get(passportNumber.trim().toUpperCase());
        if (passenger == null) {
            throw new EntityNotFoundException("Passenger with Passport " + passportNumber + " not found.");
        }
        return passenger;
    }

    @Override
    public Flight getFlight(String flightNumber) throws PlaneManagementException {
        if (flightNumber == null) throw new IllegalArgumentException("Flight number cannot be null.");
        Flight flight = flightMap.get(flightNumber.trim().toUpperCase());
        if (flight == null) {
            throw new EntityNotFoundException("Flight " + flightNumber + " not found.");
        }
        return flight;
    }

    @Override
    public Booking getBooking(String bookingId) throws PlaneManagementException {
        if (bookingId == null) throw new IllegalArgumentException("Booking ID cannot be null.");
        Booking booking = bookingMap.get(bookingId.trim().toUpperCase());
        if (booking == null) {
            throw new EntityNotFoundException("Booking " + bookingId + " not found.");
        }
        return booking;
    }
}
