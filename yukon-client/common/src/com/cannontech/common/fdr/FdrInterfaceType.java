package com.cannontech.common.fdr;

import com.cannontech.common.i18n.DisplayableEnum;

public enum FdrInterfaceType implements DisplayableEnum {
    INET(1, FdrInterfaceOption.INET_DESTINATION_SOURCE),
    ACS(2, null),
    VALMET(3, null),
    CYGNET(4, null),
    STEC(5, null),
    RCCS(6, FdrInterfaceOption.RCCS_DESTINATION_SOURCE),
    TRISTATE(7, null),
    RDEX(8, FdrInterfaceOption.RDEX_DESTINATION_SOURCE),
    SYSTEM(9, null),
    DSM2IMPORT(10, null),
    TELEGYR(11, null),
    TEXTIMPORT(12, null),
    TEXTEXPORT(13, null),
    EMPTY(14, null),
    EMPTY2(15, null),
    LODESTAR_STD(16, null),
    LODESTAR_ENH(17, null),
    DSM2FILEIN(18, null),
    XA21LM(19, null),
    BEPC(20, null),
    PI(21, null),
    LIVEDATA(22, null),
    ACSMULTI(23, FdrInterfaceOption.ACSMULTI_DESTINATION_SOURCE),
    WABASH(24, null),
    TRISTATESUB(25, null),
    OPC(26, null),
    MULTISPEAK_LM(27, null),
    DNPSLAVE(28, FdrInterfaceOption.DNPSLAVE_DESTINATION_SOURCE),
    VALMETMULTI(29, FdrInterfaceOption.VALMETMULTI_DESTINATION_SOURCE);

    private final String keyPrefix = "yukon.web.modules.amr.fdrTranslationManagement.interfaces.";

    private int pos;
    //This or the lack of this indicates a special case where the Destination field is utilized instead of defaulted.
    private final FdrInterfaceOption destinationOption;

    FdrInterfaceType(int pos, FdrInterfaceOption destinationOption) {
        this.pos = pos;
        this.destinationOption = destinationOption;
    }

    public int getValue() {
        return pos;
    }

    /**
     * Returns true if this interface uses the special case where the Destination value is contained in the Options.
     * 
     * @return
     */
    public boolean isDestinationInOptions() {
        return destinationOption != null;
    }

    public FdrInterfaceOption getDestinationOption() {
        return destinationOption;
    }

    public static FdrInterfaceType getById(int id) {
        for (FdrInterfaceType interfaceType : FdrInterfaceType.values()) {
            if (id == interfaceType.getValue()) {
                return interfaceType;
            }
        }
        return null;
    }

    @Override
    public String getFormatKey() {
        return keyPrefix + name();
    }
}
