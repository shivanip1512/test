package com.cannontech.ejb.test;

import java.rmi.RemoteException;
import java.util.Hashtable;

import javax.ejb.CreateException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.cannontech.database.data.device.LCUT3026;

/**
 * @author rneuharth
 * Sep 23, 2002 at 2:55:35 PM
 * 
 * A undefined generated comment
 */
public class TestDBPersistent
{

	private synchronized com.cannontech.ejb.DBPersistentHome getHome() throws NamingException
	{
      Hashtable props = new Hashtable();

      //----------------------JBOSS
      props.put(InitialContext.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
      props.put(InitialContext.PROVIDER_URL, "jnp://127.0.0.1:1099");
      //----------------------JBOSS

      InitialContext initialContext = new InitialContext(props);
      
		return (com.cannontech.ejb.DBPersistentHome) initialContext.lookup(
			com.cannontech.ejb.DBPersistentHome.JNDI_NAME);
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
               try
               {
com.cannontech.ejb.DBPersistent myBean = getHome().create(); //server
//(com.cannontech.yukon.IDBPersistent)
//                  ((com.cannontech.ejb.DBPersistentHome)initialContext.lookup(
//                           com.cannontech.ejb.DBPersistentHome.JNDI_NAME) ).create();

LCUT3026 y = (LCUT3026)
   com.cannontech.database.data.device.DeviceFactory.createDevice( 
      com.cannontech.database.data.pao.DeviceTypes.LCU_T3026);

y.setDeviceID( new Integer(2) );
//y = (LCUT3026)myBean.execute( com.cannontech.yukon.IDBPersistent.RETRIEVE, y ); //server
y.retrieve();    

System.out.println( y.getPAOName() );

//force the bean to clean up
//Thread.currentThread().sleep(3000);
//myBean.remove();
               }
               catch (Exception e)
               {
                  System.out.println(" EXCE : " + j );
                  e.printStackTrace();
                  return;
               }      

            }
         }).start();
      }


      System.out.println("DONE.....");
	}

	public static void main(String[] args)
	{
		TestDBPersistent test = new TestDBPersistent();
		test.testBean();

	}
}
