#pragma once

#include "dev_ied.h"
#include "prot_seriesv.h"

class IM_EX_DEVDB CtiDeviceSeriesV : public CtiDeviceIED
{
    CtiProtocolSeriesV _seriesv;

public:
    CtiDeviceSeriesV() {};

    Cti::Protocols::Interface *getProtocol() override;

    YukonError_t ExecuteRequest (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

    YukonError_t GeneralScan    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 4) override;
    YukonError_t AccumulatorScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 3) override;
    YukonError_t IntegrityScan  (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 4) override;

    YukonError_t ResultDecode(const INMESS &InMessage, const CtiTime Now, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;
};
