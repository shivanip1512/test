package com.cannontech.tdc;

/**
 * This is the event multicaster class to support the com.cannontech.tdc.TDCMainFrameListenerEventMulticaster interface.
 */
public class TDCMainFrameListenerEventMulticaster extends java.awt.AWTEventMulticaster implements com.cannontech.tdc.TDCMainFrameListener {
/**
 * Constructor to support multicast events.
 * @param a java.util.EventListener
 * @param b java.util.EventListener
 */
protected TDCMainFrameListenerEventMulticaster(java.util.EventListener a, java.util.EventListener b) {
	super(a, b);
}
/**
 * Add new listener to support multicast events.
 * @return com.cannontech.tdc.TDCMainFrameListener
 * @param a com.cannontech.tdc.TDCMainFrameListener
 * @param b com.cannontech.tdc.TDCMainFrameListener
 */
public static com.cannontech.tdc.TDCMainFrameListener add(com.cannontech.tdc.TDCMainFrameListener a, com.cannontech.tdc.TDCMainFrameListener b) {
	return (com.cannontech.tdc.TDCMainFrameListener)addInternal(a, b);
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
	return new TDCMainFrameListenerEventMulticaster(a, b);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void alarmToolBarAlarmToolBar_JToolBarButtonClearAction_actionPerformed(java.util.EventObject newEvent) {
	((com.cannontech.tdc.TDCMainFrameListener)a).alarmToolBarAlarmToolBar_JToolBarButtonClearAction_actionPerformed(newEvent);
	((com.cannontech.tdc.TDCMainFrameListener)b).alarmToolBarAlarmToolBar_JToolBarButtonClearAction_actionPerformed(newEvent);
}
/**
 * 
 * @return java.util.EventListener
 * @param oldl com.cannontech.tdc.TDCMainFrameListener
 */
protected java.util.EventListener remove(com.cannontech.tdc.TDCMainFrameListener oldl) {
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
 * @return com.cannontech.tdc.TDCMainFrameListener
 * @param l com.cannontech.tdc.TDCMainFrameListener
 * @param oldl com.cannontech.tdc.TDCMainFrameListener
 */
public static com.cannontech.tdc.TDCMainFrameListener remove(com.cannontech.tdc.TDCMainFrameListener l, com.cannontech.tdc.TDCMainFrameListener oldl) {
	if (l == oldl || l == null)
		return null;
	if(l instanceof TDCMainFrameListenerEventMulticaster)
		return (com.cannontech.tdc.TDCMainFrameListener)((com.cannontech.tdc.TDCMainFrameListenerEventMulticaster) l).remove(oldl);
	return l;
}
}
