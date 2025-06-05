package scanmycar.presenter;

import scanmycar.model.service.DataServiceImpl;
import scanmycar.model.service.OcrService;
import scanmycar.view.utils.AlertUtils;
import scanmycar.view.HomeControllerFxml;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class HomePresenter {
    private final DataServiceImpl dataService;
    private List<String> listePlate;

    private final OcrService ocrService;

    private HomeControllerFxml homeControllerFxml;
    private File selectedFile;

    public HomePresenter() throws URISyntaxException {
        this.dataService = new DataServiceImpl();
        this.ocrService = new OcrService();
        this.listePlate = dataService.findAllPlates();
    }

    public void handleBrowseFile(File selectedFile) {
        try {
            this.selectedFile = selectedFile;
            if (selectedFile != null && ocrService.isFileFound(selectedFile.getAbsolutePath())) {
                AlertUtils.showInfo("Scan en cours ...");
                ocrService.setFileToScan(selectedFile.getAbsolutePath());
                homeControllerFxml.setResultScan(ocrService.scan());
                homeControllerFxml.setPreviewImageView(selectedFile);
            } else {
                AlertUtils.showError("Fichier invalide ou non trouvé. Veuillez réessayer.");
            }
        } catch (Exception e) {
            System.err.println("Une erreur s'est produite : " + e.getMessage());
        }
    }

    private boolean isValidPlate(String plaque) {
        if (plaque == null || plaque.isEmpty()) {
            AlertUtils.showError("Plaque invalide. La plaque ne peut pas être vide.");
            return false;
        }
        if (plaque.length() > 10) {
            AlertUtils.showError("Plaque invalide. Doit compter au plus 8 caractères. Réessayez.");
            return false;
        }
        if (!plaque.matches("[A-Za-z0-9-]+")) {
            AlertUtils.showError("Plaque invalide. Seuls les lettres, chiffres et tirets sont autorisés.");
            return false;
        }
        if (plaque.contains(" ")) {
            AlertUtils.showError("Plaque invalide. Aucun espace autorisé.");
            return false;
        }
        return true;
    }

    private boolean isPlateInDatabase(String plaque) {
        this.listePlate = dataService.findAllPlates();
        return listePlate.contains(plaque);
    }

    public void check(String plaque) throws IOException {
        if (isValidPlate(plaque)) {
            if (!isPlateInDatabase(plaque)) {
                homeControllerFxml.goToPageForm();
            } else {
                homeControllerFxml.goToPageDetails();
            }
        }
    }

    public void setHomeControllerFxml(HomeControllerFxml homeControllerFxml) {
        this.homeControllerFxml = homeControllerFxml;
    }
}