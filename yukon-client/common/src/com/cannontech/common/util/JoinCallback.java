package com.cannontech.common.util;

/**
 * This type was created in VisualAge.
 */
public class JoinCallback extends Thread {
	private Thread threadToJoin;
	private Callable objectToCall;
/**
 * JoinCallback constructor comment.
 */
private JoinCallback() {
	super();
}
/**
 * This method was created in VisualAge.
 * @param threadToJoin java.lang.Thread
 * @param objectToCall com.cannontech.common.util.Callable
 */
private JoinCallback(Thread threadToJoin, Callable objectToCall) {
	this.threadToJoin = threadToJoin;
	this.objectToCall = objectToCall;
}
/**
 * This method was created in VisualAge.
 */
public void run() {

	try
	{
		threadToJoin.join();
	}
	catch(InterruptedException e )
	{
		//guess if we get interrupted we call the callback method?
	}
	finally
	{
		objectToCall.callbackMethod();
	}
}
/**
 * This method was created in VisualAge.
 * @param threadToJoin java.lang.Thread
 * @param objectToCall com.cannontech.common.util.Callable
 */
public static void setJoinCallback(Thread threadToJoin, Callable objectToCall) {

	JoinCallback j = new JoinCallback( threadToJoin, objectToCall );
	j.start();
	
}
}
