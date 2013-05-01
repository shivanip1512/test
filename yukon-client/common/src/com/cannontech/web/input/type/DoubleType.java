package com.cannontech.web.input.type;

import java.beans.PropertyEditor;

import org.springframework.beans.propertyeditors.CustomNumberEditor;

public class DoubleType extends DefaultValidatedType<Double>{
	private double minValue = 0;
	private double maxValue = 0;
	private String renderer = null;
	
	public DoubleType() {
		setRenderer("integerType.jsp");
	}

	@Override
	public String getRenderer() {
		return renderer;
	}
	
	public void setRenderer(String renderer) {
		this.renderer = renderer;
	}
	
	public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }
	
	@Override
	public PropertyEditor getPropertyEditor() {
		return new CustomNumberEditor(Double.class, true);
	}

	@Override
	public Class<Double> getTypeClass() {
		return Double.class;
	}
}
