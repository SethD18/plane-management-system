package com.planemanagement.cli;

import com.planemanagement.exception.*;
import com.planemanagement.model.*;
import com.planemanagement.model.enums.*;
import com.planemanagement.service.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class PlaneManagementApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final PlaneManagementService service = new PlaneManagementServiceImpl();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Colors for beautiful terminal UI (ANSI codes)
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BOLD = "\u001B[1m";

    /**
     * Checked exception to easily signal "go back to main menu" from nested input loops.
     */
    private static class BackException extends Exception {
        public BackException() {
            super("User requested navigation back to main menu.");
        }
    }

    public static void main(String[] args) {
        bootstrapData();
        runMainMenu();
    }

    private static void bootstrapData() {
        try {
            // Bootstrap Planes
            Aircraft a1 = new Aircraft("AC01", "Boeing 737", 10, 4, 2);
            Aircraft a2 = new Aircraft("AC02", "Airbus A320", 8, 2, 0);
            service.addAircraft(a1);
            service.addAircraft(a2);

            // Bootstrap Passengers
            Passenger p1 = new Passenger("Alice Smith", "PP101", "alice@example.com", "+123456789");
            Passenger p2 = new Passenger("Bob Jones", "PP102", "bob@example.com", "+987654321");
            Passenger p3 = new Passenger("Charlie Brown", "PP103", "charlie@example.com", "+5551234");
            service.registerPassenger(p1);
            service.registerPassenger(p2);
            service.registerPassenger(p3);

            // Bootstrap Flights
            LocalDateTime tomorrow = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
            Flight f1 = new Flight("FL101", "JFK", "LHR", tomorrow, tomorrow.plusHours(8), a1);
            Flight f2 = new Flight("FL102", "LHR", "CDG", tomorrow.plusDays(1).withHour(14).withMinute(0), tomorrow.plusDays(1).withHour(16).withMinute(30), a2);
            service.scheduleFlight(f1);
            service.scheduleFlight(f2);

            // Bootstrap Bookings
            service.bookSeat("PP101", "FL101", SeatClass.ECONOMY);
            service.bookSeat("PP102", "FL101", SeatClass.BUSINESS);
        } catch (Exception e) {
            System.err.println("Failed to bootstrap system data: " + e.getMessage());
        }
    }

    private static void runMainMenu() {
        while (true) {
            printMainMenuHeader();
            System.out.println("  1. Add Aircraft");
            System.out.println("  2. Schedule Flight");
            System.out.println("  3. Register Passenger");
            System.out.println("  4. Book a Seat");
            System.out.println("  5. Cancel Booking");
            System.out.println("  6. Passenger Check-in");
            System.out.println("  7. Update Flight Status");
            System.out.println("  8. Search Flights by Route");
            System.out.println("  9. View System Records (Aircrafts, Flights, Passengers, Bookings)");
            System.out.println("  0. " + RED + "Exit System" + RESET);
            System.out.println("=========================================================");
            System.out.print("Select an option (0-9): ");

            String choiceStr = scanner.nextLine().trim();
            if (choiceStr.isEmpty()) continue;

            try {
                int choice = Integer.parseInt(choiceStr);
                switch (choice) {
                    case 1:
                        addAircraftFlow();
                        break;
                    case 2:
                        scheduleFlightFlow();
                        break;
                    case 3:
                        registerPassengerFlow();
                        break;
                    case 4:
                        bookSeatFlow();
                        break;
                    case 5:
                        cancelBookingFlow();
                        break;
                    case 6:
                        checkInFlow();
                        break;
                    case 7:
                        updateFlightStatusFlow();
                        break;
                    case 8:
                        searchFlightsFlow();
                        break;
                    case 9:
                        viewRecordsFlow();
                        break;
                    case 0:
                        System.out.println(GREEN + "\nThank you for using the Plane Management System. Goodbye!" + RESET);
                        System.exit(0);
                    default:
                        System.out.println(RED + "Invalid option. Please enter a number between 0 and 9." + RESET);
                }
            } catch (NumberFormatException e) {
                System.out.println(RED + "Invalid input. Please enter a valid number." + RESET);
            }
        }
    }

    private static void printMainMenuHeader() {
        System.out.println("\n" + CYAN + BOLD + "=========================================================");
        System.out.println("      PLANE MANAGEMENT SYSTEM MAIN MENU (Version B)     ");
        System.out.println("=========================================================" + RESET);
    }

    private static void printFlowHeader(String flowName) {
        System.out.println("\n" + YELLOW + BOLD + ">>> " + flowName + " (Type 'back' or 'cancel' to return to menu) <<<" + RESET);
    }

    // ==========================================
    // CLI INPUT HELPERS WITH NAVIGATION SUPPORT
    // ==========================================

    private static String readString(String prompt) throws BackException {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if (input.equalsIgnoreCase("back") || input.equalsIgnoreCase("cancel")) {
            throw new BackException();
        }
        return input;
    }

    private static String readRequiredString(String prompt, String fieldName) throws BackException {
        while (true) {
            String val = readString(prompt);
            if (val.isEmpty()) {
                System.out.println(RED + fieldName + " cannot be empty. Try again." + RESET);
                continue;
            }
            return val;
        }
    }

    private static int readInt(String prompt, int min, int max) throws BackException {
        while (true) {
            String valStr = readString(prompt);
            try {
                int val = Integer.parseInt(valStr);
                if (val < min || val > max) {
                    System.out.println(RED + String.format("Please enter a number between %d and %d.", min, max) + RESET);
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println(RED + "Invalid number format. Try again." + RESET);
            }
        }
    }

    private static LocalDateTime readDateTime(String prompt) throws BackException {
        while (true) {
            String valStr = readString(prompt);
            if (valStr.isEmpty()) {
                System.out.println(RED + "Date/Time cannot be empty. Try again." + RESET);
                continue;
            }
            try {
                return LocalDateTime.parse(valStr, DATE_TIME_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println(RED + "Invalid format. Please use: yyyy-MM-dd HH:mm (e.g., 2026-07-14 10:00)" + RESET);
            }
        }
    }

    private static SeatClass readSeatClass(String prompt) throws BackException {
        while (true) {
            String valStr = readString(prompt).toUpperCase();
            try {
                return SeatClass.valueOf(valStr);
            } catch (IllegalArgumentException e) {
                System.out.println(RED + "Invalid seat class. Options: ECONOMY, BUSINESS, FIRST" + RESET);
            }
        }
    }

    private static FlightStatus readFlightStatus(String prompt) throws BackException {
        while (true) {
            String valStr = readString(prompt).toUpperCase();
            try {
                return FlightStatus.valueOf(valStr);
            } catch (IllegalArgumentException e) {
                System.out.println(RED + "Invalid flight status. Options: SCHEDULED, ACTIVE, COMPLETED, CANCELLED" + RESET);
            }
        }
    }

    // ==========================================
    // SYSTEM FLOWS
    // ==========================================

    private static void addAircraftFlow() {
        printFlowHeader("Add Aircraft");
        try {
            String id = readRequiredString("Enter Aircraft ID (e.g. AC03): ", "Aircraft ID");
            String model = readRequiredString("Enter Aircraft Model (e.g. Boeing 777): ", "Aircraft Model");
            int econ = readInt("Enter Economy seat capacity: ", 0, 500);
            int biz = readInt("Enter Business seat capacity: ", 0, 100);
            int first = readInt("Enter First Class seat capacity: ", 0, 50);

            Aircraft aircraft = new Aircraft(id, model, econ, biz, first);
            service.addAircraft(aircraft);
            System.out.println(GREEN + "\nSuccess: Aircraft '" + aircraft.getId() + "' successfully registered!" + RESET);
        } catch (BackException e) {
            System.out.println(YELLOW + "Cancelled: Returning to main menu." + RESET);
        } catch (PlaneManagementException e) {
            System.out.println(RED + "\nError: " + e.getMessage() + RESET);
        }
    }

    private static void scheduleFlightFlow() {
        printFlowHeader("Schedule Flight");
        try {
            // Check if there are aircrafts first
            if (service.getAircrafts().isEmpty()) {
                System.out.println(RED + "Error: No aircraft registered. Please register an aircraft first." + RESET);
                return;
            }

            String flightNum = readRequiredString("Enter Flight Number (e.g. FL103): ", "Flight Number");
            String origin = readRequiredString("Enter Origin airport (e.g. JFK): ", "Origin");
            String dest = readRequiredString("Enter Destination airport (e.g. CDG): ", "Destination");
            
            System.out.println("Enter Departure Date and Time (format: yyyy-MM-dd HH:mm): ");
            LocalDateTime depTime = readDateTime("  Departure: ");
            
            System.out.println("Enter Arrival Date and Time (format: yyyy-MM-dd HH:mm): ");
            LocalDateTime arrTime = readDateTime("  Arrival: ");

            // Show available aircrafts to choose from
            System.out.println("\nAvailable Aircrafts:");
            List<Aircraft> planes = service.getAircrafts();
            for (int i = 0; i < planes.size(); i++) {
                System.out.printf("  %d. %s\n", i + 1, planes.get(i).toString());
            }
            int planeSelection = readInt("Select Aircraft index (1-" + planes.size() + "): ", 1, planes.size());
            Aircraft aircraft = planes.get(planeSelection - 1);

            Flight flight = new Flight(flightNum, origin, dest, depTime, arrTime, aircraft);
            service.scheduleFlight(flight);
            System.out.println(GREEN + "\nSuccess: Flight " + flight.getFlightNumber() + " successfully scheduled!" + RESET);
        } catch (BackException e) {
            System.out.println(YELLOW + "Cancelled: Returning to main menu." + RESET);
        } catch (IllegalArgumentException | PlaneManagementException e) {
            System.out.println(RED + "\nError: " + e.getMessage() + RESET);
        }
    }

    private static void registerPassengerFlow() {
        printFlowHeader("Register Passenger");
        try {
            String name = readRequiredString("Enter Passenger Name: ", "Passenger Name");
            String passport = readRequiredString("Enter Passport Number: ", "Passport Number");
            String email = readRequiredString("Enter Email address: ", "Email");
            String phone = readRequiredString("Enter Phone number: ", "Phone number");

            Passenger passenger = new Passenger(name, passport, email, phone);
            service.registerPassenger(passenger);
            System.out.println(GREEN + "\nSuccess: Passenger " + passenger.getName() + " registered successfully!" + RESET);
        } catch (BackException e) {
            System.out.println(YELLOW + "Cancelled: Returning to main menu." + RESET);
        } catch (IllegalArgumentException | PlaneManagementException e) {
            System.out.println(RED + "\nError: " + e.getMessage() + RESET);
        }
    }

    private static void bookSeatFlow() {
        printFlowHeader("Book a Seat");
        try {
            if (service.getPassengers().isEmpty()) {
                System.out.println(RED + "Error: No passengers registered. Please register a passenger first." + RESET);
                return;
            }
            if (service.getFlights().isEmpty()) {
                System.out.println(RED + "Error: No flights scheduled. Please schedule a flight first." + RESET);
                return;
            }

            String passport = readRequiredString("Enter Passenger Passport Number: ", "Passport").toUpperCase();
            // Verify passenger exists
            service.getPassenger(passport); 

            String flightNum = readRequiredString("Enter Flight Number: ", "Flight Number").toUpperCase();
            // Verify flight exists
            service.getFlight(flightNum);

            System.out.println("Enter Seat Class (ECONOMY, BUSINESS, FIRST): ");
            SeatClass seatClass = readSeatClass("  Class: ");

            Booking booking = service.bookSeat(passport, flightNum, seatClass);
            System.out.println(GREEN + "\nSuccess: Booking completed successfully!" + RESET);
            System.out.println(CYAN + "  Booking ID: " + booking.getBookingId());
            System.out.println("  Assigned Seat: " + booking.getSeatNumber());
            System.out.println("  Seat Class: " + booking.getSeatClass() + RESET);
        } catch (BackException e) {
            System.out.println(YELLOW + "Cancelled: Returning to main menu." + RESET);
        } catch (PlaneManagementException e) {
            System.out.println(RED + "\nError: " + e.getMessage() + RESET);
        }
    }

    private static void cancelBookingFlow() {
        printFlowHeader("Cancel Booking");
        try {
            String bookingId = readRequiredString("Enter Booking ID to cancel (e.g. BKG-10001): ", "Booking ID").toUpperCase();
            // Verify booking exists
            Booking b = service.getBooking(bookingId);
            System.out.println("\nFound Booking Details:");
            System.out.println("  Passenger: " + b.getPassenger().getName());
            System.out.println("  Flight: " + b.getFlight().getFlightNumber() + " (" + b.getFlight().getOrigin() + " -> " + b.getFlight().getDestination() + ")");
            System.out.println("  Seat: " + b.getSeatNumber());
            System.out.println("  Current Status: " + b.getStatus());

            String confirm = readRequiredString("\nAre you sure you want to cancel this booking? (yes/no): ", "Confirmation");
            if (confirm.equalsIgnoreCase("yes") || confirm.equalsIgnoreCase("y")) {
                service.cancelBooking(bookingId);
                System.out.println(GREEN + "Success: Booking " + bookingId + " has been cancelled." + RESET);
            } else {
                System.out.println(YELLOW + "Cancellation aborted." + RESET);
            }
        } catch (BackException e) {
            System.out.println(YELLOW + "Cancelled: Returning to main menu." + RESET);
        } catch (PlaneManagementException | IllegalStateException e) {
            System.out.println(RED + "\nError: " + e.getMessage() + RESET);
        }
    }

    private static void checkInFlow() {
        printFlowHeader("Passenger Check-in");
        try {
            String bookingId = readRequiredString("Enter Booking ID for check-in: ", "Booking ID").toUpperCase();
            // Verify and check in
            service.checkInPassenger(bookingId);
            Booking booking = service.getBooking(bookingId);
            System.out.println(GREEN + "\nSuccess: Passenger " + booking.getPassenger().getName() + 
                " checked in for seat " + booking.getSeatNumber() + " on flight " + booking.getFlight().getFlightNumber() + "!" + RESET);
        } catch (BackException e) {
            System.out.println(YELLOW + "Cancelled: Returning to main menu." + RESET);
        } catch (PlaneManagementException | IllegalStateException e) {
            System.out.println(RED + "\nError: " + e.getMessage() + RESET);
        }
    }

    private static void updateFlightStatusFlow() {
        printFlowHeader("Update Flight Status");
        try {
            String flightNum = readRequiredString("Enter Flight Number to update: ", "Flight Number").toUpperCase();
            Flight flight = service.getFlight(flightNum);
            System.out.println("Current status of flight " + flightNum + " is: " + flight.getStatus());

            System.out.println("Enter new Flight Status (SCHEDULED, ACTIVE, COMPLETED, CANCELLED): ");
            FlightStatus newStatus = readFlightStatus("  New Status: ");

            service.updateFlightStatus(flightNum, newStatus);
            System.out.println(GREEN + "\nSuccess: Flight " + flightNum + " status updated to " + newStatus + "!" + RESET);
            if (newStatus == FlightStatus.CANCELLED) {
                System.out.println(YELLOW + "Note: All active bookings for this flight have been automatically CANCELLED." + RESET);
            }
        } catch (BackException e) {
            System.out.println(YELLOW + "Cancelled: Returning to main menu." + RESET);
        } catch (PlaneManagementException e) {
            System.out.println(RED + "\nError: " + e.getMessage() + RESET);
        }
    }

    private static void searchFlightsFlow() {
        printFlowHeader("Search Flights by Route");
        try {
            String origin = readRequiredString("Enter Origin Airport (e.g. JFK): ", "Origin").toUpperCase();
            String dest = readRequiredString("Enter Destination Airport (e.g. LHR): ", "Destination").toUpperCase();

            List<Flight> flights = service.searchFlightsByRoute(origin, dest);
            System.out.println("\nSearch Results (Origin: " + origin + ", Destination: " + dest + "):");
            
            String[] headers = {"Flight No", "Origin -> Dest", "Departure Time", "Arrival Time", "Status", "Aircraft (Model)", "Seats Free (E/B/F)"};
            List<String[]> rows = new ArrayList<>();
            for (Flight f : flights) {
                Aircraft plane = f.getAircraft();
                int freeE = plane.getEconomyCapacity() - f.getActiveCountForClass(SeatClass.ECONOMY);
                int freeB = plane.getBusinessCapacity() - f.getActiveCountForClass(SeatClass.BUSINESS);
                int freeF = plane.getFirstClassCapacity() - f.getActiveCountForClass(SeatClass.FIRST);
                String seatsFreeStr = String.format("%d/%d/%d", freeE, freeB, freeF);

                rows.add(new String[]{
                        f.getFlightNumber(),
                        f.getOrigin() + " -> " + f.getDestination(),
                        f.getDepartureTime().format(DATE_TIME_FORMATTER),
                        f.getArrivalTime().format(DATE_TIME_FORMATTER),
                        f.getStatus().toString(),
                        plane.getModel(),
                        seatsFreeStr
                });
            }
            TablePrinter.printTable(headers, rows);
        } catch (BackException e) {
            System.out.println(YELLOW + "Cancelled: Returning to main menu." + RESET);
        }
    }

    private static void viewRecordsFlow() {
        while (true) {
            System.out.println("\n" + CYAN + BOLD + "=========================================================");
            System.out.println("                    VIEW SYSTEM RECORDS                  ");
            System.out.println("=========================================================" + RESET);
            System.out.println("  1. View All Aircrafts");
            System.out.println("  2. View All Scheduled Flights");
            System.out.println("  3. View All Registered Passengers");
            System.out.println("  4. View All Bookings");
            System.out.println("  0. Back to Main Menu");
            System.out.println("=========================================================");
            System.out.print("Select an option (0-4): ");

            String choiceStr = scanner.nextLine().trim();
            if (choiceStr.isEmpty()) continue;

            if (choiceStr.equals("0") || choiceStr.equalsIgnoreCase("back") || choiceStr.equalsIgnoreCase("cancel")) {
                break;
            }

            try {
                int choice = Integer.parseInt(choiceStr);
                switch (choice) {
                    case 1:
                        viewAircrafts();
                        break;
                    case 2:
                        viewFlights();
                        break;
                    case 3:
                        viewPassengers();
                        break;
                    case 4:
                        viewBookings();
                        break;
                    default:
                        System.out.println(RED + "Invalid option. Please enter a number between 0 and 4." + RESET);
                }
            } catch (NumberFormatException e) {
                System.out.println(RED + "Invalid input. Please enter a valid number." + RESET);
            }
        }
    }

    private static void viewAircrafts() {
        System.out.println("\n--- Registered Aircrafts ---");
        List<Aircraft> list = service.getAircrafts();
        String[] headers = {"Aircraft ID", "Model Name", "Economy Capacity", "Business Capacity", "First Class Capacity", "Total Capacity"};
        List<String[]> rows = new ArrayList<>();
        for (Aircraft a : list) {
            rows.add(new String[]{
                    a.getId(),
                    a.getModel(),
                    String.valueOf(a.getEconomyCapacity()),
                    String.valueOf(a.getBusinessCapacity()),
                    String.valueOf(a.getFirstClassCapacity()),
                    String.valueOf(a.getTotalCapacity())
            });
        }
        TablePrinter.printTable(headers, rows);
    }

    private static void viewFlights() {
        System.out.println("\n--- Scheduled Flights ---");
        List<Flight> list = service.getFlights();
        String[] headers = {"Flight No", "Origin -> Dest", "Departure Time", "Arrival Time", "Status", "Aircraft Model", "Booked Seats (E/B/F)"};
        List<String[]> rows = new ArrayList<>();
        for (Flight f : list) {
            Aircraft plane = f.getAircraft();
            String bookedStr = String.format("%d/%d/%d", 
                f.getActiveCountForClass(SeatClass.ECONOMY),
                f.getActiveCountForClass(SeatClass.BUSINESS),
                f.getActiveCountForClass(SeatClass.FIRST)
            );
            rows.add(new String[]{
                    f.getFlightNumber(),
                    f.getOrigin() + " -> " + f.getDestination(),
                    f.getDepartureTime().format(DATE_TIME_FORMATTER),
                    f.getArrivalTime().format(DATE_TIME_FORMATTER),
                    f.getStatus().toString(),
                    plane.getModel() + " (" + plane.getId() + ")",
                    bookedStr
            });
        }
        TablePrinter.printTable(headers, rows);
    }

    private static void viewPassengers() {
        System.out.println("\n--- Registered Passengers ---");
        List<Passenger> list = service.getPassengers();
        String[] headers = {"Name", "Passport Number", "Email Address", "Phone Number"};
        List<String[]> rows = new ArrayList<>();
        for (Passenger p : list) {
            rows.add(new String[]{
                    p.getName(),
                    p.getPassportNumber(),
                    p.getEmail(),
                    p.getPhone()
            });
        }
        TablePrinter.printTable(headers, rows);
    }

    private static void viewBookings() {
        System.out.println("\n--- All System Bookings ---");
        List<Booking> list = service.getBookings();
        String[] headers = {"Booking ID", "Passenger Name", "Passport", "Flight No", "Seat Class", "Seat No", "Booking Status"};
        List<String[]> rows = new ArrayList<>();
        for (Booking b : list) {
            rows.add(new String[]{
                    b.getBookingId(),
                    b.getPassenger().getName(),
                    b.getPassenger().getPassportNumber(),
                    b.getFlight().getFlightNumber(),
                    b.getSeatClass().toString(),
                    b.getSeatNumber(),
                    b.getStatus().toString()
            });
        }
        TablePrinter.printTable(headers, rows);
    }

    // ==========================================
    // UTILITY TABLE FORMATTER
    // ==========================================
    private static class TablePrinter {
        public static void printTable(String[] headers, List<String[]> rows) {
            if (headers == null || headers.length == 0) return;
            int cols = headers.length;
            int[] widths = new int[cols];
            for (int i = 0; i < cols; i++) {
                widths[i] = headers[i].length();
            }
            for (String[] row : rows) {
                for (int i = 0; i < cols && i < row.length; i++) {
                    if (row[i] != null) {
                        widths[i] = Math.max(widths[i], row[i].length());
                    }
                }
            }

            // Print top border
            printBorder(widths);
            // Print headers
            printRow(headers, widths, true);
            // Print separator
            printBorder(widths);
            // Print rows
            if (rows.isEmpty()) {
                String[] empty = new String[cols];
                empty[0] = "(No records found)";
                for (int i = 1; i < cols; i++) empty[i] = "";
                printRow(empty, widths, false);
            } else {
                for (String[] row : rows) {
                    printRow(row, widths, false);
                }
            }
            // Print bottom border
            printBorder(widths);
        }

        private static void printBorder(int[] widths) {
            StringBuilder sb = new StringBuilder("+");
            for (int w : widths) {
                sb.append("-".repeat(w + 2)).append("+");
            }
            System.out.println(sb.toString());
        }

        private static void printRow(String[] row, int[] widths, boolean isHeader) {
            StringBuilder sb = new StringBuilder("|");
            for (int i = 0; i < widths.length; i++) {
                String val = i < row.length && row[i] != null ? row[i] : "";
                String content = String.format(" %-" + widths[i] + "s |", val);
                if (isHeader) {
                    content = CYAN + BOLD + content + RESET;
                }
                sb.append(content);
            }
            System.out.println(sb.toString());
        }
    }
}
