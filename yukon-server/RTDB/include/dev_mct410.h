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
* REVISION     :  $Revision: 1.59 $
* DATE         :  $Date: 2007/05/31 20:28:37 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_MCT410_H__
#define __DEV_MCT410_H__
#pragma warning( disable : 4786)


#include "dev_mct4xx.h"
#include <map>

class IM_EX_DEVDB CtiDeviceMCT410 : public CtiDeviceMCT4xx
{
private:

    typedef CtiDeviceMCT4xx Inherited;

    static const CommandSet _commandStore;
    static CommandSet initCommandStore();

    static const DynamicPaoAddressing_t         _dynPaoAddressing;
    static const DynamicPaoFunctionAddressing_t _dynPaoFuncAddressing;

    static const ConfigPartsList _config_parts;
    static ConfigPartsList initConfigParts();

    static string describeStatusAndEvents(unsigned char *buf);

    static DynamicPaoAddressing_t         initDynPaoAddressing();
    static DynamicPaoFunctionAddressing_t initDynPaoFuncAddressing();

    struct daily_read_info_t
    {
        unsigned long single_day;

        unsigned long multi_day_start;
        unsigned long multi_day_end;

        unsigned channel;

        long in_progress;
        bool retry;
        bool failed;

        enum current_request_t
        {
            Request_None,
            Request_SingleDayCh1,
            Request_SingleDayCh2,
            Request_SingleDayCh3,
            Request_MultiDay,
            Request_RecentCh1,

        } current_request;

    } _daily_read_info;

protected:

    virtual bool getOperation( const UINT &cmd,  BSTRUCT &bst ) const;

    enum Commands
    {
        Command_Connect          = 0x4c,
        Command_Disconnect       = 0x4d
    };

    enum MemoryMap
    {
        Memory_RevisionPos          = 0x01,
        Memory_RevisionLen          =    1,

        Memory_StatusPos            = 0x05,
        Memory_StatusLen            =    5,

        Memory_EventFlagsMask1Pos   = 0x0a,
        Memory_EventFlagsMask1Len   =    1,

        Memory_EventFlagsMask2Pos   = 0x0b,
        Memory_EventFlagsMask2Len   =    1,

        Memory_MeterAlarmMaskPos    = 0x0c,
        Memory_MeterAlarmMaskLen    =    2,

        Memory_CentronParametersPos = 0x0f,
        Memory_CentronParametersLen =    1,

        Memory_CentronConfigPos     = 0x0f,  //  this combines the Parameters and Multiplier read;
        Memory_CentronConfigLen     =   11,  //    it may be less reliable on noisy circuits, since it requires 3 D words

        Memory_CentronMultiplierPos = 0x19,
        Memory_CentronMultiplierLen =    1,

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
        FuncRead_Channel1SingleDayLen       =    9,
        FuncRead_Channel1SingleDayMaxOffset = 0x07,

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

        FuncRead_DisconnectConfigPos = 0xfe,
        FuncRead_DisconnectConfigLen =   11,

        FuncRead_DisconnectStatusPos = 0xfe,
        FuncRead_DisconnectStatusLen =    1,

        FuncWrite_ConfigAlarmMaskPos = 0x01,
        FuncWrite_ConfigAlarmMaskLen = 0x06,

        FuncWrite_IntervalsPos       = 0x03,
        FuncWrite_IntervalsLen       =    4,

        FuncWrite_DailyReadInterestPos = 0x50,
        FuncWrite_DailyReadInterestLen =    4,

        FuncWrite_SetAddressPos        = 0xf1,
        FuncWrite_SetAddressLen        =    4,

        FuncWrite_CentronReadingPos    = 0xf2,
        FuncWrite_CentronReadingLen    =    8,

        FuncWrite_CentronParametersPos = 0xf3,
        FuncWrite_CentronParametersLen =    3,

        FuncWrite_DisconnectConfigPos  = 0xfe,
        FuncWrite_DisconnectConfigLen  =    8,
    };

    enum PointOffsets
    {
        PointOffset_Voltage       =    4,
        PointOffset_VoltageMax    =   14,
        PointOffset_VoltageMin    =   15,

        PointOffset_Analog_Outage =  100,
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
        ValueType_LoadProfile_Voltage,
        ValueType_LoadProfile_DynamicDemand,
    };

    virtual point_info getDemandData(unsigned char *buf, int len) const;

    point_info getData(unsigned char *buf, int len, ValueType410 vt) const;

    int makeDynamicDemand(double input) const;

    long getLoadProfileInterval(unsigned channel);
    point_info getLoadProfileData(unsigned channel, unsigned char *buf, unsigned len);

    bool _intervalsSent;

    void getDynamicPaoAddressing(int address, int &foundAddress, int &foundLength, CtiTableDynamicPaoInfo::Keys &foundKey);
    void getDynamicPaoFunctionAddressing(int function, int address, int &foundAddress, int &foundLength, CtiTableDynamicPaoInfo::Keys &foundKey);

    void returnErrorMessage( int retval, const CtiOutMessage *om, list< CtiMessage* > &retList, const string &error ) const;

    virtual INT executeGetValue ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList );
    virtual INT executeGetConfig( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList );
    virtual INT executePutConfig( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList );

    CtiDeviceMCT4xx::ConfigPartsList getPartsList();

    int executePutConfigDemandLP  ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage * >&vgList, list< CtiMessage * >&retList, list< OUTMESS * > &outList );
    int executePutConfigDisconnect( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage * >&vgList, list< CtiMessage * >&retList, list< OUTMESS * > &outList );
    int executePutConfigOptions   ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage * >&vgList, list< CtiMessage * >&retList, list< OUTMESS * > &outList );
    int executePutConfigCentron   ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage * >&vgList, list< CtiMessage * >&retList, list< OUTMESS * > &outList );

    virtual INT ModelDecode( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList );
    virtual INT ErrorDecode( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage * > &vgList, list< CtiMessage * > &retList, list< OUTMESS * > &outList );

    INT decodeGetValueKWH                   ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValueDemand                ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValueVoltage               ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValueOutage                ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValueFreezeCounter         ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValueLoadProfilePeakReport ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetValueDailyRead             ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetStatusInternal             ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetStatusLoadProfile          ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetConfigCentron              ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetConfigIntervals            ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetConfigModel                ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetConfigDisconnect           ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    INT decodeGetConfigAddress              ( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

    virtual INT   calcAndInsertLPRequests( OUTMESS *&OutMessage, list< OUTMESS* > &outList );
    virtual bool  calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage );

    void sendIntervals( OUTMESS *&OutMessage, list< OUTMESS* > &outList );

public:

    enum
    {
        ChannelCount = 3,
    };

    enum SspecInformation
    {
        Sspec = 1029,

        SspecRev_NewLLP_Min         =    9,  //  rev  0.9
        SspecRev_TOUPeak_Min        =   13,  //  rev  1.3
        SspecRev_NewOutage_Min      =    8,  //  rev  0.8
        SspecRev_NewOutage_Max      =  100,  //  rev 10.0
        SspecRev_Disconnect_Min     =    8,  //  rev  0.8
        SspecRev_Disconnect_Cycle   =   12,  //  rev  1.2
        SspecRev_Disconnect_ConfigReadEnhanced = 20,  //  rev 2.0
        SspecRev_DailyRead          =   21,  //  rev  2.1

        SspecRev_BetaLo =    9,  //  rev  0.9
        SspecRev_BetaHi =  251,  //  rev 25.1
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

    CtiDeviceMCT410( );
    CtiDeviceMCT410( const CtiDeviceMCT410 &aRef );
    virtual ~CtiDeviceMCT410( );

    CtiDeviceMCT410 &operator=( const CtiDeviceMCT410 &aRef );

    void setDisconnectAddress( unsigned long address );

    virtual ULONG calcNextLPScanTime( void );

};

#endif // #ifndef __DEV_MCT410_H__
