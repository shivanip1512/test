package com.cannontech.database.data.point;

/**
 * This type was created in VisualAge.
 */
import java.util.List;

import com.cannontech.database.db.point.DynamicCalcHistorical;
import com.cannontech.database.db.point.calculation.CalcBase;
import com.cannontech.database.db.point.calculation.CalcComponent;
import com.cannontech.database.db.point.calculation.CalcPointBaseline;
import com.google.common.collect.Lists;

public class CalcStatusPoint extends StatusPoint {
	private CalcBase calcBase = null;
	private List<CalcComponent> calcComponents = null;
	private CalcPointBaseline calcBaselinePoint = null;
	private boolean baselineAssigned = false;
/**
 * CalculatedPoint constructor comment.
 */
public CalcStatusPoint() {
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
   	
   java.util.GregorianCalendar gc = new java.util.GregorianCalendar();
   gc.setTime( new java.util.Date() );
   gc.set( gc.DAY_OF_YEAR, (gc.get(gc.DAY_OF_YEAR) - 30) );
   d.setLastUpdate(gc);
   
   
	d.setDbConnection( getDbConnection() );
	d.add();

    for (CalcComponent calcComponent : getCalcComponents()) {
        calcComponent.add();
    }

	if(baselineAssigned)
	{
		getCalcBaselinePoint().setPointID(getPoint().getPointID());
		getCalcBaselinePoint().add();
	}
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
	CalcComponent.deleteCalcComponents( getPoint().getPointID(), getDbConnection() );
	if(! baselineAssigned)
		CalcPointBaseline.deleteCalcBaselinePoint( getPoint().getPointID(), getDbConnection() );
	
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

public List<CalcComponent> getCalcComponents() {

	if( calcComponents == null )
		calcComponents = Lists.newArrayList();
	
	return calcComponents;
}
/**
 * This method was created in VisualAge.
 * @return CalcPointBaseline
 */
public CalcPointBaseline getCalcBaselinePoint() {

	if(calcBaselinePoint == null)
		calcBaselinePoint = new CalcPointBaseline();
		
	return calcBaselinePoint;
}

public boolean getBaselineAssigned() {
	return baselineAssigned;
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException{
	super.retrieve();

	getCalcBase().retrieve();

	calcComponents = CalcComponent.getCalcComponents(getPoint().getPointID());
	
	calcBaselinePoint = CalcPointBaseline.getCalcBaselinePoint(getPoint().getPointID());
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.point.Point
 */
public void setCalcBase(CalcBase newValue) {
	this.calcBase = newValue;
}

public void setCalcComponents(List<CalcComponent> newValue) {
	this.calcComponents = newValue;
}

public void setCalcBaselinePoint(CalcPointBaseline newValue) {
	this.calcBaselinePoint = newValue;
}

public void setBaselineAssigned(boolean truthValue) {
	baselineAssigned = truthValue;
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
	
	for (CalcComponent calcComponent : getCalcComponents()) {
	    calcComponent.setDbConnection(conn);
	}

	((CalcPointBaseline) getCalcBaselinePoint()).setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public void setPointID(Integer pointID) {
	super.setPointID(pointID);

	getCalcBase().setPointID(pointID);

    for (CalcComponent calcComponent : getCalcComponents()) {
        calcComponent.setPointID(pointID);
    }
	
	((CalcPointBaseline) getCalcBaselinePoint()).setPointID(pointID);		
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException {
	super.update();

	getCalcBase().update();

	CalcComponent.deleteCalcComponents( getPoint().getPointID(), getDbConnection() );
    for (CalcComponent calcComponent : getCalcComponents()) {
        calcComponent.add();
    }
	
	CalcPointBaseline.deleteCalcBaselinePoint(getPoint().getPointID(), getDbConnection());	
	
	if(baselineAssigned)
	{
		getCalcBaselinePoint().setPointID(getPoint().getPointID());
		getCalcBaselinePoint().add();
	}
	
}

public static boolean inUseByPoint(Integer baselineID, String databaseAlias)
{
	return com.cannontech.database.db.point.calculation.CalcPointBaseline.inUseByPoint(baselineID, databaseAlias);
}

}
