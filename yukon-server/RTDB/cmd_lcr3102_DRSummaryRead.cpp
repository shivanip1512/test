#include "yukon.h"

#include "cmd_lcr3102_DRSummaryRead.h"

#include "dsm2err.h"

namespace Cti {
namespace Devices {
namespace Commands {

Lcr3102DemandResponseSummaryCommand::Lcr3102DemandResponseSummaryCommand() :
    Lcr3102ThreePartCommand(Read_DRSummaryLength)
{
}

DlcCommand::request_ptr Lcr3102DemandResponseSummaryCommand::decode(CtiTime now, const unsigned function, const payload_t &payload, string &description, vector<point_data> &points)
{
    if( _state == State_ActOnMessageRead )
    {
        // This was the decode from the true ActOnStoredMessage call.
        const unsigned char drSummary_info = getValueFromBits(payload, 0, 8);
      
        decodeResponseByte(drSummary_info, description);

        return request_ptr();
    }
    else
    {
        // This was the decode call after the initial expresscom write, we need to call the next execute and change state!
        _state = State_ActOnMessageRead;
        return execute(now);
    }
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

    // Bit 1 - Cold Load Pickup bit.
    description += (drSummaryByte & Summary_ColdLoadPickup) ? "In cold load pickup\n" : "Not in cold load pickup\n";

    // Bit 2 - Actived but not controlling - bit 0 guaranteed to clear or we'd have thrown an exception.
    if( drSummaryByte & Summary_ActivatedNotControlling )
    {
        description += "Activated, not controlling\n";
    }

    // Bit 3 - May or may not be activated. In either case, report about the controlling.
    if( drSummaryByte & Summary_Controlling )
    {
        description += (drSummaryByte & Summary_ActivatedControlling) ? "Activated and controlling\n" : "Device controlling\n";
    }

    // Bit 4 - Under Voltage Event
    description += (drSummaryByte & Summary_UnderVoltageEvent) ? "Under voltage event\n" : "No under voltage event\n";

    // Bit 5 - Under Frequency Event
    description += (drSummaryByte & Summary_UnderFrequencyEvent) ? "Under frequency event\n" : "No under frequency event\n";

    // Bit 6 - Out of Service
    description += (drSummaryByte & Summary_OutOfService) ? "Device out of service\n" : "Device not out of service\n";
}

}
}  
}  

