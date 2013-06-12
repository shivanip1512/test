#pragma once

#include "cmd_lcr3102_ThreePart.h"

namespace Cti {
namespace Devices {
namespace Commands {

class IM_EX_DEVDB Lcr3102DemandResponseSummaryCommand : public Lcr3102ThreePartCommand
{
private:

    enum ReadLengths
    {
        ReadLength_DemandResponseSummary = 7
    };

    enum SummaryBits
    {
        Summary_Controlling             = 0x01,
        Summary_ColdLoadPickup          = 0x02,
        Summary_ActivatedNotControlling = 0x04,
        Summary_ActivatedControlling    = 0x08,
        Summary_UnderVoltageEvent       = 0x10,
        Summary_UnderFrequencyEvent     = 0x20,
        Summary_OutOfService            = 0x40,
        Summary_UNUSED                  = 0x80
    };

public:

    Lcr3102DemandResponseSummaryCommand();

    virtual request_ptr decodeReading(const CtiTime now, const unsigned function, const Bytes &payload, std::string &description, std::vector<point_data> &points);

    void decodeResponseByte(const unsigned char drSummaryByte, std::string &description);
};

}
}
}
