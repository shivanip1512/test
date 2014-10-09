/*
 * Created on Aug 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.cannontech.multispeak.client;


/**
 * @author stacey
 */
public class MultispeakDefines
{
	public static final String AMR_VENDOR = "Cannon";
    public static final String MSP_VENDOR = "MSP_VENDOR";
    public static final String MSP_RESULT_MSG = "MSP_RESULT_MSG";

    //Default setup values
    public static int MSP_MAX_RETURN_RECORDS = 10000;
    public static long MSP_REQUEST_MESSAGE_TIMEOUT = 120000;
    public static long MSP_MAX_INITIATE_REQUEST_OBJECTS = 15;
    public static String MSP_TEMPLATE_NAME_DEFAULT = "*Default Template";
    
    //SERVER BUS Interfaces
    public static final String MR_Server_STR = "MR_Server";
    public static final String CD_Server_STR = "CD_Server";
    public static final String OD_Server_STR = "OD_Server";
    public static final String LM_Server_STR = "LM_Server";
    public static final String SCADA_Server_STR = "SCADA_Server";

    //CLIENT BUS Interfaces
    public static final String CB_Server_STR = "CB_Server";
    public static final String EA_Server_STR = "EA_Server";
    public static final String OA_Server_STR = "OA_Server";
    public static final String MDM_Server_STR = "MDM_Server";
    public static final String CB_CD_STR = "CB_CD";		//Still need to use this legacy guy.

    /** These are the Server side interfaces, the ones that Yukon supports.
     * Other systems may call these servers in Yukon
     */
    public static final String[] MSP_SERVER_INTERFACE_ARRAY = new String[]{
    	MR_Server_STR, OD_Server_STR, CD_Server_STR, LM_Server_STR, SCADA_Server_STR 
    };
    
    /** These are the Client side interface, the ones that Yukon may call
     * and are implmented by the other vendor.
     */
    public static final String[] MSP_CLIENT_INTERFACE_ARRAY = new String[]{
        CB_Server_STR, OA_Server_STR, EA_Server_STR, MDM_Server_STR, CB_CD_STR
    };
    
    public static String[] getMSP_SERVER_INTERFACE_ARRAY() {
		return MSP_SERVER_INTERFACE_ARRAY;
	}
    
    public static String[] getMSP_CLIENT_INTERFACE_ARRAY() {
		return MSP_CLIENT_INTERFACE_ARRAY;
	}
    
    public static String[] getPossibleInterfaces(MultispeakVendor mspVendor) {
        
        if (mspVendor != null && mspVendor.getVendorID() != null
                    && mspVendor.getVendorID() == MultispeakVendor.CANNON_MSP_VENDORID) {
            return MultispeakDefines.MSP_SERVER_INTERFACE_ARRAY;
        } else {
            return MultispeakDefines.MSP_CLIENT_INTERFACE_ARRAY;
        }
    }
}
