package com.cannontech.common.gui.dnd;

/**
 * Insert the type's description here.
 * Creation date: (3/7/00 2:09:37 PM)
 * @author: 
 */
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;

public class DragAndDropJlist extends javax.swing.JList implements java.awt.dnd.DropTargetListener, java.awt.dnd.DragGestureListener
{
	//droppable components
	private DropTarget dropTarget;
	private DropTarget viewDropTarget;


	//draggable components
	private DragSource dragSource = DragSource.getDefaultDragSource();
	private final static DragSourceListener DRAG_SOURCE_LISTENER =
			new DragSourceListenerClass();

	private DAndDJListRendererWrapper rendererWrapper;
	private int lastDragOrigination = -1;

	protected transient DragAndDropListener dndEventMulticaster = null;
	
/**
 * DragAndDropTree constructor comment.
 */
public DragAndDropJlist() 
{
	super();
	initialize();	
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
 * @param obj Object
 * @param row int
 */
private synchronized void addObject( final Object obj, int newRow, int origRow )
{

	if( getModel() != null && newRow != -1 )
	{
		//lock down the data model
		synchronized( getModel() )
		{
			java.util.Vector values = new java.util.Vector(getModel().getSize());

			for( int i = 0; i < getModel().getSize(); i++ )
				values.add( getModel().getElementAt(i) );

			//there is probably a more efficient way to do this, but this is so easy!
			//NOTE: There may be 2 instances of the same object in our JList!!
			values.remove( origRow );
			values.insertElementAt( obj, newRow );

			setListData( values );
			repaint();
		}
	}
	
}
/**
 * dragEnter method comment.
 */
public void dragEnter(java.awt.dnd.DropTargetDragEvent dtde) 
{
	dtde.acceptDrag( DnDConstants.ACTION_MOVE );
}
/**
 * dragExit method comment.
 */
public void dragExit(java.awt.dnd.DropTargetEvent dte) 
{
	updateDragBorder( - 1 );
}
/**
 * AddPointsTree constructor comment.
 * @param value java.util.Vector
 */
public void dragGestureRecognized(java.awt.dnd.DragGestureEvent dge) 
{
	int row = locationToIndex( dge.getDragOrigin() );

	if( row >= 0 )
	{
		lastDragOrigination = row;
		Object o = getModel().getElementAt(row);

		//create a String flavor
		TransferableObjects trans = new TransferableObjects(o);

		dragSource.startDrag( dge, 
				DragSource.DefaultMoveDrop, 
				trans, 
				DRAG_SOURCE_LISTENER );
	}

}
/**
 * dragOver method comment.
 */
public void dragOver(java.awt.dnd.DropTargetDragEvent dtde) 
{
	updateDragBorder( locationToIndex(dtde.getLocation()) );
}
/**
 * drop method comment.
 */
public synchronized void drop(java.awt.dnd.DropTargetDropEvent dtde) 
{
	try
	{
		java.awt.datatransfer.Transferable tr = dtde.getTransferable();

		if( tr.isDataFlavorSupported(TransferableObjects.OBJECT_FLAVOR) )
		{
			dtde.acceptDrop( DnDConstants.ACTION_MOVE );
			Object userObject = tr.getTransferData(TransferableObjects.OBJECT_FLAVOR);

			//insert our object here into the JList
			addObject( userObject, locationToIndex(dtde.getLocation()), lastDragOrigination );
			
			dtde.getDropTargetContext().dropComplete( true );

			//tell any listeners that a drop occured
			fireDrop_actionPerformed(new java.util.EventObject(this));
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

	updateDragBorder( -1 );
}
/**
 * dropActionChanged method comment.
 */
public void dropActionChanged(java.awt.dnd.DropTargetDragEvent dtde) {}
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
 * Creation date: (2/12/2002 5:55:07 PM)
 */
private void initialize() 
{
	dropTarget = new DropTarget( this, DnDConstants.ACTION_MOVE, this, true );

	dragSource.createDefaultDragGestureRecognizer( 
				this, 
				DnDConstants.ACTION_MOVE, 
				this );
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
 * Insert the method's description here.
 * Creation date: (2/13/2002 1:18:49 PM)
 * @return javax.swing.ListCellRenderer
 */
public void setCellRenderer( javax.swing.ListCellRenderer re )
{
	if( rendererWrapper == null )
		rendererWrapper = new DAndDJListRendererWrapper();

	rendererWrapper.setRealRenderer( re );
	super.setCellRenderer( rendererWrapper );
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/00 3:33:57 PM)
 * @param num long
 */
//comp should be the viewport this JList is in (Such as a JScrollPane)
public void setViewDropTarget(javax.swing.JComponent comp) 
{
	viewDropTarget = new DropTarget(comp, java.awt.dnd.DnDConstants.ACTION_MOVE, this, true);
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
