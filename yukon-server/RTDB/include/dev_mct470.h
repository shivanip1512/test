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
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/08/01 22:10:26 $
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
        MCT470_ChannelCount = 4,

        MCT470_Sspec        = 1030,
        MCT470_SspecRevMin  =    5,  //  rev e
        MCT470_SspecRevMax  =   15,  //  rev o is max for now

        MCT470_Memory_ChannelOffset = 0x1a,

        MCT470_FuncRead_ChannelSetupPos = 0x20,
        MCT470_FuncRead_ChannelSetupLen =    7,

        MCT470_FuncRead_LPStatusCh1Ch2Pos = 0x97,
        MCT470_FuncRead_LPStatusCh3Ch4Pos = 0x9c,
        MCT470_FuncRead_LPStatusLen       =   11,

        MCT470_FuncWrite_IEDCommand     = 0xd0,
        MCT470_FuncWrite_IEDCommandLen  =    4,

        MCT470_FuncWrite_IEDCommandData        = 0xd1,
        MCT470_FuncWrite_IEDCommandDataBaseLen =    5,
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

        MCT470_FuncRead_PeakDemandBasePos  = 0x93,
        MCT470_FuncRead_PeakDemandLen      =   12,

        MCT470_FuncRead_IED_TOU_CurrentKWBase  = 0xc1,
        MCT470_FuncRead_IED_TOU_CurrentKMBase  = 0xc5,
        MCT470_FuncRead_IED_TOU_CurrentTotals  = 0xc9,

        MCT470_FuncRead_IED_TOU_PreviousOffset =    9,

        MCT470_FuncRead_IED_TOU_MeterStatus    = 0xd3,

        MCT470_FuncRead_IED_RealTime           = 0xd5
    };

    bool isLPDynamicInfoCurrent(void);
    void requestDynamicInfo(CtiTableDynamicPaoInfo::Keys key, OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList);

    void sendIntervals         (OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList);

private:

    static DLCCommandSet initCommandStore();
    static const DLCCommandSet _commandStore;

    CtiTableDeviceMCTIEDPort _iedPort;

    enum IED_PointOffsets
    {
        MCT470_PointOffset_TotalKWH   =  1,
        MCT470_PointOffset_TOU_KWBase =  2,
        MCT470_PointOffset_TotalKW    = 10,
        MCT470_PointOffset_TotalKMH   = 11,
        MCT470_PointOffset_TOU_KMBase = 12,
        MCT470_PointOffset_TotalKM    = 20
    };

    long getLoadProfileInterval( unsigned channel );
    long _lastConfigRequest;

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

public:

    typedef CtiDeviceMCT Inherited;

    CtiDeviceMCT470( );
    CtiDeviceMCT470( const CtiDeviceMCT470 &aRef );
    virtual ~CtiDeviceMCT470( );

    CtiDeviceMCT470 &operator=( const CtiDeviceMCT470 &aRef );

    void setDisconnectAddress( unsigned long address );

    virtual bool getOperation( const UINT &cmd,  USHORT &function, USHORT &length, USHORT &io );

    virtual ULONG calcNextLPScanTime( void );
    virtual INT   calcAndInsertLPRequests( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList );
    virtual bool  calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage );

    virtual void DecodeDatabaseReader( RWDBReader &rdr );

    virtual INT ResultDecode( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList );

    virtual INT executeGetValue (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist<CtiMessage>&vgList, RWTPtrSlist<CtiMessage>&retList, RWTPtrSlist<OUTMESS>&outList);
    virtual INT executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist<CtiMessage>&vgList, RWTPtrSlist<CtiMessage>&retList, RWTPtrSlist<OUTMESS>&outList);

    INT decodeGetValueKWH          ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetValueDemand       ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetValuePeakDemand   ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetValueIED          ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetConfigIED         ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetValueLoadProfile  ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeScanLoadProfile      ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetStatusInternal    ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetStatusLoadProfile ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetConfigTime        ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetConfigIntervals   ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetConfigChannelSetup( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    INT decodeGetConfigModel       ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
};
#endif // #ifndef __DEV_MCT470_H__
