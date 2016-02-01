#include "precompiled.h"

#include "rfn_e2e_messenger.h"

#include "amq_connection.h"

#include "message_factory.h"

#include "NetworkManagerRequest.h"

#include "std_helper.h"

#include "boostutil.h"

#include <boost/algorithm/string/join.hpp>

#include <random>

namespace Cti {
namespace Messaging {
namespace Rfn {

enum
{
    E2EDT_NM_TIMEOUT =  5,
    NM_TIMEOUT       =  5
};

extern Serialization::MessageFactory<Rfn::E2eMsg>        e2eMessageFactory;
extern Serialization::MessageFactory<NetworkManagerBase> nmMessageFactory;

E2eMessenger::E2eMessenger() :
    _timeoutProcessor([this]{processTimeouts();})
{
    _sessionId = _messageId =
            std::uniform_int_distribution<long long>()(
                    std::mt19937_64(
                            std::random_device()()));
}

E2eMessenger::~E2eMessenger()
{
    _timeoutProcessor.interrupt();
    _timeoutProcessor.tryJoinOrTerminateFor(Timing::Chrono::seconds(2));
}


void E2eMessenger::start()
{
    _timeoutProcessor.start();
}


void E2eMessenger::processTimeouts()
try
{
    while( true )
    {
        {
            LockGuard guard(_expirationMux);

            const auto end = _pendingExpirations.upper_bound(CtiTime::now());

            if( _pendingExpirations.begin() != end )
            {
                for( auto itr = _pendingExpirations.begin(); itr != end; ++itr )
                {
                    const auto messageId = itr->second;

                    auto callbackItr = _pendingCallbacks.find(messageId);

                    if( callbackItr != _pendingCallbacks.end() )
                    {
                        auto callbacks = callbackItr->second;

                        callbacks.timeout();

                        _pendingCallbacks.erase(callbackItr);
                    }
                }

                _pendingExpirations.erase(_pendingExpirations.begin(), end);
            }
        }

        WorkerThread::sleepFor(Timing::Chrono::seconds(1));
    }
}
catch( WorkerThread::Interrupted &ex )
{
    //  exit gracefully
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


namespace {

using ASIDs = ApplicationServiceIdentifiers;

bool isE2eDt(const unsigned char &asid)
{
    static const std::set<unsigned char> e2eDtAsids {
        static_cast<unsigned char>(ASIDs::ChannelManager),
        static_cast<unsigned char>(ASIDs::E2EDT),
        static_cast<unsigned char>(ASIDs::EventManager),
        static_cast<unsigned char>(ASIDs::HubMeterCommandSet),
    };

    return e2eDtAsids.count(asid);
}

bool isDnp3(const unsigned char &asid)
{
    return asid == static_cast<unsigned char>(ASIDs::E2EAP_DNP3);
}

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

            const unsigned asid = indicationMsg->applicationServiceId;

            if( isE2eDt(asid) )
            {
                if( ! _e2edtCallback )
                {
                    CTILOG_WARN(dout, "WARNING - E2EDT ASID " << asid << " unhandled, no E2EDT callback registered");
                    return;
                }

                Indication ind;

                ind.rfnIdentifier = indicationMsg->rfnIdentifier;
                ind.payload       = indicationMsg->payload;

                return (*_e2edtCallback)(ind);
            }

            if( isDnp3(asid) )
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

                return (*dnp3Callback)(ind);
            }

            CTILOG_WARN(dout, "WARNING - unknown ASID " << asid << " unhandled");
        }
    }
}


namespace {

    using RT = E2eDataConfirmMsg::ReplyType;
    using CE = ClientErrors;

    const std::map<RT, YukonError_t> ConfirmErrors = {
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

        if( confirmMsg->header )
        {
            LockGuard guard(_expirationMux);

            const auto messageId = confirmMsg->header->messageId;

            if( auto callbacks = mapFind(_pendingCallbacks, messageId) )
            {
                confirmCallback = callbacks->confirm;

                _pendingCallbacks.erase(messageId);
            }
        }

        if( confirmCallback )
        {
            Confirm c;

            c.rfnIdentifier = confirmMsg->rfnIdentifier;

            if( confirmMsg->replyType != E2eDataConfirmMsg::ReplyType::OK )
            {
                const boost::optional<YukonError_t> error = mapFind(ConfirmErrors, confirmMsg->replyType);

                c.error = (error ? *error : ClientErrors::Unknown);
            }

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
    gE2eMessenger->serializeAndQueue(req, callback, timeout, asid);
}


void E2eMessenger::sendE2eAp_Dnp(const Request &req, Confirm::Callback callback, TimeoutCallback timeout)
{
    gE2eMessenger->serializeAndQueue(req, callback, timeout, ApplicationServiceIdentifiers::E2EAP_DNP3);
}


static const char *PorterGuid = "134B8C32-4505-B5EC-1371-8CBC643446A0";

void E2eMessenger::serializeAndQueue(const Request &req, Confirm::Callback callback, TimeoutCallback timeout, const ApplicationServiceIdentifiers asid)
{
    E2eDataRequestMsg msg;

    msg.applicationServiceId = static_cast<unsigned char>(asid);
    msg.highPriority  = req.priority > 7;
    msg.rfnIdentifier = req.rfnIdentifier;
    msg.protocol      = E2eMsg::Application;
    msg.payload       = req.payload;

    msg.header.clientGuid = PorterGuid;
    msg.header.sessionId  = _sessionId;
    msg.header.messageId  = ++_messageId;
    msg.header.groupId    = req.groupId;
    msg.header.priority   = req.priority;
    msg.header.expiration = req.expiration.seconds() * 1000;

    SerializedMessage serialized;

    e2eMessageFactory.serialize(msg, serialized);

    ActiveMQConnectionManager::enqueueMessageWithCallback(
            ActiveMQ::Queues::OutboundQueue::NetworkManagerE2eDataRequest,
            serialized,
            [=](const ActiveMQConnectionManager::MessageDescriptor &md)
            {
                //  ignore the ack message itself

                LockGuard guard(_expirationMux);

                _pendingExpirations.insert(
                        std::make_pair(
                                req.expiration,
                                msg.header.messageId));

                _pendingCallbacks[msg.header.messageId] = MessageCallbacks{ callback, timeout };
            },
            std::chrono::seconds{ E2EDT_NM_TIMEOUT },
            [=]
            {
                timeout();
            });
}


void E2eMessenger::cancelByGroupId(const long groupId)
{
    gE2eMessenger->cancel(groupId, NetworkManagerCancelRequest::CancelType::Group);
}


void E2eMessenger::cancel(const long id, NetworkManagerCancelRequest::CancelType cancelType)
{
    NetworkManagerCancelRequest msg;

    msg.clientGuid = PorterGuid;
    msg.sessionId  = _sessionId;
    msg.type       = cancelType;

    msg.ids.insert(id);

    SerializedMessage serialized;

    nmMessageFactory.serialize(msg, serialized);

    ActiveMQConnectionManager::enqueueMessageWithCallback(
            ActiveMQ::Queues::OutboundQueue::NetworkManagerRequest,
            serialized,
            [=](const ActiveMQConnectionManager::MessageDescriptor &md)
            {
                //  ignore the ack message itself
            },
            std::chrono::seconds{ NM_TIMEOUT },
            [=]
            {
                CTILOG_ERROR(dout, "Cancel request for id " << id << " timed out");
            });
}


}
}
}
