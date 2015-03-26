package com.cannontech.web.stars.dr.operator.inventory.service;

import java.util.List;

import com.cannontech.stars.model.AssetReportDevice;

public interface AssetReportService {
    
    List<AssetReportDevice> getAssetReportDevices(int ecId, List<Integer> assetIds);
    
}