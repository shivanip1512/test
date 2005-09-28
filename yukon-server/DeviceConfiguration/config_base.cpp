/*-----------------------------------------------------------------------------*
*
* File:   config_base
*
* Date:   8/5/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/config_base.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2005/09/28 14:30:51 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "config_base.h"
namespace Cti       {
namespace Config    {

Base::Base():
_type(0)
{
}

Base::~Base()
{
}

/*void Base::setID(string ID)//String Identifier stored in Database.
{
    _identifier = ID;
}

string Base::getID()
{
    return _identifier;
}*/

int Base::getType()
{
    return _type;
}

void Base::setType(int type)
{
    _type = type;
}

string Base::getOutputStrings()
{
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    dout << RWTime() << " No Defined output strings function.";
    return "";//null string
}

//should contain a map of all of the possible types (subtypes). (TouRateA, TouRateB).
int Base::getResolvedKey(RWCString key)
{
    return 0;
}

RWCString Base::getValueFromKey(const int &key)
{
    return RWCString();
}

//Returns LONG_MIN or LONG_MAX if overflow, LONG_MIN + 1 if invalid (null)
long Base::getLongValueFromKey(const int &key)
{
    RWCString tempStr = getValueFromKey(key);

    if(tempStr)
    {
        char *endStr;
        
        return strtol(tempStr.data(),&endStr,16);
    }
    else
    {
        return LONG_MIN+1;
    }
}

bool Base::setValueWithKey(const RWCString& value, const int &key)
{
    return false;
}

}
}
