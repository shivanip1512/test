/*-----------------------------------------------------------------------------*
*
* File:   config_device
*
* Date:   8/25/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/config_device.cpp-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2005/09/15 17:57:00 $
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
        _baseConfigurations.insert(BasePointerMap::value_type(configuration->getType(), configuration));
}

}
}
