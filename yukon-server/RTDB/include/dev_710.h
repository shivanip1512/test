#pragma once

#include "dev_idlc.h"

class CtiDeviceCCU710 : public CtiDeviceIDLC
{
    typedef CtiDeviceIDLC Inherited;

    enum Commands
    {
        Command_Loop = 123  //  something non-zero
    };

    int LPreamble(PBYTE, USHORT);

public:

    CtiDeviceCCU710();

    INT Loopback(OUTMESS*);

    YukonError_t IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 4) override;
    YukonError_t GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 4) override;
    YukonError_t ResultDecode(const INMESS&, const CtiTime, CtiMessageList   &vgList, CtiMessageList &retList, OutMessageList &outList) override;
    YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

    virtual INT getProtocolWrap() const;

};
