package com.example.showtime.Reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Reservation {
    private final String reservationCode;
    private final String phoneNumber;
    private final String customerName;
    private Show show;
    private String date; // DD/MM/YYYY
    private boolean isForEarlyShowing;
    private int ticketNum;
    private Section section;

    public Reservation(String reservationCode, String phoneNumber, String customerName,
                       Show show, String date, boolean isForEarlyShowing, int ticketNum, Section section) {
        this.reservationCode = reservationCode;
        this.phoneNumber = phoneNumber;
        this.customerName = customerName;
        this.show = show;
        this.date = date;
        this.isForEarlyShowing = isForEarlyShowing;
        this.ticketNum = ticketNum;
        this.section = section;
    }

    public static Reservation fromJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(json);

            String reservationCode = node.get("reservationCode").asText();
            String phoneNumber = node.get("phoneNumber").asText();
            String customerName = node.get("customerName").asText();
            String showName = node.get("show").asText();
            String date = node.get("date").asText();
            String time = node.get("time").asText();
            int ticketNum = node.get("ticketNum").asInt();
            String sectionStr = node.get("section").asText();


            Section section = Section.valueOf(sectionStr.toUpperCase());

            Show show = showName.equalsIgnoreCase(Show.PETER_PAN.getName()) ? Show.PETER_PAN : Show.ROMEO_AND_JULIET;
            boolean isForEarlyShowing = show.getEarlyShowing().equalsIgnoreCase(time);

            return new Reservation(reservationCode, phoneNumber, customerName, show, date, isForEarlyShowing, ticketNum, section);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize JSON to Reservation", e);
        }
    }

    public String toJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();

            node.put("reservationCode", reservationCode);
            node.put("phoneNumber", phoneNumber);
            node.put("customerName", customerName);
            node.put("show", show.getName());
            node.put("date", date);
            node.put("time", isForEarlyShowing ? show.getEarlyShowing() : show.getLateShowing());
            node.put("ticketNum", ticketNum);
            node.put("section", section.toString().substring(0,1).toUpperCase() + section.toString().substring(1).toLowerCase());

            return mapper.writeValueAsString(node);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize Reservation to JSON", e);
        }
    }

    // Getters and Setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTicketNum() {
        return ticketNum;
    }

    public void setTicketNum(int ticketNum) {
        this.ticketNum = ticketNum;
    }

    public String getReservationCode() {
        return reservationCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public boolean isForEarlyShowing() {
        return isForEarlyShowing;
    }

    public void setIsForEarlyShowing(boolean isForEarlyShowing) {
        this.isForEarlyShowing = isForEarlyShowing;
    }

    public float getPricePerTicket() {
        return this.section.getPrice();
    }

    public float getTotalPrice() {
        return this.getPricePerTicket() * this.ticketNum;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getShowName() {
        return show.getName();
    }

    public void setShow(Show show) {
        this.show = show;
    }

    public String getTime() {
        return isForEarlyShowing ? show.getEarlyShowing() : show.getLateShowing();
    }
}
