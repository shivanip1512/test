#pragma once

#include "dlldefs.h"
#include "dbmemobject.h"
#include "database_connection.h"
#include "row_reader.h"
#include "boost/optional.hpp"
#include "loggable.h"

#include <string>
#include <map>


/**
 *  Abstract base class CtiTableDynamicPaoInfoBase
 */
class IM_EX_CTIYUKONDB CtiTableDynamicPaoInfoBase : private boost::noncopyable, public Cti::Loggable
{
protected:

    long _pao_id;
    std::string _value;
    bool _fromDb;

    CtiTableDynamicPaoInfoBase();
    CtiTableDynamicPaoInfoBase(long paoid, int value);
    CtiTableDynamicPaoInfoBase(long paoid, unsigned int value);
    CtiTableDynamicPaoInfoBase(long paoid, long value);
    CtiTableDynamicPaoInfoBase(long paoid, unsigned long value);
    CtiTableDynamicPaoInfoBase(long paoid, double value);
    CtiTableDynamicPaoInfoBase(long paoid, std::string value);

    virtual std::string getKeyString() const = 0;

public:

    bool Insert(Cti::Database::DatabaseConnection &conn, const std::string &owner);
    bool Update(Cti::Database::DatabaseConnection &conn, const std::string &owner);

    static std::string getSQLCoreStatement();

    long        getPaoID() const;
    std::string getValue() const;

    bool isFromDb() const;
    void setFromDb();

    void getValue(int           &destination) const;
    void getValue(unsigned int  &destination) const;
    void getValue(long          &destination) const;
    void getValue(unsigned long &destination) const;
    void getValue(double        &destination) const;
    void getValue(std::string   &destination) const;
};

/**
 *  CtiTableDynamicPaoInfo
 */
class IM_EX_CTIYUKONDB CtiTableDynamicPaoInfo : public CtiTableDynamicPaoInfoBase
{
public:

    enum PaoInfoKeys
    {
        //  this ordering can change without adverse effects - the strings are what the DB keys on
        Key_Invalid   =  -1,
        Key_MCT_SSpec = 100,
        Key_MCT_SSpecRevision,
        Key_MCT_LoadProfileConfig,
        Key_MCT_LoadProfileInterval,
        Key_MCT_LoadProfileInterval2,
        Key_MCT_IEDLoadProfileInterval,
        Key_MCT_TimeAdjustTolerance,
        Key_MCT_DSTStartTime,
        Key_MCT_DSTEndTime,
        Key_MCT_TimeZoneOffset,
        Key_MCT_UnderVoltageThreshold,
        Key_MCT_OverVoltageThreshold,
        Key_MCT_DemandInterval,
        Key_MCT_VoltageLPInterval,
        Key_MCT_VoltageDemandInterval,
        Key_MCT_ScheduledFreezeDay,
        Key_MCT_ScheduledFreezeConfigTimestamp,
        Key_MCT_DayTable,
        Key_MCT_DaySchedule1,
        Key_MCT_DaySchedule2,
        Key_MCT_DaySchedule3,
        Key_MCT_DaySchedule4,
        Key_MCT_DefaultTOURate,
        Key_MCT_AddressBronze,
        Key_MCT_AddressCollection,
        Key_MCT_AddressServiceProviderID,
        Key_MCT_AddressLead,
        Key_MCT_Configuration,
        Key_MCT_EventFlagsMask1,
        Key_MCT_EventFlagsMask2,
        Key_MCT_MeterAlarmMask,
        Key_MCT_Options,
        Key_MCT_OutageCycles,
        Key_MCT_DisconnectMode,
        Key_MCT_DemandThreshold,
        Key_MCT_ConnectDelay,
        Key_MCT_DisconnectMinutes,
        Key_MCT_ConnectMinutes,
        Key_MCT_LLPChannel1Len,
        Key_MCT_LLPChannel2Len,
        Key_MCT_LLPChannel3Len,
        Key_MCT_LLPChannel4Len,
        Key_MCT_DisplayParameters,
        Key_MCT_TransformerRatio,
        Key_MCT_Holiday1,
        Key_MCT_Holiday2,
        Key_MCT_Holiday3,
        Key_MCT_LoadProfileKRatio1,
        Key_MCT_LoadProfileKRatio2,
        Key_MCT_LoadProfileKRatio3,
        Key_MCT_LoadProfileKRatio4,
        Key_MCT_LoadProfileMeterRatio1,
        Key_MCT_LoadProfileMeterRatio2,
        Key_MCT_LoadProfileMeterRatio3,
        Key_MCT_LoadProfileMeterRatio4,
        Key_MCT_LoadProfileChannelConfig1,
        Key_MCT_LoadProfileChannelConfig2,
        Key_MCT_LoadProfileChannelConfig3,
        Key_MCT_LoadProfileChannelConfig4,
        Key_MCT_LoadProfileResolution,
        Key_MCT_RelayATimer,
        Key_MCT_RelayBTimer,
        Key_MCT_PrecannedTableReadInterval,
        Key_MCT_PrecannedMeterNumber,
        Key_MCT_PrecannedTableType,
        Key_MCT_DNP_RealTime1CRC,
        Key_MCT_DNP_RealTime2CRC,
        Key_MCT_DNP_BinaryCRC,
        Key_MCT_DNP_AnalogCRC1,
        Key_MCT_DNP_AnalogCRC2,
        Key_MCT_DNP_AnalogCRC3,
        Key_MCT_DNP_AnalogCRC4,
        Key_MCT_DNP_AnalogCRC5,
        Key_MCT_DNP_AccumulatorCRC1,
        Key_MCT_DNP_AccumulatorCRC2,
        Key_MCT_DNP_AccumulatorCRC3,
        Key_MCT_DNP_AccumulatorCRC4,
        Key_MCT_LcdMetric01,
        Key_MCT_LcdMetric02,
        Key_MCT_LcdMetric03,
        Key_MCT_LcdMetric04,
        Key_MCT_LcdMetric05,
        Key_MCT_LcdMetric06,
        Key_MCT_LcdMetric07,
        Key_MCT_LcdMetric08,
        Key_MCT_LcdMetric09,
        Key_MCT_LcdMetric10,
        Key_MCT_LcdMetric11,
        Key_MCT_LcdMetric12,
        Key_MCT_LcdMetric13,
        Key_MCT_LcdMetric14,
        Key_MCT_LcdMetric15,
        Key_MCT_LcdMetric16,
        Key_MCT_LcdMetric17,
        Key_MCT_LcdMetric18,
        Key_MCT_LcdMetric19,
        Key_MCT_LcdMetric20,
        Key_MCT_LcdMetric21,
        Key_MCT_LcdMetric22,
        Key_MCT_LcdMetric23,
        Key_MCT_LcdMetric24,
        Key_MCT_LcdMetric25,
        Key_MCT_LcdMetric26,

        Key_MCT_WaterMeterReadInterval,

        //  run-time info
        Key_MCT_LLPInterest_Time,
        Key_MCT_LLPInterest_Channel,
        Key_MCT_LLPInterest_RequestBegin,
        Key_MCT_LLPInterest_RequestEnd,

        Key_MCT_LoadProfilePeakReportTimestamp,

        Key_MCT_DailyReadInterestChannel,

        Key_FreezeCounter,
        Key_FreezeExpected,
        Key_VerificationSequence,

        Key_FrozenRateAPeakTimestamp,
        Key_FrozenRateBPeakTimestamp,
        Key_FrozenRateCPeakTimestamp,
        Key_FrozenRateDPeakTimestamp,
        Key_FrozenDemandPeakTimestamp,
        Key_FrozenDemand2PeakTimestamp,
        Key_FrozenDemand3PeakTimestamp,
        Key_DemandFreezeTimestamp,
        Key_VoltageFreezeTimestamp,

        Key_UDP_IP,
        Key_UDP_Port,
        Key_UDP_Sequence,

        Key_LCR_SSpec,
        Key_LCR_SSpecRevision,
        Key_LCR_SerialAddress,
        Key_LCR_Spid,
        Key_LCR_GeoAddress,
        Key_LCR_Substation,
        Key_LCR_Feeder,
        Key_LCR_ZipCode,
        Key_LCR_Uda,
        Key_LCR_ProgramAddressRelay1,
        Key_LCR_ProgramAddressRelay2,
        Key_LCR_ProgramAddressRelay3,
        Key_LCR_ProgramAddressRelay4,
        Key_LCR_SplinterAddressRelay1,
        Key_LCR_SplinterAddressRelay2,
        Key_LCR_SplinterAddressRelay3,
        Key_LCR_SplinterAddressRelay4,

        Key_RPT_SSpec,
        Key_RPT_SSpecRevision,
        //  make sure to add any new enum values to the string map

        Key_MCT_Holiday4,
        Key_MCT_Holiday5,
        Key_MCT_Holiday6,
        Key_MCT_Holiday7,
        Key_MCT_Holiday8,
        Key_MCT_Holiday9,
        Key_MCT_Holiday10,
        Key_MCT_Holiday11,
        Key_MCT_Holiday12,
        Key_MCT_Holiday13,
        Key_MCT_Holiday14,
        Key_MCT_Holiday15,
        Key_MCT_Holiday16,
        Key_MCT_Holiday17,
        Key_MCT_Holiday18,
        Key_MCT_Holiday19,
        Key_MCT_Holiday20,
        Key_MCT_Holiday21,
        Key_MCT_Holiday22,
        Key_MCT_Holiday23,
        Key_MCT_Holiday24,
        Key_MCT_Holiday25,
        Key_MCT_Holiday26,
        Key_MCT_Holiday27,
        Key_MCT_Holiday28,

        Key_MCT_PhaseLossPercent,
        Key_MCT_PhaseLossSeconds,

        Key_DisplayItem01,
        Key_DisplayItem02,
        Key_DisplayItem03,
        Key_DisplayItem04,
        Key_DisplayItem05,
        Key_DisplayItem06,
        Key_DisplayItem07,
        Key_DisplayItem08,
        Key_DisplayItem09,
        Key_DisplayItem10,
        Key_DisplayItem11,
        Key_DisplayItem12,
        Key_DisplayItem13,
        Key_DisplayItem14,
        Key_DisplayItem15,
        Key_DisplayItem16,
        Key_DisplayItem17,
        Key_DisplayItem18,
        Key_DisplayItem19,
        Key_DisplayItem20,
        Key_DisplayItem21,
        Key_DisplayItem22,
        Key_DisplayItem23,
        Key_DisplayItem24,
        Key_DisplayItem25,
        Key_DisplayItem26,

        Key_MCT_TouEnabled,

        // RFN keys

        Key_RFN_E2eRequestId,

        Key_RFN_TouEnabled,

        Key_RFN_MondaySchedule,
        Key_RFN_TuesdaySchedule,
        Key_RFN_WednesdaySchedule,
        Key_RFN_ThursdaySchedule,
        Key_RFN_FridaySchedule,
        Key_RFN_SaturdaySchedule,
        Key_RFN_SundaySchedule,
        Key_RFN_HolidaySchedule,

        Key_RFN_DefaultTOURate,

        Key_RFN_Schedule1Rate0,
        Key_RFN_Schedule1Time1,
        Key_RFN_Schedule1Rate1,
        Key_RFN_Schedule1Time2,
        Key_RFN_Schedule1Rate2,
        Key_RFN_Schedule1Time3,
        Key_RFN_Schedule1Rate3,
        Key_RFN_Schedule1Time4,
        Key_RFN_Schedule1Rate4,
        Key_RFN_Schedule1Time5,
        Key_RFN_Schedule1Rate5,

        Key_RFN_Schedule2Rate0,
        Key_RFN_Schedule2Time1,
        Key_RFN_Schedule2Rate1,
        Key_RFN_Schedule2Time2,
        Key_RFN_Schedule2Rate2,
        Key_RFN_Schedule2Time3,
        Key_RFN_Schedule2Rate3,
        Key_RFN_Schedule2Time4,
        Key_RFN_Schedule2Rate4,
        Key_RFN_Schedule2Time5,
        Key_RFN_Schedule2Rate5,

        Key_RFN_Schedule3Rate0,
        Key_RFN_Schedule3Time1,
        Key_RFN_Schedule3Rate1,
        Key_RFN_Schedule3Time2,
        Key_RFN_Schedule3Rate2,
        Key_RFN_Schedule3Time3,
        Key_RFN_Schedule3Rate3,
        Key_RFN_Schedule3Time4,
        Key_RFN_Schedule3Rate4,
        Key_RFN_Schedule3Time5,
        Key_RFN_Schedule3Rate5,

        Key_RFN_Schedule4Rate0,
        Key_RFN_Schedule4Time1,
        Key_RFN_Schedule4Rate1,
        Key_RFN_Schedule4Time2,
        Key_RFN_Schedule4Rate2,
        Key_RFN_Schedule4Time3,
        Key_RFN_Schedule4Rate3,
        Key_RFN_Schedule4Time4,
        Key_RFN_Schedule4Rate4,
        Key_RFN_Schedule4Time5,
        Key_RFN_Schedule4Rate5,

        Key_RFN_Holiday1,
        Key_RFN_Holiday2,
        Key_RFN_Holiday3,

        Key_RFN_DemandInterval,
        Key_RFN_VoltageAveragingInterval,
        Key_RFN_LoadProfileInterval,
        Key_RFN_VoltageProfileEnabled,
        Key_RFN_VoltageProfileEnabledUntil,

        Key_RFN_DemandFreezeDay,

        Key_RFN_OvUvEnabled,
        Key_RFN_OvThreshold,
        Key_RFN_UvThreshold,
        Key_RFN_OvUvAlarmReportingInterval,
        Key_RFN_OvUvAlarmRepeatInterval,
        Key_RFN_OvUvRepeatCount,

        Key_RFN_LcdCycleTime,
        Key_RFN_LcdDisconnectDisplayDisabled,
        Key_RFN_LcdDisplayDigits,

        Key_RFN_DisconnectMode,
        Key_RFN_ReconnectParam,
        Key_RFN_DisconnectDemandInterval,
        Key_RFN_DemandThreshold,
        Key_RFN_ConnectDelay,
        Key_RFN_MaxDisconnects,
        Key_RFN_DisconnectMinutes,
        Key_RFN_ConnectMinutes,

        Key_RFN_TempAlarmUnsupported,
        Key_RFN_TempAlarmIsEnabled,
        Key_RFN_TempAlarmRepeatInterval,
        Key_RFN_TempAlarmRepeatCount,
        Key_RFN_TempAlarmHighTempThreshold,

        Key_RFN_RecordingIntervalSeconds,
        Key_RFN_ReportingIntervalSeconds,

        Key_RF_DA_DnpSlaveAddress,

        Key_RFN_MeterProgrammingProgress,
    };

protected:

    PaoInfoKeys _key;

    virtual std::string getKeyString() const;

public:

    CtiTableDynamicPaoInfo(long paoid, PaoInfoKeys k, int value);
    CtiTableDynamicPaoInfo(long paoid, PaoInfoKeys k, unsigned int value);
    CtiTableDynamicPaoInfo(long paoid, PaoInfoKeys k, long value);
    CtiTableDynamicPaoInfo(long paoid, PaoInfoKeys k, unsigned long value);
    CtiTableDynamicPaoInfo(long paoid, PaoInfoKeys k, double value);
    CtiTableDynamicPaoInfo(long paoid, PaoInfoKeys k, std::string value);
    CtiTableDynamicPaoInfo(Cti::RowReader& rdr);  //  throws BadKeyException

    struct BadKeyException
    {
        BadKeyException(long paoid_, std::string key_, std::string owner_) : paoid(paoid_), key(key_), owner(owner_) {};

        const long paoid;
        const std::string key;
        const std::string owner;
    };

    PaoInfoKeys getKey() const;
    static std::string getKeyString(const PaoInfoKeys key);

    virtual std::string toString() const override;
};

/**
 *  CtiTableDynamicPaoInfoIndexed
 */
class IM_EX_CTIYUKONDB CtiTableDynamicPaoInfoIndexed : public CtiTableDynamicPaoInfoBase
{
public:

    enum PaoInfoKeysIndexed
    {
        Key_Invalid =  -1,

        Key_RFN_MidnightMetrics,
        Key_RFN_IntervalMetrics,
    };

protected:

    PaoInfoKeysIndexed _key;
    boost::optional<unsigned> _index;

    virtual std::string getKeyString() const;

public:

    CtiTableDynamicPaoInfoIndexed(long paoid, PaoInfoKeysIndexed k, unsigned long numberOfindex);
    CtiTableDynamicPaoInfoIndexed(long paoid, PaoInfoKeysIndexed k, unsigned index, int value);
    CtiTableDynamicPaoInfoIndexed(long paoid, PaoInfoKeysIndexed k, unsigned index, unsigned int value);
    CtiTableDynamicPaoInfoIndexed(long paoid, PaoInfoKeysIndexed k, unsigned index, long value);
    CtiTableDynamicPaoInfoIndexed(long paoid, PaoInfoKeysIndexed k, unsigned index, unsigned long value);
    CtiTableDynamicPaoInfoIndexed(long paoid, PaoInfoKeysIndexed k, unsigned index, double value);
    CtiTableDynamicPaoInfoIndexed(long paoid, PaoInfoKeysIndexed k, unsigned index, std::string value);
    CtiTableDynamicPaoInfoIndexed(Cti::RowReader& rdr);  //  throws BadKeyException

    boost::optional<unsigned> getIndex() const;

    PaoInfoKeysIndexed getKey() const;
    static std::string getKeyString(const PaoInfoKeysIndexed key);

    virtual std::string toString() const override;
};
