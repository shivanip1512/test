package com.cannontech.cbc.capbankeditor;

/**
 * This is the event multicaster class to support the com.cannontech.cbc.capbankeditor.CapBankManualEntryPanelListenerEventMulticaster interface.
 */
public class CapBankManualEntryPanelListenerEventMulticaster extends java.awt.AWTEventMulticaster implements CapBankManualEntryPanelListener {
/**
 * Constructor to support multicast events.
 * @param a java.util.EventListener
 * @param b java.util.EventListener
 */
protected CapBankManualEntryPanelListenerEventMulticaster(java.util.EventListener a, java.util.EventListener b) {
	super(a, b);
}
/**
 * Add new listener to support multicast events.
 * @return com.cannontech.cbc.capbankeditor.CapBankManualEntryPanelListener
 * @param a com.cannontech.cbc.capbankeditor.CapBankManualEntryPanelListener
 * @param b com.cannontech.cbc.capbankeditor.CapBankManualEntryPanelListener
 */
public static com.cannontech.cbc.capbankeditor.CapBankManualEntryPanelListener add(com.cannontech.cbc.capbankeditor.CapBankManualEntryPanelListener a, com.cannontech.cbc.capbankeditor.CapBankManualEntryPanelListener b) {
	return (com.cannontech.cbc.capbankeditor.CapBankManualEntryPanelListener)addInternal(a, b);
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
	return new CapBankManualEntryPanelListenerEventMulticaster(a, b);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JButtonDismissAction_actionPerformed(java.util.EventObject newEvent) {
	((com.cannontech.cbc.capbankeditor.CapBankManualEntryPanelListener)a).JButtonDismissAction_actionPerformed(newEvent);
	((com.cannontech.cbc.capbankeditor.CapBankManualEntryPanelListener)b).JButtonDismissAction_actionPerformed(newEvent);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JButtonUpdateAction_actionPerformed(java.util.EventObject newEvent) {
	((com.cannontech.cbc.capbankeditor.CapBankManualEntryPanelListener)a).JButtonUpdateAction_actionPerformed(newEvent);
	((com.cannontech.cbc.capbankeditor.CapBankManualEntryPanelListener)b).JButtonUpdateAction_actionPerformed(newEvent);
}
/**
 * 
 * @return java.util.EventListener
 * @param oldl com.cannontech.cbc.capbankeditor.CapBankManualEntryPanelListener
 */
protected java.util.EventListener remove(com.cannontech.cbc.capbankeditor.CapBankManualEntryPanelListener oldl) {
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
 * @return com.cannontech.cbc.capbankeditor.CapBankManualEntryPanelListener
 * @param l com.cannontech.cbc.capbankeditor.CapBankManualEntryPanelListener
 * @param oldl com.cannontech.cbc.capbankeditor.CapBankManualEntryPanelListener
 */
public static com.cannontech.cbc.capbankeditor.CapBankManualEntryPanelListener remove(com.cannontech.cbc.capbankeditor.CapBankManualEntryPanelListener l, com.cannontech.cbc.capbankeditor.CapBankManualEntryPanelListener oldl) {
	if (l == oldl || l == null)
		return null;
	if(l instanceof CapBankManualEntryPanelListenerEventMulticaster)
		return (com.cannontech.cbc.capbankeditor.CapBankManualEntryPanelListener)((com.cannontech.cbc.capbankeditor.CapBankManualEntryPanelListenerEventMulticaster) l).remove(oldl);
	return l;
}
}
