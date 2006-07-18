/*-----------------------------------------------------------------------------*
*
* File:   mgr_config
*
* Date:   8/5/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/mgr_config.cpp-arc  $
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2006/07/18 15:26:12 $
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

    _typeConfig.removeAll(NULL,0);
    _deviceConfig.removeAll(NULL,0);
    {
        LockGuard map_guard(_mapMux);
        _categoryToConfig.clear();
        _configToCategory.clear();
    }

    loadCategories();

    loadConfigs();

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

string CtiConfigManager::getConfigurationCategoryTableName()
{
    return "DCConfigurationCategory";
}

string CtiConfigManager::getConfigDeviceTableName()
{
    return "DCDeviceConfiguration";
}

string CtiConfigManager::getItemValuesTableName()
{
    return "DCCategoryItem";
}

string CtiConfigManager::getCategoryTypeTableName()
{
    return "DCCategoryType";
}

string CtiConfigManager::getItemTypeTableName()
{
    return "DCItemType";
}

string CtiConfigManager::getCategoryTableName()
{
    return "DCCategory";
}

BaseSPtr CtiConfigManager::createConfigByType(const int type)
{   //This function MUST set the type variable in config_base.
    try
    {
        switch(type)
        {
            using namespace MCT;
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
            case ConfigTypeMCTOptions:
            {
                BaseSPtr tempBasePtr (CTIDBG_new ConfigurationPart<MCTOptions>());
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
            case ConfigTypeMCTDisconnect:
            {
                BaseSPtr tempBasePtr (CTIDBG_new ConfigurationPart<MCTDisconnect>());
                return tempBasePtr;
            }
            case ConfigTypeMCTLongLoadProfile:
            {
                BaseSPtr tempBasePtr (CTIDBG_new ConfigurationPart<MCTLongLoadProfile>());
                return tempBasePtr;
            }
            case ConfigTypeMCTHoliday:
            {
                BaseSPtr tempBasePtr (CTIDBG_new ConfigurationPart<MCTHoliday>());
                return tempBasePtr;
            }
            case ConfigTypeMCTLoadProfileChannels:
            {
                BaseSPtr tempBasePtr (CTIDBG_new ConfigurationPart<MCTLoadProfileChannels>());
                return tempBasePtr;
            }
            case ConfigTypeMCTRelays:
            {
                BaseSPtr tempBasePtr (CTIDBG_new ConfigurationPart<MCTRelays>());
                return tempBasePtr;
            }
            case ConfigTypeMCTPrecannedTable:
            {
                BaseSPtr tempBasePtr (CTIDBG_new ConfigurationPart<MCTPrecannedTable>());
                return tempBasePtr;
            }
            case ConfigTypeMCTSystemOptions:
            {
                BaseSPtr tempBasePtr (CTIDBG_new ConfigurationPart<MCTSystemOptions>());
                return tempBasePtr;
            }
            case ConfigTypeMCTCentron:
            {
                BaseSPtr tempBasePtr (CTIDBG_new ConfigurationPart<MCTCentron>());
                return tempBasePtr;
            }
            using namespace CBC;
            case ConfigTypeCBCVoltage:
            {
                BaseSPtr tempBasePtr (CTIDBG_new ConfigurationPart<CBCVoltage>());
                return tempBasePtr;
            }
            case ConfigTypeCBCCommsLost:
            {
                BaseSPtr tempBasePtr (CTIDBG_new ConfigurationPart<CBCCommsLost>());
                return tempBasePtr;
            }
            case ConfigTypeCBCNeutralCurrent:
            {
                BaseSPtr tempBasePtr (CTIDBG_new ConfigurationPart<CBCNeutralCurrent>());
                return tempBasePtr;
            }
            case ConfigTypeCBCFaultDetection:
            {
                BaseSPtr tempBasePtr (CTIDBG_new ConfigurationPart<CBCFaultDetection>());
                return tempBasePtr;
            }
            case ConfigTypeCBCSeason1TimeAndTemp:
            {
                BaseSPtr tempBasePtr (CTIDBG_new ConfigurationPart<CBCSeason1TimeAndTemp>());
                return tempBasePtr;
            }
            case ConfigTypeCBCSeason2TimeAndTemp:
            {
                BaseSPtr tempBasePtr (CTIDBG_new ConfigurationPart<CBCSeason2TimeAndTemp>());
                return tempBasePtr;
            }
            case ConfigTypeCBCControlTimes:
            {
                BaseSPtr tempBasePtr (CTIDBG_new ConfigurationPart<CBCControlTimes>());
                return tempBasePtr;
            }
            case ConfigTypeCBCDataLogging:
            {
                BaseSPtr tempBasePtr (CTIDBG_new ConfigurationPart<CBCDataLogging>());
                return tempBasePtr;
            }
            case ConfigTypeCBCAddressing:
            {
                BaseSPtr tempBasePtr (CTIDBG_new ConfigurationPart<CBCAddressing>());
                return tempBasePtr;
            }
            case ConfigTypeCBC_DNP:
            {
                BaseSPtr tempBasePtr (CTIDBG_new ConfigurationPart<CBC_DNP>());
                return tempBasePtr;
            }
            case ConfigTypeCBC_UDP:
            {
                BaseSPtr tempBasePtr (CTIDBG_new ConfigurationPart<CBC_UDP>());
                return tempBasePtr;
            }
            default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << "*** CHECKPOINT *** " << " No constructor for config type "<<type<< " in " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        dout << CtiTime() << "*** CHECKPOINT *** " << " Exception thrown in " << __FILE__ << " (" << __LINE__ << ")" << endl;
        return  BaseSPtr();
    }
}

bool CtiConfigManager::insertValueIntoConfigMap(const long categoryID, const string &value, const string &valueid)
{
    BaseSPtr    pTempCtiConfigBase;

    pTempCtiConfigBase = _typeConfig.find(categoryID);

    if(!pTempCtiConfigBase)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << "*** CHECKPOINT *** " << " No config loaded with categoryID "<<categoryID<< " in " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    else
    {
        int resolvedKey = pTempCtiConfigBase->getProtectedResolvedKey(valueid);
        if(resolvedKey == 0)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << "*** CHECKPOINT *** "<< " No resolver for " << valueid<<" in " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
    if( category == "config" && identifier != 0 )
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
        else if( objectType == "category" )
        {
            //oddly, I dont care about adds or removals because both are taken care of in other ways.
            if( updateType == ChangeTypeUpdate )
            {
                //Load the new data
                loadCategories(identifier);

                //Then find out which devices we need to update..
                {
                    LockGuard map_guard(_mapMux);
                    ConfigTypeToDeviceMap::iterator iter = _categoryToConfig.find(identifier);
                    if( iter != _categoryToConfig.end() )
                    {
                        std::set<long>::iterator setIter = iter->second.begin();
                        for( ; setIter != iter->second.end(); setIter++ )
                        {
                            loadConfigs(*setIter);
                        }
                    }
                }
                
                //Since it is more efficient, we will just send out all of the device configs again
                updateDeviceConfigs();
            }
        }
    }
    else if( category == "config" && identifier == 0 )
    {
        refreshConfigurations();
    }
}

void CtiConfigManager::loadCategories(long categoryID)
{
    CtiTime start, stop;

    if( categoryID != 0 )
    {
        _typeConfig.remove(categoryID);//Give us a fresh start
    }

    start = start.now();
    {   
        RWDBConnection conn = getConnection();
        RWDBDatabase db = getDatabase();
    
        RWDBSelector selector = db.selector();

        //Warning!!! Due to problems with roguewave and the selector I am using direct location
        //reads from the RWDBReader. Be careful changing the selector/reading order

        RWDBTable itemValTbl = db.table( string2RWCString(getItemValuesTableName()) );
        selector << itemValTbl["categoryid"]//0
            << itemValTbl["itemtypeid"]//1
            << itemValTbl["value"];  //2
        selector.from(itemValTbl);

        RWDBTable categoryTypeTbl = db.table(string2RWCString(getCategoryTypeTableName()) );
        selector << categoryTypeTbl["categorytypeid"]//3
            << categoryTypeTbl["name"];//4
        selector.from(categoryTypeTbl);

        RWDBTable itemTbl = db.table( string2RWCString(getItemTypeTableName()) );
        selector << itemTbl["itemtypeid"]//5
            << itemTbl["name"];//6
        selector.from(itemTbl);

        RWDBTable categoryTbl = db.table(string2RWCString(getCategoryTableName()) );
        selector << categoryTbl["categoryid"]//7
            << categoryTbl["categorytypeid"]//8
            << categoryTbl["name"];//9
        selector.from(categoryTbl);
       
        selector.where(itemTbl["itemtypeid"] == itemValTbl["itemtypeid"] && categoryTbl["categoryid"] == itemValTbl["categoryid"]);
        selector.where(categoryTypeTbl["categorytypeid"] == categoryTbl["categorytypeid"] && selector.where());
        if( categoryID != 0 )
        {
            selector.where(categoryTbl["categoryid"] == categoryID && selector.where());
        }
        RWDBReader rdr = selector.reader(conn);

        while( (rdr.status().errorCode() == RWDBStatus::ok) && rdr() )
        {
            long categoryID;
            CtiConfig_type type;
            string tempType,value,valueName;
    
            rdr[categoryTypeTbl["categoryid"]]>>categoryID;
            rdr[4] >>tempType; //categoryTypeTbl["name"]
                
            rdr[itemValTbl["value"]] >> value;
            rdr[6] >> valueName;//itemTbl["name"]
    
            if( !_typeConfig.find(categoryID) )//This key is not in the map yet
            {
                type = resolveConfigType(tempType);
                if( type != ConfigTypeInvalid )
                {
                    _typeConfig.insert(categoryID,createConfigByType(type));//Should I remember this pointer and not do the next lookup? I dont think it is too expensive
                }
            }

            if(!valueName.empty() && !value.empty())
            {
                try{
                    insertValueIntoConfigMap(categoryID, value, valueName);
                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << "*** CHECKPOINT *** " << " Exception Thrown, type "<<type<< " probably is invalid " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
    
        RWDBTable typeTbl = db.table( string2RWCString(getConfigurationCategoryTableName()) );
    
        selector << typeTbl["configid"]
            << typeTbl["categoryid"];
    
        selector.from(typeTbl);
        if( configID != 0 )
        {
            selector.where( typeTbl["configid"] == configID && selector.where() );
        }
       
        RWDBReader rdr = selector.reader(conn);

        while( (rdr.status().errorCode() == RWDBStatus::ok) && rdr() )
        {
            long categoryID, configID;
    
            rdr["configid"] >>configID;
            rdr["categoryid"]>>categoryID;

            {
                LockGuard map_guard(_mapMux);
                ConfigTypeToDeviceMap::iterator iter = _categoryToConfig.find(categoryID);
                if( iter != _categoryToConfig.end() )
                {
                    iter->second.insert(configID);
                }
                else
                {
                    set<long> insertSet;
                    insertSet.insert(configID);
                    _categoryToConfig.insert(ConfigTypeToDeviceMap::value_type(categoryID, insertSet));
                }
    
                iter = _configToCategory.find(configID);
                if( iter != _configToCategory.end() )
                {
                    iter->second.insert(categoryID);
                }
                else
                {
                    set<long> insertSet;
                    insertSet.insert(categoryID);
                    _configToCategory.insert(ConfigTypeToDeviceMap::value_type(configID, insertSet));
                }
            }

            CtiConfigDeviceSPtr configDevSPtr = _deviceConfig.find(configID);

            if( !configDevSPtr )//This key is not in the map yet
            {
                CtiConfigDeviceSPtr devPtr(CTIDBG_new CtiConfigDevice());
                ConfigDeviceMap::insert_pair tempPair = _deviceConfig.insert(configID,devPtr);
                configDevSPtr = tempPair.first->second;
            }

            BaseSPtr categorySPtr;
            if( (categorySPtr = _typeConfig.find(categoryID)) && configDevSPtr )
            {
                configDevSPtr->insertConfig(categorySPtr);
            }

            if( (configDevSPtr = _deviceConfig.find(configID)) && categorySPtr )//This is debugging code and could be removed.
            {
                if( configDevSPtr->getConfigFromType(categorySPtr->getType()) == categorySPtr )
                {
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " PartID "<< categoryID<<" was NOT correctly added to ConfigID "<<configID<< endl;
                }
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
    
        RWDBTable typeTbl = db.table(string2RWCString(getConfigDeviceTableName()) );
    
        selector << typeTbl["deviceid"]
            << typeTbl["configid"];
    
        selector.from(typeTbl);
        if( configID != 0 )
        {
            selector.where(typeTbl["configid"] == configID && selector.where());
        }
        else if( deviceID != 0 )
        {
            selector.where(typeTbl["deviceid"] == deviceID && selector.where());
        }
       
        RWDBReader rdr = selector.reader(conn);

        
        while( (rdr.status().errorCode() == RWDBStatus::ok) && rdr() )
        {
            long devID, configID;
    
            rdr["configid"] >>configID;
            rdr["deviceid"]>>devID;

            CtiDeviceSPtr pDev = _devMgr->getEqual(devID);

            CtiConfigDeviceSPtr tempSPtr;

            if( (tempSPtr = _deviceConfig.find(configID)) && pDev )
            {
                pDev->setDeviceConfig(tempSPtr);
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

void CtiConfigManager::removeFromMaps(long configID, long categoryID)
{
    if( configID )
    {
        LockGuard map_guard(_mapMux);
        ConfigTypeToDeviceMap::iterator categoryIter;

        //First erase all the entries in the category map
        ConfigTypeToDeviceMap::iterator configIter = _configToCategory.find(configID);
        if( configIter != _configToCategory.end() )
        {
            std::set<long>::iterator setIter = configIter->second.begin();
            for( ; setIter != configIter->second.end(); setIter++ )
            {
                categoryIter = _categoryToConfig.find(*setIter);
                if( categoryIter != _categoryToConfig.end() )
                {
                    categoryIter->second.erase(configID);
                }
            }

            _configToCategory.erase(configID);
        }

    }

    if( categoryID )
    {
        LockGuard map_guard(_mapMux);
        ConfigTypeToDeviceMap::iterator configIter;

        //First erase all the entries in the category map
        ConfigTypeToDeviceMap::iterator categoryIter = _categoryToConfig.find(configID);
        if( categoryIter != _configToCategory.end() )
        {
            std::set<long>::iterator setIter = categoryIter->second.begin();
            for( ; setIter != categoryIter->second.end(); setIter++ )
            {
                configIter = _configToCategory.find(*setIter);
                if( configIter != _configToCategory.end() )
                {
                    configIter->second.erase(configID);
                }
            }

            _categoryToConfig.erase(configID);
        }
    }
}
