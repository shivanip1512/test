/*-----------------------------------------------------------------------------*
*
* File:   config_base
*
* Date:   8/5/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/include/config_base.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/12/20 17:16:44 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __CONFIG_BASE_H__
#define __CONFIG_BASE_H__
#include "yukon.h"

#include "boost/shared_ptr.hpp"
using boost::shared_ptr;


#include "logger.h"
#include "dllbase.h"
#include "config_resolvers.h"
#include <string>

class CtiConfigManager;

namespace Cti       {
namespace Config    {

class IM_EX_CONFIG Base
{
    friend class CtiConfigManager;
protected:
    typedef CtiLockGuard<CtiMutex> LockGuard;//This must be used in every class that sets the value with key, or returns values.
    mutable CtiMutex    _mux;
    virtual int getProtectedResolvedKey(string key);
    virtual bool setProtectedValueWithKey(const string &value, const int key);

private:

public:
    Base();
    virtual ~Base();

//    void setID(string ID);//String Identifier stored in Database.
//    string getID();
    virtual CtiConfig_type getType();

    virtual string getOutputStrings();
/*    virtual int getResolvedKey(Rstring key);
    virtual string getValueFromKey(const int key);
    virtual long getLongValueFromKey(const int key);
    virtual bool setValueWithKey(const string &value,const int key);*/
};



#ifdef VSLICK_TAG_WORKAROUND
typedef Base * BaseSPtr;
#else
typedef shared_ptr< Base > BaseSPtr;
#endif

}
}

#endif //__CONFIG_BASE_H__
