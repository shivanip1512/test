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
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2004/04/01 21:50:02 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_MCT410_H__
#define __DEV_MCT410_H__
#pragma warning( disable : 4786)


#include "dev_mct.h"

class IM_EX_DEVDB CtiDeviceMCT410 : public CtiDeviceMCT
{
protected:

    enum
    {
        MCT410_StatusPos         = 0x05,
        MCT410_StatusLen         =    5,

        MCT410_PowerfailCountPos = 0x23,
        MCT410_PowerfailCountLen =    2,

        MCT410_AlarmsPos         = 0x15,
        MCT410_AlarmsLen         =    2,

        MCT410_DemandIntervalPos = 0x1a,
        MCT410_DemandIntervalLen =    1,

        MCT410_LPIntervalPos     = 0x1b,
        MCT410_LPIntervalLen     =    1,

        MCT410_RTCPos            = 0x40,
        MCT410_RTCLen            =    4,
        MCT410_LastTSyncPos      = 0x44,
        MCT410_LastTSyncLen      =    4,

        MCT410_FuncWriteTSyncPos = 0xf0,
        MCT410_FuncWriteTSyncLen =    6,

        MCT410_FuncReadMReadPos  = 0x90,
        MCT410_FuncReadMReadLen  =    3,  //  this is for the 410 KWH Only;  will need to be increased later

        MCT410_FuncReadDemandPos = 0x92,
        MCT410_FuncReadDemandLen =    6,  //  brings back recent demand, avg. voltage, and blink counter

        MCT410_FuncReadPeakDemandPos = 0x93,
        MCT410_FuncReadPeakDemandLen =    9,  //  peak demand, time of peak, current meter reading

        MCT410_FuncReadVoltagePos = 0x95,
        MCT410_FuncReadVoltageLen =   12,  //  max and min voltages

        MCT410_FuncReadLPStatusPos = 0x97,
        MCT410_FuncReadLPStatusLen =   12,

        MCT4XX_FreezeOne         = 0x51,
        MCT4XX_FreezeTwo         = 0x52,

        MCT4XX_VoltageOffset     =    4,

        MCT4XX_CommandPowerfailReset = 0x89,
        MCT4XX_CommandReset          = 0x8A,
    };

private:

   static DLCCommandSet _commandStore;

   RWTime _lastLPRequestAttempt, _lastLPRequestBlockStart;

public:

   typedef CtiDeviceMCT Inherited;

   CtiDeviceMCT410( );
   CtiDeviceMCT410( const CtiDeviceMCT410 &aRef );
   virtual ~CtiDeviceMCT410( );

   CtiDeviceMCT410 &operator=( const CtiDeviceMCT410 &aRef );

   static bool initCommandStore( );
   virtual bool getOperation( const UINT &cmd,  USHORT &function, USHORT &length, USHORT &io );

   virtual ULONG calcNextLPScanTime( void );
   virtual INT   calcAndInsertLPRequests( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList );
   virtual bool  calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage );

   PointQuality_t getDataQuality( int value );
   bool           isValidDataQuality( int value );

   virtual INT ResultDecode( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList );

   INT decodeGetValueKWH         ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetValueDemand      ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetValuePeakDemand  ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetValueVoltage     ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeScanLoadProfile     ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetStatusInternal   ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetStatusLoadProfile( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetConfigTime       ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetConfigInterval   ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetConfigModel      ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
};
#endif // #ifndef __DEV_MCT410_H__
