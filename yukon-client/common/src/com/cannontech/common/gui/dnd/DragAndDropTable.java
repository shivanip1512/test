package com.cannontech.common.gui.dnd;

/**
 * Insert the type's description here.
 * Creation date: (3/7/00 2:09:37 PM)
 * @author: 
 */



import java.awt.dnd.*;
import java.awt.datatransfer.DataFlavor;
import java.io.*;

public class DragAndDropTable extends javax.swing.JTable implements java.awt.dnd.DropTargetListener
{
	DropTarget dropTarget;
	DropTarget viewDropTarget;



/**
 * DragAndDropTree constructor comment.
 */
public DragAndDropTable() 
{
	super();
	dropTarget = new DropTarget( this, DnDConstants.ACTION_MOVE, this, true );
}
/**
 * Insert the method's description here.
 * Creation date: (1/31/00 2:08:42 PM)
 */
private void addDevice(com.cannontech.database.data.lite.LiteYukonPAObject device)
{
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	java.util.List devicePoints = cache.getAllPoints();
	
	for (int i=0; i<devicePoints.size(); i++)
	{
		if( device.getYukonID() == ((com.cannontech.database.data.lite.LitePoint)devicePoints.get(i)).getPaobjectID() )
			addPoint( String.valueOf( ((com.cannontech.database.data.lite.LitePoint)devicePoints.get(i)).getPointID()) );
	}
}
/**
 * Insert the method's description here.
 * Creation date: (1/31/00 2:08:42 PM)
 */
public void addDevice(final String deviceName, Object[] points )
{
	if( points.length > 0 ) // only add devices that have points
	{	
		for( int i = 0; i < points.length; i++ )
		{		

			if( points[i] instanceof com.cannontech.database.model.DBTreeNode )
			{
				Object userObject = ((com.cannontech.database.model.DBTreeNode)points[i]).getUserObject();
				String pointid = String.valueOf( ((com.cannontech.database.data.lite.LitePoint)userObject).getPointID() );
				
				addPoint(pointid);
			}
		}
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (1/31/00 2:08:42 PM)
 * @param device java.lang.String
 * @param pointid java.lang.String
 * @param pointName java.lang.String
 */
public void addPoint( final String pointid )
{
	IDroppableTableModel tableModel = (IDroppableTableModel)this.getModel();

	// just return if the point is already in the right tree
	if( tableModel.pointExists( pointid ) )
		return;

	tableModel.insertNewRow( pointid ); 
}
/**
 * dragEnter method comment.
 */
public void dragEnter(java.awt.dnd.DropTargetDragEvent dtde) 
{
	dtde.acceptDrag( DnDConstants.ACTION_MOVE  );
}
/**
 * dragExit method comment.
 */
public void dragExit(java.awt.dnd.DropTargetEvent dte) {}
/**
 * dragOver method comment.
 */
public void dragOver(java.awt.dnd.DropTargetDragEvent dtde) {}
/**
 * drop method comment.
 */
public synchronized void drop(java.awt.dnd.DropTargetDropEvent dtde) 
{	
	try
	{
		java.awt.datatransfer.Transferable tr = dtde.getTransferable();

		if( tr.isDataFlavorSupported( TransferableTreeNode.DEFAULT_MUTABLE_TREENODE_FLAVOR ) )
		{
			dtde.acceptDrop( DnDConstants.ACTION_MOVE );
			Object userObject = tr.getTransferData( TransferableTreeNode.DEFAULT_MUTABLE_TREENODE_FLAVOR );

			if( userObject instanceof com.cannontech.database.data.lite.LiteYukonPAObject )  // insert device
			{
				com.cannontech.database.data.lite.LiteYukonPAObject device = (com.cannontech.database.data.lite.LiteYukonPAObject)userObject;
				addDevice( device );
			}
			else if(userObject instanceof com.cannontech.database.data.lite.LitePoint)  //insert point
			{
				addPoint( String.valueOf(
						((com.cannontech.database.data.lite.LitePoint)userObject).getPointID() ) );
			}

			dtde.getDropTargetContext().dropComplete( true );			
		}
		else if( tr.isDataFlavorSupported( DataFlavor.stringFlavor ) )
		{
			dtde.acceptDrop( DnDConstants.ACTION_MOVE );
			String str = (String)tr.getTransferData( DataFlavor.stringFlavor );

			//addElement()
			dtde.getDropTargetContext().dropComplete( true );			
		}
		else if( tr.isDataFlavorSupported( DataFlavor.plainTextFlavor ) )
		{
			dtde.acceptDrop( DnDConstants.ACTION_MOVE );
			Object stream = tr.getTransferData( DataFlavor.plainTextFlavor );

			if( stream instanceof InputStream )
			{
				InputStreamReader isr = new InputStreamReader((InputStream)stream);
				BufferedReader reader = new BufferedReader( isr );
				String line;

				while( (line = reader.readLine()) != null )
					//addElement()
				
				dtde.getDropTargetContext().dropComplete( true );
			}
			else if( stream instanceof Reader )
			{
				BufferedReader reader = new BufferedReader((Reader)stream);
				String line;

				while( (line = reader.readLine()) != null )
					//addElement()
				
				dtde.getDropTargetContext().dropComplete( true );
			}
			else
			{
				System.err.println("Unknown type in Drag&Drop drop() : " + stream.getClass() );
				dtde.rejectDrop();
			}
				
		}
		else
		{
			System.err.println("Rejected Drop&Drag");
			dtde.rejectDrop();
		}
		
	}
	catch( java.io.IOException io )
	{
		handleException( io );
		dtde.rejectDrop();
	}
	catch( java.awt.datatransfer.UnsupportedFlavorException ufe )
	{
		handleException( ufe );
		dtde.rejectDrop();		
	}
		
}
/**
 * dropActionChanged method comment.
 */
public void dropActionChanged(java.awt.dnd.DropTargetDragEvent dtde) {}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION DragAndDropTable() ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Insert the method's description here.
 * Creation date: (2/2/00 2:21:50 PM)
 */
public void resizeTable() 
{
	// This is here to force the table to resize all the columns
	// so they take up the whole space of the table.
	// The table does not actually get sized to 1,1,1,1 because of the layout
	setBounds( 1, 1, 1, 1 );

	getTableHeader().revalidate();
	getTableHeader().repaint();
	revalidate();
	repaint();

}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:33:57 PM)
 * @param num long
 */
public void setViewDropTarget(javax.swing.JComponent comp) 
{
	viewDropTarget = new java.awt.dnd.DropTarget(comp, java.awt.dnd.DnDConstants.ACTION_MOVE, this, true);
}
}
