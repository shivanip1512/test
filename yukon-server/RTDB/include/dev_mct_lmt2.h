/*-----------------------------------------------------------------------------*
*
* File:   dev_mct_lmt2
*
* Class:  CtiDeviceMCT_LMT2
* Date:   6/19/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_mct_lmt2.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2003/03/13 19:36:13 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_MCT_LMT2_H__
#define __DEV_MCT_LMT2_H__
#pragma warning( disable : 4786)


#include "dev_mct22x.h"

class CtiDeviceMCT_LMT2 : public CtiDeviceMCT22X
{
protected:

private:

   static CTICMDSET _commandStore;

public:

   typedef CtiDeviceMCT22X Inherited;

    CtiDeviceMCT_LMT2();// {}

    CtiDeviceMCT_LMT2(const CtiDeviceMCT_LMT2& aRef);/*
    {
        *this = aRef;
    }                                                  */

    virtual ~CtiDeviceMCT_LMT2();// {}

    CtiDeviceMCT_LMT2& operator=(const CtiDeviceMCT_LMT2& aRef);/*
    {
        if(this != &aRef)
        {
           Inherited::operator=(aRef);
        }
        return *this;
    }                                                             */

    virtual INT ResultDecode(INMESS*, RWTime&, RWTPtrSlist< CtiMessage >   &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList);

    INT decodeAccumScan(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);
    INT decodeDemandScan(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);
    INT decodeGetValueDefault(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);

    static bool initCommandStore();
    virtual bool getOperation( const UINT &cmd,  USHORT &function, USHORT &length, USHORT &io );

    INT decodeGetConfigModel(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);

};
#endif // #ifndef __DEV_MCT_LMT2_H__
