#pragma once

#include "dev_mct4xx.h"
#include "dev_mct410_commands.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Mct410Device :
    public Mct4xxDevice,
    protected Commands::Mct410Command::ResultHandler
{
    typedef Mct410Device Self;
    typedef Mct4xxDevice Parent;

    typedef YukonError_t (Self::*DecodeMethod)(const INMESS &, const CtiTime, CtiMessageList &, CtiMessageList &, OutMessageList &);

    typedef std::map<int, DecodeMethod> DecodeMapping;

    static const DecodeMapping    _decodeMethods;

    static const CommandSet       _commandStore;
    static       CommandSet       initCommandStore();

    static const ValueMapping _memoryMap;
    static const FunctionReadValueMappings _functionReadValueMaps;

    static const ConfigPartsList  _config_parts;
    static       ConfigPartsList  initConfigParts();

    static std::string describeStatusAndEvents(const unsigned char *buf);

    enum SspecInformation
    {
        Sspec = 1029,

        SspecRev_NewLLP_Min         =    9,  //  rev  0.9
        SspecRev_TOUPeak_Min        =   13,  //  rev  1.3
        SspecRev_NewOutage_Min      =    8,  //  rev  0.8
        SspecRev_Disconnect_Cycle   =   12,  //  rev  1.2
        SspecRev_Disconnect_ConfigReadEnhanced = 20,  //  rev 2.0
        SspecRev_DailyRead          =   21,  //  rev  2.1
        SspecRev_HourlyKwh          =   33,

        SspecRev_BetaLo =    9,  //  rev  0.9
        SspecRev_BetaHi =  200,  //  rev 20.0
    };

    void readSspec(const OUTMESS &OutMessage, OutMessageList &outList) const;

    virtual unsigned getUsageReportDelay(const unsigned interval_length, const unsigned days) const;
protected:

    long _hourlyReadId;

    struct daily_read_info_t
    {
        enum RequestType
        {
            Request_None,
            Request_SingleDay,
            Request_MultiDay,
            Request_Recent
        };

        struct
        {
            CtiDate begin;
            CtiDate end;

            unsigned channel;

            std::atomic<bool> in_progress = false;
            int multi_day_retries;

            int user_id;

            RequestType type;

        } request;

        struct
        {
            CtiDate  date;
            bool     needs_verification;  //  if the date is susceptible to check-bit aliasing and needs to be read from the meter

        } interest;

    } _daily_read_info;

    virtual bool sspecValid(const unsigned sspec, const unsigned rev) const;

    //  protected so the MCT-420 can access them
    static ValueMapping initMemoryMap();
    static FunctionReadValueMappings initFunctionReadValueMaps();

    virtual bool getOperation( const UINT &cmd,  BSTRUCT &bst ) const;

    enum Features
    {
        Feature_HourlyKwh,
        Feature_DisconnectCollar,
        Feature_OutageUnits,
        Feature_Disconnect
    };

    virtual bool disconnectRequiresCollar() const;

    virtual bool isSupported(const Mct4xxDevice::Features f) const;
    virtual bool isSupported(const Mct410Device::Features f) const;

    enum EmetconCommands
    {
        Command_Connect          = 0x4c,
        Command_Disconnect       = 0x4d,
        Command_PhaseDetectClear = 0x84,
    };

    enum MemoryMap
    {
        Memory_SspecPos             = 0x00,
        Memory_SspecLen             =    5,

        Memory_RevisionPos          = 0x01,
        Memory_RevisionLen          =    1,

        Memory_StatusPos            = 0x05,
        Memory_StatusLen            =    5,

        Memory_MeterAlarmsPos       = 0x08,
        Memory_MeterAlarmsLen       =    2,

        Memory_EventFlagsMask1Pos   = 0x0a,
        Memory_EventFlagsMask1Len   =    1,

        Memory_EventFlagsMask2Pos   = 0x0b,
        Memory_EventFlagsMask2Len   =    1,

        Memory_MeterAlarmMaskPos    = 0x0c,
        Memory_MeterAlarmMaskLen    =    2,

        Memory_DisplayParametersPos = 0x0f,
        Memory_DisplayParametersLen =    1,

        Memory_CentronConfigPos     = 0x0f,  //  this combines the Parameters and Multiplier read;
        Memory_CentronConfigLen     =   11,  //    it may be less reliable on noisy circuits, since it requires 3 D words

        Memory_TransformerRatioPos  = 0x19,
        Memory_TransformerRatioLen  =    1,

        Memory_UniqueAddressPos     = 0x10,
        Memory_UniqueAddressLen     =    3,

        Memory_BronzeAddressPos     = 0x13,
        Memory_BronzeAddressLen     =    1,

        Memory_LeadAddressPos       = 0x14,
        Memory_LeadAddressLen       =    2,

        Memory_CollectionAddressPos = 0x16,
        Memory_CollectionAddressLen =    2,

        Memory_SPIDAddressPos       = 0x18,
        Memory_SPIDAddressLen       =    1,

        Memory_AlarmsPos            = 0x15,
        Memory_AlarmsLen            =    2,

        Memory_IntervalsPos         = 0x1a,
        Memory_IntervalsLen         =    4,

        Memory_DemandIntervalPos    = 0x1a,
        Memory_DemandIntervalLen    =    1,

        Memory_LoadProfileIntervalPos   = 0x1b,
        Memory_LoadProfileIntervalLen   =    1,

        Memory_VoltageDemandIntervalPos = 0x1c,
        Memory_VoltageDemandIntervalLen =    1,

        Memory_VoltageLPIntervalPos     = 0x1d,
        Memory_VoltageLPIntervalLen     =    1,

        Memory_ThresholdsPos      = 0x1e,
        Memory_ThresholdsLen      =    5,

        Memory_OverVThresholdPos  = 0x1e,
        Memory_OverVThresholdLen  =    2,

        Memory_UnderVThresholdPos = 0x20,
        Memory_UnderVThresholdLen =    2,

        Memory_OutageCyclesPos    = 0x22,
        Memory_OutageCyclesLen    =    1,

        Memory_PowerfailCountPos  = 0x23,
        Memory_PowerfailCountLen  =    2,

        Memory_LastFreezeTimestampPos  = 0x26,
        Memory_LastFreezeTimestampLen  =    4,

        Memory_FreezeCounterPos        = 0x2a,
        Memory_FreezeCounterLen        =    1,

        Memory_LastVoltageFreezeTimestampPos = 0x2b,
        Memory_LastVoltageFreezeTimestampLen =    4,

        Memory_VoltageFreezeCounterPos = 0x2f,
        Memory_VoltageFreezeCounterLen =    1,

        Memory_LLPStartTimePos    = 0x30,
        Memory_LLPStartTimeLen    =    4,

        Memory_LLPChannelPos      = 0x34,
        Memory_LLPChannelLen      =    1,

        Memory_WaterMeterReadIntervalPos    = 0x35,
        Memory_WaterMeterReadIntervalLen    =    1,

        Memory_TimeAdjustTolPos   = 0x36,
        Memory_TimeAdjustTolLen   =    1,

        Memory_DSTBeginPos        = 0x37,
        Memory_DSTBeginLen        =    4,

        Memory_DSTEndPos          = 0x3b,
        Memory_DSTEndLen          =    4,

        Memory_TimeZoneOffsetPos  = 0x3f,
        Memory_TimeZoneOffsetLen  =    1,

        Memory_RTCPos             = 0x40,
        Memory_RTCLen             =    4,

        Memory_LastTSyncPos       = 0x44,
        Memory_LastTSyncLen       =    4,

        Memory_Holiday1Pos        = 0xd0,
        Memory_Holiday1Len        =    4,

        Memory_Holiday2Pos        = 0xd4,
        Memory_Holiday2Len        =    4,

        Memory_Holiday3Pos        = 0xd8,
        Memory_Holiday3Len        =    4,

        Memory_DailyReadInterestPos = 0xf3,
        Memory_DailyReadInterestLen =    2,
    };

    enum Functions
    {
        FuncRead_PhaseDetect      = 0x0f,
        FuncRead_PhaseDetectLen   =   13,

        FuncRead_OutagePos        = 0x10,
        FuncRead_OutageLen        =   13,

        FuncRead_MReadLen         =    9,

        FuncRead_SingleDayDailyReportCh1Pos = 0x1d,
        FuncRead_SingleDayDailyReportCh1Len =   13,

        FuncRead_SingleDayDailyReportCh2Pos = 0x1e,
        FuncRead_SingleDayDailyReportCh2Len =   11,

        FuncRead_SingleDayDailyReportCh3Pos = 0x1f,
        FuncRead_SingleDayDailyReportCh3Len =    9,

        FuncRead_MultiDayDailyReportingBasePos   = 0x20,
        FuncRead_MultiDayDailyReportingLen       =   13,
        FuncRead_MultiDayDailyReportingMaxOffset = 0x0f,

        FuncRead_Channel1SingleDayBasePos   = 0x30,
        FuncRead_Channel1SingleDayLen       =   11,
        FuncRead_Channel1SingleDayMaxOffset = 0x07,

        FuncRead_FrozenMReadPos   = 0x91,
        FuncRead_FrozenMReadLen   =   10,

        FuncRead_DemandPos        = 0x92,
        FuncRead_DemandLen        =   10,  //  brings back recent demand, avg. voltage, blink counter, and channels 2 and 3 (may be trimmed by executeRequest)

        FuncRead_PeakDemandPos    = 0x93,
        FuncRead_PeakDemandLen    =    9,  //  peak demand, time of peak, current meter reading

        FuncRead_FrozenPeakDemandPos = 0x94,
        FuncRead_FrozenPeakDemandLen =   10,

        FuncRead_VoltagePos       = 0x95,
        FuncRead_VoltageLen       =   12,  //  max and min voltages

        FuncRead_FrozenVoltagePos = 0x96,
        FuncRead_FrozenVoltageLen =   13,  //  max and min voltages plus freeze count

        FuncRead_LPStatusPos      = 0x97,
        FuncRead_LPStatusLen      =   12,

        FuncRead_TOUkWhPos        = 0xe0,
        FuncRead_TOUkWhLen        =   13,

        FuncRead_TOUkWhFrozenPos  = 0xe1,
        FuncRead_TOUkWhFrozenLen  =   13,

        FuncRead_DisconnectConfigPos = 0xfe,
        FuncRead_DisconnectConfigLen =   11,

        FuncRead_DisconnectStatusPos = 0xfe,
        FuncRead_DisconnectStatusLen =    1,

        FuncWrite_ConfigPos          = 0x01,
        FuncWrite_ConfigLen          = 1,

        FuncWrite_ConfigAlarmMaskPos = 0x01,
        FuncWrite_ConfigAlarmMaskLen = 5,

        FuncWrite_IntervalsPos       = 0x03,
        FuncWrite_IntervalsLen       =    4,

        FuncWrite_PhaseDetect        = 0x10,
        FuncWrite_PhaseDetectLen     =    5,

        FuncWrite_PhaseDetectClear        = 0x00,
        FuncWrite_PhaseDetectClearLen     =    2,

        FuncWrite_DailyReadInterestPos = 0x50,
        FuncWrite_DailyReadInterestLen =    4,

        FuncWrite_SetAddressPos        = 0xf1,
        FuncWrite_SetAddressLen        =    4,

        FuncWrite_CentronReadingPos    = 0xf2,
        FuncWrite_CentronReadingLen    =    8,

        FuncWrite_MeterParametersPos   = 0xf3,
        FuncWrite_MeterParametersLen   =    3,

        FuncWrite_DisconnectConfigPos  = 0xfe,
        FuncWrite_DisconnectConfigLen  =    8,
    };

    enum PointOffsets
    {
        PointOffset_Voltage          =  4,
        PointOffset_VoltageMax       = 14,
        PointOffset_VoltageMin       = 15,
        PointOffset_VoltageMaxFrozen = 24,
        PointOffset_VoltageMinFrozen = 25,

        PointOffset_Analog_Outage = 100,
        PointOffset_Status_PhaseDetect =  3000,
    };

    enum ChannelIdentifiers
    {
        Channel_Voltage = 4,
    };

    enum ValueType410
    {
        ValueType_AccumulatorDelta,
        ValueType_Voltage,
        ValueType_DynamicDemand,
        ValueType_FrozenDynamicDemand,
        ValueType_LoadProfile_Voltage,
        ValueType_LoadProfile_DynamicDemand,
        ValueType_OutageCount
    };

    virtual frozen_point_info getDemandData     (const unsigned char *buf, const unsigned len, const unsigned char *freeze_counter) const;
    virtual frozen_point_info getAccumulatorData(const unsigned char *buf, const unsigned len, const unsigned char *freeze_counter) const;

    frozen_point_info getData(const unsigned char *buf, const unsigned len, const ValueType410 vt) const;

    long getLoadProfileInterval(unsigned channel);
    point_info getLoadProfileData(unsigned channel, long interval_len, const unsigned char *buf, unsigned len);

    bool _intervalsSent;

    YukonError_t executeGetValue ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList ) override;
    YukonError_t executeGetConfig( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList ) override;
    YukonError_t executePutConfig( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList ) override;
    YukonError_t executePutStatus( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList ) override;

    YukonError_t executePutConfigInstallDisconnect(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly = false) override;
    YukonError_t executePutConfigInstallFreezeDay (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly = false) override;

    virtual DlcCommandPtr makeHourlyReadCommand(const CtiDate date_begin, const CtiDate date_end, const unsigned channel) const;

    virtual ConfigPartsList getPartsList();

    virtual const ValueMapping *getMemoryMap() const;
    virtual const FunctionReadValueMappings *getFunctionReadValueMaps() const;

    YukonError_t ModelDecode( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList ) override;
    YukonError_t SubmitRetry( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList ) override;

    void handleCommandResult(const Commands::Mct410Command &command) override;
    void handleCommandResult(const Commands::Mct410DisconnectConfigurationCommand &command) override;

    YukonError_t decodeGetValueKWH                  ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    YukonError_t decodeGetValueTOUkWh               ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    YukonError_t decodeGetValueDemand               ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    YukonError_t decodeGetValueVoltage              ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    YukonError_t decodeGetValueOutage               ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    YukonError_t decodeGetValueLoadProfilePeakReport( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    YukonError_t decodeGetValueDailyRead            ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    virtual YukonError_t decodeGetStatusInternal    ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    YukonError_t decodeGetStatusLoadProfile         ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    virtual YukonError_t decodeGetStatusFreeze      ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    virtual YukonError_t decodeGetConfigIntervals   ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    YukonError_t decodeGetConfigThresholds          ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    YukonError_t decodeGetConfigFreeze              ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    virtual YukonError_t decodeGetConfigDisconnect  ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    YukonError_t decodeGetConfigAddress             ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    YukonError_t decodeGetConfigPhaseDetect         ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    virtual YukonError_t decodeGetConfigDailyReadInterest   ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    YukonError_t decodeGetConfigWaterMeterReadInterval      ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    YukonError_t decodeGetConfigLongLoadProfileStorageDays  ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    YukonError_t decodeGetConfigLoadProfileExistingPeak     ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

    virtual YukonError_t decodeGetConfigMeterParameters( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    virtual YukonError_t decodeGetConfigModel          ( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

    void decodeGetValueDailyRead_MultiDay (const INMESS& InMessage, std::string& resultString, const DSTRUCT* DSt, YukonError_t& status, bool& expectMore, CtiReturnMsg* ReturnMsg, std::string consumption_pointname, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    void decodeGetValueDailyRead_SingleDay(const INMESS& InMessage, std::string& resultString, const DSTRUCT* DSt, YukonError_t& status, bool& expectMore, CtiReturnMsg* ReturnMsg, std::string consumption_pointname, std::string demand_pointname);

    static bool isDailyReadVulnerableToAliasing(const CtiDate &date, const CtiTime &now);
    void tryVerifyDailyReadInterestDate(const unsigned interest_day, const unsigned interest_month, const CtiTime TimeNow);

    virtual std::string decodeDisconnectConfig(const DSTRUCT &DSt);

    static std::string decodeDisconnectDemandLimitConfig(const boost::optional<int> config_byte, double demand_threshold);
    static std::string decodeDisconnectCyclingConfig    (const boost::optional<int> config_byte, const int disconnect_minutes, const int connect_minutes);

    virtual std::string decodeDisconnectStatus(const DSTRUCT &DSt) const;

    void calcAndInsertLPRequests( OUTMESS *&OutMessage, OutMessageList &outList ) override;
    bool calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage ) override;

    void sendIntervals( OUTMESS *&OutMessage, OutMessageList &outList );

public:

    struct IM_EX_DEVDB Utility
    {
        static int makeDynamicDemand(double input);
    };

    enum
    {
        ChannelCount = 3,
    };

    enum Disconnect_Raw
    {
        RawStatus_Connected               = 0x00,
        RawStatus_ConnectArmed            = 0x01,
        RawStatus_DisconnectedUnconfirmed = 0x02,
        RawStatus_DisconnectedConfirmed   = 0x03
    };

    enum Disconnect_StateGroup
    {
        StateGroup_DisconnectedConfirmed   = 0,
        StateGroup_Connected               = 1,
        StateGroup_DisconnectedUnconfirmed = 2,
        StateGroup_ConnectArmed            = 3
    };

    Mct410Device( );

    void setDisconnectAddress( unsigned long address );

    ULONG calcNextLPScanTime( void ) override;

    static bool buildPhaseDetectOutMessage(CtiCommandParser & parse, OUTMESS *&OutMessage);

    static frozen_point_info decodePulseAccumulator(const unsigned char *buf, const unsigned len, const unsigned char *freeze_counter);
};

typedef boost::shared_ptr<Mct410Device> Mct410DeviceSPtr;

}
}

