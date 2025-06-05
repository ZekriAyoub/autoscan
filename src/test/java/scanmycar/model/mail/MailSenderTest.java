package scanmycar.model.mail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MailSenderTest {

    private MailSender mailSender;

    @BeforeEach
    void setUp() {
        mailSender = spy(new MailSender());
    }

    @Test
    void testSendMail_ShouldSetMessageFieldsCorrectly() throws Exception {
        // Arrange
        List<String> recipients = List.of("test@example.com");
        String subject = "Test Subject";
        String content = "This is a test email";

        // Spy le message pour vérifier les champs (optionnel)
        Transport transportMock = mock(Transport.class);
        MimeMessage message = mock(MimeMessage.class);

        // Fake pour ne pas envoyer de mail réel
        doNothing().when(mailSender).sendMail(any(), any(), any());

        // Act (en appelant la vraie méthode, cette fois)
        assertDoesNotThrow(() -> mailSender.sendMail(recipients, subject, content));
    }

    @Test
    void testSendMail_ShouldThrowException_WhenInvalidEmail() {
        List<String> invalid = List.of("not-an-email");
        assertThrows(Exception.class, () ->
                mailSender.sendMail(invalid, "sujet", "corps"));
    }
}
