package com.cannontech.web.loadcontrol;

import com.cannontech.loadcontrol.displays.ControlAreaActionListener;
import com.cannontech.loadcontrol.gui.manualentry.ResponseProg;

/**
 * @author rneuharth
 *
 * Data object used for session info in LoadManagement.
 * 
 */
public class LMSession
{
	public static final String DEF_REDIRECT = "controlareas.jsp";
	public static final int REF_SECONDS_DEF = 60;
	public static final int REF_SECONDS_PEND = 5;
	
	private String areaView = ControlAreaActionListener.SEL_ACTIVE_AREAS;
	private ResponseProg[] responseProgs = null;

	//what our current refresh rate is
	private int refreshRate = REF_SECONDS_DEF;
	

	/**
	 * 
	 */
	public LMSession()
	{
		super();
	}

	/**
	 * @return
	 */
	public String getAreaView()
	{
		return areaView;
	}

	/**
	 * @param string
	 */
	public void setAreaView(String string)
	{
		areaView = string;
	}
	
	/**
	 * @return
	 */
	public int getRefreshRate()
	{
		return refreshRate;
	}
	
	/**
	 * This clears out all saved sync messages.
	 *
	 */
	public void clearSyncMessages()
	{
		setResponseProgs( null );
	}

	/**
	 * @param i
	 */
	public void setRefreshRate(int i)
	{
		refreshRate = i;
	}

	/**
	 * @return
	 */
	public ResponseProg[] getResponseProgs()
	{
		return responseProgs;
	}

	/**
	 * @param progs
	 */
	public void setResponseProgs(ResponseProg[] progs)
	{
		responseProgs = progs;
	}

}
