package com.cannontech.tdc.utils;

/**
 * This is the event multicaster class to support the com.cannontech.tdc.utils.FontEditorPanelListenerEventMulticaster interface.
 */
public class FontEditorPanelListenerEventMulticaster extends java.awt.AWTEventMulticaster implements FontEditorPanelListener {
/**
 * Constructor to support multicast events.
 * @param a java.util.EventListener
 * @param b java.util.EventListener
 */
protected FontEditorPanelListenerEventMulticaster(java.util.EventListener a, java.util.EventListener b) {
	super(a, b);
}
/**
 * Add new listener to support multicast events.
 * @return com.cannontech.tdc.utils.FontEditorPanelListener
 * @param a com.cannontech.tdc.utils.FontEditorPanelListener
 * @param b com.cannontech.tdc.utils.FontEditorPanelListener
 */
public static com.cannontech.tdc.utils.FontEditorPanelListener add(com.cannontech.tdc.utils.FontEditorPanelListener a, com.cannontech.tdc.utils.FontEditorPanelListener b) {
	return (com.cannontech.tdc.utils.FontEditorPanelListener)addInternal(a, b);
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
	return new FontEditorPanelListenerEventMulticaster(a, b);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JButtonCancelAction_actionPerformed(java.util.EventObject newEvent) {
	((com.cannontech.tdc.utils.FontEditorPanelListener)a).JButtonCancelAction_actionPerformed(newEvent);
	((com.cannontech.tdc.utils.FontEditorPanelListener)b).JButtonCancelAction_actionPerformed(newEvent);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JButtonOkAction_actionPerformed(java.util.EventObject newEvent) {
	((com.cannontech.tdc.utils.FontEditorPanelListener)a).JButtonOkAction_actionPerformed(newEvent);
	((com.cannontech.tdc.utils.FontEditorPanelListener)b).JButtonOkAction_actionPerformed(newEvent);
}
/**
 * 
 * @return java.util.EventListener
 * @param oldl com.cannontech.tdc.utils.FontEditorPanelListener
 */
protected java.util.EventListener remove(com.cannontech.tdc.utils.FontEditorPanelListener oldl) {
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
 * @return com.cannontech.tdc.utils.FontEditorPanelListener
 * @param l com.cannontech.tdc.utils.FontEditorPanelListener
 * @param oldl com.cannontech.tdc.utils.FontEditorPanelListener
 */
public static com.cannontech.tdc.utils.FontEditorPanelListener remove(com.cannontech.tdc.utils.FontEditorPanelListener l, com.cannontech.tdc.utils.FontEditorPanelListener oldl) {
	if (l == oldl || l == null)
		return null;
	if(l instanceof FontEditorPanelListenerEventMulticaster)
		return (com.cannontech.tdc.utils.FontEditorPanelListener)((com.cannontech.tdc.utils.FontEditorPanelListenerEventMulticaster) l).remove(oldl);
	return l;
}
}
