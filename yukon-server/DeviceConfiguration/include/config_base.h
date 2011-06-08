/*-----------------------------------------------------------------------------*
*
* File:   config_base
*
* Date:   8/5/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/include/config_base.h-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2008/09/15 17:59:18 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __CONFIG_BASE_H__
#define __CONFIG_BASE_H__
#include "yukon.h"

#include <boost/shared_ptr.hpp>
#include "boostutil.h"
using boost::shared_ptr;


#include "logger.h"
#include "dllbase.h"
#include <string>

class CtiConfigManager;

namespace Cti       {
namespace Config    {

class IM_EX_CONFIG Base
{
    friend class CtiConfigManager;
protected:
    virtual int getProtectedResolvedKey(std::string key);
    virtual bool setProtectedValueWithKey(const std::string &value, const int key);

private:

public:
    Base();
    virtual ~Base();

//    void setID(string ID);//String Identifier stored in Database.
//    string getID();
    virtual CtiConfig_type getType();

    virtual std::string getOutputStrings();
/*    virtual int getResolvedKey(Rstring key);
    virtual string getValueFromKey(const int key);
    virtual long getLongValueFromKey(const int key);
    virtual bool setValueWithKey(const string &value,const int key);*/
};



typedef shared_ptr< Base > BaseSPtr;

}
}

#endif //__CONFIG_BASE_H__
