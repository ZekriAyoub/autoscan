package scanmycar.presenter;

import scanmycar.model.dto.FuelType;
import scanmycar.model.dto.OwnerDto;
import scanmycar.model.dto.VehicleDto;
import scanmycar.model.service.DataServiceImpl;
import scanmycar.model.service.MailServiceImpl;
import scanmycar.view.utils.AlertUtils;
import scanmycar.view.FormControllerFxml;

import java.io.IOException;
import java.util.Optional;

public class FormPresenter {
    private FormControllerFxml formControllerFxml;
    private static final String NAME_REGEX = "[a-zA-ZÀ-ÖØ-öø-ÿ\\-\\s]+";
    private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

    private String model;
    private String brand;
    private String color;
    private String year;
    private String owner;
    private String address;
    private String fuel;
    private String email;
    private String plate;
    DataServiceImpl dataService = new DataServiceImpl();

    public void setFormControllerFxml(FormControllerFxml formControllerFxml) {
        this.formControllerFxml = formControllerFxml;
    }

    private boolean validateNewOwnerInput() {
        if (model.isEmpty() || brand.isEmpty() || color.isEmpty() || year.isEmpty()
                || owner.isEmpty() || address.isEmpty() || fuel == null || email.isEmpty()) {
            AlertUtils.showError("Tous les champs doivent être remplis !");
            return false;
        }

        if (!isYearValid(year)) return false;

        if (!brand.matches(NAME_REGEX)) {
            AlertUtils.showError("La marque ne doit contenir que des lettres !");
            return false;
        }

        if (!owner.matches(NAME_REGEX)) {
            AlertUtils.showError("Le nom du propriétaire ne doit contenir que des lettres !");
            return false;
        }

        if (address.length() < 5) {
            AlertUtils.showError("L'adresse semble incorrecte !");
            return false;
        }

        return isEmailValid(email);
    }

    private boolean validateExistingOwnerInput() {
        if (model.isEmpty() || brand.isEmpty() || color.isEmpty() || year.isEmpty()
                || fuel == null || email.isEmpty()) {
            AlertUtils.showError("Tous les champs doivent être remplis !");
            return false;
        }

        if (!isYearValid(year)) return false;

        if (!brand.matches(NAME_REGEX)) {
            AlertUtils.showError("La marque ne doit contenir que des lettres !");
            return false;
        }

        return isEmailValid(email);
    }

    private boolean isEmailValid(String email) {
        if (!email.matches(EMAIL_REGEX)) {
            AlertUtils.showError("L'email est invalide !");
            return false;
        }
        return true;
    }

    private boolean isYearValid(String yearText) {
        try {
            int year = Integer.parseInt(yearText);
            if (year < 1886 || year > java.time.Year.now().getValue()) {
                AlertUtils.showError("L'année doit être comprise entre 1886 et "
                        + java.time.Year.now().getValue() + " !");
                return false;
            }
        } catch (NumberFormatException e) {
            AlertUtils.showError("L'année doit être un nombre valide !");
            return false;
        }
        return true;
    }


    private OwnerDto createOwner() {
        int id = dataService.saveOwner(new OwnerDto(0, owner, address, email));
        return (id > 0) ? new OwnerDto(id, owner, address, email) : null;
    }

    private VehicleDto createVehicle(int ownerId) {
        VehicleDto vehicle = new VehicleDto(0, plate, brand, model, Integer.parseInt(year), color,
                FuelType.fromDatabaseValue(fuel), ownerId, null);
        int id = dataService.saveVehicle(vehicle);

        VehicleDto created = dataService.findVehicleById(id).orElse(null);
        if (created != null) {
            MailServiceImpl mailService = new MailServiceImpl();
            mailService.sendVehicleConfirmation(vehicle);
        }

        return created;
    }

    public void treatment(String model, String brand, String color, String year, String owner,
                          String address, String fuel, String email, String plate,
                          boolean ownerAlreadyExists) throws IOException {
        this.model = model;
        this.brand = brand;
        this.color = color;
        this.year = year;
        this.owner = owner;
        this.address = address;
        this.fuel = fuel;
        this.email = email;
        this.plate = plate;

        if (ownerAlreadyExists) {
            treatmentExistingOwner();
            return;
        }

        if (!validateNewOwnerInput()) return;

        AlertUtils.showInfo("Formulaire valide, ajout en cours...");

        OwnerDto newOwner = createOwner();
        if (newOwner == null) {
            AlertUtils.showError("Erreur lors de l'enregistrement du propriétaire.");
            return;
        }

        VehicleDto newVehicle = createVehicle(newOwner.oId());

        if (newVehicle == null) {
            AlertUtils.showError("Erreur lors de l'enregistrement du véhicule.");
        }
        formControllerFxml.switchToDetailsPage();
    }

    private void treatmentExistingOwner() throws IOException {
        if (!validateExistingOwnerInput()) return;

        Optional<Integer> optionalOwnerId = dataService.findOwnerByEmail(email);

        if (optionalOwnerId.isPresent()) {
            int existingOwnerId = optionalOwnerId.get();
            AlertUtils.showInfo("Formulaire valide, ajout en cours...");
            VehicleDto newVehicle = createVehicle(existingOwnerId);
            if (newVehicle == null) {
                AlertUtils.showError("Erreur lors de l'enregistrement du véhicule.");
            }
            formControllerFxml.switchToDetailsPage();
        } else {
            AlertUtils.showError("Propriétaire non trouvé dans la base de données !");
        }
    }

}
