package com.cannontech.tdc;

/**
 * Insert the type's description here.
 * Creation date: (3/10/00 4:53:26 PM)
 * @author: 
 */
import java.util.Vector;
import com.cannontech.tdc.roweditor.ObservedPointDataChange;
import com.cannontech.message.dispatch.message.Signal;

public class ObservableRow extends java.util.Observable 
{
	private Vector row = null;
	private ObservedPointDataChange newObject = null;
	private Signal signal = null;
	private int tableLocation = -1;
/**
 * ObservableVector constructor comment.
 */
public ObservableRow() {
	super();
}
/**
 * ObservableVector constructor comment.
 */
public ObservableRow( Vector v, int rowLocation ) 
{
	super();
	row = v;
	tableLocation = rowLocation;
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/00 4:38:36 PM)
 * @return com.cannontech.message.dispatch.message.Signal
 */
public com.cannontech.message.dispatch.message.Signal getSignal() 
{
	return signal;
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/00 5:06:31 PM)
 * @return int
 */
public int getTableLocation() {
	return tableLocation;
}
/**
 * Insert the method's description here.
 * Creation date: (3/29/00 3:03:43 PM)
 * Version: <version>
 * @return java.util.Vector
 */
public Vector getVector() {
	return row;
}
/**
 * Insert the method's description here.
 * Creation date: (3/10/00 4:57:01 PM)
 * @return java.util.Vector
 */
public synchronized void setElementAt( Object o, int i ) 
{
	if( this.countObservers() > 0 )
	{
		row.setElementAt( o, i );
		setChanged();
		notifyObservers( o );
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/10/00 4:57:01 PM)
 * @return java.util.Vector
 */
public synchronized void setElementAt( Object o, int i, int type, long ptID, boolean isAlarmed )
{
	newObject = new ObservedPointDataChange( o.toString(), type, ptID, isAlarmed );
	
	row.setElementAt( o, i );

	// send a change ONLY if someone is listening
	if( this.countObservers() > 0 )
	{
		setChanged();
		notifyObservers( newObject );
	}

}
/**
 * ObservableVector constructor comment.
 */
public void setRow( Vector v ) 
{
	row = v;
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/00 4:38:36 PM)
 * @param newSignal com.cannontech.message.dispatch.message.Signal
 */
public void setSignal(com.cannontech.message.dispatch.message.Signal newSignal) 
{
	signal = newSignal;
	
	// send a change ONLY if someone is listening
	if( this.countObservers() > 0 )
	{
		setChanged();
		notifyObservers( signal );
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/00 5:06:31 PM)
 * @param newTableLocation int
 */
void setTableLocation(int newTableLocation) {
	tableLocation = newTableLocation;
}
}
