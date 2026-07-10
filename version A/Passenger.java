// Passenger Class holding passengers details
public class Passenger {
    private int id;
    private String name;
    private String passportNumber;
    private String email;
    private String phone;

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
}