package com.cannontech.tools.gui;

/**
 * @author rneuharth
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface IRunnableDBTool extends Runnable
{
	//used as a key in hash maps and labels in GUIs
	public static final String PROP_VALUE = "src_path";

	//less typing for these
	public static final String FS = System.getProperty("file.separator");
	public static final String LF = System.getProperty("line.separator");


	void setIMessageFrame( IMessageFrame frm );
	
	String getName();
	
	String getParamText();
	
	String getDefaultValue();
}
