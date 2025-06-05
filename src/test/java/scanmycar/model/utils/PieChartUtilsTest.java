package scanmycar.model.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import scanmycar.model.dto.FuelType;
import scanmycar.model.dto.LastState;
import scanmycar.model.dto.VehicleDto;
import scanmycar.model.repository.VehicleRepository;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PieChartUtilsTest {

    private VehicleRepository vehicleRepository;

    @BeforeEach
    void setUp() {
        vehicleRepository = mock(VehicleRepository.class);
    }

    @Test
    void testGetVehicleStateCounts() {
        // Création de véhicules fictifs
        VehicleDto vehicle1 = new VehicleDto(1, "AA-123-BB", "Toyota", "Yaris", 2020, "Red", FuelType.ESSENCE, 101, LastState.VALIDE);
        VehicleDto vehicle2 = new VehicleDto(2, "CC-456-DD", "Renault", "Clio", 2019, "Blue", FuelType.DIESEL, 102, LastState.REFUSE);
        VehicleDto vehicle3 = new VehicleDto(3, "EE-789-FF", "Peugeot", "208", 2021, "White", FuelType.ELECTRIQUE, 103, LastState.VALIDE);


        List<VehicleDto> vehicles = List.of(vehicle1, vehicle2, vehicle3);

        try (MockedConstruction<VehicleRepository> mocked = Mockito.mockConstruction(VehicleRepository.class,
                (mock, context) -> when(mock.findAll()).thenReturn(vehicles))) {

            Map<LastState, Integer> result = PieChartUtils.getVehicleStateCounts();

            assertEquals(2, result.get(LastState.VALIDE));
            assertEquals(1, result.get(LastState.REFUSE));
            for (LastState state : LastState.values()) {
                if (state != LastState.VALIDE && state != LastState.REFUSE) {
                    assertEquals(0, result.get(state));
                }
            }
        }
    }
}
