package com.cannontech.common.dynamicBilling.model;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class which represents a dynamic billing format
 */
public class DynamicFormat {
	private int formatId;
	private String name = null;
	private String delim = null; 
	private String header = null;
	private String footer = null;
	private boolean isSystem;
	private List<DynamicBillingField> fieldList = new ArrayList<DynamicBillingField>();
	
	public List<DynamicBillingField> getFieldList() {
		return fieldList;
	}
	public void setFieldList(List<DynamicBillingField> fieldList) {
		this.fieldList = fieldList;
	}
	public String getDelim() {
		return delim;
	}
	public void setDelim(String delim) {
		this.delim = delim;
	}
	public String getFooter() {
		return footer;
	}
	public void setFooter(String footer) {
		this.footer = footer;
	}
	public int getFormatId() {
		return formatId;
	}
	public void setFormatId(int formatId) {
		this.formatId = formatId;
	}
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public void addField(DynamicBillingField field){
		fieldList.add(field);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean getIsSystem() {
		return isSystem;
	}
	public void setIsSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}
}
