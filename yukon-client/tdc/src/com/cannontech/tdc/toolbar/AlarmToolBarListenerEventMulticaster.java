package com.cannontech.tdc.toolbar;

/**
 * This is the event multicaster class to support the com.cannontech.tdc.toolbar.AlarmToolBarListenerEventMulticaster interface.
 */
public class AlarmToolBarListenerEventMulticaster extends java.awt.AWTEventMulticaster implements AlarmToolBarListener {
/**
 * Constructor to support multicast events.
 * @param a java.util.EventListener
 * @param b java.util.EventListener
 */
protected AlarmToolBarListenerEventMulticaster(java.util.EventListener a, java.util.EventListener b) {
	super(a, b);
}
/**
 * Add new listener to support multicast events.
 * @return com.cannontech.tdc.toolbar.AlarmToolBarListener
 * @param a com.cannontech.tdc.toolbar.AlarmToolBarListener
 * @param b com.cannontech.tdc.toolbar.AlarmToolBarListener
 */
public static com.cannontech.tdc.toolbar.AlarmToolBarListener add(com.cannontech.tdc.toolbar.AlarmToolBarListener a, com.cannontech.tdc.toolbar.AlarmToolBarListener b) {
	return (com.cannontech.tdc.toolbar.AlarmToolBarListener)addInternal(a, b);
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
	return new AlarmToolBarListenerEventMulticaster(a, b);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JToolBarButtonAckAllAction_actionPerformed(java.util.EventObject newEvent) {
	((com.cannontech.tdc.toolbar.AlarmToolBarListener)a).JToolBarButtonAckAllAction_actionPerformed(newEvent);
	((com.cannontech.tdc.toolbar.AlarmToolBarListener)b).JToolBarButtonAckAllAction_actionPerformed(newEvent);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JToolBarButtonClearAction_actionPerformed(java.util.EventObject newEvent) {
	((com.cannontech.tdc.toolbar.AlarmToolBarListener)a).JToolBarButtonClearAction_actionPerformed(newEvent);
	((com.cannontech.tdc.toolbar.AlarmToolBarListener)b).JToolBarButtonClearAction_actionPerformed(newEvent);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JToolBarButtonClearAlarmAction_actionPerformed(java.util.EventObject newEvent) {
	((com.cannontech.tdc.toolbar.AlarmToolBarListener)a).JToolBarButtonClearAlarmAction_actionPerformed(newEvent);
	((com.cannontech.tdc.toolbar.AlarmToolBarListener)b).JToolBarButtonClearAlarmAction_actionPerformed(newEvent);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JToolBarButtonClearViewableAlarmsAction_actionPerformed(java.util.EventObject newEvent) {
	((com.cannontech.tdc.toolbar.AlarmToolBarListener)a).JToolBarButtonClearViewableAlarmsAction_actionPerformed(newEvent);
	((com.cannontech.tdc.toolbar.AlarmToolBarListener)b).JToolBarButtonClearViewableAlarmsAction_actionPerformed(newEvent);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JToolBarButtonRefreshAction_actionPerformed(java.util.EventObject newEvent) {
	((com.cannontech.tdc.toolbar.AlarmToolBarListener)a).JToolBarButtonRefreshAction_actionPerformed(newEvent);
	((com.cannontech.tdc.toolbar.AlarmToolBarListener)b).JToolBarButtonRefreshAction_actionPerformed(newEvent);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JToolBarButtonSilenceAlarmsAction_actionPerformed(java.util.EventObject newEvent) {
	((com.cannontech.tdc.toolbar.AlarmToolBarListener)a).JToolBarButtonSilenceAlarmsAction_actionPerformed(newEvent);
	((com.cannontech.tdc.toolbar.AlarmToolBarListener)b).JToolBarButtonSilenceAlarmsAction_actionPerformed(newEvent);
}
/**
 * 
 * @param newEvent com.klg.jclass.util.value.JCValueEvent
 */
public void JToolBarJCDateChange_actionPerformed(com.klg.jclass.util.value.JCValueEvent newEvent)
{
	((com.cannontech.tdc.toolbar.AlarmToolBarListener)a).JToolBarJCDateChange_actionPerformed(newEvent);
	((com.cannontech.tdc.toolbar.AlarmToolBarListener)b).JToolBarJCDateChange_actionPerformed(newEvent);
}
/**
 * 
 * @return java.util.EventListener
 * @param oldl com.cannontech.tdc.toolbar.AlarmToolBarListener
 */
protected java.util.EventListener remove(com.cannontech.tdc.toolbar.AlarmToolBarListener oldl) {
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
 * @return com.cannontech.tdc.toolbar.AlarmToolBarListener
 * @param l com.cannontech.tdc.toolbar.AlarmToolBarListener
 * @param oldl com.cannontech.tdc.toolbar.AlarmToolBarListener
 */
public static com.cannontech.tdc.toolbar.AlarmToolBarListener remove(com.cannontech.tdc.toolbar.AlarmToolBarListener l, com.cannontech.tdc.toolbar.AlarmToolBarListener oldl) {
	if (l == oldl || l == null)
		return null;
	if(l instanceof AlarmToolBarListenerEventMulticaster)
		return (com.cannontech.tdc.toolbar.AlarmToolBarListener)((com.cannontech.tdc.toolbar.AlarmToolBarListenerEventMulticaster) l).remove(oldl);
	return l;
}
}
