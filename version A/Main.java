// Assessment 2 - Plane Management System (Version A)
// The basic menu 
import java.util.Scanner;

public class Main {

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
                case 0:  System.out.println("Exiting system. Goodbye!"); break;
                default: System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 0);
    }

    // Displays the standard menu to the user
    static void printMenu() {
        System.out.println("\n==========================================");
        System.out.println("    PLANE MANAGEMENT SYSTEM  (Version C)  ");
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
    static void addAircraft() {}
    static void viewAllAircraft() {}
    static void addFlight() {}
    static void viewAllFlights() {}
    static void searchFlights() {}
    static void addPassenger() {}
    static void bookSeat() {}
    static void cancelBooking() {}
    static void checkInPassenger() {}
    static void viewFlightBookings() {}
    static void updateFlightStatus() {}
}