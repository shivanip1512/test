package com.cannontech.database.data.device.lm;

import java.io.Serializable;

public class LmXmlParameter implements Serializable{

	private int xmlParamId;
    private int lmGroupId;
    private String parameterName;
	private String parameterValue;
    
    public LmXmlParameter() {}
    
    public LmXmlParameter(int groupId, String name, String value) {
        this.parameterName = name;
        this.parameterValue = value;
    }
    
    public int getLmGroupId() {
        return lmGroupId;
    }
    
    public void setLmGroupId(int lmGroupId) {
        this.lmGroupId = lmGroupId;
    }
    
    public String getParameterName() {
        return parameterName;
    }
    
    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }
    
    public String getParameterValue() {
        return parameterValue;
    }
    
    public void setParameterValue(String parameterValue) {
        this.parameterValue = parameterValue;
    }
    
    public int getXmlParamId() {
		return xmlParamId;
	}

	public void setXmlParamId(int xmlParamId) {
		this.xmlParamId = xmlParamId;
	}
    
}
