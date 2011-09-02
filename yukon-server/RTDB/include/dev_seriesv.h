#pragma once

#include "dev_ied.h"
#include "prot_seriesv.h"

class IM_EX_DEVDB CtiDeviceSeriesV : public CtiDeviceIED
{
private:

    CtiProtocolSeriesV _seriesv;

protected:

public:

    Cti::Protocol::Interface *getProtocol();

    virtual INT ExecuteRequest (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

    virtual INT GeneralScan    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT AccumulatorScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 3);
    virtual INT IntegrityScan  (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);

    virtual INT ResultDecode(INMESS *InMessage, CtiTime &Now, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
};
