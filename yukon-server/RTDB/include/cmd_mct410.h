#pragma once

#include "cmd_mct4xx.h"

namespace Cti {
namespace Devices {
namespace Commands {

//  forward declaration for ResultHandler
class Mct410DisconnectConfigurationCommand;

class Mct410Command : public Mct4xxCommand
{
public:

    struct ResultHandler
    {
        virtual void handleCommandResult(const Mct410Command &command) = 0;
        //  must include children desiring a result handler call
        virtual void handleCommandResult(const Mct410DisconnectConfigurationCommand &command) = 0;
    };

    virtual void invokeResultHandler(Mct4xxCommand::ResultHandler &rh) const
    {
        rh.handleCommandResult(*this);
    }

    //  to be overridden by children desiring a result handler call
    virtual void invokeResultHandler(ResultHandler &rh) const  { }

protected:

    enum FunctionReads
    {
        Read_HourlyReadChannel1BasePos = 0x1c0,
        Read_HourlyReadChannel2BasePos = 0x1d0,
        Read_HourlyReadLen =   13,
    };
};

}
}
}

