package com.cannontech.loadcontrol.eexchange.gui;

/**
 * This is the event multicaster class to support the com.cannontech.loadcontrol.eexchange.gui.EExchangeButtonPanelListenerEventMulticaster interface.
 */
public class EExchangeButtonPanelListenerEventMulticaster extends java.awt.AWTEventMulticaster implements EExchangeButtonPanelListener {
/**
 * Constructor to support multicast events.
 * @param a java.util.EventListener
 * @param b java.util.EventListener
 */
protected EExchangeButtonPanelListenerEventMulticaster(java.util.EventListener a, java.util.EventListener b) {
	super(a, b);
}
/**
 * Add new listener to support multicast events.
 * @return com.cannontech.loadcontrol.eexchange.gui.EExchangeButtonPanelListener
 * @param a com.cannontech.loadcontrol.eexchange.gui.EExchangeButtonPanelListener
 * @param b com.cannontech.loadcontrol.eexchange.gui.EExchangeButtonPanelListener
 */
public static com.cannontech.loadcontrol.eexchange.gui.EExchangeButtonPanelListener add(com.cannontech.loadcontrol.eexchange.gui.EExchangeButtonPanelListener a, com.cannontech.loadcontrol.eexchange.gui.EExchangeButtonPanelListener b) {
	return (com.cannontech.loadcontrol.eexchange.gui.EExchangeButtonPanelListener)addInternal(a, b);
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
	return new EExchangeButtonPanelListenerEventMulticaster(a, b);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JButtonCreateOfferAction_actionPerformed(java.util.EventObject newEvent) {
	((com.cannontech.loadcontrol.eexchange.gui.EExchangeButtonPanelListener)a).JButtonCreateOfferAction_actionPerformed(newEvent);
	((com.cannontech.loadcontrol.eexchange.gui.EExchangeButtonPanelListener)b).JButtonCreateOfferAction_actionPerformed(newEvent);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JButtonCreateRevisionAction_actionPerformed(java.util.EventObject newEvent) {
	((com.cannontech.loadcontrol.eexchange.gui.EExchangeButtonPanelListener)a).JButtonCreateRevisionAction_actionPerformed(newEvent);
	((com.cannontech.loadcontrol.eexchange.gui.EExchangeButtonPanelListener)b).JButtonCreateRevisionAction_actionPerformed(newEvent);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JButtonShowOffersCustomersAction_actionPerformed(java.util.EventObject newEvent) {
	((com.cannontech.loadcontrol.eexchange.gui.EExchangeButtonPanelListener)a).JButtonShowOffersCustomersAction_actionPerformed(newEvent);
	((com.cannontech.loadcontrol.eexchange.gui.EExchangeButtonPanelListener)b).JButtonShowOffersCustomersAction_actionPerformed(newEvent);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JButtonViewRevisionsAction_actionPerformed(java.util.EventObject newEvent) {
	((com.cannontech.loadcontrol.eexchange.gui.EExchangeButtonPanelListener)a).JButtonViewRevisionsAction_actionPerformed(newEvent);
	((com.cannontech.loadcontrol.eexchange.gui.EExchangeButtonPanelListener)b).JButtonViewRevisionsAction_actionPerformed(newEvent);
}
/**
 * 
 * @return java.util.EventListener
 * @param oldl com.cannontech.loadcontrol.eexchange.gui.EExchangeButtonPanelListener
 */
protected java.util.EventListener remove(com.cannontech.loadcontrol.eexchange.gui.EExchangeButtonPanelListener oldl) {
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
 * @return com.cannontech.loadcontrol.eexchange.gui.EExchangeButtonPanelListener
 * @param l com.cannontech.loadcontrol.eexchange.gui.EExchangeButtonPanelListener
 * @param oldl com.cannontech.loadcontrol.eexchange.gui.EExchangeButtonPanelListener
 */
public static com.cannontech.loadcontrol.eexchange.gui.EExchangeButtonPanelListener remove(com.cannontech.loadcontrol.eexchange.gui.EExchangeButtonPanelListener l, com.cannontech.loadcontrol.eexchange.gui.EExchangeButtonPanelListener oldl) {
	if (l == oldl || l == null)
		return null;
	if(l instanceof EExchangeButtonPanelListenerEventMulticaster)
		return (com.cannontech.loadcontrol.eexchange.gui.EExchangeButtonPanelListener)((com.cannontech.loadcontrol.eexchange.gui.EExchangeButtonPanelListenerEventMulticaster) l).remove(oldl);
	return l;
}
}
