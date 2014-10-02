package com.cannontech.core.dao;

import java.util.List;
import java.util.Map;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.core.service.impl.PaoLoader;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public interface PaoDao {

    public YukonPao getYukonPao(int paoId);

    /**
     * Checks for existence of a unique pao with the given name and type.
     * 
     * @param paoName the name of the pao
     * @param paoType the type of the pao
     * @return A PaoIdentifier representing the PaoId and
     *         PaoType of the unique Pao if it exists or <code>null</code> if the object
     *         doesn't exist.
     */
    public YukonPao findYukonPao(String paoName, PaoType paoType);

    /**
     * Checks for existence of a unique pao with the given name, category, and class.
     * 
     * @param paoName the name of the pao
     * @param paoClass the class of the pao
     * @param paoCategory the category of the pao
     * @return A PaoIdentifier representing the PaoId and
     *         PaoType of the unique Pao if it exists or <code>null</code> if the object
     *         doesn't exist.
     */
    public YukonPao findYukonPao(String paoName, PaoCategory paoCategory, PaoClass paoClass);

    /**
     * Queries the database to return a PaoIdentifier for a unique pao that is assumed
     * to exist.
     * 
     * @param paoName The name of the Pao.
     * @param paoType The type of the Pao.
     * @return A PaoIdentifier representing the PaoId and PaoType of the pao specified.
     * @throws NotFoundException if no Pao with the specified information exists in the database.
     */
    public YukonPao getYukonPao(String paoName, PaoType paoType) throws NotFoundException;

    /**
     * Queries the database to return a PaoIdentifier for a unique pao that is assumed
     * to exist.
     * 
     * @param paoName The name of the Pao.
     * @param paoCategory The category of the Pao.
     * @param paoClass The class of the Pao.
     * @return A PaoIdentifier representing the PaoId and PaoType of the pao specified.
     * @throws NotFoundException if no Pao with the specified information exists in the database.
     */
    public YukonPao getYukonPao(String paoName, PaoCategory paoCategory, PaoClass paoClass) throws NotFoundException;

    /**
     * Retrieves the YukonPao object along with related data
     * like port, address and route info
     * 
     * @param paoID
     * @return
     */
    public LiteYukonPAObject getLiteYukonPAO(int paoID);

    /**
     * Get a map of LiteYukonPAObjects by their PaoIdentifier.
     */
    public Map<PaoIdentifier, LiteYukonPAObject> getLiteYukonPaosById(Iterable<PaoIdentifier> paos);

    /**
     * Helper method to return a paobject that exist for the paoName, category, paoClass.
     * These are the three fields that make up Unique Index Indx_PAO
     * Returns null if no object exists, otherwise returns the liteYukonPaobject that was found.
     * 
     * @param paoName
     * @param category
     * @param paoClass
     * @return true when no object is found for paoName, category, paoClass (via paoType)
     */
    public LiteYukonPAObject findUnique(String paoName, PaoType paoType);

    /**
     * Returns a list of lite pao objects by type
     *
     * @param paoType
     * @return
     * @see com.cannontech.database.data.pao.DeviceTypes
     */
    public List<LiteYukonPAObject> getLiteYukonPAObjectByType(PaoType paoType);

    public List<LiteYukonPAObject> getAllCapControlSubBuses();

    /**
     * Returns the next unused pa object id
     * 
     * @return
     */
    public int getNextPaoId();

    /**
     * This method was created in VisualAge.
     * 
     * @return String
     */
    public String getYukonPAOName(int paoID);

    public Map<Integer, String> getYukonPAONames(Iterable<Integer> ids);

    public LiteYukonPAObject[] getAllLiteRoutes();

    public LiteYukonPAObject[] getRoutesByType(PaoType... routeTypes);

    public List<LiteYukonPAObject> getLiteYukonPaoByName(String name, boolean partialMatch);

    public List<LiteYukonPAObject> getLiteYukonPaobjectsByAddress(int address);

    public List<PaoIdentifier> getPaosByAddressRange(int startAddress, int endAddress);

    public Map<Integer, PaoIdentifier> findPaoIdentifiersByCarrierAddress(Iterable<Integer> carrierAddresses);

    public Map<String, PaoIdentifier> findPaoIdentifiersByMeterNumber(Iterable<String> meterNumbers);

    public Map<String, PaoIdentifier> findPaoIdentifiersByName(Iterable<String> names);

    /**
     * Searches known routes for matching name.
     * Returns route's paoId if found, null otherwise.
     * 
     * @param routeName
     * @return
     */
    public Integer getRouteIdForRouteName(String routeName);

    public PaoLoader<DisplayablePao> getDisplayablePaoLoader();

    public List<PaoIdentifier> getPaoIdentifiersForPaoIds(Iterable<Integer> paoIds);

    /**
     * Returns true if the name is available for the PAO type's PAO class and PAO category
     */
    public boolean isNameAvailable(String paoName, PaoType paoType);

    /**
     * Returns the count of the disabled devices in a group
     * 
     * @param deviceGroup
     * @return
     */
    public int getDisabledDeviceCount(DeviceGroup deviceGroup);

    public List<PaoIdentifier> getAllPaoIdentifiersForTags(PaoTag paoTag, PaoTag... paoTags);

}
