/*-----------------------------------------------------------------------------*
*
* File:   config_type_mct_tou
*
* Date:   8/5/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/config_type_mct_tou.cpp-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2005/09/28 14:28:06 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "yukon.h"
#include "config_type_mct_tou.h"

namespace Cti       {
namespace Config    {

MCTTOU::MCTTOU()
{
}

MCTTOU::~MCTTOU()
{
}

string MCTTOU::getOutputStrings()
{
    return NULL;
}

int MCTTOU::getResolvedKey(RWCString key)
{
    key.toLower();
    if(key == "day table")
    {
        return DayTable;
    }
    else if(key == "day schedule 1")
    {
        return DaySchedule1;
    }
    else if(key == "day schedule 2")
    {
        return DaySchedule2;
    }
    else if(key == "day schedule 3")
    {
        return DaySchedule3;
    }
    else if(key == "day schedule 4")
    {
        return DaySchedule4;
    }
    else if(key == "rate")//both curent and default rates
    {
        return CurAndDefRate;
    }
    else if(key == "switch")//schedule and time
    {
        return SwtchSchdAndTime;
    }
    else if(key == "crit peak end")
    {
        return CritPkEndTime;
    }
    else
    {
        return Invalid;
    }
}

bool MCTTOU::setValueWithKey(const RWCString &value,const int &key)
{
    LockGuard config_guard(_mux);//make thread safe

    switch(key)
    {
        case DayTable:
        {
            DayTableData = value;
            break;
        }
        case DaySchedule1:
        {
            DaySchedule1Data = value;
            break;
        }
        case DaySchedule2:
        {
            DaySchedule2Data = value;
            break;
        }
        case DaySchedule3:
        {
            DaySchedule3Data = value;
            break;
        }
        case DaySchedule4:
        {
            DaySchedule4Data = value;
            break;
        }
        case CurAndDefRate:
        {
            CurAndDefRateData = value;
            break;
        }
        case SwtchSchdAndTime:
        {
            SwtchSchdAndTimeData = value;
            break;
        }
        case CritPkEndTime:
        {
            CritPkEndTimeData = value;
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
RWCString MCTTOU::getValueFromKey(const int &key)
{
    LockGuard config_guard(_mux);//make thread safe

    switch(key)
    {
        case DayTable:
        {
            return DayTableData;
        }

        case DaySchedule1:
        {
            return DaySchedule1Data;
        }
        case DaySchedule2:
        {
            return DaySchedule2Data;
        }
        case DaySchedule3:
        {
            return DaySchedule3Data;
        }
        case DaySchedule4:
        {
            return DaySchedule4Data;
        }
        case CurAndDefRate:
        {
            return CurAndDefRateData;
        }
        case SwtchSchdAndTime:
        {
            return SwtchSchdAndTimeData;
        }
        case CritPkEndTime:
        {
            return CritPkEndTimeData;
        }
        default:
        {
            return RWCString();
        }
    }
}

}//Config
}//Cti
