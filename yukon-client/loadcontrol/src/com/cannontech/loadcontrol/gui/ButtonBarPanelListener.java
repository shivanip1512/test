package com.cannontech.loadcontrol.gui;

public interface ButtonBarPanelListener extends java.util.EventListener {
/**
 * 
 * @param newEvent java.util.EventObject
 */
void JButtonDisableAllAction_actionPerformed(java.util.EventObject newEvent);
/**
 * 
 * @param newEvent java.util.EventObject
 */
void JButtonEnableAllAction_actionPerformed(java.util.EventObject newEvent);
/**
 * 
 * @param newEvent java.util.EventObject
 */
void JButtonEnableControlAreaAction_actionPerformed(java.util.EventObject newEvent);
}
