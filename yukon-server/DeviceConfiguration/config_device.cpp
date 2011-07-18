/*-----------------------------------------------------------------------------*
*
* File:   config_device
*
* Date:   8/25/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/config_device.cpp-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2007/12/03 21:15:54 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "config_device.h"
#include <string>

using std::string;

namespace Cti       {
namespace Config    {

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

// Checks each key in the array to see if it exists. The strings in the array will be changed.
bool DeviceConfig::checkValues(string stringArray[], unsigned int arrayLen)
{
    bool retVal = false;
    if( arrayLen > 0 )
    {
        retVal = true; //This is temporary!

        for( int i = 0; i < arrayLen; i++ )
        {
            CtiToLower(stringArray[i]);
            CtiHashKey findKey = CtiHashKey(stringArray[i]);
            ConfigValueMap::iterator iter = _configurationValues.find(findKey);

            if( iter == _configurationValues.end() )
            {
                retVal = false;
            }
        }
    }

    return retVal;
}

}
}
