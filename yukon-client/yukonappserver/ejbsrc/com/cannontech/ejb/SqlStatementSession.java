/*
 * Generated file - Do not edit!
 */
package com.cannontech.ejb;

import java.lang.*;
import java.rmi.RemoteException;
import java.sql.Connection;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import com.cannontech.common.util.CommandExecutionException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.Transaction;
import com.cannontech.yukon.ISQLStatement;

/**
 * Session layer for SqlStatement.
 * @xdoclet-generated at Nov 4, 2002 4:47:09 PM
 */
public class SqlStatementSession
   extends com.cannontech.ejb.SqlStatementBean
   implements javax.ejb.SessionBean
{

   static final long serialVersionUID = -9182983115029395155L;

   public void ejbActivate() throws javax.ejb.EJBException, java.rmi.RemoteException
   {
      super.ejbActivate();
   }

   public void ejbPassivate() throws javax.ejb.EJBException, java.rmi.RemoteException
   {
      super.ejbPassivate();
   }

   public void setSessionContext(javax.ejb.SessionContext ctx) throws javax.ejb.EJBException, java.rmi.RemoteException
   {
      super.setSessionContext(ctx);
   }

   public void unsetSessionContext() 
   {
   }

   public void ejbRemove() throws javax.ejb.EJBException, java.rmi.RemoteException
   {
      super.ejbRemove();
   }

   public void ejbCreate() throws javax.ejb.CreateException
   {
      super.ejbCreate();
   }

}
