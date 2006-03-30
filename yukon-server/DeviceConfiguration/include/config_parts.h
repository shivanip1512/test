/*-----------------------------------------------------------------------------*
*
* File:   config_type_mct_addressing
*
* Date:   8/5/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/config_type_mct_addressing.cpp-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2006/03/30 16:04:39 $
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

namespace MCT {
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

}//namespace mct

namespace CBC{
enum CBCVoltage
{
    ActiveSettings,
    UVClosePoint,
    OVTripPoint,
    OVUVControlTriggerTime,
    AdaptiveVoltageHysteresis,
    AdaptiveVoltageFlag,
    EmergencyUVPoint,
    EmergencyOVPoint,
    EmergencyVoltageTime,
    CBCVoltageInvalid,
};

enum CBCCommsLost
{
    CommsLostUVClosePoint,
    CommsLostOVTripPoint,
    CommsLostTime,
    CommsLostAction,
    CBCCommsLostInvalid,
};

enum CBCNeutralCurrent
{
    FaultCurrentSetPoint,
    StateChangeSetPoint,
    NeutralCurrentRetryCount,
    CBCNeutralCurrentInvalid,
};

enum CBCFaultDetection
{
    FaultDetectionActive,
    AI1AverageTime,
    AI2AverageTime,
    AI3AverageTime,
    AI1PeakSamples,
    AI2PeakSamples,
    AI3PeakSamples,
    AI1RatioThreshold,
    AI2RatioThreshold,
    AI3RatioThreshold,
    BatteryOnTime,
    CBCFaultDetectionInvalid,
};

enum CBCSeason1TimeAndTemp
{
    Season1Start,
    WeekdayTimedControlClose1,
    WeekendTimedControlClose1,
    WeekdayTimedControlTrip1,
    WeekendTimedControlTrip1,
    OffTimeState1,
    TempMinThreshold1,
    TempMinThresholdAction1,
    TempMinHysterisis1,
    TempMinThresholdTrigTime1,
    TempMaxThreshold1,
    TempMaxThresholdAction1,
    TempMaxHysterisis1,
    TempMaxThresholdTrigTime1,
    CBCSeason1TimeAndTempInvalid,
};

enum CBCSeason2TimeAndTemp
{
    Season2Start,
    WeekdayTimedControlClose2,
    WeekendTimedControlClose2,
    WeekdayTimedControlTrip2,
    WeekendTimedControlTrip2,
    OffTimeState2,
    TempMinThreshold2,
    TempMinThresholdAction2,
    TempMinHysterisis2,
    TempMinThresholdTrigTime2,
    TempMaxThreshold2,
    TempMaxThresholdAction2,
    TempMaxHysterisis2,
    TempMaxThresholdTrigTime2,
    CBCSeason2TimeAndTempInvalid,
};

enum CBCControlTimes
{
    ContactClosureTime,
    ManualControlDelayTrip,
    ManualControlDelayClose,
    RecloseDelayTime,
    CBCControlTimesInvalid,
};

enum CBCDataLogging
{
    DataLogFlags,
    LogTimeInterval,
    CBCDataLoggingInvalid,
};

enum CBCAddressing
{
    Geo,
    Substation,
    Feeder,
    Zip,
    UserDefined,
    Program,
    Splinter,
    RequiredAddressLevel,
    MCTAddressingInvalid,
};

enum CBC_DNP
{
    LineVoltageDeadBand,
    DeltaVoltageDeadBand,
    AnalogDeadBand,
    CBC_DNPInvalid,
};

enum CBC_UDP
{
    RetryDelay,
    PollTimeout,
    CBC_UDPInvalid,
};

}//namespace CBC


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

EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<MCT::MCTAddressing>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<MCT::MCT_TOU>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<MCT::MCT_DST>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<MCT::MCTVThreshold>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<MCT::MCTOptions>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<MCT::MCTDemandLoadProfile>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<MCT::MCTDisconnect>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<MCT::MCTLongLoadProfile>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<MCT::MCTHoliday>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<MCT::MCTLoadProfileChannels>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<MCT::MCTRelays>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<MCT::MCTPrecannedTable>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<MCT::MCTSystemOptions>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<CBC::CBCVoltage>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<CBC::CBCCommsLost>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<CBC::CBCNeutralCurrent>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<CBC::CBCFaultDetection>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<CBC::CBCSeason1TimeAndTemp>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<CBC::CBCSeason2TimeAndTemp>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<CBC::CBCControlTimes>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<CBC::CBCDataLogging>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<CBC::CBCAddressing>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<CBC::CBC_DNP>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<CBC::CBC_UDP>;

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
typedef ConfigurationPart<MCT::MCTAddressing> *          MCTAddressingSPtr;
typedef ConfigurationPart<MCT::MCT_TOU> *                MCT_TOU_SPtr;
typedef ConfigurationPart<MCT::MCT_DST> *                MCT_DST_SPtr;
typedef ConfigurationPart<MCT::MCTVThreshold> *          MCTVThresholdSPtr;
typedef ConfigurationPart<MCT::MCTOptions> *             MCTOptionsSPtr;
typedef ConfigurationPart<MCT::MCTDemandLoadProfile> *   MCTDemandLoadProfileSPtr;
typedef ConfigurationPart<MCT::MCTDisconnect> *          MCTDisconnectSPtr;
typedef ConfigurationPart<MCT::MCTLongLoadProfile> *     MCTLongLoadProfileSPtr;
typedef ConfigurationPart<MCT::MCTHoliday> *             MCTHolidaySPtr;
typedef ConfigurationPart<MCT::MCTLoadProfileChannels> * MCTLoadProfileChannelsSPtr;
typedef ConfigurationPart<MCT::MCTRelays> *              MCTRelaysSPtr;
typedef ConfigurationPart<MCT::MCTPrecannedTable> *      MCTPrecannedTableSPtr;
typedef ConfigurationPart<MCT::MCTSystemOptions> *       MCTSystemOptionsSPtr;

typedef ConfigurationPart<CBC::CBCVoltage> *             CBCVoltageSPtr;
typedef ConfigurationPart<CBC::CBCCommsLost> *           CBCCommsLostSPtr;
typedef ConfigurationPart<CBC::CBCNeutralCurrent> *      CBCNeutralCurrentSPtr;
typedef ConfigurationPart<CBC::CBCFaultDetection> *      CBCFaultDetectionSPtr;
typedef ConfigurationPart<CBC::CBCSeason1TimeAndTemp> *  CBCSeason1TimeAndTempSPtr;
typedef ConfigurationPart<CBC::CBCSeason2TimeAndTemp> *  CBCSeason2TimeAndTempSPtr;
typedef ConfigurationPart<CBC::CBCControlTimes> *        CBCControlTimesSPtr;
typedef ConfigurationPart<CBC::CBCDataLogging> *         CBCDataLoggingSPtr;
typedef ConfigurationPart<CBC::CBCAddressing> *          CBCAddressingSPtr;
typedef ConfigurationPart<CBC::CBC_DNP> *                CBC_DNPSPtr;
typedef ConfigurationPart<CBC::CBC_UDP> *                CBC_UDPSPtr;
#else
typedef shared_ptr< ConfigurationPart<MCT::MCTAddressing> >          MCTAddressingSPtr;
typedef shared_ptr< ConfigurationPart<MCT::MCT_TOU> >                MCT_TOU_SPtr;
typedef shared_ptr< ConfigurationPart<MCT::MCT_DST> >                MCT_DST_SPtr;
typedef shared_ptr< ConfigurationPart<MCT::MCTVThreshold> >          MCTVThresholdSPtr;
typedef shared_ptr< ConfigurationPart<MCT::MCTOptions> >             MCTOptionsSPtr;
typedef shared_ptr< ConfigurationPart<MCT::MCTDemandLoadProfile> >   MCTDemandLoadProfileSPtr;
typedef shared_ptr< ConfigurationPart<MCT::MCTDisconnect> >          MCTDisconnectSPtr;
typedef shared_ptr< ConfigurationPart<MCT::MCTLongLoadProfile> >     MCTLongLoadProfileSPtr;
typedef shared_ptr< ConfigurationPart<MCT::MCTHoliday> >             MCTHolidaySPtr;
typedef shared_ptr< ConfigurationPart<MCT::MCTLoadProfileChannels> > MCTLoadProfileChannelsSPtr;
typedef shared_ptr< ConfigurationPart<MCT::MCTRelays> >              MCTRelaysSPtr;
typedef shared_ptr< ConfigurationPart<MCT::MCTPrecannedTable> >      MCTPrecannedTableSPtr;
typedef shared_ptr< ConfigurationPart<MCT::MCTSystemOptions> >       MCTSystemOptionsSPtr;

typedef shared_ptr< ConfigurationPart<CBC::CBCVoltage> >             CBCVoltageSPtr;
typedef shared_ptr< ConfigurationPart<CBC::CBCCommsLost> >           CBCCommsLostSPtr;
typedef shared_ptr< ConfigurationPart<CBC::CBCNeutralCurrent> >      CBCNeutralCurrentSPtr;
typedef shared_ptr< ConfigurationPart<CBC::CBCFaultDetection> >      CBCFaultDetectionSPtr;
typedef shared_ptr< ConfigurationPart<CBC::CBCSeason1TimeAndTemp> >  CBCSeason1TimeAndTempSPtr;
typedef shared_ptr< ConfigurationPart<CBC::CBCSeason2TimeAndTemp> >  CBCSeason2TimeAndTempSPtr;
typedef shared_ptr< ConfigurationPart<CBC::CBCControlTimes> >        CBCControlTimesSPtr;
typedef shared_ptr< ConfigurationPart<CBC::CBCDataLogging> >         CBCDataLoggingSPtr;
typedef shared_ptr< ConfigurationPart<CBC::CBCAddressing> >          CBCAddressingSPtr;
typedef shared_ptr< ConfigurationPart<CBC::CBC_DNP> >                CBC_DNPSPtr;
typedef shared_ptr< ConfigurationPart<CBC::CBC_UDP> >                CBC_UDPSPtr;
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
