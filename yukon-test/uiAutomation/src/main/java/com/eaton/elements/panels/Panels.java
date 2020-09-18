package com.eaton.elements.panels;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.eaton.framework.DriverExtensions;

public class Panels {
	
	private DriverExtensions driverExt;
	private List<BasePanel> panels;
	 
	//================================================================================
	// Constructor Functions Section
	//================================================================================
	public Panels(DriverExtensions driverExt) {
		this.driverExt = driverExt;
	}
	
	//================================================================================
	// Getters/Setters Section
	//================================================================================
	public List<BasePanel> getPanels() {
		if(panels == null) {
			List<WebElement> elements = this.driverExt.findElements(By.cssSelector(".titled-container"), Optional.empty());
			panels = new ArrayList<BasePanel>();
			
			for(WebElement element : elements)
			{
				//Only include elements that are widgets and not sub-widgets
				if(element.getAttribute("class").contains("widget-container")) {
					panels.add(new BasePanel(driverExt, StringUtils.substringBefore(element.getText(),"\n")));
				}
			}
		}
		return panels;
    }
}