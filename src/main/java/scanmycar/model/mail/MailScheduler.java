package scanmycar.model.mail;

import scanmycar.model.dto.LastState;
import scanmycar.model.dto.OwnerDto;
import scanmycar.model.dto.VehicleDto;
import scanmycar.model.service.DataServiceImpl;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Task scheduler for automatically sending reminder emails
 * to owners of vehicles that failed the technical inspection.
 *
 * This service runs automatically every two weeks and sends
 * personalized emails to the affected users.
 */
public class MailScheduler {
    private final DataServiceImpl dataService;
    private final MailSender mailSender;

    public MailScheduler() {
        this.dataService = new DataServiceImpl();
        this.mailSender = new MailSender();
    }

    /**
     * Starts the email scheduler.
     * A reminder is sent every two weeks to owners whose vehicles
     * were rejected during the last inspection.
     */
    public void start() {
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                List<VehicleDto> allVehicles = dataService.findAllVehicles();

                // Filtrer les véhicules refusés
                List<VehicleDto> refusedVehicles = allVehicles.stream()
                        .filter(vehicle -> vehicle.getLastState() == LastState.REFUSE.toDatabaseValue())
                        .collect(Collectors.toList());

                // Extraire les propriétaires concernés sans doublons
                Set<Integer> ownerIds = refusedVehicles.stream()
                        .map(VehicleDto::getOwnerId)
                        .collect(Collectors.toSet());

                List<OwnerDto> ownersToNotify = ownerIds.stream()
                        .map(id -> dataService.findOwnerById(id).orElse(null))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                // Envoi des mails personnalisés
                for (OwnerDto owner : ownersToNotify) {
                    String email = owner.getEmail();
                    String fullName = owner.getFullName();

                    String subject = "📅 Rappel ScanMyCar";
                    String content = String.format(
                            "Bonjour %s,\n\n" +
                                    "Nous vous rappelons que l’un de vos véhicules " +
                                    "a été refusé lors du dernier contrôle.\n" +
                                    "Merci de prendre les mesures nécessaires.\n\n" +
                                    "Ceci est un rappel automatique toutes les deux semaines de ScanMyCar.\n\n" +
                                    "Bonne journée !", fullName
                    );

                    try {
                        mailSender.sendMail(List.of(email), subject, content);
                        Thread.sleep(1000); // Petite pause pour éviter le flag spam
                    } catch (MessagingException | UnsupportedEncodingException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        long twoWeeksMs = 14L * 24 * 60 * 60 * 1000; // remplacer par 30 * 1000 pour 30 secondes
        timer.scheduleAtFixedRate(task, 0, twoWeeksMs);
    }
}