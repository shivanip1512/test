package com.cannontech.common.gui.util;

/**
 * This is the event multicaster class to support the com.cannontech.common.gui.util.BitTogglePanelListenerEventMulticaster interface.
 */
public class BitTogglePanelListenerEventMulticaster extends java.awt.AWTEventMulticaster implements com.cannontech.common.gui.util.BitTogglePanelListener {
/**
 * Constructor to support multicast events.
 * @param a com.cannontech.common.gui.util.BitTogglePanelListener
 * @param b com.cannontech.common.gui.util.BitTogglePanelListener
 */
protected BitTogglePanelListenerEventMulticaster(com.cannontech.common.gui.util.BitTogglePanelListener a, com.cannontech.common.gui.util.BitTogglePanelListener b) {
	super(a, b);
}
/**
 * Add new listener to support multicast events.
 * @return com.cannontech.common.gui.util.BitTogglePanelListener
 * @param a com.cannontech.common.gui.util.BitTogglePanelListener
 * @param b com.cannontech.common.gui.util.BitTogglePanelListener
 */
public static com.cannontech.common.gui.util.BitTogglePanelListener add(com.cannontech.common.gui.util.BitTogglePanelListener a, com.cannontech.common.gui.util.BitTogglePanelListener b) {
	if (a == null)  return b;
	if (b == null)  return a;
	return new BitTogglePanelListenerEventMulticaster(a, b);
}
/**
 * Remove listener to support multicast events.
 * @return com.cannontech.common.gui.util.BitTogglePanelListener
 * @param a com.cannontech.common.gui.util.BitTogglePanelListener
 * @param b com.cannontech.common.gui.util.BitTogglePanelListener
 */
public static com.cannontech.common.gui.util.BitTogglePanelListener remove(com.cannontech.common.gui.util.BitTogglePanelListener a, com.cannontech.common.gui.util.BitTogglePanelListener b) {
	return (com.cannontech.common.gui.util.BitTogglePanelListener)removeInternal(a, b);
}
}
