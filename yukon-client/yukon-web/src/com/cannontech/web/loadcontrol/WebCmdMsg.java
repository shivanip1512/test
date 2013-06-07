package com.cannontech.web.loadcontrol;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.loadcontrol.data.Data;

/**
 * @author rneuharth
 *
 * A structure used to store data for web messages.
 */
public class WebCmdMsg
{
	private String _htmlTextMsg = "Are you sure?";
	private String _cmd = "Unknown";
	private Data _lmData = null;

	//dynamically created message for the load control server
	private BaseMessage _genLCMsg = null;
	
	
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

	protected boolean isScenarioMsg()
	{
		return getCmd().startsWith("sc_");
	}


	public BaseMessage genLCCmdMsg()
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
	public Data getLMData()
	{
		return _lmData;
	}

	/**
	 * @return
	 */
	protected void setLMData( Data lmData )
	{
		_lmData = lmData;
	}


	/**
	 * @return
	 */
	protected void setGenLCMsg( BaseMessage lmMsg )
	{
		_genLCMsg = lmMsg;
	}


    public void setCmd(String _cmd) {
        this._cmd = _cmd;
    }

}