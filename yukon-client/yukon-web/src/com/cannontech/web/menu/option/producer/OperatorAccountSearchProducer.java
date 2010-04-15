package com.cannontech.web.menu.option.producer;

import java.util.List;

import com.cannontech.stars.dr.general.model.OperatorAccountSearchBy;
import com.google.common.collect.Lists;

public class OperatorAccountSearchProducer extends SearchProducerBase {

	@Override
	public String getFormAction() {
		return "/spring/stars/operator/account/search";
	}
	
	@Override
	public String getFieldName() {
		return "searchValue";
	}
	
	@Override
	public List<SearchType> getTypeOptions() {
		
		OperatorAccountSearchBy[] searchBys = OperatorAccountSearchBy.values();
		List<SearchType> searchTypes = Lists.newArrayListWithExpectedSize(searchBys.length);
		
		for (OperatorAccountSearchBy searchBy : searchBys) {
			SearchType searchType = new SearchType(searchBy.name(), searchBy.getFormatKey());
			searchTypes.add(searchType);
		}
		
		return searchTypes;
	}
	
	@Override
	public String getTypeName() {
		return "searchBy";
	}
}
