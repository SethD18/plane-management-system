// Aircraft Class holding details of planes
public class Aircraft {
    private int id;
    private String model;
    private int economySeats;
    private int businessSeats;
    private int firstClassSeats;

    // constructor
    public Aircraft(int id, String model, int economySeats, int businessSeats, int firstClassSeats) {
        this.id = id;
        this.model = model;
        this.economySeats = economySeats;
        this.businessSeats = businessSeats;
        this.firstClassSeats = firstClassSeats;
    }

    // getters
    public int getId() { return id; }
    public String getModel() { return model; }
    public int getEconomySeats() { return economySeats; }
    public int getBusinessSeats() { return businessSeats; }
    public int getFirstClassSeats() { return firstClassSeats; }
}