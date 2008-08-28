package com.cannontech.stars.core.dao;

import java.util.List;
import java.util.Set;

import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;

public interface StarsInventoryBaseDao {

    public LiteInventoryBase getById(int inventoryId);
    
    public List<LiteInventoryBase> getByIds(Set<Integer> inventoryIds);

    /**
     * Method to get a list of all MCTs that have an account for a given list of energy companies
     * @param energyCompanyList - Companies to get hardware for
     * @return List of MCTs with assigned account
     */
    public List<LiteInventoryBase> getAllMCTsWithAccount(List<LiteStarsEnergyCompany> energyCompanyList);

    /**
     * Method to get a list of all MCTs that do not have an account for a given list of energy companies
     * @param energyCompanyList - Companies to get hardware for
     * @return List of MCTs without assigned account
     */
    public List<LiteInventoryBase> getAllMCTsWithoutAccount(List<LiteStarsEnergyCompany> energyCompanyList);

    /**
     * Method to get a list of all hardware for a given list of energy companies
     * @param energyCompanyList - Companies to get hardware for
     * @return List of hardware
     */
    public List<LiteStarsLMHardware> getAllLMHardware(List<LiteStarsEnergyCompany> energyCompanyList);

    /**
     * Method to get a list of all hardware without a load group for a given list of energy companies
     * @param energyCompanyList - Companies to get hardware for
     * @return List of hardware
     */
    public List<LiteStarsLMHardware> getAllLMHardwareWithoutLoadGroups(List<LiteStarsEnergyCompany> energyCompanyList);
    
    /**
     * @deprecated - Any call to this method should be refactored, loading all
     *               inventory from an EnergyCompany at one time is to heavy. 
     */
    @Deprecated
    public List<LiteInventoryBase> getAllByEnergyCompanyList(List<LiteStarsEnergyCompany> energyCompanyList);
    
}
