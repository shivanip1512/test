package com.cannontech.tdc;

/**
 * This is the event multicaster class to support the com.cannontech.tdc.TDCMainPanelListenerEventMulticaster interface.
 */
public class TDCMainPanelListenerEventMulticaster extends java.awt.AWTEventMulticaster implements com.cannontech.tdc.TDCMainPanelListener {
/**
 * Constructor to support multicast events.
 * @param a java.util.EventListener
 * @param b java.util.EventListener
 */
protected TDCMainPanelListenerEventMulticaster(java.util.EventListener a, java.util.EventListener b) {
	super(a, b);
}
/**
 * Add new listener to support multicast events.
 * @return com.cannontech.tdc.TDCMainPanelListener
 * @param a com.cannontech.tdc.TDCMainPanelListener
 * @param b com.cannontech.tdc.TDCMainPanelListener
 */
public static com.cannontech.tdc.TDCMainPanelListener add(com.cannontech.tdc.TDCMainPanelListener a, com.cannontech.tdc.TDCMainPanelListener b) {
	return (com.cannontech.tdc.TDCMainPanelListener)addInternal(a, b);
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
	return new TDCMainPanelListenerEventMulticaster(a, b);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JComboCurrentDisplayAction_actionPerformed(java.util.EventObject newEvent) {
	((com.cannontech.tdc.TDCMainPanelListener)a).JComboCurrentDisplayAction_actionPerformed(newEvent);
	((com.cannontech.tdc.TDCMainPanelListener)b).JComboCurrentDisplayAction_actionPerformed(newEvent);
}
/**
 * 
 * @return java.util.EventListener
 * @param oldl com.cannontech.tdc.TDCMainPanelListener
 */
protected java.util.EventListener remove(com.cannontech.tdc.TDCMainPanelListener oldl) {
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
 * @return com.cannontech.tdc.TDCMainPanelListener
 * @param l com.cannontech.tdc.TDCMainPanelListener
 * @param oldl com.cannontech.tdc.TDCMainPanelListener
 */
public static com.cannontech.tdc.TDCMainPanelListener remove(com.cannontech.tdc.TDCMainPanelListener l, com.cannontech.tdc.TDCMainPanelListener oldl) {
	if (l == oldl || l == null)
		return null;
	if(l instanceof TDCMainPanelListenerEventMulticaster)
		return (com.cannontech.tdc.TDCMainPanelListener)((com.cannontech.tdc.TDCMainPanelListenerEventMulticaster) l).remove(oldl);
	return l;
}
}
