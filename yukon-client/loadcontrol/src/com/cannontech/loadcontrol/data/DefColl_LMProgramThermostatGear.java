package com.cannontech.loadcontrol.data;

/**
 * This type was created in VisualAge.
 */
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefColl_LMProgramThermostatGear implements com.roguewave.vsj.DefineCollectable
{
	//The roguewave class id
	private static int CTI_LMPROGRAMTHERMOSTATGEAR_ID = 628;
/**
 * DefineCollectableSchedule constructor comment.
 */
public DefColl_LMProgramThermostatGear()
{
	super();
}
/**
 * This method is called from CollectableStreamer to create a new instance
 * of Schedule.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException
{
	return new LMProgramThermostatGear();
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
			return (int) 
					(((LMProgramThermostatGear)x).getYukonID().intValue() 
							- ((LMProgramThermostatGear)y).getYukonID().intValue() );
		}
	};
	
}
/**
 * getCxxClassId method comment.
 */
public int getCxxClassId()
{
	return CTI_LMPROGRAMTHERMOSTATGEAR_ID;
}
/**
 * getCxxStringId method comment.
 */
public String getCxxStringId()
{
	return DefineCollectable.NO_STRINGID;
}
/**
 * getJavaClass method comment.
 */
public Class getJavaClass()
{
	return LMProgramThermostatGear.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	LMProgramThermostatGear gear = (LMProgramThermostatGear) obj;

	gear.setSettings( (String) vstr.restoreObject( SimpleMappings.CString ) );
	gear.setMinValue( new Integer( (int)vstr.extractUnsignedInt() ) );

	gear.setMaxValue( new Integer( (int)vstr.extractUnsignedInt() ) );
	gear.setValueB( new Integer( (int)vstr.extractUnsignedInt() ) );
	gear.setValueD( new Integer( (int)vstr.extractUnsignedInt() ) );
	gear.setValueF( new Integer( (int)vstr.extractUnsignedInt() ) );
	gear.setRandom( new Integer( (int)vstr.extractUnsignedInt() ) );
	gear.setValueTa( new Integer( (int)vstr.extractUnsignedInt() ) );

	gear.setValueTb( new Integer( (int)vstr.extractUnsignedInt() ) );
	gear.setValueTc( new Integer( (int)vstr.extractUnsignedInt() ) );
	gear.setValueTd( new Integer( (int)vstr.extractUnsignedInt() ) );
	gear.setValueTe( new Integer( (int)vstr.extractUnsignedInt() ) );
	gear.setValueTf( new Integer( (int)vstr.extractUnsignedInt() ) );
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException
{
/* This saveGuts isn't implemented because we won't be sending full LMControlAreas
	 to the Server */
}
}
