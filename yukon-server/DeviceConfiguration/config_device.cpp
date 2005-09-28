/*-----------------------------------------------------------------------------*
*
* File:   config_device
*
* Date:   8/25/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/config_device.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2005/09/28 14:33:18 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "config_device.h"
namespace Cti       {
namespace Config    {

CtiConfigDevice::CtiConfigDevice()
{
}

CtiConfigDevice::~CtiConfigDevice()
{
}

CtiConfigBaseSPtr CtiConfigDevice::getConfigFromType(int type)
{
    LockGuard config_guard(_mux);

    BasePointerMap::iterator bcIterator = _baseConfigurations.find(type);
    if(bcIterator != _baseConfigurations.end())
    {
        return ((*bcIterator).second);
    }
    else
    {
        return CtiConfigBaseSPtr();
    }
}

void CtiConfigDevice::insertConfig(CtiConfigBaseSPtr configuration)
{
    LockGuard config_guard(_mux);

    _baseConfigurations.insert(BasePointerMap::value_type(configuration->getType(), configuration));
}

string CtiConfigDevice::getAllOutputStrings()
{
    string returnString = "";
    LockGuard config_guard(_mux);

    for(BasePointerMap::iterator confItr = _baseConfigurations.begin(); confItr!=_baseConfigurations.end(); confItr++)
    {
        returnString += confItr->second->getOutputStrings();
    }
    return returnString;
}

}
}
