package com.cannontech.web.dr.model;

import java.text.DecimalFormat;

import org.joda.time.YearMonth;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.dr.ecobee.dao.EcobeeQueryType;
import com.cannontech.dr.ecobee.model.EcobeeQueryStatistics;

public class EcobeeQueryStats {
    
    private YearMonth month;
    private int demandResponseCount;
    private int dataCollectionCount;
    private int systemCount;
    private int countsTotal;
    private int limit;
    private double demandResponsePercent;
    private double dataCollectionPercent;
    private double systemPercent;

    public EcobeeQueryStats(YearMonth month, int demandResponseCount, int dataCollectionCount, int systemCount, int limit) {
        this.month = month;
        this.demandResponseCount = demandResponseCount;
        this.dataCollectionCount = dataCollectionCount;
        this.systemCount = systemCount;
        this.limit = limit;
        createGeneratedData();
    }

    public EcobeeQueryStats(EcobeeQueryStatistics monthQueryStats, int limit) {
        month = new YearMonth(monthQueryStats.getYear(), monthQueryStats.getMonth());
        demandResponseCount = monthQueryStats.getQueryCountByType(EcobeeQueryType.DEMAND_RESPONSE);
        dataCollectionCount = monthQueryStats.getQueryCountByType(EcobeeQueryType.DATA_COLLECTION);
        systemCount = monthQueryStats.getQueryCountByType(EcobeeQueryType.SYSTEM);
        this.limit = limit;
        createGeneratedData();
    }

    private void createGeneratedData() {
        countsTotal =  demandResponseCount + dataCollectionCount + systemCount;
        demandResponsePercent = CtiUtilities.calculatePercentage(demandResponseCount, countsTotal);
        dataCollectionPercent = CtiUtilities.calculatePercentage(dataCollectionCount, countsTotal);
        systemPercent = CtiUtilities.calculatePercentage(systemCount, countsTotal);
    }

    public YearMonth getMonth() {
        return month;
    }

    public void setMonth(YearMonth month) {
        this.month = month;
    }

    public int getDemandResponseCount() {
        return demandResponseCount;
    }

    public void setDemandResponseCount(int demandResponseCount) {
        this.demandResponseCount = demandResponseCount;
    }

    public int getDataCollectionCount() {
        return dataCollectionCount;
    }

    public void setDataCollectionCount(int dataCollectionCount) {
        this.dataCollectionCount = dataCollectionCount;
    }

    public int getSystemCount() {
        return systemCount;
    }

    public void setSystemCount(int systemCount) {
        this.systemCount = systemCount;
    }

    public int getCountsTotal() {
        return countsTotal;
    }

    public void setCountsTotal(int countsTotal) {
        this.countsTotal = countsTotal;
    }
    
    public int getLimit() {
        return limit;
    }
    
    public void setLimit(int limit) {
        this.limit = limit;
    }
    
    /**
     * Returns the percentage of queries to the query limit, formatted as '###.#%'.
     * If total queries is greater than the limit, '100.0%' is returned.
     */
    public String getLimitPercent() {
        double percent = (double) countsTotal / limit;
        if (percent > 1.0) percent = 1.0;
        return new DecimalFormat("###.#%").format(percent);
    }

    public double getDemandResponsePercent() {
        return demandResponsePercent;
    }

    public void setDemandResponsePercent(double demandResponsePercent) {
        this.demandResponsePercent = demandResponsePercent;
    }

    public double getDataCollectionPercent() {
        return dataCollectionPercent;
    }

    public void setDataCollectionPercent(double dataCollectionPercent) {
        this.dataCollectionPercent = dataCollectionPercent;
    }

    public double getSystemPercent() {
        return systemPercent;
    }

    public void setSystemPercent(double systemPercent) {
        this.systemPercent = systemPercent;
    }
    
}
