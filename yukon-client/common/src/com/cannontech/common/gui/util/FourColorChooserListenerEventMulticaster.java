package com.cannontech.common.gui.util;

/**
 * This is the event multicaster class to support the com.cannontech.common.gui.util.FourColorChooserListenerEventMulticaster interface.
 */
public class FourColorChooserListenerEventMulticaster extends java.awt.AWTEventMulticaster implements com.cannontech.common.gui.util.FourColorChooserListener {
/**
 * Constructor to support multicast events.
 * @param a com.cannontech.common.gui.util.FourColorChooserListener
 * @param b com.cannontech.common.gui.util.FourColorChooserListener
 */
protected FourColorChooserListenerEventMulticaster(com.cannontech.common.gui.util.FourColorChooserListener a, com.cannontech.common.gui.util.FourColorChooserListener b) {
	super(a, b);
}
/**
 * Add new listener to support multicast events.
 * @return com.cannontech.common.gui.util.FourColorChooserListener
 * @param a com.cannontech.common.gui.util.FourColorChooserListener
 * @param b com.cannontech.common.gui.util.FourColorChooserListener
 */
public static com.cannontech.common.gui.util.FourColorChooserListener add(com.cannontech.common.gui.util.FourColorChooserListener a, com.cannontech.common.gui.util.FourColorChooserListener b) {
	if (a == null)  return b;
	if (b == null)  return a;
	return new FourColorChooserListenerEventMulticaster(a, b);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void colorChoice1Mouse_mouseReleased(java.util.EventObject newEvent) {
	((com.cannontech.common.gui.util.FourColorChooserListener)a).colorChoice1Mouse_mouseReleased(newEvent);
	((com.cannontech.common.gui.util.FourColorChooserListener)b).colorChoice1Mouse_mouseReleased(newEvent);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void colorChoice2Mouse_mouseReleased(java.util.EventObject newEvent) {
	((com.cannontech.common.gui.util.FourColorChooserListener)a).colorChoice2Mouse_mouseReleased(newEvent);
	((com.cannontech.common.gui.util.FourColorChooserListener)b).colorChoice2Mouse_mouseReleased(newEvent);
}
/**
 * Remove listener to support multicast events.
 * @return com.cannontech.common.gui.util.FourColorChooserListener
 * @param a com.cannontech.common.gui.util.FourColorChooserListener
 * @param b com.cannontech.common.gui.util.FourColorChooserListener
 */
public static com.cannontech.common.gui.util.FourColorChooserListener remove(com.cannontech.common.gui.util.FourColorChooserListener a, com.cannontech.common.gui.util.FourColorChooserListener b) {
	return (com.cannontech.common.gui.util.FourColorChooserListener)removeInternal(a, b);
}
}
