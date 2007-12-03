/*-----------------------------------------------------------------------------*
*
* File:   mgr_config
*
* Date:   8/5/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/include/mgr_config.h-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2007/12/03 22:19:41 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __MGR_CONFIG_H__
#define __MGR_CONFIG_H__

#include "yukon.h"
#include "dllbase.h"

#include "logger.h"
#include "mgr_device.h"
#include "config_device.h"
#include <map>


class IM_EX_DEVDB CtiConfigManager
{
private:
    typedef CtiLockGuard<CtiMutex>      LockGuard;
    //typedef set<long>                                   LongSet;
    typedef CtiSmartMap< Cti::Config::CtiConfigDevice > ConfigDeviceMap;

    CtiMutex              _mapMux;
    CtiDeviceManager*     _devMgr;
    ConfigDeviceMap       _deviceConfig;

    string getConfigTableName();
    string getConfigItemTableName();
    string getConfigToDeviceMapTableName();
    bool insertValueIntoConfig(Cti::Config::CtiConfigDeviceSPtr config, const string &value, const string &valueid);

    void loadData(long ID = 0);
    void loadConfigs(long ID = 0);
    void updateDeviceConfigs(long configID = 0, long deviceID = 0);
    void removeFromMaps(long configID = 0);

    void refreshConfigurations();
    void setDeviceManager(CtiDeviceManager &mgr);

    bool isInitialized;

public:
    CtiConfigManager();
    ~CtiConfigManager();

    void initialize(CtiDeviceManager &mgr);
    void processDBUpdate(LONG identifer, string category, string objectType, int updateType);

    Cti::Config::CtiConfigDeviceSPtr getDeviceConfigFromID(long configID);
    
};
#endif __MGR_CONFIG_H__
