package com.cannontech.yukon.cbc;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.database.db.state.State;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableState implements com.roguewave.vsj.DefineCollectable 
{
	//The roguewave class id
	private static int CTICCSTATE_ID = 507;
/**
 * DefineCollectableSchedule constructor comment.
 */
public DefineCollectableState() {
	super();
}
/**
 * This method is called from CollectableStreamer to create a new instance
 * of Schedule.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
	return new State();
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
			return (int) (((State)x).getRawState().intValue() - ((State)y).getRawState().intValue() );
		}
	};
	
}
/**
 * getCxxClassId method comment.
 */
public int getCxxClassId() {
	return this.CTICCSTATE_ID;
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
	return State.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	State state = (State) obj;
	
	String text = (String) vstr.restoreObject( SimpleMappings.CString );
	Integer fgColor = new Integer( (int)vstr.extractUnsignedInt() );
	Integer bgColor = new Integer( (int)vstr.extractUnsignedInt() );

	state.setText(text);
	state.setForegroundColor(fgColor);
	state.setBackgroundColor(bgColor);

	// set the stateGroupId to that of all CapBank states
	state.setStateGroupID( new Integer(com.cannontech.database.db.state.StateGroupUtils.STATEGROUPID_CAPBANK) );
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {

	State state = (State) obj;
	
	vstr.saveObject( state.getText(), SimpleMappings.CString );
	vstr.insertUnsignedInt( state.getForegroundColor().intValue() );
	vstr.insertUnsignedInt( state.getBackgroundColor().intValue() );
}
}
