package com.cannontech.cbc.capbankeditor;

/**
 * Insert the type's description here.
 * Creation date: (12/14/00 4:53:05 PM)
 * @author: 
 */
public class ObservableCapBankRow extends java.util.Observable 
{

	ObservedCapBankChanged newObject = null;
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
public synchronized void setChangedCapBank( com.cannontech.cbc.data.CapBankDevice capBank )
{
	newObject = new ObservedCapBankChanged( capBank );
	
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
public synchronized void setChangedCapBankVector( java.util.Vector capBanks )
{
	if( capBanks != null )
	{
		for( int i = 0; i < capBanks.size(); i++ )
		{
			newObject = new ObservedCapBankChanged( (com.cannontech.cbc.data.CapBankDevice)capBanks.elementAt(i) );
			
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
