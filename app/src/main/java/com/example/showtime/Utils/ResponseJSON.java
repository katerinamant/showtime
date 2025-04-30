package com.example.showtime.Utils;

import com.example.showtime.Reservation.Reservation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

public class ResponseJSON {
    private final Optional<String> message;
    private final Optional<String> intent;
    private final Optional<Reservation> reservation;

    public ResponseJSON(Optional<String> message, Optional<String> intent, Optional<Reservation> reservation) {
        this.message = message;
        this.intent = intent;
        this.reservation = reservation;
    }

    public Optional<String> getMessage() {
        return message;
    }

    public Optional<String> getIntent() {
        return intent;
    }

    public Optional<Reservation> getReservation() {
        return reservation;
    }

    private static Optional<String> optionalNonEmptyText(JsonNode node, String fieldName) {
        if (node.hasNonNull(fieldName)) {
            String value = node.get(fieldName).asText();
            if (value != null && !value.trim().isEmpty()) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    public static ResponseJSON fromJson(String json) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(json);

            Optional<String> message = optionalNonEmptyText(node, "message");
            Optional<String> intent = optionalNonEmptyText(node, "intent");

            Optional<Reservation> reservation = Optional.empty();
            if (node.hasNonNull("reservation") && !node.get("reservation").isEmpty()) {
                String reservationJson = mapper.writeValueAsString(node.get("reservation"));
                reservation = Optional.of(Reservation.fromJson(reservationJson));
            }

            return new ResponseJSON(message, intent, reservation);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to deserialize JSON to ReservationJSONObject", e);
        }
    }
}
