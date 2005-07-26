package com.cannontech.web.editor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.ConnectionPool;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.yukon.conns.ConnPool;

/**
 * Form for executing editor changes for DBPersitants
 *
 */
public abstract class DBEditorForm
{
    private DBPersistent dbPersistent = null;

	//contains: Key:"TabName", Value:Boolean
	private Map visibleTabs = new HashMap(16);


    public String update() throws IOException
    {
		FacesMessage facesMsg = new FacesMessage();
		String messageString = "(blank)";
		
		try
		{
			Transaction t = Transaction.createTransaction( Transaction.UPDATE, getDbPersistent() );
			setDbPersistent( t.execute() );

			//write the DBChangeMessage out to Dispatch since it was a Successfull UPDATE
			generateDBChangeMsg( getDbPersistent(), DBChangeMsg.CHANGE_TYPE_UPDATE );
		
			messageString = getDbPersistent() + " updated successfully in the database.";

			facesMsg = new FacesMessage( "Update was SUCCESSFULL" );
		}
		catch( TransactionException e )
		{
			CTILogger.error( e.getMessage(), e );
			messageString = " Error updating " + getDbPersistent() + " in the database.  Error received: " + e.getMessage() ;
			
			facesMsg = new FacesMessage(
				FacesMessage.SEVERITY_ERROR,
				"Unable to Update the database", messageString);
		}
		
		FacesContext.getCurrentInstance().addMessage("update", facesMsg);
		
//        FacesContext facesContext = FacesContext.getCurrentInstance();
//        facesContext.getExternalContext().getApplicationMap().put("fileupload_bytes", _upFile.getBytes());
//        facesContext.getExternalContext().getApplicationMap().put("fileupload_type", _upFile.getContentType());
//        facesContext.getExternalContext().getApplicationMap().put("fileupload_name", _upFile.getName());
		return messageString;
    }

	/**
	 * Insert the method's description here.
	 * Creation date: (10/9/2001 1:49:24 PM)
	 */
	private void generateDBChangeMsg( DBPersistent object, int changeType  ) 
	{
		if( object instanceof CTIDbChange )
		{
			DBChangeMsg[] dbChange = DefaultDatabaseCache.getInstance().createDBChangeMessages(
						(CTIDbChange)object, changeType );

			for( int i = 0; i < dbChange.length; i++ )
			{
				//handle the DBChangeMsg locally
				LiteBase lBase =
					DefaultDatabaseCache.getInstance().handleDBChangeMessage(dbChange[i]);

				//notify any interested GUI compnents that we may need to change
				//updateTreePanel( lBase, dbChange[i].getTypeOfChange() );
         
				ConnPool.getInstance().getDefDispatchConn().write(dbChange[i]);
			}
		}
		else
		{
			throw new IllegalArgumentException("Non " + CTIDbChange.class.getName() +
			 	" class tried to generate a " + DBChangeMsg.class.getName() + 
				" its class was : " + object.getClass().getName() );
		}
		
	}

	/**
	 * @return
	 */
	protected DBPersistent getDbPersistent()
	{
		return dbPersistent;
	}

	/**
	 * @param persistent
	 */
	protected void setDbPersistent(DBPersistent persistent)
	{
		dbPersistent = persistent;
	}

	public Map getVisibleTabs() {
		return visibleTabs;
	}

}
