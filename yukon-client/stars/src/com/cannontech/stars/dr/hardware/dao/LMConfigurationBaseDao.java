package com.cannontech.stars.dr.hardware.dao;

public interface LMConfigurationBaseDao {

    /**
     * Deletes the LMConfigurationBase and its child tables
     * 
     * @param configurationId
     */
    public void delete(int configurationId);
}
