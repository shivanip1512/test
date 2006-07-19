package com.cannontech.message.dispatch.message;

/**
 * This type was created in VisualAge.
 */
import java.util.Set;

import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;

public class DefineCollectablePointRegistration extends com.cannontech.message.util.DefineCollectableMessage implements com.roguewave.vsj.DefineCollectable {
	private static final int classId = 1570;
/**
 * DefineCollectablePointRegistration constructor comment.
 */
public DefineCollectablePointRegistration() {
	super();
}
/**
 * create method comment.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
	return new PointRegistration();
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
	return PointRegistration.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {
	//No need to implement
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {
	super.saveGuts(obj, vstr, polystr );

	PointRegistration pReg = (PointRegistration) obj;

	vstr.insertInt( pReg.getRegFlags() );
	
    Set<Integer> pointIds = pReg.getPointIds();
    if(pointIds == null) {
        vstr.insertInt(0);
    }
    else {
        vstr.insertInt(pointIds.size());
        for (Integer id : pointIds) {
            vstr.insertLong(id);
        }
    }
}
}
