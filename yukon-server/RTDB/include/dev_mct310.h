#pragma warning( disable : 4786)
#ifndef __DEV_MCT310_H__
#define __DEV_MCT310_H__

/*-----------------------------------------------------------------------------*
*
* File:   dev_mct310
*
* Class:  CtiDeviceMCT310
* Date:   4/24/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_mct310.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:47 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dev_mct.h"

class IM_EX_DEVDB CtiDeviceMCT310 : public CtiDeviceMCT
{
protected:

    enum
    {
        MCT310_DemandAddr = 0x2c,
        MCT310_DemandLen  =    2,

        MCT310_StatusAddr = 0x05,
        MCT310_StatusLen  =    2
    };

    enum
    {
        MCT3XX_FuncReadMReadAddr  = 0x90,  //  144
        MCT3XX_FuncReadMReadLen   =    9,  //  Variable based on point count... Max of 9.
        MCT3XX_FuncReadFrozenAddr = 0x91,  //  145
        MCT3XX_FuncReadFrozenLen  =    9,  //  Variable based on point count... Max of 9.

        MCT3XX_PutMRead1Addr      = 0x20,
        MCT3XX_PutMRead2Addr      = 0x32,
        MCT3XX_PutMRead3Addr      = 0x51,
        MCT3XX_PutMReadLen        =    6,

        MCT3XX_Mult1Addr          = 0x26,
        MCT3XX_Mult2Addr          = 0x38,
        MCT3XX_Mult3Addr          = 0x57,
        MCT3XX_MultLen            =    2,

        MCT3XX_PFCountAddr        = 0x07,
        MCT3XX_PFCountLen         =    2,

        MCT3XX_ResetAddr          = 0x06,
        MCT3XX_ResetLen           =    1,

        MCT3XX_TimeAddr           = 0x7A,
        MCT3XX_TimeLen            =    5,

        MCT3XX_LPStatusAddr       = 0x70,
        MCT3XX_LPStatusLen        =    9,

        MCT3XX_DemandIntervalAddr = 0x1B,
        MCT3XX_DemandIntervalLen  =    1,
        MCT3XX_LPIntervalAddr     = 0x76,
        MCT3XX_LPIntervalLen      =    1,

        MCT3XX_OptionAddr         = 0x02,
        MCT3XX_OptionLen          =    6,
        MCT3XX_GenStatAddr        = 0x05,
        MCT3XX_GenStatLen         =    9
    };

private:

   static CTICMDSET _commandStore;

   RWTime _lastLPRequestAttempt, _lastLPRequestBlockStart;

public:

   typedef CtiDeviceMCT Inherited;

   CtiDeviceMCT310( );
   CtiDeviceMCT310( const CtiDeviceMCT310 &aRef );
   virtual ~CtiDeviceMCT310( );

   CtiDeviceMCT310 &operator=( const CtiDeviceMCT310 &aRef );

   static bool initCommandStore( );
   virtual bool getOperation( const UINT &cmd,  USHORT &function, USHORT &length, USHORT &io );

   //  virtual so that the MCT318 can override them
   virtual ULONG calcNextLPScanTime( void );
   virtual INT   calcAndInsertLPRequests( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList );

   virtual INT ResultDecode( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList );

   INT decodeGetValueKWH         ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetValueDemand      ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetStatusDisconnect ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetStatusInternal   ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetStatusLoadProfile( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetConfigModel      ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetConfigOptions    ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeScanLoadProfile     ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );

   void decodeAccumulators( ULONG *result, INT accum_cnt, BYTE *Data );
   void decodeStati( INT &stat, INT which, BYTE *Data );
};
#endif // #ifndef __DEV_MCT310_H__
