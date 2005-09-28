/*-----------------------------------------------------------------------------*
*
* File:   config_type_mct_dst
*
* Date:   8/5/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/config_type_mct_dst.cpp-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2005/09/28 14:28:06 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "yukon.h"
#include "config_type_mct_dst.h"

namespace Cti       {
namespace Config    {

MCTDST::MCTDST()
{
}

MCTDST::~MCTDST()
{
}

string MCTDST::getOutputStrings()
{
    return NULL;
}

int MCTDST::getResolvedKey(RWCString key)
{
    key.toLower();
    if(key == "dst begin")
    {
        return DstBegin;
    }
    else if(key == "dst end")
    {
        return DstEnd;
    }
    else if(key == "Time Zone Offset")
    {
        return TimeZoneOffset;
    }
    else
    {
        return Invalid;
    }
}

bool MCTDST::setValueWithKey(const RWCString &value,const int &key)
{
    LockGuard config_guard(_mux);//make thread safe

    switch(key)
    {
        case DstBegin:
        {
            DstBeginData = value;
            break;
        }
        case DstEnd:
        {
            DstEndData = value;
            break;
        }
        case TimeZoneOffset:
        {
            TimeZoneOffsetData = value;
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
RWCString MCTDST::getValueFromKey(const int &key)
{
    LockGuard config_guard(_mux);//make thread safe

    switch(key)
    {
        case DstBegin:
        {
            return DstBeginData;
        }
        case DstEnd:
        {
            return DstEndData;
        }
        case TimeZoneOffset:
        {
            return TimeZoneOffsetData;
        }
        default:
        {
            return RWCString();
        }
    }
}

}//Config
}//Cti
