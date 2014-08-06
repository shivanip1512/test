#pragma once

#include "port_base.h"
#include "rfn_identifier.h"

namespace Cti   {
namespace Ports {

class IM_EX_PRTDB RfDaPort : public CtiPort
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    RfDaPort(const RfDaPort&);
    RfDaPort& operator=(const RfDaPort&);

protected:

   RfnIdentifier _rfnId;

public:

    RfDaPort();

    typedef CtiPort Inherited;

    RfnIdentifier getRfnIdentifier() const;

    static std::string getSQLCoreStatement();

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

    virtual INT inMess(CtiXfer &Xfer, CtiDeviceSPtr Dev, std::list<CtiMessage *> &traceList)
    {
        return 0;
    }
    virtual INT openPort(INT rate, INT bits, INT parity, INT stopbits)
    {
        return 0;
    }
    virtual INT outMess(CtiXfer &Xfer, CtiDeviceSPtr Dev, std::list<CtiMessage *> &traceList)
    {
        return 0;
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



