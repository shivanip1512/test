package com.cannontech.cbc.web;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.util.ParamUtil;


/**
 * @author rneuharth
 *
 * Represents a data structure used to store session info about a CBC connection.
 * One of these will exist per user.
 */
public class CBCSessionInfo
{
	private String lastArea = "";
	private int lastSubID = 0;
	private int lastFeederID = 0;
	private String lastSearchCriteria = "";
	
	private HashMap treeState = new HashMap(100);

	public static final String STR_CBC_AREA = "cbc_lastArea";
	public static final String STR_SUBID = "cbc_lastSubID";
	public static final String STR_FEEDERID = "cbc_lastFeederID";
	public static final String STR_LAST_SEARCH = "cbc_lastSearch";
	
	public static final String POPUP_EVENT = "pop_up_event";
	public CBCSessionInfo()
	{
		super();
	}
	
	public void updateState( HttpServletRequest req )
	{
		setLastArea( ParamUtil.getString(req, STR_CBC_AREA, getLastArea()) );
		setLastSubID( ParamUtil.getInteger(req, STR_SUBID, getLastSubID()) );
		setLastFeederID( ParamUtil.getInteger(req, STR_FEEDERID, getLastFeederID()) );
		setLastSearchCriteria( ParamUtil.getString(req, STR_LAST_SEARCH, getLastSearchCriteria()) );
	}

	/**
	 * @return
	 */
	public String getLastArea()
	{
		return lastArea;
	}

	/**
	 * @param string
	 */
	public void setLastArea(String area_ )
	{
		lastArea = area_;
	}

	/**
	 * @return
	 */
	public int getLastFeederID()
	{
		return lastFeederID;
	}

	/**
	 * @return
	 */
	public int getLastSubID()
	{
		return lastSubID;
	}

	/**
	 * @param i
	 */
	public void setLastFeederID(int i)
	{
		lastFeederID = i;
	}

	/**
	 * @param i
	 */
	public void setLastSubID(int i)
	{
		lastSubID = i;
	}

	/**
	 * @return
	 */
	public String getLastSearchCriteria() {
		return lastSearchCriteria;
	}

	/**
	 * @param string
	 */
	public void setLastSearchCriteria(String string) {
		lastSearchCriteria = string;
	}

	public Object getTreeState(String key) {
		return treeState.get(key);
	}

	public void setTreeState(String key, Object state) {
		this.treeState.put(key, state);
	}

}
