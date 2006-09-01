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
* REVISION     :  $Revision: 1.23 $
* DATE         :  $Date: 2006/09/01 18:44:54 $
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

    static const CommandSet _commandStore;
    static CommandSet initCommandStore();

    static const DynamicPaoAddressing_t         _dynPaoAddressing;
    static const DynamicPaoFunctionAddressing_t _dynPaoFuncAddressing;

    static CtiDeviceMCT4xx::ConfigPartsList initConfigParts();
    static const CtiDeviceMCT4xx::ConfigPartsList _config_parts;

    CtiTableDeviceMCTIEDPort _iedPort;
    CtiTime                  _iedTime;

    enum DNP_MCT_Offsets
    {
        MCT470_DNP_MCTPoint_RealTimeBinary       =   0,
        MCT470_DNP_MCTPoint_PreCannedBinary      =  30,
        MCT470_DNP_MCTPoint_RealTimeAnalog       =  43,
        MCT470_DNP_MCTPoint_PreCannedAnalog      =  49,
        MCT470_DNP_MCTPoint_RealTimeAccumulator  = 114,
        MCT470_DNP_MCTPoint_PreCannedAccumulator = 118,
    };

    enum IED_PointOffsets
    {
        PointOffset_TotalKWH    =  1,
        PointOffset_TOU_KWBase  =  2,
        PointOffset_TotalKW     = 10,
        PointOffset_TotalKMH    = 11,
        PointOffset_TOU_KMBase  = 12,
        PointOffset_TotalKM     = 20,

        PointOffset_VoltsPhaseA = 41,
        PointOffset_VoltsPhaseB = 42,
        PointOffset_VoltsPhaseC = 43,

        PointOffset_DNPStatus_RealTime1   = 501,
        PointOffset_DNPStatus_RealTime2   = 516,
        PointOffset_DNPStatus_PrecannedStart = 531,
        PointOffset_DNPAnalog_RealTime1   = 501,
        PointOffset_DNPAnalog_RealTime2   = 504,
        PointOffset_DNPAnalog_Precanned1  = 506,
        PointOffset_DNPAnalog_Precanned2  = 512,
        PointOffset_DNPAnalog_Precanned3  = 518,
        PointOffset_DNPAnalog_Precanned4  = 524,
        PointOffset_DNPAnalog_Precanned5  = 530,
        PointOffset_DNPAnalog_Precanned6  = 536,
        PointOffset_DNPAnalog_Precanned7  = 542,
        PointOffset_DNPAnalog_Precanned8  = 548,
        PointOffset_DNPAnalog_Precanned9  = 654,
        PointOffset_DNPAnalog_Precanned10 = 660,
        PointOffset_DNPCounter_RealTime1  = 501,
        PointOffset_DNPCounter_RealTime2  = 504,
        PointOffset_DNPCounter_Precanned1 = 506,
        PointOffset_DNPCounter_Precanned2 = 512,
        PointOffset_DNPCounter_Precanned3 = 518,
        PointOffset_DNPCounter_Precanned4 = 524,
        PointOffset_DNPCounter_Precanned5 = 530,
        PointOffset_DNPCounter_Precanned6 = 536,
        PointOffset_DNPCounter_Precanned7 = 542,
        PointOffset_DNPCounter_Precanned8 = 548,
    };

    enum IED_Types
    {
        IED_None               = 0x00,
        IED_LandisGyrS4        = 0x10,
        IED_AlphaA1            = 0x20,
        IED_AlphaPowerPlus     = 0x30,
        IED_GeneralElectricKV  = 0x40,
        IED_GeneralElectricKV2 = 0x50,
        IED_Sentinel           = 0x60,

        IED_Mask               = 0xF0,
    };

    long getLoadProfileInterval( unsigned channel );
    long _lastConfigRequest;

    void decodeDNPRealTimeRead(BYTE *buffer, int readNumber, string &resultString, CtiReturnMsg *ReturnMsg);
    void getBytesFromString(string &values, BYTE* buffer, int buffLen, int &numValues, int fillCount, int bytesPerValue);
    int sendDNPConfigMessages(int startMCTID,  list< OUTMESS * > &outList, OUTMESS *&OutMessage, string &dataA, string &dataB, CtiTableDynamicPaoInfo::Keys key, bool force);
    string resolveDNPStatus(int status);

    //  this probably won't be used... ?
    /*
    struct dynamic_request_times
    {
        unsigned long sspec;
        unsigned long loadprofile_rate;
        unsigned long loadprofile_config;
        unsigned long ied_loadprofile_rate;
    } _dyn_request;
    */

protected:

    virtual bool getOperation( const UINT &cmd,  USHORT &function, USHORT &length, USHORT &io );

    typedef CtiDeviceMCT4xx Inherited;

    enum
    {
        //  new/changed stuff
        MCT470_Memory_OptionsPos           = 0x02,
        MCT470_Memory_OptionsLen           =    1,

        MCT470_Memory_ConfigurationPos     = 0x03,
        MCT470_Memory_ConfigurationLen     =    1,

        MCT470_Memory_EventFlagsMaskPos    = 0x08,
        MCT470_Memory_EventFlagsMaskLen    =    2,

        MCT470_Memory_EventFlagsMask1Pos   = 0x08,
        MCT470_Memory_EventFlagsMask1Len   =    1,

        MCT470_Memory_EventFlagsMask2Pos   = 0x09,
        MCT470_Memory_EventFlagsMask2Len   =    1,

        MCT470_Memory_AddressBronzePos     = 0x0D,
        MCT470_Memory_AddressBronzeLen     =    1,

        MCT470_Memory_AddressLeadPos       = 0x0E,
        MCT470_Memory_AddressLeadLen       =    2,

        MCT470_Memory_AddressCollectionPos = 0x10,
        MCT470_Memory_AddressCollectionLen =    2,

        MCT470_Memory_AddressSPIDPos       = 0x12,
        MCT470_Memory_AddressSPIDLen       =    1,

        MCT470_Memory_TimeAdjustTolerancePos = 0x1F,
        MCT470_Memory_TimeAdjustToleranceLen =    1,

        MCT470_Memory_DSTBeginPos          = 0x20,
        MCT470_Memory_DSTBeginLen          =    4,

        MCT470_Memory_DSTEndPos            = 0x24,
        MCT470_Memory_DSTEndLen            =    4,

        MCT470_Memory_TimeZoneOffsetPos    = 0x28,
        MCT470_Memory_TimeZoneOffsetLen    =    1,

        MCT470_Memory_RTCPos               = 0x29,
        MCT470_Memory_RTCLen               =    4,
        MCT470_Memory_LastTSyncPos         = 0x2d,
        MCT470_Memory_LastTSyncLen         =    4,

        MCT470_Memory_IntervalsPos         = 0x32,
        MCT470_Memory_IntervalsLen         =    3,

        MCT470_Memory_DemandIntervalPos    = 0x32,
        MCT470_Memory_DemandIntervalLen    =    1,

        MCT470_Memory_LoadProfileInterval1Pos    = 0x33,
        MCT470_Memory_LoadProfileInterval1Len    =    1,

        MCT470_Memory_LoadProfileInterval2Pos    = 0x34,
        MCT470_Memory_LoadProfileInterval2Len    =    1,

        MCT470_Memory_TableReadIntervalPos = 0x35,
        MCT470_Memory_TableReadIntervalLen =    1,

        MCT470_Memory_PrecannedMeterNumPos = 0x36,
        MCT470_Memory_PrecannedMeterNumLen =    1,

        MCT470_Memory_PrecannedTableTypePos = 0x37,
        MCT470_Memory_PrecannedTableTypeLen =    1,

        MCT470_Memory_RelayATimerPos       = 0x48,
        MCT470_Memory_RelayATimerLen       =    1,

        MCT470_Memory_RelayBTimerPos       = 0x49,
        MCT470_Memory_RelayBTimerLen       =    1,

        MCT470_Memory_TOUDayTablePos       = 0x50,
        MCT470_Memory_TOUDayTableLen       =    2,

        MCT470_Memory_TOUDaySchedule1Pos   = 0x52,
        MCT470_Memory_TOUDaySchedule1Len   =    7,

        MCT470_Memory_TOUDaySchedule2Pos   = 0x59,
        MCT470_Memory_TOUDaySchedule2Len   =    7,

        MCT470_Memory_TOUDaySchedule3Pos   = 0x60,
        MCT470_Memory_TOUDaySchedule3Len   =    7,

        MCT470_Memory_TOUDaySchedule4Pos   = 0x67,
        MCT470_Memory_TOUDaySchedule4Len   =    7,

        MCT470_Memory_TOUDefaultRatePos    = 0x6E,
        MCT470_Memory_TOUDefaultRateLen    =    1,

        MCT470_Memory_ChannelMultiplierPos = 0x88,
        MCT470_Memory_ChannelMultiplierLen =    4,

        MCT470_Memory_MeteringRatio1Pos    = 0x88,
        MCT470_Memory_MeteringRatio1Len    =    2,

        MCT470_Memory_KRatio1Pos           = 0x8A,
        MCT470_Memory_KRatio1Len           =    2,

        MCT470_Memory_ChannelConfig1Pos    = 0x8E,
        MCT470_Memory_ChannelConfig1Len    =    1,

        MCT470_Memory_MeteringRatio2Pos    = 0xA2,
        MCT470_Memory_MeteringRatio2Len    =    2,

        MCT470_Memory_KRatio2Pos           = 0xA4,
        MCT470_Memory_KRatio2Len           =    2,

        MCT470_Memory_ChannelConfig2Pos    = 0xA8,
        MCT470_Memory_ChannelConfig2Len    =    1,

        MCT470_Memory_MeteringRatio3Pos    = 0xBC,
        MCT470_Memory_MeteringRatio3Len    =    2,

        MCT470_Memory_KRatio3Pos           = 0xBE,
        MCT470_Memory_KRatio3Len           =    2,

        MCT470_Memory_ChannelConfig3Pos    = 0xC2,
        MCT470_Memory_ChannelConfig3Len    =    1,

        MCT470_Memory_MeteringRatio4Pos    = 0xD6,
        MCT470_Memory_MeteringRatio4Len    =    2,

        MCT470_Memory_KRatio4Pos           = 0xD8,
        MCT470_Memory_KRatio4Len           =    2,

        MCT470_Memory_ChannelConfig4Pos    = 0xDC,
        MCT470_Memory_ChannelConfig4Len    =    1,

        MCT470_Memory_Holiday1Pos          = 0xE0,
        MCT470_Memory_Holiday1Len          =    4,

        MCT470_Memory_Holiday2Pos          = 0xE4,
        MCT470_Memory_Holiday2Len          =    4,

        MCT470_Memory_Holiday3Pos          = 0xE8,
        MCT470_Memory_Holiday3Len          =    4,

        //  unchanged/copied
        MCT470_Memory_ModelPos             = 0x00,
        MCT470_Memory_ModelLen             =    5,

        //  lengths are different for these
        MCT470_Memory_StatusPos            = 0x03, // CtiDeviceMCT410::Memory_StatusPos,
        MCT470_Memory_StatusLen            =    3,

        MCT470_FuncWrite_ConfigAlarmMaskPos = 0x01, // CtiDeviceMCT410::FuncWrite_ConfigAlarmMaskPos,
        MCT470_FuncWrite_ConfigAlarmMaskLen =   3,

        MCT470_FuncWrite_IntervalsPos      = 0x03, // CtiDeviceMCT410::FuncWrite_IntervalsPos,
        MCT470_FuncWrite_IntervalsLen      =    3,

        MCT470_FuncWrite_RelaysPos         = 0x08,
        MCT470_FuncWrite_RelaysLen         =    3,

        MCT470_FuncWrite_LoadProfileChannelsPos = 0x07,
        MCT470_FuncWrite_LoadProfileChannelsLen =   13,

        MCT470_FuncWrite_PrecannedTablePos = 0xD3,
        MCT470_FuncWrite_PrecannedTableLen =    4,

        MCT470_FuncWrite_DNPReqTable       = 0xD6,

        MCT470_Memory_AddressingPos        = 0x0D,
        MCT470_Memory_AddressingLen        =    6,

        MCT470_FuncRead_ChannelSetupDataPos = 0x20,
        MCT470_FuncRead_ChannelSetupDataLen =    7,

        MCT470_FuncRead_LoadProfileChannel12Pos = 0x21,
        MCT470_FuncRead_LoadProfileChannel12Len =   10,

        MCT470_FuncRead_LoadProfileChannel34Pos = 0x22,
        MCT470_FuncRead_LoadProfileChannel34Len =   10,

        MCT470_FuncRead_PrecannedTablePos  = 0x23,
        MCT470_FuncRead_PrecannedTableLen  =   11,

        MCT470_FuncRead_IED_DNPTablePos    = 0x24,
        MCT470_FuncRead_IED_DNPTableLen    =   13,

        MCT470_FuncRead_IED_CRCPos         = 0x25,
        MCT470_FuncRead_IED_CRCLen         =   12,

        FuncRead_MReadLen           =   12,

        MCT470_FuncRead_MReadFrozenPos     = 0x91,
        MCT470_FuncRead_MReadFrozenLen     =   13,

        MCT470_FuncRead_DemandPos          = 0x92,
        MCT470_FuncRead_DemandLen          =   11,

        MCT470_FuncRead_PeakDemandBasePos  = 0x93,
        MCT470_FuncRead_PeakDemandLen      =   12,

        MCT470_Memory_TimeAdjustTolPos     = 0x1F,
        MCT470_Memory_TimeAdjustTolLen     =    1,

        MCT470_FuncRead_IED_Precanned_Base     = 0xc1,
        MCT470_FuncRead_IED_Precanned_Last     = 0xd4,
        MCT470_FuncRead_IED_TOU_CurrentKWBase  = 0xc1,
        MCT470_FuncRead_IED_TOU_CurrentKMBase  = 0xc5,
        MCT470_FuncRead_IED_TOU_CurrentTotals  = 0xc9,

        MCT470_FuncRead_IED_TOU_PreviousOffset =    9,

        MCT470_FuncRead_IED_TOU_MeterStatus    = 0xd3,

        MCT470_FuncRead_IED_RealTime           = 0xd5,
        MCT470_FuncRead_IED_RealTime2          = 0xda,

        MCT470_DNP_Analog_Precanned_Offset     = 1,
        MCT470_DNP_Status_Precanned_Offset     = 0,
        MCT470_DNP_Counter_Precanned_Offset = 11,
        MCT470_DNP_Counter_Precanned_Reads  = 8,
    };

    bool isLPDynamicInfoCurrent(void);
    void requestDynamicInfo(CtiTableDynamicPaoInfo::Keys key, OUTMESS *&OutMessage, list< OUTMESS* > &outList);

    void sendIntervals         (OUTMESS *&OutMessage, list< OUTMESS* > &outList);

    static DynamicPaoAddressing_t         initDynPaoAddressing();
    static DynamicPaoFunctionAddressing_t initDynPaoFuncAddressing();
    void getDynamicPaoAddressing(int address, int &foundAddress, int &foundLength, CtiTableDynamicPaoInfo::Keys &foundKey);
    void getDynamicPaoFunctionAddressing(int function, int address, int &foundAddress, int &foundLength, CtiTableDynamicPaoInfo::Keys &foundKey);

    virtual INT ModelDecode( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

    virtual INT executeScan     (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    virtual INT executeGetValue (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    virtual INT executePutValue (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    virtual INT executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    virtual INT executePutConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);

    CtiDeviceMCT4xx::ConfigPartsList getPartsList();

    int executePutConfigLoadProfileChannel(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    int executePutConfigRelays            (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    int executePutConfigPrecannedTable    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    int executePutConfigDemandLP          (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    int executePutConfigDisconnect        (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    int executePutConfigOptions           (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    int executePutConfigDNP               (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);

    INT decodeGetValueKWH          ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValueDemand       ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValuePeakDemand   ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValueIED          ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetConfigIED         ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetStatusInternal    ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetStatusLoadProfile ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetStatusDNP         ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetConfigIntervals   ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetConfigChannelSetup( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetConfigModel       ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

    enum
    {
        MCT470_Memory_ChannelOffset = 0x1a,

        MCT470_MaxIEDReadAge = 600,  //  in seconds

        MCT430A_Sspec       = 1037,
        MCT430S_Sspec       = 1046,

        MCT470_Sspec        = 1030,
        MCT470_SspecRevMin  =    5,  //  rev e
        MCT470_SspecRevMax  =   20,  //  rev t is max for now

        MCT470_SspecRev_IEDZeroWriteMin = 13,
        MCT470_SspecRev_IEDErrorPadding = 14,

        MCT470_FuncRead_ChannelSetupPos = 0x20,
        MCT470_FuncRead_ChannelSetupLen =    7,

        MCT470_FuncWrite_IEDCommandData        = 0xd1,
        MCT470_FuncWrite_IEDCommandDataBaseLen =    5,

        MCT470_FuncWrite_CurrentReading        = 0xd5,
        MCT470_FuncWrite_CurrentReadingLen     =    5,
    };

public:

    enum
    {
        ChannelCount  = 4,

        //  These should be private - and there should be an MCT470::executeGetStatus()
        MCT470_FuncRead_LPStatusCh1Ch2Pos = 0x97,
        MCT470_FuncRead_LPStatusCh3Ch4Pos = 0x9c,
        MCT470_FuncRead_LPStatusLen       =   11,

        //  ditto
        MCT470_FuncWrite_IEDCommand            = 0xd0,
        MCT470_FuncWrite_IEDCommandLen         =    4,
    };

    CtiDeviceMCT470( );
    CtiDeviceMCT470( const CtiDeviceMCT470 &aRef );
    virtual ~CtiDeviceMCT470( );

    CtiDeviceMCT470 &operator=( const CtiDeviceMCT470 &aRef );

    virtual ULONG calcNextLPScanTime( void );
    virtual INT   calcAndInsertLPRequests( OUTMESS *&OutMessage, list< OUTMESS* > &outList );
    virtual bool  calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage );

    virtual void DecodeDatabaseReader( RWDBReader &rdr );
};

#endif // #ifndef __DEV_MCT470_H__
