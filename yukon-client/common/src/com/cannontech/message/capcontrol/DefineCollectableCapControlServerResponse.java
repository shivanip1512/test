package com.cannontech.message.capcontrol;

import com.cannontech.yukon.cbc.DefineCollectableCBCMessage;
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.CollectableStreamer;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.VirtualInputStream;
import com.roguewave.vsj.VirtualOutputStream;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableCapControlServerResponse extends DefineCollectableCBCMessage{
	public static final int SERVERRESPONSEMSG_ID = 526;
	
	public DefineCollectableCapControlServerResponse() {
		super();
	}
	
	public Object create(VirtualInputStream strm) {
		return new CapControlServerResponse();
	}
	
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
	
	public int getCxxClassId() {
		return SERVERRESPONSEMSG_ID;
	}
	
	public String getCxxStringId() {
		return DefineCollectable.NO_STRINGID;
	}
	
	@SuppressWarnings("unchecked")
	public Class getJavaClass() {
		return CapControlServerResponse.class;
	}
	
	/**
	 * restoreGuts method comment.
	 */
	public void restoreGuts(Object obj, VirtualInputStream vstr, CollectableStreamer polystr) throws java.io.IOException {
		super.restoreGuts( obj, vstr, polystr );
		
		CapControlServerResponse cmd = (CapControlServerResponse) obj;

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

		vstr.insertUnsignedInt( cmd.getResponseType().getTypeId() );
		vstr.saveObject( cmd.getResponse(), SimpleMappings.CString );
	}
}
