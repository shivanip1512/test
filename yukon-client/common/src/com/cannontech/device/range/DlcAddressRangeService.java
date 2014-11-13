package com.cannontech.device.range;

import java.util.List;

import org.apache.commons.lang3.Range;

import com.cannontech.common.pao.PaoType;

public interface DlcAddressRangeService {
    
    /**
     * Method to get address ranges for display information.
     * In general this _should_ be the actual manufacturing ranges.
     * paoType may be allowed to have an address outside of returned range. 
     */
    public List<Range<Integer>> getAddressRangeForDevice(PaoType paoType);
    
    /**
     * Method to get address ranges for enforcing validation.
     * In general this _may_ be the actual manufacturing ranges,
     * but may also just be a field size (int, double, etc.) range limitation.
     * paoType should not be allowed to have an address outside of returned range.   
     */
    public List<Range<Integer>> getEnforcedAddressRangeForDevice(PaoType paoType);
    
    /**
     * Method to validate address for paoType, using enforced address range.
     */
    boolean isValidEnforcedAddress(PaoType paoType, int address);
    
    /**
     * Method to validate address for paoType, using NON enforced address range.
     */
    boolean isValidNonEnforcedAddress(PaoType paoType, int address);
    
    /** 
     * Creates string representation of a list of int ranges.
     * i.e. "[2 - 4]"
     * i.e. "[2 - 4], [40 -66]"
     * i.e. "[2 - 4], [40 -66], [24523, 24520580]"
     */
    String rangeString(List<Range<Integer>> ranges);
    
    /**
     * Calls {@link DlcAddressRangeService#rangeString(List)} for Enforced Range.
     * @param type
     * @return
     */
    String rangeStringEnforced(PaoType type);
    /** 
     * Creates string representation for a pao type.
     * i.e. "[2 - 4]"
     * i.e. "[2 - 4], [40 -66]"
     * i.e. "[2 - 4], [40 -66], [24523, 24520580]"
     */
    String rangeString(PaoType type);
    
}