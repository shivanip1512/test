package com.cannontech.web.delete;

import javax.faces.application.FacesMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.functions.DBDeleteResult;
import com.cannontech.database.cache.functions.DBDeletionFuncs;
import com.cannontech.database.data.pao.PAOFactory;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.web.editor.DBEditorForm;
import com.cannontech.web.util.JSFParamUtil;

/**
 * @author ryan
 *
 */
public class DeleteForm extends DBEditorForm
{
	private int[] paoIDs = new int[0];
	private Deleteable[] dels = null; //new Deleteable[0];
	
	public DeleteForm() {
		super();
		initItem();
	}

	/**
	 * Calls the delete() method for each item that we are able to
	 * delete
	 */
	public void update() {
	
		for( int i = 0; i < dels.length; i++ ) {
			
			//this message will be filled in by the super class
			FacesMessage facesMsg = new FacesMessage();
			try {				
				//be sure we can attempt to delete this item
				if( dels[i].isDeleteAllowed() ) {
					deleteDBObject( dels[i].getDbPersistent(), facesMsg );
					dels[i].setWasDeleted( true );
					facesMsg.setDetail( "...deleted" );
				}
				else
					facesMsg.setDetail( "Item not deleted" );
			}
			catch( TransactionException te ) {
				//do nothing since the appropriate actions was taken in the super
			}
			finally {
				dels[i].setWarningMsg( facesMsg.getDetail() );
				dels[i].setDeleteError( facesMsg.getSeverity() == FacesMessage.SEVERITY_ERROR );
			}
		}

	}
	
	public void initItem() {

		//must be int[] paoIDs
		String[] ids = JSFParamUtil.getReqParamsVar("value");
		if( ids == null ) return;
		
		paoIDs = new int[ids.length];
		for( int i = 0; i < ids.length; i++ ) {
			paoIDs[i] = Integer.parseInt(ids[i]);
			CTILogger.debug( "  DeleteFrom inited for PAO id = " + paoIDs[i]);
		}

	}

	/**
	 * Retrieves the items that are to be deleted
	 */
	public Deleteable[] getDeleteItems() {

		if( dels == null ) {

			dels = new Deleteable[ paoIDs.length ];
			for( int i = 0; i < paoIDs.length; i++ ) {
			
				dels[i] = new Deleteable();				
				YukonPAObject dbPao = PAOFactory.createPAObject(paoIDs[i]);
				if( dbPao == null ) {
					CTILogger.warn("Unable to find PAOid = " + paoIDs[i] + " in cache, ignoring entry");
					continue;
				}
				
				dels[i].setDbPersistent(
						PAOFactory.createPAObject(paoIDs[i]) );
				
				try {
					Transaction.createTransaction(
							Transaction.RETRIEVE,
							dels[i].getDbPersistent()).execute();
	
					//if we retrieve the item, find out if we can delete it
					setDeleteMsgs( dels[i] );
	
				} catch(Exception e) {
					e.printStackTrace();
				}
				
	
			}
		}

		return dels;
	}


	/**
	 * Determine if we can delete this item and set any message as to why
	 * we may or may not be able to delete it.
	 */
	private void setDeleteMsgs( Deleteable delItem ) {
		
		DBDeleteResult delRes = null;

		try {
			//get the info about this possible deletion candidate
			delRes = DBDeletionFuncs.getDeleteInfo( 
					delItem.getDbPersistent(), delItem.getDbPersistent().toString());
				
			int res = DBDeletionFuncs.deletionAttempted( delRes );
			delItem.setDeleteAllowed(
				DBDeletionFuncs.STATUS_DISALLOW != res );

			delItem.setWarningMsg(
				delItem.isDeleteAllowed()
					? delRes.getConfirmMessage().toString()
					: delRes.getUnableDelMsg().append(delRes.getDescriptionMsg()).toString() );

		}
		catch( Exception e ) {
			CTILogger.error( e.getMessage(), e );				
			delItem.setWarningMsg( "(db exception occured)" );
		}
		
	}
}