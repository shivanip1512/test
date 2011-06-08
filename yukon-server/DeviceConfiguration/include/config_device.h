#pragma once

#include "yukon.h"

#include <boost/shared_ptr.hpp>

#include "logger.h"
#include "dllbase.h"
#include "hashkey.h"
#include <map>
#include <string>

class CtiConfigManager;

namespace Cti {
namespace Config {

class IM_EX_CONFIG DeviceConfig
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
    std::string _name;
    std::string _type;
public:

    DeviceConfig( long ID, std::string& name, std::string& type);
    ~DeviceConfig();

    bool getValue(std::string key, std::string& value);
    std::string getValueFromKey(std::string key);
    bool getLongValue(std::string key, long& value);
    long getLongValueFromKey(std::string key);
    double getFloatValueFromKey(std::string key);
    bool checkValues(std::string stringArray[], unsigned int arrayLen);
};

typedef boost::shared_ptr< DeviceConfig > DeviceConfigSPtr;

}
}

