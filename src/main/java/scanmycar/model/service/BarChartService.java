package scanmycar.model.service;

import scanmycar.model.utils.BarChartUtils;

import java.util.Map;

/**
 * Service class that delegates the logic for calculating the number of inspections
 * per month to {@link BarChartUtils}. Intended to be used by the presenter or controller
 * layers to retrieve chart-ready data.
 */
public class BarChartService {
    private final BarChartUtils barChartUtils;

    /**
     * Constructs the service with a new instance of BarChartUtils.
     */
    public BarChartService() {
        this.barChartUtils = new BarChartUtils();
    }

    /**
     * Returns the number of inspections conducted in each month for a given year.
     *
     * @param year The year to filter inspections by.
     * @return A map with month names as keys and inspection counts as values.
     */
    public Map<String, Integer> getInspectionCountPerMonth(int year) {
        return barChartUtils.getInspectionCountPerMonth(year);
    }
}
