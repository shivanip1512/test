package com.cannontech.web.delete;

import javax.faces.application.FacesMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.DBDeleteResult;
import com.cannontech.core.dao.DBDeletionDao;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.web.editor.DBEditorForm;
import com.cannontech.web.util.JSFParamUtil;

/**
 * @author ryan
 *
 */
public abstract class DeleteForm extends DBEditorForm
{
	private int[] itemIDs = new int[0];
	private Deleteable[] deletables = null; //new Deleteable[0];
	
	public DeleteForm() {
		super();
		initItem();
	}

	/**
	 * Returns the DB object for the given ID; this could be a Point, PAO, Customer, etc.
	 */
	abstract DBPersistent getDBObj( int itemID );



	/**
	 * Calls the delete() method for each item that we are able to
	 * delete
	 */
	public void update() {
	
		if (deletables != null) {
			for( int i = 0; i < deletables.length; i++ ) {
				
				//this message will be filled in by the super class
				FacesMessage facesMsg = new FacesMessage();
				try {				
					Deleteable deleteable = deletables[i];
                    //be sure we can attempt to delete this item
					if( deleteable.isDeleteAllowed() && deleteable.getChecked().booleanValue() ) {
						deleteDBObject( deleteable.getDbPersistent(), facesMsg );
                        deleteable.setWasDeleted( true );
						facesMsg.setDetail( "...deleted" );
					}
					else
						facesMsg.setDetail( "Item not deleted" );
				}
				catch( TransactionException te ) {
					//do nothing since the appropriate actions was taken in the super
				}
				finally {
					deletables[i].setWarningMsg( facesMsg.getDetail() );
					deletables[i].setDeleteError( facesMsg.getSeverity() == FacesMessage.SEVERITY_ERROR );
				}
			}
		}

	}
	
	public void initItem() {

		//must be int[] paoIDs
		String[] ids =   JSFParamUtil.getReqParamsVar("value");
		if( ids == null ) return;
		
		itemIDs = new int[ids.length];
		for( int i = 0; i < ids.length; i++ ) {
			itemIDs[i] = Integer.parseInt(ids[i]);
			CTILogger.debug( "  DeleteFrom inited for item id = " + itemIDs[i]);
		}

	}

	/**
	 * Retrieves the items that are to be deleted
	 */
	//abstract Deleteable[] getDeleteItems();


	/**
	 * Determine if we can delete this item and set any message as to why
	 * we may or may not be able to delete it.
	 */
	protected void setDeleteMsgs( Deleteable delItem ) {
		
		DBDeleteResult delRes = null;

		try {
			//get the info about this possible deletion candidate
			delRes = DaoFactory.getDbDeletionDao().getDeleteInfo( 
					delItem.getDbPersistent(), delItem.getDbPersistent().toString());
				
			int res = DaoFactory.getDbDeletionDao().deletionAttempted( delRes );
			delItem.setDeleteAllowed(
				DBDeletionDao.STATUS_DISALLOW != res );
			
			delItem.setWarningMsg(
				delItem.isDeleteAllowed()
					? delRes.getConfirmMessage().toString()
					: delRes.getUnableDelMsg().append(delRes.getDescriptionMsg()).toString() );

		}
		catch( Exception e ) {
			CTILogger.error( e.getMessage(), e );				
			delItem.setWarningMsg( "(db exception occurred)" );
		}
		
	}

	/**
	 * Retrieves the items that are to be deleted
	 */
	public Deleteable[] getDeleteItems() {

		if( getDeletables() == null ) {

			setDeletables( new Deleteable[ getItemIDs().length ] );
			for( int i = 0; i < getItemIDs().length; i++ ) {
			
				getDeletables()[i] = new Deleteable();				
				DBPersistent dbObj = getDBObj( getItemIDs()[i] );
				if( dbObj == null ) {
					CTILogger.warn("Unable to find item ID = " + getItemIDs()[i] + " in cache, ignoring entry");
					continue;
				}
				//when we delete points we have a check box that confirms the deletion,
                //on the paos we don't
				if (!(dbObj instanceof PointBase))
                {
                    getDeletables()[i].setChecked(true);
                }
                getDeletables()[i].setDbPersistent( dbObj );
				
				try {
					Transaction.createTransaction(
							Transaction.RETRIEVE,
							getDeletables()[i].getDbPersistent()).execute();
	
					//if we retrieve the item, find out if we can delete it
					setDeleteMsgs( getDeletables()[i] );
	
				} catch(Exception e) {
					e.printStackTrace();
				}
				
	
			}
		}

		return getDeletables();
	}

	/**
	 * @return
	 */
	protected Deleteable[] getDeletables() {
		return deletables;
	}

	/**
	 * @return
	 */
	protected int[] getItemIDs() {
		return itemIDs;
	}

	/**
	 * @param deleteables
	 */
	protected void setDeletables(Deleteable[] deleteables) {
		deletables = deleteables;
	}

	/**
	 * @param is
	 */
	protected void setItemIDs(int[] is) {
		itemIDs = is;
	}

}