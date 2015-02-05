
#pragma once

#include "dev_grp_expresscom.h"

class IM_EX_DEVDB CtiDeviceGroupRfnExpresscom : public CtiDeviceGroupExpresscom
{
    typedef CtiDeviceGroupExpresscom Inherited;

    void sendDRMessage(int priority, int expirationDuration, std::vector<unsigned char> &payload);

public:
    CtiDeviceGroupRfnExpresscom() {};

    YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;
    virtual std::string getSQLCoreStatement() const;
};
