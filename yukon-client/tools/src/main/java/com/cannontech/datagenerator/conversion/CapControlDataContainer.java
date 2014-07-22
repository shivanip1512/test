package com.cannontech.datagenerator.conversion;

public class CapControlDataContainer {
	String time;
	double value;
	
	int banksClosed;
	String operation = "";
	double coefValue;
	double capValue;
	
	public CapControlDataContainer(String time, double value) {
		this.time = time;
		this.value = value;
		
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public int getBanksClosed() {
		return banksClosed;
	}

	public void setBanksClosed(int banksClosed) {
		this.banksClosed = banksClosed;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public double getCoefValue() {
		return coefValue;
	}

	public void setCoefValue(double coefValue) {
		this.coefValue = coefValue;
	}

	public double getCapValue() {
		return capValue;
	}

	public void setCapValue(double capValue) {
		this.capValue = capValue;
	}

	public String[] toStringArray() {
		String[] strArray = new String[6];
		strArray[0] = getTime();
		strArray[1] = Double.toString(getValue());
		strArray[2] = Double.toString(getCapValue());
		strArray[3] = Integer.toString(getBanksClosed());
		strArray[4] = getOperation();
		strArray[5] = Double.toString(getCoefValue());
		
		return strArray;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + banksClosed;
		long temp;
		temp = Double.doubleToLongBits(capValue);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(coefValue);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((operation == null) ? 0 : operation.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		temp = Double.doubleToLongBits(value);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final CapControlDataContainer other = (CapControlDataContainer) obj;
		if (banksClosed != other.banksClosed)
			return false;
		if (Double.doubleToLongBits(capValue) != Double
				.doubleToLongBits(other.capValue))
			return false;
		if (Double.doubleToLongBits(coefValue) != Double
				.doubleToLongBits(other.coefValue))
			return false;
		if (operation == null) {
			if (other.operation != null)
				return false;
		} else if (!operation.equals(other.operation))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		if (Double.doubleToLongBits(value) != Double
				.doubleToLongBits(other.value))
			return false;
		return true;
	}
	
}
