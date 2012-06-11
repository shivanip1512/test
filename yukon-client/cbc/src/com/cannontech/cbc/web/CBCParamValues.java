package com.cannontech.cbc.web;

/**
 * Set of strings that are valid values for parameters passed
 * into web pages. The following format is used:
 *   XXX_YYY
 * 
 * XXX = the parameter name
 * YYY = the value of the parameter
 *
 *	TYPE_PF = parameter named "type", and this value is "pf"
 */
public interface CBCParamValues
{
	
	public static final String TYPE_PF = "PF";
	public static final String TYPE_VARWATTS = "VW";
	public static final String TYPE_RECENT_CNTRLS = "RC";

	public static final String TYPE_ORPH_SUBS = "__cti_oSubBuses__";
	public static final String TYPE_ORPH_SUBSTATIONS = "__cti_oSubstations__";
    public static final String TYPE_ORPH_FEEDERS = "__cti_oFeeders__";
	public static final String TYPE_ORPH_BANKS = "__cti_oBanks__";
	public static final String TYPE_ORPH_CBCS = "__cti_oCBCs__";
	public static final String TYPE_ORPH_REGULATORS = "__cti_oRegulators__";

}
