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
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2005/03/24 20:49:53 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_MCT470_H__
#define __DEV_MCT470_H__
#pragma warning( disable : 4786)


#include "dev_mct.h"
#include "dev_mct410.h"
#include <map>

class IM_EX_DEVDB CtiDeviceMCT470 : public CtiDeviceMCT410
{
public:

    enum
    {
        MCT470_ChannelCount =  4,

        MCT470_Memory_ChannelOffset = 0x1a,

        MCT470_FuncRead_LPStatusCh1Ch2Pos = 0x97,
        MCT470_FuncRead_LPStatusCh3Ch4Pos = 0x9c,
        MCT470_FuncRead_LPStatusLen       =   11
    };

protected:

    enum
    {
        //  new/changed stuff
        MCT470_Memory_RTCPos               = 0x29,
        MCT470_Memory_RTCLen               =    4,
        MCT470_Memory_LastTSyncPos         = 0x2d,
        MCT470_Memory_LastTSyncLen         =    4,

        MCT470_Memory_IntervalsPos         = 0x32,
        MCT470_Memory_IntervalsLen         =    4,

        MCT470_Memory_ChannelMultiplierPos = 0x88,
        MCT470_Memory_ChannelMultiplierLen =    4,

        //  unchanged/copied
        MCT470_Memory_ModelPos             = 0x00,
        MCT470_Memory_ModelLen             =    5,

        //  lengths are different for these
        MCT470_Memory_StatusPos            = CtiDeviceMCT410::Memory_StatusPos,
        MCT470_Memory_StatusLen            =    3,

        MCT470_FuncWrite_IntervalsPos      = CtiDeviceMCT410::FuncWrite_IntervalsPos,
        MCT470_FuncWrite_IntervalsLen      =    3,

        MCT470_FuncRead_MReadPos           = 0x90,
        MCT470_FuncRead_MReadLen           =   12,

        MCT470_FuncRead_MReadFrozenPos     = 0x91,
        MCT470_FuncRead_MReadFrozenLen     =   13,

        MCT470_FuncRead_DemandPos          = 0x92,
        MCT470_FuncRead_DemandLen          =   11,

        MCT470_FuncRead_CurrentPeak1Pos    = 0x93,
        MCT470_FuncRead_CurrentPeak1Len    =   10
    };

    void sendIntervals( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList );

private:

    static const DLCCommandSet _commandStore;
    bool _intervalsSent;

public:

    typedef CtiDeviceMCT Inherited;

    CtiDeviceMCT470( );
    CtiDeviceMCT470( const CtiDeviceMCT470 &aRef );
    virtual ~CtiDeviceMCT470( );

    CtiDeviceMCT470 &operator=( const CtiDeviceMCT470 &aRef );

    static DLCCommandSet initCommandStore( );
    virtual bool getOperation( const UINT &cmd,  USHORT &function, USHORT &length, USHORT &io );

    virtual ULONG calcNextLPScanTime( void );
    virtual INT   calcAndInsertLPRequests( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList );
    virtual bool  calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage );

    virtual INT executeGetValueLoadProfile(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist<CtiMessage>&vgList, RWTPtrSlist<CtiMessage>&retList, RWTPtrSlist<OUTMESS>&outList);

    virtual INT ResultDecode( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList );

    INT decodeGetValueKWH         ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetValueDemand      ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetValuePeakDemand  ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetValueLoadProfile ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeScanLoadProfile     ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetStatusInternal   ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetStatusLoadProfile( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetConfigTime       ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetConfigIntervals  ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetConfigModel      ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
};
#endif // #ifndef __DEV_MCT470_H__
