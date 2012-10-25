package com.cannontech.database.db.capcontrol;

import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.capcontrol.ControlAlgorithm;
import com.cannontech.capcontrol.ControlMethod;
import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.spring.YukonSpringHook;

/**
 * Strategy of control for a SubBus or Feeder.
 */
public class CapControlStrategy extends DBPersistent implements CTIDbChange {
	
	private Integer strategyID = null;
	private String strategyName = null;
	private ControlMethod controlMethod = ControlMethod.INDIVIDUAL_FEEDER;
	private Integer maxDailyOperation = new Integer(0);
	private Character maxOperationDisableFlag = new Character('N');
	private Integer peakStartTime = new Integer(0);
	private Integer peakStopTime = new Integer(86400);  //24:00
	private Integer controlInterval = new Integer(900);
	private Integer minResponseTime = new Integer(900);
	private Integer minConfirmPercent = new Integer(75);
	private Integer failurePercent = new Integer(25);
	private String daysOfWeek = new String("NYYYYYNN");
	private ControlAlgorithm controlUnits = ControlAlgorithm.KVAR;
	private Integer controlDelayTime = new Integer(0);
	private Integer controlSendRetries = new Integer(0);
    private String integrateFlag = "N";
    private Integer integratePeriod = new Integer (0);
    private String likeDayFallBack = "N";
    private String endDaySettings = CtiUtilities.STRING_NONE;
    private List<PeakTargetSetting> targetSettings = StrategyPeakSettingsHelper.getSettingDefaults(ControlAlgorithm.KVAR);
    private List<VoltageViolationSetting> voltageViolationSettings = VoltageViolationSettingsHelper.getVoltageViolationDefaults();
    private PowerFactorCorrectionSetting powerFactorCorrectionSetting = new PowerFactorCorrectionSetting();
    
	public static final String SETTER_COLUMNS[] = { 
		"StrategyName", "ControlMethod", "MaxDailyOperation",
		"MaxOperationDisableFlag",
		"PeakStartTime", "PeakStopTime",
		"ControlInterval", "MinResponseTime", "MinConfirmPercent",
		"FailurePercent", "DaysOfWeek",
		"ControlUnits", "ControlDelayTime", "ControlSendRetries",
		"IntegrateFlag", "IntegratePeriod",
        "LikeDayFallBack", "EndDaySettings"
	};
	public static final String CONSTRAINT_COLUMNS[] = { "StrategyID" };
	public static final String TABLE_NAME = "CapControlStrategy";
	public static boolean todExists = false;
	private StrategyDao strategyDao = YukonSpringHook.getBean(StrategyDao.class);
	
	public void add() throws SQLException {
		Object[] addValues = {
			getStrategyID(), getStrategyName(), getControlMethod().name(), getMaxDailyOperation(), 
			getMaxOperationDisableFlag(), getPeakStartTime(), getPeakStopTime(),
			getControlInterval(), getMinResponseTime(), getMinConfirmPercent(),
			getFailurePercent(), getDaysOfWeek(), getControlUnits().name(),
			getControlDelayTime(), getControlSendRetries(),
			getIntegrateFlag(), getIntegratePeriod(), 
            getLikeDayFallBack(), getEndDaySettings()
		};
		add( TABLE_NAME, addValues );
	}
	
	public void update() throws SQLException {
        Object setValues[]= { 
            getStrategyName(), getControlMethod().name(), getMaxDailyOperation(), 
            getMaxOperationDisableFlag(), getPeakStartTime(), getPeakStopTime(),
            getControlInterval(), getMinResponseTime(), getMinConfirmPercent(),
            getFailurePercent(), getDaysOfWeek(), getControlUnits().name(),
            getControlDelayTime(), getControlSendRetries(),
            getIntegrateFlag(), getIntegratePeriod(),
            getLikeDayFallBack(), getEndDaySettings()
        };
        Object constraintValues[] = { getStrategyID() };
        update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
        
        strategyDao.savePeakSettings(this);
        strategyDao.saveVoltageViolationSettings(this);
        strategyDao.savePowerFactorCorrectionSetting(this);
    }
	
	public void retrieve() throws SQLException {
        Object constraintValues[] = { getStrategyID() };
        Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    
        if( results.length == SETTER_COLUMNS.length ) {
            setStrategyName( (String) results[0] );
            setControlMethod( ControlMethod.valueOf((String) results[1]) );
            setMaxDailyOperation( (Integer) results[2] );
            setMaxOperationDisableFlag( new Character(results[3].toString().charAt(0)) );       
            setPeakStartTime( (Integer)results[4] );
            setPeakStopTime( (Integer)results[5] );         
            setControlInterval( (Integer) results[6] );
            setMinResponseTime( (Integer) results[7] );
            setMinConfirmPercent( (Integer) results[8] );
            setFailurePercent( (Integer) results[9] );
            setDaysOfWeek( (String) results[10] );
            setControlUnits( ControlAlgorithm.valueOf((String) results[11]) );
            setControlDelayTime( (Integer) results[12] );
            setControlSendRetries( (Integer) results[13] );
            setIntegrateFlag((String)results[14]);
            setIntegratePeriod((Integer)results[15]);
            setLikeDayFallBack((String)results[16]);
            setEndDaySettings((String)results[17]);
        } else {
            throw new IncorrectResultSizeDataAccessException(SETTER_COLUMNS.length, results.length);
        }
        retrieveTargetSettings(this);
        retrieveVoltageViolationSettings(this);
        retrievePowerFactorCorrectionSetting(this);
    }
	
	public void delete() throws java.sql.SQLException {
		delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getStrategyID() );	
	}
	
	public Integer getControlInterval() {
		return controlInterval;
	}
	
	public ControlMethod getControlMethod() {
		return controlMethod;
	}
	
	public ControlAlgorithm getControlUnits() {
		return controlUnits;
	}
	
	public String getDaysOfWeek() {
		return daysOfWeek;
	}
	
	public Integer getFailurePercent() {
		return failurePercent;
	}
	
	public Integer getMaxDailyOperation() {
		return maxDailyOperation;
	}
	
	public Character getMaxOperationDisableFlag() {
		return maxOperationDisableFlag;
	}

	public boolean isMaxOperationDisabled() {
		return CtiUtilities.isTrue(getMaxOperationDisableFlag());
	}
	
	public void setMaxOperationDisabled( boolean val ) {
		setMaxOperationDisableFlag( (val ? CtiUtilities.trueChar : CtiUtilities.falseChar) );
	}

	public Integer getMinConfirmPercent() {
		return minConfirmPercent;
	}
	
	public Integer getMinResponseTime() {
		return minResponseTime;
	}
	
	public Integer getPeakStartTime() {
		return peakStartTime;
	}

	public String getOffPeakSettingsString() {
	    return StrategyPeakSettingsHelper.getOffPeakSettingsString(this);
	}
	
	public Integer getPeakStopTime() {
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
	
	public void setControlUnits(ControlAlgorithm newControlUnits) {
		controlUnits = newControlUnits;
	}
	
	public void setDaysOfWeek(String newDaysOfWeek) {
		daysOfWeek = newDaysOfWeek;
	}

	public void setFailurePercent(Integer newValue) {
		this.failurePercent = newValue;
	}
	
	public void setMaxDailyOperation(Integer newValue) {
		this.maxDailyOperation = newValue;
	}
	
	public void setMaxOperationDisableFlag(Character newMaxOperationDisableFlag) {
		maxOperationDisableFlag = newMaxOperationDisableFlag;
	}
	
	public void setMinConfirmPercent(Integer newValue) {
		this.minConfirmPercent = newValue;
	}
	
	public void setMinResponseTime(Integer newValue) {
		this.minResponseTime = newValue;
	}

	public void setPeakStartTime(Integer newPeakStartTime) {
		peakStartTime = newPeakStartTime;
	}
	
	public void setPeakStopTime(Integer newPeakStopTime) {
		peakStopTime = newPeakStopTime;
	}

	public void setControlDelayTime(Integer newValue) {
		controlDelayTime = newValue;
	}
	
	public void setControlSendRetries(Integer newTime) {
		controlSendRetries = newTime;
	}
	
	private void retrieveTargetSettings(CapControlStrategy strategy) {
	    targetSettings = strategyDao.getPeakSettings(strategy);
	}
	
	private void retrieveVoltageViolationSettings(CapControlStrategy strategy) {
	    voltageViolationSettings = strategyDao.getVoltageViolationSettings(strategy);
	}
	
	private void retrievePowerFactorCorrectionSetting(CapControlStrategy strategy) {
	    powerFactorCorrectionSetting = strategyDao.getPowerFactorCorrectionSetting(strategy);
	}

	public String toString() {
		return getStrategyName();
	}

	public Integer getStrategyID() {
		return strategyID;
	}

	public String getStrategyName() {
		return strategyName;
	}

	public void setStrategyID(Integer integer) {
		strategyID = integer;
	}

	public void setStrategyName(String string) {
		strategyName = string;
	}

    public Integer getIntegratePeriod() {
        return integratePeriod;
    }

    public void setIntegratePeriod(Integer integratedPeriod) {
        this.integratePeriod = integratedPeriod;
    }

    public String getIntegrateFlag() {
        return integrateFlag;
    }

    public void setIntegrateFlag(String integratedFlad) {
        this.integrateFlag = integratedFlad;
    }

	public String getLikeDayFallBack() {
		return likeDayFallBack;
	}

	public void setLikeDayFallBack(String likeDayFallBack) {
		this.likeDayFallBack = likeDayFallBack;
	}
	
	public String getEndDaySettings() {
        return endDaySettings;
    }

    public void setEndDaySettings(String endDaySettings) {
        this.endDaySettings = endDaySettings;
    }

    public List<PeakTargetSetting> getTargetSettings() {
        return targetSettings;
    }
    
    public void setTargetSettings(List<PeakTargetSetting> targetSettings) {
        this.targetSettings = targetSettings;
    }
    
    public List<VoltageViolationSetting> getVoltageViolationSettings() {
        return voltageViolationSettings;
    }

    public void setVoltageViolationSettings(List<VoltageViolationSetting> voltageViolationSettings) {
        this.voltageViolationSettings = voltageViolationSettings;
    }

    public PowerFactorCorrectionSetting getPowerFactorCorrectionSetting() {
        return powerFactorCorrectionSetting;
    }

    public void setPowerFactorCorrectionSetting(PowerFactorCorrectionSetting powerFactorCorrectionSetting) {
        this.powerFactorCorrectionSetting = powerFactorCorrectionSetting;
    }

    public boolean isKVarAlgorithm () {
        return controlUnits == ControlAlgorithm.KVAR;
    }
    
    public boolean isPFAlgorithm () {
        return controlUnits == ControlAlgorithm.PFACTOR_KW_KVAR;
    }
    
    public boolean isVoltVar () {
        return controlUnits == ControlAlgorithm.MULTI_VOLT_VAR;
    }
    
    public boolean isIvvc () {
        return controlUnits == ControlAlgorithm.INTEGRATED_VOLT_VAR;
    }
    
    public boolean isBusOptimized() {
        return controlMethod == ControlMethod.BUSOPTIMIZED_FEEDER;
    }
    
    public boolean isVoltStrat() {
        if (controlUnits == ControlAlgorithm.MULTI_VOLT)
            return true;
        else if (controlUnits == ControlAlgorithm.VOLTS)
            return true;
        else
            return false;
    }
    
    public boolean isTimeOfDay(){
        return controlMethod == ControlMethod.TIME_OF_DAY;
    }
    
    /**
     * Generates a DBChange msg.
     */
    public DBChangeMsg[] getDBChangeMsgs(DbChangeType dbChangeType) {
        DBChangeMsg[] dbChange = new DBChangeMsg[1];

        //add the basic change method
        dbChange[0] = new DBChangeMsg(
                        getStrategyID().intValue(),
                        DBChangeMsg.CHANGE_CBC_STRATEGY_DB,
                        DBChangeMsg.CAT_CBC_STRATEGY,
                        dbChangeType);

        return dbChange;
    }
    
}