#include "precompiled.h"

#include "mgr_rfn_request.h"
#include "amq_connection.h"

#include "dev_rfn.h"

#include "rfn_statistics.h"

#include "std_helper.h"

#include <boost/assign/list_of.hpp>

#include <sstream>

namespace Cti {
namespace Pil {

enum
{
    E2EDT_NM_TIMEOUT           =  5,
    E2EDT_CON_RETX_TIMEOUT     = 60,
    E2EDT_CON_MAX_RETX         =  2,
    E2EDT_CON_RETX_RAND_FACTOR = 10,
    E2EDT_STATS_REPORTING_INTERVAL = 86400,
};

using Cti::Logging::Vector::Hex::operator<<;


template<class T>
struct formatAsHex
{
    const T &value;

    formatAsHex(const T &t) : value(t)  {}
};


template<class T>
std::ostream &operator<<(std::ostream &logger, const formatAsHex<T> &wrapper)
{
    const std::ios_base::fmtflags oldflags = logger.flags( std::ios::hex | std::ios::showbase | std::ios::right );
    const char oldfill = logger.fill('0');

    logger << wrapper.value;

    logger.flags(oldflags);
    logger.fill(oldfill);

    return logger;
}


Rfn::E2eStatistics stats;


RfnRequestManager::RfnRequestManager()
{
    E2eMessenger::registerE2eDtHandler(
            boost::bind(&RfnRequestManager::receiveIndication, this, _1));
}


void RfnRequestManager::receiveIndication(const E2eMessenger::Indication &msg)
{
    CtiLockGuard<CtiCriticalSection> lock(_indicationMux);

    _indications.push_back(msg);
}


void RfnRequestManager::tick()
{
    const RfnIdentifierSet rejected =
            handleConfirms();

    const RfnIdentifierSet completed =
            handleIndications();

    const RfnIdentifierSet expired =
            handleTimeouts();

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

    //  make the results available to PIL
    postResults();

    handleStatistics();
}


RfnRequestManager::RfnIdentifierSet RfnRequestManager::handleIndications()
{
    RfnIdentifierSet completedDevices;

    IndicationQueue recentIndications;

    {
        CtiLockGuard<CtiCriticalSection> lock(_indicationMux);

        std::swap(recentIndications, _indications);
    }

    for each( E2eMessenger::Indication indication in recentIndications )
    {
        RfnIdToActiveRequest::iterator itr = _activeRequests.find(indication.rfnIdentifier);

        if( itr == _activeRequests.end() )
        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << CtiTime() << " Indication message received for inactive device " << indication.rfnIdentifier << " " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

            continue;
        }

        ActiveRfnRequest &activeRequest = itr->second;

        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << CtiTime() << " Indication message received for device " << activeRequest.request.rfnIdentifier << " " << __FUNCTION__ << " @ "<< __FILE__ << " (" << __LINE__ << ")" << std::endl;

            dout << "rfnId: " << activeRequest.request.rfnIdentifier << ": " << indication.payload << std::endl;
        }

        boost::optional<Protocols::E2eDataTransferProtocol::EndpointResponse> optionalResponse;

        try
        {
            optionalResponse = _e2edt.handleIndication(indication.payload, activeRequest.request.deviceId);
        }
        catch( Protocols::E2eDataTransferProtocol::PayloadTooLarge )
        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << CtiTime() << " Indication message payload too large (" << indication.payload.size() << ") for device " << activeRequest.request.rfnIdentifier << " " << __FUNCTION__ << " @ "<< __FILE__ << " (" << __LINE__ << ")" << std::endl;

            continue;
        }

        if( ! optionalResponse )
        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << CtiTime() << " Indication message received with no payload for device " << activeRequest.request.rfnIdentifier << " " << __FUNCTION__ << " @ "<< __FILE__ << " (" << __LINE__ << ")" << std::endl;

            continue;
        }

        const Protocols::E2eDataTransferProtocol::EndpointResponse &er = *optionalResponse;

        if( ! er.ack.empty() )
        {
            //  ignore the return, don't set any timeouts - this is fire and forget
            sendE2eDataRequestPacket(
                    er.ack,
                    activeRequest.request.command->getApplicationServiceId(),
                    activeRequest.request.rfnIdentifier,
                    activeRequest.request.priority);

            stats.incrementAcks(activeRequest.request.deviceId, CtiTime::now());
        }

        if( er.token != activeRequest.request.rfnRequestId )
        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << CtiTime() << " Indication received for inactive request token " << er.token << " for device "<< activeRequest.request.rfnIdentifier << " " << __FUNCTION__ << " @ "<< __FILE__ << " (" << __LINE__ << ")" << std::endl;

            continue;
        }

        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << CtiTime() << " Erasing timeout " << activeRequest.timeout << " for device " << activeRequest.request.rfnIdentifier << " " << __FUNCTION__ << " @ "<< __FILE__ << " (" << __LINE__ << ")" << std::endl;
        }

        _upcomingExpirations[activeRequest.timeout].erase(indication.rfnIdentifier);

        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << CtiTime() << " Response received for token " << er.token << " for device " << activeRequest.request.rfnIdentifier << " " << __FUNCTION__ << " @ " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

            dout << "rfnId: " << activeRequest.request.rfnIdentifier << ": " << er.data << std::endl;
        }

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
                            activeRequest.request.priority);

            activeRequest.timeout = CtiTime::now().seconds() + gConfigParms.getValueAsInt("E2EDT_NM_TIMEOUT", E2EDT_NM_TIMEOUT);
            activeRequest.status  = ActiveRfnRequest::PendingConfirm;

            stats.incrementBlockContinuation(activeRequest.request.deviceId, previousRetransmitCount, activeRequest.currentPacket.timeSent, previousBlockTimeSent);

            {
                CtiLockGuard<CtiLogger> dout_guard(dout);
                dout << CtiTime() << " Block continuation sent for device " << activeRequest.request.rfnIdentifier << " " << __FUNCTION__ << " @ " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

                dout << "rfnId: " << activeRequest.request.rfnIdentifier << ": " << activeRequest.currentPacket.payloadSent << std::endl;
            }

            {
                CtiLockGuard<CtiLogger> dout_guard(dout);
                dout << CtiTime() << " Setting timeout " << activeRequest.timeout << " for device " << activeRequest.request.rfnIdentifier << " " << __FUNCTION__ << " @ "<< __FILE__ << " (" << __LINE__ << ")" << std::endl;
            }

            _upcomingExpirations[activeRequest.timeout].insert(indication.rfnIdentifier);
        }
        else
        {
            std::auto_ptr<RfnDeviceResult> result;

            stats.incrementCompletions(activeRequest.request.deviceId, activeRequest.currentPacket.retransmits, CtiTime::now());

            try
            {
                result.reset(
                        new RfnDeviceResult(
                                    activeRequest.request,
                                    activeRequest.request.command->decodeCommand(CtiTime::now(), activeRequest.response),
                                    ClientErrors::None));
            }
            catch( Devices::Commands::DeviceCommand::CommandException &ce )
            {
                result.reset(
                        new RfnDeviceResult(
                                    activeRequest.request,
                                    ce.error_description,
                                    ce.error_code));
            }

            {
                CtiLockGuard<CtiLogger> dout_guard(dout);
                dout << CtiTime() << " Result [" << result->status << ", " << result->commandResult.description << "] for device " << activeRequest.request.rfnIdentifier << " " << __FUNCTION__ << " @ "<< __FILE__ << " (" << __LINE__ << ")" << std::endl;
            }

            _tickResults.push_back(result);

            completedDevices.insert(indication.rfnIdentifier);

            {
                CtiLockGuard<CtiLogger> dout_guard(dout);
                dout << CtiTime() << " Erasing active request for device " << indication.rfnIdentifier << " " << __FUNCTION__ << " @ "<< __FILE__ << " (" << __LINE__ << ")" << std::endl;
            }
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
        CtiLockGuard<CtiCriticalSection> lock(_confirmMux);

        std::swap(recentConfirms, _confirms);
    }

    for each( E2eMessenger::Confirm confirm in recentConfirms )
    {
        RfnIdToActiveRequest::iterator itr = _activeRequests.find(confirm.rfnIdentifier);

        if( itr == _activeRequests.end() )
        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << CtiTime() << " Confirm message received for inactive device " << confirm.rfnIdentifier << " " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

            continue;
        }

        ActiveRfnRequest &activeRequest = itr->second;

        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << CtiTime() << " Confirm message received for device " << activeRequest.request.rfnIdentifier << " " << __FUNCTION__ << " @ "<< __FILE__ << " (" << __LINE__ << ")" << std::endl;
        }

        if( ! confirm.error )
        {
            {
                CtiLockGuard<CtiLogger> dout_guard(dout);
                dout << CtiTime() << " Erasing timeout " << activeRequest.timeout << " for device " << confirm.rfnIdentifier << " " << __FUNCTION__ << " @ "<< __FILE__ << " (" << __LINE__ << ")" << std::endl;
            }

            _upcomingExpirations[activeRequest.timeout].erase(confirm.rfnIdentifier);

            activeRequest.timeout = CtiTime::now().seconds() + activeRequest.currentPacket.retransmissionDelay;
            activeRequest.status = ActiveRfnRequest::PendingReply;

            {
                CtiLockGuard<CtiLogger> dout_guard(dout);
                dout << CtiTime() << " Setting timeout " << activeRequest.timeout << " for device " << confirm.rfnIdentifier << " " << __FUNCTION__ << " @ "<< __FILE__ << " (" << __LINE__ << ")" << std::endl;
            }

            _upcomingExpirations[activeRequest.timeout].insert(confirm.rfnIdentifier);

            continue;
        }

        std::auto_ptr<RfnDeviceResult> result(
                new RfnDeviceResult(
                        activeRequest.request,
                        activeRequest.request.command->error(CtiTime::now(), *confirm.error),
                        *confirm.error));

        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << CtiTime() << " Result [" << result->status << ", " << result->commandResult.description << "] for device " << activeRequest.request.rfnIdentifier << " " << __FUNCTION__ << " @ "<< __FILE__ << " (" << __LINE__ << ")" << std::endl;
        }

        _tickResults.push_back(result);

        rejected.insert(confirm.rfnIdentifier);

        _activeRequests.erase(confirm.rfnIdentifier);
    }

    return rejected;
}


RfnRequestManager::RfnIdentifierSet RfnRequestManager::handleTimeouts()
{
    const CtiTime Now;

    RfnIdentifierSet expirations;

    ExpirationMap::const_iterator
            expired_itr = _upcomingExpirations.begin(),
            expired_end = _upcomingExpirations.upper_bound(Now.seconds());

    RfnIdentifierSet retransmits;

    for( ; expired_itr != expired_end; ++expired_itr )
    {
        for each( const RfnIdentifier &rfnId in expired_itr->second )
        {
            RfnIdToActiveRequest::iterator request_itr = _activeRequests.find(rfnId);

            if( request_itr == _activeRequests.end() )
            {
                CtiLockGuard<CtiLogger> dout_guard(dout);
                dout << CtiTime() << " Timeout occurred for inactive device " << rfnId << " " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

                continue;
            }

            ActiveRfnRequest &activeRequest = request_itr->second;

            {
                CtiLockGuard<CtiLogger> dout_guard(dout);
                dout << CtiTime() << " Timeout " << expired_itr->first << " occurred for device " << activeRequest.request.rfnIdentifier << " " << __FUNCTION__ << " @ "<< __FILE__ << " (" << __LINE__ << ")" << std::endl;
            }

            if( activeRequest.status == ActiveRfnRequest::PendingReply )
            {
                {
                    CtiLockGuard<CtiLogger> dout_guard(dout);
                    dout << CtiTime() << " Timeout " << expired_itr->first << " reply was pending for device " << activeRequest.request.rfnIdentifier << " " << __FUNCTION__ << " @ "<< __FILE__ << " (" << __LINE__ << ")" << std::endl;
                }

                stats.incrementFailures(activeRequest.request.deviceId);

                if( activeRequest.currentPacket.retransmits < activeRequest.currentPacket.maxRetransmits )
                {
                    sendE2eDataRequestPacket(
                            activeRequest.currentPacket.payloadSent,
                            activeRequest.request.command->getApplicationServiceId(),
                            rfnId,
                            activeRequest.request.priority);

                    activeRequest.currentPacket.retransmits++;
                    activeRequest.currentPacket.retransmissionDelay *= 2;

                    activeRequest.timeout = CtiTime::now().seconds() + gConfigParms.getValueAsInt("E2EDT_NM_TIMEOUT", E2EDT_NM_TIMEOUT);
                    activeRequest.status  = ActiveRfnRequest::PendingConfirm;

                    retransmits.insert(rfnId);

                    {
                        CtiLockGuard<CtiLogger> dout_guard(dout);
                        dout << CtiTime() << " Retransmit sent (" << (activeRequest.currentPacket.maxRetransmits - activeRequest.currentPacket.retransmits) << " remaining) for device " << activeRequest.request.rfnIdentifier << " " << __FUNCTION__ << " @ "<< __FILE__ << " (" << __LINE__ << ")" << std::endl;
                    }

                    continue;
                }
            }

            YukonError_t error = ClientErrors::Unknown;

            switch( activeRequest.status )
            {
                default:
                    {
                        CtiLockGuard<CtiLogger> dout_guard(dout);
                        dout << CtiTime() << " Timeout occurred for device \"" << activeRequest.request.rfnIdentifier << "\" deviceid " << activeRequest.request.deviceId << " in unknown state " << activeRequest.status << " " << __FILE__ << " (" << __LINE__ << ")" << std::endl;
                    }
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

            {
                CtiLockGuard<CtiLogger> dout_guard(dout);
                dout << CtiTime() << " Result [" << result->status << ", " << result->commandResult.description << "] for device " << activeRequest.request.rfnIdentifier << " " << __FUNCTION__ << " @ "<< __FILE__ << " (" << __LINE__ << ")" << std::endl;
            }

            _tickResults.push_back(result);

            expirations.insert(rfnId);

            _activeRequests.erase(rfnId);
        }
    }

    _upcomingExpirations.erase(_upcomingExpirations.begin(), expired_end);

    for each( const RfnIdentifier &rfnId in retransmits )
    {
        RfnIdToActiveRequest::iterator request_itr = _activeRequests.find(rfnId);

        if( request_itr != _activeRequests.end() )
        {
            ActiveRfnRequest &activeRequest = request_itr->second;

            _upcomingExpirations[activeRequest.timeout].insert(rfnId);
        }
    }

    return expirations;
}


void RfnRequestManager::handleNewRequests(const RfnIdentifierSet &recentCompletions)
{
    RfnIdentifierSet recentlyActive(recentCompletions);

    {
        CtiLockGuard<CtiCriticalSection> lock(_submittedRequestsMux);

        for each( RfnDeviceRequest r in _submittedRequests )
        {
            recentlyActive.insert(r.rfnIdentifier);

            _pendingRequests[r.rfnIdentifier].push(r);
        }

        _submittedRequests.clear();
    }

    for each( const RfnIdentifier &rfnId in recentlyActive )
    {
        checkForNewRequest(rfnId);
    }
}


void RfnRequestManager::checkForNewRequest(const RfnIdentifier &rfnIdentifier)
{
    if( _activeRequests.count(rfnIdentifier) )
    {
        return;  //  already busy
    }

    RequestQueue &rq = _pendingRequests[rfnIdentifier];

    //  may need to try more than once
    while( ! rq.empty() )
    {
        RfnDeviceRequest request = rq.top();

        rq.pop();

        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << CtiTime() << " Got new request (" << rq.size() << " remaining) for device " << rfnIdentifier << " " << __FUNCTION__ << " @ "<< __FILE__ << " (" << __LINE__ << ")" << std::endl;
        }

        try
        {
            Devices::Commands::RfnCommand::RfnRequestPayload rfnRequest = request.command->executeCommand(CtiTime::now());

            std::vector<unsigned char> e2ePacket;

            try
            {
                e2ePacket = _e2edt.sendRequest(rfnRequest, request.deviceId, request.rfnRequestId);
            }
            catch( Protocols::E2eDataTransferProtocol::PayloadTooLarge )
            {
                throw Devices::Commands::DeviceCommand::CommandException(
                        ClientErrors::E2eRequestPayloadTooLarge,
                        "Request payload too large (" + CtiNumStr(rfnRequest.size()) + ")");
            }

            ActiveRfnRequest newRequest;

            newRequest.request = request;
            newRequest.currentPacket =
                    sendE2eDataRequestPacket(
                            e2ePacket,
                            request.command->getApplicationServiceId(),
                            request.rfnIdentifier,
                            request.priority);

            stats.incrementRequests(newRequest.request.deviceId, newRequest.currentPacket.timeSent);

            newRequest.timeout = CtiTime::now().seconds() + gConfigParms.getValueAsInt("E2EDT_NM_TIMEOUT", E2EDT_NM_TIMEOUT);
            newRequest.status  = ActiveRfnRequest::PendingConfirm;

            {
                CtiLockGuard<CtiLogger> dout_guard(dout);
                dout << CtiTime() << " Added request for device " << rfnIdentifier << " " << __FUNCTION__ << " @ "<< __FILE__ << " (" << __LINE__ << ")" << std::endl;
                //dout << "request.command         :" << newRequest.request.command          << std::endl;
                dout << "request.commandString   :" << newRequest.request.commandString    << std::endl;
                dout << "request.connectionHandle:" << newRequest.request.connectionHandle << std::endl;
                dout << "request.deviceId        :" << newRequest.request.deviceId         << std::endl;
                dout << "request.groupMessageId  :" << newRequest.request.groupMessageId   << std::endl;
                dout << "request.priority        :" << newRequest.request.priority         << std::endl;
                dout << "request.rfnIdentifier   :" << newRequest.request.rfnIdentifier    << std::endl;
                dout << "request.rfnRequestId    :" << formatAsHex<unsigned long>(newRequest.request.rfnRequestId) << std::endl;
                dout << "request.userMessageId   :" << newRequest.request.userMessageId    << std::endl;
                dout << "current message         :" << newRequest.currentPacket.payloadSent << std::endl;
                dout << "retransmission delay    :" << newRequest.currentPacket.retransmissionDelay << std::endl;
                dout << "max retransmits         :" << newRequest.currentPacket.maxRetransmits << std::endl;
                dout << "status                  :" << newRequest.status                   << std::endl;
                dout << "timeout                 :" << CtiTime(newRequest.timeout)         << std::endl;
            }

            _upcomingExpirations[newRequest.timeout].insert(request.rfnIdentifier);

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

            {
                CtiLockGuard<CtiLogger> dout_guard(dout);
                dout << CtiTime() << " Result [" << result->status << ", " << result->commandResult.description << "] for device " << request.rfnIdentifier << " " << __FUNCTION__ << " @ "<< __FILE__ << " (" << __LINE__ << ")" << std::endl;
            }

            _tickResults.push_back(result);
        }
    }
}


void RfnRequestManager::postResults()
{
    CtiLockGuard<CtiCriticalSection> lock(_resultsMux);

    _results.transfer(_results.end(), _tickResults);
}


boost::ptr_deque<RfnDeviceResult> RfnRequestManager::getResults(unsigned max)
{
    CtiLockGuard<CtiCriticalSection> lock(_resultsMux);

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
    //  TODO - perhaps a boost::multi_index container to sort by groupMessageId?
    //  Or maybe this is a rare enough event we should just iterate over all messages?
    //  Maybe we even keep a groupMessageId-to-RfnIdentifier mapping?
    //  priority_queue does not allow iteration and arbitrary erasing, so we will need to change containers at least...
}


unsigned long RfnRequestManager::submitRequests(const std::vector<RfnDeviceRequest> &requests, unsigned long requestId)
{
    CtiLockGuard<CtiCriticalSection> lock(_submittedRequestsMux);

    for each(RfnDeviceRequest r in requests)
    {
        r.rfnRequestId = ++requestId;

        _submittedRequests.push_back(r);
    }

    return requestId;
}


boost::random::mt19937 random_source;

void RfnRequestManager::receiveConfirm(const E2eMessenger::Confirm &msg)
{
    CtiLockGuard<CtiCriticalSection> lock(_confirmMux);

    _confirms.push_back(msg);
}

RfnRequestManager::PacketInfo
    RfnRequestManager::sendE2eDataRequestPacket(
        const std::vector<unsigned char> &e2ePacket,
        const ApplicationServiceIdentifiers &asid,
        const RfnIdentifier &rfnIdentifier,
        const unsigned priority)
{
    E2eMessenger::Request msg;

    msg.rfnIdentifier = rfnIdentifier;
    msg.payload       = e2ePacket;
    msg.priority      = std::max<char>(1, std::min<unsigned>(priority, MAXPRIORITY));

    E2eMessenger::sendE2eDt(msg, asid, boost::bind(&RfnRequestManager::receiveConfirm, this, _1));

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
        report << "Node, # requests, avg time/message, # of block tx, avg # blocks/block tx, avg time/block tx, avg time-to-ack, avg attempts/success, success/1 tx, success/2 tx, success/3 tx, failures" << std::endl;

        for each( const Rfn::E2eStatistics::StatisticsPerNode::value_type &spn in stats.nodeStatistics )
        {
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

            report << std::endl;
        }

        {
            CtiLockGuard<CtiLogger> guard(dout);
            dout << CtiTime() << " " << report.str() << std::endl;
        }

        stats.nodeStatistics.clear();
    }
}


}
}
