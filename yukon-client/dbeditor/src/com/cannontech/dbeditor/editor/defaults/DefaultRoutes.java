package com.cannontech.dbeditor.editor.defaults;

/**
 * Insert the type's description here.
 * Creation date: (6/27/2002 9:04:02 AM)
 * @author: 
 */

import java.awt.Container;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.route.CCURoute;
import com.cannontech.database.data.route.MacroRoute;
import com.cannontech.database.data.route.RouteBase;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.route.RepeaterRoute;


 
public class DefaultRoutes{
/**
 * DefaultRoutes constructor comment.
 */
public DefaultRoutes() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (6/27/2002 9:04:37 AM)
 */
public final static java.util.Vector calculateDefaults()
{
	java.util.Vector routes = DefaultRoutes.getAllRoutes();
	DBPersistent rt;
	java.util.Vector rpts = null;
	java.util.Vector originalDefaults = new java.util.Vector(); //holds original default values of routes
	java.util.Hashtable rptRoutes = new java.util.Hashtable(); //each repeater is a key associated with a route that uses that repeater
	int totalRecDefaults = 0;
	int totalOrigDefaults = 0;
	
	for (int i = 0; i < routes.size(); i++)
	{
		rt = (DBPersistent) routes.get(i);
		originalDefaults.addElement(((RouteBase) rt).getDefaultRoute());
		if (((RouteBase)rt).getDefaultRoute().equals("Y"))
			totalOrigDefaults++;
		boolean dflt = false;
		
		
		
		if (rt instanceof CCURoute) //all non-CCU routes are defaulted yes
		{
			rpts = ((CCURoute) rt).getRepeaterVector();
			for (int j = 0; j < rpts.size(); j++)
			{
				String rptID = ((RepeaterRoute) rpts.get(j)).getDeviceID().toString();
				if (rptRoutes.containsKey(rptID))
				{
					CCURoute existingRoute = (CCURoute) rptRoutes.get(rptID);   
																			   
					if (existingRoute != null)
					{
						if (existingRoute.getRepeaterVector().size() < rpts.size())
						{
							//start code to check for other repeaters on existing route before changing it
							boolean setFalse = false;
							for (int n = 0; n < existingRoute.getRepeaterVector().size(); n++)
							{
								String id = ((RepeaterRoute) existingRoute.getRepeaterVector().get(n)).getDeviceID().toString();
								if (rptRoutes.containsKey(id) && rptRoutes.get(id) != null)
								{
									if (Integer.parseInt(id) == Integer.parseInt(rptID))
									{
										setFalse = true;
									}
									else if (((RouteBase) rptRoutes.get(id)).getRouteID().intValue() != existingRoute.getRouteID().intValue())
										setFalse = true;
									else
										setFalse = false;
								}
							}
							if (setFalse)
								existingRoute.setDefaultRoute(CtiUtilities.getFalseCharacter().toString());
								
							//end code to check for other repeaters on existing route
							
							rptRoutes.put(rptID, rt);
							dflt = true;
							
						 }
					}
					else
					{
						rptRoutes.put(rptID, rt);
						dflt = true;
					}
				}
				else
				{
					rptRoutes.put(rptID, rt);
					dflt = true;
				}
			}
			
		}
		else
		{
			dflt = true;
		}
	
		if (rpts!= null && rpts.size() <= 0)
			dflt = true;
	
		if (dflt) {
			 ((RouteBase) rt).setDefaultRoute(CtiUtilities.getTrueCharacter().toString());
			 rpts = null;
		}
		else
			 ((RouteBase) rt).setDefaultRoute(CtiUtilities.getFalseCharacter().toString());
	}

	for (int i =0; i< routes.size(); i++) {
		rt = (DBPersistent) routes.get(i);
		if (((RouteBase)rt).getDefaultRoute().equals("Y"))
			totalRecDefaults++;
				
	}

	java.util.Vector returnVector = new Vector();
	returnVector.addElement(routes);
	returnVector.addElement(originalDefaults);
	returnVector.addElement(new Integer(totalRecDefaults));
	returnVector.addElement(new Integer(totalOrigDefaults));
	return returnVector;
}
/**
* This method returns a vector of carrier routes that have repeaters associated with them
* Uses the cache to retrieve all of the routes
* Creation date: (5/28/2002 10:26:44 AM)
*/
public static final java.util.Vector getAllRoutes()
{
	java.util.Vector realRoutes = new java.util.Vector();
	java.util.List routes;
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized (cache)
	{
		routes = cache.getAllRoutes();
		DBPersistent rt = null;
		//retrieves the repeater vector for each CCU route
		try
		{
			for (int i = 0; i < routes.size(); i++)
			{
				rt = LiteFactory.createDBPersistent((LiteBase) routes.get(i));
				if (!(rt instanceof MacroRoute))
				{
					com.cannontech.database.Transaction.createTransaction(com.cannontech.database.Transaction.RETRIEVE, rt).execute();
					realRoutes.add(rt);
				}
			}
			return realRoutes; //returns routes that have repeaters
		}
		catch (com.cannontech.database.TransactionException t)
		{
			t.printStackTrace(System.out);
			return null;
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/1/2002 12:14:41 PM)
 * @return java.util.Vector
 */
public static final Vector getDisplayReady(Vector routes, Vector originalDefaults) {

    Vector displayVector = new Vector();

    com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
    synchronized (cache) {

        java.util.List devices = cache.getAllDevices();

        for (int k = 0; k < routes.size(); k++) {

            Vector routeVector = new Vector();
            routeVector.addElement(
                ((RouteBase) routes.get(k)).getPAOName());
            routeVector.addElement(originalDefaults.get(k));
            routeVector.addElement(
                ((RouteBase) routes.get(k)).getDefaultRoute());
         

            for (int i = 0; i < devices.size(); i++) {
                if (((LiteYukonPAObject) devices.get(i)).getYukonID() == ((RouteBase) routes.get(k)).getDeviceID().intValue()) {
                    routeVector.addElement( ((LiteYukonPAObject) devices.get(i)).getPaoName() );
                    break;
                }

            }
            displayVector.addElement(routeVector);

        }

    } 
    
  
    return displayVector;
}
/**
 * Insert the method's description here.
 * Creation date: (6/27/2002 10:57:58 AM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	
	com.cannontech.clientutils.CTILogger.info("Try");
	calculateDefaults();
	com.cannontech.clientutils.CTILogger.info("Success");
	}
/**
 * Insert the method's description here.
 * Creation date: (6/3/2002 1:47:36 PM)
 * @param routes java.util.Vector
 */
public final static void updateRouteDefaults(Vector routes) {
	try {
	for (int i=0; i<routes.size(); i++) 
	{
		
	com.cannontech.database.Transaction.createTransaction(
	            com.cannontech.database.Transaction.UPDATE,((DBPersistent) routes.get(i)))
                .execute();
	
	}   

        } catch (com.cannontech.database.TransactionException t) {
            t.printStackTrace(System.out);

        }
	
	
	}
}
