package com.cannontech.tools.gui;

/**
 * @author rneuharth
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface IRunnableDBTool extends Runnable
{
	public static final String PROP_PATH = "srcpath";

	void setIMessageFrame( IMessageFrame frm );
	
	String getName();
}
