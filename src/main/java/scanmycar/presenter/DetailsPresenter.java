package scanmycar.presenter;

import scanmycar.model.dto.*;
import scanmycar.model.service.DataServiceImpl;
import scanmycar.model.service.PdfGenerationRunnable;
import scanmycar.view.DetailsControllerFxml;
import scanmycar.view.utils.AlertUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DetailsPresenter {
    private final DataServiceImpl dataService;
    private InspectionDto lastAddedInspection;

    private DetailsControllerFxml detailsControllerFxml;

    public DetailsPresenter() {
        this.dataService = new DataServiceImpl();
    }

    public void initializeVehicle(String plate) {
        Optional<VehicleDto> vehicle = dataService.findVehicleByPlate(plate);

        if (vehicle.isPresent()){
            Optional<OwnerDto> ownerDto = dataService.findOwnerById(vehicle.get().getOwnerId());
            detailsControllerFxml.setVehicleDetails(vehicle.get(),ownerDto.get());
        }
    }

    public List<InspectionDto> getInspectionsByPlate(String plate) {
        List<InspectionDto> list = dataService.findInspectionByPlate(plate);
        return list != null ? list : new ArrayList<>();
    }

    public void addInspection(String plate, String agentIdInput, String stateValue) {
        if (!validateInput(agentIdInput, stateValue)) return;

        int agentId = Integer.parseInt(agentIdInput);
        LastState state = LastState.fromDatabaseValue(stateValue);

        Optional<VehicleDto> vehicleOpt = dataService.findVehicleByPlate(plate);
        if (vehicleOpt.isEmpty()) {
            AlertUtils.showError("Erreur lors de l'ajout de l'inspection.");
        }

        VehicleDto vehicle = vehicleOpt.get();

        InspectionDto newInspection = new InspectionDto(0, agentId, LocalDate.now(), state, plate);
        int generatedId = dataService.saveInspection(newInspection);

        lastAddedInspection = new InspectionDto(generatedId, agentId, LocalDate.now(), state, plate);

        VehicleDto updated = new VehicleDto(
                vehicle.getVId(), plate, vehicle.getBrand(), vehicle.getModel(),
                vehicle.getYear(), vehicle.getColor(),
                FuelType.fromDatabaseValue(vehicle.getFuelType()), vehicle.getOwnerId(), state
        );
        dataService.saveVehicle(updated);
        detailsControllerFxml.addItemToTableView();
        detailsControllerFxml.successfullyAdded();
    }

    public InspectionDto getLastAddedInspection() {
        return lastAddedInspection;
    }

    private boolean canGenerateReport(InspectionDto lastInspection) {
        return lastInspection.date().equals(LocalDate.now());
    }

    public void generateReport(List<InspectionDto> inspections, VehicleDto vehicle) {
        if (inspections.isEmpty()) {
            AlertUtils.showError("Aucune inspection!");
            return;
        }

        InspectionDto lastInspection = inspections.getLast();
        if (!canGenerateReport(lastInspection)) {
            AlertUtils.showError("Aucune inspection n'a été réalisée aujourd'hui !");
            return;
        }

        Thread thread = new Thread(new PdfGenerationRunnable(lastInspection, vehicle));
        thread.start();

        detailsControllerFxml.successfullyPrinted();
    }

    private boolean validateInput(String agentIdInput, String stateValue) {
        if (agentIdInput == null || stateValue == null) {
            AlertUtils.showError("Tous les champs doivent etre remplis !");
            return false;
        }
        try {
            int agentNumber = Integer.parseInt(agentIdInput);
            Optional<AgentDto> agentDto = dataService.findAgentById(agentNumber);
            if (agentDto.isEmpty()) {
                AlertUtils.showError("Ce numéro d'agent n'existe pas !");
                return false;
            }
        } catch (NumberFormatException e) {
            AlertUtils.showError("Le numéro de l'agent ne doit contenir que des chiffres!");
            return false;
        }

        return true;
    }

    public void setDetailsControllerFxml(DetailsControllerFxml detailsControllerFxml) {
        this.detailsControllerFxml = detailsControllerFxml;
    }
}
