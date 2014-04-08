package com.cannontech.web.tools.mapping.dao.impl;

import java.util.Collection;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.web.tools.mapping.dao.LocationDao;
import com.cannontech.web.tools.mapping.model.Location;

// join on the YukonPAObject table for the pao type. 
// use PaoIdentifier paoIdentifier = rs.getPaoIdentifier("PaobjectId", "Type"); to get the PaoIdentifier
public class LocationDaoImpl implements LocationDao {

    @Autowired YukonJdbcTemplate template;

    @Override
    public Set<Location> getLastLocations(Collection<? extends YukonPao> paos) {
        return null;
    }

    @Override
    public Location getLastLocation(int paoId) {
        return null;
    }

    @Override
    public Set<Location> getAllLocations(int paoId) {
        return null;
    }

    @Override
    public void save(Location location) {
        // Probably a good opportunity to use SimpleTableAccessTemplate
    }

    @Override
    public void saveAll(Collection<Location> location) {
        // Use template.batchUpdate 
    }

}