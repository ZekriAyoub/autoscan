package scanmycar.model.dto;

/**
 * Enum representing different types of vehicle fuels.
 */
public enum FuelType {
    ESSENCE("Essence"),
    DIESEL("Diesel"),
    ELECTRIQUE("Électrique"),
    HYBRIDE("Hybride"),
    GPL("GPL"),
    HYDROGENE("Hydrogène");

    private final String databaseValue;

    FuelType(String databaseValue) {
        this.databaseValue = databaseValue;
    }

    public String toDatabaseValue() {
        return databaseValue;
    }


    /**
     * Returns the corresponding FuelType enum from a database string.
     * Case-insensitive matching.
     *
     * @param value The database value to convert.
     * @return The matching FuelType enum.
     * @throws IllegalArgumentException If the value does not match any enum constant.
     */
    public static FuelType fromDatabaseValue(String value) {
        for (FuelType type : values()) {
            if (type.databaseValue.equalsIgnoreCase(value)) { // Ignore la casse
                return type;
            }
        }
        throw new IllegalArgumentException("Valeur inconnue pour FuelType : " + value);
    }
}
