package com.cannontech.web.menu.option.producer;


public class CapControlSearchProducer extends SearchProducerBase {

	@Override
	public String getFormAction() {
		return "/spring/capcontrol/search/searchResults";
	}
	
	@Override
	public String getFieldName() {
		return "cbc_lastSearch";
	}
}
