package com.cannontech.loadcontrol.messages;

/**
 * Creation date: (5/16/2001 1:03:53 PM)
 * @author: Aaron Lauinger
 */
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;
 
public class DefineCollectableLMOfferSubmission extends DefineCollectableLMMessage 
{
	//RogueWave classId
	public static final int CTILMOFFERSUBMISSION_ID = 615;
/**
 * DefineCollectableLMOfferSubmission constructor comment.
 */
public DefineCollectableLMOfferSubmission() {
	super();
}
/**
 * create method comment.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
	return new LMOfferSubmission();
}
/**
 * getComparator method comment.
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
 * getCxxClassId method comment.
 */
public int getCxxClassId()
{
	return CTILMOFFERSUBMISSION_ID;
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
public Class getJavaClass() {
	return LMMessage.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {
	throw new RuntimeException("restoreGuts not supported");	
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {
	super.saveGuts( obj, vstr, polystr );

	LMOfferSubmission msg = (LMOfferSubmission) obj;
	
	vstr.insertUnsignedLong( msg.getProgramID() );
	vstr.saveObject( msg.getOfferDate(), SimpleMappings.Time );
	vstr.saveObject( msg.getNotificationDate(), SimpleMappings.Time );
	vstr.saveObject( msg.getExpirationDate(), SimpleMappings.Time );

	int[] targetLoad = msg.getTargetLoad();

	for( int i = 0; i < 24; i++ )
		vstr.insertUnsignedInt(targetLoad[i]);

	int[] pricePerHour = msg.getPricePerHour();
	
	for( int i = 0; i < 24; i++ )
		vstr.insertUnsignedInt(pricePerHour[i]);	
}
}
