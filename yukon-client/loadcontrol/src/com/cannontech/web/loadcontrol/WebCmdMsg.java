package com.cannontech.web.loadcontrol;

import com.cannontech.loadcontrol.data.ILMData;
import com.cannontech.message.util.Message;

/**
 * @author rneuharth
 *
 * A structure used to store data for web messages.
 */
public class WebCmdMsg
{
	private String _htmlTextMsg = "Are you sure?";
	private String _cmd = "Unknown";
	private ILMData _lmData = null;

	//dynamically created message for the load control server
	private Message _genLCMsg = null;
	
	
	/**
	 * 
	 */
	protected WebCmdMsg( String cmd )
	{
		super();
		_cmd = cmd;
	}


	protected boolean isControlAreaMsg()
	{
		return getCmd().startsWith("a_");
	}

	protected boolean isProgramMsg()
	{
		return getCmd().startsWith("p_");
	}

	protected boolean isGroupMsg()
	{
		return getCmd().startsWith("g_");
	}


	public Message genLCCmdMsg()
	{
		return _genLCMsg;
	}

	/**
	 * @return
	 */
	public String getHTMLTextMsg()
	{
		return _htmlTextMsg;
	}

	/**
	 * @param string
	 */
	protected void setHTMLTextMsg(String string)
	{
		_htmlTextMsg = string;
	}

	/**
	 * @return
	 */
	public String getCmd()
	{
		return _cmd;
	}

	/**
	 * @return
	 */
	public ILMData getLMData()
	{
		return _lmData;
	}

	/**
	 * @return
	 */
	protected void setLMData( ILMData lmData )
	{
		_lmData = lmData;
	}


	/**
	 * @return
	 */
	protected void setGenLCMsg( Message lmMsg )
	{
		_genLCMsg = lmMsg;
	}

}