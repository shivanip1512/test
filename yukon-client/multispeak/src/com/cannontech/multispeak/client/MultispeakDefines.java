/*
 * Created on Aug 17, 2005 TODO To change the template for this generated file
 * go to Window - Preferences - Java - Code Style - Code Templates
 */
package com.cannontech.multispeak.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

/**
 * @author stacey
 */
public class MultispeakDefines {
    public static final String NAMESPACE_v3 = "http://www.multispeak.org/Version_3.0";
    public static final String NAMESPACE_v5 = "http://www.multispeak.org/V5.0";
    public static final String COMPANY = "Company";
    public static final String APPNAME = "AppName";
    public static final String AMR_VENDOR = "Cannon";
    public static final String MSP_VENDOR = "MSP_VENDOR";
    public static final String MSP_RESULT_MSG = "MSP_RESULT_MSG";
    
    public static final String MSP_COMPANY_YUKON = "Cannon";
    public static final String MSP_APPNAME_YUKON = "Yukon";
    
    public static final String REGISTERED_NAME = "Eaton";
    public static final String FACILITY_NAME = "facilityName";

    // Default setup values
    public static int MSP_MAX_RETURN_RECORDS = 10000;
    public static long MSP_REQUEST_MESSAGE_TIMEOUT = 120000;
    public static long MSP_MAX_INITIATE_REQUEST_OBJECTS = 15;
    public static String MSP_TEMPLATE_NAME_DEFAULT = "*Default Template";

    // SERVER BUS Interfaces
    public static final String MR_Server_STR = "MR_Server";
    public static final String CD_Server_STR = "CD_Server";
    public static final String OD_Server_STR = "OD_Server";
    public static final String LM_Server_STR = "LM_Server";
    public static final String SCADA_Server_STR = "SCADA_Server";
    public static final String NOT_Server_STR = "NOT_Server";
    public static final String DR_Server_STR = "DR_Server";

    // CLIENT BUS Interfaces
    public static final String CB_Server_STR = "CB_Server";
    public static final String NOT_Server_DR_STR = "NOT_Server_DR";
    public static final String EA_Server_STR = "EA_Server";
    public static final String OA_Server_STR = "OA_Server";
    public static final String MDM_Server_STR = "MDM_Server";
    public static final String CB_CD_STR = "CB_CD"; // Still need to use this
                                                    // legacy guy.
    public static final String CUSTOMER_ID = "CustomerId"; 
    public static final String ACCOUNT_ID = "AccountId";
    // TODO : TO be tested if the SERVICE_LOCATION_ID carries a valid value
    public static final String SERVICE_LOCATION_ID = "ServiceLocationId";
    private static final List<Pair<String, MultiSpeakVersion>> MSP_SERVER_INTERFACES;
    private static final List<Pair<String, MultiSpeakVersion>> MSP_CLIENT_INTERFACES;

    static {
        /**
         * These are the Server side interfaces, the ones that Yukon supports. Other
         * systems may call these servers in Yukon
         */
        MSP_SERVER_INTERFACES = new ArrayList<>();
        MSP_SERVER_INTERFACES.add(MultispeakVendor.buildMapKey(MR_Server_STR, MultiSpeakVersion.V3));
        MSP_SERVER_INTERFACES.add(MultispeakVendor.buildMapKey(OD_Server_STR, MultiSpeakVersion.V3));
        MSP_SERVER_INTERFACES.add(MultispeakVendor.buildMapKey(CD_Server_STR, MultiSpeakVersion.V3));
        MSP_SERVER_INTERFACES.add(MultispeakVendor.buildMapKey(LM_Server_STR, MultiSpeakVersion.V3));
        MSP_SERVER_INTERFACES.add(MultispeakVendor.buildMapKey(SCADA_Server_STR, MultiSpeakVersion.V3));
        MSP_SERVER_INTERFACES.add(MultispeakVendor.buildMapKey(MR_Server_STR, MultiSpeakVersion.V5));
        MSP_SERVER_INTERFACES.add(MultispeakVendor.buildMapKey(OD_Server_STR, MultiSpeakVersion.V5));
        MSP_SERVER_INTERFACES.add(MultispeakVendor.buildMapKey(CD_Server_STR, MultiSpeakVersion.V5));
        MSP_SERVER_INTERFACES.add(MultispeakVendor.buildMapKey(DR_Server_STR, MultiSpeakVersion.V5));
        MSP_SERVER_INTERFACES.add(MultispeakVendor.buildMapKey(SCADA_Server_STR, MultiSpeakVersion.V5));
        MSP_SERVER_INTERFACES.add(MultispeakVendor.buildMapKey(NOT_Server_STR, MultiSpeakVersion.V5));

        /**
         * These are the Client side interface, the ones that Yukon may call and are
         * implmented by the other vendor.
         */
        MSP_CLIENT_INTERFACES = new ArrayList<>();
        MSP_CLIENT_INTERFACES.add(MultispeakVendor.buildMapKey(CB_Server_STR, MultiSpeakVersion.V3));
        MSP_CLIENT_INTERFACES.add(MultispeakVendor.buildMapKey(CB_Server_STR, MultiSpeakVersion.V5));
        MSP_CLIENT_INTERFACES.add(MultispeakVendor.buildMapKey(OA_Server_STR, MultiSpeakVersion.V3));
        MSP_CLIENT_INTERFACES.add(MultispeakVendor.buildMapKey(OA_Server_STR, MultiSpeakVersion.V5));
        MSP_CLIENT_INTERFACES.add(MultispeakVendor.buildMapKey(EA_Server_STR, MultiSpeakVersion.V3));
        MSP_CLIENT_INTERFACES.add(MultispeakVendor.buildMapKey(EA_Server_STR, MultiSpeakVersion.V5));
        MSP_CLIENT_INTERFACES.add(MultispeakVendor.buildMapKey(MDM_Server_STR, MultiSpeakVersion.V3));
        MSP_CLIENT_INTERFACES.add(MultispeakVendor.buildMapKey(MDM_Server_STR, MultiSpeakVersion.V5));
        MSP_CLIENT_INTERFACES.add(MultispeakVendor.buildMapKey(CB_CD_STR, MultiSpeakVersion.V3));
        MSP_CLIENT_INTERFACES.add(MultispeakVendor.buildMapKey(NOT_Server_STR, MultiSpeakVersion.V5));
        MSP_CLIENT_INTERFACES.add(MultispeakVendor.buildMapKey(NOT_Server_DR_STR, MultiSpeakVersion.V5));
    }

    public static List<Pair<String, MultiSpeakVersion>> getMSP_SERVER_INTERFACE_ARRAY() {
        return MSP_SERVER_INTERFACES;
    }

    public static List<Pair<String, MultiSpeakVersion>> getMSP_CLIENT_INTERFACE_ARRAY() {
        return MSP_CLIENT_INTERFACES;
    }

    public static List<Pair<String, MultiSpeakVersion>> getPossibleInterfaces(MultispeakVendor mspVendor) {

        if (mspVendor != null && mspVendor.getVendorID() != null
            && mspVendor.getVendorID() == MultispeakVendor.CANNON_MSP_VENDORID) {
            return MultispeakDefines.MSP_SERVER_INTERFACES;
        } else {
            return MultispeakDefines.MSP_CLIENT_INTERFACES;
        }
    }
}
