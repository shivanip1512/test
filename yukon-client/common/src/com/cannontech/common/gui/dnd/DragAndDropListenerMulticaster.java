package com.cannontech.common.gui.dnd;

/**
 * Insert the type's description here.
 * Creation date: (2/14/2002 10:56:44 AM)
 * @author: 
 */
public class DragAndDropListenerMulticaster extends java.awt.AWTEventMulticaster implements DragAndDropListener {
/**
 * DragAndDropListenerMulticaster constructor comment.
 * @param a java.util.EventListener
 * @param b java.util.EventListener
 */
protected DragAndDropListenerMulticaster(java.util.EventListener a, java.util.EventListener b) {
	super(a, b);
}
/**
 * Add new listener to support multicast events.
 */
public static DragAndDropListener add(DragAndDropListener a, DragAndDropListener b) 
{
	if (a == null)
		return b;

	if (b == null)
		return a;

	return new DragAndDropListenerMulticaster(a, b);
}
/**
 * Insert the method's description here.
 * Creation date: (2/14/2002 10:56:44 AM)
 * @param newEvent java.util.EventObject
 */
public void drop_actionPerformed(java.util.EventObject newEvent) {}
/**
 * Remove listener to support multicast events.
 */
public static DragAndDropListener remove(DragAndDropListener a, DragAndDropListener b) 
{
	return (DragAndDropListener)removeInternal(a, b);
}
}
