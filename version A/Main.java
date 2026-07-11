// Assessment 2 - Plane Management System (Version A)
import java.util.Scanner;

public class Main {

    static Aircraft[] aircrafts = new Aircraft[50];
    static Passenger[] passengers = new Passenger[200];
    static Flight[] flights = new Flight[100];
    static Booking[] bookings = new Booking[250];

    static int aircraftCount = 0;
    static int passengerCount = 0;
    static int flightCount = 0;
    static int bookingCount = 0;



    // Auto-increment counters for IDs
    static int aircraftIdCounter = 1;
    static int passengerIdCounter = 1;
    static int flightIdCounter = 1;
    static int bookingIdCounter = 1;

    // Scanner to read inputs
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        loadSampleData();
        int choice;
        do {
            printMenu();
            choice = scanner.nextInt();
            scanner.nextLine(); 

            switch (choice) {
                case 1:  addAircraft();         break;
                case 2:  viewAllAircraft();     break;
                case 3:  addFlight();           break;
                case 4:  viewAllFlights();      break;
                case 5:  searchFlights();       break;
                case 6:  addPassenger();        break;
                case 7:  bookSeat();            break;
                case 8:  cancelBooking();       break;
                case 9:  checkInPassenger();    break;
                case 10: viewFlightBookings();  break;
                case 11: updateFlightStatus();  break;
                case 0:  System.out.println("Exiting system."); break;
                default: System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 0);
    }

    // Displays the menu to the user
    static void printMenu() {
        System.out.println("\n==========================================");
        System.out.println("    PLANE MANAGEMENT SYSTEM  (Version A)  ");
        System.out.println("==========================================");
        System.out.println(" 1.  Add Aircraft");
        System.out.println(" 2.  View All Aircraft");
        System.out.println(" 3.  Add Flight");
        System.out.println(" 4.  View All Flights");
        System.out.println(" 5.  Search Flights");
        System.out.println(" 6.  Add Passenger");
        System.out.println(" 7.  Book Seat");
        System.out.println(" 8.  Cancel Booking");
        System.out.println(" 9.  Check-In Passenger");
        System.out.println(" 10. View Bookings for a Flight");
        System.out.println(" 11. Update Flight Status");
        System.out.println(" 0.  Exit");
        System.out.print("Enter choice: ");
    }

    // Adding sample data for testing purposes
    static void loadSampleData() {
        Aircraft a1 = new Aircraft(aircraftIdCounter++, "A1 739",    120, 30, 10);
        Aircraft a2 = new Aircraft(aircraftIdCounter++, "A1 320",   150, 20, 0);
        aircrafts[aircraftCount++] = a1;
        aircrafts[aircraftCount++] = a2;

        Flight f1 = new Flight(flightIdCounter++, "F101", "Colombo", "Dubai", "2026-07-10 08:00", "2026-07-10 12:00", a1);
        flights[flightCount++] = f1;

        Passenger p1 = new Passenger(passengerIdCounter++, "Seth",   "P1234", "seth@email.com",  "0771234567");
        Passenger p2 = new Passenger(passengerIdCounter++, "Chandani", "P9876", "chandani@email.com",  "0779876543");
        passengers[passengerCount++] = p1;
        passengers[passengerCount++] = p2;

        System.out.println("Sample data loaded.");
    }

    // Methods for main operations

    // Adding a new aircraft to the system
    static void addAircraft() {
        if (aircraftCount >= aircrafts.length) {
            System.out.println("Error: Aircraft storage is full.");
            return;
        }
        System.out.print("Model name: ");
        String model = scanner.nextLine();
        System.out.print("Economy seats: ");
        int economy = scanner.nextInt();
        System.out.print("Business seats: ");
        int business = scanner.nextInt();
        System.out.print("First class seats: ");
        int first = scanner.nextInt();
        scanner.nextLine();
        Aircraft a = new Aircraft(aircraftIdCounter++, model, economy, business, first);
        aircrafts[aircraftCount++] = a;
        System.out.print("Added aircraft: ");
        a.displayAircraft();
    }

    // Prints aircraft details
    static void viewAllAircraft() {
        if (aircraftCount == 0) {
            System.out.println("No aircraft found.");
            return;
        }
        System.out.println("\n--- Aircrafts ---");
        for (int i = 0; i < aircraftCount; i++) {
            aircrafts[i].displayAircraft();
        }
    }

    // Adding a new flight to the system
    static void addFlight() {
        if (flightCount >= flights.length) {
            System.out.println("Error: Flight storage is full.");
            return;
        }
        if (aircraftCount == 0) {
            System.out.println("No aircraft available.");
            return;
        }
        System.out.print("Flight number: ");
        String flightNumber = scanner.nextLine();
        System.out.print("Origin: ");
        String origin = scanner.nextLine();
        System.out.print("Destination: ");
        String destination = scanner.nextLine();

        System.out.print("Departure (yyyy-MM-dd HH:mm): ");
        String departure = scanner.nextLine();
        System.out.print("Arrival   (yyyy-MM-dd HH:mm): ");
        String arrival = scanner.nextLine();

        viewAllAircraft();
        System.out.print("Aircraft ID: ");
        int aircraftId = scanner.nextInt();
        scanner.nextLine();

        Aircraft selected = null;
        for (int i = 0; i < aircraftCount; i++) {
            if (aircrafts[i].getId() == aircraftId) {
                selected = aircrafts[i];
                break;
            }
        }
        if (selected == null) {
            System.out.println("Aircraft not found.");
            return;
        }

        Flight flight = new Flight(flightIdCounter++, flightNumber, origin, destination, departure, arrival, selected);
        flights[flightCount++] = flight;
        System.out.println("Successfully added flight!");
    }

    // Prints all the flights
    static void viewAllFlights() {
        if (flightCount == 0) {
            System.out.println("No flights found.");
            return;
        }
        System.out.println("\n--- Flights ---");
        for (int i = 0; i < flightCount; i++) {
            flights[i].displayFlight();
        }
    }

    // Search the flights by origin and destination
    static void searchFlights() {
        System.out.print("Origin: ");
        String origin = scanner.nextLine();
        System.out.print("Destination: ");
        String destination = scanner.nextLine();

        System.out.println("\n--- Results ---");
        for (int i = 0; i < flightCount; i++) {
            if (flights[i].getOrigin().equalsIgnoreCase(origin) && flights[i].getDestination().equalsIgnoreCase(destination)) {
                flights[i].displayFlight();
            }
        }
    }

    // Register a new passenger to the system
    static void addPassenger() {
        if (passengerCount >= passengers.length) {
            System.out.println("Error: Passenger storage is full.");
            return;
        }
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Passport number: ");
        String passport = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Phone: ");
        String phone = scanner.nextLine();
        Passenger p = new Passenger(passengerIdCounter++, name, passport, email, phone);
        passengers[passengerCount++] = p;
        System.out.print("Added passenger: ");
        p.displayPassenger();
    }
    static void bookSeat() {}
    static void cancelBooking() {}
    static void checkInPassenger() {}
    static void viewFlightBookings() {}
    static void updateFlightStatus() {}
}