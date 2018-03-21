package com.cannontech.common.bulk.collection.device.model;

import static com.cannontech.common.bulk.collection.device.model.CollectionActionDetailSummary.FAILURE;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionDetailSummary.NOT_ATTEMPTED;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionDetailSummary.SUCCESS;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.cannontech.common.device.model.SimpleDevice;

/**
 * Calculates counts for results. Used to display % on the progress report page and figure out counts to
 * display in alerts.
 */
public class CollectionActionCounts {

    private CollectionActionResult result;
    public CollectionActionCounts(CollectionActionResult result){
        this.result = result;
    }
  
    public int getNotAttemptedCount() {
        return getCount(NOT_ATTEMPTED);
    }
    
    public int getSuccessCount() {
        return getCount(SUCCESS);
    }
    
    public int getFailedCount() {
        return getCount(FAILURE);
    }
    
    public int getTotalCount() {
        return result.getInputs().getCollection().getDeviceCount();
    }
    
    private int getCount(CollectionActionDetailSummary summary) {
        return result.getAction().getDetails().stream()
                .filter(detail -> detail.getSummary() == summary)
                .mapToInt(detail -> result.getDeviceCollection(detail).getDeviceCount())
                .sum(); 
    }
    
    public double getPercentSuccess() {
        return calculate(getSuccessCount());
    }
    
    // Used by JS
    public double getPercentCompleted() {
        return calculate(getCompleted());
    }

    // Used by JS
    public int getCompleted() {
        Set<SimpleDevice> devices = new HashSet<>();
        result.getAction().getDetails().forEach(detail -> {
            devices.addAll(result.getDeviceCollection(detail).getDeviceList());
        });
        return devices.size();
    }
    
    public int getNotCompleted() {
        int total = result.getInputs().getCollection().getDeviceCount();
        return total - getCompleted();
    }
    
    /**
     * Caclulates %
     * @param subset - numerator
     * @return %
     */
    private double calculate(int subset){
        int total = result.getInputs().getCollection().getDeviceCount();
        if (total > 0) {
            return new BigDecimal(subset).divide(new BigDecimal(total), 2, RoundingMode.HALF_EVEN).multiply(
                new BigDecimal(100)).doubleValue();
        }
        return 0;
    }

    public double getPercentageCompleted(CollectionActionDetail detail) {
        int detailTotal = result.getDeviceCollection(detail).getDeviceCount();
        int completed = getCompleted();
        if (detailTotal > 0 && completed > 0) {
            return new BigDecimal(detailTotal).divide(new BigDecimal(getCompleted()), 2,
                RoundingMode.HALF_EVEN).multiply(new BigDecimal(100)).doubleValue();
        }
        return 0;
    }
    
    // Used by JS
    public Map<CollectionActionDetail, Double> getPercentages() {
        return result.getAction().getDetails().stream()
                .collect(Collectors.toMap(detail -> detail, detail -> getPercentageCompleted(detail)));
    }
}
