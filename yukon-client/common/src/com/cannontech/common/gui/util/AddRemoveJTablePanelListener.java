package com.cannontech.common.gui.util;

public interface AddRemoveJTablePanelListener extends java.util.EventListener {
/**
 * 
 * @param newEvent java.util.EventObject
 */
void JButtonAddAction_actionPerformed(java.util.EventObject newEvent);
/**
 * 
 * @param newEvent java.util.EventObject
 */
void JButtonRemoveAction_actionPerformed(java.util.EventObject newEvent);


void MouseTableAction_actionPerformed(java.util.EventObject newEvent);
}
