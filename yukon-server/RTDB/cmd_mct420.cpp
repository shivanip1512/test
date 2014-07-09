#include "precompiled.h"

#include "cmd_mct420.h"

namespace Cti {
namespace Devices {
namespace Commands {

DlcCommand::request_ptr Mct420Command::error(const CtiTime now, const YukonError_t error_code, std::string &description)
{
    throw CommandException(error_code, GetErrorString(error_code));
}

}
}
}
