#include "precompiled.h"

#include "rfn_e2e_messenger.h"

#include "amq_connection.h"

#include "message_factory.h"

#include "std_helper.h"

#include "boostutil.h"

#include <boost/assign/list_of.hpp>

namespace Cti {
namespace Messaging {
namespace Rfn {

extern Serialization::MessageFactory<Rfn::E2eMsg> rfnMessageFactory;

E2eMessenger::E2eMessenger()
{
    ActiveMQConnectionManager::registerHandler(
            ActiveMQ::Queues::InboundQueue::NetworkManagerE2eDataIndication,
            boost::bind(&E2eMessenger::handleRfnE2eDataIndicationMsg, this, _1));
}


void E2eMessenger::registerE2eDtHandler(Indication::Callback callback)
{
    gE2eMessenger->setE2eDtHandler(callback);
}


void E2eMessenger::setE2eDtHandler(Indication::Callback callback)
{
    readers_writer_lock_t::writer_lock_guard_t lock(_callbackMux);

    _e2edtCallback = callback;
}


void E2eMessenger::registerDnpHandler(Indication::Callback callback, const RfnIdentifier rfnid)
{
    CTILOG_INFO(dout, "Registering DNP callback for RfnIdentifier " << rfnid);

    {
        readers_writer_lock_t::writer_lock_guard_t lock(gE2eMessenger->_callbackMux);

        gE2eMessenger->_dnp3Callbacks[rfnid] = callback;
    }

    //  as soon as we get anyone interested in DNP messages, make sure the AMQ handler is started
    ActiveMQConnectionManager::start();
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

    MessagePtr<E2eMsg>::type e2eMsg = rfnMessageFactory.deserialize("com.eaton.eas.yukon.networkmanager.e2e.rfn.E2eDataIndication", msg);

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

    const std::map<RT::type, YukonError_t> ConfirmErrors = {
        { RT::DESTINATION_DEVICE_ADDRESS_UNKNOWN     , CE::E2eUnknownAddress              },
        { RT::DESTINATION_NETWORK_UNAVAILABLE        , CE::E2eNetworkUnavailable          },
        { RT::PMTU_LENGTH_EXCEEDED                   , CE::E2eRequestPacketTooLarge       },
        { RT::E2E_PROTOCOL_TYPE_NOT_SUPPORTED        , CE::E2eProtocolUnsupported         },
        { RT::NETWORK_SERVER_IDENTIFIER_INVALID      , CE::E2eInvalidNetworkServerId      },
        { RT::APPLICATION_SERVICE_IDENTIFIER_INVALID , CE::E2eInvalidApplicationServiceId },
        { RT::NETWORK_LOAD_CONTROL                   , CE::E2eNetworkLoadControl          }};
}


void E2eMessenger::handleRfnE2eDataConfirmMsg(const SerializedMessage &msg, Confirm::Callback callback)
{
    using Serialization::MessagePtr;

    CTILOG_INFO(dout, "Got new SerializedMessage");

    MessagePtr<E2eMsg>::type e2eMsg = rfnMessageFactory.deserialize("com.eaton.eas.yukon.networkmanager.e2e.rfn.E2eDataConfirm", msg);

    if( E2eDataConfirmMsg *confirmMsg = dynamic_cast<E2eDataConfirmMsg *>(e2eMsg.get()) )
    {
        CTILOG_INFO(dout, "Got new RfnE2eDataConfirmMsg");

        Confirm c;

        c.rfnIdentifier = confirmMsg->rfnIdentifier;

        if( confirmMsg->replyType != E2eDataConfirmMsg::ReplyType::OK )
        {
            const boost::optional<YukonError_t> error = mapFind(ConfirmErrors, confirmMsg->replyType);

            c.error = (error ? *error : ClientErrors::Unknown);
        }

        callback(c);
    }
}


void E2eMessenger::sendE2eDt(const Request &req, const ApplicationServiceIdentifiers asid, Confirm::Callback callback)
{
    E2eDataRequestMsg msg;

    msg.applicationServiceId = static_cast<unsigned char>(asid);
    msg.priority      = req.priority;
    msg.rfnIdentifier = req.rfnIdentifier;
    msg.protocol      = E2eMsg::Application;
    msg.payload       = req.payload;

    gE2eMessenger->serializeAndQueue(msg, callback);
}


void E2eMessenger::sendE2eAp_Dnp(const Request &req, Confirm::Callback callback)
{
    E2eDataRequestMsg msg;

    msg.applicationServiceId = static_cast<unsigned char>(ApplicationServiceIdentifiers::E2EAP_DNP3);
    msg.priority      = req.priority;
    msg.rfnIdentifier = req.rfnIdentifier;
    msg.protocol      = E2eMsg::Application;
    msg.payload       = req.payload;

    gE2eMessenger->serializeAndQueue(msg, callback);
}


void E2eMessenger::serializeAndQueue(const E2eDataRequestMsg &msg, Confirm::Callback callback)
{
    SerializedMessage serialized;

    rfnMessageFactory.serialize(msg, serialized);

    ActiveMQConnectionManager::enqueueMessageWithCallback(
            ActiveMQ::Queues::OutboundQueue::NetworkManagerE2eDataRequest,
            serialized,
            boost::bind(&E2eMessenger::handleRfnE2eDataConfirmMsg, this, _1, callback));
}


}
}
}
