package com.planemanagement.model;

public class Aircraft {
    private final String id;
    private final String model;
    private final int economyCapacity;
    private final int businessCapacity;
    private final int firstClassCapacity;

    public Aircraft(String id, String model, int economyCapacity, int businessCapacity, int firstClassCapacity) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Aircraft ID cannot be null or empty.");
        }
        if (model == null || model.trim().isEmpty()) {
            throw new IllegalArgumentException("Aircraft model cannot be null or empty.");
        }
        if (economyCapacity < 0 || businessCapacity < 0 || firstClassCapacity < 0) {
            throw new IllegalArgumentException("Seat capacities cannot be negative.");
        }
        this.id = id.trim().toUpperCase();
        this.model = model.trim();
        this.economyCapacity = economyCapacity;
        this.businessCapacity = businessCapacity;
        this.firstClassCapacity = firstClassCapacity;
    }

    public String getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public int getEconomyCapacity() {
        return economyCapacity;
    }

    public int getBusinessCapacity() {
        return businessCapacity;
    }

    public int getFirstClassCapacity() {
        return firstClassCapacity;
    }

    public int getTotalCapacity() {
        return economyCapacity + businessCapacity + firstClassCapacity;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) [Capacities: Econ=%d, Biz=%d, First=%d]", 
            id, model, economyCapacity, businessCapacity, firstClassCapacity);
    }
}
