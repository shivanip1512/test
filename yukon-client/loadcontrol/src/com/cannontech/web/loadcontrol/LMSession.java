package com.cannontech.web.loadcontrol;

import com.cannontech.clientutils.commonutils.ModifiedDate;
import com.cannontech.loadcontrol.displays.ControlAreaActionListener;

/**
 * @author rneuharth
 *
 * Data object used to session info in LoadManagement.
 * 
 */
public class LMSession
{
	public static final String DEF_REDIRECT = "controlareas.jsp";
	public static final int REF_SECONDS_DEF = 60;
	public static final int REF_SECONDS_PEND = 5;



	private String areaView = ControlAreaActionListener.SEL_ACTIVE_AREAS;

	//what our current refresh rate is
	private int refreshRate = REF_SECONDS_DEF;
	

	/**
	 * 
	 */
	public LMSession()
	{
		super();
		
		//dont show the seconds on timestamp strings
		ModifiedDate.setFormatPattern("MM-dd-yyyy HH:mm");		
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
	 * @param i
	 */
	public void setRefreshRate(int i)
	{
		refreshRate = i;
	}

}
