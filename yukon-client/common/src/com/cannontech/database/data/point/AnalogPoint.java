package com.cannontech.database.data.point;

import com.cannontech.database.db.point.PointAnalog;
import com.cannontech.database.db.point.PointAnalogControl;

public class AnalogPoint extends ScalarPoint {
	
    private PointAnalog pointAnalog = null;
    private PointAnalogControl pointAnalogControl = null;
    
    public AnalogPoint() {
    	super();
    }

    public void add() throws java.sql.SQLException {
    	super.add();
    	getPointAnalog().add();
    	getPointAnalogControl().add();
    }

    public void addPartial() throws java.sql.SQLException {
        super.addPartial();
    	getPointAnalog().add();
    	getPointAnalogControl().add();
    }

    public void delete() throws java.sql.SQLException {
        getPointAnalogControl().delete();
    	getPointAnalog().delete();
    	super.delete();		
    }

    public void deletePartial() throws java.sql.SQLException {
        getPointAnalogControl().delete();
        getPointAnalog().delete();
    	super.deletePartial();
    }

    public PointAnalog getPointAnalog() {
    	if (pointAnalog == null) {
    		pointAnalog = new PointAnalog();
    	}
    	return pointAnalog;
    }
    
    public PointAnalogControl getPointAnalogControl() {
        if (pointAnalogControl == null) {
            pointAnalogControl = new PointAnalogControl();
        }
        return pointAnalogControl;
    }
    
    public void retrieve() throws java.sql.SQLException {
    	super.retrieve();
    	getPointAnalog().retrieve();
    	getPointAnalogControl().retrieve();
    }

    public void setDbConnection(java.sql.Connection conn) {
    	super.setDbConnection(conn);
    	getPointAnalog().setDbConnection(conn);
    	getPointAnalogControl().setDbConnection(conn);
    }
    
    public void setPointID(Integer pointID) {
    	super.setPointID(pointID);
    	getPointAnalog().setPointID(pointID);
    	getPointAnalogControl().setPointID(pointID);
    }

    public void update() throws java.sql.SQLException {
    	super.update();
    	getPointAnalog().update();
    	getPointAnalogControl().update();
    }
}