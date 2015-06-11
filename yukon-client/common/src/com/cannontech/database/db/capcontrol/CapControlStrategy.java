package com.cannontech.database.db.capcontrol;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.LocalTime;
import org.joda.time.Minutes;

import com.cannontech.capcontrol.ControlAlgorithm;
import com.cannontech.capcontrol.ControlMethod;
import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.spring.YukonSpringHook;

/**
 * Strategy of control for a SubBus or Feeder.
 */
public class CapControlStrategy extends DBPersistent implements CTIDbChange {
    
    public static final LocalTime MAX_TIME = LocalTime.MIDNIGHT.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59);

    private Integer id = null;
    private String name = null;
    private ControlMethod controlMethod = ControlMethod.INDIVIDUAL_FEEDER;
    private int maxDailyOperation = 0;
    private boolean maxOperationDisabled = false;
    private LocalTime peakStartTime = LocalTime.MIDNIGHT;
    private LocalTime peakStopTime = MAX_TIME;
    private int controlInterval = Minutes.minutes(15).toStandardSeconds().getSeconds();
    private int minResponseTime = Minutes.minutes(15).toStandardSeconds().getSeconds();
    private int minConfirmPercent = 75;
    private int failurePercent = 25;
    
    private ControlAlgorithm algorithm = ControlAlgorithm.KVAR;
    private int controlDelayTime = 0;
    private int controlSendRetries = 0;
    private boolean integrateFlag = false;
    private int integratePeriod = 0;
    private boolean likeDayFallBack = false;
    private String endDaySettings = "";
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
            return "yukon.web.modules.capcontrol.strategies.endDay." + name();
        }

        @Override
        public Object getDatabaseRepresentation() {
            return dbString;
        }
        
    }
    
    private static final Map<DayOfWeek, Boolean> defaultPeakDays = new HashMap<>();
    
    static {
        for (DayOfWeek  day : DayOfWeek.values()) {
            defaultPeakDays.put(day, false);
        }
    }
    

    public static final String TABLE_NAME = "CapControlStrategy";
    private StrategyDao strategyDao = YukonSpringHook.getBean(StrategyDao.class);

    @Override
    public void add() throws SQLException {

        strategyDao.add(getName());
        strategyDao.save(this);
    }

    @Override
    public void update() throws SQLException {
        strategyDao.save(this);
    }

    /*
     * This looks weird because it is halfway through converting this from a db persistent
     * to properly use a Dao.
     * Ideally, i would just do this = that, but thats not legal.
     */
    @Override
    public void retrieve() throws SQLException {

        CapControlStrategy that = strategyDao.getForId(getId());

        setName(that.getName());
        setControlMethod(that.getControlMethod());
        setMaxDailyOperation(that.getMaxDailyOperation());
        setMaxOperationDisabled(that.isMaxOperationDisabled());
        setPeakStartTime(that.getPeakStartTime());
        setPeakStopTime(that.getPeakStopTime());
        setControlInterval(that.getControlInterval());
        setMinResponseTime(that.getMinResponseTime());
        setMinConfirmPercent(that.getMinConfirmPercent());
        setFailurePercent(that.getFailurePercent());
        setPeakDays(that.getPeakDays());
        setAlgorithm(that.getAlgorithm());
        setControlDelayTime(that.getControlDelayTime());
        setControlSendRetries(that.getControlSendRetries());
        setIntegrateFlag(that.isIntegrateFlag());
        setIntegratePeriod(that.getIntegratePeriod());
        setLikeDayFallBack(that.isLikeDayFallBack());
        setEndDaySettings(that.getEndDaySettings());

        setTargetSettings(that.getTargetSettings());
        setVoltageViolationSettings(that.getVoltageViolationSettings());
        setPowerFactorCorrectionSetting(that.getPowerFactorCorrectionSetting());
        setMinCommunicationPercentageSetting(that.getMinCommunicationPercentageSetting());
    }

    @Override
    public void delete() throws java.sql.SQLException {
        strategyDao.delete(getId());
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


    public boolean isMaxOperationDisabled() {
        return maxOperationDisabled;
    }

    public void setMaxOperationDisabled(boolean maxOperationDisabled) {
        this.maxOperationDisabled = maxOperationDisabled;
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
    
    public String getEndDaySettings() {
        return endDaySettings;
    }

    public void setEndDaySettings(String endDaySettings) {
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

    public boolean isKVarAlgorithm() {
        return algorithm == ControlAlgorithm.KVAR;
    }

    public boolean isPFAlgorithm() {
        return algorithm == ControlAlgorithm.PFACTOR_KW_KVAR;
    }

    public boolean isVoltVar() {
        return algorithm == ControlAlgorithm.MULTI_VOLT_VAR;
    }

    public boolean isIvvc() {
        return algorithm == ControlAlgorithm.INTEGRATED_VOLT_VAR;
    }

    //TODO Remove after deleting JSF strategyEditor.jsp
    public boolean isBusOptimized() {
        return controlMethod == ControlMethod.BUSOPTIMIZED_FEEDER;
    }

    public boolean isVoltStrat() {
        if (algorithm == ControlAlgorithm.MULTI_VOLT)
            return true;
        else if (algorithm == ControlAlgorithm.VOLTS)
            return true;
        else
            return false;
    }

    public boolean isTimeOfDay() {
        return controlMethod == ControlMethod.TIME_OF_DAY;
    }

    /**
     * Generates a DBChange msg.
     */
    @Override
    public DBChangeMsg[] getDBChangeMsgs(DbChangeType dbChangeType) {

        // add the basic change method
        DBChangeMsg dbChange =
            new DBChangeMsg(getId().intValue(), DBChangeMsg.CHANGE_CBC_STRATEGY_DB, DBChangeMsg.CAT_CBC_STRATEGY,
                dbChangeType);

        return new DBChangeMsg[] { dbChange };
    }

    public Map<DayOfWeek, Boolean> getPeakDays() {
        return peakDays;
    }

    public void setPeakDays(Map<DayOfWeek, Boolean> peakDays) {
        this.peakDays = peakDays;
    }

}