package com.cannontech.datagenerator;

import java.io.IOException;
import java.util.Iterator;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.dispatch.DispatchClientConnection;
import com.cannontech.messaging.message.dispatch.MultiMessage;
import com.cannontech.messaging.message.dispatch.PointDataMessage;

/**
 * FDRLight sends point changes from one dispatch to another.  
 * Originally written to drive a second test/demo esubstation 
 * server.
 * @author alauinger
 */
public class FDRLight {
			
    private FDRLightArgs args;
    	
	public FDRLight() {
	}
	
	public FDRLight(FDRLightArgs args) {
		setArgs(args);
	}
	
	public void run()  {
		FDRLightArgs args = getArgs();
		
		// set up the connections
		DispatchClientConnection srcConn = new DispatchClientConnection();
		srcConn.setHost(args.getSrcHost());
		srcConn.setPort(args.getSrcPort());
		
		com.cannontech.messaging.message.dispatch.RegistrationMessage reg = new com.cannontech.messaging.message.dispatch.RegistrationMessage();
		reg.setAppName( CtiUtilities.getAppRegistration() );
		reg.setAppIsUnique(0);
		reg.setAppKnownPort(0);
		reg.setAppExpirationDelay(5000);
		com.cannontech.messaging.message.dispatch.PointRegistrationMessage pReg = new com.cannontech.messaging.message.dispatch.PointRegistrationMessage();
		pReg.setRegFlags(com.cannontech.messaging.message.dispatch.PointRegistrationMessage.REG_ALL_PTS_MASK );
		com.cannontech.messaging.message.dispatch.MultiMessage multi = new com.cannontech.messaging.message.dispatch.MultiMessage();
		multi.getVector().addElement(reg);
		multi.getVector().addElement(pReg);
		srcConn.setRegistrationMsg(multi);
		srcConn.setAutoReconnect(true);
		
		DispatchClientConnection destConn = new DispatchClientConnection();
		destConn.setHost(args.getDestHost());
		destConn.setPort(args.getDestPort());
		
		destConn.setRegistrationMsg(reg);
		destConn.setAutoReconnect(true);
		
		try {
		srcConn.connect();
		destConn.connect();
		}
		catch( IOException ioe ) {
			ioe.printStackTrace();
			if( srcConn != null )
                srcConn.disconnect();
			if( destConn != null )
                destConn.disconnect();		 	
			return;
		}
		
		// Connections are set up start the main loop
		while(true) {	
			try {		
				handleMessage(destConn, srcConn.read());							
			}
			catch(Throwable t) {
				//something interesting happened
				//wait a while
				t.printStackTrace();
				try {
					Thread.currentThread().sleep(5000);
				} catch (InterruptedException e) {
				}				
			}
		}

	}

	private void handleMessage(DispatchClientConnection dest, Object msg) {
		if( msg instanceof MultiMessage ) {
			MultiMessage multi = (MultiMessage) msg;
			Iterator iter = multi.getVector().iterator();
			while( iter.hasNext() ) {
				handleMessage(dest, iter.next());
			}
		}
		else 
		if( msg instanceof PointDataMessage ) {
			PointDataMessage pData = (PointDataMessage) msg;
			CTILogger.info("forwarding id: " + pData.getId() + " value: " + pData.getValue());
			dest.write(pData);	
		}
	}	
	/**
	 * Returns the args.
	 * @return FDRLightArgs
	 */
	public FDRLightArgs getArgs() {
		return args;
	}

	/**
	 * Sets the args.
	 * @param args The args to set
	 */
	public void setArgs(FDRLightArgs args) {
		this.args = args;
	}

}
