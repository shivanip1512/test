package com.cannontech.cbc.capbankeditor;

import com.cannontech.cbc.data.StreamableCapObject;

/**
 * Insert the type's description here.
 * Creation date: (12/14/00 4:53:05 PM)
 * @author: 
 */
public class ObservableCapBankRow extends java.util.Observable 
{

	ObservedStreamableCapObject newObject = null;

	/**
	 * ObservableCapBankRow constructor comment.
	 */
	public ObservableCapBankRow() {
		super();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (12/10/00 4:57:01 PM)
	 */
	public synchronized void setChangedCapBank( StreamableCapObject capObj_ )
	{
		newObject = new ObservedStreamableCapObject( capObj_ );
		
		// send a change ONLY if someone is listening
		if( this.countObservers() > 0 )
		{
			setChanged();
			notifyObservers( newObject );
		}
	
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (12/10/00 4:57:01 PM)
	 */
	public synchronized void setChangedCapBankVector( java.util.Vector capObjs_ )
	{
		if( capObjs_ != null )
		{
			for( int i = 0; i < capObjs_.size(); i++ )
			{
				newObject = new ObservedStreamableCapObject( 
										(StreamableCapObject)capObjs_.elementAt(i) );
				
				// send a change ONLY if someone is listening
				if( this.countObservers() > 0 )
				{
					setChanged();
					notifyObservers( newObject );
				}
			}
		}
	}
}
