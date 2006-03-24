/*-----------------------------------------------------------------------------*
*
* File:   config_type_mct_addressing
*
* Date:   8/5/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/config_type_mct_addressing.cpp-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2006/03/24 15:11:53 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#ifndef __CONFIG_PARTS_H_
#define __CONFIG_PARTS_H_

#include "config_base.h"
class CtiConfigManager;
//***** See instructions at bottom on how to add a new config part.

namespace Cti    {
namespace Config {

enum MCTAddressing
{
    MCTAddressingInvalid,
    Bronze,
    Lead,
    ServiceProviderID,
    Collection
};

enum MCT_TOU
{
    MCT_TOUInvalid,
    DayTable,
    DaySchedule1,
    DaySchedule2,
    DaySchedule3,
    DaySchedule4,
    DefaultTOURate,
};

enum MCT_DST
{
    MCT_DSTInvalid,
    DstBegin,
    DstEnd,
    TimeZoneOffset
};

enum MCTOptions
{
    MCTOptionsInvalid,
    AlarmMaskMeter,
    AlarmMaskEvent1,
    AlarmMaskEvent2,
    TimeAdjustTolerance,
    Configuration,
    Options,
    OutageCycles
};

enum MCTDemandLoadProfile
{
    MCTDemandLoadProfileInvalid,
    DemandInterval,
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

enum MCTDisconnect
{
    MCTDisconnectInvalid,
    DemandThreshold,
    ConnectDelay
};

enum MCTLongLoadProfile
{
    MCTLongLoadProfileInvalid,
    Channel1Length,
    Channel2Length,
    Channel3Length,
    Channel4Length,
};

enum MCTHoliday
{
    MCTHolidayInvalid,
    HolidayDate1,
    HolidayDate2,
    HolidayDate3,
};

enum MCTLoadProfileChannels
{
    MCTLoadProfileChannelsInvalid,
    ChannelConfig1,
    ChannelConfig2,
    ChannelConfig3,
    ChannelConfig4,
    MeterRatio1,
    MeterRatio2,
    MeterRatio3,
    MeterRatio4,
    KRatio1,
    KRatio2,
    KRatio3,
    KRatio4,
};

enum MCTRelays
{
    MCTRelaysInvalid,
    RelayATimer,
    RelayBTimer,
};

enum MCTPrecannedTable
{
    MCTPrecannedTableInvalid,
    TableReadInterval,
    MeterNumber,
    TableType,
};

enum MCTSystemOptions
{
    MCTSystemOptionsInvalid,
    DemandMetersToScan,
};

template <class T>
class IM_EX_CONFIG ConfigurationPart : public Base
{
    friend class CtiConfigManager;
private:
    typedef map<int,string> ConfigDataContainer;

    ConfigDataContainer     _dataMap;
public:
    T getResolvedKey(string key);
    bool setValueWithKey(const string &value, T enumKey);
    string getValueFromKey(T enumKey);
    long getLongValueFromKey(T enumKey);

    virtual CtiConfig_type getType();

protected:
    virtual int getProtectedResolvedKey(string key);
    virtual bool setProtectedValueWithKey(const string &value, const int key);
};

EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<MCTAddressing>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<MCT_TOU>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<MCT_DST>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<MCTVThreshold>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<MCTOptions>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<MCTDemandLoadProfile>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<MCTDisconnect>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<MCTLongLoadProfile>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<MCTHoliday>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<MCTLoadProfileChannels>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<MCTRelays>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<MCTPrecannedTable>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<MCTSystemOptions>;

template <class T>
bool ConfigurationPart<T>::setValueWithKey(const string &value, T enumKey)
{
    LockGuard config_guard(_mux);//make thread safe

    _dataMap.insert(ConfigDataContainer::value_type(enumKey,value));
    return true;
}

//Get the string stored for this key
//Returns a null string if there is no stored value.
template <class T>
string ConfigurationPart<T>::getValueFromKey(T enumKey)
{
    LockGuard config_guard(_mux);//make thread safe

    ConfigDataContainer::iterator tempItr = _dataMap.find(enumKey);
    if(tempItr != _dataMap.end())
    {
        return tempItr->second;//stored string
    }
    else
    {
        return string();
    }
}

//Returns LONG_MIN or LONG_MAX if overflow
template <class T>
long ConfigurationPart<T>::getLongValueFromKey(T enumKey)
{
    string tempStr = getValueFromKey(enumKey);
    long tempLong = std::numeric_limits<long>::min();

    if(!tempStr.empty())
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
int ConfigurationPart<T>::getProtectedResolvedKey(string key)
{
    return getResolvedKey(key);
}

template <class T>
bool ConfigurationPart<T>::setProtectedValueWithKey(const string &value, int key)
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
typedef ConfigurationPart<MCTDisconnect> *          MCTDisconnectSPtr;
typedef ConfigurationPart<MCTLongLoadProfile> *     MCTLongLoadProfileSPtr;
typedef ConfigurationPart<MCTHoliday> *             MCTHolidaySPtr;
typedef ConfigurationPart<MCTLoadProfileChannels> * MCTLoadProfileChannelsSPtr;
typedef ConfigurationPart<MCTRelays> *              MCTRelaysSPtr;
typedef ConfigurationPart<MCTPrecannedTable> *      MCTPrecannedTableSPtr;
typedef ConfigurationPart<MCTSystemOptions> *       MCTSystemOptionsSPtr;
#else
typedef shared_ptr< ConfigurationPart<MCTAddressing> >          MCTAddressingSPtr;
typedef shared_ptr< ConfigurationPart<MCT_TOU> >                MCT_TOU_SPtr;
typedef shared_ptr< ConfigurationPart<MCT_DST> >                MCT_DST_SPtr;
typedef shared_ptr< ConfigurationPart<MCTVThreshold> >          MCTVThresholdSPtr;
typedef shared_ptr< ConfigurationPart<MCTOptions> >             MCTOptionsSPtr;
typedef shared_ptr< ConfigurationPart<MCTDemandLoadProfile> >   MCTDemandLoadProfileSPtr;
typedef shared_ptr< ConfigurationPart<MCTDisconnect> >          MCTDisconnectSPtr;
typedef shared_ptr< ConfigurationPart<MCTLongLoadProfile> >     MCTLongLoadProfileSPtr;
typedef shared_ptr< ConfigurationPart<MCTHoliday> >             MCTHolidaySPtr;
typedef shared_ptr< ConfigurationPart<MCTLoadProfileChannels> > MCTLoadProfileChannelsSPtr;
typedef shared_ptr< ConfigurationPart<MCTRelays> >              MCTRelaysSPtr;
typedef shared_ptr< ConfigurationPart<MCTPrecannedTable> >      MCTPrecannedTableSPtr;
typedef shared_ptr< ConfigurationPart<MCTSystemOptions> >       MCTSystemOptionsSPtr;
#endif

}//Config
}//Cti

#endif //__CONFIG_PARTS_H_


//**********************************
// To add a new config part:
// 
//      First add the proper enum to config_parts.h.
//      Add the proper EXTERN_CONFIG pre declaration above.
//      Add the proper smart pointer tags above.
//      Add the GetResolvedKey and GetType functions
//      To config_parts_mct or a similar file.
//      If a new file is created, add it to the makefile.
//      
//      Add the new object to config_resolvers.h/.cpp
//      Add the new type to the create object list in mgr_config.cpp
//      If working with DynamicPAOInfo, add objects there.
//      Your done!
