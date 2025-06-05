package scanmycar.model.repository;

import org.junit.jupiter.api.*;
import scanmycar.model.dto.AgentDto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AgentRepositoryTest {
    private static Connection connection;
    private AgentRepository agentRepository;

    @BeforeAll
    static void setupDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                        CREATE TABLE agent (
                            aId INTEGER PRIMARY KEY AUTOINCREMENT,
                            lastName TEXT NOT NULL,
                            firstName TEXT NOT NULL,
                            phoneNumber INTEGER NOT NULL
                        );
                    """);
        }
        ConnectionManagerTestHelper.setConnection(connection);
    }

    @BeforeEach
    void setup() throws SQLException {
        agentRepository = new AgentRepository();
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                    INSERT INTO agent (lastName, firstName, phoneNumber) VALUES
                    ('Absil', 'Romain', 1212121212),
                    ('Burda', 'Marcielo', 2121212121);
            """);
        }
    }

    @AfterEach
    void cleanUp() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            try (Statement stmt  = connection.createStatement()) {
                stmt.execute("DELETE FROM agent");
                stmt.execute("DELETE FROM sqlite_sequence WHERE name='agent'");
            }
        }
    }

    @AfterAll
    static void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void testFindByIdExists() {
        Optional<AgentDto> agent = agentRepository.findById(1);
        assertTrue(agent.isPresent());
        assertEquals("Absil", agent.get().getLastName());
    }

    @Test
    void testSave_InsertNewAgent() {
        AgentDto newAgent = new AgentDto(
                999,
                "Lambert",
                "Eve",
                888777666
        );

        Integer newId = agentRepository.save(newAgent);

        assertNotNull(newId,"L'id retourné ne doit pas être nul");

        Optional<AgentDto> inserted = agentRepository.findById(newId);
        assertTrue(inserted.isPresent(), "L'agent inséré devrait être retrouvé en BDD");
        assertEquals("Lambert", inserted.get().getLastName());
        assertEquals(888777666, inserted.get().getPhoneNumber());
    }

    @Test
    void testSave_UpdateExistingAgent() {
        // Préparer les données initiales et trouver Absil
        AgentDto agent = agentRepository.findAll().stream()
                .filter(a -> a.getLastName().equals("Absil"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Agent non trouvé"));

        // Modifier les infos de l'agent
        AgentDto updatedAgent = new AgentDto(
                agent.aId(),
                "Habil",
                "Roro",
                892938398
        );

        // Act
        Integer resultId = agentRepository.save(updatedAgent);

        // Assert
        assertEquals(agent.getAId(), resultId);
    }

    @Test
    void testDeleteById_RemovesOwner() {
        AgentDto absil = agentRepository.findAll().stream()
                .filter(o -> o.getLastName().equals("Absil"))
                .findFirst()
                .orElseThrow();

        agentRepository.deleteById(absil.aId());

        Optional<AgentDto> result = agentRepository.findById(absil.aId());
        assertTrue(result.isEmpty(), "Bob devrait avoir été supprimé");
    }


    @Test
    void testClose_DoesNotThrow() {
        AgentRepository repo = new AgentRepository(); // ne touche pas à l'instance partagée
        assertDoesNotThrow(repo::close);
    }
}