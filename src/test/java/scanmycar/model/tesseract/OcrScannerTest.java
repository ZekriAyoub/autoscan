package scanmycar.model.tesseract;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class OcrScannerTest {

    private OcrScanner ocrScanner;

    @BeforeEach
    public void setUp() throws URISyntaxException {
        ocrScanner = new OcrScanner();
    }

    @Test
    public void testIsFileFound_whenFileExists() {
        String imagePath = "src/test/resources/sample_plate.png";
        assertTrue(ocrScanner.isFileFound(imagePath));
    }

    @Test
    public void testIsFileFound_whenFileDoesNotExist() {
        String imagePath = "src/test/resources/non_existing.png";
        assertFalse(ocrScanner.isFileFound(imagePath));
    }

    @Test
    public void testSetFileToScan_andScan_withValidImage() throws Exception {
        String imagePath = "src/test/resources/sample_plate.png";
        if (!Files.exists(Paths.get(imagePath))) {
            System.out.println("WARNING: Test image not found at: " + imagePath);
            return;
        }
        ocrScanner.setFileToScan(imagePath);
        String result = ocrScanner.scan();
        assertNotNull(result);
        assertEquals("1-DAB-000",result.trim());
        assertFalse(result.isEmpty());
    }

    @Test
    public void testScan_withoutSettingFile_shouldThrowException() {
        assertThrows(IllegalStateException.class, () -> ocrScanner.scan());
    }

    @Test
    public void testScan_withInvalidImage_shouldReturnErrorMessage() throws Exception {
        String fakeImagePath = "src/test/resources/fake_image.png";
        Path fakePath = Paths.get(fakeImagePath);
        Files.writeString(fakePath, "This is not an image.");

        try {
            ocrScanner.setFileToScan(fakeImagePath);
            String result = ocrScanner.scan();

            assertEquals("Erreur inconnue.", result);
        } finally {
            Files.deleteIfExists(fakePath);
        }
    }

}
