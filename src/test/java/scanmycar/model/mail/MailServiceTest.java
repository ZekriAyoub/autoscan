package scanmycar.model.mail;

import org.junit.jupiter.api.Test;
import scanmycar.model.dto.OwnerDto;
import scanmycar.model.dto.VehicleDto;
import scanmycar.model.service.DataServiceImpl;

import javax.mail.MessagingException;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class MailServiceTest {

    @Test
    void testSendVehicleConfirmation_Success() throws MessagingException,
            UnsupportedEncodingException,
            IllegalAccessException,
            InterruptedException,
            NoSuchFieldException {
        // Arrange
        DataServiceImpl mockOwnerRepo = mock(DataServiceImpl.class);
        MailSender mockMailSender = mock(MailSender.class);

        VehicleDto vehicle = new VehicleDto(1, "ABC123", "Toyota",
                "Corolla", 2010, "Red", null, 123, null);
        OwnerDto owner = new OwnerDto(123, "Alice",
                "1 rue Paris", "alice@example.com");

        when(mockOwnerRepo.findOwnerById(123)).thenReturn(Optional.of(owner));

        // Trick: inject mocks via reflection
        MailService mailService = new MailService();
        var ownerField = MailService.class.getDeclaredField("dataService");
        ownerField.setAccessible(true);
        ownerField.set(null, mockOwnerRepo);

        var senderField = MailService.class.getDeclaredField("mailSender");
        senderField.setAccessible(true);
        senderField.set(null, mockMailSender);

        // Act
        MailService.sendVehicleConfirmation(vehicle);

        // Wait a bit because it's async
        Thread.sleep(300);

        // Assert
        verify(mockMailSender, times(1)).sendMail(
                eq(List.of("alice@example.com")),
                contains("Confirmation"),
                contains("Bonjour Alice")
        );
    }

    @Test
    void testSendVehicleConfirmation_WhenSendFails() throws Exception {
        // Arrange
        DataServiceImpl mockOwnerRepo = mock(DataServiceImpl.class);
        MailSender mockMailSender = mock(MailSender.class);

        VehicleDto vehicle = new VehicleDto(1, "ABC123", "Toyota",
                "Corolla", 2010, "Red", null, 123, null);
        OwnerDto owner = new OwnerDto(123, "Alice",
                "1 rue Paris", "alice@example.com");

        when(mockOwnerRepo.findOwnerById(123)).thenReturn(Optional.of(owner));
        doThrow(new MessagingException("Erreur SMTP")).when(mockMailSender)
                .sendMail(any(), any(), any());

        // Injecter les mocks dans MailService
        var ownerField = MailService.class.getDeclaredField("dataService");
        ownerField.setAccessible(true);
        ownerField.set(null, mockOwnerRepo);

        var senderField = MailService.class.getDeclaredField("mailSender");
        senderField.setAccessible(true);
        senderField.set(null, mockMailSender);

        // Act
        MailService.sendVehicleConfirmation(vehicle);

        // Wait for async thread to run
        Thread.sleep(300);

        // Si on arrive ici sans exception, c’est que le bloc `catch` a bien capturé l'erreur
        verify(mockMailSender).sendMail(any(), any(), any());
    }
}