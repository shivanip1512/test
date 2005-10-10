package com.cannontech.cbc.web;

import com.cannontech.cbc.web.CCOneLineGenerator;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.util.DrawingUpdater;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.yukon.cbc.CBCClientConnection;
import com.cannontech.yukon.cbc.CBCCommand;
import com.cannontech.yukon.cbc.CBCSubstationBuses;
import com.cannontech.yukon.conns.ConnPool;

/**
 * @author eWally
 *
 * Generates OneLines for CapControl based on DBChanges messages
 * heard from the CapControl server.
 * 
 */
public class OneLineSubs implements MessageListener
{
	private String dirBase = null;


	/**
	 * 
	 */
	public OneLineSubs()
	{
		super();
        
        // /yukon/server/web/webapps/yukon/capcontrol
        dirBase = CtiUtilities.getYukonBase() + "/head/yukon-web/WebContent/capcontrol/oneline";
        //dirBase = CtiUtilities.getYukonBase() + "/server/web/webapps/yukon/capcontrol/oneline";
        
        CTILogger.debug(" Oneline generation output: " + dirBase);
	}



	public void messageReceived(MessageEvent e) {
		handleMessage(e.getMessage());	
	}

	private void handleMessage(com.cannontech.message.util.Message msg)
	{
		if (msg instanceof CBCSubstationBuses)
		{
			CBCSubstationBuses subBusMsg = (CBCSubstationBuses)msg;
			java.lang.Integer msgInfoBitMask = subBusMsg.getMsgInfoBitMask();
			if( subBusMsg.isAllSubs() )
			{
				for( int i = 0; i < subBusMsg.getNumberOfBuses(); i++ )
				{
					CTILogger.debug("Generating SubBus: " +
                            getDirBase() + "/" +
                            subBusMsg.getSubBusAt(i).getCcName() );

                    String dirAndFileExt = getDirBase() +  "/" +
                        subBusMsg.getSubBusAt(i).getCcName().trim() + ".jlx";

                    Drawing ccSubBusDrawing = CCOneLineGenerator.generateSVGFileFromSubBus(
                            subBusMsg.getSubBusAt(i),
                            "/capcontrol/oneline/" + subBusMsg.getSubBusAt(i).getCcName().trim() + ".html" );


					DrawingUpdater updater = new DrawingUpdater(ccSubBusDrawing);
					updater.updateDrawing();
		
					ccSubBusDrawing.exportAs(dirAndFileExt);

					CTILogger.debug("...generation complete for " + subBusMsg.getSubBusAt(i).getCcName()); 
				}
			}
		}
	}

	public static void main(String[] args) throws Throwable
	{
        System.setProperty("cti.app.name", "cbcOneLineGen");

		OneLineSubs thisIsThis = new OneLineSubs(); 
		if( args.length > 0 ) 
			thisIsThis.dirBase = args[0];

		thisIsThis.start();
	
		while(true)
		{
			try
			{
				Thread.sleep(500);
			}
			catch( InterruptedException ex )
			{
				break;			
			}	
		}

		System.exit(0);
        
	}
    
    public String getDirBase()
    {
        return dirBase;
    }

    public void start()
    {
        try
        {
        	getConnection().addMessageListener( this );
        	getConnection().connect( 15000 );

        	getConnection().executeCommand( 0, CBCCommand.REQUEST_ALL_SUBS );
        }
        catch( Exception e ) {}
    }
    
    
    /** 
     * Stop us
     */
    public void stop()
    {
        try
        {
        	getConnection().removeMessageListener( this );
        	getConnection().disconnect();
        }
        catch( Exception e ) {}
    }
    
    public boolean isRunning()
    {    	
    	return getConnection().isValid();
    }
    
    private CBCClientConnection getConnection()
    {
    	//creates our own connection
    	return (CBCClientConnection)ConnPool.getInstance().getConn(
    					"ONELINE_CAPCONTROL");
    }
    
}
