package com.cannontech.dbeditor.editor.defaults;
/**
 * Insert the type's description here.
 * Creation date: (6/27/2002 9:04:02 AM)
 * @author: 
 */
import java.awt.Container;
import java.awt.event.*;
import java.util.Hashtable;
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
public class DefaultRoutes
{
	/**
	 * DefaultRoutes constructor comment.
	 */
	public DefaultRoutes()
	{
		super();
	}
	/**
	 * This function calculates the recommended defaults for all of the routes. 
	 * It makes sure that each route is covered but only covered the minimum number of times
	 * All routes other than carrier routes with repeaters are defaulted to 'Y'.  
	 * Creation date: (6/27/2002 9:04:37 AM)
	 */
	public final static java.util.Vector calculateDefaults()
	{
		
		
		java.util.Vector routes = DefaultRoutes.getAllRoutes();
		java.util.Vector rpts = null;
		java.util.Vector originalDefaults = new java.util.Vector(); //holds original default values of routes
		java.util.Vector rtsNoRpts = new Vector(); //Holds CCU routes that have no selected repeaters
		
		java.util.Hashtable CCUTransmitters = new java.util.Hashtable(); //holds names of CCU transmitters as key and arrays of size 8 as values
		java.util.Hashtable rptRoutes = null;
		
		DBPersistent rt;
	
		int totalRecDefaults = 0; //total number of recommended default routes
		int totalOrigDefaults = 0;//total number or original default routes
		int busNumber = 0;
		
		java.util.List devices;
		
		String CCUName ="";
		
		
		com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
		synchronized (cache)
		{
			devices = cache.getAllDevices();
		}
		
		
		for (int i = 0; i < routes.size(); i++)
		{
			
			//re-initialize all variables for this route
			rptRoutes = null;
			rt = null;
			busNumber = 0;
			CCUName ="";
			rpts = null;
		
		
		    rt = (DBPersistent) routes.get(i);
			
			originalDefaults.addElement(((RouteBase) rt).getDefaultRoute());
			
			
	        if (((RouteBase) rt).getDefaultRoute().equals(CtiUtilities.getTrueCharacter().toString()))
				totalOrigDefaults++;
				
				
			boolean dflt = false;
		
			if (rt instanceof CCURoute) //all non-CCU routes are defaulted yes
			{
				//Find the name of the transmitter associated with this route
				
				for (int j = 0; j < devices.size(); j++)
				{
					if (((LiteYukonPAObject) devices.get(j)).getYukonID() == ((RouteBase) rt).getDeviceID().intValue())
					{
						CCUName = ((LiteYukonPAObject) devices.get(j)).getPaoName();
						break;
					}
				}
				
				
				/*check to see if there are other routes using this transmitter in hashtable
				if not, create an array of 8 representing each bus number.  Each entry in the array will
				contain a hashtable of repeater ids associated with the current route */
				
				if (!CCUTransmitters.containsKey(CCUName))
				{
					CCUTransmitters.put(CCUName, new Object[9]);
				}
				
				//Get the bus number for this route
				busNumber = ((CCURoute) rt).getCarrierRoute().getBusNumber().intValue();
				
				//get all repeaters associated with this route
				rpts = ((CCURoute) rt).getRepeaterVector();
				
				//get the hashtable of repeaters for the particular transmitter and bus number
				rptRoutes = (Hashtable) ((Object[]) CCUTransmitters.get(CCUName))[busNumber];
				
				//if the current route uses repeaters and there are no other repeater routes then create a hashtable for 
				//this particular transmitter and bus number
				if (rptRoutes == null && rpts.size() > 0)
				{
					rptRoutes = new Hashtable();
					((Object[]) CCUTransmitters.get(CCUName))[busNumber] = rptRoutes;
				}
				
				
				for (int j = 0; j < rpts.size(); j++) //checks repeaters to see if a different route is using that same repeater
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
								if (setFalse) //change an existing routes default settings to N if it is no longer needed because
									//the current route covers the same route
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
			if (rpts != null && rpts.size() <= 0) {
				if (rptRoutes == null) {
					dflt = true;
					rtsNoRpts.addElement(rt);
				}
				else 
				dflt = false;
				
			}
			else if (rpts != null && rpts.size() > 0) {
				
				//sets CCU routes default to "N" if there are repeaters on this route
				
				for (int k=0; k<rtsNoRpts.size(); k++) {
			       
			     	  DBPersistent rtCompare = (DBPersistent)rtsNoRpts.get(k);
			       
			      	 int busNumberCompare = ((CCURoute)rtCompare).getCarrierRoute().getBusNumber().intValue();
				   	String CCUNameCompare ="";
				   	for (int j = 0; j < devices.size(); j++)
					{
						if (((LiteYukonPAObject) devices.get(j)).getYukonID() == ((RouteBase) rtCompare).getDeviceID().intValue())
						{
							CCUNameCompare = ((LiteYukonPAObject) devices.get(j)).getPaoName();
							break;
						}
					}
					
					if (busNumberCompare == busNumber && CCUName.equalsIgnoreCase(CCUNameCompare)) {
						
						((RouteBase)rtsNoRpts.get(k)).setDefaultRoute(CtiUtilities.getFalseCharacter().toString());
							rtsNoRpts.removeElement(rtsNoRpts.get(k));
						}
				}	
			}
			if (dflt)
			{
				((RouteBase) rt).setDefaultRoute(CtiUtilities.getTrueCharacter().toString());
				
			}
			else
				 ((RouteBase) rt).setDefaultRoute(CtiUtilities.getFalseCharacter().toString());
				 
			}
		
		//calculate the total number of recommended default routes
		for (int i = 0; i < routes.size(); i++)
		{
			rt = (DBPersistent) routes.get(i);
			if (((RouteBase) rt).getDefaultRoute().equals(CtiUtilities.getTrueCharacter().toString()))
				totalRecDefaults++;
		}
		
		java.util.Vector returnVector = new Vector();
		returnVector.addElement(routes); // add changed routes
		returnVector.addElement(originalDefaults);  //add original default settings
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
		java.util.Vector defaultedRoutes = new java.util.Vector();
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
						defaultedRoutes.add(rt);
					}
				}
				return defaultedRoutes; //returns routes that have repeaters
			}
			catch (com.cannontech.database.TransactionException t)
			{
				com.cannontech.clientutils.CTILogger.error(t.getMessage(), t);
				return null;
			}
		}
	}
	/**
	 * This method returns a 2-dimensional array of route name, current default settings, recommended default settings
	 * and the transmitter for each route 
	 * Creation date: (7/1/2002 12:14:41 PM)
	 * @return java.util.Vector
	 */
	public static final Vector getDisplayReady(Vector routes, Vector originalDefaults)
	{
		Vector displayVector = new Vector();
		com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
		synchronized (cache)
		{
			java.util.List devices = cache.getAllDevices();
			for (int k = 0; k < routes.size(); k++)
			{
				Vector routeVector = new Vector();
				routeVector.addElement(((RouteBase) routes.get(k)).getPAOName());
				routeVector.addElement(originalDefaults.get(k));
				routeVector.addElement(((RouteBase) routes.get(k)).getDefaultRoute());
				for (int i = 0; i < devices.size(); i++)
				{
					if (((LiteYukonPAObject) devices.get(i)).getYukonID() == ((RouteBase) routes.get(k)).getDeviceID().intValue())
					{
						routeVector.addElement(((LiteYukonPAObject) devices.get(i)).getPaoName());
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
	public static void main(String[] args)
	{
		com.cannontech.clientutils.CTILogger.info("Try");
		calculateDefaults();
		com.cannontech.clientutils.CTILogger.info("Success");
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/3/2002 1:47:36 PM)
	 * @param routes java.util.Vector
	 */
	public final static void updateRouteDefaults(Vector routes)
	{
		try
		{
			for (int i = 0; i < routes.size(); i++)
			{
				com.cannontech.database.Transaction.createTransaction(com.cannontech.database.Transaction.UPDATE, ((DBPersistent) routes.get(i))).execute();
			}
		}
		catch (com.cannontech.database.TransactionException t)
		{
			com.cannontech.clientutils.CTILogger.error(t.getMessage(), t);
		}
	}
}
