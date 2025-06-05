package scanmycar.model.utils;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import scanmycar.model.dto.InspectionDto;
import scanmycar.model.dto.VehicleDto;
import scanmycar.model.service.DataServiceImpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.Objects;

/**
 * Utility class for generating PDF reports related to vehicle inspections.
 * Uses iText library to generate and format the document.
 */
public class PdfUtils {

    private static final String DIRECTORY_PATH = System.getProperty("user.home") + "\\Documents\\Reports";
    private static final String IMAGE_PATH = "/images/certificat.jpg";

    /**
     * Static field used for testability, allowing mock injection.
     */
    static DataServiceImpl dataService = new DataServiceImpl();

    /**
     * Generates a detailed PDF report for a given vehicle and inspection.
     *
     * @param vehicle    The vehicle to include in the report.
     * @param inspection The inspection related to the vehicle.
     */
    public static void generatePdfReport(VehicleDto vehicle, InspectionDto inspection) {
        createDirectoryIfNeeded(DIRECTORY_PATH);
        String filePath = generateFilePath(vehicle.getLicensePlate());

        try {
            PdfDocument pdf = createPdfDocument(filePath);
            Document document = new Document(pdf);

            addTitle(document);
            addVehicleInfoTable(document, vehicle, inspection);
            addImage(document);

            document.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ensures the output directory exists.
     *
     * @param directoryPath Path to the target directory.
     */
    private static void createDirectoryIfNeeded(String directoryPath) {
        new File(directoryPath).mkdirs();
    }

    /**
     * Builds the file path where the report will be saved.
     *
     * @param licensePlate The license plate used in the file name.
     * @return A full path for the PDF file.
     */
    private static String generateFilePath(String licensePlate) {
        return DIRECTORY_PATH + "/Vehicle_Report_" + licensePlate + ".pdf";
    }

    /**
     * Creates a new PdfDocument for writing.
     *
     * @param filePath Path where the PDF will be written.
     * @return A new PdfDocument instance.
     * @throws FileNotFoundException If the file cannot be created.
     */
    private static PdfDocument createPdfDocument(String filePath) throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(filePath);
        return new PdfDocument(writer);
    }

    /**
     * Adds a bold title at the beginning of the PDF.
     *
     * @param document The document to modify.
     */
    private static void addTitle(Document document) {
        document.add(new Paragraph("🔧 Contrôle Technique - Rapport\n")
                .setBold()
                .setFontSize(20));
    }

    /**
     * Populates the document with vehicle and inspection details in a table format.
     *
     * @param document   The PDF document being written.
     * @param vehicle    The vehicle data to display.
     * @param inspection The inspection data to display.
     */
    private static void addVehicleInfoTable(Document document, VehicleDto vehicle, InspectionDto inspection) {
        float[] columnWidths = {150, 300};
        Table table = new Table(columnWidths);

        var owner = dataService.findOwnerById(vehicle.getOwnerId());

        addRow(table, "Plaque d'immatriculation :", vehicle.getLicensePlate());
        addRow(table, "Marque :", vehicle.getBrand());
        addRow(table, "Modèle :", vehicle.getModel());
        addRow(table, "Année :", String.valueOf(vehicle.getYear()));
        addRow(table, "Couleur :", vehicle.getColor());
        addRow(table, "Type de carburant :", vehicle.getFuelType());
        owner.ifPresent(o -> addRow(table, "Propriétaire :", o.getFullName()));
        addRow(table, "État du véhicule :", String.valueOf(inspection.getState()));
        addRow(table, "Inspecteur :", String.valueOf(inspection.getAgent()));
        addRow(table, "Date du contrôle :", String.valueOf(inspection.getDate()));

        if (Objects.equals(inspection.getState().toDatabaseValue(), "Refusé")) {
            addRow(table, "Délai pour repasser le contrôle :", "2 semaines au plus tard");
        }

        document.add(table);
    }

    /**
     * Adds a row to the vehicle/inspection information table.
     *
     * @param table The table where the row will be added.
     * @param label The label or field name.
     * @param value The associated value.
     */
    private static void addRow(Table table, String label, String value) {
        table.addCell(label);
        table.addCell(value);
    }

    /**
     * Adds an image (e.g., certification logo) to the PDF, positioned at the top-left.
     *
     * @param document The document to insert the image into.
     */
    public static void addImage(Document document) {
        try {
            var resource = PdfUtils.class.getResource(IMAGE_PATH);
            if (resource == null) {
                System.err.println("Image not found: " + IMAGE_PATH);
                return;
            }

            String imageLocation = resource.toExternalForm();
            Image image = new Image(ImageDataFactory.create(imageLocation))
                    .setHeight(150).setWidth(150).setFixedPosition(10, 10);
            document.add(image);
        } catch (MalformedURLException e) {
            System.err.println("Malformed image URL: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Image load error: " + e.getMessage());
        }
    }
}
