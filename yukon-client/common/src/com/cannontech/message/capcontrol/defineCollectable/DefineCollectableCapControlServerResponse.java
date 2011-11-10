package com.cannontech.message.capcontrol.defineCollectable;

import com.cannontech.message.capcontrol.model.CapControlResponseType;
import com.cannontech.message.capcontrol.model.CapControlServerResponse;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableCapControlServerResponse extends DefineCollectableCapControlMessage {
	public static final int SERVERRESPONSEMSG_ID = 526;
	
	public DefineCollectableCapControlServerResponse() {
		super();
	}
	
	public Object create(VirtualInputStream strm) {
		return new CapControlServerResponse();
	}
	
	public int getCxxClassId() {
		return SERVERRESPONSEMSG_ID;
	}
	
	public String getCxxStringId() {
		return DefineCollectable.NO_STRINGID;
	}
	
	public Class<?> getJavaClass() {
		return CapControlServerResponse.class;
	}
	
	/**
	 * restoreGuts method comment.
	 */
	public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr) throws java.io.IOException {
		super.restoreGuts( obj, vstr, polystr );
		
		CapControlServerResponse cmd = (CapControlServerResponse) obj;

        cmd.setMessageId((int)vstr.extractUnsignedInt());
		cmd.setResponseType( CapControlResponseType.getResponseTypeById( (int)vstr.extractUnsignedInt()) );
		String message = (String) vstr.restoreObject( SimpleMappings.CString );
		cmd.setResponse( message );
	}

	/**
	 * saveGuts method comment.
	 */
	public void saveGuts(Object obj, VirtualOutputStream vstr, CollectableStreamer polystr) throws java.io.IOException 
	{
		super.saveGuts( obj, vstr, polystr );

		CapControlServerResponse cmd = (CapControlServerResponse) obj;

		vstr.insertUnsignedLong( cmd.getMessageId() );
		vstr.insertUnsignedInt( cmd.getResponseType().getTypeId() );
		vstr.saveObject( cmd.getResponse(), SimpleMappings.CString );
	}
}
