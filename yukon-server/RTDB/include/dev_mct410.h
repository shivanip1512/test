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
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2003/10/27 22:04:07 $
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
        MCT410_StatusLen         =    2,

        MCT410_PowerfailCountPos = 0x07,
        MCT410_PowerfailCountLen =    2,

        MCT410_AlarmsPos         = 0x15,
        MCT410_AlarmsLen         =    2,

        MCT410_FuncReadMReadPos  = 0x90,
        MCT410_FuncReadMReadLen  =    3,  //  this is for the 410 KWH Only;  will need to be increased later

        MCT410_FuncReadDemandPos = 0x92,
        MCT410_FuncReadDemandLen =    3   //  again, this is just getting the status byte and one demand reading
    };

/*    enum
    {
        MCT3XX_FuncReadMReadPos  = 0x90,  //  144
        MCT3XX_FuncReadMReadLen  =    9,  //  Variable based on point count... Max of 9.
        MCT3XX_FuncReadFrozenPos = 0x91,  //  145
        MCT3XX_FuncReadFrozenLen =    9,  //  Variable based on point count... Max of 9.

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
        MCT3XX_GenStatLen        =    9
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
    };*/

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

   virtual INT ResultDecode( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList );

   INT decodeGetValueKWH         ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetValueDemand      ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetStatusInternal   ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
   INT decodeGetConfigModel      ( INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
};
#endif // #ifndef __DEV_MCT410_H__
