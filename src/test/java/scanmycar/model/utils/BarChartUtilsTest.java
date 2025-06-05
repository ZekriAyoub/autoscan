package scanmycar.model.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import scanmycar.model.dto.InspectionDto;
import scanmycar.model.dto.LastState;
import scanmycar.model.service.DataServiceImpl;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class BarChartUtilsTest {

    private DataServiceImpl mockRepo;
    private BarChartUtils barChartUtils;

    @BeforeEach
    void setUp() throws Exception {
        mockRepo = mock(DataServiceImpl.class);
        barChartUtils = new BarChartUtils();

        Field repoField = BarChartUtils.class.getDeclaredField("dataService");
        repoField.setAccessible(true);
        repoField.set(barChartUtils, mockRepo);
    }

    @Test
    void testGetInspectionCountPerMonth_shouldReturnCorrectCounts() {
        int targetYear = 2024;

        List<InspectionDto> fakeInspections = List.of(
                new InspectionDto(1, 101, LocalDate.of(2024, 1, 10), LastState.VALIDE, "1-ABC-123"),
                new InspectionDto(2, 102, LocalDate.of(2024, 1, 20), LastState.VALIDE, "2-DEF-456"),
                new InspectionDto(3, 103, LocalDate.of(2024, 3, 5), LastState.REFUSE, "3-GHI-789")
        );

        when(mockRepo.findAllInspections()).thenReturn(fakeInspections);

        Map<String, Integer> result = barChartUtils.getInspectionCountPerMonth(targetYear);

        assertEquals(2, result.get(Month.JANUARY.getDisplayName(TextStyle.FULL, Locale.FRENCH)));
        assertEquals(1, result.get(Month.MARCH.getDisplayName(TextStyle.FULL, Locale.FRENCH)));
        assertEquals(0, result.get(Month.APRIL.getDisplayName(TextStyle.FULL, Locale.FRENCH))); // par ex.
    }

    @Test
    void testGetInspectionCountPerMonth_shouldReturnZerosWhenNoMatch() {
        int targetYear = 2025;

        List<InspectionDto> otherYearInspections = List.of(
                new InspectionDto(1, 201, LocalDate.of(2023, 5, 15), LastState.REFUSE, "3-XYZ-000")
        );

        when(mockRepo.findAllInspections()).thenReturn(otherYearInspections);

        Map<String, Integer> result = barChartUtils.getInspectionCountPerMonth(targetYear);

        for (Month m : Month.values()) {
            String label = m.getDisplayName(TextStyle.FULL, Locale.FRENCH);
            assertEquals(0, result.get(label));
        }
    }

    @Test
    void testGetInspectionCountPerMonth_shouldHandleEmptyList() {
        when(mockRepo.findAllInspections()).thenReturn(Collections.emptyList());

        Map<String, Integer> result = barChartUtils.getInspectionCountPerMonth(2024);

        for (Month m : Month.values()) {
            String label = m.getDisplayName(TextStyle.FULL, Locale.FRENCH);
            assertEquals(0, result.get(label));
        }
    }
}
