package com.cannontech.message.dispatchmessage;

/**
 * This type was created in VisualAge.
 */

import com.cannontech.message.util.CollectionExtracter;
import com.cannontech.message.util.CollectionInserter;
import com.cannontech.messaging.message.dispatch.MultiMessage;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;

public class DefineCollectableMulti extends com.cannontech.message.util.DefineCollectableMessage implements com.roguewave.vsj.DefineCollectable {
	private static final int classId = 1591;
/**
 * DefineCollectableMultiPointChange constructor comment.
 */
public DefineCollectableMulti() {
	super();
}
/**
 * create method comment.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
	return new MultiMessage();
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
	return classId;
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
	return MultiMessage.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {
	super.restoreGuts(obj, vstr, polystr);
	
	MultiMessage multi = (MultiMessage) obj;
	multi.setVector( CollectionExtracter.extractVector(vstr, polystr) );
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {
	super.saveGuts( obj, vstr, polystr );

	MultiMessage multi = (MultiMessage) obj;
	CollectionInserter.insertVector(multi.getVector(), vstr, polystr);
}
}
