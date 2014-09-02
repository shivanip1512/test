#pragma once

#include <queue>

#include "dev_ied.h"
#include "queue.h"
#include "verification_objects.h"

class IM_EX_DEVDB CtiDeviceRTM : public CtiDeviceIED
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiDeviceRTM(const CtiDeviceRTM&);
    CtiDeviceRTM& operator=(const CtiDeviceRTM&);

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

    static bool tryDecodeAsSA305(const UCHAR *abuf, const INT len, std::string &code, std::string &cmd);

public:

    CtiDeviceRTM();
    virtual LONG getAddress() const;

    INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    INT GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 4);

    INT ResultDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    INT ErrorDecode (const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &retList);

    YukonError_t recvCommRequest(OUTMESS *OutMessage);
    YukonError_t sendCommResult(INMESS &InMessage);

    YukonError_t generate(CtiXfer &xfer);
    YukonError_t decode  (CtiXfer &xfer, YukonError_t status);

    void getVerificationObjects(std::queue<CtiVerificationBase *> &work_queue);

    bool isTransactionComplete();
};
