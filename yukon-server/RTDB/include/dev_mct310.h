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
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2004/02/11 05:04:35 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_MCT310_H__
#define __DEV_MCT310_H__
#pragma warning( disable : 4786)


#include "dev_mct.h"

class IM_EX_DEVDB CtiDeviceMCT310 : public CtiDeviceMCT
{
private:

   static DLCCommandSet _commandStore;

   RWTime _lastLPRequestAttempt, _lastLPRequestBlockStart;

protected:

    enum
    {
        MCT310_DemandPos = 0x2c,
        MCT310_DemandLen =    2,

        MCT310_StatusPos = 0x05,
        MCT310_StatusLen =    2
    };

    enum
    {
        MCT3XX_FuncReadMReadPos  = 0x90,  //  144
        MCT3XX_FuncReadMReadLen  =    9,  //  Variable based on point count... Max of 9.
        MCT3XX_FuncReadFrozenPos = 0x91,  //  145
        MCT3XX_FuncReadFrozenLen =    9,  //  Variable based on point count... Max of 9.

        MCT3XX_FuncReadMinMaxDemandPos = 0x93,
        MCT3XX_FuncReadMinMaxDemandLen =   12,  //  Variable based on point count
        MCT3XX_FuncReadFrozenDemandPos = 0x94,
        MCT3XX_FuncReadFrozenDemandLen =   12,  //  Variable based on point count

        MCT3XX_PutMRead1Pos      = 0x20,
        MCT3XX_PutMRead2Pos      = 0x32,
        MCT3XX_PutMRead3Pos      = 0x51,
        MCT3XX_PutMReadLen       =    6,

        MCT3XX_Mult1Pos          = 0x26,
        MCT3XX_Mult2Pos          = 0x38,
        MCT3XX_Mult3Pos          = 0x57,
        MCT3XX_MultLen           =    2,

        MCT3XX_PFCountPos        = 0x07,
        MCT3XX_PFCountLen        =    2,

        MCT3XX_ResetPos          = 0x06,
        MCT3XX_ResetLen          =    1,

        MCT3XX_TimePos           = 0x7A,
        MCT3XX_TimeLen           =    5,

        MCT3XX_LPStatusPos       = 0x70,
        MCT3XX_LPStatusLen       =    9,

        MCT3XX_DemandIntervalPos = 0x1B,
        MCT3XX_DemandIntervalLen =    1,
        MCT3XX_LPIntervalPos     = 0x76,
        MCT3XX_LPIntervalLen     =    1,

        MCT3XX_OptionPos         = 0x02,
        MCT3XX_OptionLen         =    6,
        MCT3XX_GenStatPos        = 0x05,
        MCT3XX_GenStatLen        =    9,

        MCT3XX_MinMaxPeakConfigPos  = 0x03,

        MCT3XX_FunctionPeakOff  = 0x59,
        MCT3XX_FunctionPeakOn   = 0x5a,

        MCT3XX_FreezeOne         = 0x51,
        MCT3XX_FreezeTwo         = 0x52,
        MCT3XX_FreezeLen         =    0
    };

    enum
    {
        MCT3XX_GroupAddrPos     = 0x10,
        MCT3XX_GroupAddrLen     =    5,

        MCT3XX_GroupAddrBronzePos      = 0x10,
        MCT3XX_GroupAddrBronzeLen      =    1,
        MCT3XX_GroupAddrLeadPos        = 0x11,
        MCT3XX_GroupAddrLeadLen        =    3,
        MCT3XX_GroupAddrGoldSilverPos  = 0x14,
        MCT3XX_GroupAddrGoldSilverLen  =    1,
        MCT3XX_UniqAddrPos             = 0x1A,
        MCT3XX_UniqAddrLen             =    6
    };

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
   virtual bool  calcLPRequestLocation( const CtiCommandParser &parse, OUTMESS *&OutMessage );

   virtual INT ResultDecode( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList );

   INT decodeGetValueKWH         ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetValueDemand      ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetValuePeak        ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetStatusDisconnect ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetStatusInternal   ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetStatusLoadProfile( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetConfigModel      ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetConfigOptions    ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeScanLoadProfile     ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodePutConfigPeakMode   ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );

   void decodeAccumulators( ULONG *result, INT accum_cnt, BYTE *Data );
   void decodeStati( INT &stat, INT which, BYTE *Data );
};
#endif // #ifndef __DEV_MCT310_H__
