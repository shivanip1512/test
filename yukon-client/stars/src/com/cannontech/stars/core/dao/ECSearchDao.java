package com.cannontech.stars.core.dao;

import java.util.List;

import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;

public interface ECSearchDao {

    public List<LiteStarsCustAccountInformation> searchAddressByLocationAddress1(String locationAddress1, List<LiteStarsCustAccountInformation> accountInfoList);
    
}
