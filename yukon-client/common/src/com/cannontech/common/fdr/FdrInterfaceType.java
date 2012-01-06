package com.cannontech.common.fdr;

import java.util.Comparator;
import java.util.List;

import com.cannontech.common.util.DatabaseRepresentationSource;
import com.google.common.collect.Lists;

public enum FdrInterfaceType implements DatabaseRepresentationSource {
    INET(1, new FdrDirection[] {
            FdrDirection.SEND, 
            FdrDirection.SEND_FOR_CONTROL, 
            FdrDirection.RECEIVE, 
            FdrDirection.RECEIVE_FOR_CONTROL}),
    ACS(2, new FdrDirection[] {
            FdrDirection.SEND, 
            FdrDirection.SEND_FOR_CONTROL, 
            FdrDirection.RECEIVE, 
            FdrDirection.RECEIVE_FOR_CONTROL}),
    VALMET(3, new FdrDirection[] {
            FdrDirection.SEND, 
            FdrDirection.SEND_FOR_CONTROL, 
            FdrDirection.RECEIVE, 
            FdrDirection.RECEIVE_FOR_CONTROL, 
            FdrDirection.RECEIVE_FOR_ANALOG_OUTPUT}),
    CYGNET(4, new FdrDirection[] {
            FdrDirection.SEND, 
            FdrDirection.SEND_FOR_CONTROL, 
            FdrDirection.RECEIVE, 
            FdrDirection.RECEIVE_FOR_CONTROL}),
    STEC(5, new FdrDirection[] {
            FdrDirection.RECEIVE}),
    RCCS(6, new FdrDirection[] {
            FdrDirection.SEND, 
            FdrDirection.SEND_FOR_CONTROL, 
            FdrDirection.RECEIVE, 
            FdrDirection.RECEIVE_FOR_CONTROL}),
    TRISTATE(7, new FdrDirection[] {
            FdrDirection.RECEIVE}),
    RDEX(8, new FdrDirection[] {
            FdrDirection.SEND, 
            FdrDirection.SEND_FOR_CONTROL, 
            FdrDirection.RECEIVE, 
            FdrDirection.RECEIVE_FOR_CONTROL}),
    SYSTEM(9, new FdrDirection[] {
            FdrDirection.LINK_STATUS}),
    DSM2IMPORT(10, new FdrDirection[] {
            FdrDirection.RECEIVE, 
            FdrDirection.RECEIVE_FOR_CONTROL}),
    TELEGYR(11, new FdrDirection[] {
            FdrDirection.RECEIVE, 
            FdrDirection.RECEIVE_FOR_CONTROL}),
    TEXTIMPORT(12, new FdrDirection[] {
            FdrDirection.RECEIVE, 
            FdrDirection.RECEIVE_FOR_CONTROL, 
            FdrDirection.RECEIVE_FOR_ANALOG_OUTPUT}),
    TEXTEXPORT(13, new FdrDirection[] {
            FdrDirection.SEND}),       
    //ID 14 NOT USED
    //ID 15 NOT USED
    LODESTAR_STD(16, new FdrDirection[] {
            FdrDirection.RECEIVE}),
    LODESTAR_ENH(17, new FdrDirection[] {
            FdrDirection.RECEIVE}),
    DSM2FILEIN(18, new FdrDirection[] {
            FdrDirection.RECEIVE, 
            FdrDirection.RECEIVE_FOR_CONTROL}),
    XA21LM(19, new FdrDirection[] {
            FdrDirection.RECEIVE, 
            FdrDirection.SEND}),
    BEPC(20, new FdrDirection[] {
            FdrDirection.SEND}),
    PI(21, new FdrDirection[] {
            FdrDirection.RECEIVE}),
    LIVEDATA(22, new FdrDirection[] {
            FdrDirection.RECEIVE}),
    ACSMULTI(23, new FdrDirection[] {
            FdrDirection.SEND, 
            FdrDirection.SEND_FOR_CONTROL, 
            FdrDirection.RECEIVE, 
            FdrDirection.RECEIVE_FOR_CONTROL}),
    WABASH(24, new FdrDirection[] {
            FdrDirection.SEND}),
    TRISTATESUB(25, new FdrDirection[] {
            FdrDirection.RECEIVE, 
            FdrDirection.SEND}),
    OPC(26, new FdrDirection[] {
            FdrDirection.RECEIVE, 
            FdrDirection.SEND}),
    MULTISPEAK_LM(27, new FdrDirection[] {
            FdrDirection.RECEIVE}),
    DNPSLAVE(28, new FdrDirection[] {
            FdrDirection.SEND}),
    VALMETMULTI(29, new FdrDirection[] {
            FdrDirection.SEND, 
            FdrDirection.SEND_FOR_CONTROL, 
            FdrDirection.RECEIVE, 
            FdrDirection.RECEIVE_FOR_CONTROL, 
            FdrDirection.RECEIVE_FOR_ANALOG_OUTPUT}),
    ;
    
    private int position;
    private FdrDirection[] supportedDirections;
    
    //This comparator is designed to let us sort FdrInterfaceTypes by name, rather than natural enum order
    public static final Comparator<FdrInterfaceType> alphabeticalComparator = new Comparator<FdrInterfaceType>() {
        public int compare(FdrInterfaceType typeOne, FdrInterfaceType typeTwo) {
            return typeOne.name().compareTo(typeTwo.name());
        }
    };
    
    FdrInterfaceType(int position, FdrDirection[] supportedDirections) {
        this.position = position;
        this.supportedDirections = supportedDirections;
    }
    
    /**
     * @return the integer id representing this interface.
     */
    public int getValue() {
        return position;
    }

    /**
     * This or the lack of this indicates a special case where the Destination field is utilized 
     * instead of defaulted to the interface name.
     * @return true if this interface uses the special case where the Destination value is contained 
     * in the Options.
     */
    public boolean isDestinationInOptions() {
        return getDestinationOption() != null;
    }
    
    /**
     * @return the destination option if this interface has one, otherwise null.
     */
    public FdrInterfaceOption getDestinationOption() {
        List<FdrInterfaceOption> options = getInterfaceOptions();
        for(FdrInterfaceOption option : options) {
            if(option.isDestinationOption()) {
                return option;
            }
        }
        return null;
    }
    
    /**
     * @return an array containing all FdrDirections supported by this interface.
     */
    public FdrDirection[] getSupportedDirections() {
        return supportedDirections;
    }
    
    /**
     * @return a List of the FdrInterfaceOptions for this FdrInterfaceType.
     */
    public List<FdrInterfaceOption> getInterfaceOptions() {
        List<FdrInterfaceOption> optionList = Lists.newArrayList();
        FdrInterfaceOption[] options = FdrInterfaceOption.values();
        for(FdrInterfaceOption option : options) {
            if(option.getInterfaceType() == this) {
                optionList.add(option);
            }
        }
        return optionList;
    }
    
    /**
     * @return the interface with the specified integer ID, or null if the ID is invalid
     */
    public static FdrInterfaceType getById(int id) {
        for (FdrInterfaceType interfaceType : FdrInterfaceType.values()) {
            if (id == interfaceType.getValue()) {
                return interfaceType;
            }
        }
        return null;
    }
    
    public static List<FdrInterfaceType> valuesList() {
        return Lists.newArrayList(FdrInterfaceType.values());
    }
    
    public Object getDatabaseRepresentation() {
        return this.toString();
    }
}
