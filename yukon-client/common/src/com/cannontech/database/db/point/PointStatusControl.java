package com.cannontech.database.db.point;

import java.util.List;

import com.cannontech.database.data.point.StateControlType;
import com.cannontech.database.data.point.StatusControlType;
import com.google.common.collect.Lists;

public class PointStatusControl extends PointControl {
	public static final int DEFAULT_CMD_TIMEOUT = 0;
	
	private Integer pointID = null;
	private Integer closeTime1 = 0;
	private Integer closeTime2 = 0;
	private String stateZeroControl = StateControlType.OPEN.getControlCommand();
	private String stateOneControl = StateControlType.CLOSE.getControlCommand();
	private Integer commandTimeOut = DEFAULT_CMD_TIMEOUT;
	private String controlType = StatusControlType.NONE.getControlName();
	
	private final List<String> validControlTypes = 
	        Lists.newArrayList(StatusControlType.NORMAL.getControlName(),
	                           StatusControlType.LATCH.getControlName(),
	                           StatusControlType.PSEUDO.getControlName(), 
	                           StatusControlType.SBOPULSE.getControlName(),
	                           StatusControlType.SBOLATCH.getControlName());

	public static final String CONSTRAINT_COLUMNS[] = { "POINTID" };

	public static final String SETTER_COLUMNS[] = 
	{ 
		"ControlType", "CloseTime1", "CloseTime2", 
		"StateZeroControl", "StateOneControl", "CommandTimeOut"
	};

	public static final String TABLE_NAME = "PointStatusControl";

    public PointStatusControl() {
        super();
    }
    
    @Override
    public void add() throws java.sql.SQLException {

        if (hasControl()) {

            super.add();
        
            Object addValues[] = { getPointID(), 
        			getControlType(), getCloseTime1(), getCloseTime2(),
        			getStateZeroControl(), getStateOneControl(),
        			getCommandTimeOut() };
        
        	add( TABLE_NAME, addValues );
        }
    }

    @Override
    public void delete() throws java.sql.SQLException {
        if (getPointID() != null) {
            
            delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getPointID() );
        
            super.delete();
        }
    }

    public Integer getCloseTime1() {
    	return closeTime1;
    }

    public Integer getCloseTime2() {
    	return closeTime2;
    }

    public Integer getCommandTimeOut() {
    	return commandTimeOut;
    }

    @Override
    public Integer getPointID() {
    	return pointID;
    }

    public java.lang.String getStateOneControl() {
    	return stateOneControl;
    }

    public java.lang.String getStateZeroControl() {
    	return stateZeroControl;
    }

    @Override
    public void retrieve() throws java.sql.SQLException 
    {
        super.retrieve();
        
    	Object constraintValues[] = { getPointID() };
    
    	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
    
        if (results.length == SETTER_COLUMNS.length) {
    		
            setControlType( (String) results[0] );
    		setCloseTime1( (Integer) results[1] );
    		setCloseTime2( (Integer) results[2] );
    		setStateZeroControl( (String) results[3] );
    		setStateOneControl( (String) results[4] );
    		setCommandTimeOut( (Integer) results[5] );
    		
    	} else if (results.length != 0) {
    		
    	    throw new Error(getClass() + " - Incorrect Number of results retrieved");
    	}
    }

    public void setCloseTime1(Integer newCloseTime1) {
    	closeTime1 = newCloseTime1;
    }

    public void setCloseTime2(Integer newCloseTime2) {
    	closeTime2 = newCloseTime2;
    }

    public void setCommandTimeOut(Integer newCommandTimeOut) {
    	commandTimeOut = newCommandTimeOut;
    }

    @Override
    public void setPointID(Integer newValue) {
        super.setPointID(newValue);
    	
        this.pointID = newValue;
    }

    public void setStateOneControl(java.lang.String newStateOneControl) {
    	stateOneControl = newStateOneControl;
    }

    public void setStateZeroControl(java.lang.String newStateZeroControl) {
    	stateZeroControl = newStateZeroControl;
    }

    @Override
    public void update() throws java.sql.SQLException {
        delete();
        add();
    }
    
    @Override
    protected boolean isValidControlType(String controlType) {
        return validControlTypes.contains(controlType);
    }
    
    @Override
    public String getControlType() {
        return controlType;
    }
    
    @Override
    public void setControlType(String newValue) {
        if (isValidControlType(newValue)) {
            this.controlType = newValue;
        } else {
            this.controlType = StatusControlType.NONE.getControlName();
        }
    }
    
    @Override
    public boolean hasControl() {
        return ! controlType.equalsIgnoreCase(StatusControlType.NONE.getControlName());
    }
    
    @Override
    protected String getNormalType() {
        return StatusControlType.NORMAL.getControlName();
    }
}
