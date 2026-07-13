// Flight class holding details of flights
public class Flight {
    private final int id;
    private final String flightNumber;
    private final String origin;
    private final String destination;
    private final String departureTime;
    private final String arrivalTime;
    private final Aircraft aircraft;
    private String status; // "SCHEDULED", "BOARDING", "DEPARTED", "ARRIVED", "CANCELLED"

    // Track bookings
    private Booking[] bookings;
    private int bookingCount;

    // True when the seat is taken, False when seat is available.
    private boolean[] economySeatsTaken;
    private boolean[] businessSeatsTaken;
    private boolean[] firstClassSeatsTaken;

    // constructor
    public Flight(int id, String flightNumber, String origin, String destination,
                  String departureTime, String arrivalTime, Aircraft aircraft) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.aircraft = aircraft;
        this.status = "SCHEDULED";

        // Get the size of the array from total number of seats in the aircraft
        this.bookings = new Booking[aircraft.getTotalSeats()];
        this.bookingCount = 0;

        // Initialize tracking arrays based on specific capacities
        this.economySeatsTaken = new boolean[aircraft.getEconomySeats()];
        this.businessSeatsTaken = new boolean[aircraft.getBusinessSeats()];
        this.firstClassSeatsTaken = new boolean[aircraft.getFirstClassSeats()];
    }

    // Checks availability by counting false values in arrays
    public int getAvailableSeats(String seatClass) {
        boolean[] seats;
        if (seatClass.equalsIgnoreCase("ECONOMY")) {
            seats = economySeatsTaken;
        } else if (seatClass.equalsIgnoreCase("BUSINESS")) {
            seats = businessSeatsTaken;
        } else {
            seats = firstClassSeatsTaken;
        }
        int count = 0;
        for (boolean taken : seats) {
            if (!taken)
                count++;
        }
        return count;
    }

    // Finds the first free seat, flags it true to mark it booked, and returns the seat ID that booked now (e.g. E4)
    // When booking seats in the flight we choose the class and then system books the first available seat
    public String reserveNextSeat(String seatClass) {
        boolean[] seats;
        String prefix;
        if (seatClass.equalsIgnoreCase("ECONOMY")) {
            seats = economySeatsTaken;
            prefix = "E";
        } else if (seatClass.equalsIgnoreCase("BUSINESS")) {
            seats = businessSeatsTaken;
            prefix = "B";
        } else {
            seats = firstClassSeatsTaken;
            prefix = "F";
        }

        for (int i = 0; i < seats.length; i++) {
            if (!seats[i]) {
                seats[i] = true;
                return prefix + (i + 1);
            }
        }
        return null;
    }

    // Frees seat when a reservation is cancelled
    public void freeSeat(String seatClass, String seatNumber) {
        try {
            int seatIndex = Integer.parseInt(seatNumber.substring(1)) - 1;
            if (seatClass.equalsIgnoreCase("ECONOMY") && seatIndex >= 0 && seatIndex < economySeatsTaken.length) {
                economySeatsTaken[seatIndex] = false;
            } else if (seatClass.equalsIgnoreCase("BUSINESS") && seatIndex >= 0 && seatIndex < businessSeatsTaken.length) {
                businessSeatsTaken[seatIndex] = false;
            } else if (seatClass.equalsIgnoreCase("FIRST") && seatIndex >= 0 && seatIndex < firstClassSeatsTaken.length) {
                firstClassSeatsTaken[seatIndex] = false;
            }
        } catch (Exception e) {
            System.out.println("something went wrong");
        }

    }

    // Adds a booking
    public void addBooking(Booking booking) {
        if (bookingCount < bookings.length) {
            bookings[bookingCount++] = booking;
        }
    }

    // getters and setters
    public int getId() { return id; }
    public String getFlightNumber() { return flightNumber; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public String getDepartureTime() { return departureTime; }
    public String getArrivalTime() { return arrivalTime; }
    public Aircraft getAircraft() { return aircraft; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Booking[] getBookings() { return bookings; }
    public int getBookingCount() { return bookingCount; }

    // method to print the details of a booking
    public void displayFlight() {
        System.out.println("Flight ID: " + id +
                " | Number: " + flightNumber +
                " | Route: " + origin + " -> " + destination +
                " | Departure: " + departureTime +
                " | Arrival: " + arrivalTime +
                " | Status: " + status +
                " | Aircraft: " + aircraft.getModel() +
                " | Available Seats: [Economy: " + getAvailableSeats("ECONOMY") +
                ", Business: " + getAvailableSeats("BUSINESS") +
                ", First Class: " + getAvailableSeats("FIRST") + "]");
    }
}