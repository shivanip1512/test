package com.cannontech.common.gui.dnd;

/**
 * Insert the type's description here.
 * Creation date: (3/7/00 2:09:37 PM)
 * @author: 
 */
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public class DAndDDevicePointTable extends DragAndDropTable implements java.awt.dnd.DropTargetListener, java.awt.dnd.DragGestureListener
{
	/**
	 * DragAndDropTree constructor comment.
	 */
	public DAndDDevicePointTable() 
	{
		super();
	}
	
	/**
	 * 
	 */
	public void addDragAndDropListener(DragAndDropListener newListener) 
	{
		dndEventMulticaster = DragAndDropListenerMulticaster.add(dndEventMulticaster, newListener);
		return;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (1/31/00 2:08:42 PM)
	 */
	private void addDevice(LiteYukonPAObject device)
	{
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		java.util.List devicePoints = cache.getAllPoints();
		
		for (int i=0; i<devicePoints.size(); i++)
		{
			if( device.getYukonID() == ((LitePoint)devicePoints.get(i)).getPaobjectID() )
				addPoint( (LitePoint)devicePoints.get(i) );
		}
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (1/31/00 2:08:42 PM)
	 */
	public void addDevice( LitePoint[] points )
	{
		if( points.length > 0 ) // only add devices that have points
		{	
			for( int i = 0; i < points.length; i++ )
			{		
				addPoint( points[i] );
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.cannontech.common.gui.dnd.DragAndDropTable#addObject(java.lang.Object)
	 */
	public void addObject( Object object_)
	{
		if( object_ instanceof LiteYukonPAObject )  // insert device
		{
			LiteYukonPAObject device = (LiteYukonPAObject)object_;
			addDevice( device );
		}
		else if(object_ instanceof LitePoint)  //insert point
		{
			addPoint( (LitePoint)object_ );
		}
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (1/31/00 2:08:42 PM)
	 * @param device java.lang.String
	 * @param pointid java.lang.String
	 * @param pointName java.lang.String
	 */
	public void addPoint( final LitePoint litePoint_ )
	{
		IDroppableTableModel tableModel = (IDroppableTableModel)this.getModel();
	
		// just return if the point is already in the right tree
		if( tableModel.objectExists( litePoint_ ) )
			return;
	
		tableModel.insertNewRow( litePoint_ ); 
	}
}
