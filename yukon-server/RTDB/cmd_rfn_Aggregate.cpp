#include "precompiled.h"

#include "std_helper.h"
#include "cmd_rfn_Aggregate.h"
#include "cmd_rfn_helper.h"

#include <boost/range/adaptor/map.hpp>
#include <boost/range/numeric.hpp>

using namespace std;

namespace Cti {
namespace Devices {
namespace Commands {

std::atomic_uint16_t RfnAggregateCommand::_globalContextId { static_cast<uint16_t>(std::time(nullptr)) };

void RfnAggregateCommand::setGlobalContextId(const uint16_t id, Test::use_in_unit_tests_only&)
{
    _globalContextId.store(id);
}

RfnAggregateCommand::RfnAggregateCommand(RfnCommandList commands)
{
    validate( Condition( ! commands.empty(), ClientErrors::MissingParameter ) << "No commands passed to RfnAggregateCommand" );

    auto contextId = _globalContextId.fetch_add(commands.size());

    boost::insert(
        _commands,
        commands
            | boost::adaptors::transformed(
                [&contextId](RfnCommandPtr &command) {
                    return std::pair<uint16_t, RfnCommandPtr>(contextId++, std::move(command)); }));
}

void RfnAggregateCommand::prepareCommandData(const CtiTime now)
{
    boost::insert(
        _messages, 
        _commands 
            | boost::adaptors::transformed(
                [now](const CommandMap::value_type & kv) {
                    return std::make_pair(kv.first, kv.second->executeCommand(now)); }));
}

auto RfnAggregateCommand::getApplicationServiceId() const -> ASID
{
    return ASID::BulkMessageHandler;
}

unsigned char RfnAggregateCommand::getCommandCode() const
{
    return Command_AggregateMessage;
}

unsigned char RfnAggregateCommand::getOperation() const
{
    return {};  //  unused
}

size_t RfnAggregateCommand::getPayloadLength() const
{
    return 
        _messages.size() * SubMessageHeaderLength + 
        boost::accumulate(
            _messages 
                | boost::adaptors::map_values 
                | boost::adaptors::transformed(
                    [](const Bytes & payload) { 
                        return payload.size(); }), 
            0);
}

auto RfnAggregateCommand::getCommandHeader() -> Bytes
{
    Bytes header;

    header.reserve(HeaderLength);

    header.push_back(getCommandCode());
    header.push_back(_commands.size());

    const auto payloadLength = getPayloadLength();

    header.push_back(static_cast<uint8_t>(payloadLength));
    header.push_back(static_cast<uint8_t>(payloadLength >> 8));

    return header;
}

RfnCommand::Bytes RfnAggregateCommand::getCommandData()
{
    Bytes payload;

    payload.reserve(getPayloadLength());

    for( const auto & kv : _messages )
    {
        auto contextId = kv.first;
        auto & message = kv.second;

        payload.push_back(static_cast<uint8_t>(contextId));
        payload.push_back(static_cast<uint8_t>(contextId >> 8));

        const auto messageLength = message.size();

        payload.push_back(static_cast<uint8_t>(messageLength));
        payload.push_back(static_cast<uint8_t>(messageLength >> 8));

        boost::insert(payload, payload.end(), message);
    }

    return payload;
}

RfnCommandResult RfnAggregateCommand::decodeCommand(const CtiTime now, const RfnResponsePayload &response)
{
    validate(Condition(response.size() >= HeaderLength, ClientErrors::InvalidData) 
        << "Response size < HeaderLength, " << response.size());
    
    validate(Condition(response[0] == 0x01, ClientErrors::InvalidData)
        << "Command != 0x01, " << response[0]);

    auto messages = response[1];
    auto payloadLength = response[2] | response[3] << 8;

    validate(Condition(response.size() >= payloadLength + HeaderLength, ClientErrors::InvalidData)
        << "Response size < payloadLength + HeaderLength, " << response.size());

    size_t pos = HeaderLength;

    RfnCommandResult aggregateResult { "" };

    std::vector<std::string> descriptions;

    for( int msgIndex = 0; msgIndex < messages; ++msgIndex )
    {
        auto contextId = response[pos] | response[pos + 1] << 8;
        pos += 2;
        auto length    = response[pos] | response[pos + 1] << 8;
        pos += 2;

        validate(Condition(pos + length <= response.size(), ClientErrors::InvalidData)
            << "Pos + message length > payloadLength, " << pos + length);

        if( auto cmd = mapFindRef(_commands, contextId) )
        {
            auto result = 
                (*cmd)->decodeCommand(
                    now, 
                    { response.cbegin() + pos, 
                      response.cbegin() + pos + length });

            descriptions.emplace_back(
                "Aggregate message " + std::to_string(msgIndex + 1) + ", context ID " + std::to_string(contextId)
                + "\n" + result.description);
            
            std::move(
                std::begin(aggregateResult.points), 
                std::end  (aggregateResult.points),
                std::back_inserter(result.points));
        }
        pos += length;
    }

    aggregateResult.description = boost::join(descriptions, "\n");

    return aggregateResult;
}


}
}
}
