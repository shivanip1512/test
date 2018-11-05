package com.cannontech.core.dao;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.dao.DataAccessException;

import com.cannontech.database.data.lite.LiteAddress;

public interface AddressDao {

    boolean add(LiteAddress address);

    boolean remove(LiteAddress address);

    boolean update(LiteAddress address);

    LiteAddress getByAddressId(int addressId) throws DataAccessException;

    Map<Integer, LiteAddress> getAddresses(List<Integer> addressIdList);

    List<LiteAddress> getAll();

    void remove(int addressId);

    /**
     * Returns a map of address site ids to addresses where the first address line is empty
     */
    Map<Integer, LiteAddress> getEmptyAddresses(Set<Integer> siteIds);

}
