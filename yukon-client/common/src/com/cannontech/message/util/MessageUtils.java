package com.cannontech.message.util;

import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.version.VersionTools;
import com.cannontech.database.db.version.CTIDatabase;
import com.cannontech.message.dispatch.message.Command;

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
			Vector msgVect = dispCmdMsg.getOpArgList();
			
			if( msgVect.size() >= 4 )
			{
				Integer majVers = (Integer)msgVect.get(1);
				Integer minVers = (Integer)msgVect.get(2);
				Integer build = (Integer)msgVect.get(3);

				//if all zeros, this is a development version, let it continue
				if( majVers.intValue() == 0
					&& minVers.intValue() == 0
					&& build.intValue() == 0 )
				{
					return null;
				}
				
				CTIDatabase ctiDB = VersionTools.getDatabaseVersion();
				String[] vers = ctiDB.getVersion().split("\\.");				
				Integer dbMaj = new Integer( vers[0] );
				Integer dbMin = new Integer( vers[1] );


				if( !(dbMaj.equals(majVers)
					  && dbMin.equals(minVers)
					  && ctiDB.getBuild().equals(build)) )
				{
					String retStr =
						" CLIENT/SERVER version mismatch in messaging, " +
						" (Client: " + ctiDB.getVersion() + " build: " + ctiDB.getBuild() + ")" +
						" (Server: " + majVers + "." + minVers + " build: " + build + ")";

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
