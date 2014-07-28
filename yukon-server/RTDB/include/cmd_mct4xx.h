#pragma once

#include "cmd_dlc.h"

namespace Cti {
namespace Devices {
namespace Commands {

//  forward declaration for ResultHandler
class Mct410Command;
class Mct4xxLoadProfileCommand;

struct IM_EX_DEVDB Mct4xxCommand : DlcCommand
{
    struct ResultHandler
    {
        virtual void handleCommandResult(const Mct4xxCommand            &command)  {};
        //  must include overloads for all children that require a result handler
        virtual void handleCommandResult(const Mct410Command            &command)  {};
        virtual void handleCommandResult(const Mct4xxLoadProfileCommand &command)  {};
    };

    virtual void invokeResultHandler(DlcCommand::ResultHandler &rh) const
    {
        rh.handleCommandResult(*this);
    }

    //  to be overridden by children that require a result handler
    virtual void invokeResultHandler(ResultHandler &rh) const  { };
};

}
}
}
