package com.cannontech.common.gui.dnd;

/**
 * Insert the type's description here.
 * Creation date: (3/7/00 2:09:37 PM)
 * @author: 
 */
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public class DragAndDropTable extends javax.swing.JTable implements java.awt.dnd.DropTargetListener, java.awt.dnd.DragGestureListener
{
	private DropTarget dropTarget;
	private DropTarget viewDropTarget;

	private DAndDJTableRendererWrapper rendererWrapper;
	private int lastDragOrigination = -1;


	//draggable components
	protected transient DragAndDropListener dndEventMulticaster = null;
	private DragSource dragSource = DragSource.getDefaultDragSource();
	private final static DragSourceListener DRAG_SOURCE_LISTENER =
			new DragSourceListenerClass();


/**
 * DragAndDropTree constructor comment.
 */
public DragAndDropTable() 
{
	super();
	initialize();

	//getRightTable().addDragAndDropListener(this);
}


private void initialize()
{
	dropTarget = new DropTarget( this, DnDConstants.ACTION_MOVE, this, true );
	
	dragSource.createDefaultDragGestureRecognizer( 
				this, 
				DnDConstants.ACTION_MOVE, 
				this );	
				
	setDefaultRenderer( Object.class, new DefaultTableCellRenderer() );
}


/**
 * 
 */
public void addDragAndDropListener(DragAndDropListener newListener) 
{
	dndEventMulticaster = 
			DragAndDropListenerMulticaster.add(dndEventMulticaster, newListener);

	return;
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
 * 
 * @param newListener com.cannontech.common.gui.util.AddRemovePanelListener
 */
public void removeDragAndDropListener(DragAndDropListener newListener) 
{
	dndEventMulticaster = 
		DragAndDropListenerMulticaster.remove(dndEventMulticaster, newListener);

	return;
}


/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireDrop_actionPerformed(java.util.EventObject newEvent) 
{
	if (dndEventMulticaster == null) 
		return;
	
	dndEventMulticaster.drop_actionPerformed(newEvent);
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
public void dragExit(java.awt.dnd.DropTargetEvent dte)
{
	updateDragBorder( -1 );
}


/**
 * dragOver method comment.
 */
public void dragOver(java.awt.dnd.DropTargetDragEvent dtde)
{
	updateDragBorder( rowAtPoint(dtde.getLocation()) );
}


/**
 * AddPointsTree constructor comment.
 * @param value java.util.Vector
 */
public void dragGestureRecognized(java.awt.dnd.DragGestureEvent dge) 
{
	int row = rowAtPoint( dge.getDragOrigin() );

	if( row >= 0 )
	{
		lastDragOrigination = row;
		
		Object o = ((IDroppableTableModel)getModel()).getRowAt(row);

		//create a legit trasferable flavor
		TransferableObjects trans = new TransferableObjects(o);

		dragSource.startDrag(
				dge,
				DragSource.DefaultMoveDrop,
				trans, 
				DRAG_SOURCE_LISTENER );
	}

}


private synchronized void insertObjectAt( final Object obj, int newRow, int origRow )
{

	if( getModel() != null && newRow != -1 )
	{
		//lock down the data model
		synchronized( getModel() )
		{
			((IDroppableTableModel)getModel()).removeRow( origRow );
			((IDroppableTableModel)getModel()).insertRowAt( obj, newRow );

			repaint();
		}
	}
	
}


public synchronized void drop(java.awt.dnd.DropTargetDropEvent dtde) 
{	
	try
	{
		java.awt.datatransfer.Transferable tr = dtde.getTransferable();

		if( tr.isDataFlavorSupported( TransferableTreeNode.DEFAULT_MUTABLE_TREENODE_FLAVOR ) )
		{
			dtde.acceptDrop( DnDConstants.ACTION_MOVE );
			Object userObject = tr.getTransferData( TransferableTreeNode.DEFAULT_MUTABLE_TREENODE_FLAVOR );

			if( userObject instanceof LiteYukonPAObject )  // insert device
			{
				LiteYukonPAObject device = (LiteYukonPAObject)userObject;
				addDevice( device );
			}
			else if(userObject instanceof LitePoint)  //insert point
			{
				addPoint( String.valueOf(
						((LitePoint)userObject).getPointID() ) );
			}

			dtde.getDropTargetContext().dropComplete( true );			
		}
		else if( tr.isDataFlavorSupported(TransferableObjects.OBJECT_FLAVOR) )
		{
			dtde.acceptDrop( DnDConstants.ACTION_MOVE );			
			Object obj = tr.getTransferData(TransferableObjects.OBJECT_FLAVOR);


			//insert our object here into the JList
			insertObjectAt( 
					obj,
					rowAtPoint(dtde.getLocation()),
					lastDragOrigination );

			
			dtde.getDropTargetContext().dropComplete( true );			
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
	

	lastDragOrigination = -1;
	updateDragBorder( -1 );
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
 * Creation date: (2/13/2002 1:18:49 PM)
 * @return javax.swing.ListCellRenderer
 */
public void setDefaultRenderer( Class clazz, TableCellRenderer re )
{
	if( rendererWrapper == null )
		rendererWrapper = new DAndDJTableRendererWrapper();

	rendererWrapper.setRealRenderer( re );
	super.setDefaultRenderer( clazz, rendererWrapper );
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


/**
 * Insert the method's description here.
 * Creation date: (2/13/2002 4:00:32 PM)
 */
private void updateDragBorder( int row )
{
	//remove the boarder line we have in our JList
	rendererWrapper.setDragOverRow( row );
	revalidate();
	repaint();
}

}
