package scanmycar.model.service;

import net.sourceforge.tess4j.TesseractException;
import scanmycar.model.tesseract.OcrScanner;

import java.net.URISyntaxException;

/**
 * Service class that provides OCR functionalities using the {@link OcrScanner}.
 * Allows checking for file existence, setting the file to scan, and executing the OCR scan.
 */
public class OcrService {

    private final OcrScanner ocrScanner;

    /**
     * Constructs a new {@code OcrService} and initializes the OCR scanner.
     *
     * @throws URISyntaxException if the path to Tesseract data is invalid.
     */
    public OcrService() throws URISyntaxException {
        this.ocrScanner = new OcrScanner();
    }

    /**
     * Checks if the image file with the given name exists.
     *
     * @param imageName The name or path of the image file.
     * @return true if the file is found, false otherwise.
     */
    public boolean isFileFound(String imageName){
        return ocrScanner.isFileFound(imageName);
    }

    /**
     * Sets the image file to be scanned by the OCR engine.
     *
     * @param imageName The path to the image file.
     */
    public void setFileToScan(String imageName) {
        ocrScanner.setFileToScan(imageName);
    }

    /**
     * Executes OCR scanning on the defined image file.
     *
     * @return The text recognized from the image.
     * @throws TesseractException if an error occurs during OCR processing.
     */
    public String scan() throws TesseractException {
        return ocrScanner.scan();
    }

}
