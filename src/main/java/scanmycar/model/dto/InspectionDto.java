package scanmycar.model.dto;

import java.time.LocalDate;


public record InspectionDto(int insId, int agent, LocalDate date,
                            LastState state, String plate) {
    public int getInsId() {
        return insId;
    }

    public int getAgent() {
        return agent;
    }

    public LocalDate getDate() {
        return date;
    }

    public LastState getState() {
        return state;
    }

    public String getPlate() {
        return plate;
    }
}
