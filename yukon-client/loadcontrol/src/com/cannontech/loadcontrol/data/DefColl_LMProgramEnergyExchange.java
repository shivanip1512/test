package com.cannontech.loadcontrol.data;

/**
 * Creation date: (5/28/2001 2:33:27 PM)
 * @author: Aaron Lauinger
 */

import java.util.Vector;

import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefColl_LMProgramEnergyExchange extends DefColl_LMProgramBase
{
	//The roguewave class id
	private static int CTILMPROGRAMENERGYEXCHANGE_ID = 617;
/**
 * DefineCollectableLMProgramEnergyExchange constructor comment.
 */
public DefColl_LMProgramEnergyExchange() {
	super();
}
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
	return new LMProgramEnergyExchange();
}
 /**
	* Return a Comparator object for the Java class being mapped.
	* For consistency when streaming objects between Java and C++, the
	* semantics of the Comparator returned here should match that of
	* the possibly-overridden RWCollectable::compareTo() method
	* from the C++ side.
	*/
public com.roguewave.tools.v2_0.Comparator getComparator() {
	return new Comparator() {
	  public int compare(Object x, Object y) {
			if( x == y )
				return 0;
			else
				return -1;
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
	return CTILMPROGRAMENERGYEXCHANGE_ID;
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
	return LMProgramEnergyExchange.class;
}
 /**
	* This method will be called by CollectableStreamer to restore the guts,
	* or internal state, of the object being restored.  Here you may
	* make recursive calls to <tt>vstr.restoreObject()</tt> for which you
	* will probably want to use the supplied CollectableStreamer.
	*/
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	super.restoreGuts(obj,vstr,polystr);

	LMProgramEnergyExchange prog = (LMProgramEnergyExchange) obj;

	prog.setMinNotifyTime( new Integer((int)vstr.extractUnsignedInt()) );
	prog.setHeading( (String) vstr.restoreObject( SimpleMappings.CString ));
	prog.setMessageHeader( (String) vstr.restoreObject( SimpleMappings.CString ));
	prog.setMessageFooter( (String) vstr.restoreObject( SimpleMappings.CString ));
	prog.setCanceledMsg( (String) vstr.restoreObject( SimpleMappings.CString ));
	prog.setStoppedEarlyMsg( (String) vstr.restoreObject( SimpleMappings.CString ))	;
	prog.setEnergyExchangeOffers( (Vector) vstr.restoreObject( polystr ));
	prog.setEnergyExchangeCustomers( (Vector) vstr.restoreObject( polystr ));
}
 /**
	* This method will be called by CollectableStreamer to save the guts,
	* or internal state, of the given object to the stream.  Here you may
	* make recursive calls to <tt>vstr.saveObject()</tt> for which you
	* will probably want to use the supplied CollectableStreamer.
	*/
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	super.saveGuts(obj,vstr,polystr);

	LMProgramEnergyExchange prog = (LMProgramEnergyExchange) obj;

	vstr.insertUnsignedLong( prog.getMinNotifyTime().longValue() );
	vstr.saveObject( prog.getHeading(), SimpleMappings.CString );
	vstr.saveObject( prog.getMessageHeader(), SimpleMappings.CString );
	vstr.saveObject( prog.getMessageFooter(), SimpleMappings.CString );
	vstr.saveObject( prog.getCanceledMsg(), SimpleMappings.CString );
	vstr.saveObject( prog.getStoppedEarlyMsg(), SimpleMappings.CString );
	vstr.saveObject( prog.getEnergyExchangeOffers(), polystr );
	vstr.saveObject( prog.getEnergyExchangeCustomers(), polystr );
}
}
