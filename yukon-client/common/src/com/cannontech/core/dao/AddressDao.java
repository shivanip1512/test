package com.cannontech.core.dao;

import java.util.List;
import java.util.Map;

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

}
