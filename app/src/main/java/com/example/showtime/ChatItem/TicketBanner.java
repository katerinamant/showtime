package com.example.showtime.ChatItem;

import com.example.showtime.Reservation.Reservation;

public class TicketBanner extends ChatItem {
    Reservation reservation;

    public TicketBanner(Reservation reservation) {
        this.reservation = reservation;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }
}
