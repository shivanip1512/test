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
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2008/06/06 20:28:44 $
* HISTORY      :
* $Log: dev_rtm.h,v $
* Revision 1.9  2008/06/06 20:28:44  jotteson
* YUK-6005 Porter LLP expect more set incorrectly
* Added an option to override expect more in the error decode call.
* Made LLP retry 3 times before failing.
*
* Revision 1.8  2006/09/21 21:31:39  mfisher
* privatized Inherited typedef
*
* Revision 1.7  2006/02/27 23:58:32  tspar
* Phase two of RWTPtrSlist replacement.
*
* Revision 1.6  2006/02/24 00:19:13  tspar
* First Series of replacements of RWTPtrSlist to std::list. Scanner, Pil, Porter.
*
* Revision 1.5  2005/12/20 17:20:30  tspar
* Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex
*
* Revision 1.4  2004/11/03 17:50:46  mfisher
* added State_Ack
*
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

using std::queue;

#include "dev_ied.h"
#include "queue.h"
#include "verification_objects.h"

class IM_EX_DEVDB CtiDeviceRTM : public CtiDeviceIED
{
private:

    typedef CtiDeviceIED Inherited;

    CtiOutMessage _outbound;
    unsigned char _inbound[256];

    enum States
    {
        State_Uninit,
        State_Output,
        State_Input,
        State_Ack,
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

    CtiDeviceRTM();
    virtual ~CtiDeviceRTM();
    virtual LONG getAddress() const;

    INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    INT GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);

    INT ResultDecode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    INT ErrorDecode (const INMESS &InMessage, const CtiTime TimeNow, list< CtiMessage* > &retList);

    int recvCommRequest(OUTMESS *OutMessage);
    int sendCommResult(INMESS *InMessage);

    int generate(CtiXfer &xfer);
    int decode(CtiXfer &xfer, int status);

    void getVerificationObjects(queue<CtiVerificationBase *> &work_queue);

    bool isTransactionComplete();
};
#endif // #ifndef __DEV_RTM_H__
