package com.cannontech.web.stars.dr.operator.deviceReconfig;

import java.beans.PropertyEditor;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cannontech.web.input.type.DateType;
import com.cannontech.web.input.type.IntegerSetType;
import com.cannontech.web.input.type.IntegerType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DeviceReconfigOptions {
	
	// PROPERTY EDITORS
	private static PropertyEditor integerSetPropertyEditor = (new IntegerSetType()).getPropertyEditor();
	private static PropertyEditor datePropertyEditor = (new DateType()).getPropertyEditor();
	private static PropertyEditor integerPropertyEditor = (new IntegerType()).getPropertyEditor();
	
	// MEMBER VARIABLES
	private DeviceReconfigDeviceSelectionStyle deviceSelectionStyle = DeviceReconfigDeviceSelectionStyle.SELECTION;

	private List<Integer> loadGroupPaoIds = Lists.newArrayList();
	private List<Integer> loadProgramPaoIds = Lists.newArrayList();
	private Date fieldInstallDate = null;
	private Date programSignupDate = null;
	private Integer deviceType = null;
	private Integer serialNumberRangeStart = null;
	private Integer serialNumberRangeEnd = null;
	private boolean notEnrolled = false;
	
	private DeviceReconfigReconfigurationStyle reconfigurationStyle = DeviceReconfigReconfigurationStyle.CURRENT_SETTINGS;
	private Integer reconfigStyleByLoadGroupId = null;
	
	// METHODS
	public Map<String, String> getUrlParameterMap() {
		
		Map<String, String> nameValueMap = Maps.newLinkedHashMap();
		nameValueMap.put("deviceSelectionStyle", deviceSelectionStyle.name());
		integerSetPropertyEditor.setValue(loadGroupPaoIds);
		nameValueMap.put("loadGroupPaoIds", integerSetPropertyEditor.getAsText());
		integerSetPropertyEditor.setValue(loadProgramPaoIds);
		nameValueMap.put("loadProgramPaoIds", integerSetPropertyEditor.getAsText());
		datePropertyEditor.setValue(fieldInstallDate);
		nameValueMap.put("fieldInstallDate", datePropertyEditor.getAsText());
		datePropertyEditor.setValue(programSignupDate);
		nameValueMap.put("programSignupDate", datePropertyEditor.getAsText());
		integerPropertyEditor.setValue(deviceType);
		nameValueMap.put("deviceType", integerPropertyEditor.getAsText());
		integerPropertyEditor.setValue(serialNumberRangeStart);
		nameValueMap.put("serialNumberRangeStart", integerPropertyEditor.getAsText());
		integerPropertyEditor.setValue(serialNumberRangeEnd);
		nameValueMap.put("serialNumberRangeEnd", integerPropertyEditor.getAsText());
		nameValueMap.put("notEnrolled", Boolean.toString(notEnrolled));
		
		return nameValueMap;
	}
	
	// GETTERS SETTERS
	public DeviceReconfigDeviceSelectionStyle getDeviceSelectionStyle() {
		return deviceSelectionStyle;
	}
	public void setDeviceSelectionStyle(DeviceReconfigDeviceSelectionStyle deviceSelectionStyle) {
		this.deviceSelectionStyle = deviceSelectionStyle;
	}
	
	public List<Integer> getLoadGroupPaoIds() {
		return loadGroupPaoIds;
	}
	public void setLoadGroupPaoIds(List<Integer> loadGroupPaoIds) {
		this.loadGroupPaoIds = loadGroupPaoIds;
	}
	public List<Integer> getLoadProgramPaoIds() {
		return loadProgramPaoIds;
	}
	public void setLoadProgramPaoIds(List<Integer> loadProgramPaoIds) {
		this.loadProgramPaoIds = loadProgramPaoIds;
	}
	public Date getFieldInstallDate() {
		return fieldInstallDate;
	}
	public void setFieldInstallDate(Date fieldInstallDate) {
		this.fieldInstallDate = fieldInstallDate;
	}
	public Date getProgramSignupDate() {
		return programSignupDate;
	}
	public void setProgramSignupDate(Date programSignupDate) {
		this.programSignupDate = programSignupDate;
	}
	public Integer getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(Integer deviceType) {
		this.deviceType = deviceType;
	}
	public Integer getSerialNumberRangeStart() {
		return serialNumberRangeStart;
	}
	public void setSerialNumberRangeStart(Integer serialNumberRangeStart) {
		this.serialNumberRangeStart = serialNumberRangeStart;
	}
	public Integer getSerialNumberRangeEnd() {
		return serialNumberRangeEnd;
	}
	public void setSerialNumberRangeEnd(Integer serialNumberRangeEnd) {
		this.serialNumberRangeEnd = serialNumberRangeEnd;
	}
	public boolean getNotEnrolled() {
		return notEnrolled;
	}
	public void setNotEnrolled(boolean notEnrolled) {
		this.notEnrolled = notEnrolled;
	}
	
	public DeviceReconfigReconfigurationStyle getReconfigurationStyle() {
		return reconfigurationStyle;
	}
	public void setReconfigurationStyle(DeviceReconfigReconfigurationStyle reconfigurationStyle) {
		this.reconfigurationStyle = reconfigurationStyle;
	}
	
	public Integer getReconfigStyleByLoadGroupId() {
		return reconfigStyleByLoadGroupId;
	}
	public void setReconfigStyleByLoadGroupId(Integer reconfigStyleByLoadGroupId) {
		this.reconfigStyleByLoadGroupId = reconfigStyleByLoadGroupId;
	}
}
