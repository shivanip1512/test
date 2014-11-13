#pragma once

#include "dev_remote.h"
#include "dev_dnp.h"
#include "tbl_dv_idlcremote.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Cbc6510Device : public DnpDevice
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    Cbc6510Device(const Cbc6510Device&);
    Cbc6510Device& operator=(const Cbc6510Device&);

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


