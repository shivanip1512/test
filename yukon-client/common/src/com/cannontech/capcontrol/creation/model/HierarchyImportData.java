package com.cannontech.capcontrol.creation.model;

import com.cannontech.common.pao.PaoType;
	
public class HierarchyImportData {
	
	private final PaoType paoType;
	private final String name;
	private final ImportAction importAction;
	private String parent;
	private String description;
	private String mapLocationId;
	private Boolean disabled;
	
	public HierarchyImportData(PaoType paoType, String name, ImportAction importAction) {
	    this.paoType = paoType;
	    this.name = name;
	    this.importAction = importAction;
	}
	
	public PaoType getPaoType() {
		return paoType;
	}
	
	public String getName() {
		return name;
	}
	
	public String getParent() {
		return parent;
	}
	
	public void setParent(String parent) {
		this.parent = parent;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getMapLocationId() {
		return mapLocationId;
	}
	
	public void setMapLocationId(String mapLocationId) {
		this.mapLocationId = mapLocationId;
	}
	
	public Boolean isDisabled() {
		return disabled;
	}
	
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	
	public ImportAction getImportAction() {
		return importAction;
	}
}