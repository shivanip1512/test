/*-----------------------------------------------------------------------------*
*
* File:   mgr_config
*
* Date:   8/5/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/mgr_config.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2005/10/17 16:47:04 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <rw/db/db.h>
#include "dbaccess.h"
#include "config_device.h"
#include "config_parts.h"
#include "config_base.h"
#include "config_resolvers.h"
#include "mgr_config.h"





class RWCString;

using namespace Cti;
using namespace Config;

CtiConfigManager::CtiConfigManager()
{
}

CtiConfigManager::~CtiConfigManager()
{
}

void CtiConfigManager::refreshConfigurations()
{
    CtiConfigDeviceSPtr  pTempCtiConfigDevice;

    RWTime start, stop, querytime;

    _typeConfig.clear();
    _deviceConfig.clear();

    start = start.now();
    {   
        RWDBConnection conn = getConnection();
        RWDBDatabase db = getDatabase();
    
        RWDBSelector selector = db.selector();

        RWDBTable typeTbl = db.table( getConfigValuesTableName());
    
        selector << typeTbl["partid"]
            << typeTbl["value"]
            << typeTbl["valueid"]
            << typeTbl["rowid"];
    
        selector.from(typeTbl);

        RWDBTable confTbl = db.table(getConfigTypeTableName() );
    
        selector << confTbl["partid"]
            << confTbl["name"]
            << confTbl["type"];
    
        selector.from(confTbl);
       
        selector.where (confTbl["partid"] == typeTbl["partid"]);
    
        RWDBReader rdr = selector.reader(conn);
    
        while( (rdr.status().errorCode() == RWDBStatus::ok) && rdr() )
        {
            int partID;
            CtiConfig_type type;
            RWCString tempString,value,valueid;
    
            rdr[confTbl["partid"]]>>partID;
            rdr["type"] >>tempString;
            type = resolveConfigType(tempString);
    
            rdr["value"] >> value;
            rdr["valueid"] >> valueid;
    
            if(_typeConfig.find(partID)==_typeConfig.end() && type != ConfigTypeInvalid)//This key is not in the map yet
            {
                _typeConfig.insert(ConfigTypeMap::value_type(partID,createConfigByType(type)));//Should I remember this pointer and not do the next lookup? I dont think it is too expensive
            }

            if(!valueid.isNull() && !value.isNull())
            {
                try{
                    insertValueIntoConfigMap(partID, value, valueid);
                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << "*** CHECKPOINT *** " << " Exception Thrown, type "<<type<< " probably is invalid " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
    }
    stop = stop.now();
    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds to load config values " << endl;
    }

    start = start.now();
    {  
        RWDBConnection conn = getConnection();
        RWDBDatabase db = getDatabase();
    
        RWDBSelector selector = db.selector();
    
        RWDBTable typeTbl = db.table(getConfigPartsTableName() );
    
        selector << typeTbl["configid"]
            << typeTbl["partid"]
            << typeTbl["rowid"];
    
        selector.from(typeTbl);
       
        RWDBReader rdr = selector.reader(conn);

        while( (rdr.status().errorCode() == RWDBStatus::ok) && rdr() )
        {
            int partID, configID;
    
            rdr["configid"] >>configID;
            rdr["partid"]>>partID;

            ConfigDeviceMap::iterator deviceMapItr;

            LockGuard conf_guard(_devMux);

            if((deviceMapItr = _deviceConfig.find(configID))==_deviceConfig.end())//This key is not in the map yet
            {
                CtiConfigDeviceSPtr devPtr(CTIDBG_new CtiConfigDevice());
                ConfigDeviceMap::_Pairib tempPair = _deviceConfig.insert(ConfigDeviceMap::value_type(configID,devPtr));
                deviceMapItr = tempPair.first;
            }

            ConfigTypeMap::iterator typeMapItr;
            if((typeMapItr = _typeConfig.find(partID))!=_typeConfig.end())
            {
                deviceMapItr->second->insertConfig(typeMapItr->second);
            }

            if(_deviceConfig.find(configID)->second->getConfigFromType(typeMapItr->second->getType())  == typeMapItr->second)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Type was correctly added to device. " << endl;
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Type was NOT correctly added to device. " << endl;

            }
        }

    }
    stop = stop.now();
    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds to load config parts " << endl;
    }

    //ok, so in theory right now my 2 maps are built up.. although I should only need one of them
    //Now I want to give the device configs to the devices themselves

    start = start.now();
    {  
        RWDBConnection conn = getConnection();
        RWDBDatabase db = getDatabase();
    
        RWDBSelector selector = db.selector();
    
        RWDBTable typeTbl = db.table(getConfigDeviceTableName() );
    
        selector << typeTbl["deviceid"]
            << typeTbl["configid"];
    
        selector.from(typeTbl);
       
        RWDBReader rdr = selector.reader(conn);

        
        while( (rdr.status().errorCode() == RWDBStatus::ok) && rdr() )
        {
            int devID, configID;
    
            rdr["configid"] >>configID;
            rdr["deviceid"]>>devID;

            CtiDeviceSPtr pDev = _devMgr->getEqual(devID);

            ConfigDeviceMap::iterator deviceMapItr;

            LockGuard conf_guard(_devMux);//make this access more thread safe.
            
            if((deviceMapItr = _deviceConfig.find(configID))!=_deviceConfig.end() && pDev)//This key is not in the map
            {
                pDev->setDeviceConfig(deviceMapItr->second);
            }
    
        }

    }
    stop = stop.now();
    if(DebugLevel & 0x80000000 || stop.seconds() - start.seconds() > 5)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " " << stop.seconds() - start.seconds() << " seconds to assign pointers to devices " << endl;
    }



}

RWCString CtiConfigManager::getConfigPartsTableName()
{
    return "ConfigurationParts";
}

RWCString CtiConfigManager::getConfigValuesTableName()
{
    return "ConfigurationValue";
}

RWCString CtiConfigManager::getConfigDeviceTableName()
{
    return "DeviceConfiguration";
}

RWCString CtiConfigManager::getConfigTypeTableName()
{
    return "ConfigurationPartsName";
}

BaseSPtr CtiConfigManager::createConfigByType(const int type)
{   //This function MUST set the type variable in config_base.
    try
    {
        switch(type)
        {
            case ConfigTypeMCTTOU:
            {
                BaseSPtr tempBasePtr (CTIDBG_new ConfigurationPart<MCT_TOU>());
                return tempBasePtr;
            }
            case ConfigTypeMCTAddressing:
            {
                BaseSPtr tempBasePtr (CTIDBG_new ConfigurationPart<MCTAddressing>());
                return tempBasePtr;
            }
            case ConfigTypeMCTConfiguration:
            {
                BaseSPtr tempBasePtr (CTIDBG_new ConfigurationPart<MCTConfiguration>());
                return tempBasePtr;
            }
            case ConfigTypeMCTDemandLP:
            {
                BaseSPtr tempBasePtr (CTIDBG_new ConfigurationPart<MCTDemandLoadProfile>());
                return tempBasePtr;
            }
            case ConfigTypeMCTDST:
            {
                BaseSPtr tempBasePtr (CTIDBG_new ConfigurationPart<MCT_DST>());
                return tempBasePtr;
            }
            case ConfigTypeMCTVThreshold:
            {
                BaseSPtr tempBasePtr (CTIDBG_new ConfigurationPart<MCTVThreshold>());
                return tempBasePtr;
            }
            default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << "*** CHECKPOINT *** " << " No constructor for config type "<<type<< " in " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
                BaseSPtr tempBasePtr (CTIDBG_new Base());
                return tempBasePtr;
                break;//Im adding to code bloat!
            }
        }
        
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << "*** CHECKPOINT *** " << " Exception thrown in " << __FILE__ << " (" << __LINE__ << ")" << endl;
        return  BaseSPtr();
    }
}

bool CtiConfigManager::insertValueIntoConfigMap(const int partID, const RWCString &value, const RWCString &valueid)
{
    BaseSPtr    pTempCtiConfigBase;

    pTempCtiConfigBase = (_typeConfig.find(partID)->second);
    if(!pTempCtiConfigBase)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << "*** CHECKPOINT *** " << " No config loaded with partID "<<partID<< " in " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    else
    {
        int resolvedKey = pTempCtiConfigBase->getProtectedResolvedKey(valueid);
        if(resolvedKey == 0)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << "*** CHECKPOINT *** "<< " No resolver for " << valueid<<" in " << __FILE__ << " (" << __LINE__ << ")" << endl;
            return false;
        }
        return pTempCtiConfigBase->setProtectedValueWithKey(value,resolvedKey);
    }
    return false;

}

void CtiConfigManager::setDeviceManager(CtiDeviceManager &mgr)
{
    _devMgr = &mgr;
}

//Config ID that is.
CtiConfigDeviceSPtr CtiConfigManager::getDeviceConfigFromID(int ID)
{
    ConfigDeviceMap::iterator deviceMapItr;
    LockGuard conf_guard(_devMux);
    if((deviceMapItr = _deviceConfig.find(ID))!=_deviceConfig.end())//This key is in the map
    {
        return deviceMapItr->second;
    }
    else
    {
        return CtiConfigDeviceSPtr();
    }
}
