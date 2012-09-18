#include "precompiled.h"

#include "mgr_config.h"
#include "dbaccess.h"
#include "config_device.h"
#include "database_connection.h"
#include "database_reader.h"

using std::string;
using std::endl;

using namespace Cti;
using namespace Config;

CtiConfigManager::CtiConfigManager() :
    isInitialized(false),
    _devMgr(0)
{
}

CtiConfigManager::~CtiConfigManager()
{
    _devMgr = 0;
}

void CtiConfigManager::refreshConfigurations()
{
    CtiTime start, stop;

    _deviceConfig.removeAll(NULL,0);

    loadAllConfigs();
    loadAllConfigurationItems();

    //ok, so in theory right now my 2 maps are built up.. although I should only need one of them
    //Now I want to give the device configs to the devices themselves

    updateDeviceConfigs(NoConfigIdSpecified, NoDeviceIdSpecified);
}

//This function was created so the initialization only happens once. I dont like it and
//will have it replaced as soon as I think of something better.
void CtiConfigManager::initialize(CtiDeviceManager &mgr)
{
    if( !isInitialized )
    {
        setDeviceManager(mgr);
        refreshConfigurations();
        isInitialized = true;
    }
}

bool CtiConfigManager::insertValueIntoConfig(DeviceConfigSPtr config, const string &value, const string &valueid)
{
    if(!config)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << "*** CHECKPOINT *** " << " No config loaded " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    else
    {
        return config->insertValue(valueid, value);
    }
    return false;

}

void CtiConfigManager::setDeviceManager(CtiDeviceManager &mgr)
{
    _devMgr = &mgr;
}

//Config ID that is.
DeviceConfigSPtr CtiConfigManager::getDeviceConfigFromID(long configID)
{
    return _deviceConfig.find(configID);
}

void CtiConfigManager::processDBUpdate(LONG identifier, string category, string objectType, int updateType)
{
    CtiToLower(category);
    CtiToLower(objectType);
    if( category == "device config" && identifier != NoConfigIdSpecified )
    {
        if( objectType == "config" )
        {
            switch(updateType)
            {
            case ChangeTypeUpdate:
            case ChangeTypeAdd:
                {
                    removeFromMaps(identifier);
                    loadConfig(identifier);
                    loadConfigurationItems(identifier);
                    updateDeviceConfigs(identifier, NoDeviceIdSpecified);
                    break;
                }
            case ChangeTypeDelete:
                {
                    removeFromMaps(identifier);
                    _deviceConfig.remove(identifier);
                    break;
                }
            default:
                {
                    break;
                }
            }
        }
        else if( objectType == "device" )
        {
            switch(updateType)
            {
            case ChangeTypeUpdate:
            case ChangeTypeAdd:
                {
                    updateDeviceConfigs(NoConfigIdSpecified, identifier);
                    break;
                }
            case ChangeTypeDelete:
                {
                    CtiDeviceSPtr pDev = _devMgr->getDeviceByID(identifier);
                    if( pDev )
                    {
                        pDev->changeDeviceConfig(DeviceConfigSPtr());//set to null
                    }
                    break;
                }
            default:
                {
                    break;
                }
            }
        }
    }
    else if( category == "config" && identifier == NoConfigIdSpecified )
    {
        refreshConfigurations();
    }
}


void CtiConfigManager::refreshConfigForDeviceId(long deviceid)
{
    updateDeviceConfigs(NoConfigIdSpecified, deviceid);
}

void CtiConfigManager::loadConfigurationItems(long configID)
{
    const string sql =  "SELECT DCI.deviceconfigurationid, DCI.fieldname, DCI.value "
                        "FROM DeviceConfigurationItem DCI "
                        "WHERE DCI.deviceconfigurationid = " + CtiNumStr(configID) +
                        "ORDER BY DCI.deviceconfigurationid ASC";

    executeLoadItems(sql);
}

void CtiConfigManager::loadAllConfigurationItems()
{
    static const string sql = "SELECT DCI.deviceconfigurationid, DCI.fieldname, DCI.value "
                              "FROM DeviceConfigurationItem DCI "
                              "ORDER BY DCI.deviceconfigurationid ASC";

    executeLoadItems(sql);
}

void CtiConfigManager::executeLoadItems(const string &sql)
{
    CtiTime start, stop;

    start = start.now();
    {
        static const string sqlNoID = "SELECT DCI.deviceconfigurationid, DCI.fieldname, DCI.value "
                                      "FROM DeviceConfigurationItem DCI "
                                      "ORDER BY DCI.deviceconfigurationid ASC";

        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader rdr(connection, sql);

        rdr.execute();

        long deviceConfigID, oldDeviceConfigID = 0;
        DeviceConfigSPtr config;
        string value,valueName;

        while( rdr() )
        {
            rdr["deviceconfigurationid"] >> deviceConfigID;
            rdr["fieldname"] >> valueName;
            rdr["value"] >> value;

            if(!valueName.empty() && !value.empty())
            {
                try
                {
                    if( oldDeviceConfigID != deviceConfigID )
                    {
                        config = _deviceConfig.find(deviceConfigID);
                        oldDeviceConfigID = deviceConfigID;
                    }
                    insertValueIntoConfig(config, value, valueName);
                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << "*** CHECKPOINT *** " << " Exception Thrown " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << CtiTime() << "*** CHECKPOINT *** " << " Configs will NOT be properly loaded " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
    }
    stop = stop.now();
    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load config values " << endl;
    }
}

void CtiConfigManager::executeLoadConfig(const string &sql) 
{
    CtiTime start, stop;

    start = start.now();
    {
        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader rdr(connection, sql);

        rdr.execute();

        while( rdr() )
        {
            long cfgID;
            string name, type;

            rdr["deviceconfigurationid"] >> cfgID;
            rdr["name"] >> name;
            rdr["type"] >> type;

            DeviceConfigSPtr configDevSPtr = _deviceConfig.find(cfgID);

            if( !configDevSPtr )//This key is not in the map yet
            {
                DeviceConfigSPtr devPtr(CTIDBG_new DeviceConfig(cfgID, name, type));
                ConfigDeviceMap::insert_pair tempPair = _deviceConfig.insert(cfgID,devPtr);
                configDevSPtr = tempPair.first->second;
            }
        }
    }
    stop = stop.now();
    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load device configurations." << endl;
    }
}

void CtiConfigManager::loadAllConfigs()
{
    static const string sql =  "SELECT DCF.deviceconfigurationid, DCF.name, DCF.type "
                               "FROM DeviceConfiguration DCF";

    _deviceConfig.clear();

    executeLoadConfig(sql);
}

void CtiConfigManager::loadConfig(long configID)
{
    string sql =  "SELECT DCF.deviceconfigurationid, DCF.name, DCF.type "
                  "FROM DeviceConfiguration DCF ";
                  "WHERE DCF.deviceconfigurationid = " + CtiNumStr(configID);


    //Give us a fresh start
    _deviceConfig.remove(configID);

    executeLoadConfig(sql);
}

//Send configs to devices
void CtiConfigManager::updateDeviceConfigs(long configID, long deviceID)
{
    CtiTime start, stop;

    start = start.now();
    {
        static const string sql = "SELECT DCD.deviceid, DCD.deviceconfigurationid "
                                  "FROM DeviceConfigurationDeviceMap DCD";

        static const string sqlConfig = "SELECT DCD.deviceid, DCD.deviceconfigurationid "
                                        "FROM DeviceConfigurationDeviceMap DCD "
                                        "WHERE DCD.deviceconfigurationid = ?";

        static const string sqlDevice = "SELECT DCD.deviceid, DCD.deviceconfigurationid "
                                        "FROM DeviceConfigurationDeviceMap DCD "
                                        "WHERE DCD.deviceid = ?";

        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader rdr(connection);

        if( configID != NoConfigIdSpecified )
        {
            rdr.setCommandText(sqlConfig);
            rdr << configID;
        }
        else if( deviceID != NoDeviceIdSpecified )
        {
            rdr.setCommandText(sqlDevice);
            rdr << deviceID;
        }
        else
        {
            // There is no where clause to be added.
            rdr.setCommandText(sql);
        }

        rdr.execute();

        // If we specify a device, this may be the equivalent of a "delete"
        // If we also specified a config, we cant risk the "delete" operation,
        // As it would be too specific and could result in a delete that was not desired
        if( deviceID != NoDeviceIdSpecified && configID == NoConfigIdSpecified )
        {
            if( rdr() )
            {
                long devID, cfgID;

                rdr["deviceconfigurationid"] >>cfgID;
                rdr["deviceid"]>>devID;

                CtiDeviceSPtr pDev = _devMgr->getDeviceByID(devID);

                DeviceConfigSPtr tempSPtr;

                if( (tempSPtr = _deviceConfig.find(cfgID)) && pDev )
                {
                    pDev->changeDeviceConfig(tempSPtr);
                }
            }
            else
            {
                //This is a delete!
                CtiDeviceSPtr pDev = _devMgr->getDeviceByID(deviceID);
                if( pDev )
                {
                    pDev->changeDeviceConfig(DeviceConfigSPtr());
                }
            }

        }
        else
        {
            while( rdr() )
            {
                long devID, cfgID;

                rdr["deviceconfigurationid"] >>cfgID;
                rdr["deviceid"]>>devID;

                CtiDeviceSPtr pDev = _devMgr->getDeviceByID(devID);

                DeviceConfigSPtr tempSPtr;

                if( (tempSPtr = _deviceConfig.find(cfgID)) && pDev )
                {
                    pDev->changeDeviceConfig(tempSPtr);
                }
            }
        }
    }
    stop = stop.now();
    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to assign pointers to devices " << endl;
    }
}

void CtiConfigManager::removeFromMaps(long configID)
{
    _deviceConfig.remove(configID);
}
