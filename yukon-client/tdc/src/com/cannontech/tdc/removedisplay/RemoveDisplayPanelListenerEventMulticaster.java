package com.cannontech.tdc.removedisplay;

/**
 * This is the event multicaster class to support the com.cannontech.tdc.removedisplay.RemoveDisplayPanelListenerEventMulticaster interface.
 */
public class RemoveDisplayPanelListenerEventMulticaster extends java.awt.AWTEventMulticaster implements RemoveDisplayPanelListener {
/**
 * Constructor to support multicast events.
 * @param a java.util.EventListener
 * @param b java.util.EventListener
 */
protected RemoveDisplayPanelListenerEventMulticaster(java.util.EventListener a, java.util.EventListener b) {
	super(a, b);
}
/**
 * Add new listener to support multicast events.
 * @return com.cannontech.tdc.removedisplay.RemoveDisplayPanelListener
 * @param a com.cannontech.tdc.removedisplay.RemoveDisplayPanelListener
 * @param b com.cannontech.tdc.removedisplay.RemoveDisplayPanelListener
 */
public static com.cannontech.tdc.removedisplay.RemoveDisplayPanelListener add(com.cannontech.tdc.removedisplay.RemoveDisplayPanelListener a, com.cannontech.tdc.removedisplay.RemoveDisplayPanelListener b) {
	return (com.cannontech.tdc.removedisplay.RemoveDisplayPanelListener)addInternal(a, b);
}
/**
 * Add new listener to support multicast events.
 * @return java.util.EventListener
 * @param a java.util.EventListener
 * @param b java.util.EventListener
 */
protected static java.util.EventListener addInternal(java.util.EventListener a, java.util.EventListener b) {
	if (a == null)  return b;
	if (b == null)  return a;
	return new RemoveDisplayPanelListenerEventMulticaster(a, b);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JButtonCancelAction_actionPerformed(java.util.EventObject newEvent) {
	((com.cannontech.tdc.removedisplay.RemoveDisplayPanelListener)a).JButtonCancelAction_actionPerformed(newEvent);
	((com.cannontech.tdc.removedisplay.RemoveDisplayPanelListener)b).JButtonCancelAction_actionPerformed(newEvent);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JButtonDeleteAction_actionPerformed(java.util.EventObject newEvent) {
	((com.cannontech.tdc.removedisplay.RemoveDisplayPanelListener)a).JButtonDeleteAction_actionPerformed(newEvent);
	((com.cannontech.tdc.removedisplay.RemoveDisplayPanelListener)b).JButtonDeleteAction_actionPerformed(newEvent);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JButtonOkAction_actionPerformed(java.util.EventObject newEvent) {
	((com.cannontech.tdc.removedisplay.RemoveDisplayPanelListener)a).JButtonOkAction_actionPerformed(newEvent);
	((com.cannontech.tdc.removedisplay.RemoveDisplayPanelListener)b).JButtonOkAction_actionPerformed(newEvent);
}
/**
 * 
 * @return java.util.EventListener
 * @param oldl com.cannontech.tdc.removedisplay.RemoveDisplayPanelListener
 */
protected java.util.EventListener remove(com.cannontech.tdc.removedisplay.RemoveDisplayPanelListener oldl) {
	if (oldl == a)  return b;
	if (oldl == b)  return a;
	java.util.EventListener a2 = removeInternal(a, oldl);
	java.util.EventListener b2 = removeInternal(b, oldl);
	if (a2 == a && b2 == b)
		return this;
	return addInternal(a2, b2);
}
/**
 * Remove listener to support multicast events.
 * @return com.cannontech.tdc.removedisplay.RemoveDisplayPanelListener
 * @param l com.cannontech.tdc.removedisplay.RemoveDisplayPanelListener
 * @param oldl com.cannontech.tdc.removedisplay.RemoveDisplayPanelListener
 */
public static com.cannontech.tdc.removedisplay.RemoveDisplayPanelListener remove(com.cannontech.tdc.removedisplay.RemoveDisplayPanelListener l, com.cannontech.tdc.removedisplay.RemoveDisplayPanelListener oldl) {
	if (l == oldl || l == null)
		return null;
	if(l instanceof RemoveDisplayPanelListenerEventMulticaster)
		return (com.cannontech.tdc.removedisplay.RemoveDisplayPanelListener)((com.cannontech.tdc.removedisplay.RemoveDisplayPanelListenerEventMulticaster) l).remove(oldl);
	return l;
}
}
