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
* REVISION     :  $Revision: 1.17 $
* DATE         :  $Date: 2005/06/21 18:04:19 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_MCT410_H__
#define __DEV_MCT410_H__
#pragma warning( disable : 4786)


#include "dev_mct.h"
#include <map>

class IM_EX_DEVDB CtiDeviceMCT410 : public CtiDeviceMCT
{
protected:

    //  forward declaration
    typedef map<unsigned long, pair<PointQuality_t, int> > QualityMap;  //  the int will hold ErrorClasses OR'd together

private:

    static const DLCCommandSet _commandStore;

    static QualityMap initErrorQualities( void );

    typedef CtiDeviceMCT Inherited;

protected:

    enum Commands
    {
        Command_Connect          = 0x4C,
        Command_Disconnect       = 0x4D
    };

    enum MemoryLocations
    {
        Memory_StatusPos         = 0x05,
        Memory_StatusLen         =    5,

        Memory_PowerfailCountPos = 0x23,
        Memory_PowerfailCountLen =    2,

        Memory_AlarmsPos         = 0x15,
        Memory_AlarmsLen         =    2,

        Memory_IntervalsPos      = 0x1a,
        Memory_IntervalsLen      =    4,

        Memory_LastFreezePos     = 0x26,
        Memory_LastFreezeLen     =   10,

        Memory_RTCPos            = 0x40,
        Memory_RTCLen            =    4,

        Memory_LastTSyncPos      = 0x44,
        Memory_LastTSyncLen      =    4
    };

    enum Functions
    {
        FuncWrite_IntervalsPos    = 0x03,
        FuncWrite_IntervalsLen    =    4,

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

        FuncRead_LLPPeakDayPos        = 0xa0,
        FuncRead_LLPPeakHourPos       = 0xa1,
        FuncRead_LLPPeakIntervalPos   = 0xa2,
        FuncRead_LLPPeakLen           =   13,

        FuncWrite_DisconnectConfigPos = 0xfe,
        FuncWrite_DisconnectConfigLen =    6,

        FuncWrite_LLPInterestPos      = 0x05,
        FuncWrite_LLPInterestLen      =    6,

        FuncWrite_LLPPeakInterestPos  = 0x06,
        FuncWrite_LLPPeakInterestLen  =    7,

        FuncRead_DisconnectConfigPos  = 0xFE,
        FuncRead_DisconnectConfigLen  =    9,

        FuncRead_DisconnectStatusPos  = 0xFE,
        FuncRead_DisconnectStatusLen  =    1
    };

    enum ValueType
    {
        ValueType_Voltage,
        ValueType_KW,
        ValueType_LoadProfile_Voltage,
        ValueType_LoadProfile_KW,
        ValueType_Accumulator,
        ValueType_Raw
    };

    enum ErrorClasses
    {
        EC_MeterReading    = 0x0001,
        EC_DemandReading   = 0x0002,
        EC_TOUDemand       = 0x0004,
        EC_TOUFrozenDemand = 0x0008,
        EC_LoadProfile     = 0x0010
    };

    enum
    {
        MCT410_PointOffset_Voltage       =    4,
        MCT410_PointOffset_MaxVoltage    =   14,
        MCT410_PointOffset_MinVoltage    =   15,

        MCT410_PointOffset_Analog_Outage =  100,

        MCT4XX_PointOffset_PeakOffset   = 10,
        MCT4XX_PointOffset_FrozenOffset = 10
    };

    enum
    {
        MCT4XX_LPChannels       =    4,
        MCT410_LPVoltageChannel =    4,
        MCT4XX_LPRecentBlocks   =   16,

        MCT410_Sspec            = 1029,
        MCT410_Min_NewOutageRev =    8,
        MCT410_Max_NewOutageRev =   30,

        MCT4XX_DawnOfTime       = 0x386d4380  //  jan 1, 2000
                                              //  if 81c99f60 is Rogue Wave's jan 1, 1970
    };

    //  this is more extensible than a pair
    struct point_info_t
    {
        double value;
        PointQuality_t quality;
        //  this could hold a timestamp someday if i get really adventurous
    };

    unsigned char crc8(const unsigned char *buf, unsigned int len);
    point_info_t  getData(unsigned char *buf, int len, ValueType vt=ValueType_KW);
    static  const QualityMap _errorQualities;

    CtiPointDataMsg *makePointDataMsg(CtiPoint *p, const point_info_t &pi, const RWCString &pointString);

    bool _intervalsSent;

    struct lp_info_t
    {
        unsigned long archived_reading;
        unsigned long current_request;
        unsigned long current_schedule;
    } _lp_info[MCT4XX_LPChannels];

    struct llp_interest_t
    {
        unsigned long time;
        int offset;
        int channel;
    } _llpInterest;

    struct llp_peak_report_interest_t
    {
        unsigned long time;
        int channel;
        int period;
        int command;
    } _llpPeakInterest;

    int _sspec, _rev;

public:

    enum
    {
        MCT410_ChannelCount = 3,

        UniversalAddress = 4194012,

        MCT4XX_Command_FreezeVoltageOne = 0x59,
        MCT4XX_Command_FreezeVoltageTwo = 0x5A,

        MCT4XX_Command_PowerfailReset = 0x89,
        MCT4XX_Command_Reset          = 0x8A,

        FuncWrite_TSyncPos       = 0xf0,
        FuncWrite_TSyncLen       =    6
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

    static DLCCommandSet initCommandStore( );
    virtual bool getOperation( const UINT &cmd,  USHORT &function, USHORT &length, USHORT &io );

    void sendIntervals( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList );

    virtual ULONG calcNextLPScanTime( void );
    virtual INT   calcAndInsertLPRequests( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList );
    virtual bool  calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage );

    virtual INT executeGetValue(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist<CtiMessage>&vgList, RWTPtrSlist<CtiMessage>&retList, RWTPtrSlist<OUTMESS>&outList);

    virtual INT ResultDecode( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList );

    INT decodeGetValueKWH                   ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetValueDemand                ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetValuePeakDemand            ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetValueVoltage               ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetValueOutage                ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetValueLoadProfile           ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetValueLoadProfilePeakReport ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeScanLoadProfile               ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetStatusInternal             ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetStatusLoadProfile          ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetConfigTime                 ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetConfigIntervals            ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetConfigModel                ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetConfigDisconnect           ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
};
#endif // #ifndef __DEV_MCT410_H__
