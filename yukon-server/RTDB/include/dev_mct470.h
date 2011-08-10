#pragma once

#include "dev_mct4xx.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Mct470Device : public Mct4xxDevice
{
private:

    typedef Mct4xxDevice Inherited;

    static const CommandSet       _commandStore;
    static       CommandSet       initCommandStore();

    static const read_key_store_t _readKeyStore;
    static       read_key_store_t initReadKeyStore();

    static const ConfigPartsList  _config_parts_430;
    static const ConfigPartsList  _config_parts_470;
    static ConfigPartsList  initConfigParts430();
    static ConfigPartsList  initConfigParts470();

    CtiTableDeviceMCTIEDPort _iedPort;
    CtiTime                  _iedTime;

    boost::shared_ptr<Cti::DataAccessLoadProfile> _deviceConfigLp;
    boost::shared_ptr<Cti::DataAccessLoadProfile> getDeviceConfigLp(Config::DeviceConfigSPtr deviceConfig);

    enum ChannelConfiguration
    {
        NotUsed,
        Electronic,
        TwoWireKYZ,
        ThreeWireKYZ
    };

    enum PointOffsets
    {
        PointOffset_MaxOffset = PointOffset_PeakOffset,      //  10
        PointOffset_MinOffset = PointOffset_PeakOffset + 10  //  20
    };

    enum DNP_MCT_Offsets
    {
        MCT470_DNP_MCTPoint_RealTimeBinary       =   0,
        MCT470_DNP_MCTPoint_PreCannedBinary      =  30,
        MCT470_DNP_MCTPoint_RealTimeAnalog       =  42,
        MCT470_DNP_MCTPoint_PreCannedAnalog      =  48,
        MCT470_DNP_MCTPoint_RealTimeAccumulator  = 108,
        MCT470_DNP_MCTPoint_PreCannedAccumulator = 112,
    };

    enum IED_PointOffsets
    {
        PointOffset_TotalKWH    =  1,
        PointOffset_TOU_KWBase  =  2,
        PointOffset_TotalKW     = 10,
        PointOffset_TotalKMH    = 11,
        PointOffset_TOU_KMBase  = 12,
        PointOffset_TotalKM     = 20,
        PointOffset_PeakKW      = 21,
        PointOffset_PeakKM      = 22,

        PointOffset_AveragePowerFactor = 31,

        PointOffset_DemandResetCount = 39,
        PointOffset_OutageCount = 40,

        PointOffset_VoltsPhaseA = 41,
        PointOffset_VoltsPhaseB = 42,
        PointOffset_VoltsPhaseC = 43,

        PointOffset_CurrentNeutral  = 44,
        PointOffset_CurrentPhaseA   = 45,
        PointOffset_CurrentPhaseB   = 46,
        PointOffset_CurrentPhaseC   = 47,

        PointOffset_FrozenPointOffset = 100,        // Frozen points are 100 more than the corresponding non frozen

        PointOffset_DNPStatus_RealTime1   = 501,
        PointOffset_DNPStatus_RealTime2   = 516,
        PointOffset_DNPStatus_PrecannedStart = 531,
        PointOffset_DNPAnalog_RealTime1   = 501,
        PointOffset_DNPAnalog_RealTime2   = 504,
        PointOffset_DNPAnalog_Precanned1  = 507,
        PointOffset_DNPAnalog_Precanned2  = 513,
        PointOffset_DNPAnalog_Precanned3  = 519,
        PointOffset_DNPAnalog_Precanned4  = 525,
        PointOffset_DNPAnalog_Precanned5  = 531,
        PointOffset_DNPAnalog_Precanned6  = 537,
        PointOffset_DNPAnalog_Precanned7  = 543,
        PointOffset_DNPAnalog_Precanned8  = 549,
        PointOffset_DNPAnalog_Precanned9  = 555,
        PointOffset_DNPAnalog_Precanned10 = 561,
        PointOffset_DNPCounter_RealTime1  = 501,
        PointOffset_DNPCounter_RealTime2  = 503,
        PointOffset_DNPCounter_Precanned1 = 505,
        PointOffset_DNPCounter_Precanned2 = 511,
        PointOffset_DNPCounter_Precanned3 = 517,
        PointOffset_DNPCounter_Precanned4 = 523,
        PointOffset_DNPCounter_Precanned5 = 529,
        PointOffset_DNPCounter_Precanned6 = 535,
        PointOffset_DNPCounter_Precanned7 = 541,
        PointOffset_DNPCounter_Precanned8 = 547,
    };

    enum
    {
        Memory_ChannelOffset = 0x1a,

        MaxIEDReadAge = 600,  //  in seconds

        Sspec_MCT470_128k = 1030,
        Sspec_MCT430A     = 1037,
        Sspec_MCT430S     = 1046,
        Sspec_MCT470_256k = 1061,
        SspecRev_Min    =    5,  //  rev  0.5
        SspecRev_Max    =  255,  //  rev 25.5
        SspecRev_BetaLo =    9,  //  rev  0.9
        SspecRev_BetaHi =  200,  //  rev 20.0

        SspecRev_IED_ZeroWriteMin    = 13,  //  rev 1.3
        SspecRev_IED_ErrorPadding    = 14,  //  rev 1.4
        SspecRev_IED_LPExtendedRange = 26,  //  rev 2.6
        SspecRev_IED_Precanned11     = 42,  //  rev 4.2
    };

    virtual bool isSupported(const Mct4xxDevice::Features feature) const  {  return true;  };
    virtual bool sspecValid(const unsigned sspec, const unsigned rev) const  {  return true;  };  //  not checking SSPECs yet

    bool hasIedInputs()   const;
    bool hasPulseInputs() const;

    bool isIedChannel(unsigned channel) const;

    long       getLoadProfileInterval( unsigned channel );
    point_info getLoadProfileData    ( unsigned channel, const unsigned char *buf, unsigned len );

    bool hasChannelConfig(unsigned channel);
    bool requestChannelConfig(unsigned channel, OUTMESS *OutMessage, OutMessageList &outList);

    virtual unsigned getUsageReportDelay(const unsigned interval_length, const unsigned days) const;

    virtual boost::shared_ptr<DataAccessLoadProfile> getLoadProfile();

    long _lastConfigRequest;

    void decodeDNPRealTimeRead(BYTE *buffer, int readNumber, std::string &resultString, CtiReturnMsg *ReturnMsg, INMESS *InMessage);
    void getBytesFromString(std::string &values, BYTE* buffer, int buffLen, int &numValues, int fillCount, int bytesPerValue);
    int sendDNPConfigMessages(int startMCTID,  OutMessageList &outList, OUTMESS *&OutMessage, std::string &dataA, std::string &dataB, CtiTableDynamicPaoInfo::PaoInfoKeys key, bool force, bool verifyOnly);

    int setupRatioBytesBasedOnMeterType(int channel, double multiplier, double peakKwResolution, double lastIntervalDemandResolution, double lpResolution, unsigned int &ratio, unsigned int &kRatio);
    bool computeMultiplierFactors(double multiplier, unsigned &numerator, unsigned &denominator) const;
    std::string describeChannel(unsigned char channel_config) const;

    enum IED_Types
    {
        IED_Type_None     = 0x00,
        IED_Type_LG_S4    = 0x01,
        IED_Type_Alpha_A3 = 0x02,
        IED_Type_Alpha_PP = 0x03,
        IED_Type_GE_kV    = 0x04,
        IED_Type_GE_kV2   = 0x05,
        IED_Type_Sentinel = 0x06,
        IED_Type_DNP      = 0x07,
        IED_Type_GE_kV2c  = 0x08,
    };

    static IED_Types resolveIEDType(const std::string &iedType);
    static std::string    resolveIEDName(int bits);
    static std::string    resolveDNPStatus(int status);
    static long      resolveScheduleName(const std::string & scheduleName);

protected:

    virtual bool getOperation( const UINT &cmd, BSTRUCT &bst ) const;

    enum ValueType470
    {
        ValueType_PulseDemand,
        ValueType_LoadProfile_PulseDemand,
        ValueType_LoadProfile_IED_LG_S4,
        ValueType_LoadProfile_IED_Alpha_A3,
        ValueType_LoadProfile_IED_Alpha_PP,
        ValueType_LoadProfile_IED_GE_kV,
        ValueType_LoadProfile_IED_GE_kV2,
        ValueType_LoadProfile_IED_GE_kV2c,
        ValueType_LoadProfile_IED_Sentinel,
        ValueType_IED,
    };

    virtual point_info getDemandData(const unsigned char *buf, const unsigned len, const unsigned char *freeze_counter) const;

    point_info getData(const unsigned char *buf, const unsigned len, const ValueType470 vt) const;

    static const error_map _error_info_old_lp;
    static const error_map _error_info_lgs4;
    static const error_map _error_info_alphaa3;
    static const error_map _error_info_alphapp;
    static const error_map _error_info_gekv;
    static const error_map _error_info_sentinel;

    static error_map initErrorInfoOldLP   ( void );
    static error_map initErrorInfoLGS4    ( void );
    static error_map initErrorInfoAlphaA3 ( void );
    static error_map initErrorInfoAlphaPP ( void );
    static error_map initErrorInfoGEkV    ( void );
    static error_map initErrorInfoSentinel( void );

    enum MemoryMap
    {
        //  new/changed stuff
        Memory_EventFlagsMask1Pos   = 0x08,
        Memory_EventFlagsMask1Len   =    1,

        Memory_EventFlagsMask2Pos   = 0x09,
        Memory_EventFlagsMask2Len   =    1,

        Memory_AddressingPos        = 0x0d,
        Memory_AddressingLen        =    6,

        Memory_AddressBronzePos     = 0x0d,
        Memory_AddressBronzeLen     =    1,

        Memory_AddressLeadPos       = 0x0e,
        Memory_AddressLeadLen       =    2,

        Memory_AddressCollectionPos = 0x10,
        Memory_AddressCollectionLen =    2,

        Memory_AddressSPIDPos       = 0x12,
        Memory_AddressSPIDLen       =    1,

        Memory_PowerfailCountPos    = 0x13,
        Memory_PowerfailCountLen    =    2,

        Memory_LastFreezeTimestampPos = 0x15,
        Memory_LastFreezeTimestampLen =    4,

        Memory_FreezeCounterPos       = 0x19,
        Memory_FreezeCounterLen       =    1,

        Memory_TimeAdjustTolerancePos = 0x1f,
        Memory_TimeAdjustToleranceLen =    1,

        Memory_DSTBeginPos          = 0x20,
        Memory_DSTBeginLen          =    4,

        Memory_DSTEndPos            = 0x24,
        Memory_DSTEndLen            =    4,

        Memory_TimeZoneOffsetPos    = 0x28,
        Memory_TimeZoneOffsetLen    =    1,

        Memory_RTCPos               = 0x29,
        Memory_RTCLen               =    4,
        Memory_LastTSyncPos         = 0x2d,
        Memory_LastTSyncLen         =    4,

        Memory_IntervalsPos         = 0x32,
        Memory_IntervalsLen         =    6,

        Memory_DemandIntervalPos    = 0x32,
        Memory_DemandIntervalLen    =    1,

        Memory_LoadProfileInterval1Pos  = 0x33,
        Memory_LoadProfileInterval1Len  =    1,

        Memory_LoadProfileInterval2Pos  = 0x34,
        Memory_LoadProfileInterval2Len  =    1,

        Memory_TableReadIntervalPos = 0x35,
        Memory_TableReadIntervalLen =    1,

        Memory_PrecannedMeterNumPos = 0x36,
        Memory_PrecannedMeterNumLen =    1,

        Memory_PrecannedTableTypePos = 0x37,
        Memory_PrecannedTableTypeLen =    1,

        Memory_RelayATimerPos       = 0x48,
        Memory_RelayATimerLen       =    1,

        Memory_RelayBTimerPos       = 0x49,
        Memory_RelayBTimerLen       =    1,

        Memory_ChannelMultiplierPos = 0x88,
        Memory_ChannelMultiplierLen =    4,

        Memory_MeteringRatio1Pos    = 0x88,
        Memory_MeteringRatio1Len    =    2,

        Memory_KRatio1Pos           = 0x8a,
        Memory_KRatio1Len           =    2,

        Memory_ChannelConfig1Pos    = 0x8e,
        Memory_ChannelConfig1Len    =    1,

        Memory_MeteringRatio2Pos    = 0xa2,
        Memory_MeteringRatio2Len    =    2,

        Memory_KRatio2Pos           = 0xa4,
        Memory_KRatio2Len           =    2,

        Memory_ChannelConfig2Pos    = 0xa8,
        Memory_ChannelConfig2Len    =    1,

        Memory_MeteringRatio3Pos    = 0xbc,
        Memory_MeteringRatio3Len    =    2,

        Memory_KRatio3Pos           = 0xbe,
        Memory_KRatio3Len           =    2,

        Memory_ChannelConfig3Pos    = 0xc2,
        Memory_ChannelConfig3Len    =    1,

        Memory_MeteringRatio4Pos    = 0xd6,
        Memory_MeteringRatio4Len    =    2,

        Memory_KRatio4Pos           = 0xd8,
        Memory_KRatio4Len           =    2,

        Memory_ChannelConfig4Pos    = 0xdc,
        Memory_ChannelConfig4Len    =    1,

        Memory_Holiday1Pos          = 0xe0,
        Memory_Holiday1Len          =    4,

        Memory_Holiday2Pos          = 0xe4,
        Memory_Holiday2Len          =    4,

        Memory_Holiday3Pos          = 0xe8,
        Memory_Holiday3Len          =    4,

        Memory_IedDnpAddressPos     = 0xf6,
        Memory_IedDnpAddressLen     =    4,

        Memory_ModelLen             =    5,

        //  lengths are different for these
        Memory_StatusPos            = 0x05, // Mct410Device::Memory_StatusPos,
        Memory_StatusLen            =    3,

    };

    enum Functions
    {
        FuncWrite_ConfigAlarmMaskLen    =   3,  //  func write 0x01

        FuncWrite_IntervalsPos          = 0x03, // Mct410Device::FuncWrite_IntervalsPos,
        FuncWrite_IntervalsLen          =    3,

        FuncWrite_RelaysPos             = 0x08,
        FuncWrite_RelaysLen             =    3,

        FuncWrite_SetupLPChannelsPos    = 0x07,
        FuncWrite_SetupLPChannelsLen    =   13,
        FuncWrite_SetupLPChannelLen     =    7,  //  if you're only doing one channel

        FuncWrite_IEDCommandData        = 0xd1,
        FuncWrite_IEDCommandDataBaseLen =    5,

        FuncWrite_PrecannedTablePos     = 0xd3,
        FuncWrite_PrecannedTableLen     =    4,

        FuncWrite_CurrentReading        = 0xd5,
        FuncWrite_CurrentReadingLen     =    5,

        FuncRead_PhaseCurrent           = 0xda,
        FuncRead_PhaseCurrentLen        =   13,

        FuncWrite_DNPReqTable           = 0xd6,

        FuncRead_ChannelSetupDataPos    = 0x20,
        FuncRead_ChannelSetupDataLen    =    7,

        FuncRead_LoadProfileChannel12Pos = 0x21,
        FuncRead_LoadProfileChannel12Len =   10,

        FuncRead_LoadProfileChannel34Pos = 0x22,
        FuncRead_LoadProfileChannel34Len =   10,

        FuncRead_PrecannedTablePos      = 0x23,
        FuncRead_PrecannedTableLen      =   11,

        FuncRead_IED_DNPTablePos        = 0x24,
        FuncRead_IED_DNPTableLen        =   13,

        FuncRead_IED_CRCPos             = 0x25,
        FuncRead_IED_CRCLen             =   12,

        FuncRead_MReadLen               =   12,

        FuncRead_MReadFrozenPos         = 0x91,
        FuncRead_MReadFrozenLen         =   13,

        FuncRead_DemandPos              = 0x92,
        FuncRead_DemandLen              =   11,  //  0x92

        FuncRead_PeakDemandPos          = 0x93,
        FuncRead_PeakDemandLen          =   12,  //  0x93

        FuncRead_TOUChannelOffset       = 0x08,  //  TOU function reads for channel 2 are offset by 8

        FuncRead_IED_Precanned_Base     = 0xc1,
        FuncRead_IED_Precanned_Last     = 0xd4,
        FuncRead_IED_TOU_CurrentKWBase  = 0xc1,
        FuncRead_IED_TOU_CurrentKMBase  = 0xc5,
        FuncRead_IED_TOU_CurrentTotals  = 0xc9,

        FuncRead_IED_Peak_kW            = 0xc4,
        FuncRead_IED_Peak_kM            = 0xc8,

        FuncRead_IED_TOU_PreviousOffset =    9,

        FuncRead_IED_TOU_MeterStatus    = 0xd3,

        FuncRead_IED_RealTime           = 0xd5,
        FuncRead_IED_RealTime2          = 0xda,

        MCT470_DNP_Analog_Precanned_Offset  =  1,
        MCT470_DNP_Status_Precanned_Offset  =  0,
        MCT470_DNP_Counter_Precanned_Offset = 11,
        MCT470_DNP_Counter_Precanned_Reads  =  8,
    };

    bool isLPDynamicInfoCurrent(void);
    void requestDynamicInfo(CtiTableDynamicPaoInfo::PaoInfoKeys key, OUTMESS *&OutMessage, OutMessageList &outList);

    void sendIntervals(OUTMESS *&OutMessage, OutMessageList &outList);

    virtual const read_key_store_t &getReadKeyStore(void) const;

    virtual INT ModelDecode( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    virtual INT ErrorDecode( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &retList);

    virtual INT executeScan     (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    virtual INT executeGetValue (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    virtual INT executePutValue (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    virtual INT executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    virtual INT executePutConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

    virtual Mct4xxDevice::ConfigPartsList getPartsList();

    int executePutConfigLoadProfileChannel   (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly = false);
    int executePutConfigRelays               (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly = false);
    int executePutConfigPrecannedTable       (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly = false);
    int executePutConfigDemandLP             (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly = false);
    int executePutConfigTOU                  (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly = false);
    //int executePutConfigDNP                (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, MessageList &vgList, MessageList &retList, OutMessageList &outList);

    void reportPointData(const CtiPointType_t pointType, const int pointOffset, CtiReturnMsg &ReturnMsg, const INMESS &InMessage, const point_info &pi, const std::string pointName="");

    INT decodeGetValueKWH           ( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeGetValueDemand        ( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeGetValueMinMaxDemand  ( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeGetValueIED           ( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeGetValuePhaseCurrent  ( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeGetConfigIED          ( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeGetStatusInternal     ( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeGetStatusLoadProfile  ( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeGetStatusDNP          ( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeGetStatusFreeze       ( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeGetConfigIntervals    ( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeGetConfigChannelSetup ( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeGetConfigMultiplier   ( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeGetConfigModel        ( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );
    INT decodeGetConfigIedDnpAddress( INMESS *InMessage, CtiTime &TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList );

    int decodeGetValueIEDPrecannedTable11Peak(const CtiCommandParser &parse, const DSTRUCT &DSt, const CtiTime &TimeNow, const unsigned demand_offset, const std::string &demand_name, const unsigned consumption_offset, const std::string &consumption_name, CtiReturnMsg *ReturnMsg);

    bool isPrecannedTableCurrent() const;

    unsigned long convertTimestamp(unsigned long timestamp, const CtiDate &current_date=CtiDate()) const;

    static unsigned char computeResolutionByte(double lpResolution, double peakKwResolution, double lastIntervalDemandResolution);

    virtual INT   calcAndInsertLPRequests( OUTMESS *&OutMessage, OutMessageList &outList );
    virtual bool  calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage );

public:

    enum
    {
        ChannelCount  = 4,

        //  These should be private - and there should be an MCT470::executeGetStatus()
        FuncRead_LPStatusCh1Ch2Pos = 0x97,
        FuncRead_LPStatusCh3Ch4Pos = 0x9c,
        FuncRead_LPStatusLen       =   11,

        //  ditto
        FuncWrite_IEDCommand            = 0xd0,
        FuncWrite_IEDCommandLen         =    4,
        FuncWrite_IEDCommandWithData    = 0xd1,
    };

    Mct470Device( );
    Mct470Device( const Mct470Device &aRef );
    virtual ~Mct470Device( );

    Mct470Device &operator=( const Mct470Device &aRef );

    virtual ULONG calcNextLPScanTime( void );
    virtual void changeDeviceConfig(Config::DeviceConfigSPtr config);

    virtual void DecodeDatabaseReader(RowReader &rdr);
};

}
}

