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



#include "dev_repeater.h"

class IM_EX_DEVDB CtiDeviceRepeater800 : public CtiDeviceRepeater900
{
private:

   static const CommandSet _commandStore;
   static CommandSet initCommandStore();

protected:

    virtual bool getOperation( const UINT &cmd,  USHORT &function, USHORT &length, USHORT &io );

    enum
    {
        Rpt800_PFCountPos = 0x36,
        Rpt800_PFCountLen =    3
    };

    INT decodeGetValuePFCount(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);

    typedef CtiDeviceRepeater900 Inherited;

public:

   CtiDeviceRepeater800();
   CtiDeviceRepeater800(const CtiDeviceRepeater800& aRef);

   virtual ~CtiDeviceRepeater800();

   CtiDeviceRepeater800& operator=(const CtiDeviceRepeater800& aRef);

   virtual INT ResultDecode(INMESS*InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);

};

#endif // #ifndef __DEV_REPEATER800_H__
