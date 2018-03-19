#include "precompiled.h"

#include "std_helper.h"
#include "cmd_rfn_Aggregate.h"
#include "cmd_rfn_helper.h"

#include <boost/bimap.hpp>

using namespace std;

namespace Cti {
namespace Devices {
namespace Commands {

RfnAggregateCommand::RfnAggregateCommand(RfnCommandList commands)
    :   _commands( std::move( commands ) )
{
    validate( Condition( ! _commands.empty(), ClientErrors::MissingParameter ) << "No commands passed to RfnAggregateCommand" );
}

auto RfnAggregateCommand::getApplicationServiceId() const -> ASID
{
    return ASID::BulkMessageHandler;
}

unsigned char RfnAggregateCommand::getCommandCode() const
{
    return 0x01;  //  Aggregate Message
}

unsigned char RfnAggregateCommand::getOperation() const
{
    return {};  //  unused
}

auto RfnAggregateCommand::getCommandHeader() -> Bytes
{
    Bytes header;

    header.push_back(getCommandCode());
    header.push_back(_commands.size());

    return header;
}

RfnCommand::Bytes RfnAggregateCommand::getCommandData()
{
    return Bytes();
}

RfnCommandResult RfnAggregateCommand::decodeCommand(const CtiTime now, const RfnResponsePayload &response)
{
    RfnCommandResult result;

    return result;
}


}
}
}
