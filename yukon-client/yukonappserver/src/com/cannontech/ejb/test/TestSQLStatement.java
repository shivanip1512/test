package com.cannontech.ejb.test;

import java.rmi.RemoteException;
import java.util.Hashtable;

import javax.ejb.CreateException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.cannontech.ejb.SqlStatement;
import com.cannontech.ejb.SqlStatementBean;
import com.cannontech.ejb.SqlStatementHome;

/**
 * @author rneuharth
 * Sep 23, 2002 at 2:55:35 PM
 * 
 * A undefined generated comment
 */
public class TestSQLStatement
{

	private synchronized SqlStatementHome getHome() throws NamingException
	{
      Hashtable props = new Hashtable();

      //----------------------JBOSS
      props.put(InitialContext.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
      props.put(InitialContext.PROVIDER_URL, "jnp://127.0.0.1:1099");
      //----------------------JBOSS

      InitialContext initialContext = new InitialContext(props);
      
		return (SqlStatementHome) initialContext.lookup(
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
SqlStatement myBean = getHome().create(); //server
//myBean.setDBConnection()
myBean.setSQLString("select * from ctidatabase");
myBean.execute();
for( int i = 0; i < myBean.getRowCount(); i++ )
{
	for( int j = 0; j < myBean.getRow(i).length; j++ )
	System.out.println( myBean.getRow(i)[j].toString() );
}

//force the bean to clean up
//Thread.currentThread().sleep(3000);
//System.out.println("Removing Bean...");
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
		TestSQLStatement test = new TestSQLStatement();
		test.testBean();

	}
}
