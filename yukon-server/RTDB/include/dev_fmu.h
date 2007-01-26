
/*-----------------------------------------------------------------------------*
*
* File:   dev_fmu
*
* Class:  CtiDeviceFMU
* Date:   9/26/2006
*
* Author: Julie Richter
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2007/01/26 19:56:14 $
* HISTORY      :
* $Log: dev_fmu.h,v $
* Revision 1.1  2007/01/26 19:56:14  jrichter
* FMU stuff for jess....
*
*
* Copyright (c) 2002, 2003, 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __DEV_FMU_H__
#define __DEV_FMU_H__

#include <queue>

using std::queue;
//using namespace Cti;
//using namespace Protocol;
//using namespace fmuProtocol;

#include "dev_ied.h"
#include "queue.h"
#include "verification_objects.h"
#include "prot_fmu.h"

using namespace Cti;         
using namespace Protocol;    
using namespace fmuProtocol; 

#define NAK_ACTION_RESEND_LAST_MSG 0x80
#define NAK_ACTION_RESET_SEQUENCE  0x40
#define NAK_INVALID_REQUEST        0x10
#define NAK_INVALID_DATA           0x08
#define NAK_INVALID_DATA_LENGTH    0x04
#define NAK_INVALID_SEQUENCE       0x02
#define NAK_INVALID_CMD            0x01

class IM_EX_DEVDB CtiDeviceFMU : public CtiDeviceIED
{
private:

    typedef CtiDeviceIED Inherited;

    CtiOutMessage _outbound;
    unsigned char _inbound[32];
    CtiProtocolFMU _fmuProtocol;

    //ULONG _fmuAddress;
    USHORT _sequence;
    USHORT _cmd;
   
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
    bool _endOfTransactionFlag;

    enum
    {
        MaxErrors = 3
    };

    queue< CtiVerificationBase * > _verification_objects;

protected:

public:
    INT _cmd_len;

    CtiDeviceFMU();
    ~CtiDeviceFMU();
    virtual LONG getAddress() const;

    INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    INT GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);

    INT ResultDecode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    INT ErrorDecode (INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);

    int recvCommRequest(OUTMESS *OutMessage);
    int sendCommResult(INMESS *InMessage);

    int generate(CtiXfer &xfer);
    int decode(CtiXfer &xfer, int status);

    void getVerificationObjects(queue<CtiVerificationBase *> &work_queue);

    bool isTransactionComplete();
   CtiProtocolFMU getFMUProtocol( void );

};
#endif // #ifndef __DEV_FMU_H__
