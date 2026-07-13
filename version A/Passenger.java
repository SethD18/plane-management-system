// Passenger Class holding passengers details
public class Passenger {
    private final int id;
    private final String name;
    private final String passportNumber;
    private final String email;
    private final String phone;

    // constructor
    public Passenger(int id, String name, String passportNumber, String email, String phone) {
        this.id = id;
        this.name = name;
        this.passportNumber = passportNumber;
        this.email = email;
        this.phone = phone;
    }

    // getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getPassportNumber() { return passportNumber; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }

    // method to print the details of a passenger
    public void displayPassenger() {
        System.out.println("Passenger ID: " + id +
                " | Name: " + name +
                " | Passport: " + passportNumber +
                " | Email: " + email +
                " | Phone: " + phone);
    }
}