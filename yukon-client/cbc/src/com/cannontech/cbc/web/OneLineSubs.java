package com.cannontech.cbc.web;

import com.cannontech.cbc.data.CBCClientConnection;
import com.cannontech.cbc.messages.CBCSubstationBuses;
import com.cannontech.cbc.web.CCOneLineGenerator;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.util.DrawingUpdater;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;

/**
 * @author rneuharth
 *
 * Generates OneLines for CapControl based on DBChanges messages
 * heard from the CapControl server.
 * 
 */
public class OneLineSubs implements MessageListener
{
	private String dirAndFile = CtiUtilities.getLogDirPath();
    private CBCClientConnection connection = null;


	/**
	 * 
	 */
	public OneLineSubs()
	{
		super();
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
			if( (msgInfoBitMask.intValue() & 0x0000001) == 0x0000001 )
			{
				for( int i = 0; i < subBusMsg.getNumberOfBuses(); i++ )
				{
					CTILogger.info("Generating SubBus - " + subBusMsg.getSubBusAt(i).getCcName() 
						+ "/" + subBusMsg.getSubBusAt(i).getCcArea() );

					Drawing ccSubBusDrawing = CCOneLineGenerator.generateSVGFileFromSubBus(subBusMsg.getSubBusAt(i));

					String dirAndFileExt = getDirAndFile().concat(subBusMsg.getSubBusAt(i).getCcName().trim());
					dirAndFileExt = dirAndFileExt.concat(".jlx");
					DrawingUpdater updater = new DrawingUpdater(ccSubBusDrawing);
					updater.updateDrawing();
		
					ccSubBusDrawing.exportAs(dirAndFileExt);

					CTILogger.info("Generated SubBus - " + subBusMsg.getSubBusAt(i).getCcName() 
						+ "/" + subBusMsg.getSubBusAt(i).getCcArea() );
				}
			}
		}
	}

	public static void main(String[] args) throws Throwable
	{
        System.setProperty("cti.app.name", "cbcOneLineGen");

		OneLineSubs thisIsThis = new OneLineSubs(); 
		if( args.length > 0 ) 
		{
			thisIsThis.dirAndFile = args[0];
		}
		CBCClientConnection connection = new CBCClientConnection();
	
		connection.addMessageListener(thisIsThis);
		//start the conn!!!
		connection.connectWithoutWait();	
	
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
    
    public String getDirAndFile()
    {
        return dirAndFile;
    }

    public void start()
    {
        connection = new CBCClientConnection();            
        
        try
        {
            connection.addMessageListener( this );
            connection.connectWithoutWait();
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
            connection.removeMessageListener( this );
            connection.disconnect();
        }
        catch( Exception e ) {}

        connection = null;
    }
    
    public boolean isRunning()
    {
        return connection != null;
    }
}
