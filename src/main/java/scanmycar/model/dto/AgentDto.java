package scanmycar.model.dto;

public record AgentDto(int aId, String lastName, String firstName, int phoneNumber) {
    public int getAId() {
        return aId;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }
}
