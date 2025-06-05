package scanmycar.model.mail;

import org.junit.jupiter.api.Test;
import scanmycar.model.dto.LastState;
import scanmycar.model.dto.OwnerDto;
import scanmycar.model.dto.VehicleDto;
import scanmycar.model.repository.OwnerRepository;
import scanmycar.model.repository.VehicleRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class TestableMailSchedulerTest {

    @Test
    void testStartOnce_SendsReminderToRefusedVehicleOwners() throws Exception {
        // Mocks
        OwnerRepository ownerRepository = mock(OwnerRepository.class);
        VehicleRepository vehicleRepository = mock(VehicleRepository.class);
        MailSender mailSender = mock(MailSender.class);

        // Fake data
        VehicleDto refusedVehicle = new VehicleDto(
                1, "XYZ123", "Renault", "Clio", 2012, "Blue",
                null, 101, LastState.REFUSE
        );

        OwnerDto owner = new OwnerDto(
                101, "Jean Dupont", "123 Rue de Paris", "jean.dupont@example.com"
        );

        // Stubs
        when(vehicleRepository.findAll()).thenReturn(List.of(refusedVehicle));
        when(ownerRepository.findById(101)).thenReturn(Optional.of(owner));

        // Instancier le scheduler de test
        TestableMailScheduler scheduler = new TestableMailScheduler(
                ownerRepository, mailSender, vehicleRepository
        );

        // Act
        scheduler.startOnce();
        Thread.sleep(300); // attendre l'exécution du timer

        // Assert
        verify(mailSender).sendMail(
                eq(List.of("jean.dupont@example.com")),
                eq("📅 Rappel ScanMyCar"),
                contains("Bonjour Jean Dupont")
        );
    }
}
