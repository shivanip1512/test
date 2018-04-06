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

RfnAggregateCommand::RfnAggregateCommand(RfnIndividualCommandList commands)
{
    validate( Condition( ! commands.empty(), ClientErrors::MissingParameter ) << "No commands passed to RfnAggregateCommand" );

    auto contextId = _globalContextId.fetch_add(commands.size());

    boost::insert(
        _commands,
        commands
            | boost::adaptors::transformed(
                [&contextId](RfnIndividualCommandPtr &command) {
                    return make_pair(contextId++, std::move(command)); }));
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

RfnCommandResultList RfnAggregateCommand::handleResponse(const CtiTime now, const RfnResponsePayload &response)
try
{
    validate(Condition(response.size() >= HeaderLength, ClientErrors::DataMissing) 
        << "Response size < HeaderLength, " << response.size() << " < " << HeaderLength);
    
    validate(Condition(response[0] == 0x01, ClientErrors::UnknownCommandReceived)
        << "Command != 0x01, " << response[0]);

    auto messages = response[1];
    auto payloadLength = response[2] | response[3] << 8;

    validate(Condition(response.size() >= payloadLength + HeaderLength, ClientErrors::DataMissing)
        << "Response size < payloadLength + HeaderLength, " << response.size() << " < " << payloadLength + HeaderLength);

    size_t pos = HeaderLength;

    RfnCommandResultList aggregateResults;

    YukonError_t remainingErrors = ClientErrors::NoAggregateResponseEntry;

    for( int msgIndex = 0; msgIndex < messages; ++msgIndex )
    {
        auto contextId = response[pos] | response[pos + 1] << 8;
        pos += 2;
        auto length    = response[pos] | response[pos + 1] << 8;
        pos += 2;

        if( response.size() < pos + length )
        {
            CTILOG_ERROR(dout, "Response size < pos + message length, " << response.size() << " < " << pos + length);

            remainingErrors = ClientErrors::DataMissing;

            break;
        }

        if( auto cmd = mapFindRef(_commands, contextId) )
        {
            try
            {
                RfnCommandResult result = (*cmd)->decodeCommand(
                                              now,
                                              { response.cbegin() + pos,
                                              response.cbegin() + pos + length });

                result.description = (*cmd)->getCommandName() + " " + result.description;
                aggregateResults.push_back(result);
            }
            catch( const CommandException & ce )
            {
                aggregateResults.emplace_back((*cmd)->getCommandName() + " " + ce.error_description, ce.error_code);
            }

            _statuses.emplace(contextId, aggregateResults.back().status);
        }
        pos += length;
    }

    for( const auto & kv : _commands )
    {
        const auto contextId = kv.first;
        auto & command = kv.second;

        if( ! _statuses.count(contextId) )
        {
            aggregateResults.emplace_back(
                command->error(
                    now,
                    remainingErrors));
        }
    }

    return std::move(aggregateResults);
}
catch( const CommandException & ce )
{
    CTILOG_EXCEPTION_ERROR(dout, ce);

    return handleError(now, ce.error_code);
}

RfnCommandResultList RfnAggregateCommand::handleError(const CtiTime now, YukonError_t error)
{
    RfnCommandResultList aggregateResults;

    for( auto & kv : _commands )
    {
        const auto contextId = kv.first;
        auto & command = kv.second;

        auto result = command->error(now, error);
        
        _statuses.emplace(contextId, result.status);

        aggregateResults.emplace_back(result);
    }

    return aggregateResults;
}

void RfnAggregateCommand::invokeResultHandler(ResultHandler &rh) const
{
    for( auto & kv : _commands )
    {
        const auto contextId = kv.first;
        auto & command = kv.second;

        if( const auto status = mapFind(_statuses, contextId) )
        {
            if( ! *status )
            {
                command->invokeResultHandler(rh);
            }
        }
    }
}


}
}
}
