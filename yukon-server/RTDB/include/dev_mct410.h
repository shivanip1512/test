/*-----------------------------------------------------------------------------*
*
* File:   dev_MCT410
*
* Class:  CtiDeviceMCT410
* Date:   4/24/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_MCT410.h-arc  $
* REVISION     :  $Revision: 1.39 $
* DATE         :  $Date: 2006/08/10 15:27:10 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_MCT410_H__
#define __DEV_MCT410_H__
#pragma warning( disable : 4786)


#include "dev_mct.h"
#include "dev_mct4xx.h"
#include <map>

class IM_EX_DEVDB CtiDeviceMCT410 : public CtiDeviceMCT4xx
{
private:

    static const CommandSet _commandStore;
    static CommandSet initCommandStore();

    static const DynamicPaoAddressing_t         _dynPaoAddressing;
    static const DynamicPaoFunctionAddressing_t _dynPaoFuncAddressing;

    static const ConfigPartsList _config_parts;
    static ConfigPartsList initConfigParts();

    typedef CtiDeviceMCT4xx Inherited;

protected:

    virtual bool getOperation( const UINT &cmd,  USHORT &function, USHORT &length, USHORT &io );

    enum Commands
    {
        Command_Connect          = 0x4c,
        Command_Disconnect       = 0x4d
    };

    enum MemoryLocations
    {
        Memory_SSpecPos           = 0x00,
        Memory_SSpecLen           =    1,

        Memory_RevisionPos        = 0x01,
        Memory_RevisionLen        =    1,

        Memory_OptionsPos         = 0x02,
        Memory_OptionsLen         =    1,

        Memory_ConfigurationPos   = 0x03,
        Memory_ConfigurationLen   =    1,

        Memory_StatusPos          = 0x05,
        Memory_StatusLen          =    5,

        Memory_EventFlagsMask1Pos = 0x0A,
        Memory_EventFlagsMask1Len =    1,

        Memory_EventFlagsMask2Pos = 0x0B,
        Memory_EventFlagsMask2Len =    1,

        Memory_MeterAlarmMaskPos    = 0x0C,
        Memory_MeterAlarmMaskLen    =    2,

        Memory_CentronParametersPos = 0x0f,
        Memory_CentronParametersLen =    1,

        Memory_CentronMultiplierPos = 0x19,
        Memory_CentronMultiplierLen =    1,

        Memory_AddressingPos      = 0x13,
        Memory_AddressingLen      =    6,

        Memory_BronzeAddressPos   = 0x13,
        Memory_BronzeAddressLen   =    1,

        Memory_LeadAddressPos     = 0x14,
        Memory_LeadAddressLen     =    2,

        Memory_CollectionAddressPos = 0x16,
        Memory_CollectionAddressLen =    2,

        Memory_SPIDAddressPos     = 0x18,
        Memory_SPIDAddressLen     =    1,

        Memory_AlarmsPos          = 0x15,
        Memory_AlarmsLen          =    2,

        Memory_IntervalsPos       = 0x1a,
        Memory_IntervalsLen       =    4,

        Memory_DemandIntervalPos  = 0x1a,
        Memory_DemandIntervalLen  =    1,

        Memory_LoadProfileIntervalPos   = 0x1b,
        Memory_LoadProfileIntervalLen   =    1,

        Memory_VoltageDemandIntervalPos = 0x1C,
        Memory_VoltageDemandIntervalLen =    1,

        Memory_VoltageLPIntervalPos = 0x1D,
        Memory_VoltageLPIntervalLen =    1,

        Memory_OverVThresholdPos  = 0x1e,
        Memory_OverVThresholdLen  =    2,

        Memory_UnderVThresholdPos = 0x20,
        Memory_UnderVThresholdLen =    2,

        Memory_OutageCyclesPos    = 0x22,
        Memory_OutageCyclesLen    =    1,

        Memory_PowerfailCountPos  = 0x23,
        Memory_PowerfailCountLen  =    2,

        Memory_LastFreezePos      = 0x26,
        Memory_LastFreezeLen      =   10,

        Memory_LLPStartTimePos    = 0x30,
        Memory_LLPStartTimeLen    =    4,

        Memory_LLPChannelPos      = 0x34,
        Memory_LLPChannelLen      =    1,

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

        Memory_TOUDayTablePos     = 0x50,
        Memory_TOUDayTableLen     =    2,

        Memory_TOUDailySched1Pos  = 0x52,
        Memory_TOUDailySched1Len  =    7,

        Memory_TOUDailySched2Pos  = 0x59,
        Memory_TOUDailySched2Len  =    7,

        Memory_TOUDailySched3Pos  = 0x60,
        Memory_TOUDailySched3Len  =    7,

        Memory_TOUDailySched4Pos  = 0x67,
        Memory_TOUDailySched4Len  =    7,

        Memory_DefaultTOURatePos  = 0x6e,
        Memory_DefaultTOURateLen  =    1,

        Memory_Holiday1Pos        = 0xd0,
        Memory_Holiday1Len        =    4,

        Memory_Holiday2Pos        = 0xd4,
        Memory_Holiday2Len        =    4,

        Memory_Holiday3Pos        = 0xd8,
        Memory_Holiday3Len        =    4,

    };

    enum Functions
    {
        FuncRead_OutagePos        = 0x10,
        FuncRead_OutageLen        =   13,

        FuncRead_MReadPos         = 0x90,
        FuncRead_MReadLen         =    9,

        FuncRead_FrozenMReadPos   = 0x91,
        FuncRead_FrozenMReadLen   =   10,

        FuncRead_DemandPos        = 0x92,
        FuncRead_DemandLen        =   10,  //  brings back recent demand, avg. voltage, blink counter, and channels 2 and 3 (may be trimmed by executeRequest)

        FuncRead_PeakDemandPos    = 0x93,
        FuncRead_PeakDemandLen    =    9,  //  peak demand, time of peak, current meter reading

        FuncRead_FrozenPos        = 0x94,
        FuncRead_FrozenLen        =   10,

        FuncRead_VoltagePos       = 0x95,
        FuncRead_VoltageLen       =   12,  //  max and min voltages

        FuncRead_FrozenVoltagePos = 0x96,
        FuncRead_FrozenVoltageLen =   13,  //  max and min voltages plus freeze count

        FuncRead_LPStatusPos      = 0x97,
        FuncRead_LPStatusLen      =   12,

        FuncRead_TOUBasePos          = 0xb0,
        FuncRead_TOULen              =    9,
        FuncRead_TOUFrozenOffset     =    4,

        FuncRead_LLPStatusPos        = 0x9d,
        FuncRead_LLPStatusLen        =    8,

        FuncRead_LLPPeakDayPos       = 0xa0,
        FuncRead_LLPPeakHourPos      = 0xa1,
        FuncRead_LLPPeakIntervalPos  = 0xa2,
        FuncRead_LLPPeakLen          =   13,

        FuncRead_TOUDaySchedulePos   = 0xAD,
        FuncRead_TOUDayScheduleLen   =   11,

        FuncRead_TOUStatusPos           = 0xad,
        FuncRead_TOUStatusLen           =   11,
        FuncRead_TOUSwitchSchedule12Pos = 0xae,
        FuncRead_TOUSwitchSchedule12Len =   13,
        FuncRead_TOUSwitchSchedule34Pos = 0xaf,
        FuncRead_TOUSwitchSchedule34Len =   13,

        FuncRead_DisconnectConfigPos = 0xfe,
        FuncRead_DisconnectConfigLen =    9,

        FuncRead_DisconnectStatusPos = 0xfe,
        FuncRead_DisconnectStatusLen =    1,

        FuncWrite_ConfigAlarmMaskPos = 0x01,
        FuncWrite_ConfigAlarmMaskLen = 0x06,

        FuncWrite_IntervalsPos       = 0x03,
        FuncWrite_IntervalsLen       =    4,

        FuncWrite_LLPStoragePos      = 0x04,
        FuncWrite_LLPStorageLen      =    5,

        FuncWrite_LLPPeakInterestPos = 0x06,
        FuncWrite_LLPPeakInterestLen =    7,

        FuncWrite_TOUSchedule1Pos    = 0x30,
        FuncWrite_TOUSchedule1Len    =   15,

        FuncWrite_TOUSchedule2Pos    = 0x31,
        FuncWrite_TOUSchedule2Len    =   15,

        FuncWrite_CentronReadingPos    = 0xf2,
        FuncWrite_CentronReadingLen    =    8,

        FuncWrite_CentronParametersPos = 0xf3,
        FuncWrite_CentronParametersLen =    3,

        FuncWrite_DisconnectConfigPos  = 0xfe,
        FuncWrite_DisconnectConfigLen  =    8,

    };

    enum PointOffsets
    {
        MCT410_PointOffset_Voltage       =    4,
        MCT410_PointOffset_MaxVoltage    =   14,
        MCT410_PointOffset_MinVoltage    =   15,

        MCT410_PointOffset_Analog_Outage =  100,

        MCT410_PointOffset_TOUBase       =  100,  //  this is okay because TOU only has peak and frozen demand - it must start at 111 anyway
    };

    enum SspecInformation
    {
        MCT410_Sspec = 1029,

        MCT410_SspecRev_NewLLP_Min       =    8,
        MCT410_SspecRev_TOUPeak_Min      =   13,
        MCT410_SspecRev_NewOutage_Min    =    8,
        MCT410_SspecRev_NewOutage_Max    =   30,
        MCT410_SspecRev_Disconnect_Min   =    8,
        MCT410_SspecRev_Disconnect_Cycle =   12,
    };

    enum ChannelIdentifiers
    {
        Channel_Voltage = 4,
    };

    long getLoadProfileInterval(unsigned channel);
    point_info_t getLoadProfileData(unsigned channel, unsigned char *buf, unsigned len);

    bool _intervalsSent;

    static DynamicPaoAddressing_t         initDynPaoAddressing();
    static DynamicPaoFunctionAddressing_t initDynPaoFuncAddressing();
    void getDynamicPaoAddressing(int address, int &foundAddress, int &foundLength, CtiTableDynamicPaoInfo::Keys &foundKey);
    void getDynamicPaoFunctionAddressing(int function, int address, int &foundAddress, int &foundLength, CtiTableDynamicPaoInfo::Keys &foundKey);

    virtual INT executeGetValue (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList);
    virtual INT executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList);
    virtual INT executePutConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList);

    CtiDeviceMCT4xx::ConfigPartsList getPartsList();

    int executePutConfigDemandLP  (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList);
    int executePutConfigDisconnect(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList);
    int executePutConfigOptions   (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList);
    int executePutConfigCentron   (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList);
    int executePutConfigTOU       (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* >&vgList, list< CtiMessage* >&retList, list< OUTMESS * > &outList);

    virtual INT ModelDecode( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

    INT decodeGetValueKWH                   ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValueDemand                ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValuePeakDemand            ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValueVoltage               ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValueOutage                ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValueFreezeCounter         ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValueLoadProfilePeakReport ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetStatusInternal             ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetStatusLoadProfile          ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetConfigTOU                  ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetConfigTime                 ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetConfigCentron              ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetConfigIntervals            ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetConfigModel                ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetConfigDisconnect           ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

public:

    enum
    {
        MCT410_ChannelCount = 3,
    };

    enum Disconnect_Raw
    {
        MCT410_RawStatus_Connected               = 0x00,
        MCT410_RawStatus_ConnectArmed            = 0x01,
        MCT410_RawStatus_DisconnectedUnconfirmed = 0x02,
        MCT410_RawStatus_DisconnectedConfirmed   = 0x03
    };

    enum Disconnect_StateGroup
    {
        MCT410_StateGroup_DisconnectedConfirmed   = 0,
        MCT410_StateGroup_Connected               = 1,
        MCT410_StateGroup_DisconnectedUnconfirmed = 2,
        MCT410_StateGroup_ConnectArmed            = 3
    };

    CtiDeviceMCT410( );
    CtiDeviceMCT410( const CtiDeviceMCT410 &aRef );
    virtual ~CtiDeviceMCT410( );

    CtiDeviceMCT410 &operator=( const CtiDeviceMCT410 &aRef );

    void setDisconnectAddress( unsigned long address );

    void sendIntervals( OUTMESS *&OutMessage, list< OUTMESS* > &outList );

    virtual ULONG calcNextLPScanTime( void );
    virtual INT   calcAndInsertLPRequests( OUTMESS *&OutMessage, list< OUTMESS* > &outList );
    virtual bool  calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage );

};
#endif // #ifndef __DEV_MCT410_H__
