/*-----------------------------------------------------------------------------*
*
* File:   dev_mct210
*
* Class:  CtiDeviceMCT210
* Date:   5/3/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_mct210.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2003/03/13 19:36:12 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_MCT210_H__
#define __DEV_MCT210_H__
#pragma warning( disable : 4786)


#include "dev_mct2xx.h"

class IM_EX_DEVDB CtiDeviceMCT210 : public CtiDeviceMCT2XX
{
protected:

    enum
    {
        MCT210_MReadAddr    = 0x12,
        MCT210_MReadLen     =    3,  // 24 bit most recent (MREAD) copied from CUREAD value on interval boundary
        MCT210_DemandAddr   = 0x0a,
        MCT210_DemandLen    =    2,
        MCT210_AccumAddr    = 0x0f,
        MCT210_AccumLen     =    6,
        MCT210_StatusAddr   = 0x36,
        MCT210_StatusLen    =    2,
        MCT210_PutMReadAddr = 0x0f,
        MCT210_PutMReadLen  =    9,

        MCT210_MultAddr     = 0x19,
        MCT210_MultLen      =    2,

        MCT210_GenStatAddr  = 0x30,
        MCT210_GenStatLen   =    9,

        MCT210_ResetAddr    = 0x36,
        MCT210_ResetLen     =    3
    };

private:

   static CTICMDSET _commandStore;

public:

   typedef CtiDeviceMCT2XX Inherited;

   CtiDeviceMCT210();
   CtiDeviceMCT210(const CtiDeviceMCT210& aRef);

   virtual ~CtiDeviceMCT210();

   CtiDeviceMCT210& operator=(const CtiDeviceMCT210 &aRef);

   static  bool initCommandStore();
   virtual bool getOperation( const UINT &cmd,  USHORT &function, USHORT &length, USHORT &io );

   virtual INT ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList);

   INT  decodeGetStatusDisconnect(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);
   void decodeStati(INT &stat, INT which, BYTE *Data);
};
#endif // #ifndef __DEV_MCT210_H__
