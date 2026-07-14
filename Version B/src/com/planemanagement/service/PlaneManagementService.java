package com.planemanagement.service;

import com.planemanagement.exception.PlaneManagementException;
import com.planemanagement.model.Aircraft;
import com.planemanagement.model.Booking;
import com.planemanagement.model.Flight;
import com.planemanagement.model.Passenger;
import com.planemanagement.model.enums.FlightStatus;
import com.planemanagement.model.enums.SeatClass;

import java.util.List;

public interface PlaneManagementService {
    void addAircraft(Aircraft aircraft) throws PlaneManagementException;
    void registerPassenger(Passenger passenger) throws PlaneManagementException;
    void scheduleFlight(Flight flight) throws PlaneManagementException;
    
    Booking bookSeat(String passportNumber, String flightNumber, SeatClass seatClass) throws PlaneManagementException;
    void cancelBooking(String bookingId) throws PlaneManagementException;
    void checkInPassenger(String bookingId) throws PlaneManagementException;
    
    void updateFlightStatus(String flightNumber, FlightStatus status) throws PlaneManagementException;
    List<Flight> searchFlightsByRoute(String origin, String destination);
    
    List<Aircraft> getAircrafts();
    List<Passenger> getPassengers();
    List<Flight> getFlights();
    List<Booking> getBookings();
    
    Aircraft getAircraft(String id) throws PlaneManagementException;
    Passenger getPassenger(String passportNumber) throws PlaneManagementException;
    Flight getFlight(String flightNumber) throws PlaneManagementException;
    Booking getBooking(String bookingId) throws PlaneManagementException;
}
