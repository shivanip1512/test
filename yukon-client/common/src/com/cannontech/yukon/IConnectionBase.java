package com.cannontech.yukon;

import java.util.Observer;

/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public interface IConnectionBase 
{
	public void addObserver(Observer obs);

	public void deleteObserver(Observer obs);

	public String getHost();

	public int getPort();

	public boolean isValid();

}
