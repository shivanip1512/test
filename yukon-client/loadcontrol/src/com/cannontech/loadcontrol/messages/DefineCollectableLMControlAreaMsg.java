package com.cannontech.loadcontrol.messages;

/**
 * This type was created in VisualAge.
 */
import java.util.List;

import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.message.util.CollectionExtracter;
import com.cannontech.message.util.CollectionInserter;
import com.roguewave.tools.v2_0.Comparator;

public class DefineCollectableLMControlAreaMsg extends DefineCollectableLMMessage
{
	//RogueWave classId
	public static final int CTILMCONTROLAREA_MSG_ID = 602;
/**
 * DefineCollectableMessage constructor comment.
 */
public DefineCollectableLMControlAreaMsg()
{
	super();
}
/**
 * create method comment.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException
{
	return new LMControlAreaMsg();
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
public int getCxxClassId() {
	return CTILMCONTROLAREA_MSG_ID;
}
/**
 * getJavaClass method comment.
 */
public Class getJavaClass()
{
	return LMControlAreaMsg.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {

	super.restoreGuts( obj, vstr, polystr );

	LMControlAreaMsg lmControlAreaMsg = (LMControlAreaMsg) obj;

	lmControlAreaMsg.setMsgInfoBitMask( new Integer( (int)vstr.extractUnsignedInt() ) );
	//java.util.Vector controlAreaVector = (java.util.Vector)vstr.restoreObject( polystr );
    java.util.Vector controlAreaVector = CollectionExtracter.extractVector(vstr,polystr);

	((LMControlAreaMsg)obj).setLMControlAreaVector(controlAreaVector);
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	super.saveGuts( obj, vstr, polystr );

	LMControlAreaMsg lmControlAreaMsg = (LMControlAreaMsg) obj;

	vstr.insertUnsignedInt( lmControlAreaMsg.getMsgInfoBitMask().intValue() );
	List<LMControlArea> areaVector = lmControlAreaMsg.getLMControlAreaVector();
	//vstr.saveObject( areaVector, com.roguewave.vsj.streamer.CollectableMappings.allCollectables );
    CollectionInserter.insertVector(areaVector, vstr, polystr);
}
}
