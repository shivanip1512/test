#pragma once

#include "dev_idlc.h"

class CtiDeviceCCU710 : public CtiDeviceIDLC
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiDeviceCCU710(const CtiDeviceCCU710&);
    CtiDeviceCCU710& operator=(const CtiDeviceCCU710&);

    typedef CtiDeviceIDLC Inherited;

    enum Commands
    {
        Command_Loop = 123  //  something non-zero
    };

    int LPreamble(PBYTE, USHORT);

public:

    CtiDeviceCCU710();

    INT Loopback(OUTMESS*);

    INT IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 4) override;
    INT GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 4) override;
    INT ResultDecode(const INMESS&, const CtiTime, CtiMessageList   &vgList, CtiMessageList &retList, OutMessageList &outList) override;
    INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

    virtual INT getProtocolWrap() const;

};
