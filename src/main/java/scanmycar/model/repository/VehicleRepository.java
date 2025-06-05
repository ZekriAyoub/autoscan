package scanmycar.model.repository;

import scanmycar.model.dto.FuelType;
import scanmycar.model.dto.LastState;
import scanmycar.model.dto.VehicleDto;
import scanmycar.model.repository.utilRepository.RepositoryException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository responsible for managing {@link VehicleDto} entities.
 * Provides persistence operations related to the "vehicle" table.
 */
public class VehicleRepository extends AbstractRepository<VehicleDto, Integer> {
    @Override
    protected String getTableName() {
        return "vehicle";
    }

    @Override
    protected VehicleDto mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new VehicleDto(
                rs.getInt("vId"),
                rs.getString("licensePlate"),
                rs.getString("brand"),
                rs.getString("model"),
                rs.getInt("year"),
                rs.getString("color"),
                FuelType.fromDatabaseValue(rs.getString("fuelType")), // It's an enum type
                rs.getInt("ownerId"),
                LastState.fromDatabaseValue(rs.getString("lastState")) // It's an enum type
        );
    }

    @Override
    protected PreparedStatement prepareInsertStatement(VehicleDto vehicle) throws SQLException {
        String sql = "INSERT INTO vehicle(licensePlate, brand, model," +
                " year, color, fuelType, ownerId, lastState)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        stmt.setString(1, vehicle.licensePlate());
        stmt.setString(2, vehicle.brand());
        stmt.setString(3, vehicle.model());
        stmt.setInt(4, vehicle.year());
        stmt.setString(5, vehicle.color());
        stmt.setString(6, vehicle.fuelType().toDatabaseValue());
        stmt.setInt(7, vehicle.ownerId());
        // Si lastState est null, ne pas l'ajouter à la requête
        if (vehicle.lastState() != null) {
            stmt.setString(8, vehicle.lastState().toDatabaseValue());
        } else {
            stmt.setNull(8, java.sql.Types.VARCHAR); // Utilisation de setNull pour gérer le cas null
        }
        return stmt;
    }

    @Override
    protected PreparedStatement prepareUpdateStatement(VehicleDto vehicle) throws SQLException {
        String sql = "UPDATE vehicle SET licensePlate = ?, brand = ?, " +
                "model = ?, year = ?, color = ?, fuelType = ?, " +
                "ownerId = ?, lastState = ?" +
                " WHERE vId = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, vehicle.licensePlate());
        stmt.setString(2, vehicle.brand());
        stmt.setString(3, vehicle.model());
        stmt.setInt(4, vehicle.year());
        stmt.setString(5, vehicle.color());
        stmt.setString(6, vehicle.fuelType().toDatabaseValue());
        stmt.setInt(7, vehicle.ownerId());
        stmt.setString(8, vehicle.lastState().toDatabaseValue());
        stmt.setInt(9, vehicle.vId());
        return stmt;
    }

    @Override
    protected Integer getEntityId(VehicleDto vehicle) {
        return vehicle.vId();
    }

    // Récuperer toutes les plaques
    public List<String> findAllPlate() {
        List<String> licensesPlates = new ArrayList<>();
        String sql = "SELECT licensePlate FROM vehicle";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                licensesPlates.add(rs.getString("licensePlate"));
            }
        } catch (SQLException e) {
            throw new RepositoryException("Erreur lors de la récupération des plaques: ", e);
        }
        return licensesPlates;
    }

    /**
     * Searches for a vehicle based on its license plate.
     *
     * @param plate The plate to search for.
     * @return An Optional containing the vehicle if found, otherwise Optional.empty().
     * @throws RepositoryException in case of an SQL error.
     */
    public Optional<VehicleDto> findByPlate(String plate) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE licensePlate = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, plate);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Selection impossible du véhicule", e);
        }
        return Optional.empty();
    }
}
