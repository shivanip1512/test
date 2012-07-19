package com.cannontech.database.db.point;

import com.cannontech.database.data.point.ControlType;
import com.cannontech.database.data.point.StateControlType;

public class PointStatusControl extends PointControl 
{
	public static final int DEFAULT_CMD_TIMEOUT = 0;
	
	private Integer pointID = null;
	private Integer closeTime1 = 0;
	private Integer closeTime2 = 0;
	private String stateZeroControl = StateControlType.OPEN.getControlCommand();
	private String stateOneControl = StateControlType.CLOSE.getControlCommand();
	private Integer commandTimeOut = DEFAULT_CMD_TIMEOUT;

	public static final String CONSTRAINT_COLUMNS[] = { "POINTID" };

	public static final String SETTER_COLUMNS[] = 
	{ 
		"ControlType", "CloseTime1", "CloseTime2", 
		"StateZeroControl", "StateOneControl", "CommandTimeOut"
	};

	public static final String TABLE_NAME = "PointStatusControl";	

    public PointStatusControl() 
    {
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
    
    protected boolean isValidControlType(String controlType) {
        
        return 
            controlType.equalsIgnoreCase(ControlType.NORMAL.getControlName()) ||
            controlType.equalsIgnoreCase(ControlType.LATCH.getControlName()) ||
            controlType.equalsIgnoreCase(ControlType.SBOPULSE.getControlName()) ||
            controlType.equalsIgnoreCase(ControlType.SBOLATCH.getControlName());
    }
}
