package com.cannontech.common.gui.util;

/**
 * This is the event multicaster class to support the com.cannontech.common.gui.util.OkPanelListenerEventMulticaster interface.
 */
public class OkPanelListenerEventMulticaster extends java.awt.AWTEventMulticaster implements com.cannontech.common.gui.util.OkPanelListener {
/**
 * Constructor to support multicast events.
 * @param a java.util.EventListener
 * @param b java.util.EventListener
 */
protected OkPanelListenerEventMulticaster(java.util.EventListener a, java.util.EventListener b) {
	super(a, b);
}
/**
 * Add new listener to support multicast events.
 * @return com.cannontech.common.gui.util.OkPanelListener
 * @param a com.cannontech.common.gui.util.OkPanelListener
 * @param b com.cannontech.common.gui.util.OkPanelListener
 */
public static com.cannontech.common.gui.util.OkPanelListener add(com.cannontech.common.gui.util.OkPanelListener a, com.cannontech.common.gui.util.OkPanelListener b) {
	return (com.cannontech.common.gui.util.OkPanelListener)addInternal(a, b);
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
	return new OkPanelListenerEventMulticaster(a, b);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JButtonOkAction_actionPerformed(java.util.EventObject newEvent) {
	((com.cannontech.common.gui.util.OkPanelListener)a).JButtonOkAction_actionPerformed(newEvent);
	((com.cannontech.common.gui.util.OkPanelListener)b).JButtonOkAction_actionPerformed(newEvent);
}
/**
 * 
 * @return java.util.EventListener
 * @param oldl com.cannontech.common.gui.util.OkPanelListener
 */
protected java.util.EventListener remove(com.cannontech.common.gui.util.OkPanelListener oldl) {
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
 * @return com.cannontech.common.gui.util.OkPanelListener
 * @param l com.cannontech.common.gui.util.OkPanelListener
 * @param oldl com.cannontech.common.gui.util.OkPanelListener
 */
public static com.cannontech.common.gui.util.OkPanelListener remove(com.cannontech.common.gui.util.OkPanelListener l, com.cannontech.common.gui.util.OkPanelListener oldl) {
	if (l == oldl || l == null)
		return null;
	if(l instanceof OkPanelListenerEventMulticaster)
		return (com.cannontech.common.gui.util.OkPanelListener)((com.cannontech.common.gui.util.OkPanelListenerEventMulticaster) l).remove(oldl);
	return l;
}
}
