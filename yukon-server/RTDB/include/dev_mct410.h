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
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2004/10/26 21:18:03 $
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

    enum
    {
        MCT410_StatusPos         = 0x05,
        MCT410_StatusLen         =    5,

        MCT410_PowerfailCountPos = 0x23,
        MCT410_PowerfailCountLen =    2,

        MCT410_AlarmsPos         = 0x15,
        MCT410_AlarmsLen         =    2,

        MCT410_IntervalsPos  = 0x1a,
        MCT410_IntervalsLen  =    4,

        MCT410_RTCPos            = 0x40,
        MCT410_RTCLen            =    4,
        MCT410_LastTSyncPos      = 0x44,
        MCT410_LastTSyncLen      =    4,

        MCT410_FuncWriteIntervalsPos = 0x03,
        MCT410_FuncWriteIntervalsLen =    4,

        MCT410_FuncReadOutagePos = 0x10,
        MCT410_FuncReadOutageLen =   13,

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

    enum
    {
        MCT4XX_LPChannels       =  4,
        MCT4XX_LPVoltageChannel =  4,
        MCT4XX_LPRecentBlocks   = 16
    };

private:

   static DLCCommandSet _commandStore;
   bool _intervalsSent;

   enum ErrorClasses
   {
       EC_MeterReading    = 0x0001,
       EC_DemandReading   = 0x0002,
       EC_TOUDemand       = 0x0004,
       EC_TOUFrozenDemand = 0x0008,
       EC_LoadProfile     = 0x0010
   };

   enum ValueType
   {
       ValueType_Voltage,
       ValueType_KW,
       ValueType_Accumulator,
       ValueType_Raw
   };

   typedef map<unsigned long, pair<PointQuality_t, int> > QualityMap;
   static  QualityMap _errorQualities;
   static  QualityMap initErrorQualities( void );

   struct LPInfo
   {
       unsigned long archived_reading;
       unsigned long current_request;
       unsigned long current_schedule;
   } _lp_info[MCT4XX_LPChannels];

   struct LLPInterest
   {
       unsigned long time;
       int offset;
       int channel;
   } _llpInterest;

public:

   typedef CtiDeviceMCT Inherited;
   typedef pair<double, PointQuality_t> data_pair;

   CtiDeviceMCT410( );
   CtiDeviceMCT410( const CtiDeviceMCT410 &aRef );
   virtual ~CtiDeviceMCT410( );

   CtiDeviceMCT410 &operator=( const CtiDeviceMCT410 &aRef );

   static DLCCommandSet initCommandStore( );
   virtual bool getOperation( const UINT &cmd,  USHORT &function, USHORT &length, USHORT &io );

   void sendIntervals( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList );

   virtual ULONG calcNextLPScanTime( void );
   virtual INT   calcAndInsertLPRequests( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList );
   virtual bool  calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage );

   data_pair getData(unsigned char *buf, int len, ValueType vt=ValueType_KW);

   virtual INT executeGetValueLoadProfile(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist<CtiMessage>&vgList, RWTPtrSlist<CtiMessage>&retList, RWTPtrSlist<OUTMESS>&outList);

   virtual INT ResultDecode( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList );

   INT decodeGetValueKWH         ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetValueDemand      ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetValuePeakDemand  ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetValueVoltage     ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetValueOutage      ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetValueLoadProfile ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeScanLoadProfile     ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetStatusInternal   ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetStatusLoadProfile( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetConfigTime       ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetConfigInterval   ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetConfigModel      ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
};
#endif // #ifndef __DEV_MCT410_H__
