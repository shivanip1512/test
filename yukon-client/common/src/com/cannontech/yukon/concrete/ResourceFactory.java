package com.cannontech.yukon.concrete;

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
               
   private static final String YUKON_CLIENT = 
            "com.cannontech.yukon.client.YukonClientResource";

   private static IYukon yukRes = null;
   
   
   private static IYukon createYukonResource()
   {
      Object o = null;

      try
      {
         //check for ther server yukon (only server for now)
         if( true )
         {
            o = Class.forName(YUKON_SERVER).newInstance();
         }
         else if( false ) //check the client Yukon
         {
            o = Class.forName(YUKON_CLIENT).newInstance();
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

   public static IYukon getIYukon()
   {
      if( yukRes == null )
         yukRes = createYukonResource();
         
      return yukRes;
   }
   
}
