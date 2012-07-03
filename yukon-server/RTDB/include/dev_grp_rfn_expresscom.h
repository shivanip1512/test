
#pragma once

#include "dev_grp_expresscom.h"

class IM_EX_DEVDB CtiDeviceGroupRfnExpresscom : public CtiDeviceGroupExpresscom
{
private:

    typedef CtiDeviceGroupExpresscom Inherited;

public:
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
    virtual string getSQLCoreStatement() const;
};
