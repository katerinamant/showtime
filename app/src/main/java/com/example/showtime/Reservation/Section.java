package com.example.showtime.Reservation;

public enum Section {
    RED(40.0f),
    YELLOW(30.0f),
    BLUE(25.0f),
    GREEN(25.0f);

    private final float price;

    Section(float price) {
        this.price = price;
    }

    public float getPrice() {
        return price;
    }
}
