/*-----------------------------------------------------------------------------*
*
* File:   config_type_general
*
* Date:   8/5/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/config_type_general.cpp-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2005/09/28 14:28:55 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "yukon.h"
#include "config_type_general.h"

namespace Cti       {
namespace Config    {

General::General()
{
}

General::~General()
{
}

string General::getOutputStrings()
{
    return NULL;
}

int General::getResolvedKey(RWCString key)
{
    int returnValue=0;
    int count = 0;
    for(int a = 0;a<4 && a<key.length();a++)
    {
        returnValue += ((int)(key[(size_t)a]))*(a+1);//multiplying makes location matter!
    }
    returnValue += (int)(key[key.length()-1])*key.length();
    return returnValue;
}

bool General::setValueWithKey(const RWCString &value,const int &key)
{
    LockGuard config_guard(_mux);//make thread safe

    valueMap.insert(StringValueMap::value_type(key,value));
    return true;
}

RWCString General::getValueFromKey(const int &key)
{
    LockGuard config_guard(_mux);//make thread safe

    StringValueMap::iterator tempItr = valueMap.find(key);
    if(tempItr != valueMap.end())
    {
        return tempItr->second;//stored string
    }
    else
    {
        return RWCString();
    }
}

}//Config
}//Cti
