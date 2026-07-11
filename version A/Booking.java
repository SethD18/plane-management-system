// Booking Class representing ticket reservations
public class Booking {
    private int id;
    private Flight flight;
    private Passenger passenger;
    private String seatClass; // "ECONOMY", "BUSINESS", "FIRST CLASS"
    private String seatNumber;
    private String status; // "CONFIRMED", "CANCELLED", "CHECKED_IN"
    private String bookingDate;

    // constructor
    public Booking(int id, Flight flight, Passenger passenger, String seatClass, String seatNumber, String bookingDate) {
        this.id = id;
        this.flight = flight;
        this.passenger = passenger;
        this.seatClass = seatClass;
        this.seatNumber = seatNumber;
        this.status = "CONFIRMED";
        this.bookingDate = bookingDate;
    }

    // getters and setters
    public int getId() { return id; }
    public Flight getFlight() { return flight; }
    public Passenger getPassenger() { return passenger; }
    public String getSeatClass() { return seatClass; }
    public String getSeatNumber() { return seatNumber; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getBookingDate() { return bookingDate; }

    // method to print the details of a booking
    public void displayBooking() {
        System.out.println("Booking ID: " + id +
                " | Flight: " + flight.getFlightNumber() +
                " | Passenger: " + passenger.getName() +
                " | Class: " + seatClass +
                " | Seat: " + seatNumber +
                " | Status: " + status +
                " | Date: " + bookingDate);
    }
}