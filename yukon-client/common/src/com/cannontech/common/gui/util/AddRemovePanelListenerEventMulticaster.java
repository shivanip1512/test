package com.cannontech.common.gui.util;

/**
 * This is the event multicaster class to support the com.cannontech.common.gui.util.AddRemovePanelListenerEventMulticaster interface.
 */
public class AddRemovePanelListenerEventMulticaster extends java.awt.AWTEventMulticaster implements AddRemovePanelListener {
/**
 * Constructor to support multicast events.
 * @param a com.cannontech.common.gui.util.AddRemovePanelListener
 * @param b com.cannontech.common.gui.util.AddRemovePanelListener
 */
protected AddRemovePanelListenerEventMulticaster(com.cannontech.common.gui.util.AddRemovePanelListener a, com.cannontech.common.gui.util.AddRemovePanelListener b) {
	super(a, b);
}
/**
 * Add new listener to support multicast events.
 * @return com.cannontech.common.gui.util.AddRemovePanelListener
 * @param a com.cannontech.common.gui.util.AddRemovePanelListener
 * @param b com.cannontech.common.gui.util.AddRemovePanelListener
 */
public static com.cannontech.common.gui.util.AddRemovePanelListener add(com.cannontech.common.gui.util.AddRemovePanelListener a, com.cannontech.common.gui.util.AddRemovePanelListener b) {
	if (a == null)  return b;
	if (b == null)  return a;
	return new AddRemovePanelListenerEventMulticaster(a, b);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void addButtonAction_actionPerformed(java.util.EventObject newEvent) {
	((com.cannontech.common.gui.util.AddRemovePanelListener)a).addButtonAction_actionPerformed(newEvent);
	((com.cannontech.common.gui.util.AddRemovePanelListener)b).addButtonAction_actionPerformed(newEvent);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void leftListListSelection_valueChanged(java.util.EventObject newEvent) {
	((com.cannontech.common.gui.util.AddRemovePanelListener)a).leftListListSelection_valueChanged(newEvent);
	((com.cannontech.common.gui.util.AddRemovePanelListener)b).leftListListSelection_valueChanged(newEvent);
}
/**
 * Remove listener to support multicast events.
 * @return com.cannontech.common.gui.util.AddRemovePanelListener
 * @param a com.cannontech.common.gui.util.AddRemovePanelListener
 * @param b com.cannontech.common.gui.util.AddRemovePanelListener
 */
public static com.cannontech.common.gui.util.AddRemovePanelListener remove(com.cannontech.common.gui.util.AddRemovePanelListener a, com.cannontech.common.gui.util.AddRemovePanelListener b) {
	return (com.cannontech.common.gui.util.AddRemovePanelListener)removeInternal(a, b);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void removeButtonAction_actionPerformed(java.util.EventObject newEvent) {
	((com.cannontech.common.gui.util.AddRemovePanelListener)a).removeButtonAction_actionPerformed(newEvent);
	((com.cannontech.common.gui.util.AddRemovePanelListener)b).removeButtonAction_actionPerformed(newEvent);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void rightListListSelection_valueChanged(java.util.EventObject newEvent) {
	((com.cannontech.common.gui.util.AddRemovePanelListener)a).rightListListSelection_valueChanged(newEvent);
	((com.cannontech.common.gui.util.AddRemovePanelListener)b).rightListListSelection_valueChanged(newEvent);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mouseClicked(java.util.EventObject newEvent) {
	((com.cannontech.common.gui.util.AddRemovePanelListener)a).rightListMouse_mouseClicked(newEvent);
	((com.cannontech.common.gui.util.AddRemovePanelListener)b).rightListMouse_mouseClicked(newEvent);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mouseEntered(java.util.EventObject newEvent) {
	((com.cannontech.common.gui.util.AddRemovePanelListener)a).rightListMouse_mouseEntered(newEvent);
	((com.cannontech.common.gui.util.AddRemovePanelListener)b).rightListMouse_mouseEntered(newEvent);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mouseExited(java.util.EventObject newEvent) {
	((com.cannontech.common.gui.util.AddRemovePanelListener)a).rightListMouse_mouseExited(newEvent);
	((com.cannontech.common.gui.util.AddRemovePanelListener)b).rightListMouse_mouseExited(newEvent);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mousePressed(java.util.EventObject newEvent) {
	((com.cannontech.common.gui.util.AddRemovePanelListener)a).rightListMouse_mousePressed(newEvent);
	((com.cannontech.common.gui.util.AddRemovePanelListener)b).rightListMouse_mousePressed(newEvent);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mouseReleased(java.util.EventObject newEvent) {
	((com.cannontech.common.gui.util.AddRemovePanelListener)a).rightListMouse_mouseReleased(newEvent);
	((com.cannontech.common.gui.util.AddRemovePanelListener)b).rightListMouse_mouseReleased(newEvent);
}
/**
 * 
 * @param newEvent java.util.EventObject
 */
public void rightListMouseMotion_mouseDragged(java.util.EventObject newEvent) {
	((com.cannontech.common.gui.util.AddRemovePanelListener)a).rightListMouseMotion_mouseDragged(newEvent);
	((com.cannontech.common.gui.util.AddRemovePanelListener)b).rightListMouseMotion_mouseDragged(newEvent);
}
}
