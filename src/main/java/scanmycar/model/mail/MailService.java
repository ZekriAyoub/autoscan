package scanmycar.model.mail;

import scanmycar.model.dto.OwnerDto;
import scanmycar.model.dto.VehicleDto;
import scanmycar.model.service.DataServiceImpl;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Class for sending emails related to user actions.
 * Currently, this service handles sending confirmation emails after a vehicle registration.
 */
public class MailService {
    static DataServiceImpl dataService = new DataServiceImpl();
    static MailSender mailSender = new MailSender();

    /**
     * Sends a confirmation email to a vehicle owner after the vehicle has been registered.
     * The email is sent in a separate thread to avoid blocking the main application.
     *
     * @param vehicle The vehicle that has just been registered.
     */
    public static void sendVehicleConfirmation(VehicleDto vehicle) {
        if (vehicle == null) return;

        new Thread(() -> {
            OwnerDto owner = dataService.findOwnerById(vehicle.getOwnerId()).orElse(null);
            if (owner == null) return;

            String email = owner.getEmail();
            String fullName = owner.getFullName();
            String subject = "\uD83D\uDE97 Confirmation d’enregistrement de votre véhicule";
            String content =  String.format(
                            "Bonjour %s,\n\n" +
                            "Nous vous confirmons que votre véhicule a bien été enregistré dans notre système.\n" +
                            "Merci d’utiliser ScanMyCar !\n\n" +
                            "À bientôt,\nL'équipe ScanMyCar",
                    fullName
            );
            try {
                mailSender.sendMail(List.of(email), subject, content);
            } catch (MessagingException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
