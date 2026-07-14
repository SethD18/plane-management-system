package com.planemanagement;

import com.planemanagement.exception.*;
import com.planemanagement.model.*;
import com.planemanagement.model.enums.*;
import com.planemanagement.service.*;

import java.time.LocalDateTime;
import java.util.List;

public class VerificationTest {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("       RUNNING SYSTEM INTEGRATION TESTS           ");
        System.out.println("==================================================");

        PlaneManagementService service = new PlaneManagementServiceImpl();

        try {
            // 1. Setup Aircraft
            Aircraft plane1 = new Aircraft("AC101", "Boeing 737", 5, 2, 1);
            service.addAircraft(plane1);
            System.out.println("[PASS] Aircraft registration successful.");

            // 2. Setup Passengers
            Passenger passenger1 = new Passenger("John Doe", "PP1001", "john@example.com", "+123456789");
            Passenger passenger2 = new Passenger("Alice Smith", "PP1002", "alice@example.com", "+987654321");
            Passenger passenger3 = new Passenger("Bob Jones", "PP1003", "bob@example.com", "+555555555");
            service.registerPassenger(passenger1);
            service.registerPassenger(passenger2);
            service.registerPassenger(passenger3);
            System.out.println("[PASS] Passenger registrations successful.");

            // 3. Schedule a Flight
            LocalDateTime depTime = LocalDateTime.now().plusDays(1);
            LocalDateTime arrTime = depTime.plusHours(6);
            Flight flight1 = new Flight("FL999", "JFK", "LHR", depTime, arrTime, plane1);
            service.scheduleFlight(flight1);
            System.out.println("[PASS] Flight scheduling successful.");

            // 4. Book Seats (Sequential E1, E2, B1...)
            Booking b1 = service.bookSeat("PP1001", "FL999", SeatClass.ECONOMY);
            Booking b2 = service.bookSeat("PP1002", "FL999", SeatClass.ECONOMY);
            Booking b3 = service.bookSeat("PP1003", "FL999", SeatClass.BUSINESS);

            assertTest("E1".equals(b1.getSeatNumber()), "First Economy booking should be seat E1. Got: " + b1.getSeatNumber());
            assertTest("E2".equals(b2.getSeatNumber()), "Second Economy booking should be seat E2. Got: " + b2.getSeatNumber());
            assertTest("B1".equals(b3.getSeatNumber()), "First Business booking should be seat B1. Got: " + b3.getSeatNumber());
            System.out.println("[PASS] Sequential seat assignment working correctly.");

            // 5. Test Duplicate Booking Rule (Passenger cannot have two active bookings on same flight)
            try {
                service.bookSeat("PP1001", "FL999", SeatClass.ECONOMY);
                failTest("DuplicateBookingException should have been thrown for double booking on same flight!");
            } catch (DuplicateBookingException e) {
                System.out.println("[PASS] DuplicateBookingException correctly thrown: " + e.getMessage());
            }

            // 6. Test Flight Status Rule (Only allow bookings if status is SCHEDULED)
            service.updateFlightStatus("FL999", FlightStatus.ACTIVE);
            Passenger passenger4 = new Passenger("Charlie Brown", "PP1004", "charlie@example.com", "+11111111");
            service.registerPassenger(passenger4);
            try {
                service.bookSeat("PP1004", "FL999", SeatClass.ECONOMY);
                failTest("FlightNotScheduledException should have been thrown for booking on ACTIVE flight!");
            } catch (FlightNotScheduledException e) {
                System.out.println("[PASS] FlightNotScheduledException correctly thrown: " + e.getMessage());
            }

            // Reset flight status to SCHEDULED for further tests
            flight1.setStatus(FlightStatus.SCHEDULED);

            // 7. Test Cancel Booking and Seat Reuse
            service.cancelBooking(b1.getBookingId());
            assertTest(b1.getStatus() == BookingStatus.CANCELLED, "Booking should show CANCELLED status.");
            System.out.println("[PASS] Cancellation successful.");

            // Since E1 was cancelled, the next Economy booking should reuse E1
            Booking b4 = service.bookSeat("PP1004", "FL999", SeatClass.ECONOMY);
            assertTest("E1".equals(b4.getSeatNumber()), "E1 should be reused after cancellation. Got: " + b4.getSeatNumber());
            System.out.println("[PASS] Seat number E1 correctly reused after cancellation.");

            // 8. Test Search Flights by Route
            List<Flight> results = service.searchFlightsByRoute("JFK", "LHR");
            assertTest(results.size() == 1, "Should find 1 flight for JFK->LHR");
            assertTest("FL999".equals(results.get(0).getFlightNumber()), "Search route should return FL999");
            System.out.println("[PASS] Search flights by route (JFK -> LHR) successful using Streams.");

            System.out.println("\nALL INTEGRATION TESTS PASSED SUCCESSFULLY!");
        } catch (Exception e) {
            System.out.println("[FAIL] Unexpected exception occurred during testing:");
            e.printStackTrace();
        }
    }

    private static void assertTest(boolean condition, String message) {
        if (!condition) {
            throw new RuntimeException("Assertion failed: " + message);
        }
    }

    private static void failTest(String message) {
        throw new RuntimeException("Test Failed: " + message);
    }
}
