// Assessment 2 - Plane Management System (Version A)
// The basic menu 
import java.util.Scanner;

public class Main {

    static Aircraft[] aircrafts = new Aircraft[50];
    static Passenger[] passengers = new Passenger[100];

    static int aircraftCount = 0;
    static int passengerCount = 0;



    // Auto-increment counters for IDs
    static int aircraftIdCounter = 1;
    static int passengerIdCounter = 1;

    // Scanner to read inputs
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
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

    // Methods

    //Adding a new aircraft to the system
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
        System.out.println("Added: ID " + a.getId() + ", Model " + a.getModel());
    }

    // Prints aircraft details
    static void viewAllAircraft() {
        if (aircraftCount == 0) {
            System.out.println("No aircraft found.");
            return;
        }
        System.out.println("\n--- Aircrafts ---");
        for (int i = 0; i < aircraftCount; i++) {
            System.out.println("ID: " + aircrafts[i].getId() + " | Model: " + aircrafts[i].getModel() +
                    " | Economy: " + aircrafts[i].getEconomySeats()+ " | Business: " + aircrafts[i].getBusinessSeats()+
                    " | First class: " + aircrafts[i].getFirstClassSeats());
        }
    }

    static void addFlight() {}
    static void viewAllFlights() {}
    static void searchFlights() {}

    //Register a new passenger to the system
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
        System.out.println("Added passenger: ID " + p.getId() + ", Name " + p.getName());
    }
    static void bookSeat() {}
    static void cancelBooking() {}
    static void checkInPassenger() {}
    static void viewFlightBookings() {}
    static void updateFlightStatus() {}
}