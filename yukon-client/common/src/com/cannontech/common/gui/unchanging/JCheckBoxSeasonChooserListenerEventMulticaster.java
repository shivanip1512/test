package com.cannontech.common.gui.unchanging;

/**
 * This is the event multicaster class to support the com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooserListenerEventMulticaster interface.
 */
public class JCheckBoxSeasonChooserListenerEventMulticaster extends java.awt.AWTEventMulticaster implements JCheckBoxSeasonChooserListener {
/**
 * Constructor to support multicast events.
 * @param a java.util.EventListener
 * @param b java.util.EventListener
 */
protected JCheckBoxSeasonChooserListenerEventMulticaster(java.util.EventListener a, java.util.EventListener b) {
	super(a, b);
}
/**
 * Add new listener to support multicast events.
 * @return com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooserListener
 * @param a com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooserListener
 * @param b com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooserListener
 */
public static com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooserListener add(com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooserListener a, com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooserListener b) {
	return (com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooserListener)addInternal(a, b);
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
	return new JCheckBoxSeasonChooserListenerEventMulticaster(a, b);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JCheckBoxFallAction_actionPerformed(java.util.EventObject newEvent) {
	((com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooserListener)a).JCheckBoxFallAction_actionPerformed(newEvent);
	((com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooserListener)b).JCheckBoxFallAction_actionPerformed(newEvent);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JCheckBoxSpringAction_actionPerformed(java.util.EventObject newEvent) {
	((com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooserListener)a).JCheckBoxSpringAction_actionPerformed(newEvent);
	((com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooserListener)b).JCheckBoxSpringAction_actionPerformed(newEvent);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JCheckBoxSummerAction_actionPerformed(java.util.EventObject newEvent) {
	((com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooserListener)a).JCheckBoxSummerAction_actionPerformed(newEvent);
	((com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooserListener)b).JCheckBoxSummerAction_actionPerformed(newEvent);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JCheckBoxWinterAction_actionPerformed(java.util.EventObject newEvent) {
	((com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooserListener)a).JCheckBoxWinterAction_actionPerformed(newEvent);
	((com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooserListener)b).JCheckBoxWinterAction_actionPerformed(newEvent);
}
/**
 * 
 * @return java.util.EventListener
 * @param oldl com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooserListener
 */
protected java.util.EventListener remove(com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooserListener oldl) {
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
 * @return com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooserListener
 * @param l com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooserListener
 * @param oldl com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooserListener
 */
public static com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooserListener remove(com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooserListener l, com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooserListener oldl) {
	if (l == oldl || l == null)
		return null;
	if(l instanceof JCheckBoxSeasonChooserListenerEventMulticaster)
		return (com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooserListener)((com.cannontech.common.gui.unchanging.JCheckBoxSeasonChooserListenerEventMulticaster) l).remove(oldl);
	return l;
}
}
