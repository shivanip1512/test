/*-----------------------------------------------------------------------------*
*
* File:   config_device
*
* Date:   8/25/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/include/config_device.h-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2008/09/15 17:59:18 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __CONFIG_DEVICE_H__
#define __CONFIG_DEVICE_H__

#include "yukon.h"

#include <boost/shared_ptr.hpp>
#include "boostutil.h"
using boost::shared_ptr;

#include "logger.h"
#include "dllbase.h"
#include "hashkey.h"
#include <map>
#include <string>

class CtiConfigManager;

namespace Cti {
namespace Config {

class IM_EX_CONFIG CtiConfigDevice
{
    friend class CtiConfigManager;
protected:
    bool insertValue(std::string identifier, const std::string& value);

    //virtual int getProtectedResolvedKey(string key);
    //virtual bool setProtectedValueWithKey(const string &value, const int key);

private:
    typedef std::map<CtiHashKey, std::string> ConfigValueMap;
    ConfigValueMap _configurationValues;

    long _id;
    string _name;
    string _type;
public:

    CtiConfigDevice( long ID, string& name, string& type);
    ~CtiConfigDevice();

    bool getValue(std::string key, std::string& value);
    string getValueFromKey(std::string key);
    bool getLongValue(std::string key, long& value);
    long getLongValueFromKey(std::string key);
    double getFloatValueFromKey(std::string key);
    bool checkValues(string stringArray[], unsigned int arrayLen);
};

typedef shared_ptr< CtiConfigDevice > CtiConfigDeviceSPtr;

}
}

#endif//__CONFIG_DEVICE_H
