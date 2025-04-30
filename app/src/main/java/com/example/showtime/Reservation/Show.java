package com.example.showtime.Reservation;

public enum Show {
    PETER_PAN("Peter Pan", "5:00PM", "8:00PM"),
    ROMEO_AND_JULIET("Romeo and Juliet", "6:00PM", "9:00PM");

    private final String name;
    private final String earlyShowing, lateShowing;

    Show(String name, String earlyShowing, String lateShowing) {
        this.name = name;
        this.earlyShowing = earlyShowing;
        this.lateShowing = lateShowing;
    }

    public String getName() {
        return name;
    }

    public String getEarlyShowing() {
        return earlyShowing;
    }

    public String getLateShowing() {
        return lateShowing;
    }
}
