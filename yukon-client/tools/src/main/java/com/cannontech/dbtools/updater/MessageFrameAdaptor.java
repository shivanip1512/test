package com.cannontech.dbtools.updater;

import com.cannontech.tools.gui.IMessageFrame;
import com.cannontech.tools.gui.IRunnableDBTool;

/**
 * @author rneuharth
 *
 * Classes who want a default way of creating a message frame should extend
 * this class.
 */
public abstract class MessageFrameAdaptor implements IRunnableDBTool
{
	private IMessageFrame output = null;

	/**
	 * 
	 */
	public MessageFrameAdaptor()
	{
		super();
	}



	/**
	 * Insert the method's description here.
	 * Creation date: (7/12/2001 1:05:41 PM)
	 * @param newCf IMessageFrame
	 */
	public void setIMessageFrame(IMessageFrame newMf) 
	{
		output = newMf;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (7/12/2001 1:05:41 PM)
	 * @param newCf IMessageFrame
	 */
	public IMessageFrame getIMessageFrame() 
	{
		if( output == null )
		{
			//just in case this is not set, add a default outputter
			output = new IMessageFrame()
			{
				public void addOutput( final String msg )
				{
					System.out.println(msg);
				}
			
				public void addOutputNoLN( final String msg )
                {
                    System.out.print(msg);
                }
				
				public void finish( String msg )
				{}  //no-op for now
			
			};
		}
	
		return output;
	}

}