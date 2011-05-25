package com.cannontech.database.db.capcontrol;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.data.point.CapBankMonitorPointParams;
import com.cannontech.database.db.DBPersistent;

public class CCMonitorBankList extends DBPersistent {

	private Integer capBankId = new Integer ( 0 );
	private Integer pointId = new Integer ( 0 );
	
	private Integer displayOrder = new Integer ( 0 );
	private Character scannable = new Character ( 'N' );
	private Long NINAvg = new Long ( 3 );
	private Float upperBandwidth = new Float(0);
	private Float lowerBandwidth = new Float(0);
	private Character phase;
	
	private CapBankMonitorPointParams monitorPoint = null;

	
	public static final String SETTER_COLUMNS[] = 
	{ 
		"DisplayOrder",
		"Scannable",
		"NINAvg",
		"UpperBandwidth",
		"LowerBandwidth",
		"Phase"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "BankId", "PointId" };

	public static final String TABLE_NAME= "CCMONITORBANKLIST";
	
	
	
	public CCMonitorBankList(){
		super();
	}
	public CCMonitorBankList(CapBankMonitorPointParams _monitorPoint_) {
		super();
		monitorPoint = _monitorPoint_;
		capBankId = new Integer ( monitorPoint.getCapBankId() );
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
	}

	public void add() throws SQLException {

		Object[] addValues = { getCapBankId(), getPointId(), getDisplayOrder(), getScannable(),
							   getNINAvg(), getUpperBandwidth(), getLowerBandwidth(), getPhase()
							   };

		add( TABLE_NAME, addValues );

	}

	public void delete() throws SQLException {
		Object[] values = { getCapBankId(), getPointId() };
		
		delete( TABLE_NAME, CONSTRAINT_COLUMNS, values );
	}

	public void retrieve() throws SQLException {
		Object constraintValues[] = { getCapBankId(), getPointId()};
		Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

		if( results.length == SETTER_COLUMNS.length )
		{
			setDisplayOrder((Integer) results[0]);
			setScannable((Character) results[1]);
			setNINAvg((Long) results[2]);
			setUpperBandwidth((Float) results[3]);
			setLowerBandwidth((Float) results[4]);
			setPhase((Character) results[5]);
		}
		else
			throw new Error(getClass() + " - Incorrect Number of results retrieved");


	}

	public void update() throws SQLException {
		Object setValues[]= 
		{
			getDisplayOrder(),getScannable(), getNINAvg(), getUpperBandwidth(), getLowerBandwidth(), 
			getPhase()
		};

		Object constraintValues[] = { getCapBankId(), getPointId()};

		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}


	public Integer getCapBankId() {
		if (monitorPoint != null) 
			capBankId = new Integer ( monitorPoint.getCapBankId() );
		return capBankId;
	}

	public void setCapBankId(Integer capBankId) {
			getMonitorPoint().setCapBankId(capBankId.intValue());
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

    public CapBankMonitorPointParams getMonitorPoint() {
		if (monitorPoint == null)
			monitorPoint = new CapBankMonitorPointParams();
		return monitorPoint;
	}
	
    public static List<CCMonitorBankList> getMonitorPointsOnCapBankList (Integer capBankId) {
        String sqlStmt = "SELECT * FROM CCMonitorBankList WHERE bankId = ?";  
    
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();            
        
        List<CCMonitorBankList> monitorPoints = yukonTemplate.query(sqlStmt, new Integer[] {capBankId},
        		new RowMapper() {
        			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        				CCMonitorBankList monitorPoint = new CCMonitorBankList(); 
        				monitorPoint.setCapBankId(new Integer ( rs.getBigDecimal(1).intValue() ));
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
        				return monitorPoint;
        			}
        		});

        return monitorPoints;  	
    }

    public static void deleteMonitorPointsOnCapBankList (Integer capBankId) {
        String sqlStmt = "DELETE FROM CCMonitorBankList WHERE bankId = ?";  
 
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();                    
        
        yukonTemplate.update(sqlStmt, new Integer[] {capBankId});

    }
}
