package com.cannontech.yukon.concrete;

import com.cannontech.common.util.CtiProperties;
import com.cannontech.yukon.IYukon;

/**
 * Factory to create the proper set of concrete yukon classes.
 * @author alauinger
 */
public class ResourceFactory 
{
   //all class strings better by instances of com.cannontech.yukon.IYukon!!
	private static final String YUKON_SERVER = 
            "com.cannontech.yukon.server.YukonServerResource";

	//this class has been removed as of 12-4-2003
   private static final String YUKON_CLIENT = 
            "com.cannontech.yukon.client.YukonClientResource";

   private static IYukon yukRes = null;
   
   
   private static IYukon createYukonResource()
   {
      Object o = null;

      try
      {
         //check for ther server yukon
         String mode = CtiProperties.getInstance().getProperty(
         						CtiProperties.KEY_MODE, "server" ).toLowerCase();

         if( "client".equalsIgnoreCase(mode) )
         {
            o = Class.forName(YUKON_CLIENT).newInstance();
         }
         else
         {
         	o = Class.forName(YUKON_SERVER).newInstance();            
         }

      }
      catch( Exception e )
      {
         com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
      }
      
      if( o != null && (o instanceof IYukon) )
      {
         return (IYukon)o;
      }
      else //we must have some extreme problems here, chuck Error!
         throw new Error("Unable to load the YukonResource layer");
   }

   public static synchronized IYukon getIYukon()
   {
      if( yukRes == null )
         yukRes = createYukonResource();
         
      return yukRes;
   }
   
}
