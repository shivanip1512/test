package com.cannontech.loadcontrol.data;

/**
 * Creation date: (5/28/2001 2:08:29 PM)
 * @author: Aaron Lauinger
 */
import java.util.Vector;

import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableLMEnergyExchangeCustomer implements com.roguewave.vsj.DefineCollectable 
{
	//The roguewave class id
	private static int CTILMENERGYEXCHANGECUSTOMER_ID = 615;	
 /**
	* This method will be called by CollectableStreamer to
	* create an instance of the object being
	* restored.  Note that the guts (internal state) of the object
	* will be restored in the restoreGuts() method and should not
	* be restored here.  You may, if you must, carefully read built-in
	* types from the supplied stream if they are necessary for the
	* creation of the object.  You may <em>not</em>, however, do
	* anything which would result in a recursive call
	* to <tt>vstr.restoreObject()</tt>.  This would cause the read table
	* to be out of sync and the results could be catastrophic.
	*/
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
	return new LMEnergyExchangeCustomer();
}
 /**
	* Return a Comparator object for the Java class being mapped.
	* For consistency when streaming objects between Java and C++, the
	* semantics of the Comparator returned here should match that of
	* the possibly-overridden RWCollectable::compareTo() method
	* from the C++ side.
	*/
public com.roguewave.tools.v2_0.Comparator getComparator() {
	return new Comparator() 
	{
		public int compare(Object x, Object y) 
		{
			return (int) (((LMEnergyExchangeCustomer)x).getYukonID().intValue() - ((LMEnergyExchangeCustomer)y).getYukonID().intValue() );
		}
	};
}
 /**
	* This method must return the C++ ClassId when the C++ class
	* being mapped uses a ClassId and not a StringId.  If the class
	* being mapped uses a StringId, this method should return
	* the value NO_CLASSID.  This method is used by CollectableStreamer
	* during registration.
	*/
public int getCxxClassId() {
	return CTILMENERGYEXCHANGECUSTOMER_ID;
}
 /**
	* This method must return the C++ StringId when the C++ class
	* being mapped uses a StringId and not a ClassId.  If the class
	* being mapped uses a ClassId, this method <em>must</em> return
	* the value NO_STRINGID.  This method is used by CollectableStreamer
	* during registration.
	*/
public String getCxxStringId() {
	return DefineCollectable.NO_STRINGID;
}
 /**
	* This method must return a java Class object for the Java class
	* being mapped.  It is used by CollectableStreamer during registration.
	*/
public Class getJavaClass() {
	return LMEnergyExchangeCustomer.class;
}
 /**
	* This method will be called by CollectableStreamer to restore the guts,
	* or internal state, of the object being restored.  Here you may
	* make recursive calls to <tt>vstr.restoreObject()</tt> for which you
	* will probably want to use the supplied CollectableStreamer.
	*
	* Be careful with the booleans, it just so happens they are unsigned int
	* on the c++ side as it stands.
	*
	* see lmenergyexchangecustomer.cpp
	*/
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	LMEnergyExchangeCustomer cust = (LMEnergyExchangeCustomer) obj;

	cust.setYukonID( new Integer((int)vstr.extractUnsignedInt()) );
	cust.setYukonCategory( (String) vstr.restoreObject(SimpleMappings.CString) );
	cust.setYukonClass( (String) vstr.restoreObject(SimpleMappings.CString) );
	cust.setYukonName( (String) vstr.restoreObject(SimpleMappings.CString) );
	cust.setYukonType( new Integer((int)vstr.extractUnsignedInt()) );
	cust.setYukonDescription( (String) vstr.restoreObject(SimpleMappings.CString) );
	cust.setDisableFlag( (vstr.extractUnsignedInt() > 0 ? Boolean.TRUE : Boolean.FALSE ) );
	cust.setCustomerOrder( new Integer((int)vstr.extractUnsignedInt()) );
	cust.setCustomerTimeZone( (String) vstr.restoreObject(SimpleMappings.CString) );
	cust.setEnergyExchangeCustomerReplies( (Vector) vstr.restoreObject(polystr) );
		
}


 /**
	* This method will be called by CollectableStreamer to save the guts,
	* or internal state, of the given object to the stream.  Here you may
	* make recursive calls to <tt>vstr.saveObject()</tt> for which you
	* will probably want to use the supplied CollectableStreamer.	
	*
	* Be careful with the booleans, it just so happens they are unsigned int
	* on the c++ side as it stands
	*
	* see lmenergyexchangecustomer.cpp
	*/
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	LMEnergyExchangeCustomer cust = (LMEnergyExchangeCustomer) obj;
	
	vstr.insertUnsignedLong( cust.getYukonID().longValue() );
	vstr.saveObject( cust.getYukonName(), SimpleMappings.CString );
	vstr.saveObject( cust.getYukonDescription(), SimpleMappings.CString );
	vstr.insertUnsignedInt( ( cust.getDisableFlag().booleanValue() ? 1 : 0 ) );
	vstr.insertUnsignedLong( cust.getCustomerOrder().longValue() );
	vstr.saveObject( cust.getCustomerTimeZone(), SimpleMappings.CString );
	vstr.saveObject( cust.getEnergyExchangeCustomerReplies(), polystr );
}
}
