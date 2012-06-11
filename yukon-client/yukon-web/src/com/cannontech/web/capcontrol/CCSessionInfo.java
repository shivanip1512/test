package com.cannontech.web.capcontrol;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.util.ParamUtil;


/**
 * @author rneuharth
 *
 * Represents a data structure used to store session info about a CBC connection.
 * One of these will exist per user.
 */
public class CCSessionInfo
{
	private String lastArea = "";
    private Integer lastAreaId = -1;
	private int lastSubID = 0;
	private int lastFeederID = 0;
	private String lastSearchCriteria = "";
	private String lastSpecialAreaFlag = "";
	
	private HashMap<String, Object> treeState = new HashMap<String, Object>(100);

	public static final String STR_CC_AREA = "cbc_lastArea";
    public static final String STR_CC_AREAID = "cbc_lastAreaId";
	public static final String STR_SUBID = "cbc_lastSubID";
	public static final String STR_FEEDERID = "cbc_lastFeederID";
	public static final String STR_LAST_SEARCH = "cbc_lastSearch";
	public static final String STR_LAST_SPECIALAREAFLAG = "specialArea";
	public static final String POPUP_EVENT = "pop_up_event";
	public CCSessionInfo()
	{
		super();
	}
	
	public void updateState( HttpServletRequest req )
	{
		setLastArea( ParamUtil.getString(req, STR_CC_AREA, getLastArea()) );
        setLastAreaId( ParamUtil.getInteger(req, STR_CC_AREAID, getLastAreaId()) );
		setLastSubID( ParamUtil.getInteger(req, STR_SUBID, getLastSubID()) );
		setLastFeederID( ParamUtil.getInteger(req, STR_FEEDERID, getLastFeederID()) );
		setLastSearchCriteria( ParamUtil.getString(req, STR_LAST_SEARCH, getLastSearchCriteria()) );
		setLastSpecialAreaFlag( ParamUtil.getString(req, STR_LAST_SPECIALAREAFLAG, getLastSpecialAreaFlag()) );
	}

	/**
	 * @return String
	 */
	public String getLastArea() {
		return lastArea;
	}

	/**
	 * @param area_
	 */
	public void setLastArea(String area_ ) {
		lastArea = area_;
	}
    
    /**
     * @return Integer
     */
    public Integer getLastAreaId() {
        return lastAreaId;
    }

    /**
     * @param Integer
     */
    public void setLastAreaId(Integer areaId ) {
        lastAreaId = areaId;
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

	public String getLastSpecialAreaFlag() {
		return lastSpecialAreaFlag;
	}

	public void setLastSpecialAreaFlag(String lastSpecialAreaFlag) {
		this.lastSpecialAreaFlag = lastSpecialAreaFlag;
	}

}
