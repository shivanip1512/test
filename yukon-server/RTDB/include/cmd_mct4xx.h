#pragma once

#include "cmd_dlc.h"

namespace Cti {
namespace Devices {
namespace Commands {

//  forward declaration for ResultHandler
class Mct410Command;
class Mct4xxLoadProfileCommand;
class Mct4xxLoadProfilePeakReportCommand;

struct IM_EX_DEVDB Mct4xxCommand : DlcCommand
{
    struct ResultHandler
    {
        virtual void handleCommandResult(const Mct4xxCommand                      &)  {};
        //  must include overloads for all children that require a result handler
        virtual void handleCommandResult(const Mct4xxLoadProfileCommand           &)  {};
        virtual void handleCommandResult(const Mct4xxLoadProfilePeakReportCommand &)  {};
        virtual void handleCommandResult(const Mct410Command                      &)  {};
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
