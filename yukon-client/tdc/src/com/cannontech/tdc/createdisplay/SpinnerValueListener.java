package com.cannontech.tdc.createdisplay;

/**
 * Insert the type's description here.
 * Creation date: (1/27/00 9:23:00 AM)
 * @author: 
 */
public class SpinnerValueListener implements com.klg.jclass.util.value.JCValueListener 
{
	Object caller = null;
	
/**
 * SpinnerValueListener constructor comment.
 */
public SpinnerValueListener() {
	super();
}
/**
 * SpinnerValueListener constructor comment.
 */
public SpinnerValueListener( Object call ) {
	super();

	caller = call;
}
/**
 * valueChanged method comment.
 */
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
{
	//caller.getClass()  -- I wonder if this could work
	
	if ( caller instanceof CreateBottomPanel )
		((CreateBottomPanel)caller).jCSpinFieldWidth_ValueChange( arg1 );

}
/**
 * valueChanging method comment.
 */
public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) {}
}
