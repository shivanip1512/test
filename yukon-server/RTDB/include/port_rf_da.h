#pragma once

#include "port_base.h"
#include "rfn_identifier.h"

namespace Cti   {
namespace Ports {

class IM_EX_PRTDB RfDaPort : public CtiPort
{
   RfnIdentifier _rfnId;

public:

    RfDaPort();

    typedef CtiPort Inherited;

    RfnIdentifier getRfnIdentifier() const;

    static std::string getSQLCoreStatement();

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

    virtual YukonError_t inMess(CtiXfer &Xfer, CtiDeviceSPtr Dev, std::list<CtiMessage *> &traceList)
    {
        return ClientErrors::None;
    }
    virtual YukonError_t openPort(INT rate, INT bits, INT parity, INT stopbits)
    {
        return ClientErrors::None;
    }
    virtual YukonError_t outMess(CtiXfer &Xfer, CtiDeviceSPtr Dev, std::list<CtiMessage *> &traceList)
    {
        return ClientErrors::None;
    }
    virtual bool isViable()
    {
        return true;  //  connectionless, so always viable
    }

    virtual void incQueueSubmittal();
    virtual void incQueueProcessed();
    unsigned concurrentRequests() const;
};

typedef boost::shared_ptr< RfDaPort > RfDaPortSPtr;

}
}



