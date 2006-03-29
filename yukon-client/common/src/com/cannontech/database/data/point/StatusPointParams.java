package com.cannontech.database.data.point;

public class StatusPointParams extends PointParams {
	private int controlType = PointTypes.CONTROLTYPE_NONE;
	
	
    public StatusPointParams(int offset, String name) {
        super(offset, name);

    }
    
    public StatusPointParams (int offset, String name, int controlType_) {
    	super(offset, name);
    	controlType = controlType_;
    }
    
    public int getType() {

        return PointTypes.STATUS_POINT;
    }

	public int getControlType() {
		return controlType;
	}

}
