package com.cannontech.capcontrol.creation.model;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum CapControlXmlImportEnum {
	AREA("area", PaoType.CAP_CONTROL_AREA),
	SPECIAL_AREA("specialArea", PaoType.CAP_CONTROL_SPECIAL_AREA),
	SUBSTATION("substation", PaoType.CAP_CONTROL_SUBSTATION),
	SUBSTATION_BUS("substationBus", PaoType.CAP_CONTROL_SUBBUS),
	FEEDER("feeder", PaoType.CAP_CONTROL_FEEDER),
	CAP_BANK("capBank", PaoType.CAPBANK),
	;
	
	private final String xmlElementName;
	private final PaoType paoType;
	
	private final static Logger log = YukonLogManager.getLogger(CapControlXmlImportEnum.class);
	
	private final static ImmutableMap<String, CapControlXmlImportEnum> lookupByXmlString;
	
	static {
		try {
			Builder<String, CapControlXmlImportEnum> dbBuilder = ImmutableMap.builder();
			for (CapControlXmlImportEnum value : values()) {
                dbBuilder.put(value.xmlElementName, value);
            }
			lookupByXmlString = dbBuilder.build();
        } catch (IllegalArgumentException e) {
            log.warn("Caught exception while building lookup maps, look for a duplicate name or db string.", e);
            throw e;
        }
	}
	
	private CapControlXmlImportEnum(String xmlElementName, PaoType paoType) {
		this.xmlElementName = xmlElementName;
		this.paoType = paoType;
	}
	
	public static CapControlXmlImportEnum getForXmlString(String xmlString) {
		CapControlXmlImportEnum value = lookupByXmlString.get(xmlString);
		Validate.notNull(value, xmlString);
		return value;
	}
	
	public static PaoType getPaoTypeForXmlString(String xmlString) {
		CapControlXmlImportEnum value = getForXmlString(xmlString);
		return value.paoType;
	}
	
	public PaoType getPaoType() {
		return paoType;
	}
	
	public String getXmlElementName() {
		return xmlElementName;
	}
}
