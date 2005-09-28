/*-----------------------------------------------------------------------------*
*
* File:   config_base
*
* Date:   8/5/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/include/config_base.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2005/09/28 14:30:51 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __CONFIG_BASE_H__
#define __CONFIG_BASE_H__
#include "yukon.h"

#include "boost/shared_ptr.hpp"
using boost::shared_ptr;
using namespace std;

#include "logger.h"
#include "dllbase.h"
#include <string>

namespace Cti       {
namespace Config    {

class IM_EX_CONFIG Base
{
protected:
    typedef CtiLockGuard<CtiMutex> LockGuard;//This must be used in every class that sets the value with key, or returns values.
    mutable CtiMutex    _mux;

private:
    string              _identifier;
    int                 _type;


public:
    Base();
    virtual ~Base();

//    void setID(string ID);//String Identifier stored in Database.
//    string getID();
    int getType();
    void setType(int type);
    virtual string getOutputStrings();
    virtual int getResolvedKey(RWCString key);
    virtual RWCString getValueFromKey(const int &key);
    virtual long getLongValueFromKey(const int &key);
    virtual bool setValueWithKey(const RWCString &value,const int &key);
};



#ifdef VSLICK_TAG_WORKAROUND
typedef Base * CtiConfigBaseSPtr;
#else
typedef shared_ptr< Base > CtiConfigBaseSPtr;
#endif

}
}

#endif //__CONFIG_BASE_H__
