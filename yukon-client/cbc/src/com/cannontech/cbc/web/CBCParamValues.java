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
interface CBCParamValues
{
	
	public static final String TYPE_PF = "PF";
	public static final String TYPE_VARWATTS = "VW";
	public static final String TYPE_RECENT_CNTRLS = "RC";

	public static final String TYPE_ORPH_FEEDERS = "oFeeders";
	public static final String TYPE_ORPH_BANKS = "oBanks";
	public static final String TYPE_ORPH_CBCS = "oCBCs";


}
