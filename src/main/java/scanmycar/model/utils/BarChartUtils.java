package scanmycar.model.utils;

import scanmycar.model.dto.InspectionDto;
import scanmycar.model.service.DataServiceImpl;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * BarChartUtils provides utility methods for processing inspection data
 * for display in bar charts (e.g., inspections per month).
 */
public class BarChartUtils {

    private final DataServiceImpl dataService;

    /**
     * Constructs a new BarChartUtils using a default DataServiceImpl.
     */
    public BarChartUtils() {
        this.dataService = new DataServiceImpl();
    }

    /**
     * Returns a map of months (in French) with the number of inspections performed in each,
     * for the specified year.
     *
     * @param year the year to filter inspections
     * @return a map with month names as keys and counts as values
     */
    public Map<String, Integer> getInspectionCountPerMonth(int year) {
        Map<String, Integer> result = new LinkedHashMap<>();

        // Initialize map with all months set to 0
        for (Month month : Month.values()) {
            result.put(month.getDisplayName(TextStyle.FULL, Locale.FRENCH), 0);
        }

        // Filter inspections for the given year
        List<InspectionDto> inspections = dataService.findAllInspections().stream()
                .filter(i -> i.getDate().getYear() == year)
                .toList();

        // Count inspections per month
        for (InspectionDto inspection : inspections) {
            Month month = inspection.getDate().getMonth();
            String monthName = month.getDisplayName(TextStyle.FULL, Locale.FRENCH);
            result.put(monthName, result.getOrDefault(monthName, 0) + 1);
        }

        return result;
    }
}
