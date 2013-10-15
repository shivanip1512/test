#include "precompiled.h"

#include "mgr_rfn_request.h"
#include "amq_connection.h"

#include "dev_rfn.h"

#include "message_factory.h"

#include "RfnE2eDataRequestMsg.h"
#include "RfnE2eDataConfirmMsg.h"
#include "RfnE2eDataIndicationMsg.h"

#include "std_helper.h"

#include <boost/assign/list_of.hpp>

namespace Cti {
namespace Pil {

extern Messaging::Serialization::MessageFactory<Messaging::Rfn::E2eMsg> rfnMessageFactory;

enum
{
    E2EDT_AMQ_TIMEOUT      =  5,
    E2EDT_CON_RETX_TIMEOUT = 60,
    E2EDT_CON_MAX_RETX     =  2,
};

using Devices::RfnIdentifier;


RfnRequestManager::RfnRequestManager() :
    _generator(std::time(0))
{
    using Messaging::ActiveMQConnectionManager;
    using Messaging::ActiveMQ::Queues::InboundQueue;

    ActiveMQConnectionManager::registerHandler(
        InboundQueue::NetworkManagerE2eDataIndication,
        boost::bind(&RfnRequestManager::handleRfnE2eDataIndicationMsg, this, _1));
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

    //  send the messages to ActiveMQ
    sendMessages();

    //  make the results available to PIL
    postResults();
}


RfnRequestManager::RfnIdentifierSet RfnRequestManager::handleIndications()
{
    RfnIdentifierSet completedDevices;

    IndicationQueue recentIndications;

    {
        CtiLockGuard<CtiCriticalSection> lock(_indicationMux);

        recentIndications.transfer(recentIndications.begin(), _indications);
    }

    while( ! recentIndications.empty() )
    {
        IndicationQueue::auto_type indication = recentIndications.pop_front();

        RfnIdToActiveRequest::iterator itr = _activeRequests.find(indication->rfnIdentifier);

        if( itr == _activeRequests.end() )
        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << CtiTime() << " E2eIndicationMsg received for inactive device " << indication->rfnIdentifier << " " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

            continue;
        }

        ActiveRfnRequest &activeRequest = itr->second;

        Protocols::E2eDataTransferProtocol::EndpointResponse er = _e2edt.handleIndication(indication->payload);

        _upcomingExpirations[activeRequest.timeout].erase(indication->rfnIdentifier);

        activeRequest.response.insert(
                activeRequest.response.end(),
                er.data.begin(),
                er.data.end());

        if( er.blockContinuation )
        {
            _messages.push_back(*er.blockContinuation);

            activeRequest.timeout = CtiTime::now().seconds() + gConfigParms.getValueAsInt("E2EDT_AMQ_TIMEOUT", E2EDT_AMQ_TIMEOUT);

            _upcomingExpirations[activeRequest.timeout].insert(indication->rfnIdentifier);
        }
        else
        {
            std::auto_ptr<RfnDeviceResult> result(new RfnDeviceResult);

            result->request = activeRequest.request;

            try
            {
                result->commandResult = activeRequest.request.command->decodeCommand(CtiTime::now(), activeRequest.response);
                result->status  = NoError;
            }
            catch( Devices::Commands::DeviceCommand::CommandException &ce )
            {
                result->commandResult.description = ce.error_description;
                result->status   = static_cast<YukonError_t>(ce.error_code);
            }

            _tickResults.push_back(result);

            completedDevices.insert(indication->rfnIdentifier);

            _activeRequests.erase(indication->rfnIdentifier);
        }
    }

    return completedDevices;
}


namespace
{
    typedef Messaging::Rfn::E2eDataConfirmMsg::ReplyType RT;

    const std::map<int, YukonError_t> ConfirmErrors = boost::assign::map_list_of
        (RT::DESTINATION_DEVICE_ADDRESS_UNKNOWN     , ErrorUnknownAddress             )
        (RT::DESTINATION_NETWORK_UNAVAILABLE        , ErrorRequestPacketTooLarge      )
        (RT::PMTU_LENGTH_EXCEEDED                   , ErrorProtocolUnsupported        )
        (RT::E2E_PROTOCOL_TYPE_NOT_SUPPORTED        , ErrorInvalidNetworkServerId     )
        (RT::NETWORK_SERVER_IDENTIFIER_INVALID      , ErrorInvalidApplicationServiceId)
        (RT::APPLICATION_SERVICE_IDENTIFIER_INVALID , ErrorNetworkLoadControl         )
        (RT::NETWORK_LOAD_CONTROL                   , ErrorNetworkUnavailable         )
        ;
}


RfnRequestManager::RfnIdentifierSet RfnRequestManager::handleConfirms()
{
    RfnIdentifierSet rejected;

    ConfirmQueue recentConfirms;

    {
        CtiLockGuard<CtiCriticalSection> lock(_confirmMux);

        recentConfirms.transfer(recentConfirms.begin(), _confirms);
    }

    while( ! recentConfirms.empty() )
    {
        ConfirmQueue::auto_type confirm = recentConfirms.pop_front();

        RfnIdToActiveRequest::iterator itr = _activeRequests.find(confirm->rfnIdentifier);

        if( itr == _activeRequests.end() )
        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << CtiTime() << " E2eConfirmMsg received for inactive device " << confirm->rfnIdentifier << " " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

            continue;
        }

        ActiveRfnRequest &activeDevice = itr->second;

        if( confirm->replyType == Messaging::Rfn::E2eDataConfirmMsg::ReplyType::OK )
        {
            _upcomingExpirations[activeDevice.timeout].erase(confirm->rfnIdentifier);

            activeDevice.timeout = CtiTime::now().seconds() + gConfigParms.getValueAsInt("E2EDT_CON_RETX_TIMEOUT", E2EDT_CON_RETX_TIMEOUT);
            activeDevice.status = ActiveRfnRequest::PendingReply;

            _upcomingExpirations[activeDevice.timeout].insert(confirm->rfnIdentifier);

            continue;
        }

        std::auto_ptr<RfnDeviceResult> result(new RfnDeviceResult);

        result->request = activeDevice.request;

        boost::optional<YukonError_t> error = mapFind(ConfirmErrors, confirm->replyType);

        if( error )
        {
            result->status = *error;
        }
        else
        {
            result->status = UnknownError;
        }

        result->commandResult = result->request.command->error(CtiTime::now(), result->status);

        _tickResults.push_back(result);

        rejected.insert(confirm->rfnIdentifier);

        _activeRequests.erase(confirm->rfnIdentifier);
    }

    return rejected;
}


RfnRequestManager::RfnIdentifierSet RfnRequestManager::handleTimeouts()
{
    const CtiTime now;

    RfnIdentifierSet expirations;

    ExpirationMap::const_iterator
            expired_itr = _upcomingExpirations.begin(),
            expired_end = _upcomingExpirations.upper_bound(now.seconds());

    RfnIdentifierSet retransmits;

    for( ; expired_itr != expired_end; ++expired_itr )
    {
        for each( const RfnIdentifier &rfnId in expired_itr->second )
        {
            RfnIdToActiveRequest::iterator expired_itr = _activeRequests.find(rfnId);

            if( expired_itr == _activeRequests.end() )
            {
                CtiLockGuard<CtiLogger> dout_guard(dout);
                dout << CtiTime() << " Timeout occurred for inactive device " << rfnId << " " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

                continue;
            }

            ActiveRfnRequest &activeRequest = expired_itr->second;

            if( activeRequest.status == ActiveRfnRequest::PendingReply )
            {
                if( activeRequest.retransmits-- )
                {
                    _messages.push_back(activeRequest.requestMessage);

                    activeRequest.status = ActiveRfnRequest::PendingConfirm;

                    retransmits.insert(rfnId);

                    continue;
                }
            }

            std::auto_ptr<RfnDeviceResult> result(new RfnDeviceResult);

            result->request = activeRequest.request;

            switch( activeRequest.status )
            {
                default:
                    {
                        CtiLockGuard<CtiLogger> dout_guard(dout);
                        dout << CtiTime() << " Timeout occurred for device \"" << activeRequest.request.rfnIdentifier << "\" deviceid " << activeRequest.request.deviceId << " in state " << activeRequest.status << " " << __FILE__ << " (" << __LINE__ << ")" << std::endl;
                    }
                    result->status = UnknownError;
                    break;
                case ActiveRfnRequest::PendingConfirm:
                    result->status = ErrorNetworkManagerTimeout;
                    break;
                case ActiveRfnRequest::PendingReply:
                    result->status = ErrorRequestTimeout;
                    break;
            }

            result->commandResult = result->request.command->error(CtiTime::now(), result->status);

            _tickResults.push_back(result);

            expirations.insert(rfnId);

            _activeRequests.erase(rfnId);
        }
    }

    _upcomingExpirations.erase(_upcomingExpirations.begin(), expired_end);

    const time_t newExpiration = CtiTime::now().seconds() + gConfigParms.getValueAsInt("E2EDT_AMQ_TIMEOUT", E2EDT_AMQ_TIMEOUT);

    _upcomingExpirations[newExpiration].insert(retransmits.begin(), retransmits.end());

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

        try
        {
            Devices::Commands::RfnCommand::RfnRequestPayload payload = request.command->executeCommand(CtiTime::now());

            if( ! _e2eIds.count(request.deviceId) )
            {
                _e2eIds[request.deviceId] = boost::random::uniform_int_distribution<>(0x1000, 0xf000)(_generator);
            }

            ActiveRfnRequest &newRequest = _activeRequests[request.rfnIdentifier];

            newRequest.request = request;
            newRequest.e2eId = ++_e2eIds[request.deviceId];
            newRequest.retransmits = gConfigParms.getValueAsInt("E2EDT_CON_MAX_RETX", E2EDT_CON_MAX_RETX);

            Protocols::E2eDataTransferProtocol::SendInfo si = _e2edt.sendRequest(payload, newRequest.e2eId);

            Messaging::Rfn::E2eDataRequestMsg msg;

            msg.applicationServiceId = request.command->getApplicationServiceId();  //  2 for Channel Manager, 6 for Event Manager, etc...
            msg.payload         = si.message;
            msg.high_priority   = false;
            msg.rfnIdentifier   = request.rfnIdentifier;

            SerializedMessage serialized;

            rfnMessageFactory.serialize(msg, serialized);

            newRequest.requestMessage = serialized;
            newRequest.status = ActiveRfnRequest::PendingReply;
            newRequest.timeout = CtiTime::now().seconds() + gConfigParms.getValueAsInt("E2EDT_AMQ_TIMEOUT", E2EDT_AMQ_TIMEOUT);

            _upcomingExpirations[newRequest.timeout].insert(request.rfnIdentifier);

            _messages.push_back(serialized);

            return;
        }
        catch( Devices::Commands::DeviceCommand::CommandException &ce )
        {
            std::auto_ptr<RfnDeviceResult> result(new RfnDeviceResult);

            result->request  = request;
            result->status   = static_cast<YukonError_t>(ce.error_code);
            result->commandResult.description = ce.error_description;

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


void RfnRequestManager::handleRfnE2eDataIndicationMsg(const SerializedMessage &msg)
{
    using Messaging::Rfn::E2eMsg;
    using Messaging::Rfn::E2eDataIndicationMsg;
    using Messaging::Serialization::MessagePtr;

    MessagePtr<E2eMsg>::type e2eMsg = rfnMessageFactory.deserialize("RfnE2eDataIndicationMsg", msg);

    if( E2eDataIndicationMsg *indicationMsg = dynamic_cast<E2eDataIndicationMsg *>(e2eMsg.get()) )
    {
        CtiLockGuard<CtiCriticalSection> lock(_indicationMux);

        e2eMsg.release();

        _indications.push_back(indicationMsg);
    }
}


void RfnRequestManager::handleRfnE2eDataConfirmMsg(const SerializedMessage &msg)
{
    using Messaging::Rfn::E2eMsg;
    using Messaging::Rfn::E2eDataConfirmMsg;
    using Messaging::Serialization::MessagePtr;

    MessagePtr<E2eMsg>::type e2eMsg = rfnMessageFactory.deserialize("RfnE2eDataConfirmMsg", msg);

    if( E2eDataConfirmMsg *confirmMsg = dynamic_cast<E2eDataConfirmMsg *>(e2eMsg.get()) )
    {
        CtiLockGuard<CtiCriticalSection> lock(_confirmMux);

        e2eMsg.release();

        _confirms.push_back(confirmMsg);
    }
}


void RfnRequestManager::cancelByGroupMessageId(long groupMessageId)
{
    //  TODO - perhaps a boost::multi_index container to sort by groupMessageId?
    //  Or maybe this is a rare enough event we should just iterate over all messages?
    //  Maybe we even keep a groupMessageId-to-RfnIdentifier mapping?
    //  priority_queue does not allow iteration and arbitrary erasing, so we will need to change containers at least...
}


void RfnRequestManager::submitRequests(const std::vector<RfnDeviceRequest> &requests)
{
    CtiLockGuard<CtiCriticalSection> lock(_submittedRequestsMux);

    _submittedRequests.insert(
            _submittedRequests.end(),
            requests.begin(),
            requests.end());
}

void RfnRequestManager::sendMessages()
{
    using Messaging::ActiveMQ::Queues::OutboundQueue;

    Messaging::ActiveMQConnectionManager::enqueueMessages(
            OutboundQueue::NetworkManagerE2eDataRequest,
            _messages,
            boost::bind(&RfnRequestManager::handleRfnE2eDataConfirmMsg, this, _1));

    _messages.clear();
}

}
}
