package com.cannontech.web.stars.service;

import java.util.List;

import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.stars.gateway.model.CellularDeviceCommData;

public interface RfnCellularCommDataService {

    /**
     * @return CellularDeviceCommData Objects for a list of gateway Ids, commStatuses and cellTypes;
     * @param gatewayIds - A list of GatewayIds
     * @param commStauses - A list of Comm Status values to filter by
     * @param deviceTypes - A list of Cellular device types to filter by
     */
    List<CellularDeviceCommData> getCellularDeviceCommDataForGateways(List<Integer> gatewayIds, List<Integer> commStatuses, List<PaoType> deviceTypes);
    
    /**
     * @return CellularDeviceCommData Object for a Cellular Device
     * @param RfnDevice - a Cellular device
     */
    CellularDeviceCommData buildCellularDeviceCommDataObject(RfnDevice rfnDevice);


    /**
     * This method will send a "getstatus cell" command to devices of paoType Cellular
     * 
     * @param paoIdentifiers - A list of PaoIdentifiers
     * @param user - LiteYukonUser object
     */
    void refreshCellularDeviceConnection(List<PaoIdentifier> paoIdentifiers, LiteYukonUser user);
    
}
