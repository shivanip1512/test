package com.cannontech.ejb.test;

import java.rmi.RemoteException;
import java.util.Hashtable;

import javax.ejb.CreateException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sound.midi.SysexMessage;

/**
 * @author rneuharth
 * Sep 19, 2002 at 12:15:39 PM
 * 
 * A undefined generated comment
 */
public class TestDatabaseCache
{

	private synchronized com.cannontech.ejb.DatabaseCacheHome getHome() throws NamingException
	{
      Hashtable props = new Hashtable();

      //----------------------JBOSS
      props.put(InitialContext.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
      props.put(InitialContext.PROVIDER_URL, "jnp://127.0.0.1:1099");
      //----------------------JBOSS

      InitialContext initialContext = new InitialContext(props);

      return (com.cannontech.ejb.DatabaseCacheHome) initialContext.lookup(
         com.cannontech.ejb.DatabaseCacheHome.JNDI_NAME);         
   }


	public void testBean()
	{

      for( int i = 0; i < 1; i++ )
      {
         final int j = i;
         new Thread( new Runnable()
         {
            public void run()
            {
               java.util.List l = null;
               try
               {
         			com.cannontech.ejb.DatabaseCache myBean = null;
                  
                  myBean = getHome().create();
                  

         			//--------------------------------------
         			//This is the place you make your calls.
                  l = myBean.getAllDevices();
               }
               catch (Exception e)
               {
                  System.out.println(" EXCE : " + j );
                  e.printStackTrace();
                  return;
               }      
               
               System.out.println( "Sz = " + l.size() );
               for( int a = 0; a < l.size(); a++ )
                  System.out.println("("+j+") "+l.get(a).toString() );
            }
         }).start();
      }
	}

	public static void main(String[] args)
	{
		TestDatabaseCache test = new TestDatabaseCache();
		test.testBean();

	}

}
