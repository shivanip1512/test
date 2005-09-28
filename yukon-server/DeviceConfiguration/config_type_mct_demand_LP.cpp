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
#include "config_type_mct_demand_LP.h"

namespace Cti       {
namespace Config    {

MCTDemandLoadProfile::MCTDemandLoadProfile()
{
}

MCTDemandLoadProfile::~MCTDemandLoadProfile()
{
}

string MCTDemandLoadProfile::getOutputStrings()
{
    return NULL;
}

int MCTDemandLoadProfile::getResolvedKey(RWCString key)
{
    key.toLower();
    if(key == "demand interval")
    {
        return DemandInterval;
    }
    else if(key == "lp interval")
    {
        return LPInterval;
    }
    else if(key == "lp volt interval")
    {
        return VoltageLPInterval;
    }
    else if(key == "demand v interval")
    {
        return VoltageDemandInterval;
    }
    else if(key == "load profile interval")
    {
        return LoadProfileInterval;
    }
    else if(key == "llp start time")
    {
        return LLPStartTime;
    }
    else if(key == "llp channel")
    {
        return LLPChannel;
    }
    else
    {
        return Invalid;
    }
}

bool MCTDemandLoadProfile::setValueWithKey(const RWCString &value,const int &key)
{
    LockGuard config_guard(_mux);//make thread safe

    switch(key)
    {
        case DemandInterval:
        {
            DemandIntervalData = value;
            break;
        }
        case LPInterval:
        {
            LPIntervalData = value;
            break;
        }
        case VoltageLPInterval:
        {
            VoltageLPIntervalData = value;
            break;
        }
        case VoltageDemandInterval:
        {
            VoltageDemandIntervalData = value;
            break;
        }
        case LoadProfileInterval:
        {
            LoadProfileIntervalData = value;
            break;
        }
        case LLPStartTime:
        {
            LLPStartTimeData = value;
            break;
        }
        case LLPChannel:
        {
            LLPChannelData = value;
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
RWCString MCTDemandLoadProfile::getValueFromKey(const int &key)
{
    LockGuard config_guard(_mux);//make thread safe

    switch(key)
    {
        case DemandInterval:
        {
            return DemandIntervalData;
        }
        case LPInterval:
        {
            return LPIntervalData;
        }
        case VoltageLPInterval:
        {
            return VoltageLPIntervalData;
        }
        case VoltageDemandInterval:
        {
            return VoltageDemandIntervalData;
        }
        case LoadProfileInterval:
        {
            return LoadProfileIntervalData;
        }
        case LLPStartTime:
        {
            return LLPStartTimeData;
        }
        case LLPChannel:
        {
            return LLPChannelData;
        }
        default:
        {
            return RWCString();
        }
    }
}

}//Config
}//Cti
