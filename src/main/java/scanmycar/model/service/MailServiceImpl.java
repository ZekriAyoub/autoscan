package scanmycar.model.service;

import scanmycar.model.dto.VehicleDto;
import scanmycar.model.mail.MailScheduler;
import scanmycar.model.mail.MailSender;
import scanmycar.model.mail.MailService;

/**
 * Service implementation for handling email-related operations.
 * This class is responsible for sending confirmation emails and starting
 * scheduled email tasks.
 */
public class MailServiceImpl {
    private final MailSender mailSender;
    private final DataServiceImpl dataService;

    /**
     * Constructs a new instance of MailServiceImpl with its dependencies.
     */
    public MailServiceImpl() {
        this.mailSender = new MailSender();
        this.dataService = new DataServiceImpl();
    }

    /**
     * Sends a confirmation email when a vehicle is successfully registered.
     *
     * @param vehicle The vehicle for which the confirmation email should be sent.
     */
    public void sendVehicleConfirmation(VehicleDto vehicle) {
        MailService.sendVehicleConfirmation(vehicle);
    }

    /**
     * Starts the email scheduler to send automated emails every two weeks.
     */
    public void startScheduler() {
        MailScheduler scheduler = new MailScheduler();
        scheduler.start();
    }
}