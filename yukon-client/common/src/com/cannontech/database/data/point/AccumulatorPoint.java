package com.cannontech.database.data.point;

import com.cannontech.database.db.point.PointAccumulator;

public class AccumulatorPoint extends ScalarPoint {
	private PointAccumulator pointAccumulator = null;

    public AccumulatorPoint() {
    	super();
    }
    
    public void add() throws java.sql.SQLException {
    	super.add();
    	getPointAccumulator().add();
    }
    
    public void addPartial() throws java.sql.SQLException {
        super.addPartial();
    	getPointAccumulator().add();
    }
    
    public void delete() throws java.sql.SQLException {
    	getPointAccumulator().delete();
    	super.delete();
    }
    
    public void deletePartial() throws java.sql.SQLException {
        getPointAccumulator().delete();
    	super.deletePartial();
    }
    
    public PointAccumulator getPointAccumulator() {
    	if( pointAccumulator == null )
    		pointAccumulator = new PointAccumulator();
    		
    	return pointAccumulator;
    }

    public void retrieve() throws java.sql.SQLException{
    	super.retrieve();
    	getPointAccumulator().retrieve();
    }

    public void setDbConnection(java.sql.Connection conn) {
    	super.setDbConnection(conn);
    	getPointAccumulator().setDbConnection(conn);
    }

    public void setPointAccumulator(PointAccumulator newValue) {
    	this.pointAccumulator = newValue;
    }
    
    public void setPointID(Integer pointID) {
    	super.setPointID(pointID);
    	getPointAccumulator().setPointID(pointID);
    }
    
    public void update() throws java.sql.SQLException {
    	super.update();
    	getPointAccumulator().update();
    }
}
