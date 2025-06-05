package scanmycar.model.service;

import scanmycar.model.dto.LastState;
import scanmycar.model.utils.PieChartUtils;

import java.util.Map;

/**
 * Service class providing vehicle state distribution data for use in pie charts.
 * It delegates the logic to {@link PieChartUtils}.
 */
public class PieChartService {

    /**
     * Returns a map containing the number of vehicles in each {@link LastState}.
     *
     * @return A map with {@code LastState} as keys and their respective counts as values.
     */
    public static Map<LastState, Integer> getVehicleStateCounts() {
        return PieChartUtils.getVehicleStateCounts();
    }
}
