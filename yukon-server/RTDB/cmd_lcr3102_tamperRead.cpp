#include "yukon.h"

#include "cmd_lcr3102_tamperRead.h"

#include "dsm2err.h"

namespace Cti {
namespace Devices {
namespace Commands {

Lcr3102TamperReadCommand::Lcr3102TamperReadCommand() :
    Lcr3102ThreePartCommand(Read_TamperReadLength)
{
}

//  throws CommandException
DlcCommand::request_ptr Lcr3102TamperReadCommand::decode(CtiTime now, const unsigned function, const payload_t &payload, string &description, vector<point_data> &points)
{
    if( _state == State_Reading )
    {
        // This was the decode from the true ActOnStoredMessage call, not the dummy.
        const unsigned char tamper_info = getValueFromBits(payload, 0, 8);
    
        if (tamper_info > 3 || tamper_info < 0)
        {
            description = "Returned value is outside the range of acceptable values (" + CtiNumStr(tamper_info) + ")";
            throw CommandException(ErrorInvalidData, description);
        }
    
        point_data circuit, runtime;
    
        circuit.name    = "Relay Circuit Fault";
        circuit.offset  = 30;
        circuit.type    = StatusPointType;
        circuit.quality = NormalQuality;
        circuit.time    = now;
        circuit.value   = !!(tamper_info & 0x01);

        runtime.name    = "Runtime Tamper";
        runtime.offset  = 31;
        runtime.type    = StatusPointType;
        runtime.quality = NormalQuality;
        runtime.time    = now;
        runtime.value   = !!(tamper_info & 0x02);
    
        points.push_back(circuit);
        points.push_back(runtime);
    
        if( !tamper_info )
        {
            description = "No tamper detected.";
        }
        else 
        {
            description = "";
            
            if(tamper_info & 0x01) description += "Relay Circuit Fault detected. ";
            if(tamper_info & 0x02) description += "Runtime Tamper detected. ";
        }

        return request_ptr();
    }
    else
    {
        // This was the decode call after the initial expresscom write, we need to call the next execute and change state!
        _state = State_Reading;
        return execute(now);
    }
}

}
}
}

