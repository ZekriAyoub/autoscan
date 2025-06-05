package scanmycar.model.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import scanmycar.model.dto.FuelType;
import scanmycar.model.dto.InspectionDto;
import scanmycar.model.dto.LastState;
import scanmycar.model.dto.VehicleDto;
import scanmycar.model.utils.PdfUtils;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

public class PdfGenerationRunnableTest {

    private InspectionDto inspectionDto;
    private VehicleDto vehicleDto;

    @BeforeEach
    void setUp() {
        inspectionDto = new InspectionDto(1, 100, LocalDate.of(2024, 4, 14), LastState.VALIDE, "1-XYZ-123");
        vehicleDto = new VehicleDto(1, "1-XYZ-123", "Peugeot", "208", 2020, "Bleu",
                FuelType.ESSENCE, 101, LastState.VALIDE);
    }

    @Test
    void testRun_shouldInvokeGeneratePdfReport() {
        try (MockedStatic<PdfUtils> mocked = mockStatic(PdfUtils.class)) {
            PdfGenerationRunnable runnable = new PdfGenerationRunnable(inspectionDto, vehicleDto);

            runnable.run();

            mocked.verify(() -> PdfUtils.generatePdfReport(vehicleDto, inspectionDto), times(1));
        }
    }
}
