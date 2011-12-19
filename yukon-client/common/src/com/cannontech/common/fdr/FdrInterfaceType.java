package com.cannontech.common.fdr;

import java.util.Comparator;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum FdrInterfaceType implements DatabaseRepresentationSource {
    INET(1, true, FdrInterfaceOption.INET_DESTINATION_SOURCE),
    ACS(2, true, null),
    VALMET(3, true, null),
    CYGNET(4, true, null),
    STEC(5, true, null),
    RCCS(6, true, FdrInterfaceOption.RCCS_DESTINATION_SOURCE),
    TRISTATE(7, true, null),
    RDEX(8, true, FdrInterfaceOption.RDEX_DESTINATION_SOURCE),
    SYSTEM(9, true, null),
    DSM2IMPORT(10, true, null),
    TELEGYR(11, true, null),
    TEXTIMPORT(12, true, null),
    TEXTEXPORT(13, true, null),
    EMPTY(14, false, null),
    EMPTY2(15, false, null),
    LODESTAR_STD(16, true, null),
    LODESTAR_ENH(17, true, null),
    DSM2FILEIN(18, true, null),
    XA21LM(19, true, null),
    BEPC(20, true, null),
    PI(21, true, null),
    LIVEDATA(22, true, null),
    ACSMULTI(23, true, FdrInterfaceOption.ACSMULTI_DESTINATION_SOURCE),
    WABASH(24, true, null),
    TRISTATESUB(25, true, null),
    OPC(26, true, null),
    MULTISPEAK_LM(27, true, null),
    DNPSLAVE(28, true, FdrInterfaceOption.DNPSLAVE_DESTINATION_SOURCE),
    VALMETMULTI(29, true, FdrInterfaceOption.VALMETMULTI_DESTINATION_SOURCE);

    private boolean display;
    private int pos;
    //This or the lack of this indicates a special case where the Destination field is utilized instead of defaulted.
    private final FdrInterfaceOption destinationOption;
    //This comparator is designed to let us sort FdrInterfaceTypes by name, rather than natural enum order
    public static final Comparator<FdrInterfaceType> alphabeticalComparator = new Comparator<FdrInterfaceType>() {
        public int compare(FdrInterfaceType typeOne, FdrInterfaceType typeTwo) {
            return typeOne.name().compareTo(typeTwo.name());
        }
    };
    
    FdrInterfaceType(int pos, boolean display, FdrInterfaceOption destinationOption) {
        this.pos = pos;
        this.display = display;
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
    
    /**
     * Returns false if the interface should not be displayed in UI lists, etc.
     */
    public boolean isDisplayed() {
        return display;
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
    
    public Object getDatabaseRepresentation() {
        return this.toString();
    }
}
