package scanmycar.model.repository;

import scanmycar.model.dto.AgentDto;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Repository for managing {@link AgentDto} entities.
 * Implements CRUD operations specific to the "Agent" table.
 */
public class AgentRepository extends AbstractRepository<AgentDto, Integer> {
    @Override
    protected String getTableName() {
        return "Agent";
    }

    @Override
    protected AgentDto mapResultSetToEntity(ResultSet rs) throws SQLException {
        return new AgentDto(
                rs.getInt("aId"),
                rs.getString("lastName"),
                rs.getString("firstName"),
                rs.getInt("phoneNumber")
        );
    }

    @Override
    protected PreparedStatement prepareInsertStatement(AgentDto agent) throws SQLException {
        String sql = "INSERT INTO Agent (lastName, firstName, phoneNumber) " +
                "VALUES (?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        stmt.setString(1, agent.lastName());
        stmt.setString(2, agent.firstName());
        stmt.setInt(3, agent.phoneNumber());
        return stmt;
    }

    @Override
    protected PreparedStatement prepareUpdateStatement(AgentDto agent) throws SQLException {
        String sql = "UPDATE Agent SET lastName = ?, firstname = ?, phoneNumber = ? WHERE aId = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, agent.lastName());
        stmt.setString(2, agent.firstName());
        stmt.setInt(3, agent.phoneNumber());
        stmt.setInt(4, agent.aId());
        return stmt;
    }

    @Override
    protected Integer getEntityId(AgentDto agent) {
        return agent.aId();
    }
}
