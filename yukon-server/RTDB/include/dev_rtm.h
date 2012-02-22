#pragma once

#include <queue>

#include "dev_ied.h"
#include "queue.h"
#include "verification_objects.h"

class IM_EX_DEVDB CtiDeviceRTM : public CtiDeviceIED
{
    typedef CtiDeviceIED Inherited;

    CtiOutMessage _outbound;
    unsigned char _inbound[256];

    enum States
    {
        State_Uninit,
        State_Output,
        State_InputHeader,
        State_Input,
        State_Ack,
        State_Complete
    } _state;

    unsigned short _error_count;
    unsigned long  _in_total, _in_actual;
    int _code_len, _codes_received;

    enum
    {
        HeaderLength = 8,
        MaxErrors = 3
    };

    std::queue< CtiVerificationBase * > _verification_objects;

    unsigned long findHeader(unsigned char *buf, unsigned long len);

protected:

    static bool tryDecodeAsSA305(const UCHAR *abuf, const INT len, string &code, string &cmd);

public:

    CtiDeviceRTM();
    virtual LONG getAddress() const;

    INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
    INT GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);

    INT ResultDecode(INMESS *InMessage, CtiTime &TimeNow, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
    INT ErrorDecode (const INMESS &InMessage, const CtiTime TimeNow, std::list< CtiMessage* > &retList);

    int recvCommRequest(OUTMESS *OutMessage);
    int sendCommResult(INMESS *InMessage);

    int generate(CtiXfer &xfer);
    int decode(CtiXfer &xfer, int status);

    void getVerificationObjects(std::queue<CtiVerificationBase *> &work_queue);

    bool isTransactionComplete();
};
