package com.example.showtime.Utils;

import com.example.showtime.Reservation.Reservation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class ResponseJSON {
    private final Optional<String> message;
    private final Optional<String> intent, showExtra;
    private final Optional<Reservation> reservation;
    private final List<SuggestedQuestion> suggestedQuestions;

    public ResponseJSON(Optional<String> message, Optional<String> intent, Optional<String> showExtra, Optional<Reservation> reservation, List<SuggestedQuestion> suggestedQuestions) {
        this.message = message;
        this.intent = intent;
        this.showExtra = showExtra;
        this.reservation = reservation;
        this.suggestedQuestions = suggestedQuestions;
    }

    public Optional<String> getMessage() {
        return message;
    }

    public Optional<String> getIntent() {
        return intent;
    }

    public Optional<String> getShowExtraValue() {
        return showExtra;
    }

    public Optional<Reservation> getReservation() {
        return reservation;
    }

    public List<SuggestedQuestion> getSuggestedQuestions() {
        return suggestedQuestions;
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
            Optional<String> showExtra = optionalNonEmptyText(node, "showExtra");
            Optional<String> intent = optionalNonEmptyText(node, "intent");

            Optional<Reservation> reservation = Optional.empty();
            if (node.hasNonNull("reservation") && !node.get("reservation").isEmpty()) {
                String reservationJson = mapper.writeValueAsString(node.get("reservation"));
                reservation = Optional.of(Reservation.fromJson(reservationJson));
            }

            List<SuggestedQuestion> suggestedQuestions = new ArrayList<>();
            if (node.has("suggestedQuestions") && node.get("suggestedQuestions").isArray()) {
                for (JsonNode questionNode : node.get("suggestedQuestions")) {
                    if (questionNode.isTextual()) {
                        String question = questionNode.asText();
                        suggestedQuestions.add(new SuggestedQuestion(question));
                    }
                }
            }

            return new ResponseJSON(message, intent, showExtra, reservation, suggestedQuestions);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to deserialize JSON to ReservationJSONObject", e);
        }
    }
}
