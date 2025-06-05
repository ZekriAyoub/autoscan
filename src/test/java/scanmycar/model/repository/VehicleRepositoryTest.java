package scanmycar.model.repository;

import org.junit.jupiter.api.*;
import scanmycar.model.dto.FuelType;
import scanmycar.model.dto.LastState;
import scanmycar.model.dto.VehicleDto;
import scanmycar.model.repository.utilRepository.RepositoryException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class VehicleRepositoryTest {
    private static Connection connection;
    private VehicleRepository vehicleRepository;
    private Integer ownerId = 1;

    @BeforeAll
    static void setupDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");

        try (Statement stmt = connection.createStatement()) {
            // Créer la table OWNER d'abord (clé primaire référencée)
            stmt.execute("""
            CREATE TABLE owner (
                oId INTEGER PRIMARY KEY AUTOINCREMENT,
                fullName TEXT NOT NULL,
                address TEXT NOT NULL,
                email TEXT NOT NULL
            );
        """);

            // Puis la table VEHICLE qui dépend de owner
            stmt.execute("""
            CREATE TABLE vehicle (
                licensePlate TEXT NOT NULL,
                brand TEXT NOT NULL,
                model INTEGER NOT NULL,
                year INTEGER NOT NULL,
                color TEXT NOT NULL,
                fuelType TEXT NOT NULL,
                ownerId INTEGER NOT NULL,
                lastState TEXT,
                vId INTEGER PRIMARY KEY AUTOINCREMENT,
                FOREIGN KEY (ownerId) REFERENCES owner(oId)
            );
        """);
        }

        ConnectionManagerTestHelper.setConnection(connection);
    }

    @BeforeEach
    void setup() throws SQLException {
        vehicleRepository = new VehicleRepository();

        try (Statement stmt = connection.createStatement()) {
            // Insertion d'un propriétaire (pour respecter la FK)
            stmt.execute("""
            INSERT INTO owner (fullName, address, email) VALUES 
            ('Alice Dupont', '123 Rue Principale', 'alice@example.com');
        """);

            // Ensuite insertion de véhicules reliés à cet owner (oId = 1)
            stmt.execute("""
            INSERT INTO vehicle (licensePlate, brand, model, year, color, fuelType, ownerId, lastState) VALUES 
            ('ABC123', 'Toyota', 'Corolla', 2010, 'Rouge', 'ESSENCE', 1, 'VALIDE'),
            ('XYZ789', 'Peugeot', '308', 2020, 'Bleu', 'DIESEL', 1, 'VALIDE');
        """);
        }
    }

    @AfterEach
    void cleanUp() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            try (Statement stmt = connection.createStatement()) {
                // Supprimer d'abord les véhicules (car dépendant de owner)
                stmt.execute("DELETE FROM vehicle");

                // Puis les propriétaires
                stmt.execute("DELETE FROM owner");

                // Réinitialiser l'auto-incrément (si tu veux que les IDs recommencent à 1)
                stmt.execute("DELETE FROM sqlite_sequence WHERE name IN ('vehicle', 'owner')");
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
        Optional<VehicleDto> vehicle = vehicleRepository.findById(1);
        assertTrue(vehicle.isPresent());
        assertEquals("ABC123", vehicle.get().getLicensePlate());
    }

    @Test
    void testSave_InsertNewVehicle() {
        // Arrange
        VehicleDto newVehicle = new VehicleDto(
                999,
                "ZYX987",
                "Citroen",
                "C4",
                1999,
                "Red",
                FuelType.DIESEL,
                ownerId,
                LastState.REFUSE
        );

        // Act
        Integer newId = vehicleRepository.save(newVehicle);

        // Assert
        assertNotNull(newId, "L'ID retourné ne doit pas être nul");

        Optional<VehicleDto> inserted = vehicleRepository.findById(newId);
        assertTrue(inserted.isPresent(), "L'owner inséré devrait être retrouvé en BDD");
        assertEquals("ZYX987", inserted.get().getLicensePlate());
        assertEquals("C4", inserted.get().getModel());
    }

    @Test
    void testSave_UpdateExistingVehicle() {
        VehicleDto vehicle = vehicleRepository.findAll().stream()
                .filter(v -> v.getLicensePlate().equals("ABC123"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Plaque non trouvé"));

        // Modifier les infos du véhicule
        VehicleDto updatedVehicle = new VehicleDto(
                vehicle.getVId(),
                "ABC123",
                "Renault",
                "Kangoo",
                1999,
                "Red",
                FuelType.ELECTRIQUE,
                ownerId,
                LastState.VALIDE
        );

        // Act
        Integer resultId = vehicleRepository.save(updatedVehicle);

        // Assert
        assertEquals(vehicle.getVId(), resultId);

        Optional<VehicleDto> afterUpdate = vehicleRepository.findById(resultId);
        assertTrue(afterUpdate.isPresent());

        VehicleDto result = afterUpdate.get();
        assertEquals("Renault", result.getBrand());
        assertEquals("Kangoo", result.getModel());
        assertEquals(FuelType.ELECTRIQUE.toDatabaseValue(), result.getFuelType());
    }

    @Test
    void testDeleteById_RemovesOwner() {
        VehicleDto vehicle = vehicleRepository.findAll().stream()
                .filter(v -> v.getLicensePlate().equals("ABC123"))
                .findFirst()
                .orElseThrow();

        vehicleRepository.deleteById(vehicle.getVId());

        Optional<VehicleDto> result = vehicleRepository.findById(vehicle.getVId());
        assertTrue(result.isEmpty(), "Véhicule devrait avoir été supprimé");
    }

    @Test
    void testSave_InvalidOwner_ThrowsException() {
        VehicleDto invalidOwner = new VehicleDto(
                999,
                null,  // fullName est NOT NULL
                null,
                null,
                null,
                "red",
                null,
                111,
                null
        );

        assertThrows(NullPointerException.class, () -> vehicleRepository.save(invalidOwner));
    }

    @Test
    void testFindAllPlate_MultiplePlates() {
        List<String> expectedPlates = List.of("ABC123", "XYZ789");

        List<String> actualPlates = vehicleRepository.findAllPlate();

        assertEquals(expectedPlates.size(), actualPlates.size());
        assertTrue(actualPlates.containsAll(expectedPlates));
    }

    @Test
    void testFindAllPlate_ThrowsRepositoryException_WhenTableMissing() throws SQLException {
        // Supprimer la table vehicle (provoque une erreur SQL)
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS vehicle");
        }

        // Vérifier que l'appel lève bien une RepositoryException
        assertThrows(RepositoryException.class, () -> vehicleRepository.findAllPlate());

        // Recréer la table pour ne pas casser les tests suivants
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
            CREATE TABLE vehicle (
                licensePlate TEXT NOT NULL,
                brand TEXT NOT NULL,
                model INTEGER NOT NULL,
                year INTEGER NOT NULL,
                color TEXT NOT NULL,
                fuelType TEXT NOT NULL,
                ownerId INTEGER NOT NULL,
                lastState TEXT,
                vId INTEGER PRIMARY KEY AUTOINCREMENT,
                FOREIGN KEY (ownerId) REFERENCES owner(oId)
            );
        """);
        }
    }

    @Test
    void testFindByPlate_NotFound_ReturnsEmpty() {
        Optional<VehicleDto> result = vehicleRepository.findByPlate("NOT_EXISTING_PLATE");

        assertTrue(result.isEmpty(), "On attend un Optional.empty() pour une plaque inconnue");
    }

    @Test
    void testFindByPlate_ThrowsRepositoryException_WhenTableMissing() throws SQLException {
        // Supprimer la table
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS vehicle");
        }

        // Appel de la méthode → devrait lever RepositoryException
        assertThrows(RepositoryException.class, () -> vehicleRepository.findByPlate("ANY_PLATE"));

        // Recréer la table pour les tests suivants
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
            CREATE TABLE vehicle (
                licensePlate TEXT NOT NULL,
                brand TEXT NOT NULL,
                model INTEGER NOT NULL,
                year INTEGER NOT NULL,
                color TEXT NOT NULL,
                fuelType TEXT NOT NULL,
                ownerId INTEGER NOT NULL,
                lastState TEXT,
                vId INTEGER PRIMARY KEY AUTOINCREMENT,
                FOREIGN KEY (ownerId) REFERENCES owner(oId)
            );
        """);
        }
    }

    @Test
    void testFindByPlate_ReturnsVehicle_WhenExists() {
        Optional<VehicleDto> result = vehicleRepository.findByPlate("ABC123");

        assertTrue(result.isPresent(), "On attend un résultat pour la plaque ABC123");

        VehicleDto vehicle = result.get();
        assertEquals("ABC123", vehicle.licensePlate());
        assertEquals("Toyota", vehicle.brand());
        assertEquals("Essence", vehicle.fuelType().toDatabaseValue());
    }
}