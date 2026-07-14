package com.planemanagement.model;

import java.util.Objects;

public class Passenger {
    private final String name;
    private final String passportNumber;
    private final String email;
    private final String phone;

    public Passenger(String name, String passportNumber, String email, String phone) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Passenger name cannot be null or empty.");
        }
        if (passportNumber == null || passportNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Passport number cannot be null or empty.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty.");
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty.");
        }
        this.name = name.trim();
        this.passportNumber = passportNumber.trim().toUpperCase();
        this.email = email.trim().toLowerCase();
        this.phone = phone.trim();
    }

    public String getName() {
        return name;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Passenger passenger = (Passenger) o;
        return Objects.equals(passportNumber, passenger.passportNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(passportNumber);
    }

    @Override
    public String toString() {
        return String.format("%s (Passport: %s, Email: %s, Phone: %s)", name, passportNumber, email, phone);
    }
}
