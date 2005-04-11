/*-----------------------------------------------------------------------------*
*
* File:   dev_710
*
* Class:  CtiDeviceCCU710
* Date:   6/21/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_710.h-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/04/11 16:17:55 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_710_H__
#define __DEV_710_H__
#pragma warning( disable : 4786)


#include <windows.h>

#include "dev_idlc.h"


class CtiDeviceCCU710 : public CtiDeviceIDLC
{
protected:

private:

    enum Commands
    {
        Command_Loop = 123  //  something non-zero
    };

public:

    typedef CtiDeviceIDLC Inherited;

    CtiDeviceCCU710();

    CtiDeviceCCU710(const CtiDeviceCCU710& aRef);
    virtual ~CtiDeviceCCU710();

    CtiDeviceCCU710& operator=(const CtiDeviceCCU710& aRef);

    INT Loopback(OUTMESS*);

    virtual INT IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT ResultDecode(INMESS*, RWTime&, RWTPtrSlist< CtiMessage >   &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);

    virtual INT getProtocolWrap() const;

};
#endif // #ifndef __DEV_710_H__
