package com.cannontech.stars.util.task;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.util.ServerUtils;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SendConfigCommandTask implements Runnable {
	
	public static int COMMAND_INTERVAL = 300;	// Wait 5 minutes in between sending commands
	
	private LiteStarsEnergyCompany energyCompany = null;
	private LiteStarsLMHardware liteHw = null;

	public SendConfigCommandTask(LiteStarsEnergyCompany energyCompany, LiteStarsLMHardware liteHw) {
		this.energyCompany = energyCompany;
		this.liteHw = liteHw;
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		CTILogger.info( "*** Start SendConfigCommand task ***" );
        
		String cmd = "putconfig service in serial " + liteHw.getManufacturerSerialNumber();
		
        int routeID = liteHw.getRouteID();
        if (routeID == 0) routeID = energyCompany.getDefaultRouteID();
        
		ServerUtils.sendSerialCommand( cmd, routeID );
        
        try {
	        Thread.sleep( COMMAND_INTERVAL * 1000 );
        }
        catch (InterruptedException e) {}
		
		com.cannontech.database.db.stars.hardware.LMHardwareConfiguration[] configs =
				com.cannontech.database.db.stars.hardware.LMHardwareConfiguration.getALLHardwareConfigs( new Integer(liteHw.getInventoryID()) );
		for (int i = 0; i < configs.length; i++) {
			if (configs[i].getAddressingGroupID().intValue() == 0) continue;
			
			String groupName = com.cannontech.database.cache.functions.PAOFuncs.getYukonPAOName( configs[i].getAddressingGroupID().intValue() );
            cmd = "putconfig serial " + liteHw.getManufacturerSerialNumber() + " template '" + groupName + "'";
            
            ServerUtils.sendSerialCommand( cmd, routeID );
		}
		
		CTILogger.info( "*** End SendConfigCommand task ***" );
	}

}
