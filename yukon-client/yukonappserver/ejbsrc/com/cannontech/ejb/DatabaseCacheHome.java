/*
 * Generated file - Do not edit!
 */
package com.cannontech.ejb;



/**
 * Home interface for DatabaseCache. Lookup using {1}
 * @xdoclet-generated at Nov 5, 2002 2:19:53 PM
 */
public interface DatabaseCacheHome
   extends javax.ejb.EJBHome
{
   public static final String COMP_NAME="java:comp/env/ejb/DatabaseCache";
   public static final String JNDI_NAME="jndi/DatabaseCacheBean";

	public com.cannontech.ejb.DatabaseCache create() throws javax.ejb.CreateException, java.rmi.RemoteException;
}
