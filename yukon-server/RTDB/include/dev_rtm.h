
/*-----------------------------------------------------------------------------*
*
* File:   dev_rtm
*
* Class:  CtiDeviceRTM
* Date:   7/12/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2004/07/20 16:19:03 $
* HISTORY      :
* $Log: dev_rtm.h,v $
* Revision 1.1  2004/07/20 16:19:03  cplender
* IR
*
*
* Copyright (c) 2002, 2003, 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __DEV_RTM_H__
#define __DEV_RTM_H__

#include <list>
using namespace std;

#include "dev_ied.h"
#include "queue.h"

class CtiDeviceRTM : public CtiDeviceIED
{
protected:

private:

public:

    typedef CtiDeviceIED Inherited;

    CtiDeviceRTM();
    virtual ~CtiDeviceRTM();
    virtual LONG getAddress() const;

    INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);
    INT IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = MAXPRIORITY - 4);
    INT GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = MAXPRIORITY - 4);

    INT ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage> &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);
    INT ErrorDecode (INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage> &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);

};
#endif // #ifndef __DEV_RTM_H__
