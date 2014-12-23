package com.cannontech.core.dao;

import java.util.List;

import org.joda.time.Interval;
import org.joda.time.Period;

import com.cannontech.common.bulk.model.AdaStatus;
import com.cannontech.common.bulk.model.Analysis;
import com.cannontech.common.bulk.model.DeviceArchiveData;
import com.cannontech.common.bulk.model.DevicePointValuesHolder;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;

public interface ArchiveDataAnalysisDao {
    
    /**
     * Sets up a new Analysis by populating the Analysis and Slots tables.
     * This step must be completed prior to inserting any slot data for a given Analysis.
     */
    public int createNewAnalysis(BuiltInAttribute attribute, 
                                 Period intervalPeriod, 
                                 boolean excludeBadPointQualities, 
                                 Interval dateTimeRange);
    
    /**
     * Inserts slot values for one device in a given Analysis.
     */
    public void insertSlotValues(PaoIdentifier paoIdentifier, Analysis analysis, int pointId, boolean excludeBadPointQualities);
    
    /**
     * Retrieves all slot values for all devices in a given analysis. Returned as a list of
     * DeviceArchiveData, each of which contains all slot values for a single device.
     * Use to collect results after analysis is complete.
     */
    public List<DeviceArchiveData> getSlotValues(int analysisId);
    
    /**
     * Retrieves the Analysis object for a given analysisId.
     */
    public Analysis getAnalysisById(int analysisId);
    
    /**
     * Retrieves a list of all Analyses which don't have an AnalysisStatus of 'DELETED'
     */
    public List<Analysis> getAllNotDeletedAnalyses();
    
    /**
     * Deletes an analysis and all associated slot and read status information.
     */
    public void deleteAnalysis(int analysisId);
    
    /**
     * Retrieves the number of devices in a given analysis.
     */
    public int getNumberOfDevicesInAnalysis(int analysisId);
    
    /**
     * Retrieves the list of deviceIds associated with a given analysis.
     */
    public List<PaoIdentifier> getRelevantDeviceIds(int analysisId);
    
    /**
     * Retrieves the displayablePao for each device in the analysis and the list of
     * point values associated with each device, wrapped in DevicePointValuesHolders.
     */
    public List<DevicePointValuesHolder> getAnalysisPointValues(int analysisId);
    
    /**
     * Updates the status and status id of an Analysis. StatusId may be null when
     * the status is COMPLETE or DELETED.
     */
    public void updateStatus(int analysisId, AdaStatus status, String statusId);
    
}
