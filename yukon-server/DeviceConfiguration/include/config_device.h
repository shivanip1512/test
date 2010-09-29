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
    string _name;
    string _type;
public:

    DeviceConfig( long ID, string& name, string& type);
    ~DeviceConfig();

    bool getValue(std::string key, std::string& value);
    string getValueFromKey(std::string key);
    bool getLongValue(std::string key, long& value);
    long getLongValueFromKey(std::string key);
    double getFloatValueFromKey(std::string key);
    bool checkValues(string stringArray[], unsigned int arrayLen);
};

typedef boost::shared_ptr< DeviceConfig > DeviceConfigSPtr;

}
}

