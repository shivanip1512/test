/*-----------------------------------------------------------------------------*
*
* File:   config_type_mct_addressing
*
* Date:   8/5/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/config_type_mct_addressing.cpp-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2005/09/28 14:28:06 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "yukon.h"
#include "config_type_mct_addressing.h"

namespace Cti       {
namespace Config    {

MCTAddressing::MCTAddressing()
{
}

MCTAddressing::~MCTAddressing()
{
}

string MCTAddressing::getOutputStrings()
{
    return NULL;
}

int MCTAddressing::getResolvedKey(RWCString key)
{
    key.toLower();
    if(key == "bronze address")
    {
        return Bronze;
    }
    else if(key == "lead address")
    {
        return Lead;
    }
    else if(key == "collection address")
    {
        return Collection;
    }
    else
    {
        return Invalid;
    }
}

bool MCTAddressing::setValueWithKey(const RWCString &value,const int &key)
{
    LockGuard config_guard(_mux);//make thread safe

    switch(key)
    {
        case Bronze:
        {
            BronzeData = value;
            break;
        }
        case Lead:
        {
            LeadData = value;
            break;
        }
        case Collection:
        {
            CollectionData = value;
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
RWCString MCTAddressing::getValueFromKey(const int &key)
{
    LockGuard config_guard(_mux);//make thread safe

    switch(key)
    {
        case Bronze:
        {
            return BronzeData;
        }
        case Lead:
        {
            return LeadData;
        }
        case Collection:
        {
            return CollectionData;
        }
        default:
        {
            return RWCString();
        }
    }
}

}//Config
}//Cti
