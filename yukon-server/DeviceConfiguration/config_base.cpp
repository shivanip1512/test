/*-----------------------------------------------------------------------------*
*
* File:   config_base
*
* Date:   8/5/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/config_base.cpp-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2005/09/15 17:57:00 $
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
    return NULL;
}

//should contain a map of all of the possible types (subtypes). (TouRateA, TouRateB).
int Base::getResolvedKey(RWCString key)
{
    return 0;
}

bool Base::setValueWithKey(const RWCString& value, const int &key)
{
    return false;
}

}
}
