package scanmycar.model.dto;

public record VehicleDto(int vId, String licensePlate, String brand, String model,
                         Integer year, String color, FuelType fuelType, int ownerId,
                         LastState lastState) {
    public int getVId() {
        return vId;
    }
    public String getLicensePlate() {
        return licensePlate;
    }
    public String getBrand() {
        return brand;
    }
    public String getModel() {
        return model;
    }
    public Integer getYear() {
        return year;
    }
    public String getColor() {
        return color;
    }
    public String getFuelType() {
        return fuelType.toDatabaseValue();
    }
    public int getOwnerId() {
        return ownerId;
    }
    public String getLastState() {
        return lastState.toDatabaseValue();
    }
}
