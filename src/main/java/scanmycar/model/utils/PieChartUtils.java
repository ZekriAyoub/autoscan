package scanmycar.model.utils;

import scanmycar.model.dto.LastState;
import scanmycar.model.dto.VehicleDto;
import scanmycar.model.service.DataServiceImpl;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for calculating vehicle distribution per inspection state,
 * primarily used for populating pie charts or state summaries.
 */
public class PieChartUtils {

    /**
     * Computes how many vehicles are in each technical state (Valid, Rejected, etc.).
     *
     * @return A map of {@link LastState} to the number of vehicles in that state.
     */
    public static Map<LastState, Integer> getVehicleStateCounts() {
        DataServiceImpl dataService = new DataServiceImpl();
        List<VehicleDto> vehicles = dataService.findAllVehicles();
        Map<LastState, Integer> stateCounts = initStateCountMap();

        for (VehicleDto v : vehicles) {
            LastState state = LastState.fromDatabaseValue(v.getLastState());
            stateCounts.put(state, stateCounts.get(state) + 1);
        }

        return stateCounts;
    }


    /**
     * Initializes a map with all possible vehicle states, each count set to 0.
     *
     * @return A map of {@link LastState} initialized to 0.
     */
    private static Map<LastState, Integer> initStateCountMap() {
        Map<LastState, Integer> counts = new EnumMap<>(LastState.class);
        for (LastState state : LastState.values()) {
            counts.put(state, 0);
        }
        return counts;
    }
}
