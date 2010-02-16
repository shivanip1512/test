package com.cannontech.web.menu.option.producer;

import java.util.List;

import com.cannontech.web.menu.option.SimpleMenuOptionLink;
import com.google.common.collect.Lists;

public class LeftSideContextualMenuOptionsProducer {
	
	private List<SimpleMenuOptionLink> menuOptions = Lists.newArrayList();
	private String selectedMenuId = null;
	
	public LeftSideContextualMenuOptionsProducer(List<SimpleMenuOptionLink> menuOptions, String selectedMenuId) {
		
		this.menuOptions = menuOptions;
		this.selectedMenuId = selectedMenuId;
	}

	public List<SimpleMenuOptionLink> getMenuOptions() {
		return menuOptions;
	}
	
	public String getSelectedMenuId() {
		return selectedMenuId;
	}
}
