package com.cannontech.tdc.debug;

/**
 * Insert the type's description here.
 * Creation date: (3/5/2001 10:36:05 AM)
 * @author: 
 */
import com.cannontech.tdc.PointValues;

public class RowDebugViewer extends com.cannontech.debug.gui.AbstractListDataViewer 
{
	private PointValues pointValue = null;
/**
 * ScheduleDebugViewer constructor comment.
 */
public RowDebugViewer() {
	super();
}
/**
 * ScheduleDebugViewer constructor comment.
 * @param owner java.awt.Dialog
 */
public RowDebugViewer(java.awt.Dialog owner) {
	super(owner);
}
/**
 * ScheduleDebugViewer constructor comment.
 * @param owner java.awt.Dialog
 * @param title java.lang.String
 */
public RowDebugViewer(java.awt.Dialog owner, String title) {
	super(owner, title);
}
/**
 * ScheduleDebugViewer constructor comment.
 * @param owner java.awt.Dialog
 * @param title java.lang.String
 * @param modal boolean
 */
public RowDebugViewer(java.awt.Dialog owner, String title, boolean modal) {
	super(owner, title, modal);
}
/**
 * ScheduleDebugViewer constructor comment.
 * @param owner java.awt.Dialog
 * @param modal boolean
 */
public RowDebugViewer(java.awt.Dialog owner, boolean modal) {
	super(owner, modal);
}
/**
 * ScheduleDebugViewer constructor comment.
 * @param owner java.awt.Frame
 */
public RowDebugViewer(java.awt.Frame owner) {
	super(owner);
}
/**
 * ScheduleDebugViewer constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 */
public RowDebugViewer(java.awt.Frame owner, String title) {
	super(owner, title);
}
/**
 * ScheduleDebugViewer constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 * @param modal boolean
 */
public RowDebugViewer(java.awt.Frame owner, String title, boolean modal) {
	super(owner, title, modal);
}
/**
 * ScheduleDebugViewer constructor comment.
 * @param owner java.awt.Frame
 * @param modal boolean
 */
public RowDebugViewer(java.awt.Frame owner, boolean modal) {
	super(owner, modal);
}
/**
 * Insert the method's description here.
 * Creation date: (3/5/2001 10:36:05 AM)
 */
public Object getValue() 
{
	return pointValue;
}
/**
 * Insert the method's description here.
 * Creation date: (3/5/2001 10:36:05 AM)
 */
public void setValue(Object o) 
{
	pointValue = (PointValues)o;
	java.util.Vector data = new java.util.Vector(20);
	setTitle("CTI Row Debug");

	try
	{
		data.addElement("Pt Name   : " + pointValue.getPointName());
		data.addElement("Pt State  : " + pointValue.getPointState());
		data.addElement("Dec Plces : " + pointValue.getDecimalPlaces());
		data.addElement("Dev State : " + pointValue.getDeviceCurrentState());
		data.addElement("Dev ID    : " + pointValue.getDeviceID());
		data.addElement("Dev Name  : " + pointValue.getDeviceName());
		data.addElement("Dev Type  : " + pointValue.getDeviceType());
		
		String colorString = new String("FG Colors :");
		for( int i = 0; i < pointValue.getColorCount(); i++ )
			colorString += " '" + pointValue.getColor(i) + "'";
		data.addElement(colorString);

		String textString = new String("Text      :");
		for( int i = 0; i < pointValue.getAllText().length; i++ )	
			textString += " '" + pointValue.getAllText()[i] + "'";
		data.addElement(textString);

		data.addElement(" ");
		data.addElement("POINTDATA MESSAGE");
		data.addElement("------------------------------");
		if( pointValue.getPointData() != null )
		{		
			data.addElement("Pt ID     : " + new Long(pointValue.getPointData().getId()) );
			data.addElement("Tags(HX)  : " + Long.toHexString(pointValue.getPointData().getTags()) );
			data.addElement("Forced(HX): " + Long.toHexString(pointValue.getPointData().getForced()) );
			data.addElement("Atribs(HX): " + Long.toHexString(pointValue.getPointData().getAttributes()) );
			data.addElement("Limit     : " + new Long(pointValue.getPointData().getLimit()) );
			data.addElement("TimeStamp : " + pointValue.getPointData().getPointDataTimeStamp() );
			data.addElement("Priority  : " + pointValue.getPointData().getPriority() );
			data.addElement("Quality   : " + com.cannontech.database.data.point.PointQualities.getQuality( new Long(pointValue.getPointData().getQuality()).intValue() ) + "("+pointValue.getPointData().getQuality()+")" );
			data.addElement("String    : " + pointValue.getPointData().getStr() );
			data.addElement("SOE Tag   : " + pointValue.getPointData().getSOE_Tag() );
			
			String type = null;
			if(pointValue.getPointData().getType() != com.cannontech.tdc.utils.TDCDefines.ROW_BREAK_ID )
				type = com.cannontech.database.data.point.PointTypes.getType(pointValue.getPointData().getType());
			else
				type = pointValue.getPointName();
			data.addElement("Type      : " + type +"("+pointValue.getPointData().getType()+")" );
			
			data.addElement("Value     : " + new Double(pointValue.getPointData().getValue()) );
		}
		else
			data.addElement("   (null)");	
	}
	catch(Throwable t )  // Catch ALL things and just print them out
	{
		System.out.println("*** Throwable caught in + " + this.getClass() + " : " + t.getMessage() );
	}
		
	getJListInfo().setListData(data);
}
}
