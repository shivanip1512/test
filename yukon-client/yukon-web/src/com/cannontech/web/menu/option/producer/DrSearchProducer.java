package com.cannontech.web.menu.option.producer;


public class DrSearchProducer extends SearchProducerBase {

	@Override
	public String getFormAction() {
		return "/spring/dr/search";
	}
	
	@Override
	public String getFieldName() {
		return "name";
	}
}
