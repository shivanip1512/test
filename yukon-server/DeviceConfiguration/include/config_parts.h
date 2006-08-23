/*-----------------------------------------------------------------------------*
*
* File:   config_type_mct_addressing
*
* Date:   8/5/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DEVICECONFIGURATION/config_type_mct_addressing.cpp-arc  $
* REVISION     :  $Revision: 1.12 $
* DATE         :  $Date: 2006/08/23 15:07:24 $
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
    MCTAddressingInvalid = 0,
    Bronze,
    Lead,
    ServiceProviderID,
    Collection
};

enum MCT_TOU
{
    MCT_TOUInvalid = 0,
    DayTable,
    DaySchedule1,
    DaySchedule2,
    DaySchedule3,
    DaySchedule4,
    DefaultTOURate,
};

enum MCT_DST
{
    MCT_DSTInvalid = 0,
    DstBegin,
    DstEnd,
    TimeZoneOffset
};

enum MCTOptions
{
    MCTOptionsInvalid = 0,
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
    MCTDemandLoadProfileInvalid = 0,
    DemandInterval,
    LoadProfileInterval,
    VoltageLPInterval,
    VoltageDemandInterval,
    LoadProfileInterval2
};

enum MCTVThreshold
{
    MCTVThresholdInvalid = 0,
    UnderVoltageThreshold,
    OverVoltageThreshold
};

enum MCTDisconnect
{
    MCTDisconnectInvalid = 0,
    DemandThreshold,
    ConnectDelay,
    CyclingDisconnectMinutes,
    CyclingConnectMinutes
};

enum MCTLongLoadProfile
{
    MCTLongLoadProfileInvalid = 0,
    Channel1Length,
    Channel2Length,
    Channel3Length,
    Channel4Length,
};

enum MCTHoliday
{
    MCTHolidayInvalid = 0,
    HolidayDate1,
    HolidayDate2,
    HolidayDate3,
};

enum MCTLoadProfileChannels
{
    MCTLoadProfileChannelsInvalid = 0,
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
    MCTRelaysInvalid = 0,
    RelayATimer,
    RelayBTimer,
};

enum MCTPrecannedTable
{
    MCTPrecannedTableInvalid = 0,
    TableReadInterval,
    MeterNumber,
    TableType,
};

enum MCTSystemOptions
{
    MCTSystemOptionsInvalid = 0,
    DemandMetersToScan,
};

enum MCTCentron
{
    MCTCentronInvalid = 0,
    CentronParameters,
    CentronTransformerRatio
};

enum MCT_DNP
{
    MCT_DNPInvalid = 0,
    DNPCollection1BinaryA,
    DNPCollection1BinaryB,
    DNPCollection1Analog,
    DNPCollection1Accumulator,
    DNPCollection2BinaryA,
    DNPCollection2BinaryB,
    DNPCollection2Analog,
    DNPCollection2Accumulator,
    DNPBinaryByte1A,
    DNPBinaryByte1B,
    DNPAnalog1,
    DNPAnalog2,
    DNPAnalog3,
    DNPAnalog4,
    DNPAnalog5,
    DNPAnalog6,
    DNPAnalog7,
    DNPAnalog8,
    DNPAnalog9,
    DNPAnalog10,
    DNPAccumulator1,
    DNPAccumulator2,
    DNPAccumulator3,
    DNPAccumulator4,
    DNPAccumulator5,
    DNPAccumulator6,
    DNPAccumulator7,
    DNPAccumulator8,
};

}//namespace mct

namespace CBC{
enum CBCVoltage
{
    CBCVoltageInvalid = 0,
    ActiveSettings,
    UVClosePoint,
    OVTripPoint,
    OVUVControlTriggerTime,
    AdaptiveVoltageHysteresis,
    AdaptiveVoltageFlag,
    EmergencyUVPoint,
    EmergencyOVPoint,
    EmergencyVoltageTime,
};

enum CBCCommsLost
{
    CBCCommsLostInvalid = 0,
    CommsLostUVClosePoint,
    CommsLostOVTripPoint,
    CommsLostTime,
    CommsLostAction,
};

enum CBCNeutralCurrent
{
    CBCNeutralCurrentInvalid = 0,
    FaultCurrentSetPoint,
    StateChangeSetPoint,
    NeutralCurrentRetryCount,
};

enum CBCFaultDetection
{
    CBCFaultDetectionInvalid = 0,
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
};

enum CBCSeason1TimeAndTemp
{
    CBCSeason1TimeAndTempInvalid = 0,
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
};

enum CBCSeason2TimeAndTemp
{
    CBCSeason2TimeAndTempInvalid = 0,
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
};

enum CBCControlTimes
{
    CBCControlTimesInvalid = 0,
    ContactClosureTime,
    ManualControlDelayTrip,
    ManualControlDelayClose,
    RecloseDelayTime,
};

enum CBCDataLogging
{
    CBCDataLoggingInvalid = 0,
    DataLogFlags,
    LogTimeInterval,
};

enum CBCAddressing
{
    MCTAddressingInvalid = 0,
    Geo,
    Substation,
    Feeder,
    Zip,
    UserDefined,
    Program,
    Splinter,
    RequiredAddressLevel,
};

enum CBC_DNP
{
    CBC_DNPInvalid = 0,
    LineVoltageDeadBand,
    DeltaVoltageDeadBand,
    AnalogDeadBand,
};

enum CBC_UDP
{
    CBC_UDPInvalid = 0,
    RetryDelay,
    PollTimeout,
};

}//namespace CBC


template <class T>
class IM_EX_CONFIG ConfigurationPart : public Base
{
    friend class CtiConfigManager;
private:
    typedef std::map<int,string>                            ConfigDataContainer;
    typedef std::pair<ConfigDataContainer::iterator, bool>  ConfigDataInsertResult;

    ConfigDataContainer     _dataMap;
public:
    T getResolvedKey(string key);
    //bool setValueWithKey(const string &value, T enumKey);
    string getValueFromKey(T enumKey);
    long getLongValueFromKey(T enumKey);

    virtual CtiConfig_type getType();

protected:
    //These are here for those who believe they can ensure T correctness by themselves (no Type checking)
    //Note the friend class CtiConfigManager has access to these functions.
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
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<MCT::MCTCentron>;
EXTERN_CONFIG template class IM_EX_CONFIG ConfigurationPart<MCT::MCT_DNP>;
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

/*template <class T>
bool ConfigurationPart<T>::setValueWithKey(const string &value, T enumKey)
{
    ConfigDataInsertResult result;
    LockGuard config_guard(_mux);//make thread safe

    result = _dataMap.insert(ConfigDataContainer::value_type(enumKey,value));
    if(!result.second)
    {
        result.first->second = value;
    }
    return true;
}*/

//Get the string stored for this key
//Returns a null string if there is no stored value.
template <class T>
string ConfigurationPart<T>::getValueFromKey(T enumKey)
{
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
        tempLong = strtol(tempStr.data(),NULL,0);
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
typedef ConfigurationPart<MCT::MCTCentron> *             MCTCentronSPtr;
typedef ConfigurationPart<MCT::MCT_DNP> *                MCT_DNP_SPtr;

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
typedef shared_ptr< ConfigurationPart<MCT::MCTCentron> >             MCTCentronSPtr;
typedef shared_ptr< ConfigurationPart<MCT::MCT_DNP> >                MCT_DNP_SPtr;

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
