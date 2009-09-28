package com.cannontech.tools.gui;

/**
 * @author rneuharth
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface IMessageFrame
{
	void addOutput(final String output);
	
	void addOutputNoLN( final String msg );
	
	void finish( String msg );
}
