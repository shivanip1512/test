package com.cannontech.common.bulk.collection.device.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CollectionActionCounts {

    private CollectionActionResult result;
    public CollectionActionCounts(CollectionActionResult result){
        this.result = result;
    }

    public double getPercentage(CollectionActionDetail detail) {
        int detailTotal = result.getDeviceCollection(detail).getDeviceCount();
        return calculate(detailTotal);
    }
    
    public double getPercentSuccess() {
        int successTotal = result.getAction().getDetails().stream().filter(
            detail -> detail.getSummary() == CollectionActionDetailSummary.SUCCESS)
                .mapToInt(detail -> result.getDeviceCollection(detail).getDeviceCount())
                .sum();
        return calculate(successTotal);
    }
    
    public double getPercentProgress() {
        int successTotal = result.getAction().getDetails().stream()
                .mapToInt(detail -> result.getDeviceCollection(detail).getDeviceCount())
                .sum();
        return calculate(successTotal);
    }

    public double getPercentCompleted() {
        int completedTotal = getCompleted();
        return calculate(completedTotal);
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
}
