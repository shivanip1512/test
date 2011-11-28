package com.cannontech.database.data.fdr;

import java.util.List;

import com.cannontech.database.db.point.fdr.FDRInterfaceOption;
import com.cannontech.database.db.point.fdr.FDRTranslation;
import com.google.common.collect.Lists;

/**
 * Insert the type's description here.
 * Creation date: (5/22/2001 4:24:30 PM)
 * @author: 
 */
public class FDRInterface extends com.cannontech.database.db.DBPersistent
{
	private com.cannontech.database.db.point.fdr.FDRInterface fdrInterface = null;

	//holds instances of com.cannontech.database.db.point.fdr.FDRInterfaceOption
	private java.util.Vector interfaceOptionVector = null;
/**
 * FDRInterface constructor comment.
 */
public FDRInterface() {
	super();
}
/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException {}
/**
 * This method was created by a SmartGuide.
 */
public void delete() throws java.sql.SQLException {}
/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 4:39:43 PM)
 * @return com.cannontech.database.db.point.fdr.FDRInterface
 */
public com.cannontech.database.db.point.fdr.FDRInterface getFdrInterface() 
{
	if( fdrInterface == null )
		fdrInterface = new com.cannontech.database.db.point.fdr.FDRInterface();

	return fdrInterface;
}
/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 4:25:25 PM)
 * @return java.util.Vector
 */
public java.util.Vector getInterfaceOptionVector() 
{
	if( interfaceOptionVector == null )
		interfaceOptionVector = new java.util.Vector(5);

	return interfaceOptionVector;
}

public List<FDRInterfaceOption> getInterfaceOptionList() {
    List<FDRInterfaceOption> options = Lists.newArrayList(getInterfaceOptionVector());
    
    return options;
}
/**
 * This method was created by a SmartGuide.
 */
public void retrieve() throws java.sql.SQLException {}
/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 4:39:43 PM)
 * @param newFdrInterface com.cannontech.database.db.point.fdr.FDRInterface
 */
public void setFdrInterface(com.cannontech.database.db.point.fdr.FDRInterface newFdrInterface) {
	fdrInterface = newFdrInterface;
}
/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 4:25:25 PM)
 * @param newInterfaceOptionVector java.util.Vector
 */
public void setInterfaceOptionVector(java.util.Vector newInterfaceOptionVector) {
	interfaceOptionVector = newInterfaceOptionVector;
}
/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 5:12:09 PM)
 * @return java.lang.String
 */
public String toString() 
{
	if( getFdrInterface() != null )
			return getFdrInterface().getInterfaceName();
		
	return null;
}
/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException {}



/**
 * Returns a valid translation string with the given inputs, else returns null
 */
public static String createFDRTranslation( String[] labels, String[] values, String pointType )  {

	if( labels == null || values == null 
			|| (labels.length != values.length) ) {
		return null;
	}
		
	StringBuffer translation = new StringBuffer();
	for( int i = 0; i < labels.length; i++ ) {

		//append the labels text
		translation.append( labels[i] + ":" );

		//append the values text
		translation.append( values[i] + ";" );
	}

	//For all interfaces, add the PointType to the end of the translation string
	translation.append( "POINTTYPE:" + pointType + ";" );

	return translation.toString();
}

/**
 * Creates a default translation string for the given interface
 */
public static FDRTranslation createDefaultTranslation( FDRInterface inter, Integer pointID, String pointType ) {
	
	if( inter == null || inter.getInterfaceOptionVector().size() <= 0
			|| inter.getFdrInterface().getAllDirections().length <= 0 )
		return new FDRTranslation(pointID);  //some dummy with no interface attributes

	String[] labels = new String[inter.getInterfaceOptionVector().size()];
	String[] values = new String[inter.getInterfaceOptionVector().size()];
	for( int i = 0; i < inter.getInterfaceOptionVector().size(); i++ ) {
	
		FDRInterfaceOption fdrOption = (FDRInterfaceOption)inter.getInterfaceOptionVector().get(i);
		
		labels[i] = fdrOption.getOptionLabel();
		values[i] = fdrOption.getAllOptionValues()[0]; //default to the first value in the list		
	}
	
	String dest = inter.getFdrInterface().getInterfaceName();
	for ( int i = 0; i < labels.length; i++) {
		String lowercase = labels[i].toLowerCase();
		if (lowercase.contains("destination")) {
			dest = values[i];
			break;
		}
	}
	
	return new FDRTranslation(pointID, inter.getFdrInterface().getAllDirections()[0],
							  inter.getFdrInterface().getInterfaceName(), dest, 
							  createFDRTranslation(labels, values,pointType));
}
}