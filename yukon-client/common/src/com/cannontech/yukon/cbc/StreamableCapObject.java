package com.cannontech.yukon.cbc;

/**
 * Insert the type's description here.
 * Creation date: (11/19/2001 11:25:45 AM)
 * @author: 
 */
public abstract class StreamableCapObject 
{
	private Integer ccId = null;
	private String ccCategory = null;
	private String ccClass = null;
	private String ccName = null;
	private String ccType = null;
	private String ccArea = null;
	private Boolean ccDisableFlag = null;
/**
 * StreamableCapObject constructor comment.
 */
protected StreamableCapObject() {
	super();
}
/**
 * StreamableCapObject constructor comment.
 */
public StreamableCapObject( Integer ccId_, String ccCategory_, String ccClass_,
				String ccName_, String ccType_, String ccDescription_, 
				Boolean ccDisableFlag_ )
{
	super();

	setCcId( ccId_ );
	setCcCategory( ccCategory_ );
	setCcClass( ccClass_ );
	setCcName( ccName_ );
	setCcType( ccType_ );
	setCcArea( ccDescription_ );
	setCcDisableFlag( ccDisableFlag_ );
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param obj
 */
public boolean equals(Object obj) {

	if( obj instanceof StreamableCapObject )
		return getCcId().equals( ((StreamableCapObject)obj).getCcId() );
	else
		return super.equals(obj);
}

/**
 * This method was created in VisualAge.
 * @return boolean
 */
public int hashCode() 
{
	if( getCcId() != null )
		return getCcId().intValue();
	else
		return super.hashCode();
}

/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 4:53:14 PM)
 * @return java.lang.String
 */
public java.lang.String getCcArea() {
	return ccArea;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 4:53:14 PM)
 * @return java.lang.String
 */
public java.lang.String getCcCategory() {
	return ccCategory;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 4:53:14 PM)
 * @return java.lang.String
 */
public java.lang.String getCcClass() {
	return ccClass;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 4:53:14 PM)
 * @return java.lang.Boolean
 */
public java.lang.Boolean getCcDisableFlag() {
	return ccDisableFlag;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 4:53:14 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCcId() {
	return ccId;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 4:53:14 PM)
 * @return java.lang.String
 */
public java.lang.String getCcName() {
	return ccName;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 4:53:14 PM)
 * @return java.lang.String
 */
public java.lang.String getCcType() {
	return ccType;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 4:53:14 PM)
 * @param newCcArea java.lang.String
 */
public void setCcArea(java.lang.String newCcArea) {
	ccArea = newCcArea;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 4:53:14 PM)
 * @param newCcCategory java.lang.String
 */
public void setCcCategory(java.lang.String newCcCategory) {
	ccCategory = newCcCategory;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 4:53:14 PM)
 * @param newCcClass java.lang.String
 */
public void setCcClass(java.lang.String newCcClass) {
	ccClass = newCcClass;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 4:53:14 PM)
 * @param newCcDisableFlag java.lang.Boolean
 */
public void setCcDisableFlag(java.lang.Boolean newCcDisableFlag) {
	ccDisableFlag = newCcDisableFlag;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 4:53:14 PM)
 * @param newCcId java.lang.Integer
 */
public void setCcId(java.lang.Integer newCcId) {
	ccId = newCcId;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 4:53:14 PM)
 * @param newCcName java.lang.String
 */
public void setCcName(java.lang.String newCcName) {
	ccName = newCcName;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 4:53:14 PM)
 * @param newCcType java.lang.String
 */
public void setCcType(java.lang.String newCcType) {
	ccType = newCcType;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 5:22:08 PM)
 * @return java.lang.String
 */
public String toString() 
{
	if( getCcName() != null )
		return getCcName();
	else
		return super.toString();
}
}
