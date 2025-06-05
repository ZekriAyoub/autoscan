package scanmycar.model.dto;

public record OwnerDto(int oId, String fullName, String address, String email) {
    public int getOId() {
        return oId;
    }
    public String getFullName() {
        return fullName;
    }
    public String getAddress() {
        return address;
    }
    public String getEmail() {return email;}
}
