package com.cannontech.tdc.observe;

/**
 * Insert the type's description here.
 * Creation date: (1/9/2002 10:48:06 AM)
 * @author: 
 */
public abstract class ObservableJPopupMenu extends javax.swing.JPopupMenu 
{
	private java.util.Observable observable = null;
/**
 * ObservableJPopupMenu constructor comment.
 */
public ObservableJPopupMenu() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (1/9/2002 10:54:50 AM)
 * @return java.util.Observable
 */
public java.util.Observable getObservable() {
	return observable;
}
/**
 * Insert the method's description here.
 * Creation date: (1/9/2002 10:54:50 AM)
 * @param newObservable java.util.Observable
 */
public void setObservable(java.util.Observable newObservable) {
	observable = newObservable;
}
}
