package com.cannontech.common.gui.util;

/**
 * This is the event multicaster class to support the com.cannontech.common.gui.util.ColorChoiceListenerEventMulticaster interface.
 */
public class ColorChoiceListenerEventMulticaster extends java.awt.AWTEventMulticaster implements com.cannontech.common.gui.util.ColorChoiceListener {
/**
 * Constructor to support multicast events.
 * @param a com.cannontech.common.gui.util.ColorChoiceListener
 * @param b com.cannontech.common.gui.util.ColorChoiceListener
 */
protected ColorChoiceListenerEventMulticaster(com.cannontech.common.gui.util.ColorChoiceListener a, com.cannontech.common.gui.util.ColorChoiceListener b) {
	super(a, b);
}
/**
 * Add new listener to support multicast events.
 * @return com.cannontech.common.gui.util.ColorChoiceListener
 * @param a com.cannontech.common.gui.util.ColorChoiceListener
 * @param b com.cannontech.common.gui.util.ColorChoiceListener
 */
public static com.cannontech.common.gui.util.ColorChoiceListener add(com.cannontech.common.gui.util.ColorChoiceListener a, com.cannontech.common.gui.util.ColorChoiceListener b) {
	if (a == null)  return b;
	if (b == null)  return a;
	return new ColorChoiceListenerEventMulticaster(a, b);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void JRadioButtonItem_itemStateChanged(java.util.EventObject newEvent) {
	((com.cannontech.common.gui.util.ColorChoiceListener)a).JRadioButtonItem_itemStateChanged(newEvent);
	((com.cannontech.common.gui.util.ColorChoiceListener)b).JRadioButtonItem_itemStateChanged(newEvent);
}
/**
 * Remove listener to support multicast events.
 * @return com.cannontech.common.gui.util.ColorChoiceListener
 * @param a com.cannontech.common.gui.util.ColorChoiceListener
 * @param b com.cannontech.common.gui.util.ColorChoiceListener
 */
public static com.cannontech.common.gui.util.ColorChoiceListener remove(com.cannontech.common.gui.util.ColorChoiceListener a, com.cannontech.common.gui.util.ColorChoiceListener b) {
	return (com.cannontech.common.gui.util.ColorChoiceListener)removeInternal(a, b);
}
}
