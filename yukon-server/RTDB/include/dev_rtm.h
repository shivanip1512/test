
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
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2004/09/20 16:11:04 $
* HISTORY      :
* $Log: dev_rtm.h,v $
* Revision 1.3  2004/09/20 16:11:04  mfisher
* implemented comms in generate() and decode()
*
* Revision 1.2  2004/07/30 21:35:07  cplender
* RTM stuff
*
* Revision 1.1  2004/07/20 16:19:03  cplender
* IR
*
*
* Copyright (c) 2002, 2003, 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __DEV_RTM_H__
#define __DEV_RTM_H__

#include <queue>

using namespace std;

#include "dev_ied.h"
#include "queue.h"
#include "verification_objects.h"

class IM_EX_DEVDB CtiDeviceRTM : public CtiDeviceIED
{
private:

    CtiOutMessage _outbound;
    unsigned char _inbound[32];

    enum States
    {
        State_Uninit,
        State_Output,
        State_Input,
        State_Complete
    } _state;

    unsigned short _error_count;
    unsigned long  _in_expected, _in_actual;
    int _code_len, _codes_received;

    enum
    {
        MaxErrors = 3
    };

    queue< CtiVerificationBase * > _verification_objects;

protected:

public:

    typedef CtiDeviceIED Inherited;

    CtiDeviceRTM();
    virtual ~CtiDeviceRTM();
    virtual LONG getAddress() const;

    INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);
    INT GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = MAXPRIORITY - 4);

    INT ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage> &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);
    INT ErrorDecode (INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage> &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);

    int recvCommRequest(OUTMESS *OutMessage);
    int sendCommResult(INMESS *InMessage);

    int generate(CtiXfer &xfer);
    int decode(CtiXfer &xfer, int status);

    void getVerificationObjects(queue<CtiVerificationBase *> &work_queue);

    bool isTransactionComplete();
};
#endif // #ifndef __DEV_RTM_H__
