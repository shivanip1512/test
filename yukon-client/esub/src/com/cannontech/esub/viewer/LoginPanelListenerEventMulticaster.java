package com.cannontech.esub.viewer;

/**
 * This is the event multicaster class to support the com.cannontech.esub.viewer.LoginPanelListenerEventMulticaster interface.
 */
public class LoginPanelListenerEventMulticaster extends java.awt.AWTEventMulticaster implements com.cannontech.esub.viewer.LoginPanelListener {
/**
 * Constructor to support multicast events.
 * @param a java.util.EventListener
 * @param b java.util.EventListener
 */
protected LoginPanelListenerEventMulticaster(java.util.EventListener a, java.util.EventListener b) {
	super(a, b);
}
/**
 * Add new listener to support multicast events.
 * @return com.cannontech.esub.viewer.LoginPanelListener
 * @param a com.cannontech.esub.viewer.LoginPanelListener
 * @param b com.cannontech.esub.viewer.LoginPanelListener
 */
public static com.cannontech.esub.viewer.LoginPanelListener add(com.cannontech.esub.viewer.LoginPanelListener a, com.cannontech.esub.viewer.LoginPanelListener b) {
	return (com.cannontech.esub.viewer.LoginPanelListener)addInternal(a, b);
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
	return new LoginPanelListenerEventMulticaster(a, b);
}
/**
 * 
 * @return java.util.EventListener
 * @param oldl com.cannontech.esub.viewer.LoginPanelListener
 */
protected java.util.EventListener remove(com.cannontech.esub.viewer.LoginPanelListener oldl) {
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
 * @return com.cannontech.esub.viewer.LoginPanelListener
 * @param l com.cannontech.esub.viewer.LoginPanelListener
 * @param oldl com.cannontech.esub.viewer.LoginPanelListener
 */
public static com.cannontech.esub.viewer.LoginPanelListener remove(com.cannontech.esub.viewer.LoginPanelListener l, com.cannontech.esub.viewer.LoginPanelListener oldl) {
	if (l == oldl || l == null)
		return null;
	if(l instanceof LoginPanelListenerEventMulticaster)
		return (com.cannontech.esub.viewer.LoginPanelListener)((com.cannontech.esub.viewer.LoginPanelListenerEventMulticaster) l).remove(oldl);
	return l;
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void submitButtonAction_actionPerformed(java.util.EventObject newEvent) {
	((com.cannontech.esub.viewer.LoginPanelListener)a).submitButtonAction_actionPerformed(newEvent);
	((com.cannontech.esub.viewer.LoginPanelListener)b).submitButtonAction_actionPerformed(newEvent);
}
}
