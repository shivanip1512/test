package com.cannontech.capcontrol.creation.model;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum CapControlXmlImport {
	AREA("area", PaoType.CAP_CONTROL_AREA),
	SPECIAL_AREA("specialArea", PaoType.CAP_CONTROL_SPECIAL_AREA),
	SUBSTATION("substation", PaoType.CAP_CONTROL_SUBSTATION),
	SUBSTATION_BUS("substationBus", PaoType.CAP_CONTROL_SUBBUS),
	FEEDER("feeder", PaoType.CAP_CONTROL_FEEDER),
	CAP_BANK("capBank", PaoType.CAPBANK),
	;
	
	private final String xmlElementName;
	private final PaoType paoType;
	
	private final static Logger log = YukonLogManager.getLogger(CapControlXmlImport.class);
	
	private final static ImmutableMap<String, CapControlXmlImport> lookupByXmlString;
	
	static {
		try {
			Builder<String, CapControlXmlImport> dbBuilder = ImmutableMap.builder();
			for (CapControlXmlImport value : values()) {
                dbBuilder.put(value.xmlElementName.toLowerCase(), value);
            }
			lookupByXmlString = dbBuilder.build();
        } catch (IllegalArgumentException e) {
            log.warn("Caught exception while building lookup maps, look for a duplicate name or db string.", e);
            throw e;
        }
	}
	
	private CapControlXmlImport(String xmlElementName, PaoType paoType) {
		this.xmlElementName = xmlElementName;
		this.paoType = paoType;
	}
	
	public static CapControlXmlImport getForXmlString(String xmlString) {
		CapControlXmlImport value = lookupByXmlString.get(xmlString.toLowerCase());
		Validate.notNull(value, xmlString);
		return value;
	}
	
	public static PaoType getPaoTypeForXmlString(String xmlString) {
		CapControlXmlImport value = getForXmlString(xmlString);
		return value.paoType;
	}
	
	public PaoType getPaoType() {
		return paoType;
	}
	
	public String getXmlElementName() {
		return xmlElementName;
	}
}
