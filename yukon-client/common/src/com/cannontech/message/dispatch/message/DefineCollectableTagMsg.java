/*
 * Created on Sep 2, 2003
 */
package com.cannontech.message.dispatch.message;

import java.io.IOException;
import java.util.Date;

import com.cannontech.message.util.DefineCollectableMessage;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;
import com.roguewave.vsj.streamer.SimpleMappings;

/**
 * DefineCollectable for TagMsg
 * @author aaron
 */
public class DefineCollectableTagMsg extends DefineCollectableMessage implements DefineCollectable {
			
	private static final int classId = 1594;
		
	/* (non-Javadoc)
	 * @see com.roguewave.vsj.DefineCollectable#create(com.roguewave.vsj.VirtualInputStream)
	 */
	public Object create(VirtualInputStream vstr) throws IOException {
		return new TagMsg();
	}

	/* (non-Javadoc)
	 * @see com.roguewave.vsj.DefineCollectable#getComparator()
	 */
	public Comparator getComparator() {
		return new Comparator() {
		  public int compare(Object x, Object y) {
				if( x == y )
					return 0;
				else
					return -1;
		  }
		};
	}

	/* (non-Javadoc)
	 * @see com.roguewave.vsj.DefineCollectable#getCxxClassId()
	 */
	public int getCxxClassId() {
		return classId;
	}

	/* (non-Javadoc)
	 * @see com.roguewave.vsj.DefineCollectable#getCxxStringId()
	 */
	public String getCxxStringId() {
		return DefineCollectable.NO_STRINGID;
	}

	/* (non-Javadoc)
	 * @see com.roguewave.vsj.DefineCollectable#getJavaClass()
	 */
	public Class getJavaClass() {
		return TagMsg.class;
	}

	/* (non-Javadoc)
	 * @see com.roguewave.vsj.DefineCollectable#restoreGuts(java.lang.Object, com.roguewave.vsj.VirtualInputStream, com.roguewave.vsj.CollectableStreamer)
	 */
	public void restoreGuts(
		Object obj,
		VirtualInputStream vstr,
		CollectableStreamer polystr)
		throws IOException {

		super.restoreGuts(obj, vstr, polystr);
		
		int instanceID = vstr.extractInt();
		int pointID = vstr.extractInt();
		int tagID = vstr.extractInt();
		String descriptionStr = (String) vstr.restoreObject(SimpleMappings.CString);
		int action = vstr.extractInt();
		Date tagTime = (Date) vstr.restoreObject(SimpleMappings.Time);
		String referenceStr = (String) vstr.restoreObject(SimpleMappings.CString);
		String taggedForStr = (String) vstr.restoreObject(SimpleMappings.CString);
		int msgID = vstr.extractInt();
		
		TagMsg msg = (TagMsg) obj;
		msg.setInstanceID(instanceID);
		msg.setPointID(pointID);
		msg.setTagID(tagID);
		msg.setDescriptionStr(descriptionStr);
		msg.setAction(action);
		msg.setTagTime(tagTime);
		msg.setReferenceStr(referenceStr);
		msg.setTaggedForStr(taggedForStr);
		msg.setClientMessageID(msgID);
	}

	/* (non-Javadoc)
	 * @see com.roguewave.vsj.DefineCollectable#saveGuts(java.lang.Object, com.roguewave.vsj.VirtualOutputStream, com.roguewave.vsj.CollectableStreamer)
	 */
	public void saveGuts(
		Object obj,
		VirtualOutputStream vstr,
		CollectableStreamer polystr)
		throws IOException {

		super.saveGuts(obj, vstr, polystr);
		
		TagMsg msg = (TagMsg) obj;
		vstr.insertInt(msg.getInstanceID());
		vstr.insertInt(msg.getPointID());
		vstr.insertInt(msg.getTagID());
		vstr.saveObject(msg.getDescriptionStr(), SimpleMappings.CString);
		vstr.insertInt(msg.getAction());
		vstr.saveObject(msg.getTagTime(), SimpleMappings.Time);
		vstr.saveObject(msg.getReferenceStr(), SimpleMappings.CString);
		vstr.saveObject(msg.getTaggedForStr(), SimpleMappings.CString);
		vstr.insertInt(msg.getClientMessageID());
	}

}