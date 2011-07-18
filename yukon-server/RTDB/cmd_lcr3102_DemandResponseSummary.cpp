#include "precompiled.h"

#include "cmd_lcr3102_DemandResponseSummary.h"

#include "dsm2err.h"

namespace Cti {
namespace Devices {
namespace Commands {

Lcr3102DemandResponseSummaryCommand::Lcr3102DemandResponseSummaryCommand() :
    Lcr3102ThreePartCommand(ReadLength_DemandResponseSummary)
{
}

DlcCommand::request_ptr Lcr3102DemandResponseSummaryCommand::decodeReading(CtiTime now, const unsigned function, const payload_t &payload, std::string &description, std::vector<point_data> &points)
{
    const unsigned char drSummary_info = getValueFromBits(payload, 48, 8);

    decodeResponseByte(drSummary_info, description);

    return request_ptr();
}

// Throws command exception
void Lcr3102DemandResponseSummaryCommand::decodeResponseByte(const unsigned char drSummaryByte, std::string &description)
{
    description = "";

    if( ((drSummaryByte & 0x01) && (drSummaryByte & 0x04)) ||  // Bit 0 said we are controlling, bit 2 said we're not.
        (!(drSummaryByte & 0x01) && (drSummaryByte & 0x08)) || // Bit 0 said we're not controlling, bit 3 said we are.
        ((drSummaryByte & 0x08) && (drSummaryByte & 0x04)) )   // Both activated bits are set: can't be both controlled and not controlled.
    {
       description = "LCR returned a conflicted account of its currently controlled state (" + CtiNumStr(drSummaryByte) + ")";
       throw CommandException(ErrorInvalidData, description);
    }

    description += (drSummaryByte & Summary_ColdLoadPickup) ? "In cold load pickup\n" : "Not in cold load pickup\n";

    if( drSummaryByte & Summary_ActivatedNotControlling )
    {
        description += "Activated, not controlling\n";
    }

    if( drSummaryByte & Summary_Controlling )
    {
        description += (drSummaryByte & Summary_ActivatedControlling) ? "Activated and controlling\n" : "Device controlling\n";
    }

    description += (drSummaryByte & Summary_UnderVoltageEvent) ? "Under voltage event\n" : "No under voltage event\n";

    description += (drSummaryByte & Summary_UnderFrequencyEvent) ? "Under frequency event\n" : "No under frequency event\n";

    description += (drSummaryByte & Summary_OutOfService) ? "Device out of service\n" : "Device not out of service\n";
}

}
}
}

