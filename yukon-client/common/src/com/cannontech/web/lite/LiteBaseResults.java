package com.cannontech.web.lite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;

/**
 * Executes a linear search by name on LitePAOs and
 * LitePoints based on a given criteria. The given criteria is
 * a java regular expression.
 *  
 * @author ryan
 */
public class LiteBaseResults
{
	//contains LiteWrapper objects
	private LiteWrapper[] foundItems = new LiteWrapper[0];
	private String criteria = null;
	
	/**
	 * 
	 */
	public LiteBaseResults()
	{
		super();
	}
	
	public void searchLiteObjects( String srchCriteria ) {
		foundItems = new LiteWrapper[0];
		setCriteria( srchCriteria );

		if (getCriteria() != null && getCriteria().length() > 0) {

            final List<LiteYukonPAObject> paoList = DaoFactory.getPaoDao().searchByName(getCriteria(), PAOGroups.STRING_CAT_CAPCONTROL);
            final List<LitePoint> pointList = DaoFactory.getPointDao().searchByName(getCriteria(), PAOGroups.STRING_CAT_CAPCONTROL);
            final List<LiteWrapper> wrapperList = new ArrayList<LiteWrapper>(paoList.size() + pointList.size());
            
            for (final LiteYukonPAObject liteYukonPAObject : paoList) {
                wrapperList.add(new LiteWrapper(liteYukonPAObject));
            }
            
            for (final LitePoint point : pointList) {
                wrapperList.add(new LiteWrapper(point));
            }
            
            Collections.sort(wrapperList, LiteComparators.liteNameComparator);
            foundItems = wrapperList.toArray(new LiteWrapper[wrapperList.size()]);
		}

	}

	/**
	 * @return
	 */
	public LiteWrapper[] getFoundItems()
	{
		return foundItems;
	}


	/**
	 * @return
	 */
	public String getCriteria()
	{
		return criteria;
	}

	/**
	 * @param string
	 */
	public void setCriteria(String string)
	{
		criteria = string;
	}

}
