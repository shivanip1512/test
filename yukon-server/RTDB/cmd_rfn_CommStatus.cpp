#include "precompiled.h"

#include "cmd_rfn_CommStatus.h"
#include "numstr.h"
#include "std_helper.h"
#include "cmd_rfn_helper.h"

using namespace std::chrono_literals;

namespace Cti::Devices::Commands {

//  unused
unsigned char RfnGetCommunicationStatusUpdateCommand::getOperation()   const {  return {};  }
unsigned char RfnGetCommunicationStatusUpdateCommand::getCommandCode() const {  return {};  }

auto RfnGetCommunicationStatusUpdateCommand::getApplicationServiceId() const -> ASID
{
    return ASID::EventManager;
}

auto RfnGetCommunicationStatusUpdateCommand::getCommandHeader() -> Bytes
{
    return { static_cast<uint8_t>(Command::Request) };
}

auto RfnGetCommunicationStatusUpdateCommand::getCommandData() -> Bytes
{
    return { static_cast<uint8_t>(Operation::GetCommunicationStatusUpdate) };
}

std::string RfnGetCommunicationStatusUpdateCommand::getCommandName() const
{
    return "Get Communication Status Request";
}


}