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
      	//"com/cannontech/DatabaseCache");
   }

//14.6
//4.124
//4.23
//3.952
//3.249
//3.374
//3.967
//3.328
//4.124
//3.390
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
						java.util.Date timerStart = null;
						java.util.Date timerStop = null;
						timerStart = new java.util.Date();
               	
         			com.cannontech.ejb.DatabaseCache myBean = null;
                  
                  myBean = getHome().create();
                  

         			//--------------------------------------
         			//This is the place you make your calls.
                  l = myBean.getAllDevices();


						timerStop = new java.util.Date();
						com.cannontech.clientutils.CTILogger.info( 
							 (timerStop.getTime() - timerStart.getTime())*.001 + 
								" Secs for JBOSS call" );                  
               }
               catch (Exception e)
               {
                  System.out.println(" EXCE : " + j );
                  e.printStackTrace();
                  return;
               }      
               
               System.out.println( "Sz = " + l.size() );
//               for( int a = 0; a < l.size(); a++ )
//                  System.out.println("("+j+") "+l.get(a).toString() );
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
