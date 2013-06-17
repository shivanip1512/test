package com.cannontech.common.gui.dnd;

/**
 * Insert the type's description here.
 * Creation date: (3/7/00 2:09:37 PM)
 * @author: 
 */
import java.util.List;

import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.spring.YukonSpringHook;

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
        List<LitePoint> points = YukonSpringHook.getBean(PointDao.class).getLitePointsByPaObjectId(device.getYukonID());
        for (LitePoint point : points) {
            addPoint(point);
        }
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (1/31/00 2:08:42 PM)
	 */
	public void addDevice( List<LitePoint> points )
	{
        for (LitePoint point : points) {
            addPoint(point);
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
