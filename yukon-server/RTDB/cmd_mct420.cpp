#include "precompiled.h"

#include "cmd_mct420.h"

namespace Cti {
namespace Devices {
namespace Commands {

DlcCommand::request_ptr Mct420Command::error(const CtiTime now, const int error_code, std::string &description)
{
    throw CommandException(error_code, GetError(error_code));
}

}
}
}
