package com.cannontech.web.menu.option.producer;

import java.util.List;

public abstract class SearchProducerBase implements SearchProducer {

	// typically search form with do a get
	@Override
	public String getFormMethod() {
		return "get";
	}

	// typically not used
	@Override
	public String getTypeName() {
		return null;
	}

	// typically not used
	@Override
	public List<SearchType> getTypeOptions() {
		return null;
	}
}
