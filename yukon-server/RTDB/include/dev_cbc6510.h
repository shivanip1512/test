#pragma once

#include "dev_remote.h"
#include "dev_dnp.h"
#include "tbl_dv_idlcremote.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Cbc6510Device : public DnpDevice
{
private:

    typedef DnpDevice Inherited;

    enum
    {
        PointOffset_Close = 1,
        PointOffset_Trip  = 2,

        PointOffset_TripClosePaired = 1
    };

    struct tripclose_info_t
    {
        double state;
        CtiTime time;
        short  millis;
    } _trip_info, _close_info;

protected:

    virtual void processPoints( Protocol::Interface::pointlist_t &points );

public:

    Cbc6510Device();
    Cbc6510Device(const Cbc6510Device& aRef);
    virtual ~Cbc6510Device();

    Cbc6510Device& operator=(const Cbc6510Device& aRef);

    virtual std::string getDescription(const CtiCommandParser & parse) const;

    INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
};

}
}


