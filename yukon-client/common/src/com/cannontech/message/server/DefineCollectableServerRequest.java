/*
 * Created on Mar 8, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.message.server;

import java.io.IOException;

import com.cannontech.message.util.DefineCollectableMessage;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;

/**
 * DefineCollectable class for ServerRequestMsg
 * @author aaron
 */
public class DefineCollectableServerRequest extends DefineCollectableMessage {

	private static final int CLASSID = 1550;
	
	/* (non-Javadoc)
	 * @see com.roguewave.vsj.DefineCollectable#create(com.roguewave.vsj.VirtualInputStream)
	 */
	public Object create(VirtualInputStream arg0) throws IOException {
		return new ServerRequestMsg();
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
		return CLASSID;
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
		return ServerRequestMsg.class;
	}

	/* (non-Javadoc)
	 * @see com.roguewave.vsj.DefineCollectable#restoreGuts(java.lang.Object, com.roguewave.vsj.VirtualInputStream, com.roguewave.vsj.CollectableStreamer)
	 */
	public void restoreGuts(Object o, VirtualInputStream vistr, CollectableStreamer strmr) throws IOException {
		super.restoreGuts(o, vistr, strmr);
        ServerRequestMsg msg = (ServerRequestMsg)o;
        msg.setId(vistr.extractInt());
        msg.setPayload(vistr.restoreObject(strmr));
	}

	/* (non-Javadoc)
	 * @see com.roguewave.vsj.DefineCollectable#saveGuts(java.lang.Object, com.roguewave.vsj.VirtualOutputStream, com.roguewave.vsj.CollectableStreamer)
	 */
	public void saveGuts(Object o, VirtualOutputStream vostr, CollectableStreamer strmr) throws IOException {
		super.saveGuts(o,vostr,strmr);
		ServerRequestMsg msg = (ServerRequestMsg) o;
		vostr.insertInt(msg.getId());
		vostr.saveObject(msg.getPayload(), strmr);
	}

}
