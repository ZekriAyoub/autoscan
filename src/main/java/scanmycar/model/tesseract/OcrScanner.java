package scanmycar.model.tesseract;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import scanmycar.model.utils.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * OcrScanner handles Optical Character Recognition (OCR) operations using Tess4J (Tesseract).
 * It loads an image, optionally converts it to black & white, and extracts any readable text.
 */
public class OcrScanner {

    private Path fileToScan;
    private final Tesseract tesseract;

    /**
     * Initializes the Tesseract engine with the French trained data.
     *
     * @throws URISyntaxException if the trained data path cannot be resolved
     */
    public OcrScanner() throws URISyntaxException {
        tesseract = new Tesseract();
        tesseract.setDatapath(FileUtils.getTrainedDataDirectory());
        tesseract.setLanguage("fra");
    }

    /**
     * Checks if the image file exists using FileUtils.
     *
     * @param imageName the name or path of the image
     * @return true if the file exists, false otherwise
     */
    public boolean isFileFound(String imageName) {
        return FileUtils.isFileFound(imageName);
    }

    /**
     * Sets the path to the image file to be scanned.
     *
     * @param imageName the name or full path of the image file
     */
    public void setFileToScan(String imageName) {
        fileToScan = Paths.get(imageName);
    }

    /**
     * Performs OCR on the image file previously set.
     * Converts the image to black and white for better accuracy.
     *
     * @return the extracted text or "Erreur inconnue." if an error occurs
     * @throws TesseractException if OCR processing fails
     * @throws IllegalStateException if no file has been set before scanning
     */
    public String scan() throws TesseractException {
        if (fileToScan == null) {
            throw new IllegalStateException("No file has been set for scanning.");
        }

        try {
            BufferedImage originalImage = ImageIO.read(fileToScan.toFile());
            BufferedImage bwImage = FileUtils.convertToBlackAndWhite(originalImage);
            return tesseract.doOCR(bwImage);
        } catch (Exception e) {
            System.err.println("Unexpected error during OCR: " + e.getMessage());
            return "Erreur inconnue.";
        }
    }
}
