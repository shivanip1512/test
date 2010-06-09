package com.cannontech.web.lite;

import java.util.Collections;
import java.util.List;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * Executes a linear search by name on LitePAOs and
 * LitePoints based on a given criteria. The given criteria is
 * a java regular expression.
 *  
 * @author ryan
 */
public class LiteBaseResults {
	private List<LiteWrapper> foundItems;
	private String criteria = null;
	
	public void searchLiteObjects( String srchCriteria ) {
	    foundItems = Lists.newArrayList();
	    
		setCriteria( srchCriteria );

		if (getCriteria() != null && getCriteria().length() > 0) {

            final List<LiteYukonPAObject> paoList = DaoFactory.getPaoDao().searchByName(getCriteria(), PAOGroups.STRING_CAT_CAPCONTROL);
            final List<LitePoint> pointList = DaoFactory.getPointDao().searchByName(getCriteria(), PAOGroups.STRING_CAT_CAPCONTROL);
            
            final Function<LiteBase, LiteWrapper> toLiteWrapper = new Function<LiteBase, LiteWrapper>() {
                public LiteWrapper apply(LiteBase liteBase) {
                    return new LiteWrapper(liteBase);
                }};
            
            List<LiteWrapper> foundPaoWrappers = Lists.transform(paoList, toLiteWrapper);
            List<LiteWrapper> foundPointWrappers = Lists.transform(pointList, toLiteWrapper);
            
            foundItems.addAll(foundPaoWrappers);
            foundItems.addAll(foundPointWrappers);
            
            Collections.sort(foundItems, LiteComparators.liteNameComparator);
		}

	}

	public List<LiteWrapper> getFoundItems() {
	    return foundItems;
	}
	
	public List<LiteWrapper> getFoundItems(int fromIndex, int pageCount) {
	    int lastIndex = foundItems.size() -1;
	    if(lastIndex < (fromIndex + (pageCount - 1))) {
	        return foundItems.subList(fromIndex, foundItems.size());
	    } else {
	        return foundItems.subList(fromIndex, fromIndex + pageCount);
	    }
	}

	public String getCriteria() {
		return criteria;
	}

	public void setCriteria(String string) {
		criteria = string;
	}

}