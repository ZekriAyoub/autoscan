package scanmycar.model.mail;

import io.github.cdimascio.dotenv.Dotenv;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Service responsible for sending emails via SMTP (Gmail in this case).
 * Credentials are loaded from a .env file.
 */
public class MailSender {
    Dotenv dotenv = Dotenv.load();
    private final String username = dotenv.get("EMAIL_USERNAME");
    private final String password = dotenv.get("EMAIL_PASSWORD");
    private final Session session;

    /**
     * Initializes the mail session with Gmail's SMTP settings.
     */
    public MailSender() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        this.session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }


    /**
     * Sends an email using the configured SMTP session.
     *
     * @param destinataires List of recipient email addresses.
     * @param sujet         Subject of the email.
     * @param contenu       Plain text content of the email.
     *
     * @throws MessagingException If a failure occurs during the sending process (e.g. invalid address, connection issue).
     * @throws UnsupportedEncodingException If the sender's name encoding is invalid.
     */

    public void sendMail(List<String> destinataires, String sujet, String contenu) throws MessagingException,
            UnsupportedEncodingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username, "ScanMyCar"));

        String joined = destinataires.stream().collect(Collectors.joining(","));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(joined));

        message.setSubject(sujet);
        message.setText(contenu);

        Transport.send(message);
    }
}
