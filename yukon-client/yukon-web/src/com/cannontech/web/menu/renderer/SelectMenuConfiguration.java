package com.cannontech.web.menu.renderer;

import com.cannontech.web.menu.MenuBase;

public class SelectMenuConfiguration {

	private MenuBase menuBase;
	private String headerKey;
	private String onChange = "javascript:window.location=(this[this.selectedIndex].value);";
	
	public MenuBase getMenuBase() {
		return menuBase;
	}
	public void setMenuBase(MenuBase menuBase) {
		this.menuBase = menuBase;
	}
	public String getHeaderKey() {
		return headerKey;
	}
	public void setHeaderKey(String headerKey) {
		this.headerKey = headerKey;
	}
	public String getOnChange() {
		return onChange;
	}
	public void setOnChange(String onChange) {
		this.onChange = onChange;
	}
	
	
}
