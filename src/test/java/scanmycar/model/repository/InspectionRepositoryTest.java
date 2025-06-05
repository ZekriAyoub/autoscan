package scanmycar.model.repository;

import org.junit.jupiter.api.*;
import scanmycar.model.dto.InspectionDto;
import scanmycar.model.dto.LastState;
import scanmycar.model.repository.utilRepository.RepositoryException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InspectionRepositoryTest {
    private static Connection connection;
    private InspectionRepository inspectionRepository;

    @BeforeAll
    static void setupDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");

        try (Statement stmt = connection.createStatement()) {
            // Activer les clés étrangères
            stmt.execute("PRAGMA foreign_keys = ON");

            // 1. Créer la table Owner
            stmt.execute("""
            CREATE TABLE owner (
                oId INTEGER PRIMARY KEY AUTOINCREMENT,
                fullName TEXT NOT NULL,
                address TEXT NOT NULL,
                email TEXT NOT NULL
            );
        """);

            // 2. Créer la table Agent
            stmt.execute("""
            CREATE TABLE Agent (
                aId INTEGER PRIMARY KEY AUTOINCREMENT,
                lastName TEXT NOT NULL,
                firstName TEXT NOT NULL,
                phoneNumber INTEGER NOT NULL
            );
        """);

            // 3. Créer la table Vehicle (avec FK vers owner)
            stmt.execute("""
            CREATE TABLE vehicle (
                licensePlate TEXT PRIMARY KEY,
                brand TEXT NOT NULL,
                model INTEGER NOT NULL,
                year INTEGER NOT NULL,
                color TEXT NOT NULL,
                fuelType TEXT NOT NULL,
                ownerId INTEGER NOT NULL,
                lastState TEXT,
                vId INTEGER,
                FOREIGN KEY (ownerId) REFERENCES owner(oId)
            );
        """);

            // 4. Créer la table Inspection (FK vers agent & vehicle)
            stmt.execute("""
            CREATE TABLE Inspection (
                insId INTEGER PRIMARY KEY AUTOINCREMENT,
                agent INTEGER NOT NULL,
                date TEXT NOT NULL,
                state TEXT NOT NULL,
                plate TEXT NOT NULL,
                FOREIGN KEY (agent) REFERENCES Agent(aId),
                FOREIGN KEY (plate) REFERENCES vehicle(licensePlate)
            );
        """);
        }

        // Forcer l’utilisation de cette connexion dans le repo
        ConnectionManagerTestHelper.setConnection(connection);
    }

    @BeforeEach
    void setup() throws SQLException {
        inspectionRepository = new InspectionRepository();

        try (Statement stmt = connection.createStatement()) {
            // 1. Insérer un owner (FK pour vehicle)
            stmt.execute("""
            INSERT INTO owner (fullName, address, email)
            VALUES ('Alice Dupont', '123 Rue Principale', 'alice@example.com');
        """);

            // 2. Insérer un agent (FK pour inspection)
            stmt.execute("""
            INSERT INTO Agent (lastName, firstName, phoneNumber)
            VALUES ('Smith', 'John', 123456789);
        """);

            // 3. Insérer un véhicule (lié à owner)
            stmt.execute("""
            INSERT INTO vehicle (licensePlate, brand, model, year, color, fuelType, ownerId, lastState, vId)
            VALUES ('ABC123', 'Toyota', 2020, 2020, 'Red', 'DIESEL', 1, 'VALIDE', 1);
        """);

            // 4. Insérer une inspection (liée à agent + véhicule)
            stmt.execute("""
            INSERT INTO Inspection (agent, date, state, plate)
            VALUES (1, '2024-04-10', 'VALIDE', 'ABC123');
        """);
        }
    }

    @AfterEach
    void cleanUp() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            try (Statement stmt = connection.createStatement()) {
                // ⚠️ Respecter l'ordre inverse des dépendances
                stmt.execute("DELETE FROM Inspection");
                stmt.execute("DELETE FROM vehicle");
                stmt.execute("DELETE FROM Agent");
                stmt.execute("DELETE FROM owner");
                stmt.execute("DELETE FROM sqlite_sequence");
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
        Optional<InspectionDto> inspection = inspectionRepository.findById(1);
        assertTrue(inspection.isPresent());
        assertEquals("ABC123", inspection.get().getPlate());
    }

    @Test
    void testSave_InsertNewInspection() {
        // Arrange
        InspectionDto newInspection = new InspectionDto(
                0, // ID fictif (sera auto-généré)
                1, // agentId (existant via @BeforeEach)
                LocalDate.of(2025, 4, 15),
                LastState.NULL,
                "ABC123" // plaque existante (déjà insérée)
        );

        // Act
        Integer newId = inspectionRepository.save(newInspection);

        // Assert
        assertNotNull(newId, "L'ID retourné ne doit pas être nul");

        // Vérifier via findAll ou requête directe si nécessaire
        List<InspectionDto> inspections = inspectionRepository.findByPlate("ABC123");
        assertTrue(inspections.stream().anyMatch(ins ->
                ins.insId() == newId &&
                        ins.agent() == 1 &&
                        ins.state() == LastState.NULL &&
                        ins.date().equals(LocalDate.of(2025, 4, 15))
        ));
    }

    @Test
    void testSave_UpdateExistingInspection() {
        // Récupérer une inspection existante (via la plaque connue)
        InspectionDto existing = inspectionRepository.findByPlate("ABC123")
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Inspection non trouvée"));

        // Créer une version modifiée
        InspectionDto updated = new InspectionDto(
                existing.insId(),         // même ID → déclenche update
                existing.agent(),         // même agent
                LocalDate.of(2025, 4, 20),// nouvelle date
                LastState.NULL,         // nouveau state
                "ABC123"                  // même plaque
        );

        // Act
        Integer resultId = inspectionRepository.save(updated);

        // Assert
        assertEquals(existing.insId(), resultId);

        List<InspectionDto> resultList = inspectionRepository.findByPlate("ABC123");
        InspectionDto result = resultList.stream()
                .filter(i -> i.insId() == resultId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Inspection mise à jour introuvable"));

        assertEquals(LocalDate.of(2025, 4, 20), result.date());
        assertEquals(LastState.NULL.toDatabaseValue(), result.state().toDatabaseValue());
    }



    @Test
    void testFindByPlate_ReturnsInspectionList() {
        List<InspectionDto> inspections = inspectionRepository.findByPlate("ABC123");

        assertNotNull(inspections, "La liste ne doit pas être nulle");
        assertEquals(1, inspections.size(), "Il devrait y avoir une inspection");
        InspectionDto inspection = inspections.get(0);
        assertEquals("ABC123", inspection.plate());
        assertEquals(1, inspection.agent());
        assertEquals(LocalDate.of(2024, 4, 10), inspection.date());
    }

    @Test
    void testFindByPlate_ReturnsEmptyList_WhenPlateUnknown() {
        List<InspectionDto> inspections = inspectionRepository.findByPlate("UNKNOWN_PLATE");

        assertNotNull(inspections);
        assertTrue(inspections.isEmpty(), "La liste doit être vide pour une plaque inconnue");
    }

    @Test
    void testFindByPlate_ThrowsRepositoryException_WhenTableMissing() throws SQLException {
        // Supprimer la table Inspection pour forcer une erreur SQL
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS Inspection");
        }

        assertThrows(RepositoryException.class, () -> inspectionRepository.findByPlate("ABC123"));

        // Recréer la table pour les tests suivants
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
            CREATE TABLE Inspection (
                insId INTEGER PRIMARY KEY AUTOINCREMENT,
                agent INTEGER NOT NULL,
                date TEXT NOT NULL,
                state TEXT NOT NULL,
                plate TEXT NOT NULL,
                FOREIGN KEY (agent) REFERENCES Agent(aId),
                FOREIGN KEY (plate) REFERENCES vehicle(licensePlate)
            );
        """);
        }
    }



}