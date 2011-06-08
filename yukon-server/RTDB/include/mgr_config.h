#pragma once

#include "dllbase.h"

#include "mgr_device.h"
#include "config_device.h"

class IM_EX_DEVDB CtiConfigManager
{
private:

    typedef CtiSmartMap< Cti::Config::DeviceConfig > ConfigDeviceMap;

    CtiDeviceManager*     _devMgr;
    ConfigDeviceMap       _deviceConfig;

    bool insertValueIntoConfig(Cti::Config::DeviceConfigSPtr config, const std::string &value, const std::string &valueid);

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
    void processDBUpdate(LONG identifer, std::string category, std::string objectType, int updateType);

    Cti::Config::DeviceConfigSPtr getDeviceConfigFromID(long configID);
};
