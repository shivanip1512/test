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
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2004/12/07 18:14:26 $
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
        MCT210_MReadPos    = 0x12,
        MCT210_MReadLen    =    3,  // 24 bit most recent (MREAD) copied from CUREAD value on interval boundary
        MCT210_DemandPos   = 0x0a,
        MCT210_DemandLen   =    2,
        MCT210_AccumPos    = 0x0f,
        MCT210_AccumLen    =    6,
        MCT210_StatusPos   = 0x36,
        MCT210_StatusLen   =    2,
        MCT210_PutMReadPos = 0x0f,
        MCT210_PutMReadLen =    9,

        MCT210_MultPos     = 0x19,
        MCT210_MultLen     =    2,

        MCT210_GenStatPos  = 0x30,
        MCT210_GenStatLen  =    9,

        MCT210_ResetPos    = 0x36,
        MCT210_ResetLen    =    3
    };

private:

    static DLCCommandSet _commandStore;

public:

    enum
    {
        MCT210_StatusConnected    = 0x80,
        MCT210_StatusDisconnected = 0x40
    };

    typedef CtiDeviceMCT2XX Inherited;

    CtiDeviceMCT210();
    CtiDeviceMCT210(const CtiDeviceMCT210& aRef);

    virtual ~CtiDeviceMCT210();

    CtiDeviceMCT210& operator=(const CtiDeviceMCT210 &aRef);

    static  bool initCommandStore();
    virtual bool getOperation( const UINT &cmd,  USHORT &function, USHORT &length, USHORT &io );

    virtual INT ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList);
};
#endif // #ifndef __DEV_MCT210_H__
