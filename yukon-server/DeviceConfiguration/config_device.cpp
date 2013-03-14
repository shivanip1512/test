#include "precompiled.h"

#include "config_device.h"
#include <string>

using std::string;

namespace Cti       {
namespace Config    {

const string TrueString = "true";

DeviceConfig::DeviceConfig(long ID, string& name, string& type) :
_id(ID), _name(name), _type(type)
{
}

DeviceConfig::~DeviceConfig()
{
}

// Inserts a value into the mapping, this is a protected function and is
// not meant to be called by devices
bool DeviceConfig::insertValue(string identifier, const string& value)
{
    CtiToLower(identifier);
    CtiHashKey insertKey = CtiHashKey(identifier);

    std::pair<ConfigValueMap::iterator, bool> retVal = _configurationValues.insert(ConfigValueMap::value_type(insertKey, value));

    return retVal.second;
}

// getValue will look for the key and set value to the value of that key.
// returns true if successful.
bool DeviceConfig::getValue(std::string key, string& value)
{
    bool retVal = false;
    CtiToLower(key);
    CtiHashKey findKey = CtiHashKey(key);
    value = string();

    ConfigValueMap::iterator iter = _configurationValues.find(key);

    if( iter != _configurationValues.end() )
    {
        value = iter->second;
        retVal = true;
    }

    return retVal;
}

// getLongValue will look for the key and set value to the long value of that key.
// returns true if successful.
bool DeviceConfig::getLongValue(std::string key, long& value)
{
    bool retVal = false;
    CtiToLower(key);
    CtiHashKey findKey = CtiHashKey(key);
    value = std::numeric_limits<long>::min();

    ConfigValueMap::iterator iter = _configurationValues.find(findKey);

    if( iter != _configurationValues.end() )
    {
        string tempStr = iter->second;

        if(!tempStr.empty())
        {
            value = strtol(tempStr.data(),NULL,0);
            retVal = true;
        }

    }
    return retVal;
}

string DeviceConfig::getValueFromKey(std::string key)
{
    string retVal;
    CtiToLower(key);
    CtiHashKey findKey = CtiHashKey(key);

    ConfigValueMap::iterator iter = _configurationValues.find(key);

    if( iter != _configurationValues.end() )
    {
        retVal = iter->second;
    }

    return retVal;
}

long DeviceConfig::getLongValueFromKey(std::string key)
{
    long retVal = std::numeric_limits<long>::min();
    CtiToLower(key);
    CtiHashKey findKey = CtiHashKey(key);

    ConfigValueMap::iterator iter = _configurationValues.find(findKey);

    if( iter != _configurationValues.end() )
    {
        string tempStr = iter->second;

        if(!tempStr.empty())
        {
            retVal = strtol(tempStr.data(),NULL,0);
        }
    }
    return retVal;
}

double DeviceConfig::getFloatValueFromKey(std::string key)
{
    double retVal = std::numeric_limits<double>::min();
    CtiToLower(key);
    CtiHashKey findKey = CtiHashKey(key);

    ConfigValueMap::iterator iter = _configurationValues.find(findKey);

    if( iter != _configurationValues.end() )
    {
        string tempStr = iter->second;

        if(!tempStr.empty())
        {
            retVal = atof(tempStr.data());
        }
    }
    return retVal;
}

bool DeviceConfig::getBoolValue(std::string key, bool &value)
{
    string strValue;

    if ( !getValue(key, strValue) )
    {
        return false;
    }

    value = ciStringEqual(strValue, TrueString);

    return true;
}

}
}
