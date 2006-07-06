/*-----------------------------------------------------------------------------*
*
* File:   mgr_config
*
* Date:   8/5/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/include/mgr_config.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2006/07/06 20:32:25 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __MGR_CONFIG_H__
#define __MGR_CONFIG_H__

#include "yukon.h"
#include "dllbase.h"

#include "logger.h"
#include "mgr_device.h"
#include <map>


class IM_EX_DEVDB CtiConfigManager
{
private:
    typedef CtiLockGuard<CtiMutex>      LockGuard;
    //typedef set<long>                                   LongSet;
    typedef CtiSmartMap< Cti::Config::Base >            ConfigTypeMap;
    typedef std::map< long, std::set<long> >                 ConfigTypeToDeviceMap;
    typedef CtiSmartMap< Cti::Config::CtiConfigDevice > ConfigDeviceMap;

    CtiMutex              _mapMux;
    CtiDeviceManager*     _devMgr;
    ConfigTypeMap         _typeConfig;
    ConfigDeviceMap       _deviceConfig;
    ConfigTypeToDeviceMap _categoryToConfig;
    ConfigTypeToDeviceMap _configToCategory;

    string getConfigurationCategoryTableName();
    string getConfigDeviceTableName();
    string getItemValuesTableName();
    string getCategoryTypeTableName();
    string getItemTypeTableName();
    string getCategoryTableName();
    Cti::Config::BaseSPtr createConfigByType(const int type);
    bool insertValueIntoConfigMap(const long categoryID, const string &value, const string &valueid);

    void loadCategories(long ID = 0);
    void loadConfigs(long ID = 0);
    void updateDeviceConfigs(long configID = 0, long deviceID = 0);
    void removeFromMaps(long configID = 0, long categoryID = 0);

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
