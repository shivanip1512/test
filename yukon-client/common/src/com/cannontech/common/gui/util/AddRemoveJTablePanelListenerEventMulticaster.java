package com.cannontech.common.gui.util;

/**
 * This is the event multicaster class to support the com.cannontech.common.gui.util.AddRemoveJTablePanelListenerEventMulticaster interface.
 */
public class AddRemoveJTablePanelListenerEventMulticaster extends java.awt.AWTEventMulticaster implements AddRemoveJTablePanelListener {
/**
 * Constructor to support multicast events.
 * @param a java.util.EventListener
 * @param b java.util.EventListener
 */
protected AddRemoveJTablePanelListenerEventMulticaster(java.util.EventListener a, java.util.EventListener b) {
	super(a, b);
}
/**
 * Add new listener to support multicast events.
 * @return com.cannontech.common.gui.util.AddRemoveJTablePanelListener
 * @param a com.cannontech.common.gui.util.AddRemoveJTablePanelListener
 * @param b com.cannontech.common.gui.util.AddRemoveJTablePanelListener
 */
public static com.cannontech.common.gui.util.AddRemoveJTablePanelListener add(com.cannontech.common.gui.util.AddRemoveJTablePanelListener a, com.cannontech.common.gui.util.AddRemoveJTablePanelListener b) {
	return (com.cannontech.common.gui.util.AddRemoveJTablePanelListener)addInternal(a, b);
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
	return new AddRemoveJTablePanelListenerEventMulticaster(a, b);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JButtonAddAction_actionPerformed(java.util.EventObject newEvent) {
	((com.cannontech.common.gui.util.AddRemoveJTablePanelListener)a).JButtonAddAction_actionPerformed(newEvent);
	((com.cannontech.common.gui.util.AddRemoveJTablePanelListener)b).JButtonAddAction_actionPerformed(newEvent);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JButtonRemoveAction_actionPerformed(java.util.EventObject newEvent) {
	((com.cannontech.common.gui.util.AddRemoveJTablePanelListener)a).JButtonRemoveAction_actionPerformed(newEvent);
	((com.cannontech.common.gui.util.AddRemoveJTablePanelListener)b).JButtonRemoveAction_actionPerformed(newEvent);
}
/**
 * 
 * @return java.util.EventListener
 * @param oldl com.cannontech.common.gui.util.AddRemoveJTablePanelListener
 */
protected java.util.EventListener remove(com.cannontech.common.gui.util.AddRemoveJTablePanelListener oldl) {
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
 * @return com.cannontech.common.gui.util.AddRemoveJTablePanelListener
 * @param l com.cannontech.common.gui.util.AddRemoveJTablePanelListener
 * @param oldl com.cannontech.common.gui.util.AddRemoveJTablePanelListener
 */
public static com.cannontech.common.gui.util.AddRemoveJTablePanelListener remove(com.cannontech.common.gui.util.AddRemoveJTablePanelListener l, com.cannontech.common.gui.util.AddRemoveJTablePanelListener oldl) {
	if (l == oldl || l == null)
		return null;
	if(l instanceof AddRemoveJTablePanelListenerEventMulticaster)
		return (com.cannontech.common.gui.util.AddRemoveJTablePanelListener)((com.cannontech.common.gui.util.AddRemoveJTablePanelListenerEventMulticaster) l).remove(oldl);
	return l;
}
}
