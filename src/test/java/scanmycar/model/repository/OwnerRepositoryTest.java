package scanmycar.model.repository;

import org.junit.jupiter.api.*;
import scanmycar.model.dto.OwnerDto;
import scanmycar.model.repository.utilRepository.RepositoryException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class OwnerRepositoryTest {
    private static Connection connection;
    private OwnerRepository ownerRepository;

    @BeforeAll
    static void setupDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                    CREATE TABLE owner (
                        oId INTEGER PRIMARY KEY AUTOINCREMENT,
                        fullName TEXT NOT NULL,
                        address TEXT NOT NULL,
                        email TEXT NOT NULL
                    );
                """);
        }
        ConnectionManagerTestHelper.setConnection(connection);
    }

    @BeforeEach
    void setup() throws SQLException {
        ownerRepository = new OwnerRepository();
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                INSERT INTO owner (fullName, address, email) VALUES 
                ('Alice Dupont', '123 Rue Principale', 'alice@example.com'),
                ('Bob Martin', '456 Avenue des Champs', 'bob@example.com');
            """);
        }
    }

    @AfterEach
    void cleanUp() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("DELETE FROM owner");
                stmt.execute("DELETE FROM sqlite_sequence WHERE name='owner'");
            }
        }
    }

    @AfterAll
    static void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    void testFindByIdExists() {
        Optional<OwnerDto> owner = ownerRepository.findById(1);
        assertTrue(owner.isPresent());
        assertEquals("Alice Dupont", owner.get().getFullName());
    }


    @Test
    void testGetAllEmails() {
        var emails = ownerRepository.getAllEmails();
        assertEquals(2, emails.size());
        assertTrue(emails.contains("alice@example.com"));
        assertTrue(emails.contains("bob@example.com"));
    }

    @Test
    void testSave_InsertNewOwner() {
        // Arrange
        OwnerDto newOwner = new OwnerDto(
                999,  // ID fictif non présent en BDD (sera ignoré à l'insertion)
                "Eve Lambert",
                "789 Rue de Paris",
                "eve@example.com"
        );

        // Act
        Integer newId = ownerRepository.save(newOwner);

        // Assert
        assertNotNull(newId, "L'ID retourné ne doit pas être nul");

        Optional<OwnerDto> inserted = ownerRepository.findById(newId);
        assertTrue(inserted.isPresent(), "L'owner inséré devrait être retrouvé en BDD");
        assertEquals("Eve Lambert", inserted.get().fullName());
        assertEquals("eve@example.com", inserted.get().getEmail());
    }

    @Test
    void testSave_UpdateExistingOwner() {
        // Préparer les données initiales et trouver Alice
        OwnerDto alice = ownerRepository.findAll().stream()
                .filter(o -> o.getFullName().equals("Alice Dupont"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Alice non trouvée"));

        // Modifier les infos d'Alice
        OwnerDto updatedAlice = new OwnerDto(
                alice.getOId(),  // utilise l'ID réel en base
                "Alice Changement",
                "999 Avenue Modifiée",
                "alice.new@example.com"
        );

        // Act
        Integer resultId = ownerRepository.save(updatedAlice);

        // Assert
        assertEquals(alice.oId(), resultId);

        Optional<OwnerDto> afterUpdate = ownerRepository.findById(resultId);
        assertTrue(afterUpdate.isPresent());

        OwnerDto result = afterUpdate.get();
        assertEquals("Alice Changement", result.fullName());
        assertEquals("999 Avenue Modifiée", result.address());
        assertEquals("alice.new@example.com", result.getEmail());
    }

    @Test
    void testDeleteById_RemovesOwner() {
        OwnerDto bob = ownerRepository.findAll().stream()
                .filter(o -> o.fullName().equals("Bob Martin"))
                .findFirst()
                .orElseThrow();

        ownerRepository.deleteById(bob.getOId());

        Optional<OwnerDto> result = ownerRepository.findById(bob.getOId());
        assertTrue(result.isEmpty(), "Bob devrait avoir été supprimé");
    }

    @Test
    void testSave_InvalidOwner_ThrowsException() {
        OwnerDto invalidOwner = new OwnerDto(
                0,
                null,  // fullName est NOT NULL
                "123 Rue Test",
                "test@example.com"
        );

        assertThrows(RepositoryException.class, () -> ownerRepository.save(invalidOwner));
    }

    @Test
    void testGetAllEmails_ThrowsException() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE owner");
        }

        assertThrows(RepositoryException.class, () -> ownerRepository.getAllEmails());

        // recréer la table pour les tests suivants
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
            CREATE TABLE owner (
                oId INTEGER PRIMARY KEY AUTOINCREMENT,
                fullName TEXT NOT NULL,
                address TEXT NOT NULL,
                email TEXT NOT NULL
            );
        """);
        }
    }

    @Test
    void testClose_DoesNotThrow() {
        OwnerRepository repo = new OwnerRepository(); // ne touche pas à l'instance partagée
        assertDoesNotThrow(repo::close);
    }


    @Test
    void testInsert_ThrowsSQLException() {
        OwnerDto invalidOwner = new OwnerDto(
                0,
                null,  // fullName est NOT NULL
                "Some Address",
                "some@example.com"
        );

        assertThrows(RepositoryException.class, () -> ownerRepository.save(invalidOwner));
    }

    @Test
    void testUpdateOwnerFails_NoRowsUpdated() {
        OwnerDto nonExistent = new OwnerDto(
                9999,  // ID inexistant
                "Ghost",
                "Nowhere",
                "ghost@example.com"
        );

        // save() retournera null si update ne met à jour aucune ligne
        Integer result = ownerRepository.save(nonExistent);
        assertNotNull(result, "Même si l'update échoue, un insert doit être fait à la place");
    }
}