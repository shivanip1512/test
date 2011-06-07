package com.cannontech.device.range;

import com.cannontech.common.pao.PaoType;

public interface DlcAddressRangeService {
    
	/**
	 * Method to get Address Range for display information.
	 * In general this _should_ be the actual manufacturing ranges.
	 * paoType may be allowed to have an address outside of returned range. 
	 * @param paoType
	 * @return
	 */
    public IntegerRange getAddressRangeForDevice(PaoType paoType);

    /**
     * Method to get Address Range for enforcing validation.
     * In general this _may_ be the actual manufacturing ranges,
     *  but may also just be a field size (int, double, etc.) range limitation.
     * paoType should not be allowed to have an address outside of returned range.   
     * @param paoType
     * @return
     */
	public IntegerRange getEnforcedAddressRangeForDevice(PaoType paoType);
    
	/**
	 * Method to validate address for paoType, using enforced address range.
	 * @param paoType
	 * @param address
	 * @return
	 */
    public boolean isValidAddress(PaoType paoType, int address);
}
