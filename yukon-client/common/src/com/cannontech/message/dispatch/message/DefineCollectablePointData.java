package com.cannontech.message.dispatch.message;

/**
 * Insert the type's description here.
 * Creation date: (1/28/00 11:53:13 AM)
 * @author: 
 */
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectablePointData extends com.cannontech.message.util.DefineCollectableMessage implements com.roguewave.vsj.DefineCollectable {
	private static final int classId = 1595;
/**
 * DefineCollectablePointData constructor comment.
 */
public DefineCollectablePointData() {
	super();
}
/**
 * create method comment.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
	return new PointData();
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
	return PointData.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {
	super.restoreGuts(obj, vstr, polystr);
	
	PointData pData = (PointData) obj;

	pData.setId( vstr.extractLong() );
	pData.setType( vstr.extractInt() );
	pData.setQuality( vstr.extractUnsignedInt() );
	pData.setTags( vstr.extractUnsignedInt() );
	pData.setAttributes( vstr.extractUnsignedInt() );
	pData.setLimit( vstr.extractUnsignedInt() );
	pData.setValue( vstr.extractDouble());
	pData.setForced( vstr.extractUnsignedInt() );	
	pData.setStr( (String) vstr.restoreObject(SimpleMappings.CString));
	pData.setTime( (java.util.Date) vstr.restoreObject( SimpleMappings.Time ));
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {
	super.saveGuts( obj, vstr, polystr );

	PointData pData = (PointData) obj;

	vstr.insertLong( pData.getId() );
	vstr.insertInt( pData.getType() );
	vstr.insertUnsignedInt( pData.getQuality() );
	vstr.insertUnsignedInt( pData.getTags() );
	vstr.insertUnsignedInt( pData.getAttributes() );
	vstr.insertUnsignedInt( pData.getLimit() );
	vstr.insertDouble( pData.getValue() );
	vstr.insertUnsignedInt( pData.getForced() );	
	vstr.saveObject( pData.getStr(), SimpleMappings.CString );
	vstr.saveObject( pData.getPointDataTimeStamp(), SimpleMappings.Time );
}
}
