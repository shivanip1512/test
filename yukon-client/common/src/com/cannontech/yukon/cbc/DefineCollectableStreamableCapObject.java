package com.cannontech.yukon.cbc;

/**
 * Insert the type's description here.
 * Creation date: (8/17/00 3:21:47 PM)
 * @author: 
 */
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableStreamableCapObject implements com.roguewave.vsj.DefineCollectable
{
/**
 * DefineCollectableCapBankVector constructor comment.
 */
public DefineCollectableStreamableCapObject() {
	super();
}
/**
 * This method is called from CollectableStreamer to create a new instance
 * of Schedule.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException 
{
	//I dont think this method will ever be called
	//return new StreamableCapObject();
	return null;
}
/**
 * getComparator method comment.
 */
public com.roguewave.tools.v2_0.Comparator getComparator() 
{
	return new Comparator() 
	{
		public int compare(Object x, Object y) 
		{
			return (int) (((StreamableCapObject)x).getCcId().intValue() - ((StreamableCapObject)y).getCcId().intValue() );
		}
	};
	
}
/**
 * getCxxClassId method comment.
 */
public int getCxxClassId() 
{
	return DefineCollectable.NO_CLASSID;
}
/**
 * getCxxStringId method comment.
 */
public String getCxxStringId() {
	return DefineCollectable.NO_STRINGID;
}
/**
 * getJavaClass method comment.
 */
public Class getJavaClass() {
	return StreamableCapObject.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	StreamableCapObject capObj = (StreamableCapObject)obj;
	
	capObj.setCcId( new Integer( (int)vstr.extractUnsignedInt() ) );
	capObj.setCcCategory( (String)vstr.restoreObject( SimpleMappings.CString ) );
	capObj.setCcClass( (String)vstr.restoreObject( SimpleMappings.CString ) );
	capObj.setCcName( (String)vstr.restoreObject( SimpleMappings.CString ) );
	capObj.setCcType( (String)vstr.restoreObject( SimpleMappings.CString ) );
	capObj.setCcArea( (String)vstr.restoreObject( SimpleMappings.CString ) );
	
	capObj.setCcDisableFlag( 
			((int)vstr.extractUnsignedInt() == 1) 
			? new Boolean(true) : new Boolean(false)  );
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	StreamableCapObject capObj = (StreamableCapObject)obj;

	vstr.insertUnsignedInt( capObj.getCcId().intValue() );
	vstr.saveObject( capObj.getCcCategory(), SimpleMappings.CString );
	vstr.saveObject( capObj.getCcClass(), SimpleMappings.CString );
	vstr.saveObject( capObj.getCcName(), SimpleMappings.CString );
	vstr.saveObject( capObj.getCcType(), SimpleMappings.CString );
	vstr.saveObject( capObj.getCcArea(), SimpleMappings.CString );
	
	vstr.insertUnsignedInt( 
		(capObj.getCcDisableFlag().booleanValue() == true)
		? 1 : 0 );
}
}
