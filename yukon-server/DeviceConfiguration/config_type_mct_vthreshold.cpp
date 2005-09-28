/*-----------------------------------------------------------------------------*
*
* File:   config_type_mct_vthreshold
*
* Date:   8/5/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/config_type_mct_vthreshold.cpp-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2005/09/28 14:28:06 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "yukon.h"
#include "config_type_mct_vthreshold.h"

namespace Cti       {
namespace Config    {

MCTVThreshold::MCTVThreshold()
{
}

MCTVThreshold::~MCTVThreshold()
{
}

string MCTVThreshold::getOutputStrings()
{
    return NULL;
}

int MCTVThreshold::getResolvedKey(RWCString key)
{
    key.toLower();
    if(key == "under vthreshold")
    {
        return UnderVoltageThreshold;
    }
    else if(key == "over vthreshold")
    {
        return OverVoltageThreshold;
    }
    else
    {
        return Invalid;
    }
}

bool MCTVThreshold::setValueWithKey(const RWCString &value,const int &key)
{
    LockGuard config_guard(_mux);//make thread safe

    switch(key)
    {
        case UnderVoltageThreshold:
        {
            UnderVoltageThresholdData = value;
            break;
        }
        case OverVoltageThreshold:
        {
            OverVoltageThresholdData = value;
            break;
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
RWCString MCTVThreshold::getValueFromKey(const int &key)
{
    LockGuard config_guard(_mux);//make thread safe

    switch(key)
    {
        case UnderVoltageThreshold:
        {
            return UnderVoltageThresholdData;
        }

        case OverVoltageThreshold:
        {
            return OverVoltageThresholdData;
        }
        default:
        {
            return RWCString();
        }
    }
}

}//Config
}//Cti
