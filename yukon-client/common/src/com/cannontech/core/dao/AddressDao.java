package com.cannontech.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;

import com.cannontech.database.data.lite.LiteAddress;

public interface AddressDao {

    public boolean add(LiteAddress address);
    
    public boolean remove(LiteAddress address);
    
    public boolean update(LiteAddress address);
    
    public LiteAddress getByAddressId(int addressId) throws DataAccessException;
    
    public Map<Integer,LiteAddress> getAddresses(List<Integer> addressIdList);
    
    public List<LiteAddress> getByCityName(String cityName);
    
    public List<LiteAddress> getByStateCode(String stateCode);
    
    public List<LiteAddress> getByZipCode(String zipCode);
    
    public List<LiteAddress> getByCounty(String county);
    
    public List<LiteAddress> getAll();
    
}
