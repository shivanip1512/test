/*
 * Generated file - Do not edit!
 */
package com.cannontech.ejb;

import java.lang.*;
import com.cannontech.database.TransactionException;
import java.util.List;
import javax.ejb.SessionBean;
import javax.ejb.EJBException;
import javax.ejb.SessionContext;
import javax.ejb.RemoveException;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.yukon.IDatabaseCache;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.cache.CacheDBChangeListener;
import com.cannontech.yukon.server.cache.ServerDatabaseCache;

/**
 * Home interface for DatabaseCache. Lookup using {1}
 * @xdoclet-generated at Nov 4, 2002 5:13:03 PM
 */
public interface DatabaseCacheHome
   extends javax.ejb.EJBHome
{
   public static final String COMP_NAME="java:comp/env/ejb/DatabaseCache";
   public static final String JNDI_NAME="jndi/DatabaseCacheBean";
   
	public com.cannontech.ejb.DatabaseCache create() throws javax.ejb.CreateException, java.rmi.RemoteException;

}
