#include "precompiled.h"

#include "rfn_e2e_messenger.h"

#include "amq_connection.h"

#include "message_factory.h"

#include "std_helper.h"

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


void E2eMessenger::registerHandler(const ApplicationServiceIdentifiers asid, Indication::Callback callback)
{
    readers_writer_lock_t::writer_lock_guard_t lock(gE2eMessenger->_indicationMux);

    gE2eMessenger->_indicationCallbacks[asid] = callback;
}


void E2eMessenger::handleRfnE2eDataIndicationMsg(const SerializedMessage &msg)
{
    using Serialization::MessagePtr;

    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << CtiTime() << " Got new SerializedMessage " << __FUNCTION__ << " @ "<< __FILE__ << " (" << __LINE__ << ")" << std::endl;
    }

    MessagePtr<E2eMsg>::type e2eMsg = rfnMessageFactory.deserialize("com.eaton.eas.yukon.networkmanager.e2e.rfn.E2eDataIndication", msg);

    if( E2eDataIndicationMsg *indicationMsg = dynamic_cast<E2eDataIndicationMsg *>(e2eMsg.get()) )
    {
        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << CtiTime() << " Got new RfnE2eDataIndicationMsg " << __FUNCTION__ << " @ "<< __FILE__ << " (" << __LINE__ << ")" << std::endl;
        }

        {
            readers_writer_lock_t::reader_lock_guard_t lock(_indicationMux);

            const boost::optional<Indication::Callback> callback = mapFind(_indicationCallbacks, indicationMsg->applicationServiceId);

            if( ! callback )
            {
                Indication ind;

                //  check protocol?

                ind.rfnIdentifier = indicationMsg->rfnIdentifier;
                ind.payload       = indicationMsg->payload;

                (*callback)(ind);
            }
            else
            {
                CtiLockGuard<CtiLogger> dout_guard(dout);
                dout << CtiTime() << " WARNING - ASID " << indicationMsg->applicationServiceId << " unhandled " << __FUNCTION__ << " @ "<< __FILE__ << " (" << __LINE__ << ")" << std::endl;
            }
        }
    }
}


namespace {

    typedef E2eDataConfirmMsg::ReplyType RT;

    const std::map<E2eDataConfirmMsg::ReplyType::type, YukonError_t> ConfirmErrors = boost::assign::map_list_of
        (RT::DESTINATION_DEVICE_ADDRESS_UNKNOWN     , ErrorUnknownAddress             )
        (RT::DESTINATION_NETWORK_UNAVAILABLE        , ErrorNetworkUnavailable         )
        (RT::PMTU_LENGTH_EXCEEDED                   , ErrorRequestPacketTooLarge      )
        (RT::E2E_PROTOCOL_TYPE_NOT_SUPPORTED        , ErrorProtocolUnsupported        )
        (RT::NETWORK_SERVER_IDENTIFIER_INVALID      , ErrorInvalidNetworkServerId     )
        (RT::APPLICATION_SERVICE_IDENTIFIER_INVALID , ErrorInvalidApplicationServiceId)
        (RT::NETWORK_LOAD_CONTROL                   , ErrorNetworkLoadControl         )
        ;

}


void E2eMessenger::handleRfnE2eDataConfirmMsg(const SerializedMessage &msg, Confirm::Callback callback)
{
    using Serialization::MessagePtr;

    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << CtiTime() << " Got new SerializedMessage " << __FUNCTION__ << " @ "<< __FILE__ << " (" << __LINE__ << ")" << std::endl;
    }

    MessagePtr<E2eMsg>::type e2eMsg = rfnMessageFactory.deserialize("com.eaton.eas.yukon.networkmanager.e2e.rfn.E2eDataConfirm", msg);

    if( E2eDataConfirmMsg *confirmMsg = dynamic_cast<E2eDataConfirmMsg *>(e2eMsg.get()) )
    {
        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            dout << CtiTime() << " Got new RfnE2eDataConfirmMsg " << __FUNCTION__ << " @ "<< __FILE__ << " (" << __LINE__ << ")" << std::endl;
        }

        Confirm c;

        c.rfnIdentifier = confirmMsg->rfnIdentifier;

        const boost::optional<YukonError_t> error = mapFind(ConfirmErrors, confirmMsg->replyType);

        c.error = (error ? *error : UnknownError);

        callback(c);
    }
}


void E2eMessenger::sendE2eDt(const Request &req, ApplicationServiceIdentifiers asid, Confirm::Callback callback)
{
    E2eDataRequestMsg msg;

    msg.applicationServiceId = asid.value;
    msg.high_priority = true;
    msg.rfnIdentifier = req.rfnIdentifier;
    msg.protocol      = E2eMsg::Application;
    msg.payload       = req.payload;

    gE2eMessenger->serializeAndQueue(msg, callback);
}


void E2eMessenger::sendE2eAp_Dnp(const Request &req, Confirm::Callback callback)
{
    E2eDataRequestMsg msg;

    msg.applicationServiceId = ApplicationServiceIdentifiers::E2EAP_DNP3.value;
    msg.high_priority = true;
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
