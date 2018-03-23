#include "precompiled.h"

#include "mgr_rfn_request.h"
#include "amq_connection.h"
#include "dev_rfn.h"
#include "rfn_statistics.h"
#include "std_helper.h"

#include <boost/range/adaptor/map.hpp>
#include <boost/range/adaptor/transformed.hpp>
#include <boost/range/algorithm/count.hpp>
#include <boost/range/algorithm/heap_algorithm.hpp>
#include <boost/range/numeric.hpp>

using Cti::Devices::Commands::DeviceCommand;
using Cti::Devices::Commands::RfnCommandResult;
using Cti::Devices::Commands::RfnCommandResultList;
using Cti::Logging::Vector::Hex::operator<<;
using Cti::Messaging::Rfn::E2eMessenger;

using namespace std::chrono_literals;

namespace Cti {
namespace Pil {

enum
{
    E2EDT_CON_RETX_TIMEOUT     = 60,
    E2EDT_CON_MAX_RETX         =  2,
    E2EDT_CON_RETX_RAND_FACTOR = 10,
    E2EDT_STATS_REPORTING_INTERVAL = 86400,
    E2EDT_ACK_PRIORITY         = 15,  //  high priority - we want these to get out so the node doesn't retransmit
    E2EDT_RETRANSMIT_PRIORITY  = 15,  //  If the first one got out, we need the retransmits to get out as well
};

Rfn::E2eStatistics stats;

void RfnRequestManager::start()
{
    E2eMessenger::registerE2eDtHandler(
            [&](const E2eMessenger::Indication &msg)
            {
                LockGuard guard(_indicationMux);

                _indications.push_back(msg);
            });
}


void RfnRequestManager::tick()
{
    const auto rejected  = handleConfirms();
    const auto completed = handleIndications();
    const auto expired   = handleTimeouts();

    RfnIdentifierSet devicesToInspect;

    //  combine the sets of interesting devices
    devicesToInspect.insert(rejected .begin(),
                            rejected .end());
    devicesToInspect.insert(completed.begin(),
                            completed.end());
    devicesToInspect.insert(expired  .begin(),
                            expired  .end());

    //  provide a hint as to which devices are ready for a new request
    handleNewRequests(devicesToInspect);

    //  make the results available to Porter
    postResults();

    handleStatistics();
}


Protocols::E2eDataTransferProtocol::EndpointResponse RfnRequestManager::handleE2eDtIndication(const std::vector<unsigned char> &payload, const long endpointId)
{
    return _e2edt.handleIndication(payload, endpointId);
}


RfnRequestManager::RfnIdentifierSet RfnRequestManager::handleIndications()
{
    RfnIdentifierSet completedDevices;

    IndicationQueue recentIndications;

    {
        LockGuard guard(_indicationMux);

        std::swap(recentIndications, _indications);
    }

    {
        LockGuard guard(_activeRequestsMux);

        const CtiTime Now;

        for( auto &indication : recentIndications )
        {
            auto request = mapFindRef(_activeRequests, indication.rfnIdentifier);

            if( ! request )
            {
                CTILOG_WARN(dout, "Indication message received for inactive device "<< indication.rfnIdentifier);
                continue;
            }

            ActiveRfnRequest &activeRequest = *request;

            CTILOG_INFO(dout, "Indication message received for device "<< activeRequest.request.parameters.rfnIdentifier <<
                    std::endl << "rfnId: " << activeRequest.request.parameters.rfnIdentifier << ": " << indication.payload);

            std::vector<RfnCommandResult> commandResults;

            try
            {
                const Protocols::E2eDataTransferProtocol::EndpointResponse er =
                        handleE2eDtIndication(indication.payload, activeRequest.request.parameters.deviceId);

                if( ! er.ack.empty() )
                {
                    sendE2eDataAck(
                            er.ack,
                            activeRequest.request.command->getApplicationServiceId(),
                            activeRequest.request.parameters.rfnIdentifier);

                    stats.incrementAcks(activeRequest.request.parameters.deviceId, Now);
                }

                if( er.token != activeRequest.request.rfnRequestId )
                {
                    CTILOG_ERROR(dout, "Indication received for inactive request token "<< er.token <<" for device "<< activeRequest.request.parameters.rfnIdentifier);
                    continue;
                }

                CTILOG_INFO(dout, "Response received for token "<< er.token <<" for device "<< activeRequest.request.parameters.rfnIdentifier <<
                        std::endl <<"rfnId: "<< activeRequest.request.parameters.rfnIdentifier << ": " << er.data);

                activeRequest.response.insert(
                        activeRequest.response.end(),
                        er.data.begin(),
                        er.data.end());

                if( ! er.blockContinuation.empty() )
                {
                    const CtiTime  previousBlockTimeSent   = activeRequest.currentPacket.timeSent;
                    const unsigned previousRetransmitCount = activeRequest.currentPacket.retransmits;

                    activeRequest.currentPacket =
                            sendE2eDataRequestPacket(
                                    er.blockContinuation,
                                    activeRequest.request.command->getApplicationServiceId(),
                                    activeRequest.request.parameters.rfnIdentifier,
                                    activeRequest.request.parameters.priority,
                                    activeRequest.request.parameters.groupMessageId,
                                    activeRequest.timeout);

                    stats.incrementBlockContinuation(activeRequest.request.parameters.deviceId, previousRetransmitCount, activeRequest.currentPacket.timeSent, previousBlockTimeSent);

                    CTILOG_INFO(dout, "Block continuation sent for device "<< activeRequest.request.parameters.rfnIdentifier <<
                            std::endl <<"rfnId: "<< activeRequest.request.parameters.rfnIdentifier <<": "<< activeRequest.currentPacket.payloadSent);

                    continue;
                }

                stats.incrementCompletions(activeRequest.request.parameters.deviceId, activeRequest.currentPacket.retransmits, Now);

                boost::insert(
                    commandResults, 
                    commandResults.end(), 
                    activeRequest.request.command->handleResponse(Now, activeRequest.response));
            }
            catch( const DeviceCommand::CommandException &ce )
            {
                commandResults.emplace_back(ce.error_description, ce.error_code);
            }
            catch( const Protocols::E2eDataTransferProtocol::RequestNotAcceptable &rne )
            {
                CTILOG_ERROR(dout, "Endpoint indicated request not acceptable for device "<< activeRequest.request.parameters.rfnIdentifier);

                boost::insert(
                    commandResults,
                    commandResults.end(),
                    activeRequest.request.command->handleError(Now, ClientErrors::E2eRequestNotAcceptable));
            }
            catch( Protocols::E2eDataTransferProtocol::E2eException &ex )
            {
                CTILOG_EXCEPTION_WARN(dout, ex, "device " << activeRequest.request.parameters.rfnIdentifier);

                continue;
            }

            for( const auto & commandResult : commandResults )
            {
                CTILOG_INFO(dout, "Result ["<< commandResult.status <<", "<< commandResult.description <<"] for device "<< indication.rfnIdentifier);
            }

            _tickResults.emplace_back(std::move(activeRequest.request), std::move(commandResults));

            completedDevices.insert(indication.rfnIdentifier);

            CTILOG_INFO(dout, "Erasing active request for device " << indication.rfnIdentifier);

            _activeRequests.erase(indication.rfnIdentifier);
        }
    }

    return completedDevices;
}


RfnRequestManager::RfnIdentifierSet RfnRequestManager::handleConfirms()
{
    RfnIdentifierSet rejected;

    ConfirmQueue recentConfirms;

    {
        LockGuard guard(_confirmMux);

        std::swap(recentConfirms, _confirms);
    }

    {
        LockGuard guard(_activeRequestsMux);

        for( auto &confirm : recentConfirms )
        {
            auto request = mapFindRef(_activeRequests, confirm.rfnIdentifier);

            if( ! request )
            {
                CTILOG_WARN(dout, "Confirm message received for inactive device "<< confirm.rfnIdentifier);
                continue;
            }

            ActiveRfnRequest &activeRequest = *request;

            CTILOG_INFO(dout, "Confirm message received for device "<< activeRequest.request.parameters.rfnIdentifier);

            if( confirm.error )
            {
                const auto commandErrors = activeRequest.request.command->handleError(CtiTime::now(), confirm.error);

                for( const auto & commandError : commandErrors )
                {
                    CTILOG_INFO(dout, "Result [" << commandError.status << ", " << commandError.description << "] for device " << confirm.rfnIdentifier);
                }

                _tickResults.emplace_back(std::move(activeRequest.request), std::move(commandErrors));

                rejected.insert(confirm.rfnIdentifier);

                _activeRequests.erase(confirm.rfnIdentifier);

                continue;
            }

            _awaitingIndications.emplace(CtiTime::now().addSeconds(activeRequest.currentPacket.retransmissionDelay), RfnRequestIdentifier { confirm.rfnIdentifier, _activeTokens[confirm.rfnIdentifier] } );
        }
    }

    return rejected;
}


RfnRequestManager::RfnIdentifierSet RfnRequestManager::handleTimeouts()
{
    const auto Now = std::chrono::system_clock::now();

    RfnIdentifierSet expirations;

    ExpirationCauses recentExpirations;

    {
        std::lock_guard<std::mutex> guard(_expirationMux);

        std::swap(recentExpirations, _expirations);
    }

    RfnIdentifierSet retransmits;

    {
        LockGuard guard(_activeRequestsMux);

        const auto end = _awaitingIndications.upper_bound(Now);

        if( _awaitingIndications.begin() != end )
        {
            for( auto itr = _awaitingIndications.begin(); itr != end; ++itr )
            {
                const auto rfnId = itr->second.rfnId;
                const auto token = itr->second.token;

                if( token == Cti::mapFind(_activeTokens, rfnId) )
                {
                    if( auto request = mapFindRef(_activeRequests, rfnId) )
                    {
                        auto& activeRequest = *request;

                        CTILOG_INFO(dout, "Timeout reply was pending for device "<< activeRequest.request.parameters.rfnIdentifier);

                        stats.incrementFailures(activeRequest.request.parameters.deviceId);

                        if( activeRequest.currentPacket.retransmits < activeRequest.currentPacket.maxRetransmits )
                        {
                            sendE2eDataRequestPacket(
                                activeRequest.currentPacket.payloadSent,
                                activeRequest.request.command->getApplicationServiceId(),
                                activeRequest.request.parameters.rfnIdentifier,
                                E2EDT_RETRANSMIT_PRIORITY,
                                activeRequest.request.parameters.groupMessageId,
                                CtiTime::now() + activeRequest.currentPacket.retransmissionDelay);
                            //  expire the message if it hasn't gone out by the time we will send the next one

                            activeRequest.currentPacket.retransmits++;
                            activeRequest.currentPacket.retransmissionDelay *= 2;

                            CTILOG_INFO(dout, "Retransmit sent ("<< (activeRequest.currentPacket.maxRetransmits - activeRequest.currentPacket.retransmits) <<" remaining) for device "<< activeRequest.request.parameters.rfnIdentifier);

                            continue;
                        }

                        recentExpirations.emplace(activeRequest.request.parameters.rfnIdentifier, ClientErrors::E2eRequestTimeout);
                    }
                    else
                    {
                        CTILOG_WARN(dout, "Could not find active request for active token " << token << ", rfnIdentifier " << rfnId);
                    }
                }
            }

            _awaitingIndications.erase(_awaitingIndications.begin(), end);
        }

        for( const auto &kv : recentExpirations )
        {
            const auto& rfnId = kv.first;
            const auto error  = kv.second;

            auto &request = mapFindRef(_activeRequests, rfnId);

            if( ! request )
            {
                CTILOG_WARN(dout, "Timeout occurred for inactive device " << rfnId);
                continue;
            }

            ActiveRfnRequest &activeRequest = *request;

            CTILOG_INFO(dout, "Timeout occurred for device "<< rfnId);

            auto commandErrors = activeRequest.request.command->handleError(CtiTime::now(), error);

            for( const auto & commandError : commandErrors )
            {
                CTILOG_INFO(dout, "Result [" << commandError.status << ", " << commandError.description << "] for device " << rfnId);
            }

            _tickResults.emplace_back(std::move(activeRequest.request), std::move(commandErrors));

            expirations.insert(rfnId);

            _activeRequests.erase(rfnId);
            _activeTokens.erase(rfnId);
        }
    }

    return expirations;
}


void RfnRequestManager::handleNewRequests(const RfnIdentifierSet &recentCompletions)
{
    RfnIdentifierSet recentlyActive(recentCompletions);

    LockGuard guard(_pendingRequestsMux);

    {
        LockGuard guard(_submittedRequestsMux);

        for( RfnDeviceRequest& r : _submittedRequests )
        {
            const auto rfnId = r.parameters.rfnIdentifier;

            recentlyActive.insert(rfnId);

            _pendingRequests[rfnId].push_back(std::move(r));

            boost::range::push_heap(_pendingRequests[rfnId]);
        }

        _submittedRequests.clear();
    }

    for each( const RfnIdentifier &rfnId in recentlyActive )
    {
        checkForNewRequest(rfnId);
    }
}


std::vector<unsigned char>  RfnRequestManager::sendE2eDtRequest(const std::vector<unsigned char> &payload, const long endpointId, const unsigned long token)
{
    return _e2edt.sendRequest(payload, endpointId, token);
}


std::chrono::minutes calcExpiration(const RfnDeviceRequest &request)
{
    return request.parameters.priority > 7 ? 1h : 24h;
}

void RfnRequestManager::checkForNewRequest(const RfnIdentifier &rfnIdentifier)
{
    LockGuard guard(_activeRequestsMux);

    if( _activeRequests.count(rfnIdentifier) )
    {
        return;  //  already busy
    }

    //  note that _pendingRequestMux is already acquired by handleNewRequests, so we are safe to access _pendingRequests
    RequestHeap &rq = _pendingRequests[rfnIdentifier];

    //  may need to try more than once
    while( ! rq.empty() )
    {
        boost::range::pop_heap(rq);

        RfnDeviceRequest request = std::move(rq.back());

        rq.pop_back();

        CTILOG_INFO(dout, "Got new request ("<< rq.size() <<" remaining) for device "<< rfnIdentifier);

        try
        {
            auto rfnRequest = request.command->executeCommand(CtiTime::now());

            std::vector<unsigned char> e2ePacket;

            try
            {
                e2ePacket = sendE2eDtRequest(rfnRequest, request.parameters.deviceId, request.rfnRequestId);
            }
            catch( Protocols::E2eDataTransferProtocol::PayloadTooLarge )
            {
                throw DeviceCommand::CommandException(
                        ClientErrors::E2eRequestPayloadTooLarge,
                        "Request payload too large (" + CtiNumStr(rfnRequest.size()) + ")");
            }

            auto timeout = std::chrono::system_clock::now() + calcExpiration(request);
            auto currentPacket =
                    sendE2eDataRequestPacket(
                            e2ePacket,
                            request.command->getApplicationServiceId(),
                            request.parameters.rfnIdentifier,
                            request.parameters.priority,
                            request.parameters.groupMessageId,
                            timeout);

            stats.incrementRequests(request.parameters.deviceId, currentPacket.timeSent);

            ActiveRfnRequest newRequest { std::move(request), currentPacket, timeout };

            FormattedList logItems;
            logItems.add("commandString")    << newRequest.request.parameters.commandString;
            //logItems.add("command")          << newRequest.request.parameters.command;
            logItems.add("connectionHandle") << newRequest.request.parameters.connectionHandle;
            logItems.add("deviceId")         << newRequest.request.parameters.deviceId;
            logItems.add("groupMessageId")   << newRequest.request.parameters.groupMessageId;
            logItems.add("priority")         << newRequest.request.parameters.priority;
            logItems.add("rfnIdentifier")    << newRequest.request.parameters.rfnIdentifier;
            logItems.add("rfnRequestId")     << CtiNumStr(newRequest.request.rfnRequestId).xhex().zpad(8);
            logItems.add("userMessageId")    << newRequest.request.parameters.userMessageId;
            logItems.add("current message")          << newRequest.currentPacket.payloadSent;
            logItems.add("retransmission delay")     << newRequest.currentPacket.retransmissionDelay;
            logItems.add("max retransmits")          << newRequest.currentPacket.maxRetransmits;
            logItems.add("timeout")                  << newRequest.timeout;

            CTILOG_INFO(dout, "Added request for device "<< rfnIdentifier << logItems);

            _activeTokens[rfnIdentifier] = newRequest.request.rfnRequestId;
            _activeRequests.emplace(rfnIdentifier, std::move(newRequest));

            return;
        }
        catch( DeviceCommand::CommandException &ce )
        {
            CTILOG_INFO(dout, "Result ["<< ce.error_code <<", "<< ce.error_description <<"] for device "<< rfnIdentifier);

            _tickResults.emplace_back(std::move(request), RfnCommandResultList { { ce.error_description, ce.error_code } });
        }
    }
}


void RfnRequestManager::postResults()
{
    LockGuard guard(_resultsMux);

    _results.insert(
            _results.end(),
            std::make_move_iterator(_tickResults.begin()), 
            std::make_move_iterator(_tickResults.end()));

    _tickResults.clear();
}


auto RfnRequestManager::getResults(unsigned max) -> ResultQueue
{
    LockGuard guard(_resultsMux);

    ResultQueue tmp { 
            std::make_move_iterator(_results.begin()), 
            std::make_move_iterator(_results.end())};

    _results.clear();

    return tmp;
}


void RfnRequestManager::cancelByGroupMessageId(long groupMessageId)
{
    const auto rfnDeviceRequestGroupIdMatches =
        [=](const RfnDeviceRequest &r) {
            return r.parameters.groupMessageId == groupMessageId;
        };

    {
        LockGuard guard(_pendingRequestsMux);

        {
            LockGuard guard(_submittedRequestsMux);

            _submittedRequests.erase(
                    std::remove_if(
                            _submittedRequests.begin(),
                            _submittedRequests.end(),
                            rfnDeviceRequestGroupIdMatches),
                    _submittedRequests.end());
        }

        for( auto &kv : _pendingRequests )
        {
            auto &queue = kv.second;

            auto itr = queue.begin();

            while( itr != queue.end() )
            {
                if( rfnDeviceRequestGroupIdMatches(*itr) )
                {
                    queue.erase(itr++);
                }
                else
                {
                    ++itr;
                }
            }
        }
    }

    //  Cancel status will be returned via E2eIndication for any canceled messages
    E2eMessenger::cancelByGroupId(groupMessageId);
}


size_t RfnRequestManager::countByGroupMessageId(long groupMessageId)
{
    using boost::adaptors::map_values;
    using boost::adaptors::transformed;

    auto get_group_message_id = make_lambda_overloads(
        [](const RfnDeviceRequest &r) { return r.parameters.groupMessageId; },
        [](const ActiveRfnRequest &a) { return a.request.parameters.groupMessageId; });

    const auto countGroupMessageId = [=](const auto & rq) { return boost::count(rq | transformed(get_group_message_id), groupMessageId); };

    size_t count = 0;

    {
        LockGuard guard(_pendingRequestsMux);

        {
            LockGuard guard(_submittedRequestsMux);

            count += countGroupMessageId(_submittedRequests);
        }

        count += boost::accumulate(_pendingRequests | map_values | transformed(countGroupMessageId), size_t{});
    }

    {
        LockGuard guard(_activeRequestsMux);

        count += countGroupMessageId(_activeRequests | map_values);
    }

    return count;
}


void RfnRequestManager::submitRequests(RfnDeviceRequestList requests)
{
    LockGuard guard(_submittedRequestsMux);

    std::move(std::begin(requests), std::end(requests), std::back_inserter(_submittedRequests));
}


boost::random::mt19937 random_source;

RfnRequestManager::PacketInfo
    RfnRequestManager::sendE2eDataRequestPacket(
        const std::vector<unsigned char> &e2ePacket,
        const ApplicationServiceIdentifiers &asid,
        const RfnIdentifier &rfnIdentifier,
        const unsigned priority,
        const long groupMessageId,
        const CtiTime expiration)
{
    E2eMessenger::Request msg;

    msg.rfnIdentifier = rfnIdentifier;
    msg.payload       = e2ePacket;
    msg.priority      = clamp<1, MAXPRIORITY>(priority);
    msg.expiration    = expiration;
    msg.groupId       = groupMessageId;

    E2eMessenger::sendE2eDt(msg, asid,
            [this](const E2eMessenger::Confirm &msg)
            {
                LockGuard guard(_confirmMux);

                _confirms.push_back(msg);
            },
            [=](const YukonError_t error)
            {
                LockGuard guard(_confirmMux);

                _expirations.emplace(rfnIdentifier, error);
            });

    PacketInfo transmissionReceipt;

    transmissionReceipt.payloadSent = e2ePacket;
    boost::random::uniform_int_distribution<> random_factor(0, gConfigParms.getValueAsInt("E2EDT_CON_RETX_RAND_FACTOR", E2EDT_CON_RETX_RAND_FACTOR));

    transmissionReceipt.retransmissionDelay = gConfigParms.getValueAsInt("E2EDT_CON_RETX_TIMEOUT", E2EDT_CON_RETX_TIMEOUT);
    transmissionReceipt.retransmissionDelay *= 100 + random_factor(random_source);
    transmissionReceipt.retransmissionDelay /= 100;

    transmissionReceipt.maxRetransmits = gConfigParms.getValueAsInt("E2EDT_CON_MAX_RETX", E2EDT_CON_MAX_RETX);

    transmissionReceipt.retransmits = 0;

    return transmissionReceipt;
}


void RfnRequestManager::sendE2eDataAck(
        const std::vector<unsigned char> &e2eAck,
        const ApplicationServiceIdentifiers &asid,
        const RfnIdentifier &rfnIdentifier)
{
    E2eMessenger::Request msg;

    msg.rfnIdentifier = rfnIdentifier;
    msg.payload       = e2eAck;
    msg.priority      = E2EDT_ACK_PRIORITY;
    msg.expiration    = E2EDT_CON_RETX_TIMEOUT;

    //  ignore the confirm and timeout callbacks - this is fire and forget, even if we don't hear back from NM
    E2eMessenger::sendE2eDt(msg, asid);
}


unsigned statsReportFrequency = gConfigParms.getValueAsInt("E2EDT_STATS_REPORTING_INTERVAL", E2EDT_STATS_REPORTING_INTERVAL);
CtiTime nextStatisticsReport = nextScheduledTimeAlignedOnRate(CtiTime::now(), statsReportFrequency);


void RfnRequestManager::handleStatistics()
{
    if( CtiTime::now() > nextStatisticsReport )
    {
        nextStatisticsReport = nextScheduledTimeAlignedOnRate(nextStatisticsReport, statsReportFrequency);

        StreamBuffer report;

        report << "RFN statistics report:" << std::endl;
        report << "Attempted communication with # nodes: " << stats.nodeStatistics.size() << std::endl;
        report << "Node, # requests, avg time/message, # of block tx, avg # blocks/block tx, avg time/block tx, avg time-to-ack, avg attempts/success, success/1 tx, success/2 tx, success/3 tx, failures";

        for each( const Rfn::E2eStatistics::StatisticsPerNode::value_type &spn in stats.nodeStatistics )
        {
            report << std::endl;
            report << spn.first;

            const Rfn::E2eNodeStatistics &nodeStats = spn.second;

            report << "," << nodeStats.uniqueMessages;
            if( nodeStats.firstRequest && nodeStats.lastRequest && (nodeStats.uniqueMessages > 1) )
            {
                report << "," << static_cast<double>(nodeStats.lastRequest->seconds() - nodeStats.firstRequest->seconds()) / (nodeStats.uniqueMessages - 1);
            }
            else
            {
                report << ",0";
            }
            report << "," << nodeStats.blockTransfers;
            if( nodeStats.blockTransfers )
            {
                report << "," << static_cast<double>(nodeStats.blocksReceived) / nodeStats.blockTransfers;
                report << "," << static_cast<double>(nodeStats.cumulativeBlockTransferDelay) / nodeStats.blockTransfers;
            }
            else
            {
                report << ",0,0";
            }

            unsigned totalSuccesses = 0;
            unsigned totalTransmits = 0;

            for( int i = 0; i < nodeStats.successes.size(); ++i )
            {
                totalSuccesses += nodeStats.successes[i];
                totalTransmits += nodeStats.successes[i] * (i + 1);
            }

            if( totalSuccesses )
            {
                report << "," << static_cast<double>(nodeStats.cumulativeSuccessfulDelay) / totalSuccesses;
                report << "," << static_cast<double>(totalTransmits) / totalSuccesses;
            }
            else
            {
                report << ",0,0";
            }

            for( int i = 0; i < 3; ++i )
            {
                if( nodeStats.successes.size() > i )
                {
                    report << "," << nodeStats.successes[i];
                }
                else
                {
                    report << ",0";
                }
            }

            report << "," << nodeStats.totalFailures;
        }

        CTILOG_INFO(dout, report);

        stats.nodeStatistics.clear();
    }
}

}
} //namespace Cti::Pil
