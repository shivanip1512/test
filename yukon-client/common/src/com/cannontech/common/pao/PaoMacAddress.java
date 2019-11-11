package com.cannontech.common.pao;

/**
 * A YukonPao that also includes a mac address string.
 */
public class PaoMacAddress implements YukonPao {
    private final PaoIdentifier paoIdentifier;
    private final String macAddress;
    
    public PaoMacAddress(PaoIdentifier paoIdentifier, String macAddress) {
        this.paoIdentifier = paoIdentifier;
        this.macAddress = macAddress;
    }
    
    public PaoMacAddress(PaoType paoType, int paoId, String macAddress) {
        paoIdentifier = new PaoIdentifier(paoId, paoType);
        this.macAddress = macAddress;
    }
    
    @Override
    public PaoIdentifier getPaoIdentifier() {
        return paoIdentifier;
    }
    
    public int getPaoId() {
        return paoIdentifier.getPaoId();
    }
    
    public PaoType getPaoType() {
        return paoIdentifier.getPaoType();
    }
    
    public String getMacAddress() {
        return macAddress;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("PaoMacAddress [paoIdentifier=")
                                  .append(paoIdentifier)
                                  .append(", macAddress=")
                                  .append(macAddress)
                                  .append("]")
                                  .toString();
    }
}
