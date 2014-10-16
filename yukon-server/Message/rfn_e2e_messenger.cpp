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
    readers_writer_lock_t::writer_lock_guard_t lock(gE2eMessenger->_callbackMux);

    gE2eMessenger->_e2edtCallback = callback;
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

bool operator==(const unsigned char value, const ApplicationServiceIdentifiers &asid)
{
    return value == asid.value;
}

bool isE2eDt(const unsigned char &asid)
{
    return (asid == ApplicationServiceIdentifiers::ChannelManager)
        || (asid == ApplicationServiceIdentifiers::E2EDT)
        || (asid == ApplicationServiceIdentifiers::EventManager)
        || (asid == ApplicationServiceIdentifiers::HubMeterCommandSet);
}

bool isDnp3(const unsigned char &asid)
{
    return asid == ApplicationServiceIdentifiers::E2EAP_DNP3;
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

    typedef E2eDataConfirmMsg::ReplyType RT;

    const std::map<E2eDataConfirmMsg::ReplyType::type, YukonError_t> ConfirmErrors = boost::assign::map_list_of
        (RT::DESTINATION_DEVICE_ADDRESS_UNKNOWN     , ClientErrors::E2eUnknownAddress             )
        (RT::DESTINATION_NETWORK_UNAVAILABLE        , ClientErrors::E2eNetworkUnavailable         )
        (RT::PMTU_LENGTH_EXCEEDED                   , ClientErrors::E2eRequestPacketTooLarge      )
        (RT::E2E_PROTOCOL_TYPE_NOT_SUPPORTED        , ClientErrors::E2eProtocolUnsupported        )
        (RT::NETWORK_SERVER_IDENTIFIER_INVALID      , ClientErrors::E2eInvalidNetworkServerId     )
        (RT::APPLICATION_SERVICE_IDENTIFIER_INVALID , ClientErrors::E2eInvalidApplicationServiceId)
        (RT::NETWORK_LOAD_CONTROL                   , ClientErrors::E2eNetworkLoadControl         )
        ;

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


void E2eMessenger::sendE2eDt(const Request &req, const ApplicationServiceIdentifiers &asid, Confirm::Callback callback)
{
    E2eDataRequestMsg msg;

    msg.applicationServiceId = asid.value;
    msg.priority      = req.priority;
    msg.rfnIdentifier = req.rfnIdentifier;
    msg.protocol      = E2eMsg::Application;
    msg.payload       = req.payload;

    gE2eMessenger->serializeAndQueue(msg, callback);
}


void E2eMessenger::sendE2eAp_Dnp(const Request &req, Confirm::Callback callback)
{
    E2eDataRequestMsg msg;

    msg.applicationServiceId = ApplicationServiceIdentifiers::E2EAP_DNP3.value;
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
