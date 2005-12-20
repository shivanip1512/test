/*-----------------------------------------------------------------------------*
*
* File:   mgr_config
*
* Date:   8/5/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/include/mgr_config.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2005/12/20 17:20:30 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __MGR_CONFIG_H__
#define __MGR_CONFIG_H__

#include "yukon.h"
#include "dllbase.h"

#include "logger.h"
#include "mgr_device.h"


using namespace Config;
class IM_EX_DEVDB CtiConfigManager
{
private:
    typedef CtiLockGuard<CtiMutex> LockGuard;
    mutable CtiMutex    _devMux;//For use with _deviceConfig map.

    typedef map<int,BaseSPtr>    ConfigTypeMap;
    typedef map<int,CtiConfigDeviceSPtr>  ConfigDeviceMap;

    CtiDeviceManager*   _devMgr;
    ConfigTypeMap       _typeConfig;
    ConfigDeviceMap     _deviceConfig;

    string getConfigPartsTableName();
    string getConfigValuesTableName();
    string getConfigTypeTableName();
    string getConfigDeviceTableName();
    BaseSPtr createConfigByType(const int type);
    bool insertValueIntoConfigMap(const int partID, const string &value, const string &valueid);

public:
    CtiConfigManager();
    ~CtiConfigManager();

    void refreshConfigurations();
    void setDeviceManager(CtiDeviceManager &mgr);

    CtiConfigDeviceSPtr getDeviceConfigFromID(int id);
    
};
#endif __MGR_CONFIG_H__
