package com.cannontech.tdc.spawn;

/**
 * This is the event multicaster class to support the com.cannontech.tdc.TDCMainFrameListenerEventMulticaster interface.
 */
public class TDCMainFrameSpawnListenerEventMulticaster extends java.awt.AWTEventMulticaster implements TDCMainFrameSpawnListener 
{
/**
 * Constructor to support multicast events.
 * @param a java.util.EventListener
 * @param b java.util.EventListener
 */
protected TDCMainFrameSpawnListenerEventMulticaster(java.util.EventListener a, java.util.EventListener b) {
	super(a, b);
}
/**
 * Add new listener to support multicast events.
 * @return com.cannontech.tdc.TDCMainFrameListener
 * @param a com.cannontech.tdc.TDCMainFrameListener
 * @param b com.cannontech.tdc.TDCMainFrameListener
 */
public static TDCMainFrameSpawnListener add(TDCMainFrameSpawnListener a, TDCMainFrameSpawnListener b) 
{
	return (TDCMainFrameSpawnListener)addInternal(a, b);
}
/**
 * Add new listener to support multicast events.
 * @return java.util.EventListener
 * @param a java.util.EventListener
 * @param b java.util.EventListener
 */
protected static java.util.EventListener addInternal(java.util.EventListener a, java.util.EventListener b) 
{
	if (a == null)  return b;
	if (b == null)  return a;
	return new TDCMainFrameSpawnListenerEventMulticaster(a, b);
}
/**
 * 
 */
public void otherTDCMainFrameActionPerformed(SpawnTDCMainFrameEvent newEvent)
{
	((TDCMainFrameSpawnListener)a).otherTDCMainFrameActionPerformed(newEvent);
	((TDCMainFrameSpawnListener)b).otherTDCMainFrameActionPerformed(newEvent);
}
/**
 * 
 */
protected java.util.EventListener remove(TDCMainFrameSpawnListener oldl) {
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
 */
public static TDCMainFrameSpawnListener remove(TDCMainFrameSpawnListener l, TDCMainFrameSpawnListener oldl) {
	if (l == oldl || l == null)
		return null;
	if(l instanceof TDCMainFrameSpawnListenerEventMulticaster)
		return (TDCMainFrameSpawnListener)((TDCMainFrameSpawnListenerEventMulticaster) l).remove(oldl);
	return l;
}
}
