package com.cannontech.database.db.route;

/**
 * This type was created in VisualAge.
 */
public class VersacomRoute extends com.cannontech.database.db.DBPersistent {
	private Integer routeID = null;
	private Integer utilityID = null;
	private Integer sectionAddress = null;
	private Integer classAddress = null;
	private Integer divisionAddress = null;
	private Integer busNumber = null;
	private Integer ampCardSet = null;

	private static final String tableName = "VersacomRoute";
/**
 * VcomTransportRoute constructor comment.
 */
public VersacomRoute() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {
	Object addValues[] = { getRouteID(), getUtilityID(), getSectionAddress(), getClassAddress(), 
						   getDivisionAddress(), getBusNumber(), getAmpCardSet() };

	add( this.tableName, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {
	delete( this.tableName, "RouteID", getRouteID() );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getAmpCardSet() {
	return ampCardSet;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getBusNumber() {
	return busNumber;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getClassAddress() {
	return classAddress;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getDivisionAddress() {
	return divisionAddress;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getRouteID() {
	return routeID;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getSectionAddress() {
	return sectionAddress;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getUtilityID() {
	return utilityID;
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {

	String selectColumns[] = { "UtilityID", "SectionAddress", "ClassAddress", "DivisionAddress", "BusNumber", "AmpCardSet" };

	String constraintColumns [] = { "RouteID" };
	Object constraintValues[] = { getRouteID() };

	Object results[] = retrieve(selectColumns, this.tableName, constraintColumns, constraintValues );

	if( results.length == selectColumns.length )
	{
		setUtilityID( (Integer) results[0] );
		setSectionAddress( (Integer) results[1] );
		setClassAddress( (Integer) results[2] );
		setDivisionAddress( (Integer) results[3] );
		setBusNumber( (Integer) results[4] );
		setAmpCardSet( (Integer) results[5] );
	}
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setAmpCardSet(Integer newValue) {
	this.ampCardSet = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setBusNumber(Integer newValue) {
	this.busNumber = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setClassAddress(Integer newValue) {
	this.classAddress = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setDivisionAddress(Integer newValue) {
	this.divisionAddress = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setRouteID(Integer newValue) {
	this.routeID = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setSectionAddress(Integer newValue) {
	this.sectionAddress = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setUtilityID(Integer newValue) {
	this.utilityID = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {
	String setColumns[] = { "UtilityID", "SectionAddress", "ClassAddress", "DivisionAddress", "BusNumber", "AmpCardSet" };
	Object setValues[] = { getUtilityID(), getSectionAddress(), getClassAddress(), getDivisionAddress(), getBusNumber(), getAmpCardSet()  };

	String constraintColumns[] = { "RouteID" };
	Object constraintValues[] = { getRouteID() };

	update(this.tableName, setColumns, setValues, constraintColumns, constraintValues );
	
}
}
