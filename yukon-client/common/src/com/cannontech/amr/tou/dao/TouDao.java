package com.cannontech.amr.tou.dao;

import java.util.List;

import com.cannontech.amr.tou.model.TouAttributeMapper;

public interface TouDao {

    /**
     * gets the tou Attributes from the database.
     * @return
     */
    public List<TouAttributeMapper> getTouMappings();

}
