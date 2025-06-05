package scanmycar.model.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import scanmycar.model.dto.*;
import scanmycar.model.repository.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DataServiceImplTest {

    private OwnerRepository mockOwnerRepo;
    private VehicleRepository mockVehicleRepo;
    private AgentRepository mockAgentRepo;
    private InspectionRepository mockInspectionRepo;

    private DataServiceImpl dataService;

    @BeforeEach
    void setUp() {
        mockOwnerRepo = mock(OwnerRepository.class);
        mockVehicleRepo = mock(VehicleRepository.class);
        mockAgentRepo = mock(AgentRepository.class);
        mockInspectionRepo = mock(InspectionRepository.class);

        dataService = new DataServiceImpl(
                mockVehicleRepo,
                mockOwnerRepo,
                mockAgentRepo,
                mockInspectionRepo
        );
    }

    @Test
    void testFindOwnerById_shouldDelegateToRepository() {
        OwnerDto dummyOwner = new OwnerDto(1, "Alice", "123 rue Paris", "alice@example.com");

        when(mockOwnerRepo.findById(1)).thenReturn(Optional.of(dummyOwner));

        Optional<OwnerDto> result = dataService.findOwnerById(1);

        assertTrue(result.isPresent());
        assertEquals("Alice", result.get().getFullName());

        verify(mockOwnerRepo, times(1)).findById(1);
    }

    @Test
    void testFindAllEmailsOwner_shouldReturnEmails() {
        List<String> fakeEmails = List.of("a@example.com", "b@example.com");

        when(mockOwnerRepo.getAllEmails()).thenReturn(fakeEmails);

        List<String> result = dataService.getAllEmailsOwner();

        assertEquals(2, result.size());
        assertTrue(result.contains("a@example.com"));
    }

    @Test
    void testDeleteOwnerById_shouldCallRepository() {
        dataService.deleteOwnerById(55);
        verify(mockOwnerRepo, times(1)).deleteById(55);
    }
    @Test
    void testFindVehicleById_shouldReturnVehicle() {
        VehicleDto vehicle = new VehicleDto(1, "AB-123", "Renault", "Clio", 2020, "Rouge", FuelType.ESSENCE, 2, LastState.VALIDE);
        when(mockVehicleRepo.findById(1)).thenReturn(Optional.of(vehicle));

        Optional<VehicleDto> result = dataService.findVehicleById(1);

        assertTrue(result.isPresent());
        assertEquals("Clio", result.get().getModel());
        verify(mockVehicleRepo).findById(1);
    }

    @Test
    void testFindAllVehicles_shouldReturnList() {
        when(mockVehicleRepo.findAll()).thenReturn(List.of());
        List<VehicleDto> result = dataService.findAllVehicles();
        assertNotNull(result);
    }

    @Test
    void testFindAllPlates_shouldReturnPlates() {
        when(mockVehicleRepo.findAllPlate()).thenReturn(List.of("AA-111", "BB-222"));
        List<String> plates = dataService.findAllPlates();
        assertEquals(2, plates.size());
    }

    @Test
    void testFindVehicleByPlate_shouldDelegateToRepo() {
        VehicleDto vehicle = new VehicleDto(1, "ZZ-999", "Tesla", "Model 3", 2023, "Noir", FuelType.ELECTRIQUE, 3, LastState.REFUSE);
        when(mockVehicleRepo.findByPlate("ZZ-999")).thenReturn(Optional.of(vehicle));
        var result = dataService.findVehicleByPlate("ZZ-999");
        assertTrue(result.isPresent());
        assertEquals("Tesla", result.get().getBrand());
    }

    @Test
    void testSaveVehicle_shouldCallSave() {
        VehicleDto vehicle = mock(VehicleDto.class);
        when(mockVehicleRepo.save(vehicle)).thenReturn(123);
        Integer result = dataService.saveVehicle(vehicle);
        assertEquals(123, result);
    }

    @Test
    void testDeleteVehicle_shouldInvokeDelete() {
        dataService.deleteVehicleById(8);
        verify(mockVehicleRepo).deleteById(8);
    }

    @Test
    void testSaveOwner_shouldCallSave() {
        OwnerDto owner = new OwnerDto(0, "Bob", "Somewhere", "bob@mail.com");
        when(mockOwnerRepo.save(owner)).thenReturn(10);
        Integer result = dataService.saveOwner(owner);
        assertEquals(10, result);
    }

    @Test
    void testFindOwnerByEmail_shouldReturnId() {
        when(mockOwnerRepo.findByEmail("bob@mail.com")).thenReturn(Optional.of(999));
        var result = dataService.findOwnerByEmail("bob@mail.com");
        assertTrue(result.isPresent());
        assertEquals(999, result.get());
    }

    @Test
    void testFindAgentById_shouldWork() {
        AgentDto agent = new AgentDto(7, "Smith", "John", 123456789);
        when(mockAgentRepo.findById(7)).thenReturn(Optional.of(agent));
        var result = dataService.findAgentById(7);
        assertTrue(result.isPresent());
        assertEquals("Smith", result.get().getLastName());
    }

    @Test
    void testSaveAgents_shouldCallRepo() {
        AgentDto agent = mock(AgentDto.class);
        when(mockAgentRepo.save(agent)).thenReturn(77);
        int result = dataService.saveAgents(agent);
        assertEquals(77, result);
    }

    @Test
    void testDeleteAgent_shouldDelegate() {
        dataService.deleteAgentById(42);
        verify(mockAgentRepo).deleteById(42);
    }

    @Test
    void testFindInspectionByPlate_shouldReturnList() {
        when(mockInspectionRepo.findByPlate("ABC-999")).thenReturn(List.of());
        var list = dataService.findInspectionByPlate("ABC-999");
        assertNotNull(list);
    }

    @Test
    void testSaveInspection_shouldCallRepo() {
        InspectionDto inspection = mock(InspectionDto.class);
        when(mockInspectionRepo.save(inspection)).thenReturn(5);
        int result = dataService.saveInspection(inspection);
        assertEquals(5, result);
    }

    @Test
    void testFindAllOwners_shouldReturnList() {
        when(mockOwnerRepo.findAll()).thenReturn(List.of(
                new OwnerDto(1, "Alice", "Paris", "alice@mail.com"),
                new OwnerDto(2, "Bob", "Lyon", "bob@mail.com")
        ));

        List<OwnerDto> result = dataService.findAllOwners();

        assertEquals(2, result.size());
        verify(mockOwnerRepo).findAll();
    }

    @Test
    void testFindAllAgents_shouldReturnAgents() {
        when(mockAgentRepo.findAll()).thenReturn(List.of(
                new AgentDto(1, "Durand", "Jean", 123456789)
        ));

        List<AgentDto> result = dataService.findAllAgents();

        assertEquals(1, result.size());
        assertEquals("Jean", result.get(0).getFirstName());
        verify(mockAgentRepo).findAll();
    }

    @Test
    void testFindInspectionById_shouldReturnValue() {
        InspectionDto inspection = new InspectionDto(1, 3, LocalDate.now(), LastState.VALIDE, "ZZ-777");
        when(mockInspectionRepo.findById(1)).thenReturn(Optional.of(inspection));

        Optional<InspectionDto> result = dataService.findInspectionById(1);

        assertTrue(result.isPresent());
        assertEquals("ZZ-777", result.get().getPlate());
    }

    @Test
    void testFindAllInspections_shouldReturnList() {
        when(mockInspectionRepo.findAll()).thenReturn(List.of(
                new InspectionDto(1, 3, LocalDate.now(), LastState.VALIDE, "AA-888")
        ));

        List<InspectionDto> result = dataService.findAllInspections();

        assertEquals(1, result.size());
        verify(mockInspectionRepo).findAll();
    }

    @Test
    void testDeleteInspectionById_shouldDelegate() {
        dataService.deleteInspectionById(999);
        verify(mockInspectionRepo).deleteById(999);
    }

}
