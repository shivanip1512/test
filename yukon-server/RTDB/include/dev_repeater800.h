/*-----------------------------------------------------------------------------*
*
* File:   dev_repeater
*
* Class:  CtiDeviceRepeater
* Date:   8/27/2001
*
* Author: Matthew Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :
* REVISION     :
* DATE         :
*
* Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_REPEATER800_H__
#define __DEV_REPEATER800_H__
#pragma warning( disable : 4786)


#include <rw\cstring.h>

#include "dev_repeater.h"

class IM_EX_DEVDB CtiDeviceRepeater800 : public CtiDeviceRepeater900
{

protected:

    enum
    {
        Rpt800_PFCountAddr = 0x36,
        Rpt800_PFCountLen  =    3
    };

private:

   static CTICMDSET _commandStore;

public:

   typedef CtiDeviceRepeater900 Inherited;

   CtiDeviceRepeater800();

   CtiDeviceRepeater800(const CtiDeviceRepeater800& aRef);

   virtual ~CtiDeviceRepeater800();

   CtiDeviceRepeater800& operator=(const CtiDeviceRepeater800& aRef);

   static bool initCommandStore( );
   virtual bool getOperation( const UINT &cmd,  USHORT &function, USHORT &length, USHORT &io );

   virtual INT ResultDecode(INMESS*InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList);

   INT decodeGetValuePFCount(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);
};

#endif // #ifndef __DEV_REPEATER800_H__
