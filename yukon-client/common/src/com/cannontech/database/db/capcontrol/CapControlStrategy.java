package com.cannontech.database.db.capcontrol;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.LocalTime;
import org.joda.time.Minutes;

import com.cannontech.capcontrol.ControlAlgorithm;
import com.cannontech.capcontrol.ControlMethod;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;

/**
 * Strategy of control for a SubBus or Feeder.
 */
public class CapControlStrategy {
    
    public static final LocalTime MAX_TIME = LocalTime.MIDNIGHT;

    private Integer id = null;
    private String name = null;
    private ControlMethod controlMethod = ControlMethod.INDIVIDUAL_FEEDER;
    private Integer maxDailyOperation = 0;
    private boolean maxOperationEnabled = false;
    private LocalTime peakStartTime = LocalTime.MIDNIGHT;
    private LocalTime peakStopTime = MAX_TIME;
    private int controlInterval = Minutes.minutes(15).toStandardSeconds().getSeconds();
    private int minResponseTime = Minutes.minutes(15).toStandardSeconds().getSeconds();
    private Integer minConfirmPercent = 75;
    private Integer failurePercent = 25;
    
    private ControlAlgorithm algorithm = ControlAlgorithm.KVAR;
    private int controlDelayTime = 0;
    private Integer controlSendRetries = 0;
    private boolean integrateFlag = false;
    private int integratePeriod = 0;
    private boolean likeDayFallBack = false;
    private EndDaySetting endDaySettings = EndDaySetting.NONE;
    private double maxDeltaVoltage = 10.0;
    private Map<TargetSettingType, PeakTargetSetting> targetSettings = StrategyPeakSettingsHelper.getAllSettingDefaults();
    private Map<VoltViolationType, VoltageViolationSetting> voltageViolationSettings =
        VoltageViolationSettingsHelper.getVoltageViolationDefaults();
    private PowerFactorCorrectionSetting powerFactorCorrectionSetting = new PowerFactorCorrectionSetting();
    private CommReportingPercentageSetting minCommunicationPercentageSetting = new CommReportingPercentageSetting();
    
    private Map<DayOfWeek, Boolean> peakDays = new HashMap<>(defaultPeakDays);
    
    public static enum DayOfWeek implements DisplayableEnum {
        SUNDAY,
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY,
        HOLIDAY;

        public boolean isDisplay() {
            return this != HOLIDAY;
        }

        @Override
        public String getFormatKey() {
            return "yukon.common.day." + name() + ".short";
        }
        
    }
    
    public static enum EndDaySetting implements DisplayableEnum, DatabaseRepresentationSource {
        NONE("(none)"),
        TRIP("Trip"),
        CLOSE("Close");
        
        private String dbString;
        
        EndDaySetting(String dbString) {
            this.dbString = dbString;
        }

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.capcontrol.strategy.endDaySetting." + name();
        }

        @Override
        public Object getDatabaseRepresentation() {
            return dbString;
        }

        public static EndDaySetting from(String dbString) {
            for (EndDaySetting setting : values()) {
                if (setting.dbString.equals(dbString)) {
                    return setting;
                }
            }
            return null;
        }
        
    }
    
    private static final Map<DayOfWeek, Boolean> defaultPeakDays = new HashMap<>();
    
    static {
        for (DayOfWeek  day : DayOfWeek.values()) {
            defaultPeakDays.put(day, false);
        }
    }
    
    public Integer getControlInterval() {
        return controlInterval;
    }

    public ControlMethod getControlMethod() {
        return controlMethod;
    }

    public ControlAlgorithm getAlgorithm() {
        return algorithm;
    }

    public Integer getFailurePercent() {
        return failurePercent;
    }

    public Integer getMaxDailyOperation() {
        return maxDailyOperation;
    }


    public boolean isMaxOperationEnabled() {
        return maxOperationEnabled;
    }

    public void setMaxOperationEnabled(boolean maxOperationEnabled) {
        this.maxOperationEnabled = maxOperationEnabled;
    }

    public Integer getMinConfirmPercent() {
        return minConfirmPercent;
    }

    public Integer getMinResponseTime() {
        return minResponseTime;
    }

    public LocalTime getPeakStartTime() {
        return peakStartTime;
    }

    public LocalTime getPeakStopTime() {
        return peakStopTime;
    }

    public Integer getControlDelayTime() {
        return controlDelayTime;
    }

    public Integer getControlSendRetries() {
        return controlSendRetries;
    }

    public void setControlInterval(Integer newValue) {
        this.controlInterval = newValue;
    }

    public void setControlMethod(ControlMethod newControlMethod) {
        controlMethod = newControlMethod;
    }

    public void setAlgorithm(ControlAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public void setFailurePercent(Integer newValue) {
        this.failurePercent = newValue;
    }

    public void setMaxDailyOperation(Integer newValue) {
        this.maxDailyOperation = newValue;
    }

    public void setMinConfirmPercent(Integer newValue) {
        this.minConfirmPercent = newValue;
    }

    public void setMinResponseTime(Integer newValue) {
        this.minResponseTime = newValue;
    }

    public void setPeakStartTime(LocalTime peakStart) {
        peakStartTime = peakStart;
    }

    public void setPeakStopTime(LocalTime localTime) {
        peakStopTime = localTime;
    }

    public void setControlDelayTime(Integer newValue) {
        controlDelayTime = newValue;
    }

    public void setControlSendRetries(Integer newTime) {
        controlSendRetries = newTime;
    }

    @Override
    public String toString() {
        return getName();
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Integer integer) {
        id = integer;
    }

    public void setName(String string) {
        name = string;
    }

    public Integer getIntegratePeriod() {
        return integratePeriod;
    }

    public void setIntegratePeriod(Integer integratedPeriod) {
        this.integratePeriod = integratedPeriod;
    }

    public boolean isIntegrateFlag() {
        return integrateFlag;
    }

    public void setIntegrateFlag(boolean integratedFlad) {
        this.integrateFlag = integratedFlad;
    }

    public boolean isLikeDayFallBack() {
        return likeDayFallBack;
    }
    
    public void setLikeDayFallBack(boolean fallBackFlag) {
        this.likeDayFallBack = fallBackFlag;
    }
    
    public EndDaySetting getEndDaySettings() {
        return endDaySettings;
    }

    public void setEndDaySettings(EndDaySetting endDaySettings) {
        this.endDaySettings = endDaySettings;
    }

    public Map<TargetSettingType, PeakTargetSetting> getTargetSettings() {
        return targetSettings;
    }
    
    public void setTargetSettings(Map<TargetSettingType, PeakTargetSetting> newTargetSettings) {
        this.targetSettings = newTargetSettings;
    }
    
    public Map<VoltViolationType, VoltageViolationSetting> getVoltageViolationSettings() {
        return voltageViolationSettings;
    }

    public void setVoltageViolationSettings(Map<VoltViolationType, VoltageViolationSetting> voltageViolationSettings) {
        this.voltageViolationSettings = voltageViolationSettings;
    }

    public PowerFactorCorrectionSetting getPowerFactorCorrectionSetting() {
        return powerFactorCorrectionSetting;
    }

    public void setPowerFactorCorrectionSetting(PowerFactorCorrectionSetting powerFactorCorrectionSetting) {
        this.powerFactorCorrectionSetting = powerFactorCorrectionSetting;
    }

    public CommReportingPercentageSetting getMinCommunicationPercentageSetting() {
        return minCommunicationPercentageSetting;
    }

    public void setMinCommunicationPercentageSetting(CommReportingPercentageSetting minCommunicationPercentageSetting) {
        this.minCommunicationPercentageSetting = minCommunicationPercentageSetting;
    }

    public boolean isIvvc() {
        return algorithm == ControlAlgorithm.INTEGRATED_VOLT_VAR;
    }
    
    public boolean isMultiVoltVar() {
        return algorithm == ControlAlgorithm.MULTI_VOLT_VAR;
    }
    
    public boolean isMultiVolt() {
        return algorithm == ControlAlgorithm.MULTI_VOLT;
    }

    public boolean isTimeOfDay() {
        return controlMethod == ControlMethod.TIME_OF_DAY;
    }

    public Map<DayOfWeek, Boolean> getPeakDays() {
        return peakDays;
    }

    public void setPeakDays(Map<DayOfWeek, Boolean> peakDays) {
        this.peakDays = peakDays;
    }

    public double getMaxDeltaVoltage() {
        return maxDeltaVoltage;
    }

    public void setMaxDeltaVoltage(double maxDeltaVoltage) {
        this.maxDeltaVoltage = maxDeltaVoltage;
    }

}