/*-----------------------------------------------------------------------------*
*
* File:   config_base
*
* Date:   8/5/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/config_base.cpp-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/12/20 17:16:44 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "config_base.h"
#include "config_resolvers.h"
namespace Cti       {
namespace Config    {

Base::Base()
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

CtiConfig_type Base::getType()
{
    return ConfigTypeInvalid;
}

string Base::getOutputStrings()
{
    CtiLockGuard<CtiLogger> doubt_guard(dout);
    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    dout << CtiTime() << " No Defined output strings function.";
    return "";//null string
}

//should contain a map of all of the possible types (subtypes). (TouRateA, TouRateB).
int Base::getProtectedResolvedKey(string key)
{
    return 0;
}

bool Base::setProtectedValueWithKey(const string& value, const int key)
{
    return false;
}

}
}
