package com.planemanagement.model.enums;

public enum SeatClass {
    ECONOMY("E"),
    BUSINESS("B"),
    FIRST("F");

    private final String prefix;

    SeatClass(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
