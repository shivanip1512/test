#include "precompiled.h"

#include "std_helper.h"
#include "cmd_rfn.h"

namespace Cti {
namespace Devices {
namespace Commands {


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


RfnCommandPtr RfnCommand::handleUnsolicitedResponse(const CtiTime now, RfnResponsePayload payload)
{
    return nullptr;
}


}
}
}
