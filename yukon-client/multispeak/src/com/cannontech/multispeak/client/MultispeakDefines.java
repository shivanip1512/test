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
    public static final int MAX_RETURN_RECORDS = 10000;
    
	public static final String AMR_VENDOR = "Cannon";
    public static final String MSP_VENDOR = "MSP_VENDOR";
    public static final String MSP_RESULT_MSG = "MSP_RESULT_MSG";
    public static final String MSP_ERROR_MSG = "MSP_ERROR_MSG";
    public static final String[] MSP_INTERFACE_ARRAY = new String[]{
//        "CB_LM", "LM_CB",   //Interface 1
        "CB_MR", "MR_CB",   //Interface 2A
        "CB_CD", "CD_CB",   //Interface 2B
//        "CB_LP", "LP_CB",   //Interface 2C
//        "LM_SCADA", "SCADA_LM",   //Interface 3
        "MR_EA", "EA_MR",   //Interface 4
        "OA_OD", "OD_OA",   //Interface 5
//        "CH_OA", "OA_CH",   //Interface 6
//        "OA_GIS",   //batch only   //Interface 7
//        "EA_SCADA", "SCADA_EA",   //Interface 8
//        "OA_SCADA", "SCADA_OA",   //Interface 9
//        "EA_OA", "OA_EA",   //Interface 10
//        "FA_STAKING", "STAKING_FA",   //Interface 11
//        "SGV_GIS", "GIS_SGV",   //Interface 12
//        "WS_OA", //Omitted     //Interface 13
//        "CB_CH", "CH_CB",   //Interface 14
//        "CB_OA", "OA_CB",   //Interface 15
//        "GIS_CB", "CB_GIS",   //Interface 16
//        "WS_DGV",   //Omitted   //Interface 17
//        "DGV_OA", "OA_DGV",   //Interface 18
//        "CB_EA", "EA_CB",    //Interface 19
//        "CH_DGV",   //Omitted   //Interface 20
//        "STAKING_GIS", "GIS_STAKING",   //Interface 21
//        "EA_GIS", "GIS_EA",   //Interface 22
//        "SCADA_DGV", "DGV_SCADA",   //Interface 23
//        "GIS_SCADA", //Batch Only   //Interface 24
//        "CB_OD", "OD_CB",    //Interface 25
//        "DGV_OD", "OD_DGV",   //Interface 26
//        "MR_OA", "OA_MR",   //Interface 27
//        "CB_CRM",   //Batch Only   //Interface 28
//        "FA_CRM",   //Batch Only   //Interface 29
//        "GIS_LM", "LM_GIS"   //Interface 30
    };

    //SERVER interfaces
    public static final String MR_CB_STR = "MR_CB";
    public static final String MR_EA_STR = "MR_EA";
    public static final String MR_OA_STR = "MR_OA";
    public static final String CD_CB_STR = "CD_CB";
    public static final String OD_OA_STR = "OD_OA";
    
    //CLIENT only interfaces
    public static final String CB_MR_STR = "CB_MR";
    public static final String EA_MR_STR = "EA_MR";
    public static final String OA_MR_STR = "OA_MR";
    public static final String CB_CD_STR = "CB_CD";
    public static final String OA_OD_STR = "OA_OD";
    
    public static String[] getMSP_INTERFACE_ARRAY()
    {
        return MSP_INTERFACE_ARRAY;
    }
}
