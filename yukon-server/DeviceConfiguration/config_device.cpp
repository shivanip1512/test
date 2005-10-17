/*-----------------------------------------------------------------------------*
*
* File:   config_device
*
* Date:   8/25/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/config_device.cpp-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2005/10/17 16:42:18 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "config_device.h"
#include "config_resolvers.h"
namespace Cti       {
namespace Config    {

CtiConfigDevice::CtiConfigDevice()
{
}

CtiConfigDevice::~CtiConfigDevice()
{
}

BaseSPtr CtiConfigDevice::getConfigFromType(CtiConfig_type type)
{
    LockGuard config_guard(_mux);

    BasePointerMap::iterator bcIterator = _baseConfigurations.find(type);
    if(bcIterator != _baseConfigurations.end())
    {
        return ((*bcIterator).second);
    }
    else
    {
        return BaseSPtr();
    }
}

void CtiConfigDevice::insertConfig(BaseSPtr configuration)
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
