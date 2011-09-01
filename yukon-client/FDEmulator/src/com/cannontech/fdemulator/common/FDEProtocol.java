/*
 * Created on Jun 2, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.fdemulator.common;
import java.util.Observer;

import javax.swing.JPanel;

/**
 * @author ASolberg
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class FDEProtocol implements Runnable
{
	/**
	 * @return the protocol name
	 */
	public abstract String getName();

	public abstract JPanel getStatPanel();

	public abstract boolean startConnection();

	public abstract void closeConnection();

	public abstract FDTestPanel getTestPanel();

	public abstract void updateStats();

	public abstract boolean settingDone();

	public abstract void run();

	public abstract void hbOn(boolean on);

	public abstract String getTimeStamp();

	public abstract String getFormalTimeStamp();

	public abstract void listenForActions(Observer o);

	public abstract void editSettings();

}