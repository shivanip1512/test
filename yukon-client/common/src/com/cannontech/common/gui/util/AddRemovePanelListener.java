package com.cannontech.common.gui.util;

public interface AddRemovePanelListener extends java.util.EventListener {
/**
 * 
 * @param newEvent java.util.EventObject
 */
void addButtonAction_actionPerformed(java.util.EventObject newEvent);
/**
 * 
 * @param newEvent java.util.EventObject
 */
void leftListListSelection_valueChanged(java.util.EventObject newEvent);
/**
 * 
 * @param newEvent java.util.EventObject
 */
void removeButtonAction_actionPerformed(java.util.EventObject newEvent);
/**
 * 
 * @param newEvent java.util.EventObject
 */
void rightListListSelection_valueChanged(java.util.EventObject newEvent);
/**
 * 
 * @param newEvent java.util.EventObject
 */
void rightListMouse_mouseClicked(java.util.EventObject newEvent);
/**
 * 
 * @param newEvent java.util.EventObject
 */
void rightListMouse_mouseEntered(java.util.EventObject newEvent);
/**
 * 
 * @param newEvent java.util.EventObject
 */
void rightListMouse_mouseExited(java.util.EventObject newEvent);
/**
 * 
 * @param newEvent java.util.EventObject
 */
void rightListMouse_mousePressed(java.util.EventObject newEvent);
/**
 * 
 * @param newEvent java.util.EventObject
 */
void rightListMouse_mouseReleased(java.util.EventObject newEvent);
/**
 * 
 * @param newEvent java.util.EventObject
 */
void rightListMouseMotion_mouseDragged(java.util.EventObject newEvent);
}
