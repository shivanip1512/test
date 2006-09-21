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
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2006/09/21 21:31:37 $
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

    typedef CtiDeviceIDLC Inherited;

    enum Commands
    {
        Command_Loop = 123  //  something non-zero
    };

public:

    CtiDeviceCCU710();

    CtiDeviceCCU710(const CtiDeviceCCU710& aRef);
    virtual ~CtiDeviceCCU710();

    CtiDeviceCCU710& operator=(const CtiDeviceCCU710& aRef);

    INT Loopback(OUTMESS*);

    virtual INT IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT ResultDecode(INMESS*, CtiTime&, list< CtiMessage* >   &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);

    virtual INT getProtocolWrap() const;

};
#endif // #ifndef __DEV_710_H__
