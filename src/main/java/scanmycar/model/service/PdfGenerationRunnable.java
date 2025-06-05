package scanmycar.model.service;

import scanmycar.model.dto.InspectionDto;
import scanmycar.model.dto.VehicleDto;
import scanmycar.model.utils.PdfUtils;

/**
 * Runnable class responsible for generating a PDF report for a vehicle inspection.
 * This allows the PDF generation process to run in a separate thread.
 */
public class PdfGenerationRunnable implements Runnable {
    private final InspectionDto inspection;
    private final VehicleDto vehicle;

    /**
     * Constructs a new {@code PdfGenerationRunnable} with the given vehicle and inspection data.
     *
     * @param inspection The inspection data to include in the PDF.
     * @param vehicle    The vehicle associated with the inspection.
     */
    public PdfGenerationRunnable(InspectionDto inspection, VehicleDto vehicle) {
        this.inspection = inspection;
        this.vehicle = vehicle;
    }

    /**
     * Executes the PDF generation using {@link PdfUtils}.
     */
    @Override
    public void run() {
        PdfUtils.generatePdfReport(vehicle, inspection);
    }
}
