package com.cannontech.common.gui.util;

/**
 * This is the event multicaster class to support the com.cannontech.tdc.utils.OkCancelPanelListenerEventMulticaster interface.
 */
public class OkCancelPanelListenerEventMulticaster extends java.awt.AWTEventMulticaster implements OkCancelPanelListener {
/**
 * Constructor to support multicast events.
 * @param a java.util.EventListener
 * @param b java.util.EventListener
 */
protected OkCancelPanelListenerEventMulticaster(java.util.EventListener a, java.util.EventListener b) {
	super(a, b);
}
/**
 * Add new listener to support multicast events.
 * @return OkCancelPanelListener
 * @param a OkCancelPanelListener
 * @param b OkCancelPanelListener
 */
public static OkCancelPanelListener add(OkCancelPanelListener a, OkCancelPanelListener b) {
	return (OkCancelPanelListener)addInternal(a, b);
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
	return new OkCancelPanelListenerEventMulticaster(a, b);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JButtonCancelAction_actionPerformed(java.util.EventObject newEvent) {
	((OkCancelPanelListener)a).JButtonCancelAction_actionPerformed(newEvent);
	((OkCancelPanelListener)b).JButtonCancelAction_actionPerformed(newEvent);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JButtonOkAction_actionPerformed(java.util.EventObject newEvent) {
	((OkCancelPanelListener)a).JButtonOkAction_actionPerformed(newEvent);
	((OkCancelPanelListener)b).JButtonOkAction_actionPerformed(newEvent);
}
/**
 * 
 * @return java.util.EventListener
 * @param oldl OkCancelPanelListener
 */
protected java.util.EventListener remove(OkCancelPanelListener oldl) {
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
 * @return OkCancelPanelListener
 * @param l OkCancelPanelListener
 * @param oldl OkCancelPanelListener
 */
public static OkCancelPanelListener remove(OkCancelPanelListener l, OkCancelPanelListener oldl) {
	if (l == oldl || l == null)
		return null;
	if(l instanceof OkCancelPanelListenerEventMulticaster)
		return (OkCancelPanelListener)((OkCancelPanelListenerEventMulticaster) l).remove(oldl);
	return l;
}
}
