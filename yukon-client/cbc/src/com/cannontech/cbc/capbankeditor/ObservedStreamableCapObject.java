package com.cannontech.cbc.capbankeditor;

/**
 * Insert the type's description here.
 * Creation date: (12/14/00 4:41:31 PM)
 * @author: 
 */
import com.cannontech.cbc.data.StreamableCapObject;

public class ObservedStreamableCapObject 
{
	StreamableCapObject capObject = null;

	/**
	 * ObservedStreamableCapObject constructor comment.
	 */
	public ObservedStreamableCapObject(StreamableCapObject newCapObj_ ) 
	{
		capObject = newCapObj_;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (12/14/00 4:43:07 PM)
	 * @return StreamableCapObject
	 */
	public StreamableCapObject getCapObject() {
		return capObject;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (12/14/00 4:43:07 PM)
	 * @param StreamableCapObject
	 */
	public void setCapObject(StreamableCapObject newCapObj_ ) {
		capObject = newCapObj_;
	}
}
