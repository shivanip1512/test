package com.cannontech.web.menu.option.producer;


public class MeteringSearchProducer extends SearchProducerBase {

	@Override
	public String getFormAction() {
		return "/spring/meter/search";
	}
	
	@Override
	public String getFieldName() {
		return "Quick Search";
	}
}
