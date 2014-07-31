#pragma once

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

    int LPreamble(PBYTE, USHORT);

public:

    CtiDeviceCCU710();
    virtual ~CtiDeviceCCU710();

    INT Loopback(OUTMESS*);

    virtual INT IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT ResultDecode(const INMESS*, CtiTime&, std::list< CtiMessage* >   &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

    virtual INT getProtocolWrap() const;

};
