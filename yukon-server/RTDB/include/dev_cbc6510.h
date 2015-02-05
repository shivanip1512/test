#pragma once

#include "dev_remote.h"
#include "dev_dnp.h"
#include "tbl_dv_idlcremote.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Cbc6510Device : public DnpDevice
{
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

    void processPoints( Protocols::Interface::pointlist_t &points ) override;

public:

    Cbc6510Device();

    virtual std::string getDescription(const CtiCommandParser & parse) const;

    YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
};

}
}


