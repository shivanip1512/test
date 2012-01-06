package com.cannontech.common.fdr;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;

/**
 * Class to represent the option values for each FDR interface.
 */
public enum FdrInterfaceOption {
    INET_DEVICE(FdrInterfaceType.INET, "Device", FdrOptionType.TEXT, false, null),
    INET_POINT(FdrInterfaceType.INET, "Point", FdrOptionType.TEXT, false, null),
    INET_DESTINATION_SOURCE(FdrInterfaceType.INET, "Destination/Source", FdrOptionType.TEXT, true, null),
    ACS_CATEGORY(FdrInterfaceType.ACS, "Category", FdrOptionType.COMBO, false, new String[] {"PSEUDO", "REAL", "CALCULATED"}),
    ACS_REMOTE(FdrInterfaceType.ACS, "Remote", FdrOptionType.TEXT, false, null),
    ACS_POINT(FdrInterfaceType.ACS, "Point", FdrOptionType.TEXT, false, null),
    VALMET_POINT(FdrInterfaceType.VALMET, "Point", FdrOptionType.TEXT, false, null),
    CYGNET_POINTID(FdrInterfaceType.CYGNET, "PointID", FdrOptionType.TEXT, false, null),
    STEC_POINT(FdrInterfaceType.STEC, "Point", FdrOptionType.COMBO, false, new String[] {"SYSTEM LOAD", "STEC LOAD"}),
    RCCS_DEVICE(FdrInterfaceType.RCCS, "Device", FdrOptionType.TEXT, false, null),
    RCCS_POINT(FdrInterfaceType.RCCS, "Point", FdrOptionType.TEXT, false, null),
    RCCS_DESTINATION_SOURCE(FdrInterfaceType.RCCS, "Destination/Source", FdrOptionType.TEXT, true, null),
    TRISTATE_POINT(FdrInterfaceType.TRISTATE, "Point", FdrOptionType.COMBO, false, new String[] {"SYSTEM LOAD", "30 Minute AVG"}),
    RDEX_TRANSLATION(FdrInterfaceType.RDEX, "Translation", FdrOptionType.TEXT, false, null),
    RDEX_DESTINATION_SOURCE(FdrInterfaceType.RDEX, "Destination/Source", FdrOptionType.TEXT, true, null),
    SYSTEM_CLIENT(FdrInterfaceType.SYSTEM, "Client", FdrOptionType.TEXT, false, null),
    DSM2IMPORT_POINT(FdrInterfaceType.DSM2IMPORT, "Point", FdrOptionType.TEXT, false, null),
    TELEGYR_POINT(FdrInterfaceType.TELEGYR, "Point", FdrOptionType.TEXT, false, null),
    TELEGYR_INTERVAL_SEC(FdrInterfaceType.TELEGYR, "Interval (sec)", FdrOptionType.TEXT, false, null),
    TEXTIMPORT_POINT_ID(FdrInterfaceType.TEXTIMPORT, "Point ID", FdrOptionType.TEXT, false, null),
    TEXTIMPORT_DRIVEPATH(FdrInterfaceType.TEXTIMPORT, "DrivePath", FdrOptionType.TEXT, false, null),
    TEXTIMPORT_FILENAME(FdrInterfaceType.TEXTIMPORT, "Filename", FdrOptionType.TEXT, false, null),
    TEXTEXPORT_POINT_ID(FdrInterfaceType.TEXTEXPORT, "Point ID", FdrOptionType.TEXT, false, null),
    LODESTAR_STD_CUSTOMER(FdrInterfaceType.LODESTAR_STD, "Customer", FdrOptionType.TEXT, false, null),
    LODESTAR_STD_CHANNEL(FdrInterfaceType.LODESTAR_STD, "Channel", FdrOptionType.TEXT, false, null),
    LODESTAR_STD_DRIVEPATH(FdrInterfaceType.LODESTAR_STD, "DrivePath", FdrOptionType.TEXT, false, null),
    LODESTAR_STD_FILENAME(FdrInterfaceType.LODESTAR_STD, "Filename", FdrOptionType.TEXT, false, null),
    LODESTAR_ENH_CUSTOMER(FdrInterfaceType.LODESTAR_ENH, "Customer", FdrOptionType.TEXT, false, null),
    LODESTAR_ENH_CHANNEL(FdrInterfaceType.LODESTAR_ENH, "Channel", FdrOptionType.TEXT, false, null),
    LODESTAR_ENH_DRIVEPATH(FdrInterfaceType.LODESTAR_ENH, "DrivePath", FdrOptionType.TEXT, false, null),
    LODESTAR_ENH_FILENAME(FdrInterfaceType.LODESTAR_ENH, "Filename", FdrOptionType.TEXT, false, null),
    DSM2FILEIN_OPTION_NUMBER(FdrInterfaceType.DSM2FILEIN, "Option Number", FdrOptionType.COMBO, false, new String[] {"1"}),
    DSM2FILEIN_POINT_ID(FdrInterfaceType.DSM2FILEIN, "Point ID", FdrOptionType.TEXT, false, null),
    XA21LM_TRANSLATION(FdrInterfaceType.XA21LM, "Translation", FdrOptionType.TEXT, false, null),
    BEPC_COOP_ID(FdrInterfaceType.BEPC, "Coop Id", FdrOptionType.TEXT, false, null),
    BEPC_FILENAME(FdrInterfaceType.BEPC, "Filename", FdrOptionType.TEXT, false, null),
    PI_TAG_NAME(FdrInterfaceType.PI, "Tag Name", FdrOptionType.TEXT, false, null),
    PI_PERIOD_SEC(FdrInterfaceType.PI, "Period (sec)", FdrOptionType.TEXT, false, null),
    LIVEDATA_ADDRESS(FdrInterfaceType.LIVEDATA, "Address", FdrOptionType.TEXT, false, null),
    LIVEDATA_DATA_TYPE(FdrInterfaceType.LIVEDATA, "Data Type", FdrOptionType.COMBO, false, new String[] {"Data_RealExtended", "Data_DiscreteExtended", "Data_StateExtended", "Data_RealQ", "Data_DiscreteQ", "Data_State", "Data_Discrete", "Data_Real", "Data_RealQTimeTag", "Data_StateQTimeTag", "Data_DiscreteQTimeTag"}),
    ACSMULTI_CATEGORY(FdrInterfaceType.ACSMULTI, "Category", FdrOptionType.COMBO, false, new String[] {"PSEUDO", "REAL", "CALCULATED"}),
    ACSMULTI_REMOTE(FdrInterfaceType.ACSMULTI, "Remote", FdrOptionType.TEXT, false, null),
    ACSMULTI_POINT(FdrInterfaceType.ACSMULTI, "Point", FdrOptionType.TEXT, false, null),
    ACSMULTI_DESTINATION_SOURCE(FdrInterfaceType.ACSMULTI, "Destination/Source", FdrOptionType.TEXT, true, null),
    WABASH_SCHEDNAME(FdrInterfaceType.WABASH, "SchedName", FdrOptionType.TEXT, false, null),
    WABASH_PATH(FdrInterfaceType.WABASH, "Path", FdrOptionType.TEXT, false, new String[] {"c:\\yukon\\server\\export"}),
    WABASH_FILENAME(FdrInterfaceType.WABASH, "Filename", FdrOptionType.TEXT, false, new String[] {"control.txt"}),
    TRISTATESUB_POINT(FdrInterfaceType.TRISTATESUB, "Point", FdrOptionType.COMBO, false, new String[]{"Nucla 115/69 Xfmr.", "Happy Canyon 661Idarado", "Cascade 115/69 (T2)", "Ames Generation", "Dallas Creek MW", "Dallas Creek MV"}),
    OPC_SERVER_NAME(FdrInterfaceType.OPC, "Server Name", FdrOptionType.TEXT, false, null),
    OPC_OPC_GROUP(FdrInterfaceType.OPC, "OPC Group", FdrOptionType.TEXT, false, null),
    OPC_OPC_ITEM(FdrInterfaceType.OPC, "OPC Item", FdrOptionType.TEXT, false, null),
    MULTISPEAK_LM_OBJECTID(FdrInterfaceType.MULTISPEAK_LM, "ObjectId", FdrOptionType.TEXT, false, null),
    DNPSLAVE_MASTERID(FdrInterfaceType.DNPSLAVE, "MasterId", FdrOptionType.TEXT, false, null),
    DNPSLAVE_SLAVEID(FdrInterfaceType.DNPSLAVE, "SlaveId", FdrOptionType.TEXT, false, null),
    DNPSLAVE_OFFSET(FdrInterfaceType.DNPSLAVE, "Offset", FdrOptionType.TEXT, false, null),
    DNPSLAVE_DESTINATION_SOURCE(FdrInterfaceType.DNPSLAVE, "Destination/Source", FdrOptionType.TEXT, true, null),
    DNPSLAVE_MULTIPLIER(FdrInterfaceType.DNPSLAVE, "Multiplier", FdrOptionType.TEXT, false, new String[]{"1.0"}),
    VALMETMULTI_POINT(FdrInterfaceType.VALMETMULTI, "Point", FdrOptionType.TEXT, false, null),
    VALMETMULTI_DESTINATION_SOURCE(FdrInterfaceType.VALMETMULTI, "Destination/Source", FdrOptionType.TEXT, true, null),
    VALMETMULTI_PORT(FdrInterfaceType.VALMETMULTI, "Port", FdrOptionType.TEXT, false, null);
    
    private final FdrInterfaceType interfaceType;
    private final String optionLabel;
    private final FdrOptionType optionType;
    private final String[] optionValues;
    private final boolean isDestinationOption;
    
    private FdrInterfaceOption(FdrInterfaceType interfaceType, String optionLabel, FdrOptionType optionType, boolean isDestinationOption, String[] optionValues) {
        this.interfaceType = interfaceType;
        this.optionLabel = optionLabel;
        this.optionType = optionType;
        this.isDestinationOption = isDestinationOption;
        this.optionValues = optionValues;
    }
    
    public FdrInterfaceType getInterfaceType() {
        return this.interfaceType;
    }
    
    public String getOptionLabel() {
        return this.optionLabel;
    }
    
    public FdrOptionType getOptionType() {
        return this.optionType;
    }
    
    public String[] getOptionValues() {
        return optionValues;
    }
    
    public String returnOptionValuesString() {
        return StringUtils.join(optionValues, ", ");
    }
    
    public boolean isDestinationOption() {
        return isDestinationOption;
    }
    
    public static List<FdrInterfaceOption> getByInterface(FdrInterfaceType interfaceType) {
        List<FdrInterfaceOption> optionList = Lists.newArrayList();
        FdrInterfaceOption[] options = values();
        for(FdrInterfaceOption option : options) {
            if(option.getInterfaceType() == interfaceType) {
                optionList.add(option);
            }
        }
        return optionList;
    }
}
