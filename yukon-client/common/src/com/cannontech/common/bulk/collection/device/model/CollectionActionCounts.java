package com.cannontech.common.bulk.collection.device.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.stream.Collectors;

import static com.cannontech.common.bulk.collection.device.model.CollectionActionDetailSummary.*;

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
    
    public double getPercentProgress() {
        int successTotal = result.getAction().getDetails().stream()
                .mapToInt(detail -> result.getDeviceCollection(detail).getDeviceCount())
                .sum();
        return calculate(successTotal);
    }

    public double getPercentCompleted() {
        return calculate(getCompleted());
    }

    public int getCompleted() {
        int completedTotal = result.getAction().getDetails().stream()
                .mapToInt(detail -> result.getDeviceCollection(detail).getDeviceCount())
                .sum();
        return completedTotal;
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
        return new BigDecimal(subset).divide(new BigDecimal(total), 2, RoundingMode.HALF_EVEN).multiply(
            new BigDecimal(100)).doubleValue();
    }

    public double getPercentage(CollectionActionDetail detail) {
        int detailTotal = result.getDeviceCollection(detail).getDeviceCount();
        return calculate(detailTotal);
    }
    
    public Map<CollectionActionDetail, Double> getPercentages() {
        return result.getAction().getDetails().stream()
                .collect(Collectors.toMap(detail -> detail, detail -> getPercentage(detail)));
    }
}
