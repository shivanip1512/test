package com.cannontech.web.search.lucene.criteria;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.database.data.pao.DeviceTypes;

public class TwoWayLcrCriteria extends YukonObjectCriteriaHelper {

	private final static List<String> types = new ArrayList<String>();
	
	static {
		types.addAll(Arrays.asList(DeviceTypes.STRING_LCR_3102));
	}
	
	public TwoWayLcrCriteria() {
		
	    for (String type : types) {
	        addCriteria("type", type, BooleanClause.Occur.SHOULD);
	    }
	}
}
