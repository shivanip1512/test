package com.cannontech.database.data.lite;

/**
 * This type was created in VisualAge.
 */
public class LiteUnitMeasure extends LiteBase
{
	private String unitMeasureName = null;
	private int unitMeasureCalcType;
	private String longName = null;
	
/**
 * PointUnit constructor comment.
 */
public LiteUnitMeasure(int umID, String umName, int umCalcType, String longName_ ) 
{
	super();
	unitMeasureName = umName;
	unitMeasureCalcType = umCalcType;
	setLongName( longName_ );
	setLiteID(umID);
	setLiteType( LiteTypes.UNITMEASURE );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public int getUnitMeasureCalcType() {
	return unitMeasureCalcType;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String getUnitMeasureName() {
	return unitMeasureName;
}
/**
 * Insert the method's description here.
 * Creation date: (9/21/2001 3:46:06 PM)
 * @return int
 */
public int getUomID() {
	return getLiteID();
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setUnitMeasureCalcType(int newValue) {
	this.unitMeasureCalcType = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.String
 */
public void setUnitMeasureName(String newValue) {
	this.unitMeasureName = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (9/21/2001 3:46:06 PM)
 * @param newUomID int
 */
public void setUomID(int newUomID) 
{
	setLiteID(newUomID);
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return getLongName();
}
	/**
	 * @return
	 */
	public String getLongName()
	{
		return longName;
	}

	/**
	 * @param string
	 */
	public void setLongName(String string)
	{
		longName = string;
	}

}
