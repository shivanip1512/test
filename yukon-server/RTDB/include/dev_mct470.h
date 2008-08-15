/*-----------------------------------------------------------------------------*
*
* File:   dev_mct470
*
* Class:  CtiDeviceMCT470
* Date:   2005-jan-03
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_MCT470.h-arc  $
* REVISION     :  $Revision: 1.48 $
* DATE         :  $Date: 2008/08/15 13:08:05 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_MCT470_H__
#define __DEV_MCT470_H__
#pragma warning( disable : 4786)


#include "dev_mct4xx.h"
#include <map>

class IM_EX_DEVDB CtiDeviceMCT470 : public CtiDeviceMCT4xx
{
private:

    typedef CtiDeviceMCT4xx Inherited;

    static const CommandSet       _commandStore;
    static       CommandSet       initCommandStore();

    static const read_key_store_t _readKeyStore;
    static       read_key_store_t initReadKeyStore();

    static const ConfigPartsList  _config_parts;
    static       ConfigPartsList  initConfigParts();


    CtiTableDeviceMCTIEDPort _iedPort;
    CtiTime                  _iedTime;

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

        PointOffset_AveragePowerFactor = 31,

        PointOffset_OutageCount = 40,

        PointOffset_VoltsPhaseA = 41,
        PointOffset_VoltsPhaseB = 42,
        PointOffset_VoltsPhaseC = 43,

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

    long       getLoadProfileInterval( unsigned channel );
    point_info getLoadProfileData    ( unsigned channel, unsigned char *buf, unsigned len );

    long _lastConfigRequest;

    void decodeDNPRealTimeRead(BYTE *buffer, int readNumber, string &resultString, CtiReturnMsg *ReturnMsg, INMESS *InMessage);
    void getBytesFromString(string &values, BYTE* buffer, int buffLen, int &numValues, int fillCount, int bytesPerValue);
    int sendDNPConfigMessages(int startMCTID,  list< OUTMESS * > &outList, OUTMESS *&OutMessage, string &dataA, string &dataB, CtiTableDynamicPaoInfo::Keys key, bool force, bool verifyOnly);

    bool computeMultiplierFactors(double multiplier, unsigned &numerator, unsigned &denominator) const;
    string describeChannel(unsigned char channel_config) const;

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

    static IED_Types resolveIEDType(const string &iedType);
    static string    resolveIEDName(int bits);
    static string    resolveDNPStatus(int status);

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

    virtual point_info getDemandData(unsigned char *buf, int len) const;

    point_info getData(unsigned char *buf, int len, ValueType470 vt) const;

    static const error_set _error_info_old_lp;
    static const error_set _error_info_lgs4;
    static const error_set _error_info_alphaa3;
    static const error_set _error_info_alphapp;
    static const error_set _error_info_gekv;
    static const error_set _error_info_sentinel;

    static error_set initErrorInfoOldLP   ( void );
    static error_set initErrorInfoLGS4    ( void );
    static error_set initErrorInfoAlphaA3 ( void );
    static error_set initErrorInfoAlphaPP ( void );
    static error_set initErrorInfoGEkV    ( void );
    static error_set initErrorInfoSentinel( void );

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

        Memory_ModelLen             =    5,

        //  lengths are different for these
        Memory_StatusPos            = 0x05, // CtiDeviceMCT410::Memory_StatusPos,
        Memory_StatusLen            =    3,
    };

    enum Functions
    {
        FuncWrite_ConfigAlarmMaskLen    =   3,  //  func write 0x01

        FuncWrite_IntervalsPos          = 0x03, // CtiDeviceMCT410::FuncWrite_IntervalsPos,
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
    void requestDynamicInfo(CtiTableDynamicPaoInfo::Keys key, OUTMESS *&OutMessage, list< OUTMESS* > &outList);

    void sendIntervals(OUTMESS *&OutMessage, list< OUTMESS* > &outList);

    virtual const read_key_store_t &getReadKeyStore(void) const;

    virtual INT ModelDecode( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    virtual INT ErrorDecode( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, bool &overrideExpectMore );

    virtual INT executeScan     (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    virtual INT executeGetValue (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    virtual INT executePutValue (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    virtual INT executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    virtual INT executePutConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);

    CtiDeviceMCT4xx::ConfigPartsList getPartsList();
    CtiDeviceMCT470::ConfigPartsList getBasicPartsList();

    int executePutConfigLoadProfileChannel(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    //int executePutConfigRelays            (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    //int executePutConfigPrecannedTable    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    int executePutConfigDemandLP          (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    //int executePutConfigDisconnect        (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    //int executePutConfigOptions           (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    //int executePutConfigDNP               (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);

    INT decodeGetValueKWH          ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValueDemand       ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValueMinMaxDemand ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValueIED          ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetConfigIED         ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetStatusInternal    ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetStatusLoadProfile ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetStatusDNP         ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetStatusFreeze      ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetConfigIntervals   ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetConfigChannelSetup( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetConfigMultiplier  ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetConfigModel       ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

    enum
    {
        Memory_ChannelOffset = 0x1a,

        MaxIEDReadAge = 600,  //  in seconds

        MCT430A_Sspec       = 1037,
        MCT430S_Sspec       = 1046,

        Sspec          = 1030,
        SspecRev_Min    =    5,  //  rev  0.5
        SspecRev_Max    =  255,  //  rev 25.5
        SspecRev_BetaLo =    9,  //  rev  0.9
        SspecRev_BetaHi =  200,  //  rev 20.0

        SspecRev_IED_ZeroWriteMin    = 13,  //  rev 1.3
        SspecRev_IED_ErrorPadding    = 14,  //  rev 1.4
        SspecRev_IED_LPExtendedRange = 26,  //  rev 2.6
    };

    virtual INT   calcAndInsertLPRequests( OUTMESS *&OutMessage, list< OUTMESS* > &outList );
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

    CtiDeviceMCT470( );
    CtiDeviceMCT470( const CtiDeviceMCT470 &aRef );
    virtual ~CtiDeviceMCT470( );

    CtiDeviceMCT470 &operator=( const CtiDeviceMCT470 &aRef );

    virtual ULONG calcNextLPScanTime( void );

    virtual void DecodeDatabaseReader( RWDBReader &rdr );
};

#endif // #ifndef __DEV_MCT470_H__
