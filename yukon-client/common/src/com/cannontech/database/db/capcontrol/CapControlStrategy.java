package com.cannontech.database.db.capcontrol;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.capcontrol.ControlMethod;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.StrategyDao;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YukonJdbcOperations;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.point.calculation.CalcComponentTypes;
import com.cannontech.database.db.point.calculation.ControlAlgorithm;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.spring.YukonSpringHook;

/**
 * Strategy of control for a SubBus or Feeder.
 *  No .data. object for this DBPersistent at this time.
 * 
 */
public class CapControlStrategy extends DBPersistent implements CTIDbChange {
	
	private Integer strategyID = null;
	private String strategyName = null;
	private String controlMethod = ControlMethod.INDIVIDUAL_FEEDER.getDatabaseRepresentation();
	private Integer maxDailyOperation = new Integer(0);
	private Character maxOperationDisableFlag = new Character('N');
	private Integer peakStartTime = new Integer(0);
	private Integer peakStopTime = new Integer(86400);  //24:00
	private Integer controlInterval = new Integer(900);
	private Integer minResponseTime = new Integer(900);
	private Integer minConfirmPercent = new Integer(75);
	private Integer failurePercent = new Integer(25);
	private String daysOfWeek = new String("NYYYYYNN");
	private String controlUnits = CalcComponentTypes.LABEL_KVAR;
	private Integer controlDelayTime = new Integer(0);
	private Integer controlSendRetries = new Integer(0);
    private String integrateFlag = "N";
    private Integer integratePeriod = new Integer (0);
    private String likeDayFallBack = "N";
    private String endDaySettings = CtiUtilities.STRING_NONE;
    private List<PeakTargetSetting> targetSettings;
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
	
	/**
	 * Default constructor.
	 */
	public CapControlStrategy() {
		super();
	}
	
	/**
	 * add method comment.
	 */
	public void add() throws java.sql.SQLException {
		Object[] addValues = {
			getStrategyID(), getStrategyName(), getControlMethod(), getMaxDailyOperation(), 
			getMaxOperationDisableFlag(), getPeakStartTime(), getPeakStopTime(),
			getControlInterval(), getMinResponseTime(), getMinConfirmPercent(),
			getFailurePercent(), getDaysOfWeek(), getControlUnits(),
			getControlDelayTime(), getControlSendRetries(),
			getIntegrateFlag(), getIntegratePeriod(), 
            getLikeDayFallBack(), getEndDaySettings()
		};
		add( TABLE_NAME, addValues );
	}
	
	
	/**
	 * delete method comment.
	 */
	public void delete() throws java.sql.SQLException {
		delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getStrategyID() );	
	}
	
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Integer
	 */
	public Integer getControlInterval() {
		return controlInterval;
	}
	
	public String getControlIntervalString() {
		String str = new String();
		str += controlInterval/60 + "m" + controlInterval%60 + "s";
		return str;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11/9/2001 1:42:02 PM)
	 * @return java.lang.String
	 */
	public java.lang.String getControlMethod() {
		return controlMethod;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (7/5/2002 10:22:53 AM)
	 * @return java.lang.String
	 */
	public java.lang.String getControlUnits() {
		return controlUnits;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (9/26/00 10:27:13 AM)
	 * @return java.lang.String
	 */
	public String getDaysOfWeek() {
		return daysOfWeek;
	}
	
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Integer
	 */
	public Integer getFailurePercent() {
		return failurePercent;
	}
	
	public String getPassFailPercentString() {
		String str = new String();
		str +=  minConfirmPercent + "/" + failurePercent + "";
		return str;
	}
	
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Integer
	 */
	public Integer getMaxDailyOperation() {
		return maxDailyOperation;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (11/9/2001 1:42:02 PM)
	 * @return java.lang.Character
	 */
	public java.lang.Character getMaxOperationDisableFlag() {
		return maxOperationDisableFlag;
	}

	/**
	 * Boolean method for MaxOperations diable flag
	 */
	public boolean isMaxOperationDisabled() {
		return CtiUtilities.isTrue(getMaxOperationDisableFlag());
	}
	
	/**
	 * Boolean method for MaxOperations diable flag
	 */
	public void setMaxOperationDisabled( boolean val ) {
		setMaxOperationDisableFlag( (val ? CtiUtilities.trueChar : CtiUtilities.falseChar) );
	}

	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Integer
	 */
	public Integer getMinConfirmPercent() {
		return minConfirmPercent;
	}
	
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Integer
	 */
	public Integer getMinResponseTime() {
		return minResponseTime;
	}
	
	public String getMinResponseTimeString() {
		String str = new String();
		str += minResponseTime/60 + "m" + minResponseTime%60 + "s";
		return str;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11/13/2001 3:58:59 PM)
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getPeakStartTime() {
		return peakStartTime;
	}

	public String getPeakSettingsString() {
	    return StrategyPeakSettingsHelper.getPeakSettingsString(this);
	}

	public String getOffPeakSettingsString() {
	    return StrategyPeakSettingsHelper.getOffPeakSettingsString(this);
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (11/13/2001 3:58:59 PM)
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getPeakStopTime() {
		return peakStopTime;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (7/5/2002 10:22:53 AM)
	 * @return java.lang.Double
	 */
	public Integer getControlDelayTime() {
		return controlDelayTime;
	}
	
	public Integer getControlSendRetries() {
		return controlSendRetries;
	}
	
	/**
	 * retrieve method comment.
	 */
	public void retrieve() throws java.sql.SQLException {
		Object constraintValues[] = { getStrategyID() };
		Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	
		if( results.length == SETTER_COLUMNS.length ) {
			setStrategyName( (String) results[0] );
			setControlMethod( (String) results[1] );
			setMaxDailyOperation( (Integer) results[2] );
			setMaxOperationDisableFlag( new Character(results[3].toString().charAt(0)) );		
			setPeakStartTime( (Integer)results[4] );
			setPeakStopTime( (Integer)results[5] );			
			setControlInterval( (Integer) results[6] );
			setMinResponseTime( (Integer) results[7] );
			setMinConfirmPercent( (Integer) results[8] );
			setFailurePercent( (Integer) results[9] );
			setDaysOfWeek( (String) results[10] );
			setControlUnits( (String) results[11] );
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
	}
	
	/**
	 * This method was created in VisualAge.
	 * @param newValue java.lang.Integer
	 */
	public void setControlInterval(Integer newValue) {
		this.controlInterval = newValue;
	}
	
	/**
	 * Sets the control method for this strategy, if the control method is 
	 * time of day, we also set the control units to time of day since the
	 * UI no longer has the ability to touch that field.
	 */
	public void setControlMethod(String newControlMethod) {
		controlMethod = newControlMethod;
		ControlMethod method = ControlMethod.getForDbString(newControlMethod);
		if(method == ControlMethod.TIME_OF_DAY) {
			setControlUnits(ControlAlgorithm.TIME_OF_DAY.getDisplayName());
		}
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (7/5/2002 10:22:53 AM)
	 * @param newControlUnits java.lang.String
	 */
	public void setControlUnits(java.lang.String newControlUnits) {
		controlUnits = newControlUnits;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (9/26/00 10:27:13 AM)
	 * @param newDaysOfWeek java.lang.String
	 */
	public void setDaysOfWeek(String newDaysOfWeek) {
		daysOfWeek = newDaysOfWeek;
	}

	/**
	 * This method was created in VisualAge.
	 * @param newValue java.lang.Integer
	 */
	public void setFailurePercent(Integer newValue) {
		this.failurePercent = newValue;
	}
	
	/**
	 * This method was created in VisualAge.
	 * @param newValue java.lang.Integer
	 */
	public void setMaxDailyOperation(Integer newValue) {
		this.maxDailyOperation = newValue;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (11/9/2001 1:42:02 PM)
	 * @param newMaxOperationDisableFlag java.lang.Character
	 */
	public void setMaxOperationDisableFlag(java.lang.Character newMaxOperationDisableFlag) {
		maxOperationDisableFlag = newMaxOperationDisableFlag;
	}
	
	/**
	 * This method was created in VisualAge.
	 * @param newValue java.lang.Integer
	 */
	public void setMinConfirmPercent(Integer newValue) {
		this.minConfirmPercent = newValue;
	}
	
	/**
	 * This method was created in VisualAge.
	 * @param newValue java.lang.Integer
	 */
	public void setMinResponseTime(Integer newValue) {
		this.minResponseTime = newValue;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (11/13/2001 3:58:59 PM)
	 * @param newPeakStartTime java.lang.Integer
	 */
	public void setPeakStartTime(java.lang.Integer newPeakStartTime) {
		peakStartTime = newPeakStartTime;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (11/13/2001 3:58:59 PM)
	 * @param newPeakStopTime java.lang.Integer
	 */
	public void setPeakStopTime(java.lang.Integer newPeakStopTime) {
		peakStopTime = newPeakStopTime;
	}

	public void setControlDelayTime(Integer newValue) {
		controlDelayTime = newValue;
	}
	
	public void setControlSendRetries(Integer newTime) {
		controlSendRetries = newTime;
	}
	/**
	 * update method comment.
	 */
	public void update() throws java.sql.SQLException {
		Object setValues[]= { 
			getStrategyName(), getControlMethod(), getMaxDailyOperation(), 
			getMaxOperationDisableFlag(), getPeakStartTime(), getPeakStopTime(),
			getControlInterval(), getMinResponseTime(), getMinConfirmPercent(),
			getFailurePercent(), getDaysOfWeek(), getControlUnits(),
			getControlDelayTime(), getControlSendRetries(),
			getIntegrateFlag(), getIntegratePeriod(),
            getLikeDayFallBack(), getEndDaySettings()
		};
		Object constraintValues[] = { getStrategyID() };
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
		StrategyDao strategyDao = YukonSpringHook.getBean(StrategyDao.class);
		strategyDao.savePeakSettings(this);
	}

	/**
	 * Gets the next unused ID for a Strategy
	 * 
	 */
	public final static Integer getNextStrategyID() {
		SqlStatement stmt = new SqlStatement( "SELECT Max(StrategyID)+1 FROM " + TABLE_NAME , CtiUtilities.getDatabaseAlias());

		try {
			stmt.execute();
			if( stmt.getRowCount() > 0 ) {
				return new Integer(stmt.getRow(0)[0].toString());
			}else {
				return new Integer(1);
			}
		} catch( Exception e ) {
		   CTILogger.warn( e.getMessage(), e );
		   return new Integer(1);
		}
	}
	
	public static boolean todExists(Integer strategyId) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
	    sql.append("SELECT count (*)");
	    sql.append("FROM CCStrategyTargetSettings ts, ");
	    sql.append(  "JOIN capcontrolstrategy strat ON ts.strategyId = strat.strategyId");
	    sql.append("WHERE strat.controlmethod").eq(ControlMethod.TIME_OF_DAY);
	    sql.append(  "AND strategyid").eq(strategyId);
	    YukonJdbcOperations template = YukonSpringHook.getBean(YukonJdbcOperations.class);
	    int count = template.queryForInt(sql);
	    todExists = count > 0;
	    return todExists;
	}
	
	public static void deleteTod(Integer strategyId) {
        String sql = "delete from CCStrategyTargetSettings where strategyId = ?";
        SimpleJdbcOperations jdbcTemplate = (SimpleJdbcOperations) YukonSpringHook.getBean("simpleJdbcTemplate");
        jdbcTemplate.update(sql, strategyId);
    }

	/**
	 * This method returns all CapControlStrategy currently
	 * in the database with all their attributes populated
	 *
	 */
	public static List<CapControlStrategy> getAllCBCStrategies() {
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
		List<CapControlStrategy> list = new ArrayList<CapControlStrategy>();

	   //Get all the data from the database                
	   String sql = "select " + 
		   "StrategyID, StrategyName, ControlMethod, MaxDailyOperation,"+
		   "MaxOperationDisableFlag," +
		   "PeakStartTime, PeakStopTime," +
		   "ControlInterval, MinResponseTime, MinConfirmPercent," +
		   "FailurePercent, DaysOfWeek," +
		   "ControlUnits, ControlDelayTime, ControlSendRetries," +
		   "IntegrateFlag, IntegratePeriod," +
           "LikeDayFallBack, EndDaySettings" +
		   " from " + TABLE_NAME + " order by StrategyName";

		try {		
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

			if( conn == null ) {
				throw new IllegalStateException("Error getting database connection.");
			} else {
				pstmt = conn.prepareStatement(sql.toString());			
				rset = pstmt.executeQuery();
	
				while( rset.next() ) {
					CapControlStrategy cbcStrat = new CapControlStrategy();
					cbcStrat.setStrategyID( new Integer(rset.getInt("StrategyId")) );
					cbcStrat.setStrategyName( rset.getString("StrategyName") );
					cbcStrat.setControlMethod( rset.getString("ControlMethod") );
					cbcStrat.setMaxDailyOperation( new Integer(rset.getInt("MaxDailyOperation")) );
					cbcStrat.setMaxOperationDisableFlag( new Character(rset.getString("MaxOperationDisableFlag").charAt(0)) );		
					cbcStrat.setPeakStartTime( new Integer(rset.getInt("PeakStartTime")) );
					cbcStrat.setPeakStopTime( new Integer(rset.getInt("PeakStopTime")) );		
					cbcStrat.setControlInterval( new Integer(rset.getInt("ControlInterval")) );
					cbcStrat.setMinResponseTime( new Integer(rset.getInt("MinResponseTime")) );
					cbcStrat.setMinConfirmPercent( new Integer(rset.getInt("MinConfirmPercent")) );
					cbcStrat.setFailurePercent( new Integer(rset.getInt("FailurePercent")) );
					cbcStrat.setDaysOfWeek( rset.getString("DaysOfWeek") );
					cbcStrat.setControlUnits( rset.getString("ControlUnits") );
					cbcStrat.setControlDelayTime( new Integer(rset.getInt("ControlDelayTime")) );
					cbcStrat.setControlSendRetries( new Integer(rset.getInt("ControlSendRetries")) );
                    cbcStrat.setIntegrateFlag(new String (rset.getString("IntegrateFlag")));
                    cbcStrat.setIntegratePeriod(new Integer (rset.getInt("IntegratePeriod")));
                    cbcStrat.setLikeDayFallBack(new String (rset.getString("LikeDayFallBack")));
                    cbcStrat.setEndDaySettings(new String (rset.getString("EndDaySettings")));
                    cbcStrat.retrieveTargetSettings(cbcStrat);
                    list.add( cbcStrat );
				}

			}		
		}
		catch( java.sql.SQLException e ) {
			CTILogger.error( e.getMessage(), e );
		}
		finally {
			SqlUtils.close(rset, pstmt, conn );
		}
		return list;
	}
	
	private void retrieveTargetSettings(CapControlStrategy strategy){
	    StrategyDao strategyDao = YukonSpringHook.getBean("strategyDao", StrategyDao.class);
	    targetSettings = strategyDao.getPeakSettings(strategy);
	}

	public static List<LiteCapControlStrategy> getAllLiteCapControlStrategy() {
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
		List<LiteCapControlStrategy> list = new ArrayList<LiteCapControlStrategy>();

	    //Get all the data from the database                
	    String sql = "select StrategyID, StrategyName from " + TABLE_NAME + " order by StrategyName";

		try {		
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

			if( conn == null ) {
				throw new IllegalStateException("Error getting database connection.");
			} else {
				pstmt = conn.prepareStatement(sql.toString());			
				rset = pstmt.executeQuery();
	
				while( rset.next() ) {
					LiteCapControlStrategy cbcStrat = new LiteCapControlStrategy();
					cbcStrat.setStrategyId( new Integer(rset.getInt(1)) );
					cbcStrat.setStrategyName( rset.getString(2) );
                    list.add( cbcStrat );				
				}
			}		
		}
		catch( java.sql.SQLException e ) {
			CTILogger.error( e.getMessage(), e );
		}
		finally {
			SqlUtils.close(rset, pstmt, conn );
		}
		return list;
	}

	
	/**
	 * Generates a DBChange msg.
	 * 
	 */
	public DBChangeMsg[] getDBChangeMsgs( int typeOfChange )
	{
		DBChangeMsg[] dbChange = new DBChangeMsg[1];

		//add the basic change method
		dbChange[0] = new DBChangeMsg(
						getStrategyID().intValue(),
						DBChangeMsg.CHANGE_CBC_STRATEGY_DB,
						DBChangeMsg.CAT_CBC_STRATEGY,
						typeOfChange );

		return dbChange;
	}

	/**
	 * @param string
	 */
	public String toString() {
		return getStrategyName();
	}


	/**
	 * @return
	 */
	public Integer getStrategyID() {
		return strategyID;
	}

	/**
	 * @return
	 */
	public String getStrategyName() {
		return strategyName;
	}

	/**
	 * @param integer
	 */
	public void setStrategyID(Integer integer) {
		strategyID = integer;
	}

	/**
	 * @param string
	 */
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
    
    public boolean isKVarAlgorithm () {
        return getControlUnits().equalsIgnoreCase(CalcComponentTypes.LABEL_KVAR);
    }
    
    public boolean isPFAlgorithm () {
        return getControlUnits().equalsIgnoreCase(CalcComponentTypes.PFACTOR_KW_KVAR_FUNCTION);
    }
    
    public boolean isVoltVar () {
        return getControlUnits().equalsIgnoreCase(CalcComponentTypes.LABEL_MULTI_VOLT_VAR);
    }
    
    public boolean isVoltStrat() {
        if (getControlUnits().equalsIgnoreCase(CalcComponentTypes.LABEL_MULTI_VOLT))
            return true;
        else if (getControlUnits().equalsIgnoreCase(CalcComponentTypes.LABEL_VOLTS))
            return true;
        else
            return false;
    }
    
    public boolean isTimeOfDay(){
        return ControlMethod.getForDbString(getControlMethod()).equals(ControlMethod.TIME_OF_DAY);
    }
    
    public void controlUnitsChanged(String newValue) {
        targetSettings = StrategyPeakSettingsHelper.getSettingDefaults(ControlAlgorithm.getControlAlgorithm(newValue));
    }
    
    public void controlMethodChanged(String newValue) {
        if(newValue.equalsIgnoreCase("TimeOfDay")){
            targetSettings = StrategyPeakSettingsHelper.getSettingDefaults(ControlAlgorithm.TIME_OF_DAY);
        } else {
            targetSettings = StrategyPeakSettingsHelper.getSettingDefaults(ControlAlgorithm.getControlAlgorithm(getControlUnits()));
        }
    }
    
}