package com.cannontech.dbeditor;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

import Acme.RefInt;
import com.cannontech.common.gui.util.TreeViewPanel;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.model.DBTreeNode;

/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class TreeItemDeleter 
{
	private TreeViewPanel treeViewPanel = null;
	private DefaultMutableTreeNode[] nodes = null;
	private DBPersistent[] deletables = null;
	
	
	public TreeItemDeleter( TreeViewPanel tvp )
	{
		super();
		treeViewPanel = tvp;
		
		if( treeViewPanel == null )
			throw new IllegalArgumentException(this.getClass().getName() + 
			" only accepts a non null argument passed to its constructor");

	   nodes = 
	   	(treeViewPanel.getSelectedNodes() == null 
	   	 ? new DefaultMutableTreeNode[0]
	   	 : treeViewPanel.getSelectedNodes()) ;

		deletables = new DBPersistent[ nodes.length ];
	}


	public int executeDelete()
	{
	   int confirm = JOptionPane.NO_OPTION;
	   boolean deleteVerified = false, canDelete = false, isMultiDelete = nodes.length > 1;
		byte deleteVal = DBDeletionFuncs.STATUS_DISALLOW;
		StringBuffer unableDel = new StringBuffer(), message = new StringBuffer();
		String dbDeletionWarning = "";
		
		if( nodes.length <= 0 )
		{
			javax.swing.JOptionPane.showMessageDialog(
				getParentFrame(),
				"Something must be selected for deletion",
				"Unable to Delete",
				JOptionPane.WARNING_MESSAGE);
					
			return JOptionPane.NO_OPTION;			
		}


		for( int i = 0; i < nodes.length; i++ )
		{
		   if( !(nodes[i].getUserObject() instanceof com.cannontech.database.data.lite.LiteBase) )
		   {
				javax.swing.JOptionPane.showMessageDialog(
					getParentFrame(),
					"Only Yukon data items can be deleted",
					"Unable to Delete",
					JOptionPane.WARNING_MESSAGE);
					
				return JOptionPane.NO_OPTION;
		   }

			if( (nodes[i] instanceof DBTreeNode)
				  && ((DBTreeNode)nodes[i]).isSystemReserved() )
			{
				javax.swing.JOptionPane.showMessageDialog(
					getParentFrame(),
					"System reserved items cannot be deleted",
					"Unable to Delete",
					JOptionPane.WARNING_MESSAGE);
					
				return JOptionPane.NO_OPTION;
			}
	
		
			//a DBPersistent object must be created from the Lite object so you can do a delete
//			deletables[i] =
//				com.cannontech.database.data.lite.LiteFactory.createDBPersistent(
//						(com.cannontech.database.data.lite.LiteBase) nodes[i].getUserObject());

			//do some mapping to get the compatible DBPersistent
			deletables[i] = 
				LiteFactory.convertLiteToDBPers( (LiteBase)nodes[i].getUserObject() );
				
			RefInt delID = new RefInt(0), delType = new RefInt(0);
			 			
			//get the info about this possible deletion candidate
			deleteVerified = DBDeletionFuncs.getDeleteInfo( 
										deletables[i], delID, delType, 
										message, unableDel,
								 		nodes[i].getUserObject().toString() );
	 		
	   
			if( deleteVerified )
			{
				try
				{	
		         deleteVal = DBDeletionFuncs.deletionAttempted(delID.val, delType.val);
		         dbDeletionWarning = DBDeletionFuncs.getTheWarning().toString();
		         
		         //as soon as we can NOT delete, preserve that false value
		         canDelete = (deleteVal == DBDeletionFuncs.STATUS_ALLOW || deleteVal == DBDeletionFuncs.STATUS_CONFIRM);
		         
		         if( !canDelete )
		         	break;
		         
				}
				catch (java.sql.SQLException e)
				{
					com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
					confirm = javax.swing.JOptionPane.showConfirmDialog(
							getParentFrame(),
							"Are you sure you want to permanently delete '" + nodes[i] + "'?",
							"Confirm Delete",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE);							
				}
					
			}	
		}
			
		//get a final verdict for permission to delete the node or not
		canDelete &= deleteVerified; //be sure we can do both
		
		
		String title = null, msgUsed = null;
		title =  ( isMultiDelete && canDelete ? "Confirm Multi-Delete" :
					( isMultiDelete ? "Unable to Multi-Delete Items" :
					( canDelete ? "Confirm Delete" :
					  "Unable to Delete")));

		msgUsed = ( isMultiDelete && canDelete ? "Are you sure you want to permanently delete all the items selected?" :
					 ( isMultiDelete ? unableDel + dbDeletionWarning : //message.toString() :
					 ( canDelete ? message.toString() + dbDeletionWarning :
					   unableDel + dbDeletionWarning)));

		if( canDelete )
		{
			confirm = javax.swing.JOptionPane.showConfirmDialog(
					getParentFrame(),
					msgUsed, 
					title,
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE);
		}
		else
		{
			javax.swing.JOptionPane.showMessageDialog(
				getParentFrame(),				
				msgUsed,
				title,
				JOptionPane.WARNING_MESSAGE);
	 	
			confirm = JOptionPane.NO_OPTION;
		}
	
		return confirm;	
	}



	public DefaultMutableTreeNode[] getNodes()
	{
		return nodes;
	}
	
	public DBPersistent[] getDeletables()
	{
		return deletables;
	}


	private JFrame getParentFrame()
	{
		return (JFrame)com.cannontech.common.util.CtiUtilities.getParentFrame(treeViewPanel );		
	}

}
