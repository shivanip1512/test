package com.cannontech.tdc.toolbar;

public interface AlarmToolBarListener extends java.util.EventListener {
/**
 * 
 * @param newEvent java.util.EventObject
 */
void JToolBarButtonAckAllAction_actionPerformed(java.util.EventObject newEvent);
/**
 * 
 * @param newEvent java.util.EventObject
 */
void JToolBarButtonClearAction_actionPerformed(java.util.EventObject newEvent);
/**
 * 
 * @param newEvent java.util.EventObject
 */
void JToolBarButtonClearAlarmAction_actionPerformed(java.util.EventObject newEvent);
/**
 * 
 * @param newEvent java.util.EventObject
 */
void JToolBarButtonClearViewableAlarmsAction_actionPerformed(java.util.EventObject newEvent);
/**
 * 
 * @param newEvent java.util.EventObject
 */
void JToolBarButtonRefreshAction_actionPerformed(java.util.EventObject newEvent);
/**
 * 
 * @param newEvent java.util.EventObject
 */
void JToolBarButtonSilenceAlarmsAction_actionPerformed(java.util.EventObject newEvent);
/**
 * 
 * @param newEvent java.util.EventObject
 */
void JToolBarJCDateChange_actionPerformed(com.klg.jclass.util.value.JCValueEvent event);
}
