#pragma warning( disable : 4786)

#ifndef __DEV_MCT22X_H__
#define __DEV_MCT22X_H__

/*-----------------------------------------------------------------------------*
*
* File:   dev_mct22X
*
* Class:  CtiDeviceMCT22X
* Date:   5/3/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_mct22X.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:19:52 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dev_mct2xx.h"

class CtiDeviceMCT22X : public CtiDeviceMCT2XX
{
protected:

    enum
    {
        MCT22X_MReadAddr    = 0x89,
        MCT22X_MReadLen     =    3,  //  24 bit most recent (MREAD) copied from CUREAD value on interval boundary
        MCT22X_DiscAddr     = 0x37,
        MCT22X_DiscLen      =    3,  //  Gets last latch cmnd recv, reserved byte, LCIMAG
        MCT22X_PutMReadAddr = 0x86,
        MCT22X_PutMReadLen  =    9,
    };

private:

   static CTICMDSET _commandStore;

public:

   typedef CtiDeviceMCT2XX Inherited;

   CtiDeviceMCT22X();
   CtiDeviceMCT22X(const CtiDeviceMCT22X& aRef);

   virtual ~CtiDeviceMCT22X();

   CtiDeviceMCT22X& operator=(const CtiDeviceMCT22X& aRef);

   static  bool initCommandStore();
   virtual bool getOperation( const UINT &cmd,  USHORT &function, USHORT &length, USHORT &io );

   virtual INT ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList);
};
#endif // #ifndef __DEV_MCT22X_H__
