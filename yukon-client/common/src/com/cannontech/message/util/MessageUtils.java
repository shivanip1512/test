package com.cannontech.message.util;

import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.version.VersionTools;

/**
 * @author rneuharth
 *
 * Handles random utility functions that do not have a home anywhere else.
 * 
 */
public class MessageUtils
{

	/**
	 * 
	 */
	private MessageUtils()
	{
		super();
	}

	/**
	 * Return a string comparision between the current client Yukon
	 * version and the current server Yukon version. Returns null if
	 * the versions are equal.
	 * 
	 * @param dispCmdMsg
	 * @return String
	 */
	public static synchronized String getVersionComp( Command dispCmdMsg )
	{
		if( dispCmdMsg.getOperation() == Command.ARE_YOU_THERE)
		{
            List<Integer> opArgList = dispCmdMsg.getOpArgList();
			
			if( opArgList.size() >= 4 )
			{
				int servMaj = opArgList.get(1);
				int servMin = opArgList.get(2);
				int servBuild = opArgList.get(3);

				//if all zeros, this is a development version, let it continue
				if( servMaj == 0
					&& servMin == 0
					&& servBuild == 0 )
				{
					return null;
				}
				
				String[] vers = VersionTools.getYUKON_VERSION().split("\\.");				
				Integer clientMaj = new Integer( vers[0].trim() );
				Integer clientMin = new Integer( vers[1].trim() );
				Integer clientBuild = new Integer( vers[2].trim() );


				if( !(clientMaj.equals(servMaj)
					  && clientMin.equals(servMin)
					  && clientBuild.equals(servBuild)) )
				{
					String retStr =
						" CLIENT/SERVER version mismatch in messaging, " +
						" (Client: " + clientMaj + "." + clientMin + " build: " + clientBuild + ")" +
						" (Server: " + servMaj + "." + servMin + " build: " + servBuild + ")";

					CTILogger.warn( retStr );
					return retStr;
				}
				else
					return null;
			}
			
		}
		
		return null;
	}

}
