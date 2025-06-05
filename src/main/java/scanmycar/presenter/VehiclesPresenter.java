package scanmycar.presenter;

import scanmycar.model.dto.VehicleDto;
import scanmycar.model.service.DataServiceImpl;
import scanmycar.view.utils.AlertUtils;
import scanmycar.view.VehiclesListControllerFxml;

import java.io.IOException;
import java.util.List;

public class VehiclesPresenter {
    private final DataServiceImpl dataService;
    private VehiclesListControllerFxml vehiclesListControllerFxml;

    public VehiclesPresenter() {
        this.dataService = new DataServiceImpl();
    }

    public void setVehiclesListControllerFxml(VehiclesListControllerFxml vehiclesListControllerFxml) {
        this.vehiclesListControllerFxml = vehiclesListControllerFxml;
    }

    public List<VehicleDto> getAllVehicles() {
        return dataService.findAllVehicles();
    }

    public void checkSelectedVehicle(VehicleDto selectedVehicle) throws IOException {
        if (selectedVehicle == null) {
            AlertUtils.showError("Aucun véhicule sélectionné");
            return;
        }
        vehiclesListControllerFxml.switchToDetailsPage(selectedVehicle);
    }

}
