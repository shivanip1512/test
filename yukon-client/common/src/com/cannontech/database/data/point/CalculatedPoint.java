package com.cannontech.database.data.point;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.database.db.point.DynamicCalcHistorical;
import com.cannontech.database.db.point.calculation.CalcBase;
import com.cannontech.database.db.point.calculation.CalcComponent;

public class CalculatedPoint extends ScalarPoint {
	private CalcBase calcBase = null;
	private java.util.Vector calcComponentVector = null;
/**
 * CalculatedPoint constructor comment.
 */
public CalculatedPoint() {
	super();
}
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException 
{
	super.add();

	getCalcBase().add();

	//add a DynamicClacHistorical row for this new calc point
	DynamicCalcHistorical d = new DynamicCalcHistorical();
	d.setPointID( getPoint().getPointID() );
	d.setLastUpdate( com.cannontech.common.util.CtiUtilities.get1990GregCalendar() );
	d.setDbConnection( getDbConnection() );
	d.add();

	
	for( int i = 0; i < getCalcComponentVector().size(); i++ )
		((CalcComponent) getCalcComponentVector().elementAt(i)).add();
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2001 10:50:46 AM)
 * @exception java.sql.SQLException The exception description.
 */
public void addPartial() throws java.sql.SQLException {

	getCalcBaseDefaults().add();
	super.addPartial();

}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException 
{
	CalcComponent.deleteCalcComponents(getPoint().getPointID());
	
	//a dynamic table used by the CalcHistorical application
	delete(DynamicCalcHistorical.TABLE_NAME, "PointID", getPoint().getPointID());
	
	getCalcBase().delete();	
	super.delete();
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2001 10:51:07 AM)
 * @exception java.sql.SQLException The exception description.
 */
public void deletePartial() throws java.sql.SQLException {


	super.deletePartial();
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.point.Point
 */
public CalcBase getCalcBase() {
	if( this.calcBase == null )
		this.calcBase = new CalcBase();
		
	return this.calcBase;
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2001 12:39:07 PM)
 * @return com.cannontech.database.db.point.calculation.CalcBase
 */
public CalcBase getCalcBaseDefaults()
{

	getCalcBase().setPeriodicRate(new Integer(1));
	getCalcBase().setUpdateType("On First Change");
	return getCalcBase();
}
/**
 * This method was created in VisualAge.
 * @return java.util.Vector
 */
public java.util.Vector getCalcComponentVector() {

	if( calcComponentVector == null )
		calcComponentVector = new java.util.Vector();
	
	return calcComponentVector;
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException{
	super.retrieve();

	getCalcBase().retrieve();

	calcComponentVector = CalcComponent.getCalcComponents(getPoint().getPointID());
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.point.Point
 */
public void setCalcBase(CalcBase newValue) {
	this.calcBase = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.util.Vector
 */
public void setCalcComponentVector(java.util.Vector newValue) {
	this.calcComponentVector = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getCalcBase().setDbConnection(conn);

	for( int i = 0; i < getCalcComponentVector().size(); i++ )
		((CalcComponent) getCalcComponentVector().elementAt(i)).setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public void setPointID(Integer pointID) {
	super.setPointID(pointID);

	getCalcBase().setPointID(pointID);

	for( int i = 0; i < getCalcComponentVector().size(); i++ )
		((CalcComponent) getCalcComponentVector().elementAt(i)).setPointID(pointID);
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException {
	super.update();

	getCalcBase().update();

	CalcComponent.deleteCalcComponents(getPoint().getPointID());
	for( int i = 0; i < getCalcComponentVector().size(); i++ )
		((CalcComponent) getCalcComponentVector().elementAt(i)).add();
}
}
