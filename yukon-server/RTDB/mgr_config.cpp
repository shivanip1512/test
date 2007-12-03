/*-----------------------------------------------------------------------------*
*
* File:   mgr_config
*
* Date:   8/5/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/mgr_config.cpp-arc  $
* REVISION     :  $Revision: 1.16 $
* DATE         :  $Date: 2007/12/03 22:19:41 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "mgr_config.h"
#include <rw/db/db.h>
#include "dbaccess.h"
#include "config_device.h"
#include "rwutil.h"

using std::string;

using namespace Cti;
using namespace Config;

CtiConfigManager::CtiConfigManager() : isInitialized(false)
{
}

CtiConfigManager::~CtiConfigManager()
{
}

void CtiConfigManager::refreshConfigurations()
{
    CtiTime start, stop;

    _deviceConfig.removeAll(NULL,0);

    loadConfigs();
    loadData();

    //ok, so in theory right now my 2 maps are built up.. although I should only need one of them
    //Now I want to give the device configs to the devices themselves

    updateDeviceConfigs();
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

string CtiConfigManager::getConfigTableName()
{
    return "DeviceConfiguration";
}

string CtiConfigManager::getConfigItemTableName()
{
    return "DeviceConfigurationItem";
}

string CtiConfigManager::getConfigToDeviceMapTableName()
{
    return "DeviceConfigurationDeviceMap";
}

bool CtiConfigManager::insertValueIntoConfig(CtiConfigDeviceSPtr config, const string &value, const string &valueid)
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
CtiConfigDeviceSPtr CtiConfigManager::getDeviceConfigFromID(long configID)
{
    CtiConfigDeviceSPtr tempSPtr;
    tempSPtr = _deviceConfig.find(configID);

    if(tempSPtr)//This key is in the map
    {
        return tempSPtr;
    }
    else
    {
        return CtiConfigDeviceSPtr();
    }
}

void CtiConfigManager::processDBUpdate(LONG identifier, string category, string objectType, int updateType)
{
    CtiToLower(category);
    CtiToLower(objectType);
    if( category == "device config" && identifier != 0 )
    {
        if( objectType == "config" )
        {
            switch(updateType)
            {
            case ChangeTypeUpdate:
            case ChangeTypeAdd:
                {
                    removeFromMaps(identifier);
                    loadConfigs(identifier);
                    loadData(identifier);
                    updateDeviceConfigs(identifier);
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
                    updateDeviceConfigs(0, identifier);
                    break;
                }
            case ChangeTypeDelete:
                {
                    CtiDeviceSPtr pDev = _devMgr->getEqual(identifier);
                    if( pDev )
                    {
                        pDev->setDeviceConfig(CtiConfigDeviceSPtr());//set to null
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
    else if( category == "config" && identifier == 0 )
    {
        refreshConfigurations();
    }
}

void CtiConfigManager::loadData(long configID)
{
    CtiTime start, stop;

    start = start.now();
    {   
        RWDBConnection conn = getConnection();
        RWDBDatabase db = getDatabase();
    
        RWDBSelector selector = db.selector();

        RWDBTable itemValTbl = db.table( string2RWCString(getConfigItemTableName()) );
        selector //<< itemValTbl["deviceconfigurationitemid"]
            << itemValTbl["deviceconfigurationid"]
            << itemValTbl["fieldname"]
            << itemValTbl["value"];
        selector.from(itemValTbl);

        if( configID != 0 )
        {
            selector.where(itemValTbl["deviceconfigurationid"] == configID && selector.where());
        }
        selector.orderBy(itemValTbl["deviceconfigurationid"]); //This should make loading faster.

        RWDBReader rdr = selector.reader(conn);
        long oldDeviceConfigID = 0;
        CtiConfigDeviceSPtr config;
        long deviceConfigID;
        string value,valueName;

        while( (rdr.status().errorCode() == RWDBStatus::ok) && rdr() )
        {
            //rdr["deviceconfigurationitemid"] >> itemID;
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

void CtiConfigManager::loadConfigs(long configID)
{
    CtiTime start, stop;

    if( configID != 0 )
    {
        _deviceConfig.remove(configID);//Give us a fresh start
    }

    start = start.now();
    {  
        RWDBConnection conn = getConnection();
        RWDBDatabase db = getDatabase();
    
        RWDBSelector selector = db.selector();
    
        RWDBTable configTbl = db.table( string2RWCString(getConfigTableName()) );
    
        selector << configTbl["deviceconfigurationid"]
            << configTbl["name"]
            << configTbl["type"];;
    
        selector.from(configTbl);
        if( configID != 0 )
        {
            selector.where( configTbl["deviceconfigurationid"] == configID && selector.where() );
        }
       
        RWDBReader rdr = selector.reader(conn);

        while( (rdr.status().errorCode() == RWDBStatus::ok) && rdr() )
        {
            long configID;
            string name, type;
    
            rdr["deviceconfigurationid"] >> configID;
            rdr["name"] >> name;
            rdr["type"] >> type;

            CtiConfigDeviceSPtr configDevSPtr = _deviceConfig.find(configID);

            if( !configDevSPtr )//This key is not in the map yet
            {
                CtiConfigDeviceSPtr devPtr(CTIDBG_new CtiConfigDevice(configID, name, type));
                ConfigDeviceMap::insert_pair tempPair = _deviceConfig.insert(configID,devPtr);
                configDevSPtr = tempPair.first->second;
            }
        }

    }
    stop = stop.now();
    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds to load config parts " << endl;
    }
}

//Send configs to devices
void CtiConfigManager::updateDeviceConfigs(long configID, long deviceID)
{
    CtiTime start, stop;

    start = start.now();
    {  
        RWDBConnection conn = getConnection();
        RWDBDatabase db = getDatabase();
    
        RWDBSelector selector = db.selector();
    
        RWDBTable typeTbl = db.table(string2RWCString(getConfigToDeviceMapTableName()) );
    
        selector << typeTbl["deviceid"]
            << typeTbl["deviceconfigurationid"];
    
        selector.from(typeTbl);
        if( configID != 0 )
        {
            selector.where(typeTbl["deviceconfigurationid"] == configID && selector.where());
        }
        else if( deviceID != 0 )
        {
            selector.where(typeTbl["deviceid"] == deviceID && selector.where());
        }
       
        RWDBReader rdr = selector.reader(conn);

        // If we specify a device, this may be the equivalent of a "delete"
        // If we also specified a config, we cant risk the "delete" operation,
        // As it would be too specific and could result in a delete that was not desired
        if( deviceID != 0 && configID == 0 )
        {
            if( rdr() )
            {
                long devID, configID;
    
                rdr["deviceconfigurationid"] >>configID;
                rdr["deviceid"]>>devID;
    
                CtiDeviceSPtr pDev = _devMgr->getEqual(devID);
    
                CtiConfigDeviceSPtr tempSPtr;
    
                if( (tempSPtr = _deviceConfig.find(configID)) && pDev )
                {
                    pDev->setDeviceConfig(tempSPtr);
                }    
            }
            else
            {
                //This is a delete!
                CtiDeviceSPtr pDev = _devMgr->getEqual(deviceID);
                if( pDev )
                {
                    pDev->setDeviceConfig(CtiConfigDeviceSPtr());
                }  
            }
            
        }
        else
        {
            while( (rdr.status().errorCode() == RWDBStatus::ok) && rdr() )
            {
                long devID, configID;
        
                rdr["deviceconfigurationid"] >>configID;
                rdr["deviceid"]>>devID;
    
                CtiDeviceSPtr pDev = _devMgr->getEqual(devID);
    
                CtiConfigDeviceSPtr tempSPtr;
    
                if( (tempSPtr = _deviceConfig.find(configID)) && pDev )
                {
                    pDev->setDeviceConfig(tempSPtr);
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
    if( configID )
    {
        _deviceConfig.remove(configID);
    }
}
