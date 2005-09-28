/*-----------------------------------------------------------------------------*
*
* File:   config_type_mct_configuration
*
* Date:   8/5/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/config_type_mct_configuration.cpp-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2005/09/28 14:28:06 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "yukon.h"
#include "config_type_mct_configuration.h"

namespace Cti       {
namespace Config    {

MCTConfiguration::MCTConfiguration()
{
}

MCTConfiguration::~MCTConfiguration()
{
}

string MCTConfiguration::getOutputStrings()
{
    return NULL;
}

int MCTConfiguration::getResolvedKey(RWCString key)
{
    key.toLower();
    if(key == "configuration")
    {
        return Configuration;
    }
    else
    {
        return Invalid;
    }
}

bool MCTConfiguration::setValueWithKey(const RWCString &value,const int &key)
{
    LockGuard config_guard(_mux);//make thread safe

    switch(key)
    {
        case Configuration:
        {
            ConfigurationData = value;
        }
        default:
        {
            return false;
        }
    }
    return true;
}

/******************************************************************************
*   Function returns a string representation of the stored value based upon the
*   key given. This returns a string to reduce the coding necessary 
*
******************************************************************************/
RWCString MCTConfiguration::getValueFromKey(const int &key)
{
    LockGuard config_guard(_mux);//make thread safe

    switch(key)
    {
        case Configuration:
        {
            return ConfigurationData;
        }
        default:
        {
            return RWCString();
        }
    }
}

}//Config
}//Cti
