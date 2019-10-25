#include "precompiled.h"

#include "rfn_e2e_messenger.h"
#include "amq_queues.h"

#include "message_factory.h"

#include "NetworkManagerRequest.h"

#include "cparms.h"
#include "error.h"

#include "std_helper.h"

#include "boostutil.h"

#include <boost/algorithm/string/join.hpp>

#include <random>

namespace Cti::Messaging::Rfn {

enum
{
    E2EDT_NM_TIMEOUT =  30,
    NM_TIMEOUT       =  30
};

extern Serialization::MessageFactory<Rfn::E2eMsg>        e2eMessageFactory;
extern Serialization::MessageFactory<NetworkManagerBase> nmMessageFactory;

E2eMessenger::E2eMessenger() :
    _timeoutProcessor{ [this]{ processTimeouts(); } }
{
    // empty
}

E2eMessenger::~E2eMessenger()
{
    _timeoutProcessor.interrupt();
    _timeoutProcessor.tryJoinOrTerminateFor(Timing::Chrono::seconds(2));
}


void E2eMessenger::start()
{
    _ackProcessorHandle = 
            ActiveMQConnectionManager::registerSessionCallback(
                    [this](const ActiveMQConnectionManager::MessageDescriptor& msg) { ackProcessor(msg); });

    _timeoutProcessor.start();
}


void E2eMessenger::processTimeouts()
try
{
    while( true )
    {
        {
            LockGuard guard(_expirationMux);

            const CtiTime Now;

            handleTimeouts(Now, _ackTimeouts,     _awaitingAcks,     ClientErrors::NetworkManagerTimeout);
            handleTimeouts(Now, _confirmTimeouts, _awaitingConfirms, ClientErrors::NetworkManagerTimeout);
        }

        WorkerThread::sleepFor(Timing::Chrono::seconds(1));
    }
}
catch( WorkerThread::Interrupted &ex )
{
    //  exit gracefully
}


void E2eMessenger::handleTimeouts(const CtiTime Now, MessageExpirations& messageExpirations, HandlingPerMessage& messageHandling, const YukonError_t error)
{
    const auto end = messageExpirations.upper_bound(Now);

    if( messageExpirations.begin() != end )
    {
        for( auto itr = messageExpirations.begin(); itr != end; ++itr )
        {
            const auto messageId = itr->second;

            auto handlingItr = messageHandling.find(messageId);

            if( handlingItr != messageHandling.end() )
            {
                CTILOG_DEBUG(dout, "Timeout occurred for message ID " << messageId);

                handlingItr->second.timeoutCallback(error);

                messageHandling.erase(handlingItr);
            }
        }

        messageExpirations.erase(messageExpirations.begin(), end);
    }
}


//  This is only called by PilServer on startup via RfnRequestManager::start()
void E2eMessenger::registerE2eDtHandler(Indication::Callback callback)
{
    gE2eMessenger->setE2eDtHandler(callback);
}


void E2eMessenger::setE2eDtHandler(Indication::Callback callback)
{
    {
        readers_writer_lock_t::writer_lock_guard_t lock(_callbackMux);

        _e2edtCallback = callback;
    }

    ActiveMQConnectionManager::registerHandler(
            ActiveMQ::Queues::InboundQueue::NetworkManagerE2eDataIndication,
            [this](const ActiveMQConnectionManager::MessageDescriptor &md)
            {
                handleRfnE2eDataIndicationMsg(md.msg);
            });

    ActiveMQConnectionManager::registerHandler(
            ActiveMQ::Queues::InboundQueue::NetworkManagerE2eDataConfirm,
            [this](const ActiveMQConnectionManager::MessageDescriptor &md)
            {
                handleRfnE2eDataConfirmMsg(md.msg);
            });

    ActiveMQConnectionManager::registerHandler(
            ActiveMQ::Queues::InboundQueue::NetworkManagerResponse,
            [this](const ActiveMQConnectionManager::MessageDescriptor &md)
            {
                handleNetworkManagerResponseMsg(md.msg, md.type);
            });

    ActiveMQConnectionManager::start();
}


void E2eMessenger::registerDataStreamingHandler(Indication::Callback callback)
{
    CTILOG_INFO(dout, "Registering Data Streaming callback");

    {
        readers_writer_lock_t::writer_lock_guard_t lock(gE2eMessenger->_callbackMux);

        gE2eMessenger->_dataStreamingCallback = callback;
    }
}


void E2eMessenger::registerDnpHandler(Indication::Callback callback, const RfnIdentifier rfnid)
{
    CTILOG_INFO(dout, "Registering DNP callback for RfnIdentifier " << rfnid);

    {
        readers_writer_lock_t::writer_lock_guard_t lock(gE2eMessenger->_callbackMux);

        gE2eMessenger->_dnp3Callbacks[rfnid] = callback;
    }

    //  We don't need to start the AMQ connection here, since it
    //    was already started by PIL's call to registerE2eDtHandler()
}


void E2eMessenger::handleRfnE2eDataIndicationMsg(const SerializedMessage &msg)
{
    using Serialization::MessagePtr;

    CTILOG_INFO(dout, "Got new SerializedMessage");

    MessagePtr<E2eMsg>::type e2eMsg = e2eMessageFactory.deserialize("com.eaton.eas.yukon.networkmanager.e2e.rfn.E2eDataIndication", msg);

    if( E2eDataIndicationMsg *indicationMsg = dynamic_cast<E2eDataIndicationMsg *>(e2eMsg.get()) )
    {
        CTILOG_INFO(dout, "Got new RfnE2eDataIndicationMsg");

        {
            readers_writer_lock_t::reader_lock_guard_t lock(_callbackMux);

            //  check protocol?

            const auto asid = indicationMsg->applicationServiceId;

            if( isAsid_E2eDt(asid) )
            {
                if( ! _e2edtCallback )
                {
                    CTILOG_WARN(dout, "WARNING - E2EDT ASID " << asid << " unhandled, no E2EDT callback registered");
                    return;
                }

                Indication ind;

                ind.rfnIdentifier = indicationMsg->rfnIdentifier;
                ind.payload       = indicationMsg->payload;
                ind.asid          = indicationMsg->applicationServiceId;

                return (*_e2edtCallback)(ind);
            }

            if( isAsid_Dnp3(asid) )
            {
                boost::optional<Indication::Callback> dnp3Callback = mapFind(_dnp3Callbacks, indicationMsg->rfnIdentifier);

                if( ! dnp3Callback )
                {
                    CTILOG_WARN(dout, "WARNING - DNP3 ASID " << asid << " unhandled, no callback registered for RfnIdentifier " << indicationMsg->rfnIdentifier);
                    return;
                }

                Indication ind;

                ind.rfnIdentifier = indicationMsg->rfnIdentifier;
                ind.payload       = indicationMsg->payload;
                ind.asid          = indicationMsg->applicationServiceId;

                return (*dnp3Callback)(ind);
            }

            if( isAsid_DataStreaming(asid) )
            {
                if( ! _dataStreamingCallback )
                {
                    CTILOG_WARN(dout, "WARNING - Data Streaming ASID " << asid << " unhandled, no callback registered");
                    return;
                }

                Indication ind;

                ind.rfnIdentifier = indicationMsg->rfnIdentifier;
                ind.payload       = indicationMsg->payload;
                ind.asid          = indicationMsg->applicationServiceId;

                return (*_dataStreamingCallback)(ind);
            }
            
            CTILOG_WARN(dout, "WARNING - unknown ASID " << asid << " unhandled");
        }
    }
}


namespace {

    using RT = E2eDataConfirmMsg::ReplyType;
    using CE = ClientErrors;

    const std::map<RT, YukonError_t> ConfirmErrors = {
        { RT::OK                                     , CE::None                           },
        { RT::DESTINATION_DEVICE_ADDRESS_UNKNOWN     , CE::E2eUnknownAddress              },
        { RT::DESTINATION_NETWORK_UNAVAILABLE        , CE::E2eNetworkUnavailable          },
        { RT::PMTU_LENGTH_EXCEEDED                   , CE::E2eRequestPacketTooLarge       },
        { RT::E2E_PROTOCOL_TYPE_NOT_SUPPORTED        , CE::E2eProtocolUnsupported         },
        { RT::NETWORK_SERVER_IDENTIFIER_INVALID      , CE::E2eInvalidNetworkServerId      },
        { RT::APPLICATION_SERVICE_IDENTIFIER_INVALID , CE::E2eInvalidApplicationServiceId },
        { RT::NETWORK_LOAD_CONTROL                   , CE::E2eNetworkLoadControl          },
        { RT::NETWORK_SERVICE_FAILURE                , CE::E2eNetworkServiceFailure       },
        { RT::REQUEST_CANCELED                       , CE::RequestCancelled               },
        { RT::REQUEST_EXPIRED                        , CE::RequestExpired                 }};
}


void E2eMessenger::handleRfnE2eDataConfirmMsg(const SerializedMessage &msg)
{
    using Serialization::MessagePtr;

    CTILOG_INFO(dout, "Got new SerializedMessage");

    MessagePtr<E2eMsg>::type e2eMsg = e2eMessageFactory.deserialize("com.eaton.eas.yukon.networkmanager.e2e.rfn.E2eDataConfirm", msg);

    if( E2eDataConfirmMsg *confirmMsg = dynamic_cast<E2eDataConfirmMsg *>(e2eMsg.get()) )
    {
        CTILOG_INFO(dout, "Got new RfnE2eDataConfirmMsg");

        boost::optional<Confirm::Callback> confirmCallback;

        const auto yukonErrorCode = mapFindOrDefault(ConfirmErrors, confirmMsg->replyType, ClientErrors::E2eErrorUnmapped);

        if( confirmMsg->header )
        {
            LockGuard guard(_expirationMux);

            const auto messageId = confirmMsg->header->messageId;

            auto itr = _awaitingConfirms.find(messageId);

            if( itr != _awaitingConfirms.end() )
            {
                CTILOG_DEBUG(dout, "Confirm message received for message ID " << messageId << " with status " << yukonErrorCode << ", " << CtiError::GetErrorString(yukonErrorCode));

                confirmCallback = itr->second.confirmCallback;

                _awaitingConfirms.erase(itr);
            }
            else
            {
                CTILOG_WARN(dout, "Confirm message received for unknown message ID " << messageId << " with status " << yukonErrorCode << ", " << CtiError::GetErrorString(yukonErrorCode));
            }
        }

        if( confirmCallback )
        {
            Confirm c;

            c.rfnIdentifier = confirmMsg->rfnIdentifier;
            c.error = yukonErrorCode;

            (*confirmCallback)(c);
        }
    }
}


void E2eMessenger::handleNetworkManagerResponseMsg(const SerializedMessage &msg, const std::string &type)
{
    //  Force the message type until Network Manager sends the type in the header
    auto omsg = nmMessageFactory.deserialize( "com.eaton.eas.yukon.networkmanager.NetworkManagerCancelResponse", msg );

    // check for any deserialize failure
    if( ! omsg.get() )
    {
        CTILOG_ERROR(dout, "message: \"" << type << "\" cannot be deserialized.");
        return;
    }

    if( auto response = dynamic_cast<const NetworkManagerCancelResponse *>(omsg.get()) )
    {
        FormattedList cancelDetails;

        cancelDetails.add("Client GUID") << response->clientGuid;
        cancelDetails.add("Session ID")  << response->sessionId;

        std::vector<std::string> results_success, results_notFound, results_invalid;

        for( const auto &kv : response->results )
        {
            auto result_as_string = std::to_string(kv.first);

            switch(kv.second)
            {
                case NetworkManagerCancelResponse::MessageStatus::Success:
                    results_success .emplace_back(result_as_string);
                    break;
                case NetworkManagerCancelResponse::MessageStatus::NotFound:
                    results_notFound.emplace_back(result_as_string);
                    break;
                default:
                    results_invalid .emplace_back(result_as_string);
                    break;
            }
        }

        cancelDetails.add("Success")   << boost::join(results_success,  ",");
        cancelDetails.add("Not found") << boost::join(results_notFound, ",");
        cancelDetails.add("Invalid")   << boost::join(results_invalid,  ",");

        CTILOG_INFO(dout, "Cancel response received:" << cancelDetails);
    }
}


void E2eMessenger::sendE2eDt(const Request &req, const ApplicationServiceIdentifiers asid, Confirm::Callback callback, TimeoutCallback timeout)
{
    gE2eMessenger->serializeAndQueue(req, asid, callback, timeout);
}


void E2eMessenger::sendE2eDt(const Request &req, const ApplicationServiceIdentifiers asid)
{
    gE2eMessenger->serializeAndQueue(req, asid);
}


void E2eMessenger::sendE2eAp_Dnp(const Request &req, Confirm::Callback callback, TimeoutCallback timeout)
{
    gE2eMessenger->serializeAndQueue(req, ApplicationServiceIdentifiers::E2EAP_DNP3, callback, timeout);
}


E2eDataRequestMsg E2eMessenger::createMessageFromRequest(const Request& req, const ApplicationServiceIdentifiers asid)
{
    E2eDataRequestMsg msg;

    msg.applicationServiceId = asid;
    msg.highPriority  = req.priority > 7;
    msg.rfnIdentifier = req.rfnIdentifier;
    msg.protocol      = E2eMsg::Application;
    msg.payload       = req.payload;

    msg.header = Rfn::SessionInfoManager::getNmHeader( req.groupId, req.expiration, req.priority );

    return msg;
}


void E2eMessenger::serializeAndQueue(const Request &req, const ApplicationServiceIdentifiers asid, Confirm::Callback successCallback, TimeoutCallback timeoutCallback)
{
    E2eDataRequestMsg msg = createMessageFromRequest(req, asid);

    CTILOG_DEBUG(dout, "Sending E2E request with message ID " << msg.header.messageId);

    SerializedMessage serialized;

    e2eMessageFactory.serialize(msg, serialized);

    const auto e2eNmTimeout = gConfigParms.getValueAsInt("E2EDT_NM_TIMEOUT", E2EDT_NM_TIMEOUT);

    {
        LockGuard guard(_expirationMux);

        _ackTimeouts.emplace(
            CtiTime::now().addSeconds(e2eNmTimeout),
            msg.header.messageId);

        _awaitingAcks.emplace(
            msg.header.messageId,
            MessageHandling{ req.expiration, successCallback, timeoutCallback });
    }

    ActiveMQConnectionManager::enqueueMessageWithSessionCallback(
            ActiveMQ::Queues::OutboundQueue::NetworkManagerE2eDataRequest,
            serialized,
            _ackProcessorHandle);
}


void E2eMessenger::serializeAndQueue(const Request &req, const ApplicationServiceIdentifiers asid)
{
    E2eDataRequestMsg msg = createMessageFromRequest(req, asid);

    CTILOG_DEBUG(dout, "Sending E2E request with message ID " << msg.header.messageId);

    SerializedMessage serialized;

    e2eMessageFactory.serialize(msg, serialized);

    //  Ignore the ack entirely
    ActiveMQConnectionManager::enqueueMessage(
        ActiveMQ::Queues::OutboundQueue::NetworkManagerE2eDataRequest,
        serialized);
}


void E2eMessenger::cancelByGroupId(const long groupId)
{
    gE2eMessenger->cancel(groupId, NetworkManagerCancelRequest::CancelType::Group);
}


void E2eMessenger::cancel(const long id, NetworkManagerCancelRequest::CancelType cancelType)
{
    if( ! Rfn::SessionInfoManager::isSessionActive() )
    {
        CTILOG_DEBUG(dout, "No NM requests have been sent, cancel request for id " << id << " will not be sent to NM");

        return;
    }

    Rfn::SessionInfo sessionInfo = Rfn::SessionInfoManager::getSessionInfo();

    NetworkManagerCancelRequest msg;

    msg.clientGuid = sessionInfo.appGuid; 
    msg.sessionId  = sessionInfo.sessionId; 
    msg.type       = cancelType;

    msg.ids.insert(id);

    SerializedMessage serialized;

    nmMessageFactory.serialize(msg, serialized);

    ActiveMQConnectionManager::enqueueMessageWithCallback(
            ActiveMQ::Queues::OutboundQueue::NetworkManagerRequest,
            serialized,
            [=](const ActiveMQConnectionManager::MessageDescriptor &md)
            {
                //  ignore the ack message itself - it does echo the full cancel request, so it could eventually be moved to ackProcessor()
            },
            std::chrono::seconds{ NM_TIMEOUT },
            [=]
            {
                CTILOG_ERROR(dout, "Cancel request for id " << id << " timed out");
            });
}


void E2eMessenger::ackProcessor(const ActiveMQConnectionManager::MessageDescriptor& descriptor)
{
    LockGuard guard(_expirationMux);

    if( const auto msg = nmMessageFactory.deserialize( "com.eaton.eas.yukon.networkmanager.NetworkManagerRequestAck", descriptor.msg ) )
    {
        if( const auto reqMsg = dynamic_cast<const Messaging::Rfn::NetworkManagerRequestAck*>(msg.get()) )
        {
            const auto messageId = reqMsg->header.messageId;

            auto itr = _awaitingAcks.find(messageId);

            if( itr != _awaitingAcks.end() )
            {
                CTILOG_DEBUG(dout, "Received ack for message ID " << messageId);
                _confirmTimeouts.emplace(itr->second.messageTimeout, messageId);
                _awaitingConfirms.emplace(*itr);
                _awaitingAcks.erase(itr);
            }
            else
            {
                CTILOG_WARN(dout, "Received ack for unknown message ID " << messageId);
            }
        }
    }
}


}