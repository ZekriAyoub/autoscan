package scanmycar.model.repository;

import scanmycar.model.dto.OwnerDto;
import scanmycar.model.repository.utilRepository.RepositoryException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository responsible for managing {@link OwnerDto} entities.
 * Allows access to the "owner" database table.
 */
public class OwnerRepository extends AbstractRepository<OwnerDto, Integer> {

    @Override
    protected String getTableName() {
        return "owner";
    }

    @Override
    protected OwnerDto mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new OwnerDto(
                rs.getInt("oId"),
                rs.getString("fullName"),
                rs.getString("address"),
                rs.getString("email")
        );
    }

    @Override
    protected PreparedStatement prepareInsertStatement(OwnerDto owner) throws SQLException {
        String sql = "INSERT INTO owner(fullName, address, email) VALUES (?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        stmt.setString(1, owner.fullName());
        stmt.setString(2, owner.address());
        stmt.setString(3, owner.getEmail());
        return stmt;
    }

    @Override
    protected PreparedStatement prepareUpdateStatement(OwnerDto owner) throws SQLException {
        String sql = "UPDATE owner SET fullName = ?, address = ?, email = ? WHERE oId = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, owner.fullName());
        stmt.setString(2, owner.address());
        stmt.setString(3, owner.getEmail());
        stmt.setInt(4, owner.oId());
        return stmt;
    }

    @Override
    protected Integer getEntityId(OwnerDto owner) {
        return owner.oId();
    }

    /**
     * Retrieves all owner email addresses.
     *
     * @return A list containing all registered email addresses.
     * @throws RepositoryException in case of an SQL error.
     */
    public List<String> getAllEmails() {
        List<String> emails = new ArrayList<>();
        String sql = "SELECT email FROM Owner";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                emails.add(rs.getString("email"));
            }
        } catch (SQLException e) {
            throw new RepositoryException("Erreur lors de la récupération des mails", e);
        }
        return emails;
    }

    /**
     * Searches for an owner's ID based on their email address.
     *
     * @param email The email address to search for.
     * @return An Optional containing the owner's ID if found, otherwise Optional.empty().
     * @throws RepositoryException in case of an SQL error.
     */
    public Optional<Integer> findByEmail(String email) {
        String sql = "SELECT oId FROM " + getTableName() + " Where email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getInt("oId"));
                }
            }
        } catch (SQLException e ) {
            throw new RepositoryException("Selection impossible de l'email", e);
        }
        return Optional.empty();
    }
}
