package com.cannontech.database.data.lite;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;

public class LiteYukonPAObject extends LiteBase implements YukonPao {
    
    private String paoName;
    private PaoType paoType;
    private String paoDescription;
    private String disableFlag;
    private int portID = -1; // Only for devices that belong to a port
    private int routeID = -1; // Only for devices that have a route, (DeviceRoutes)
    private int address = -1; // Only for devices that have a physical address (DeviceCarrierStatistics)
    
    /**
     * Use this constructor ONLY when the full LiteYukonPaobject will be loaded
     * before using the object. For example: Constructing a LiteYukonPaobject to
     * call dbPersistent.retrieve() for is okay. The paoType MUST be set (either
     * explicitly or by a retrieve()) for correct usage of this object (required
     * by implements YukonPao).
     * 
     * @param paoId
     */
    public LiteYukonPAObject(int paoId) {
        super();
        setLiteID(paoId);
        setLiteType(LiteTypes.YUKON_PAOBJECT);
    }
    
    public LiteYukonPAObject() {
        super();
    }
    /**
     * @param paoId
     * @param paoName
     * @param paoType - paoCategory and paoClass will also be loaded from paoType
     * @param paoDescription
     * @param disableFlag
     */
    public LiteYukonPAObject(int paoId, String paoName, PaoType paoType, String paoDescription, String disableFlag) {
        this(paoId);
        setPaoName(paoName);
        setPaoType(paoType);
        setPaoDescription(paoDescription);
        setDisableFlag(disableFlag);
    }
    
    public LiteYukonPAObject(int paoId, String paoName, PaoCategory paoCategory, PaoClass paoClass, PaoType paoType, 
            String paoDescription, String disableFlag) {
        this(paoId);
        setPaoName(paoName);
        setPaoType(paoType);
        setCategory(paoCategory);
        setPaoClass(paoClass);
        setPaoDescription(paoDescription);
        setDisableFlag(disableFlag);
    }
    
    public String getPaoName() {
        return paoName;
    }
    
    public void setPaoName(String paoName) {
        this.paoName = paoName;
    }
    
    public int getPortID() {
        return portID;
    }
    
    public void setPortID(int portID) {
        this.portID = portID;
    }
    
    public PaoType getPaoType() {
        return paoType;
    }
    
    public void setPaoType(PaoType paoType) {
        this.paoType = paoType;
    }
    
    public int getYukonID() {
        return getLiteID();
    }
    
    public String getDisableFlag() {
        return disableFlag;
    }
    
    public void setDisableFlag(String disableFlag) {
        this.disableFlag = disableFlag;
    }
    
    public String getPaoDescription() {
        return paoDescription;
    }
    
    public void setPaoDescription(String paoDescription) {
        this.paoDescription = paoDescription;
    }
    
    public int getAddress() {
        return address;
    }
    
    public void setAddress(int address) {
        this.address = address;
    }
    
    public int getRouteID() {
        return routeID;
    }
    
    public void setRouteID(int routeID) {
        this.routeID = routeID;
    }
    
    /**
     * This setter does not actually set the paoCategory. paoType.paoCategory
     * will be used for paoCategory.
     * 
     * @param paoCategory
     */
    private void setCategory(PaoCategory paoCategory) {
        if (getPaoType().getPaoCategory() != paoCategory) {
            CTILogger.warn("PaoCategory (" + paoCategory + ") does not match PaoType (" + getPaoType() + ")");
        }
    }
    
    /**
     * This setter does not actually set the paoClass. paoType.paoClass will be
     * used for paoClass.
     * 
     * @param paoClass
     */
    private void setPaoClass(PaoClass paoClass) {
        if (getPaoType().getPaoClass() != paoClass) {
            CTILogger.warn("PaoClass (" + paoClass + ") does not match PaoType (" + getPaoType() + ")");
        }
    }
    
    @Override
    public PaoIdentifier getPaoIdentifier() {
        return new PaoIdentifier(getLiteID(), paoType);
    }
    
    @Override
    public String toString() {
        return getPaoName();
    }
    
}