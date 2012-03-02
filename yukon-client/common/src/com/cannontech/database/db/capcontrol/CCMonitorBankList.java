package com.cannontech.database.db.capcontrol;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.common.util.DatabaseRepresentationSource;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.data.point.CapBankMonitorPointParams;
import com.cannontech.database.db.DBPersistent;

public class CCMonitorBankList extends DBPersistent {

	private Integer deviceId = new Integer ( 0 );
	private Integer pointId = new Integer ( 0 );
	
	private Integer displayOrder = new Integer ( 0 );
	private Character scannable = new Character ( 'N' );
	private Long NINAvg = new Long ( 3 );
	private Float upperBandwidth = new Float(0);
	private Float lowerBandwidth = new Float(0);
	private Character phase;
	private Boolean overrideStrategySettings = Boolean.FALSE;
	
	private CapBankMonitorPointParams monitorPoint = null;

	
	public static final String SETTER_COLUMNS[] = 
	{ 
		"DisplayOrder",
		"Scannable",
		"NINAvg",
		"UpperBandwidth",
		"LowerBandwidth",
		"Phase",
		"OverrideStrategy"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "DeviceId", "PointId" };

	public static final String TABLE_NAME= "CCMONITORBANKLIST";
	
	
	
	public CCMonitorBankList(){
		super();
	}
	
	public CCMonitorBankList(CapBankMonitorPointParams _monitorPoint_) {
		super();
		monitorPoint = _monitorPoint_;
		deviceId = new Integer ( monitorPoint.getDeviceId() );
		pointId =  new Integer (monitorPoint.getPointId() );
		displayOrder = new Integer ( monitorPoint.getDisplayOrder() );
		if (monitorPoint.isInitScan())
			scannable = new Character ('Y');
		else
			scannable = new Character ('N');
		NINAvg = new Long ( monitorPoint.getNINAvg() );
		upperBandwidth = new Float ( monitorPoint.getUpperBandwidth() );
		lowerBandwidth = new Float ( monitorPoint.getLowerBandwidth() );
		String phaseParam = monitorPoint.getPhase();
		if (phaseParam != null) {
		    phase = new Character (phaseParam.charAt(0));
		} else {
		    phase = new Character ('A'); //Default to phase A
		}
		overrideStrategySettings = monitorPoint.isOverrideFdrLimits();
	}

	public void add() throws SQLException {

		Object[] addValues = { getDeviceId(), getPointId(), getDisplayOrder(), getScannable(),
							   getNINAvg(), getUpperBandwidth(), getLowerBandwidth(), getPhase(),
							   getOverrideStrategySettingsObject()
							 };

		add( TABLE_NAME, addValues );

	}

	public void delete() throws SQLException {
		Object[] values = { getDeviceId(), getPointId() };
		
		delete( TABLE_NAME, CONSTRAINT_COLUMNS, values );
	}

	public void retrieve() throws SQLException {
		Object constraintValues[] = { getDeviceId(), getPointId()};
		Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

		if( results.length == SETTER_COLUMNS.length )
		{
			setDisplayOrder((Integer) results[0]);
			setScannable((Character) results[1]);
			setNINAvg((Long) results[2]);
			setUpperBandwidth((Float) results[3]);
			setLowerBandwidth((Float) results[4]);
			setPhase((Character) results[5]);
			setOverrideStrategySettings((Boolean) results[6]);
		}
		else
			throw new Error(getClass() + " - Incorrect Number of results retrieved");


	}

	public void update() throws SQLException {
		Object setValues[]= 
		{
			getDisplayOrder(),getScannable(), getNINAvg(), getUpperBandwidth(), getLowerBandwidth(), 
			getPhase(), getOverrideStrategySettingsObject()
		};

		Object constraintValues[] = { getDeviceId(), getPointId()};

		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}


	public Integer getDeviceId() {
		if (monitorPoint != null) 
			deviceId = new Integer ( monitorPoint.getDeviceId() );
		return deviceId;
	}

	public void setDeviceId(Integer deviceId) {
			getMonitorPoint().setDeviceId(deviceId.intValue());
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public Float getLowerBandwidth() {
		return lowerBandwidth;
	}

	public void setLowerBandwidth(Float lowerBandwidth) {
		this.lowerBandwidth = lowerBandwidth;
	}

	public Long getNINAvg() {
		return NINAvg;
	}

	public void setNINAvg(Long avg) {
		NINAvg = avg;
	}

	public Integer getPointId() {
		if (monitorPoint != null)
			pointId = new Integer (monitorPoint.getPointId());
		return pointId;
	}

	public void setPointId(Integer pointId) {
		
			getMonitorPoint().setPointId( pointId.intValue());
	}

	public Character getScannable() {
		return scannable;
	}

	public void setScannable(Character scannable) {
		this.scannable = scannable;
	}

	public Float getUpperBandwidth() {
		return upperBandwidth;
	}

	public void setUpperBandwidth(Float upperBandwidth) {
		this.upperBandwidth = upperBandwidth;
	}

	public Character getPhase() {
        return phase;
    }

    public void setPhase(Character phase) {
        this.phase = phase;
    }
    
    public Boolean isOverrideStrategySettings() {
        return overrideStrategySettings;
    }
    
    public Object getOverrideStrategySettingsObject() {
        DatabaseRepresentationSource ynBoolean = YNBoolean.valueOf(overrideStrategySettings);
        return ynBoolean.getDatabaseRepresentation();
    }
    
    public void setOverrideStrategySettings(Boolean overrideStrategySettings) {
        this.overrideStrategySettings = overrideStrategySettings;
    }

    public CapBankMonitorPointParams getMonitorPoint() {
		if (monitorPoint == null)
			monitorPoint = new CapBankMonitorPointParams();
		return monitorPoint;
	}
	
    public static List<CCMonitorBankList> getMonitorPointsOnCapBankList (Integer deviceId) {
        String sqlStmt = "SELECT * FROM CCMonitorBankList WHERE DeviceId = ?";  
    
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();            
        
        List<CCMonitorBankList> monitorPoints = yukonTemplate.query(sqlStmt, new Integer[] {deviceId},
        		new RowMapper<CCMonitorBankList>() {
        			public CCMonitorBankList mapRow(ResultSet rs, int rowNum) throws SQLException {
        				CCMonitorBankList monitorPoint = new CCMonitorBankList(); 
        				monitorPoint.setDeviceId(new Integer ( rs.getBigDecimal(1).intValue() ));
        				monitorPoint.setPointId(new Integer ( rs.getBigDecimal(2).intValue() ));
        				monitorPoint.setDisplayOrder(new Integer ( rs.getBigDecimal(3).intValue() ));
        				monitorPoint.setScannable(new Character (rs.getString(4).charAt(0) ));
        				monitorPoint.setNINAvg(new Long ( rs.getBigDecimal(5).longValue() ));
        				monitorPoint.setUpperBandwidth(new Float (rs.getFloat(6) ));
        				monitorPoint.setLowerBandwidth(new Float (rs.getFloat(7) ));
        				String phase = rs.getString(8);
        				if (phase != null) {
        				    monitorPoint.setPhase(new Character (phase.charAt(0) ));
        				} else {
        				    monitorPoint.setPhase(null);
        				}
        				char overrideStrategySettingsChar = rs.getString(9).charAt(0);
        				monitorPoint.setOverrideStrategySettings(YNBoolean.valueOf(overrideStrategySettingsChar).getBoolean());
        				return monitorPoint;
        			}
        		});

        return monitorPoints;  	
    }

    public static void deleteMonitorPointsOnCapBankList (Integer deviceId) {
        String sqlStmt = "DELETE FROM CCMonitorBankList WHERE DeviceId = ?";  
 
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();                    
        
        yukonTemplate.update(sqlStmt, new Object[] {deviceId});

    }
}
