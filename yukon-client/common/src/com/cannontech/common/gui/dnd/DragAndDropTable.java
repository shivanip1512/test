package com.cannontech.common.gui.dnd;

/**
 * Insert the type's description here.
 * Creation date: (3/7/00 2:09:37 PM)
 * @author: 
 */
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.util.ArrayList;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

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
	this(null);
}
/**
 * DragAndDropTree constructor comment.
 */
public DragAndDropTable(DAndDJTableRendererWrapper rendererWrapper_) 
{
	super();
	rendererWrapper = rendererWrapper_;
	initialize();
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
 * @param device java.lang.String
 * @param pointid java.lang.String
 * @param pointName java.lang.String
 */
public void addObject( final Object object_ )
{
	IDroppableTableModel tableModel = (IDroppableTableModel)this.getModel();

	// just return if the point is already in the right tree
	if( tableModel.objectExists( object_ ) )
		return;

	tableModel.insertNewRow( object_ ); 
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
	int row = rowAtPoint(dtde.getLocation());
	updateDragBorder( row );
	
	ArrayList rowNums = getViewableRowNumbers();
	if( rowNums != null && rowNums.size() > 0 )
	{
		Integer min = (Integer)rowNums.get(0);
		Integer max = (Integer)rowNums.get( rowNums.size()-1 );

//System.out.println("  min=" + min + ", max=" + max + ", row=" + row );
//System.out.println("  	dtdeY=" + dtde.getLocation().getY() + ", hght=" + getRowHeight() );

		if( row > 0 && row <= (min.intValue()+1) ) //allow for a wider tolerance to scroll
		{
			scrollRectToVisible( new Rectangle(
				new Point(0, (int)dtde.getLocation().getY() - getRowHeight()) ) );
		}
		else if( row < getRowCount() && row >= max.intValue() )
		{						
			scrollRectToVisible( new Rectangle(
				new Point(0, (int)dtde.getLocation().getY() + getRowHeight()) ) );
		}
	}
	
}


/**
 * Returns a set of row numbers that are viewable
 * @param ArrayList [Integer]
 */
public java.util.ArrayList getViewableRowNumbers() 
{
	int rowHeight = getRowHeight();
	double startY = getVisibleRect().getMinY();
	double distanceY = getVisibleRect().getMaxY() - getVisibleRect().getMinY();

	int rowsPresent = (int)distanceY / rowHeight;
	java.util.ArrayList rows = new java.util.ArrayList(rowsPresent);

	for( int i = 0; i < rowsPresent; i++, startY += rowHeight )
	{
		java.awt.Point loc = new java.awt.Point(0, (int)startY );
		rows.add( new Integer(rowAtPoint( loc )) );
	}	

	// returns all the pointids in the ViewPort of the JTable
	return rows;
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

			addObject(userObject);

			dtde.getDropTargetContext().dropComplete( true );			
		}
		else if( tr.isDataFlavorSupported(TransferableObjects.OBJECT_FLAVOR) )
		{
			dtde.acceptDrop( DnDConstants.ACTION_MOVE );			
			Object obj = tr.getTransferData(TransferableObjects.OBJECT_FLAVOR);

			//insert our object here into the JList
			insertObjectAt(obj, rowAtPoint(dtde.getLocation()), lastDragOrigination );

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

public void setRendererWrapper(DAndDJTableRendererWrapper wrapper)
{
	rendererWrapper = wrapper;
	//MUST reinit in order for this wrapper renderer to take be picked up!
	initialize();
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
