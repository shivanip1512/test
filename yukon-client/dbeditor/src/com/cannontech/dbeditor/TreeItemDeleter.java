package com.cannontech.dbeditor;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

import Acme.RefInt;
import com.cannontech.common.gui.util.TreeViewPanel;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.db.DBPersistent;

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

	   nodes = treeViewPanel.getSelectedNodes();
		deletables = new DBPersistent[ nodes.length ];
	}


	public int executeDelete()
	{
	   int confirm = JOptionPane.NO_OPTION;
	   boolean deleteVerified = false, canDelete = false, isMultiDelete = nodes.length > 1;
		byte deleteVal = DBDeletionWarn.STATUS_DISALLOW;
		StringBuffer unableDel = new StringBuffer(), message = new StringBuffer();
		String dbDeletionWarning = "";
		
		
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
	
		
			//a DBPersistent object must be created from the Lite object so you can do a delete
//			deletables[i] =
//				com.cannontech.database.data.lite.LiteFactory.createDBPersistent(
//						(com.cannontech.database.data.lite.LiteBase) nodes[i].getUserObject());

			//do some mapping to get the compatible DBPersistent
			deletables[i] = 
				LiteFactory.convertLiteToDBPers( (LiteBase)nodes[i].getUserObject() );
				
			RefInt delID = new RefInt(0), delType = new RefInt(0);
			 			
			//get the info about this possible deletion candidate
			deleteVerified = getDeleteInfo( deletables[i], 
										delID, delType, message, unableDel,
								 		nodes[i].getUserObject().toString() );
	 		
	   
			if( deleteVerified )
			{
				try
				{	
		         deleteVal = DBDeletionWarn.deletionAttempted(delID.val, delType.val);
		         dbDeletionWarning = DBDeletionWarn.getTheWarning().toString();
		         
		         //as soon as we can NOT delete, preserve that false value
		         canDelete = (deleteVal == DBDeletionWarn.STATUS_ALLOW || deleteVal == DBDeletionWarn.STATUS_CONFIRM);
		         
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


/*	private int isDBPersistentDeletable( DBPersistent dbPers, StringBuffer tmp, DefaultMutableTreeNode node, boolean showConfirm  )
	{
		int retVal = 0;
		tmp.setLength(0);
		
		if( dbPers instanceof com.cannontech.database.data.pao.YukonPAObject )
		{
			tmp.append("Are you sure you want to permanently delete '" + node.toString() + 
					"' and all of its points?\n\n" +
					"*The delete process will take extra time if several points are present.\n" +
					"*All points history will also be deleted.");
			
			if( showConfirm )	
				retVal =
					javax.swing.JOptionPane.showConfirmDialog(
						getParentFrame(),
						tmp.toString(),
						"Confirm Delete",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE);
		}
		else if( dbPers instanceof com.cannontech.database.db.notification.AlarmCategory )
		{
			tmp.append("You can not delete alarm categories using the DatabaseEditor"); 
	
			if( showConfirm )	
				javax.swing.JOptionPane.showMessageDialog(
					getParentFrame(),
					tmp.toString(), 
					"Unable to Delete",
					JOptionPane.OK_OPTION );
			
			retVal = JOptionPane.NO_OPTION;
		}
		else
		{
			tmp.append("Are you sure you want to permanently delete '" + node.toString() + "'?");
			
			if( showConfirm )	 
				retVal =
					javax.swing.JOptionPane.showConfirmDialog(
						getParentFrame(),
						tmp.toString(),
						"Confirm Delete",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE);
		}
	
		return retVal;
	}
*/

	private boolean getDeleteInfo( DBPersistent toDelete, RefInt delID, 
			RefInt delType, StringBuffer message, StringBuffer unableDel, final String nodeName )
	{
		int anID = 0, deletionType = 0;
		message.setLength(0);
		unableDel.setLength(0);
		boolean retValue = true;
		
		if (toDelete instanceof com.cannontech.database.data.point.PointBase)
		{
			message.append("Are you sure you want to Permanently Delete '" + nodeName + "'?");
			unableDel.append("You cannot delete the point '" + nodeName + "'");
			anID = ((com.cannontech.database.data.point.PointBase) toDelete).getPoint().getPointID().intValue();
			deletionType = DBDeletionWarn.POINT_TYPE;
		}
		else if (toDelete instanceof com.cannontech.database.data.notification.NotificationRecipient)
		{
         message.append("Are you sure you want to permanently delete '" + nodeName + "'?");
			unableDel.append("You cannot delete the notification recipient '" + nodeName + "'");
			anID = ((com.cannontech.database.data.notification.NotificationRecipient) toDelete).getNotificationRecipient().getRecipientID().intValue();
			deletionType = DBDeletionWarn.NOTIFICATION_TYPE;
		}		
		else if (toDelete instanceof com.cannontech.database.data.notification.GroupNotification)
		{
			message.append("Are you sure you want to permanently delete '" + nodeName + "'?");
			unableDel.append("You cannot delete the notification group '" + nodeName + "'");
			anID = ((com.cannontech.database.data.notification.GroupNotification) toDelete).getNotificationGroup().getNotificationGroupID().intValue();
			deletionType = DBDeletionWarn.NOTIFICATION_TYPE;
		}
		else if (toDelete instanceof com.cannontech.database.data.state.GroupState)
		{
			message.append("Are you sure you want to permanently delete '" + nodeName + "'?");
			unableDel.append("You cannot delete the state group '" + nodeName + "'");
			anID = ((com.cannontech.database.data.state.GroupState) toDelete).getStateGroup().getStateGroupID().intValue();
			deletionType = DBDeletionWarn.STATEGROUP_TYPE;
		}
		else if (toDelete instanceof com.cannontech.database.data.port.DirectPort)
		{
			message.append("Are you sure you want to permanently delete '" + ((com.cannontech.database.data.port.DirectPort) toDelete).getPortName() + "?");
			unableDel.append("You cannot delete the comm port '" + ((com.cannontech.database.data.port.DirectPort) toDelete).getPortName() + "'");
			anID = ((com.cannontech.database.data.port.DirectPort) toDelete).getCommPort().getPortID().intValue();
			deletionType = DBDeletionWarn.PORT_TYPE;
		}
		else if (toDelete instanceof com.cannontech.database.data.device.DeviceBase)
		{
			message.append("Are you sure you want to permanently delete '" + nodeName + "?");
			unableDel.append("You cannot delete the device '" + nodeName + "'");
			anID = ((com.cannontech.database.data.device.DeviceBase) toDelete).getDevice().getDeviceID().intValue();
			deletionType = DBDeletionWarn.DEVICE_TYPE;
		}
		else if( toDelete instanceof com.cannontech.database.data.pao.YukonPAObject )
		{
			message.append("Are you sure you want to permanently delete '" + nodeName + 
								"' and all of its points?\n\n" +
								"*The delete process will take extra time if several points are present.\n" +
								"*All points history will also be deleted.");
			unableDel.append("You cannot delete the point attachable object '" + nodeName + "'");
			anID = ((com.cannontech.database.data.pao.YukonPAObject) toDelete).getPAObjectID().intValue();
			deletionType = DBDeletionWarn.PAO_TYPE;
		}
		else if( toDelete instanceof com.cannontech.database.db.notification.AlarmCategory )
		{
			message.append("You can not delete alarm categories using the DatabaseEditor"); 
			unableDel.append("You cannot delete the AlarmCategory '" + nodeName + "'");
			anID = ((com.cannontech.database.db.notification.AlarmCategory) toDelete).getAlarmCategoryID().intValue();

			retValue = false;
		}
		else
		{
			message.append("You can not delete this object using the DatabaseEditor"); 
			unableDel.append("You cannot delete object named '" + nodeName + "'");
						
			retValue = false;
		}

	
	
		delID.val = anID;
		delType.val = deletionType;
		return retValue;
	}


	private JFrame getParentFrame()
	{
		return (JFrame)com.cannontech.common.util.CtiUtilities.getParentFrame(treeViewPanel );		
	}

}
