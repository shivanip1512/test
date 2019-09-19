#include "precompiled.h"

#include "std_helper.h"
#include "cmd_rfn.h"

#include "cmd_rfn_ConfigNotification.h"
#include "error_helper.h"

namespace Cti {
namespace Devices {
namespace Commands {

using Logging::Vector::Hex::operator<<;

// Construct a byte vector request
RfnCommand::RfnRequestPayload RfnCommand::executeCommand(const CtiTime now)
{
    RfnRequestPayload req;

    prepareCommandData(now);

    Bytes header = getCommandHeader();

    req.insert(req.end(), header.begin(), header.end());

    Bytes data = getCommandData();

    req.insert(req.end(), data.begin(), data.end());

    return req;
}

// Default header for RFN messages
RfnCommand::Bytes RfnCommand::getCommandHeader()
{
    Bytes   header;

    header.push_back(getCommandCode());
    header.push_back(getOperation());

    return header;
}

// Defaults to Advanced Metrology, which operates via Channel Manager
auto RfnCommand::getApplicationServiceId() const -> ASID
{
    return ASID::ChannelManager;
}


std::unique_ptr<RfnConfigNotificationCommand> RfnCommand::handleNodeOriginated(const CtiTime now, RfnResponsePayload payload)
{
    validate( Condition( ! payload.empty(), ClientErrors::DataMissing ) << "Empty payload");

    CTILOG_INFO(dout, "Handling unsolicited report, payload " << payload);

    if( payload[0] == RfnConfigNotificationCommand::getUnsolicitedCommandCode() )
    {
        CTILOG_INFO(dout, "Creating config notification command");

        auto command = std::make_unique<RfnConfigNotificationCommand>();

        //  ignore command results
        command->handleResponse(now, payload);

        return std::move(command);
    }

    return nullptr;
}


}
}
}
