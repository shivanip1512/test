package com.cannontech.amr.tou.dao;

import java.util.List;

import com.cannontech.amr.tou.model.TouAttributeMapping;

public interface TouDao {

    /**
     * gets the tou Attributes from the database.
     * @return
     */
    public List<TouAttributeMapping> getTouMappings();

}
