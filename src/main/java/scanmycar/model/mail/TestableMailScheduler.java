package scanmycar.model.mail;

import scanmycar.model.dto.LastState;
import scanmycar.model.dto.OwnerDto;
import scanmycar.model.dto.VehicleDto;
import scanmycar.model.repository.OwnerRepository;
import scanmycar.model.repository.VehicleRepository;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

/**
 * A test-friendly version of the MailScheduler class, designed for unit testing and controlled execution.
 *
 * This class is responsible for sending reminder emails to owners whose vehicles have failed
 * the last technical inspection. Unlike the production scheduler, this version only runs once,
 * making it easier to test without waiting for scheduled intervals.
 */
public class TestableMailScheduler {
    private final OwnerRepository ownerRepository;
    private final MailSender mailSender;
    private final VehicleRepository vehicleRepository;

    /**
     * Constructs the scheduler with the required repositories and mail service.
     *
     * @param ownerRepository the repository used to fetch owner information
     * @param mailSender the service used to send emails
     * @param vehicleRepository the repository used to retrieve vehicle data
     */
    public TestableMailScheduler(OwnerRepository ownerRepository, MailSender mailSender,
                                 VehicleRepository vehicleRepository) {
        this.ownerRepository = ownerRepository;
        this.mailSender = mailSender;
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Executes the email reminder logic a single time.
     * This method filters vehicles with a "REFUSE" state and sends reminder emails
     * to their respective owners.
     */
    public void startOnce() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                List<VehicleDto> allVehicles = vehicleRepository.findAll();

                List<VehicleDto> refusedVehicles = allVehicles.stream()
                        .filter(v -> v.getLastState().equals(LastState.REFUSE.toDatabaseValue()))
                        .toList();

                Set<Integer> ownerIds = refusedVehicles.stream()
                        .map(VehicleDto::getOwnerId)
                        .collect(Collectors.toSet());

                List<OwnerDto> ownersToNotify = ownerIds.stream()
                        .map(id -> ownerRepository.findById(id).orElse(null))
                        .filter(Objects::nonNull)
                        .toList();

                for (OwnerDto owner : ownersToNotify) {
                    try {
                        mailSender.sendMail(
                                List.of(owner.getEmail()),
                                "📅 Rappel ScanMyCar",
                                "Bonjour " + owner.getFullName() + ",\n\n[...]"
                        );
                    } catch (Exception e) {
                        System.err.println("Erreur lors de l'envoi de l'e-mail à " + owner.getEmail());
                    }
                }
            }
        };

        new Timer().schedule(task, 0);
    }
}
