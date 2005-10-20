/*-----------------------------------------------------------------------------*
*
* File:   config_type_mct_addressing
*
* Date:   8/5/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/config_type_mct_addressing.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2005/10/20 18:26:07 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#ifndef __CONFIG_PARTS_H_
#define __CONFIG_PARTS_H_

#include "config_base.h"
class CtiConfigManager;

namespace Cti    {
namespace Config {

enum MCTAddressing
{
    MCTAddressingInvalid,
    Bronze = 1,
    Lead,
    Collection
};

enum MCT_TOU
{
    MCT_TOUInvalid,
    DayTable = 1,
    DaySchedule1,
    DaySchedule2,
    DaySchedule3,
    DaySchedule4,
    DefaultTOURate,
};

enum MCT_DST
{
    MCT_DSTInvalid,
    DstBegin = 1,
    DstEnd,
    TimeZoneOffset
};

enum MCTOptions
{
    MCTOptionsInvalid,
    Configuration = 1,
    Options
};

enum MCTDemandLoadProfile
{
    MCTDemandLoadProfileInvalid,
    DemandInterval = 1,
    LoadProfileInterval,
    VoltageLPInterval,
    VoltageDemandInterval,
    LoadProfileInterval2
};

enum MCTVThreshold
{
    MCTVThresholdInvalid,
    UnderVoltageThreshold,
    OverVoltageThreshold
};


template <class T>
class IM_EX_CONFIG ConfigurationPart : public Base
{
    friend class CtiConfigManager;
private:
    typedef map<int,RWCString> ConfigDataContainer;

    ConfigDataContainer     _dataMap;
public:
    T getResolvedKey(RWCString key);
    bool setValueWithKey(const RWCString &value, T enumKey);
    RWCString getValueFromKey(T enumKey);
    long getLongValueFromKey(T enumKey);

    virtual CtiConfig_type getType();

protected:
    virtual int getProtectedResolvedKey(RWCString key);
    virtual bool setProtectedValueWithKey(const RWCString &value, const int key);
};

EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<MCTAddressing>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<MCT_TOU>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<MCT_DST>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<MCTVThreshold>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<MCTOptions>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<MCTDemandLoadProfile>;

template <class T>
bool ConfigurationPart<T>::setValueWithKey(const RWCString &value, T enumKey)
{
    LockGuard config_guard(_mux);//make thread safe

    _dataMap.insert(ConfigDataContainer::value_type(enumKey,value));
    return true;
}

//Get the RWCString stored for this key
//Returns a null RWCString if there is no stored value.
template <class T>
RWCString ConfigurationPart<T>::getValueFromKey(T enumKey)
{
    LockGuard config_guard(_mux);//make thread safe

    ConfigDataContainer::iterator tempItr = _dataMap.find(enumKey);
    if(tempItr != _dataMap.end())
    {
        return tempItr->second;//stored string
    }
    else
    {
        return RWCString();
    }
}

//Returns LONG_MIN or LONG_MAX if overflow
template <class T>
long ConfigurationPart<T>::getLongValueFromKey(T enumKey)
{
    RWCString tempStr = getValueFromKey(enumKey);
    long tempLong = numeric_limits<long>::min();

    if(tempStr)
    {
        tempLong = strtol(tempStr.data(),NULL,16);
    }

    return tempLong;
}

/************************************************
*
* template <class T>
* CtiConfig_type ConfigurationPart<T>::getType()
* {
*     return ConfigTypeInvalid;
* }
************************************************/


//Protected Function
template <class T>
int ConfigurationPart<T>::getProtectedResolvedKey(RWCString key)
{
    return getResolvedKey(key);
}

template <class T>
bool ConfigurationPart<T>::setProtectedValueWithKey(const RWCString &value, int key)
{
    LockGuard config_guard(_mux);//make thread safe

    _dataMap.insert(ConfigDataContainer::value_type(key,value));
    return true;
}





#ifdef VSLICK_TAG_WORKAROUND
typedef ConfigurationPart<MCTAddressing> *          MCTAddressingSPtr;
typedef ConfigurationPart<MCT_TOU> *                MCT_TOU_SPtr;
typedef ConfigurationPart<MCT_DST> *                MCT_DST_SPtr;
typedef ConfigurationPart<MCTVThreshold> *          MCTVThresholdSPtr;
typedef ConfigurationPart<MCTOptions> *             MCTOptionsSPtr;
typedef ConfigurationPart<MCTDemandLoadProfile> *   MCTDemandLoadProfileSPtr;
#else
typedef shared_ptr< ConfigurationPart<MCTAddressing> >          MCTAddressingSPtr;
typedef shared_ptr< ConfigurationPart<MCT_TOU> >                MCT_TOU_SPtr;
typedef shared_ptr< ConfigurationPart<MCT_DST> >                MCT_DST_SPtr;
typedef shared_ptr< ConfigurationPart<MCTVThreshold> >          MCTVThresholdSPtr;
typedef shared_ptr< ConfigurationPart<MCTOptions> >             MCTOptionsSPtr;
typedef shared_ptr< ConfigurationPart<MCTDemandLoadProfile> >   MCTDemandLoadProfileSPtr;
#endif

}//Config
}//Cti

#endif //__CONFIG_PARTS_H_
