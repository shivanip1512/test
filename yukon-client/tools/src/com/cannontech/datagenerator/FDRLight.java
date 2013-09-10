package com.cannontech.datagenerator;

// This Class is scheduled to be removed soon
// Do not use as Dispatch connections now use the ActiveMQ broker


import java.io.IOException;
import java.util.Iterator;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.message.dispatch.DispatchClientConnection;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.util.ClientConnectionFactory;

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
		DispatchClientConnection srcConn = ClientConnectionFactory.getInstance().createDispatchConn();
		
		com.cannontech.message.dispatch.message.Registration reg = new com.cannontech.message.dispatch.message.Registration();
		reg.setAppName( CtiUtilities.getAppRegistration() );
		reg.setAppIsUnique(0);
		reg.setAppKnownPort(0);
		reg.setAppExpirationDelay(5000);
		com.cannontech.message.dispatch.message.PointRegistration pReg = new com.cannontech.message.dispatch.message.PointRegistration();
		pReg.setRegFlags(com.cannontech.message.dispatch.message.PointRegistration.REG_ALL_PTS_MASK );
		com.cannontech.message.dispatch.message.Multi multi = new com.cannontech.message.dispatch.message.Multi();
		multi.getVector().addElement(reg);
		multi.getVector().addElement(pReg);
		srcConn.setRegistrationMsg(multi);
		srcConn.setAutoReconnect(true);
		
		DispatchClientConnection destConn = ClientConnectionFactory.getInstance().createDispatchConn();
		
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
		if( msg instanceof Multi ) {
			Multi multi = (Multi) msg;
			Iterator iter = multi.getVector().iterator();
			while( iter.hasNext() ) {
				handleMessage(dest, iter.next());
			}
		}
		else 
		if( msg instanceof PointData ) {
			PointData pData = (PointData) msg;
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
