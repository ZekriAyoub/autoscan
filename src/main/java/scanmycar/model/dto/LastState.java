package scanmycar.model.dto;

/**
 * Enumeration representing the final state of a vehicle after inspection.
 * Each value is associated with a text representation used in the database.
 */
public enum LastState {
    VALIDE("Valide"),
    DEFAVORABLE("Défavorable"),
    REFUSE("Refusé"),
    NULL("N/A");

    private final String databaseValue;

    LastState(String databaseValue) {
        this.databaseValue = databaseValue;
    }

    public String toDatabaseValue() {
        return databaseValue;
    }

    /**
     * Converts a text value from the database to an instance of {@link LastState}.
     *
     * @param value The value from the database.
     * @return The corresponding enumeration, or {@link #NULL} if the value is null.
     * @throws IllegalArgumentException If the value doesn't correspond to a known state.
     */
    public static LastState fromDatabaseValue(String value) {
        if (value == null) {
            return NULL;
        }

        for (LastState state : values()) {
            if (state.databaseValue.equalsIgnoreCase(value)) { // Ignore la casse
                return state;
            }
        }
        throw new IllegalArgumentException("Valeur inconnue pour LastState : " + value);
    }
}
