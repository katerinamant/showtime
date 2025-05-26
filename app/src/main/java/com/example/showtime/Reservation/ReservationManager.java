package com.example.showtime.Reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.HashMap;

public class ReservationManager {
    private final HashMap<String, Reservation> reservations = new HashMap<>();

    public HashMap<String, Reservation> getAllReservations() {
        return reservations;
    }

    public Reservation getReservation(String reservationCode) {
        return reservations.get(reservationCode);
    }

    public void addReservation(String reservationCode, Reservation reservation) {
        reservations.put(reservationCode, reservation);
    }

    public void updateReservation(String reservationCode, Reservation reservation) {
        reservations.replace(reservationCode, reservation);
    }

    public void deleteReservation(String reservationCode) {
        reservations.remove(reservationCode);
    }

    public String toJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ArrayNode arrayNode = mapper.createArrayNode();

            for (Reservation reservation : reservations.values()) {
                arrayNode.add(mapper.readTree(reservation.toJson()));
            }

            return mapper.writeValueAsString(arrayNode);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize reservations to JSON array", e);
        }
    }
}
