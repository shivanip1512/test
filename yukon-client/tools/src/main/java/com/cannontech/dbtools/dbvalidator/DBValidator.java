package com.cannontech.dbtools.dbvalidator;

import com.cannontech.database.SqlUtils;

/**
 * Insert the type's description here.
 * Creation date: (8/7/2001 3:43:05 PM)
 * @author: 
 */
public class DBValidator {
/**
 * DBValidator constructor comment.
 */
public DBValidator() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/2001 3:43:14 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args)
{

   com.cannontech.clientutils.CTILogger.info("Started....");
	java.sql.Connection conn = null;

   try
   {
		java.util.ArrayList classNames = new java.util.ArrayList(50);
		java.io.File f = new java.io.File("d:/yukon/client/bin/");
		java.util.ArrayList files = new java.util.ArrayList(50);
		for( int i = 0; i < f.listFiles().length; i++ )
	  	  if( f.listFiles()[i].getAbsolutePath().toLowerCase().endsWith(".jar") )
	  	  		files.add(f.listFiles()[i]); //get all the .jar files in the directory


	  for( int i = 0; i < files.size(); i++ )
	  {
com.cannontech.clientutils.CTILogger.info("				" + ((java.io.File)files.get(i)).getAbsolutePath() ) ;
		  java.util.jar.JarFile j = new java.util.jar.JarFile(
			  			((java.io.File)files.get(i)).getAbsolutePath() );
		  
		  if (j == null)
			 com.cannontech.clientutils.CTILogger.info("Unable to find JAR file.");

		  java.util.Enumeration entryEnum = j.entries();
		  while (entryEnum.hasMoreElements())
		  {
			 java.util.jar.JarEntry e = (java.util.jar.JarEntry) entryEnum.nextElement();
			 String name = e.getName();

			 if (name.endsWith(".class"))
			 {
				 if( name.startsWith("com.cannontech.database.db/")
					  || name.startsWith("com.cannontech.database.db/") )
				 {
				 	classNames.add( name.replace('/','.').substring(0, name.length()-6) );
				 }
				 	
			 }
			 
		  } //end of while loop
	  } //end of file loop



	  

	 //get our DBConnection 
	conn = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");

	for( int i = 0; i < classNames.size(); i++ )
	{		
		try
		{
			Class obj = Class.forName( classNames.get(i).toString() );
			
			if( obj != null && !obj.isInterface() && !obj.isPrimitive()
				 && !java.lang.reflect.Modifier.isAbstract(obj.getModifiers()) 
				 && obj.getSuperclass().equals(com.cannontech.database.db.DBPersistent.class) )
			{
				
				com.cannontech.clientutils.CTILogger.info("PROCESSING : " + classNames.get(i).toString() );

				try
				{
					//com.cannontech.database.db.DBPersistent db = (com.cannontech.database.db.DBPersistent)obj.newInstance();
					Object instance = obj.newInstance();
					
					Class[] pType = {java.sql.Connection.class};
					Object[] params = {conn};
					//java.lang.reflect.Method m = obj.getMethod("setDbConnection", pType);
					//m.invoke(instance, params);



					for( int y=0; y < obj.getMethods().length; y++ )
					{
						if( obj.getMethods()[y].getName().substring(0,3).equalsIgnoreCase("set")
							 && obj.getMethods()[y].getReturnType() == Void.TYPE
							 && obj.getMethods()[y].getParameterTypes().length == 1
							 && (obj.getMethods()[y].getParameterTypes()[0] == Integer.class
								  || obj.getMethods()[y].getParameterTypes()[0] == Long.class) )
							{
								com.cannontech.clientutils.CTILogger.info("		EXECUTING : " + obj.getMethods()[y].getName() );
								
								if( obj.getMethods()[y].getParameterTypes()[0] == Long.class) 
									params[0] = new Long(1);
								else
									params[0] = new Integer(1);

								obj.getMethods()[y].invoke( instance, params);
								break;
							}
					}

					com.cannontech.database.db.DBPersistent db = (com.cannontech.database.db.DBPersistent)instance;
					com.cannontech.clientutils.CTILogger.info("		RETRIEVING " );
					//m = obj.getMethod("retrieve", null);
					//m.invoke(instance, null);						
					db.setDbConnection(conn);
					db.retrieve();
				}				
				catch( IllegalArgumentException r )
				{
					com.cannontech.clientutils.CTILogger.error( r.getMessage(), r );
				};
				
				//obj.getMethod( "retrieve", null ).invoke( obj, null);
				//multi.setDbConnection(conn);
				
			}
			
		}
		catch( ClassNotFoundException e )
		{
			com.cannontech.clientutils.CTILogger.info("ClassNotFoundException for class : " + classNames.get(i).toString() );
			continue;
		}
		//catch( java.lang.reflect.InvocationTargetException t )
		//{
			//com.cannontech.clientutils.CTILogger.info("InvocationTargetException for class : " + classNames.get(i).toString() );
		//}

	
	}
	
	
	
	
	
	 
   }
   catch (Exception e)
   {
	  com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
   }
	finally
	{
		try
		{
			if( conn != null )
			{
				conn.commit();
			}
		}
		catch( java.sql.SQLException e )
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		finally{			
			SqlUtils.close(conn);
		}
		
	}

	
}
}
