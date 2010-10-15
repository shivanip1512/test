#include "yukon.h"

#include "cmd_lcr3102_tamperRead.h"
#include "dev_lcr3102.h"

#include "dsm2err.h"

namespace Cti {
namespace Devices {
namespace Commands {

Lcr3102TamperReadCommand::Lcr3102TamperReadCommand() :
    _firstRead(true),
    _dummyRead(true),
    _retries(2)
{
}

DlcCommand::request_ptr Lcr3102TamperReadCommand::execute(const CtiTime now)
{
    if( _firstRead )
    {
        _firstRead = false;
        return request_ptr(new read_request_t(Read_DummyRead, 0));
    }
    else
    {
        _dummyRead = false;
        return request_ptr(new read_request_t(Read_ActOnStoredMessage, Read_TamperReadLength));
    }
}

//  throws CommandException
DlcCommand::request_ptr Lcr3102TamperReadCommand::decode(CtiTime now, const unsigned function, const payload_t &payload, string &description, vector<point_data> &points)
try
{
    if( !_dummyRead )
    {
        // This was the decode from the true ActOnStoredMessage call, not the dummy.
        const unsigned char tamper_info = getValueFromBits(payload, 0, 8);
    
        if (tamper_info > 3 || tamper_info < 0)
        {
            description = "Returned value is outside the range of acceptable values (" + CtiNumStr(tamper_info) + ")";
            throw CommandException(ErrorInvalidData, description);
        }
    
        point_data tamper;
    
        tamper.name    = "tamper";
        tamper.offset  = 30;
        tamper.type    = StatusPointType;
        tamper.quality = NormalQuality;
        tamper.time    = now;
        tamper.value   = tamper_info;
    
        points.push_back(tamper);
    
        if( !tamper_info )
        {
            description = "No tamper detected.";
        }
        else 
        {
            description = "";
            
            if(tamper_info & 0x01) description += "RCircuit Fault detected.\n";
            if(tamper_info & 0x02) description += "Runtime Tamper detected.\n";
        }

        return request_ptr();
    }
    else
    {
        // This was the decode call after the initial dummy call, we need to call the next execute!
        return execute(now);
    }
}
catch( BaseCommand::CommandException &ex )
{
    return error(now, ex.error_code, description = ex.error_description);
}

DlcCommand::request_ptr Lcr3102TamperReadCommand::error(const CtiTime now, const int error_code, std::string &description)
{
    if( description.empty() )
    {
        description = GetError(error_code);
    }

    description += "\n";

    if( _retries > 0 )
    {
        _retries--;

        description += "Retrying (" + CtiNumStr(_retries) + " remaining)";

        return execute(now);
    }
    else
    {
        throw CommandException(error_code, description + "Retries exhausted");
    }
}

}
}
}

