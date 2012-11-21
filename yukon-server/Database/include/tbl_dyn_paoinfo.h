#pragma once

#include "dlldefs.h"
#include "dbmemobject.h"
#include "database_connection.h"
#include "row_reader.h"

#include <string>
#include <map>

class IM_EX_CTIYUKONDB CtiTableDynamicPaoInfo : public CtiMemDBObject
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

        Key_MCT_StatusFlags,

    };

protected:

    typedef std::map<CtiApplication_t, const std::string> owner_map_t;
    typedef std::map<PaoInfoKeys,      const std::string> key_map_t;

    static const owner_map_t _owner_map;
    static const key_map_t   _key_map;

    long _entry_id;
    long _pao_id;
    CtiApplication_t _owner_id;

    PaoInfoKeys _key;
    std::string _value;

public:

    typedef CtiMemDBObject Inherited;

    CtiTableDynamicPaoInfo();
    CtiTableDynamicPaoInfo(long paoid, PaoInfoKeys k);  //  owner doesn't matter until the new row gets written to the DB

    bool operator<(const CtiTableDynamicPaoInfo &rhs) const;  //  this is for the set in dev_base

    static std::string getTableName();

    bool Insert(Cti::Database::DatabaseConnection &conn);
    bool Update(Cti::Database::DatabaseConnection &conn);

    static std::string getSQLCoreStatement(CtiApplication_t _app_id);

    void DecodeDatabaseReader(Cti::RowReader& rdr);

    long                getPaoID()       const;
    long                getEntryID()     const;
    CtiApplication_t    getOwnerID()     const;
    std::string         getOwnerString() const;
    PaoInfoKeys         getKey()         const;
    std::string         getKeyString()   const;
    std::string         getValue()       const;

    void getValue(int           &destination) const;
    void getValue(long          &destination) const;
    void getValue(unsigned long &destination) const;
    void getValue(double        &destination) const;
    void getValue(std::string   &destination) const;

    void setEntryID(long entry_id);
    void setOwner(CtiApplication_t o);
    void setKey(PaoInfoKeys k);

    //  we actually want to limit the input conversions into setValue, because we
    //    need to be able to convert them from string form in the getValue functions
    void setValue(int i);
    void setValue(unsigned int i);
    void setValue(long l);
    void setValue(unsigned long l);
    void setValue(double d);
    void setValue(const std::string &s);

    virtual void dump();
};

