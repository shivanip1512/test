/*
 * Generated file - Do not edit!
 */
package com.cannontech.ejb;



/**
 * Home interface for DatabaseCache. Lookup using {1}
 * @xdoclet-generated at Nov 5, 2002 2:19:53 PM
 */
public interface TimedDatabaseCacheHome
   extends javax.ejb.EJBHome
{
   public static final String COMP_NAME="java:comp/env/ejb/TimedDatabaseCache";
   public static final String JNDI_NAME="jndi/TimedDatabaseCacheBean";

	public com.cannontech.ejb.TimedDatabaseCache create() throws javax.ejb.CreateException, java.rmi.RemoteException;
}
