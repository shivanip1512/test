package com.cannontech.web.loadcontrol;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.core.dao.AuthDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.gui.manualentry.ResponseProg;
import com.cannontech.messaging.message.loadcontrol.ManualControlRequestMessage;
import com.cannontech.roles.loadcontrol.DirectLoadcontrolRole;
import com.cannontech.spring.YukonSpringHook;

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
	
	private String areaView = null; //ControlAreaActionListener.SEL_ALL_CONTROL_AREAS;	
	private ResponseProg[] responseProgs = null;

	//what our current refresh rate is
	private int refreshRate = REF_SECONDS_DEF;
	

	/**
	 * 
	 */
	public LMSession() {
		super();		
	}

	public List getConstraintOptions( LiteYukonUser user ) {

		ArrayList constList = new ArrayList(8);

		if( YukonSpringHook.getBean(AuthDao.class).checkRoleProperty( user,
				DirectLoadcontrolRole.ALLOW_OBSERVE_CONSTRAINTS) )
			constList.add(
				ManualControlRequestMessage.CONSTRAINT_FLAG_STRS[ManualControlRequestMessage.CONSTRAINTS_FLAG_USE] );

		if( YukonSpringHook.getBean(AuthDao.class).checkRoleProperty( user,
				DirectLoadcontrolRole.ALLOW_CHECK_CONSTRAINTS) )
			constList.add(
				ManualControlRequestMessage.CONSTRAINT_FLAG_STRS[ManualControlRequestMessage.CONSTRAINTS_FLAG_CHECK] );	

		return constList;
	}

    public boolean isOverrideAllowed( LiteYukonUser user ) {

        return YukonSpringHook.getBean(AuthDao.class).checkRoleProperty( user,
                DirectLoadcontrolRole.ALLOW_OVERRIDE_CONSTRAINT);
    }

	public String getConstraintDefault( LiteYukonUser user ) {

		//set first element to be the selection specified
		// in our default role property
		String defSel = 
			YukonSpringHook.getBean(AuthDao.class).getRolePropertyValue(
				user,
				DirectLoadcontrolRole.DEFAULT_CONSTRAINT_SELECTION);

		return defSel;
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
