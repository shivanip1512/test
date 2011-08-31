package com.cannontech.capcontrol.creation.model;

import com.cannontech.common.pao.PaoType;
	
public class HierarchyImportData {
	
	private PaoType paoType;
	private String name;
	private String parent;
	private String description;
	private String mapLocationId;
	private Boolean disabled;
	private ImportActionsEnum importAction;
	
	public PaoType getPaoType() {
		return paoType;
	}
	
	public void setPaoType(PaoType paoType) {
		this.paoType = paoType;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
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
	
	public ImportActionsEnum getImportAction() {
		return importAction;
	}
	
	public void setImportAction(ImportActionsEnum importAction) {
		this.importAction = importAction;
	}
}