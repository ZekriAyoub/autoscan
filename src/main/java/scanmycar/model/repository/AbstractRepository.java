package scanmycar.model.repository;

import scanmycar.model.repository.utilRepository.RepositoryException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Generic abstract class for handling basic CRUD operations on a database.
 *
 * @param <T>  The type of the managed entity.
 * @param <ID> The type of the entity identifier.
 */
public abstract class AbstractRepository<T, ID> implements GenericRepository<T, ID> {
    protected Connection connection;

    public AbstractRepository() {
        this.connection = ConnectionManager.getConnection();
    }

    protected abstract String getTableName();
    /**
     * Converts a SQL ResultSet into an entity.
     *
     * @param rs The ResultSet to map.
     * @return The corresponding entity.
     * @throws SQLException If an error occurs while accessing the columns.
     */
    protected abstract T mapResultSetToEntity(ResultSet rs) throws SQLException;
    /**
     * Prepares a SQL insert statement from an entity.
     *
     * @param entity The entity to insert.
     * @return A PreparedStatement ready to be executed.
     * @throws SQLException In case of a SQL error.
     */
    protected abstract PreparedStatement prepareInsertStatement(T entity) throws SQLException;

    /**
     * Prepares a SQL update statement from an entity.
     *
     * @param entity The entity to update.
     * @return A PreparedStatement ready to be executed.
     * @throws SQLException In case of a SQL error.
     */
    protected abstract PreparedStatement prepareUpdateStatement(T entity) throws SQLException;

    /**
     * Returns the identifier of the entity.
     *
     * @param entity The entity.
     * @return The identifier of the entity.
     */
    protected abstract ID getEntityId(T entity);

    @Override
    public Optional<T> findById(ID id) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE oId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Selection impossible", e);
        }
        return Optional.empty();
    }

    @Override
    public List<T> findAll() {
        List<T> entities = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                entities.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new RepositoryException("Selection impossible", e);
        }
        return entities;
    }

    /**
     * Saves an entity: inserts it if it doesn't exist, updates it otherwise.
     *
     * @param entity The entity to save.
     * @return The identifier of the saved entity, or null if the operation failed.
     */
    @Override
    public ID save(T entity) {
        String sql = "SELECT COUNT(*) FROM " + getTableName() + " WHERE oId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, getEntityId(entity));
            try (ResultSet rs = stmt.executeQuery()) {
                boolean found = rs.next() && rs.getInt(1) > 0;
                if (found) {
                    return (this.update(entity) > 0) ? getEntityId(entity) : null;
                } else {
                    return this.insert(entity);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Recherche impossible", e);
        }
    }

    private ID insert(T entity) {
        try (PreparedStatement stmt = prepareInsertStatement(entity)) {
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return (ID) rs.getObject(1);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Insertion impossible", e);
        }
        return null;
    }

    private int update(T entity) {
        try (PreparedStatement stmt = prepareUpdateStatement(entity)) {
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Mise à jour impossible", e);
        }
    }

    @Override
    public void deleteById(ID id) {
        String sql = "DELETE FROM " + getTableName() + " WHERE oId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Suppression impossible", e);
        }
    }

    /**
     * Closes the database connection.
     */
    @Override
    public void close() {
        ConnectionManager.close();
    }
}
