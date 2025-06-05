package scanmycar.model.utils;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.junit.jupiter.api.*;
import scanmycar.model.dto.*;
import scanmycar.model.service.DataServiceImpl;

import java.io.File;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PdfUtilsTest {
    // Dans PdfUtils
    private static DataServiceImpl dataService = new DataServiceImpl(); // <-- ce champ


    private Path outputPath;

    private static final VehicleDto MOCK_VEHICLE = new VehicleDto(
            1, "TEST-123", "Toyota", "Corolla", 2020, "Bleu",
            FuelType.ESSENCE, 101, LastState.VALIDE);

    private static final InspectionDto MOCK_INSPECTION = new InspectionDto(
            10, 1, LocalDate.of(2024, 1, 10), LastState.VALIDE, "TEST-123");

    @BeforeEach
    void setup() throws Exception {
        // Générer un path unique
        String fileName = "Vehicle_Report_TEST-" + UUID.randomUUID() + ".pdf";
        outputPath = Path.of(System.getProperty("user.home"), "Documents", "Reports", fileName);

        // Injecter un mock dans PdfUtils
        var dataServiceField = PdfUtils.class.getDeclaredField("dataService"); // s'il est statique
        dataServiceField.setAccessible(true);

        DataServiceImpl mockService = mock(DataServiceImpl.class);
        when(mockService.findOwnerById(anyInt()))
                .thenReturn(Optional.of(new OwnerDto(1, "Test Owner", "1 rue Test", "test@example.com")));

        dataServiceField.set(null, mockService); // <- null car c’est une méthode statique
    }


    @AfterEach
    void cleanup() throws Exception {
        Files.deleteIfExists(outputPath);
    }

    @Test
    @Order(1)
    void testGeneratePdfReport_shouldCreateFileWithoutError() {
        assertDoesNotThrow(() -> PdfUtils.generatePdfReport(MOCK_VEHICLE, MOCK_INSPECTION));
        assertTrue(Files.exists(Path.of(System.getProperty("user.home"), "Documents", "Reports", "Vehicle_Report_TEST-123.pdf")));
    }

    @Test
    @Order(2)
    void testGeneratePdfReport_shouldContainValidContent() throws Exception {
        PdfUtils.generatePdfReport(MOCK_VEHICLE, MOCK_INSPECTION);
        File pdfFile = new File(System.getProperty("user.home") + "/Documents/Reports/Vehicle_Report_TEST-123.pdf");
        assertTrue(pdfFile.exists());

        try (PdfReader reader = new PdfReader(pdfFile)) {
            PdfDocument pdf = new PdfDocument(reader);
            assertEquals(1, pdf.getNumberOfPages());
            pdf.close();
        }
    }

    @Test
    @Order(3)
    void testAddImage_shouldNotThrow_whenImageIsMissing() {
        Path testPdfPath = Path.of(outputPath.toString().replace(".pdf", "_noimg.pdf"));

        assertDoesNotThrow(() -> {
            try (PdfWriter writer = new PdfWriter(testPdfPath.toFile());
                 PdfDocument pdfDoc = new PdfDocument(writer);
                 Document doc = new Document(pdfDoc)) {

                doc.add(new Paragraph("Test fallback content"));

                var method = PdfUtils.class.getDeclaredMethod("addImage", Document.class);
                method.setAccessible(true);
                method.invoke(null, doc);
            }

            assertTrue(Files.exists(testPdfPath));
            Files.deleteIfExists(testPdfPath);
        });
    }

    @Test
    @Order(4)
    void testGeneratePdfReport_shouldNotCrashWithRefuseState() {
        VehicleDto refusedVehicle = new VehicleDto(
                2, "REF-456", "Renault", "Clio", 2015, "Rouge",
                FuelType.DIESEL, 202, LastState.REFUSE);

        InspectionDto refusedInspection = new InspectionDto(
                20, 3, LocalDate.now(), LastState.REFUSE, "REF-456");

        assertDoesNotThrow(() -> PdfUtils.generatePdfReport(refusedVehicle, refusedInspection));

        File file = new File(System.getProperty("user.home") + "/Documents/Reports/Vehicle_Report_REF-456.pdf");
        assertTrue(file.exists());
        file.delete();
    }
}
