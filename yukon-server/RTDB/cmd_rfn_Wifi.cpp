#include "precompiled.h"

#include "cmd_rfn_Wifi.h"
#include "numstr.h"
#include "std_helper.h"
#include "cmd_rfn_helper.h"

using namespace std::chrono_literals;

namespace Cti::Devices::Commands {

//  unused
unsigned char RfnWifiGetCommunicationStatusUpdateCommand::getOperation()   const {  return {};  }
unsigned char RfnWifiGetCommunicationStatusUpdateCommand::getCommandCode() const {  return {};  }

auto RfnWifiGetCommunicationStatusUpdateCommand::getApplicationServiceId() const -> ASID
{
    return ASID::EventManager;
}

auto RfnWifiGetCommunicationStatusUpdateCommand::getCommandHeader() -> Bytes
{
    return { static_cast<uint8_t>(Command::Request) };
}

auto RfnWifiGetCommunicationStatusUpdateCommand::getCommandData() -> Bytes
{
    return { static_cast<uint8_t>(Operation::GetCommunicationStatusUpdate) };
}

std::string RfnWifiGetCommunicationStatusUpdateCommand::getCommandName()
{
    return "Get Wi-Fi Communication Status Request";
}


}