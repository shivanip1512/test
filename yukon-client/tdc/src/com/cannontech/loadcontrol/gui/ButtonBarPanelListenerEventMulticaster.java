package com.cannontech.loadcontrol.gui;

/**
 * This is the event multicaster class to support the com.cannontech.loadcontrol.gui.ButtonBarPanelListenerEventMulticaster interface.
 */
public class ButtonBarPanelListenerEventMulticaster extends java.awt.AWTEventMulticaster implements ButtonBarPanelListener {
/**
 * Constructor to support multicast events.
 * @param a java.util.EventListener
 * @param b java.util.EventListener
 */
protected ButtonBarPanelListenerEventMulticaster(java.util.EventListener a, java.util.EventListener b) {
	super(a, b);
}
/**
 * Add new listener to support multicast events.
 * @return com.cannontech.loadcontrol.gui.ButtonBarPanelListener
 * @param a com.cannontech.loadcontrol.gui.ButtonBarPanelListener
 * @param b com.cannontech.loadcontrol.gui.ButtonBarPanelListener
 */
public static com.cannontech.loadcontrol.gui.ButtonBarPanelListener add(com.cannontech.loadcontrol.gui.ButtonBarPanelListener a, com.cannontech.loadcontrol.gui.ButtonBarPanelListener b) {
	return (com.cannontech.loadcontrol.gui.ButtonBarPanelListener)addInternal(a, b);
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
	return new ButtonBarPanelListenerEventMulticaster(a, b);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JButtonDisableAllAction_actionPerformed(java.util.EventObject newEvent) {
	((com.cannontech.loadcontrol.gui.ButtonBarPanelListener)a).JButtonDisableAllAction_actionPerformed(newEvent);
	((com.cannontech.loadcontrol.gui.ButtonBarPanelListener)b).JButtonDisableAllAction_actionPerformed(newEvent);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JButtonEnableAllAction_actionPerformed(java.util.EventObject newEvent) {
	((com.cannontech.loadcontrol.gui.ButtonBarPanelListener)a).JButtonEnableAllAction_actionPerformed(newEvent);
	((com.cannontech.loadcontrol.gui.ButtonBarPanelListener)b).JButtonEnableAllAction_actionPerformed(newEvent);
}

/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JButtonStartScenarioAction_actionPerformed(java.util.EventObject newEvent) {
	((com.cannontech.loadcontrol.gui.ButtonBarPanelListener)a).JButtonStartScenarioAction_actionPerformed(newEvent);
	((com.cannontech.loadcontrol.gui.ButtonBarPanelListener)b).JButtonStartScenarioAction_actionPerformed(newEvent);
}

/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JButtonStopScenarioAction_actionPerformed(java.util.EventObject newEvent) {
	((com.cannontech.loadcontrol.gui.ButtonBarPanelListener)a).JButtonStopScenarioAction_actionPerformed(newEvent);
	((com.cannontech.loadcontrol.gui.ButtonBarPanelListener)b).JButtonStopScenarioAction_actionPerformed(newEvent);
}

/**
 * 
 * @return java.util.EventListener
 * @param oldl com.cannontech.loadcontrol.gui.ButtonBarPanelListener
 */
protected java.util.EventListener remove(com.cannontech.loadcontrol.gui.ButtonBarPanelListener oldl) {
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
 * @return com.cannontech.loadcontrol.gui.ButtonBarPanelListener
 * @param l com.cannontech.loadcontrol.gui.ButtonBarPanelListener
 * @param oldl com.cannontech.loadcontrol.gui.ButtonBarPanelListener
 */
public static com.cannontech.loadcontrol.gui.ButtonBarPanelListener remove(com.cannontech.loadcontrol.gui.ButtonBarPanelListener l, com.cannontech.loadcontrol.gui.ButtonBarPanelListener oldl) {
	if (l == oldl || l == null)
		return null;
	if(l instanceof ButtonBarPanelListenerEventMulticaster)
		return (com.cannontech.loadcontrol.gui.ButtonBarPanelListener)((com.cannontech.loadcontrol.gui.ButtonBarPanelListenerEventMulticaster) l).remove(oldl);
	return l;
}
}
