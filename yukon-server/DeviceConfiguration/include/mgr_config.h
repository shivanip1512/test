/*-----------------------------------------------------------------------------*
*
* File:   mgr_config
*
* Date:   8/5/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/include/mgr_config.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2005/09/28 14:48:40 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __MGR_CONFIG_H__
#define __MGR_CONFIG_H__

#include "yukon.h"
#include "dllbase.h"

#include "logger.h"
#include "config_base.h"
#include "config_device.h"
#include "mgr_device.h"

using namespace Config;
class IM_EX_CONFIG CtiConfigManager
{


private:
    typedef CtiLockGuard<CtiMutex> LockGuard;
    mutable CtiMutex    _devMux;//For use with _deviceConfig map.

    typedef map<int,CtiConfigBaseSPtr>    ConfigTypeMap;
    typedef map<int,CtiConfigDeviceSPtr>  ConfigDeviceMap;

    CtiDeviceManager*   _devMgr;
    ConfigTypeMap       _typeConfig;
    ConfigDeviceMap     _deviceConfig;

    RWCString getConfigPartsTableName();
    RWCString getConfigValuesTableName();
    RWCString getConfigTypeTableName();
    RWCString getConfigDeviceTableName();
    CtiConfigBaseSPtr createConfigByType(const int &type);
    bool insertValueIntoConfigMap(const int &partID, const RWCString &value, const RWCString &key);

public:
    CtiConfigManager();
    ~CtiConfigManager();

    void refreshConfigurations();
    void setDeviceManager(CtiDeviceManager &mgr);

    CtiConfigDeviceSPtr getDeviceConfigFromID(int id);
    
};
#endif __MGR_CONFIG_H__
