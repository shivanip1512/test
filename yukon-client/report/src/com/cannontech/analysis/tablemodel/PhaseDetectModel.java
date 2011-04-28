package com.cannontech.analysis.tablemodel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.amr.phaseDetect.data.DetectedPhase;
import com.cannontech.amr.phaseDetect.data.PhaseDetectResult;
import com.cannontech.amr.phaseDetect.data.PhaseDetectVoltageReading;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.model.DeviceCollectionReportDevice;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.enums.Phase;
import com.cannontech.user.YukonUserContext;

public class PhaseDetectModel extends BareReportModelBase<PhaseDetectModel.ModelRow> implements ReportModelMetaInfo {
    
    // dependencies
    private RecentResultsCache<PhaseDetectResult> phaseDetectResultsCache = null;
    private Logger log = YukonLogManager.getLogger(PhaseDetectModel.class);
    // inputs
    private String cacheKey;

    // member variables
    private static String title = "Phase Detect Report";
    private List<ModelRow> data = new ArrayList<ModelRow>();
    private PaoLoadingService paoLoadingService;
    
    
    static public class ModelRow {
        public String mctName;
        public String routeName;
        public String phaseDetected;
        public String phaseAReading;
        public String phaseBReading;
        public String phaseCReading;
    }
    
    public void doLoadData() {
        
        PhaseDetectResult result = (PhaseDetectResult) phaseDetectResultsCache.getResult(cacheKey);
        List<SimpleDevice> masterList = new ArrayList<SimpleDevice>(result.getInputDeviceList());
        Set<SimpleDevice> failedSet = result.getFailureGroupMap().keySet();
        masterList.removeAll(failedSet);
        List<DeviceCollectionReportDevice> displayables =  paoLoadingService.getDeviceCollectionReportDevices(masterList);
        Map<Integer, Map<String, PhaseDetectVoltageReading>> deviceReadingMap = result.getDeviceReadingsMap();
        
        for(DeviceCollectionReportDevice displayableDevice : displayables){
            PhaseDetectModel.ModelRow row = new PhaseDetectModel.ModelRow();
            row.mctName = displayableDevice.getName();
            row.routeName = displayableDevice.getRoute();
            SimpleDevice device = new SimpleDevice(displayableDevice.getPaoIdentifier());
            DetectedPhase phase = result.getDetectedPhaseResult().get(device);
            if(phase == null){
                phase = DetectedPhase.UNKNOWN;
            }
            row.phaseDetected = phase.name();
            String phaseAReading = "N/A";
            String phaseBReading = "N/A";
            String phaseCReading = "N/A";
            if(!result.getTestData().isReadAfterAll() && deviceReadingMap != null){
                Map<String, PhaseDetectVoltageReading> phaseToReadingMap = deviceReadingMap.get(device.getDeviceId());
                if(phaseToReadingMap != null) {
                    PhaseDetectVoltageReading aReading = phaseToReadingMap.get(Phase.A.name());
                    PhaseDetectVoltageReading bReading = phaseToReadingMap.get(Phase.B.name());
                    PhaseDetectVoltageReading cReading = phaseToReadingMap.get(Phase.C.name());
                    if(aReading != null){
                        phaseAReading = "Initial: " + aReading.getInitial() + " Last: " + aReading.getLast() + " Delta: " + aReading.getDelta();
                    }
                    if(bReading != null){
                        phaseBReading = "Initial: " + bReading.getInitial() + " Last: " + bReading.getLast() + " Delta: " + bReading.getDelta();
                    }
                    if(cReading != null){
                        phaseCReading = "Initial: " + cReading.getInitial() + " Last: " + cReading.getLast() + " Delta: " + cReading.getDelta();
                    }
                }
            }
            row.phaseAReading = phaseAReading;
            row.phaseBReading = phaseBReading;
            row.phaseCReading = phaseCReading;
            data.add(row);
        
        }
        log.info("Report Records Collected from Database: " + masterList.size());
    }
    
    @Override
    public LinkedHashMap<String, String> getMetaInfo(YukonUserContext userContext) {
        
        LinkedHashMap<String, String> info = new LinkedHashMap<String, String>();
        return info;
    }
    
    @Override
    protected ModelRow getRow(int rowIndex) {
        return data.get(rowIndex);
    }

    @Override
    protected Class<ModelRow> getRowClass() {
        return ModelRow.class;
    }

    public int getRowCount() {
        return data.size();
    }

    public String getTitle() {
        return title;
    }
    
    public String getCacheKey() {
        return cacheKey;
    }

    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }

    @Required
    public void setPhaseDetectResultsCache(RecentResultsCache<PhaseDetectResult> phaseDetectResultsCache) {
        this.phaseDetectResultsCache = phaseDetectResultsCache;
    }

    @Required
    public void setPaoLoadingService(PaoLoadingService paoLoadingService){
        this.paoLoadingService = paoLoadingService;
    }
}