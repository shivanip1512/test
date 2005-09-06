package com.cannontech.web.editor;

import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.model.SelectItem;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.yukon.conns.ConnPool;

/**
 * Form for executing editor changes for DBPersitants
 *
 */
public abstract class DBEditorForm implements DBEditorTypes
{
    private DBPersistent dbPersistent = null;

	//contains: Key:"TabName", Value:Boolean
	private Map visibleTabs = new HashMap(16);

	//title of our editor panel
	private String editorTitle = "Database Editor";

	//dummy UI comp to be used for internal event firing only
	protected static final UIComponent DUMMY_UI = new UIData();

	//generic time list in seconds for a number of fields
	protected static final SelectItem[] timeInterval;

	//init our static data with real values
	static {
		//value, label
		timeInterval = new SelectItem[18];
		timeInterval[0] = new SelectItem(new Integer(5), "5 seconds");
		timeInterval[1] = new SelectItem(new Integer(10), "10 seconds");
		timeInterval[2] = new SelectItem(new Integer(15), "15 seconds");
		timeInterval[3] = new SelectItem(new Integer(30), "30 seconds");
		timeInterval[4] = new SelectItem(new Integer(60), "1 minute");
		timeInterval[5] = new SelectItem(new Integer(120), "2 minutes");
		timeInterval[6] = new SelectItem(new Integer(180), "3 minutes");
		timeInterval[7] = new SelectItem(new Integer(300), "5 minutes");
		timeInterval[8] = new SelectItem(new Integer(600), "10 minutes");
		timeInterval[9] = new SelectItem(new Integer(900), "15 minutes");
		timeInterval[10] = new SelectItem(new Integer(1200), "20 minutes");
		timeInterval[11] = new SelectItem(new Integer(1500), "25 minutes");		
		timeInterval[12] = new SelectItem(new Integer(1800), "30 minutes");
		timeInterval[13] = new SelectItem(new Integer(3600), "1 hour");
		timeInterval[14] = new SelectItem(new Integer(7200), "2 hours");
		timeInterval[15] = new SelectItem(new Integer(21600), "6 hours");
		timeInterval[16] = new SelectItem(new Integer(43200), "12 hours");
		timeInterval[17] = new SelectItem(new Integer(86400), "1 day");
	}


	/**
	 * Resets this form with the original values from the database
	 *
	 */
	public void resetForm() {
		
		initItem();
	}


	/**
	 * Initializes the dbpersistent object from the database
	 */
	protected abstract void initItem();
	/**
	 * Updates the content of the current dbpersistent object into the database
	 */
	public abstract void update();


	/**
	 * Updates a given DB object.
	 */
	protected void updateDBObject( DBPersistent db, FacesMessage facesMsg ) throws TransactionException {

		if( facesMsg == null ) facesMsg = new FacesMessage();
		
		try {
			Transaction t = Transaction.createTransaction( Transaction.UPDATE, db );
			t.execute();

			generateDBChangeMsg( db, DBChangeMsg.CHANGE_TYPE_UPDATE );
		}
		catch( TransactionException e ) {
			CTILogger.error( e.getMessage(), e );
			facesMsg.setDetail( "Error updating the database, " + e.getMessage() );
			facesMsg.setSeverity( FacesMessage.SEVERITY_ERROR );

			throw e; //chuck this thing up
		}
		catch( Exception e ) {
			CTILogger.error( e.getMessage(), e );
			facesMsg.setDetail( "Unable to update the database, " + e.getMessage() );
			facesMsg.setSeverity( FacesMessage.SEVERITY_ERROR );

			throw new TransactionException(e.getMessage(), e); //chuck this thing up
		}
		
	}

	/**
	 * Add a given DB object.
	 */
	protected void addDBObject( DBPersistent db, FacesMessage facesMsg ) throws TransactionException {

		if( facesMsg == null ) facesMsg = new FacesMessage();

		try {
			Transaction t = Transaction.createTransaction( Transaction.INSERT, db );
			t.execute();

			generateDBChangeMsg( db, DBChangeMsg.CHANGE_TYPE_ADD );
		}
		catch( TransactionException e ) {
			CTILogger.error( e.getMessage(), e );
			facesMsg.setDetail( "Error insert into the database, " + e.getMessage() );
			facesMsg.setSeverity( FacesMessage.SEVERITY_ERROR );

			throw e; //chuck this thing up
		}
		catch( Exception e ) {
			CTILogger.error( e.getMessage(), e );
			facesMsg.setDetail( "Unable to insert into the database, " + e.getMessage() );
			facesMsg.setSeverity( FacesMessage.SEVERITY_ERROR );

			throw new TransactionException(e.getMessage(), e); //chuck this thing up
		}
		
	}

	/**
	 * Delete a given DB object.
	 */
	protected void deleteDBObject( DBPersistent db, FacesMessage facesMsg ) throws TransactionException {

		if( facesMsg == null ) facesMsg = new FacesMessage();

		try {
			Transaction t = Transaction.createTransaction( Transaction.DELETE, db );
			t.execute();

			generateDBChangeMsg( db, DBChangeMsg.CHANGE_TYPE_DELETE );
		}
		catch( TransactionException e ) {
			CTILogger.error( e.getMessage(), e );
			facesMsg.setDetail( "Error delete from the database, " + e.getMessage() );
			facesMsg.setSeverity( FacesMessage.SEVERITY_ERROR );

			throw e; //chuck this thing up
		}
		catch( Exception e ) {
			CTILogger.error( e.getMessage(), e );
			facesMsg.setDetail( "Unable to delete from the database, " + e.getMessage() );
			facesMsg.setSeverity( FacesMessage.SEVERITY_ERROR );

			throw new TransactionException(e.getMessage(), e); //chuck this thing up
		}
		
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
				//LiteBase lBase =
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


	/**
	 * Retuns the wizard URL for the given editorType. Add all editor types
	 * and their starting panel here.
	 * 
	 */
	public static String getWizardURL( int wizardType ) {
		
		switch(wizardType) {
			case DBEditorForm.EDITOR_POINT:
				return "/editor/point/pointWizard.jsf";

			case DBEditorForm.EDITOR_CAPCONTROL:
				return "/editor/pao/cbcWizard.jsf";
			
			default:
				CTILogger.info("Uknown WizardType ("+wizardType+"), redirecting to same page");
				return "";
		}

	}

	/**
	 * Retuns the editor URL for the  given editorType. Add all editor types
	 * and their starting panel here.
	 * 
	 */
	public static String getEditorURL( int editorType ) {
		
		switch(editorType) {
			case DBEditorForm.EDITOR_POINT:
				return "/editor/point/pointEditor.jsf";

			case DBEditorForm.EDITOR_CAPCONTROL:
				return "/editor/pao/cbcEditor.jsf";
			
			default:
				CTILogger.info("Uknown EditorType ("+editorType+"), redirecting to same page");
				return "";
		}

	}

	/**
	 * Retuns the editor URL for the  given litetype
	 * 
	 * @param liteType: int type from the Lites
	 * @param itemID:  the primary key identifier
	 * @param itemType: specific type within the given litetype 
	 * 			(Status Point, CCU711 Transmitter)
	 */
	public static String getEditorURL( int liteType, int itemID ) {
		
		return getEditorURL(liteType) +
			"?itemid=" + itemID;
			//"&itemtype=" + itemType;
	}

	/**
	 * @return
	 */
	public String getEditorTitle() {
		return editorTitle;
	}

	/**
	 * @param string
	 */
	public void setEditorTitle(String string) {
		editorTitle = string;
	}

}
