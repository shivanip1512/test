
#pragma once

#include "dev_grp_expresscom.h"

class IM_EX_DEVDB CtiDeviceGroupRfnExpresscom : public CtiDeviceGroupExpresscom
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiDeviceGroupRfnExpresscom(const CtiDeviceGroupRfnExpresscom&);
    CtiDeviceGroupRfnExpresscom& operator=(const CtiDeviceGroupRfnExpresscom&);

    typedef CtiDeviceGroupExpresscom Inherited;

    void sendDRMessage(int priority, int expirationDuration, std::vector<unsigned char> &payload);

public:
    CtiDeviceGroupRfnExpresscom() {};
    virtual ~CtiDeviceGroupRfnExpresscom() {};

    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
    virtual std::string getSQLCoreStatement() const;
};
