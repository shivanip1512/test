#include "precompiled.h"

#include "mgr_rfn_request.h"
#include "amq_connection.h"
#include "dev_rfn.h"
#include "rfn_statistics.h"
#include "std_helper.h"

#include <boost/assign/list_of.hpp>
#include <sstream>

using Cti::Logging::Vector::Hex::operator<<;
using Cti::Messaging::Rfn::E2eMessenger;

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

    for each( E2eMessenger::Indication indication in recentIndications )
    {
        RfnIdToActiveRequest::iterator itr = _activeRequests.find(indication.rfnIdentifier);

        if( itr == _activeRequests.end() )
        {
            CTILOG_WARN(dout, "Indication message received for inactive device "<< indication.rfnIdentifier);
            continue;
        }

        ActiveRfnRequest &activeRequest = itr->second;

        CTILOG_INFO(dout, "Indication message received for device "<< activeRequest.request.rfnIdentifier <<
                std::endl << "rfnId: " << activeRequest.request.rfnIdentifier << ": " << indication.payload);

        std::auto_ptr<RfnDeviceResult> result;

        try
        {
            const Protocols::E2eDataTransferProtocol::EndpointResponse er =
                    handleE2eDtIndication(indication.payload, activeRequest.request.deviceId);

            if( ! er.ack.empty() )
            {
                sendE2eDataAck(
                        er.ack,
                        activeRequest.request.command->getApplicationServiceId(),
                        activeRequest.request.rfnIdentifier);

                stats.incrementAcks(activeRequest.request.deviceId, CtiTime::now());
            }

            if( er.token != activeRequest.request.rfnRequestId )
            {
                CTILOG_ERROR(dout, "Indication received for inactive request token "<< er.token <<" for device "<< activeRequest.request.rfnIdentifier);
                continue;
            }

            CTILOG_INFO(dout, "Response received for token "<< er.token <<" for device "<< activeRequest.request.rfnIdentifier <<
                    std::endl <<"rfnId: "<< activeRequest.request.rfnIdentifier << ": " << er.data);

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
                                activeRequest.request.rfnIdentifier,
                                activeRequest.request.priority,
                                activeRequest.timeout);

                activeRequest.status = ActiveRfnRequest::PendingConfirm;

                stats.incrementBlockContinuation(activeRequest.request.deviceId, previousRetransmitCount, activeRequest.currentPacket.timeSent, previousBlockTimeSent);

                CTILOG_INFO(dout, "Block continuation sent for device "<< activeRequest.request.rfnIdentifier <<
                        std::endl <<"rfnId: "<< activeRequest.request.rfnIdentifier <<": "<< activeRequest.currentPacket.payloadSent);

                continue;
            }

            stats.incrementCompletions(activeRequest.request.deviceId, activeRequest.currentPacket.retransmits, CtiTime::now());

            result.reset(
                    new RfnDeviceResult(
                                activeRequest.request,
                                activeRequest.request.command->decodeCommand(CtiTime::now(), activeRequest.response),
                                ClientErrors::None));
        }
        catch( const Devices::Commands::DeviceCommand::CommandException &ce )
        {
            result.reset(
                    new RfnDeviceResult(
                                activeRequest.request,
                                ce.error_description,
                                ce.error_code));
        }
        catch( const Protocols::E2eDataTransferProtocol::RequestNotAcceptable &rne )
        {
            CTILOG_ERROR(dout, "Endpoint indicated request not acceptable for device "<< activeRequest.request.rfnIdentifier);

            result.reset(
                    new RfnDeviceResult(
                                activeRequest.request,
                                rne.reason,
                                ClientErrors::E2eRequestNotAcceptable));
        }
        catch( Protocols::E2eDataTransferProtocol::E2eException &ex )
        {
            CTILOG_EXCEPTION_ERROR(dout, ex, "device " << activeRequest.request.rfnIdentifier);

            continue;
        }

        CTILOG_INFO(dout, "Result ["<< result->status <<", "<< result->commandResult.description <<"] for device "<< activeRequest.request.rfnIdentifier);

        _tickResults.push_back(result);

        completedDevices.insert(indication.rfnIdentifier);

        CTILOG_INFO(dout, "Erasing active request for device " << indication.rfnIdentifier);

        _activeRequests.erase(indication.rfnIdentifier);
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

    for each( E2eMessenger::Confirm confirm in recentConfirms )
    {
        RfnIdToActiveRequest::iterator itr = _activeRequests.find(confirm.rfnIdentifier);

        if( itr == _activeRequests.end() )
        {
            CTILOG_WARN(dout, "Confirm message received for inactive device "<< confirm.rfnIdentifier);
            continue;
        }

        ActiveRfnRequest &activeRequest = itr->second;

        CTILOG_INFO(dout, "Confirm message received for device "<< activeRequest.request.rfnIdentifier);

        if( confirm.error )
        {
            std::auto_ptr<RfnDeviceResult> result(
                new RfnDeviceResult(
                        activeRequest.request,
                        activeRequest.request.command->error(CtiTime::now(), *confirm.error),
                        *confirm.error));

            CTILOG_INFO(dout, "Result ["<< result->status <<", "<< result->commandResult.description <<"] for device "<< activeRequest.request.rfnIdentifier);

            _tickResults.push_back(result);

            rejected.insert(confirm.rfnIdentifier);

            _activeRequests.erase(confirm.rfnIdentifier);

            continue;
        }

        activeRequest.status = ActiveRfnRequest::PendingReply;
    }

    return rejected;
}


RfnRequestManager::RfnIdentifierSet RfnRequestManager::handleTimeouts()
{
    const CtiTime Now;

    RfnIdentifierSet expirations;

    ExpirationQueue recentExpirations;

    {
        std::lock_guard<std::mutex> guard(_expirationMux);

        std::swap(recentExpirations, _expirations);
    }

    RfnIdentifierSet retransmits;

    for( const auto &rfnId : recentExpirations )
    {
        RfnIdToActiveRequest::iterator request_itr = _activeRequests.find(rfnId);

        if( request_itr == _activeRequests.end() )
        {
            CTILOG_WARN(dout, "Timeout occurred for inactive device " << rfnId);
            continue;
        }

        ActiveRfnRequest &activeRequest = request_itr->second;

        CTILOG_INFO(dout, "Timeout occurred for device "<< activeRequest.request.rfnIdentifier);

        if( activeRequest.status == ActiveRfnRequest::PendingReply )
        {
            CTILOG_INFO(dout, "Timeout reply was pending for device "<< activeRequest.request.rfnIdentifier);

            stats.incrementFailures(activeRequest.request.deviceId);

            if( activeRequest.currentPacket.retransmits < activeRequest.currentPacket.maxRetransmits )
            {
                sendE2eDataRequestPacket(
                        activeRequest.currentPacket.payloadSent,
                        activeRequest.request.command->getApplicationServiceId(),
                        activeRequest.request.rfnIdentifier,
                        E2EDT_RETRANSMIT_PRIORITY,
                        CtiTime::now() + activeRequest.currentPacket.retransmissionDelay);
                        //  expire the message if it hasn't gone out by the time we will send the next one

                activeRequest.currentPacket.retransmits++;
                activeRequest.currentPacket.retransmissionDelay *= 2;
                activeRequest.status  = ActiveRfnRequest::PendingConfirm;

                CTILOG_INFO(dout, "Retransmit sent ("<< (activeRequest.currentPacket.maxRetransmits - activeRequest.currentPacket.retransmits) <<" remaining) for device "<< activeRequest.request.rfnIdentifier);

                continue;
            }
        }

        YukonError_t error = ClientErrors::Unknown;

        switch( activeRequest.status )
        {
            default:
                CTILOG_WARN(dout, "Timeout occurred for device \""<< activeRequest.request.rfnIdentifier <<"\" deviceid "<< activeRequest.request.deviceId <<" in unknown state "<< activeRequest.status);
                break;
            case ActiveRfnRequest::PendingConfirm:
                error = ClientErrors::NetworkManagerTimeout;
                break;
            case ActiveRfnRequest::PendingReply:
                error = ClientErrors::E2eRequestTimeout;
                break;
        }

        std::auto_ptr<RfnDeviceResult> result(
                new RfnDeviceResult(
                        activeRequest.request,
                        activeRequest.request.command->error(CtiTime::now(), error),
                        error));

        CTILOG_INFO(dout, "Result ["<< result->status <<", "<< result->commandResult.description <<"] for device "<< activeRequest.request.rfnIdentifier);

        _tickResults.push_back(result);

        expirations.insert(rfnId);

        _activeRequests.erase(rfnId);
    }

    return expirations;
}


void RfnRequestManager::handleNewRequests(const RfnIdentifierSet &recentCompletions)
{
    RfnIdentifierSet recentlyActive(recentCompletions);

    LockGuard guard(_pendingRequestsMux);

    {
        LockGuard guard(_submittedRequestsMux);

        for each( RfnDeviceRequest r in _submittedRequests )
        {
            recentlyActive.insert(r.rfnIdentifier);

            _pendingRequests[r.rfnIdentifier].insert(r);
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


int calcExpiration(const RfnDeviceRequest &request)
{
    return request.priority > 7 ? 3600 : 86400;
}

void RfnRequestManager::checkForNewRequest(const RfnIdentifier &rfnIdentifier)
{
    if( _activeRequests.count(rfnIdentifier) )
    {
        return;  //  already busy
    }

    //  note that _pendingRequestMux is already acquired by handleNewRequests, so we are safe to access _pendingRequests
    RequestQueue &rq = _pendingRequests[rfnIdentifier];

    //  may need to try more than once
    while( ! rq.empty() )
    {
        auto top = rq.begin();

        RfnDeviceRequest request = *top;

        rq.erase(top);

        CTILOG_INFO(dout, "Got new request ("<< rq.size() <<" remaining) for device "<< rfnIdentifier);

        try
        {
            Devices::Commands::RfnCommand::RfnRequestPayload rfnRequest = request.command->executeCommand(CtiTime::now());

            std::vector<unsigned char> e2ePacket;

            try
            {
                e2ePacket = sendE2eDtRequest(rfnRequest, request.deviceId, request.rfnRequestId);
            }
            catch( Protocols::E2eDataTransferProtocol::PayloadTooLarge )
            {
                throw Devices::Commands::DeviceCommand::CommandException(
                        ClientErrors::E2eRequestPayloadTooLarge,
                        "Request payload too large (" + CtiNumStr(rfnRequest.size()) + ")");
            }

            ActiveRfnRequest newRequest;

            newRequest.request = request;
            newRequest.timeout = CtiTime::now() + calcExpiration(request);
            newRequest.currentPacket =
                    sendE2eDataRequestPacket(
                            e2ePacket,
                            newRequest.request.command->getApplicationServiceId(),
                            newRequest.request.rfnIdentifier,
                            newRequest.request.priority,
                            newRequest.timeout);

            stats.incrementRequests(newRequest.request.deviceId, newRequest.currentPacket.timeSent);
            newRequest.status  = ActiveRfnRequest::PendingConfirm;

            FormattedList logItems;
            logItems.add("request.commandString")    << newRequest.request.commandString;
            //logItems.add("request.command")          << newRequest.request.command;
            logItems.add("request.connectionHandle") << newRequest.request.connectionHandle;
            logItems.add("request.deviceId")         << newRequest.request.deviceId;
            logItems.add("request.groupMessageId")   << newRequest.request.groupMessageId;
            logItems.add("request.priority")         << newRequest.request.priority;
            logItems.add("request.rfnIdentifier")    << newRequest.request.rfnIdentifier;
            logItems.add("request.rfnRequestId")     << CtiNumStr(newRequest.request.rfnRequestId).xhex().zpad(8);
            logItems.add("request.userMessageId")    << newRequest.request.userMessageId;
            logItems.add("current message")          << newRequest.currentPacket.payloadSent;
            logItems.add("retransmission delay")     << newRequest.currentPacket.retransmissionDelay;
            logItems.add("max retransmits")          << newRequest.currentPacket.maxRetransmits;
            logItems.add("status")                   << newRequest.status;
            logItems.add("timeout")                  << newRequest.timeout;

            CTILOG_INFO(dout, "Added request for device "<< rfnIdentifier <<
                    logItems);

            _activeRequests[request.rfnIdentifier] = newRequest;

            return;
        }
        catch( Devices::Commands::DeviceCommand::CommandException &ce )
        {
            std::auto_ptr<RfnDeviceResult> result(
                    new RfnDeviceResult(
                            request,
                            ce.error_description,
                            static_cast<YukonError_t>(ce.error_code)));

            CTILOG_ERROR(dout, "Result ["<< result->status <<", "<< result->commandResult.description <<"] for device "<< request.rfnIdentifier);

            _tickResults.push_back(result);
        }
    }
}


void RfnRequestManager::postResults()
{
    LockGuard guard(_resultsMux);

    _results.transfer(_results.end(), _tickResults);
}


boost::ptr_deque<RfnDeviceResult> RfnRequestManager::getResults(unsigned max)
{
    LockGuard guard(_resultsMux);

    boost::ptr_deque<RfnDeviceResult> tmp;

    while( ! _results.empty() && max-- )
    {
        RfnDeviceResult *result = _results.pop_front().release();

        tmp.push_back(result);
    }

    return tmp;
}


void RfnRequestManager::cancelByGroupMessageId(long groupMessageId)
{
    const auto rfnDeviceRequestGroupIdMatches =
        [=](const RfnDeviceRequest &r) {
            return r.groupMessageId == groupMessageId;
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


unsigned long RfnRequestManager::submitRequests(const std::vector<RfnDeviceRequest> &requests, unsigned long requestId)
{
    LockGuard guard(_submittedRequestsMux);

    for each(RfnDeviceRequest r in requests)
    {
        r.rfnRequestId = ++requestId;

        _submittedRequests.push_back(r);
    }

    return requestId;
}


boost::random::mt19937 random_source;

RfnRequestManager::PacketInfo
    RfnRequestManager::sendE2eDataRequestPacket(
        const std::vector<unsigned char> &e2ePacket,
        const ApplicationServiceIdentifiers &asid,
        const RfnIdentifier &rfnIdentifier,
        const unsigned priority,
        const CtiTime expiration)
{
    E2eMessenger::Request msg;

    msg.rfnIdentifier = rfnIdentifier;
    msg.payload       = e2ePacket;
    msg.priority      = clamp<1, MAXPRIORITY>(priority);
    msg.expiration    = expiration;

    E2eMessenger::sendE2eDt(msg, asid,
            [this](const E2eMessenger::Confirm &msg)
            {
                LockGuard guard(_confirmMux);

                _confirms.push_back(msg);
            },
            [=]
            {
                LockGuard guard(_confirmMux);

                _expirations.push_back(rfnIdentifier);
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
    E2eMessenger::sendE2eDt(msg, asid, [](const E2eMessenger::Confirm &msg){}, []{});
}


unsigned statsReportFrequency = gConfigParms.getValueAsInt("E2EDT_STATS_REPORTING_INTERVAL", E2EDT_STATS_REPORTING_INTERVAL);
CtiTime nextStatisticsReport = nextScheduledTimeAlignedOnRate(CtiTime::now(), statsReportFrequency);


void RfnRequestManager::handleStatistics()
{
    if( CtiTime::now() > nextStatisticsReport )
    {
        nextStatisticsReport = nextScheduledTimeAlignedOnRate(nextStatisticsReport, statsReportFrequency);

        std::ostringstream report;

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

        CTILOG_INFO(dout, report.str());

        stats.nodeStatistics.clear();
    }
}

}
} //namespace Cti::Pil
