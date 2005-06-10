/*
 * Created on Mar 8, 2004
 */
package com.cannontech.message.server;

import java.io.IOException;

import com.cannontech.message.util.DefineCollectableMessage;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;
import com.roguewave.vsj.streamer.SimpleMappings;

/**
 * DefineCollectable class for ServerResponseMsg
 * @author aaron
 */
public class DefineCollectableServerResponse extends DefineCollectableMessage {

	private static final int CLASSID = 1551;
	
	/* (non-Javadoc)
	 * @see com.roguewave.vsj.DefineCollectable#create(com.roguewave.vsj.VirtualInputStream)
	 */
	public Object create(VirtualInputStream arg0) throws IOException {
		return new ServerResponseMsg();
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
		return ServerResponseMsg.class;
	}

	/* (non-Javadoc)
	 * @see com.roguewave.vsj.DefineCollectable#restoreGuts(java.lang.Object, com.roguewave.vsj.VirtualInputStream, com.roguewave.vsj.CollectableStreamer)
	 */
	public void restoreGuts(Object o, VirtualInputStream vistr, CollectableStreamer strmr) throws IOException {
		super.restoreGuts(o, vistr, strmr);
		ServerResponseMsg sResp = (ServerResponseMsg) o;
		sResp.setId(vistr.extractInt());
		sResp.setStatus(vistr.extractInt());
		sResp.setMessage((String) vistr.restoreObject(SimpleMappings.CString));
		
		//flag to indicate if a payload is coming along
		if(vistr.extractInt() != 0) {
			sResp.setPayload(vistr.restoreObject(strmr));
		}
	}

	/* (non-Javadoc)
	 * @see com.roguewave.vsj.DefineCollectable#saveGuts(java.lang.Object, com.roguewave.vsj.VirtualOutputStream, com.roguewave.vsj.CollectableStreamer)
	 */
	public void saveGuts(Object o, VirtualOutputStream vostr, CollectableStreamer strmr) throws IOException {
		super.saveGuts(o, vostr, strmr);
        ServerResponseMsg msg = (ServerResponseMsg) o;
        
        vostr.insertInt(msg.getId());
        vostr.insertInt(msg.getStatus());
        vostr.saveObject(msg.getMessage(), SimpleMappings.CString);
        if (msg.getPayload() != null) {
            vostr.insertInt(1); // flag to indicate payload follows
            vostr.saveObject(msg.getPayload(), strmr);
        } else {
            vostr.insertInt(0);
        }
	}

}
