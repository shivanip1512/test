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
* REVISION     :  $Revision: 1.14 $
* DATE         :  $Date: 2006/12/26 15:43:42 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_MCT22X_H__
#define __DEV_MCT22X_H__
#pragma warning( disable : 4786)


#include "dev_mct2xx.h"

class CtiDeviceMCT22X : public CtiDeviceMCT2XX
{
private:

    typedef CtiDeviceMCT2XX Inherited;

    static const CommandSet _commandStore;
    static CommandSet initCommandStore();

protected:

    virtual bool getOperation( const UINT &cmd,  BSTRUCT &bst ) const;

    enum
    {
        MCT22X_MReadPos    = 0x89,
        MCT22X_MReadLen    =    3,  //  24 bit most recent (MREAD) copied from CUREAD value on interval boundary
        MCT22X_DiscPos     = 0x37,
        MCT22X_DiscLen     =    3,  //  Gets last latch cmnd recv, reserved byte, LCIMAG
        MCT22X_PutMReadPos = 0x86,
        MCT22X_PutMReadLen =    9,
        MCT22X_DemandPos   = 0x86,
        MCT22X_DemandLen   =    6
    };

    virtual INT ModelDecode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);

    int decodeGetValueDemand(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);

public:

    CtiDeviceMCT22X();
    CtiDeviceMCT22X(const CtiDeviceMCT22X& aRef);

    virtual ~CtiDeviceMCT22X();

    CtiDeviceMCT22X& operator=(const CtiDeviceMCT22X& aRef);
};

#endif // #ifndef __DEV_MCT22X_H__
