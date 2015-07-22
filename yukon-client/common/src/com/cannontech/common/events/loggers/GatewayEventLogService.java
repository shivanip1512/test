package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface GatewayEventLogService {
    
    @YukonEventLog(category="system.rfn.gateway")
    public void createdGateway(@Arg(ArgEnum.username) LiteYukonUser user,
                               @Arg(ArgEnum.paoName) String paoName,
                               @Arg(ArgEnum.serialNumber) String serialNumber,
                               @Arg(ArgEnum.ipAddress) String ipAddress,
                               String adminUser,
                               String superAdminUser);
    
    @YukonEventLog(category="system.rfn.gateway")
    public void createdGatewayAutomatically(@Arg(ArgEnum.paoName) String paoName,
                                            @Arg(ArgEnum.serialNumber) String serialNumber);
    
    @YukonEventLog(category="system.rfn.gateway")
    public void updatedGateway(@Arg(ArgEnum.username) LiteYukonUser user,
                               @Arg(ArgEnum.paoName) String paoName,
                               @Arg(ArgEnum.serialNumber) String serialNumber,
                               @Arg(ArgEnum.ipAddress) String ipAddress,
                               String adminUser,
                               String superAdminUser);
    
    @YukonEventLog(category="system.rfn.gateway")
    public void deletedGateway(@Arg(ArgEnum.username) LiteYukonUser user,
                               @Arg(ArgEnum.paoName) String paoName,
                               @Arg(ArgEnum.serialNumber) String serialNumber);
    
    @YukonEventLog(category="system.rfn.gateway")
    public void sentCertificateUpdate(@Arg(ArgEnum.username) LiteYukonUser user,
                                      String fileName,
                                      String certificateId,
                                      int gatewaysAffected);
    @YukonEventLog(category = "system.rfn.gateway")
    public void locationUpdated(@Arg(ArgEnum.username) LiteYukonUser user, @Arg(ArgEnum.paoName) String paoName,
            @Arg(ArgEnum.paoId) PaoIdentifier paoIdentifier, String latitude, String longitude, String origin);
}
