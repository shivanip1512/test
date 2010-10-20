#pragma once

#include "cmd_lcr3102_ThreePart.h"

namespace Cti {
namespace Devices {
namespace Commands {

class IM_EX_DEVDB Lcr3102DemandResponseSummaryCommand : public Lcr3102ThreePartCommand
{
private:

    enum ReadLength
    {
        Read_DRSummaryLength  = 1
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

    // These conflicts are with respect to the 0 bit of the response.
    enum ConflictedResults
    {
        Conflict_ActivatedNotControlling = 0x05, // Bit 0 said we are controlling, bit 2 said we're not.
        Conflict_ActivatedControlling    = 0x09, // Bit 0 said we're not controlling, bit 3 said we are.
        Conflict_BothActivatedEnabled    = 0x0c, // Both activated bits are set: can't be both controlled and not controlled.
    };

public:
    
    Lcr3102DemandResponseSummaryCommand();

    virtual request_ptr decode (const CtiTime now, const unsigned function, const payload_t &payload, std::string &description, std::vector<point_data> &points);
    
    void decodeResponseByte(const unsigned char drSummaryByte, std::string &description);
};

}   // Commands
}   // Devices
}   // Cti
