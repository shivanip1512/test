#pragma warning( disable : 4786)
#ifndef __DEV_CBC6510_H__
#define __DEV_CBC6510_H__

/*-----------------------------------------------------------------------------*
*
* File:   dev_cbc6510
*
* Class:  CtiDeviceCBC6510
* Date:   5/22/2002
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_cbc.h-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2003/03/05 23:54:47 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dev_remote.h"
#include "dev_dnp.h"
#include "tbl_dv_idlcremote.h"

class IM_EX_DEVDB CtiDeviceCBC6510 : public CtiDeviceDNP
{
protected:

private:

public:

    typedef CtiDeviceDNP Inherited;

    CtiDeviceCBC6510();
    CtiDeviceCBC6510(const CtiDeviceCBC6510& aRef);
    virtual ~CtiDeviceCBC6510();

    CtiDeviceCBC6510& operator=(const CtiDeviceCBC6510& aRef);

    virtual RWCString getDescription(const CtiCommandParser & parse) const;

    void processInboundPoints(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, RWTPtrSlist<CtiPointDataMsg> &points );
    INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);
};


#endif // #ifndef __DEV_CBC_H__
