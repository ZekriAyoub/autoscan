package scanmycar.model.repository;

import scanmycar.model.dto.InspectionDto;
import scanmycar.model.dto.LastState;
import scanmycar.model.repository.utilRepository.RepositoryException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/**
 * Repository responsible for persistence operations for {@link InspectionDto} entities.
 * Manages access to the "Inspection" table in the database.
 */
public class InspectionRepository extends AbstractRepository<InspectionDto, Integer> {
    @Override
    protected String getTableName() {
        return "Inspection";
    }

    @Override
    protected InspectionDto mapResultSetToEntity(ResultSet rs) throws SQLException {
        // Conversion de la date en java.sql.Date (assurant que la date dans la base est au format 'yyyy-mm-dd')
        String dateString = rs.getString("date");
        LocalDate date = LocalDate.parse(dateString);  // Conversion

        return new InspectionDto(
                rs.getInt("insId"),
                rs.getInt("agent"),
                date,
                LastState.fromDatabaseValue(rs.getString("state")),
                rs.getString("plate")
        );
    }

    @Override
    protected PreparedStatement prepareInsertStatement(InspectionDto inspection) throws SQLException {
        String sql = "INSERT INTO Inspection (agent, date, state, plate) " +
                "VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, inspection.agent());
        stmt.setString(2, inspection.date().toString());
        stmt.setString(3, inspection.state().toDatabaseValue());
        stmt.setString(4, inspection.plate());
        return stmt;
    }

    @Override
    protected PreparedStatement prepareUpdateStatement(InspectionDto inspection) throws SQLException {
        String sql = """
                UPDATE
                    Inspection
                SET
                    agent = ?, date = ?, state = ?, plate = ?
                WHERE
                    insId = ?
                """;
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, inspection.agent());
        stmt.setString(2, inspection.date().toString());
        stmt.setString(3, inspection.state().toDatabaseValue());
        stmt.setString(4, inspection.plate());
        stmt.setInt(5, inspection.insId());
        return stmt;
    }

    @Override
    protected Integer getEntityId(InspectionDto inspection) {
        return inspection.insId();
    }

    /**
     * Retrieves all inspections related to a given license plate.
     *
     * @param licensePlate The license plate to search for.
     * @return A list of inspections associated with this plate.
     * @throws RepositoryException If an error occurs during query execution.
     */
    public List<InspectionDto> findByPlate(String licensePlate) {
        List<InspectionDto> inspections = new ArrayList<>();
        String sql = "SELECT * FROM Inspection WHERE plate = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, licensePlate);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                inspections.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new RepositoryException("Erreur lors de la récupération des inspections pour le vId :", e);
        }
        return inspections;
    }
}