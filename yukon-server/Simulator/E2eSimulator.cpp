#include "precompiled.h"

#include "E2eSimulator.h"

#include "RfnMeter.h"

#include "message_factory.h"
#include "RfnE2eDataRequestMsg.h"
#include "RfnE2eDataConfirmMsg.h"
#include "RfnE2eDataIndicationMsg.h"
#include "NetworkManagerRequest.h"

#include "coap_helper.h"

#include "rfn_asid.h"

#include "amq_util.h"
#include "amq_connection.h"
#include "amq_constants.h"

extern "C" {
#include "coap/pdu.h"
#include "coap/block.h"
#undef E  //  CoAP define that interferes with templates
}

using namespace Cti::Messaging::ActiveMQ;
using namespace Cti::Messaging::Rfn;

using Cti::Messaging::Serialization::MessageFactory;
using Cti::Messaging::Serialization::MessagePtr;

using Cti::Logging::Vector::Hex::operator<<;

namespace Cti {
namespace Messaging {
namespace Rfn {
DLLIMPORT MessageFactory<E2eMsg> e2eMessageFactory;
DLLIMPORT MessageFactory<NetworkManagerBase> nmMessageFactory;
}
}
    
namespace Simulator {

E2eSimulator::~E2eSimulator() = default;

E2eSimulator::E2eSimulator()
{
    conn = std::make_unique<ManagedConnection>(Broker::defaultURI);

    conn->start();

    consumerSession = conn->createSession();
    producerSession = conn->createSession();

    requestConsumer    = createQueueConsumer(*consumerSession, Queues::OutboundQueue::NetworkManagerE2eDataRequest.name);
    confirmProducer    = createQueueProducer(*producerSession, Queues::InboundQueue ::NetworkManagerE2eDataConfirm.name);
    indicationProducer = createQueueProducer(*producerSession, Queues::InboundQueue ::NetworkManagerE2eDataIndication.name);

    requestListener = 
            std::make_unique<MessageListener>(
                    [this](const cms::Message* msg) 
                    { 
                        handleE2eDtRequest(msg); 
                    });

    requestConsumer->setMessageListener(requestListener.get());

    CTILOG_INFO(dout, "E2EDT listener registered");
}

void E2eSimulator::stop()
{
    requestConsumer.reset();
    requestListener.reset();

    indicationProducer.reset();
    confirmProducer.reset();

    producerSession.reset();
    consumerSession.reset();

    conn->close();
    conn.reset();
}

void E2eSimulator::handleE2eDtRequest(const cms::Message* msg)
{
    CTILOG_INFO(dout, "Received message on RFN E2E queue");

    if( const auto b = dynamic_cast<const cms::BytesMessage*>(msg) )
    {
        const auto buf = std::unique_ptr<unsigned char> { b->getBodyBytes() };

        std::vector<unsigned char> payload { buf.get(), buf.get() + b->getBodyLength() };

        CTILOG_INFO(dout, "Received BytesMessage on RFN E2E queue, attempting to decode as E2eDataRequest");

        auto e2eMsg = e2eMessageFactory.deserialize("com.eaton.eas.yukon.networkmanager.e2e.rfn.E2eDataRequest", payload);

        if( E2eDataRequestMsg *requestMsg = dynamic_cast<E2eDataRequestMsg *>(e2eMsg.get()) )
        {
            CTILOG_INFO(dout, "Got new RfnE2eDataRequestMsg" << requestMsg->payload);

            if( requestMsg->payload.size() > COAP_MAX_PDU_SIZE )
            {
                CTILOG_INFO(dout, "Payload too large: " << requestMsg->payload.size());
                return;
            }

            sendNetworkManagerRequestAck(requestMsg->header, msg->getCMSReplyTo());

            sendE2eDataConfirm(*requestMsg);

            if( auto e2edtRequest = parseE2eDtRequestPayload(requestMsg->payload, requestMsg->rfnIdentifier) )
            {
                e2edt_packet replyPacket;

                replyPacket.id      = e2edtRequest->id;
                replyPacket.token   = e2edtRequest->token;
                replyPacket.payload = buildRfnResponse(e2edtRequest->payload, requestMsg->applicationServiceId, requestMsg->rfnIdentifier);
                replyPacket.status  = COAP_RESPONSE_205_CONTENT;

                auto e2edtReply = buildE2eDtReplyPayload(replyPacket);

                sendE2eDataIndication(*requestMsg, e2edtReply);
            }
        }
    }
}


void E2eSimulator::sendNetworkManagerRequestAck(const NetworkManagerRequestHeader &header, const cms::Destination* tempQueue)
{
    NetworkManagerRequestAck requestAck;

    requestAck.header = header;

    std::vector<unsigned char> requestAckBytes;

    nmMessageFactory.serialize(requestAck, requestAckBytes);

    auto tempQueueProducer = createDestinationProducer(*producerSession, tempQueue);

    std::unique_ptr<cms::BytesMessage> bytesMsg { producerSession->createBytesMessage() };

    bytesMsg->writeBytes(requestAckBytes);

    tempQueueProducer->send(bytesMsg.get());
}


void E2eSimulator::sendE2eDataConfirm(const E2eDataRequestMsg& requestMsg)
{
    E2eDataConfirmMsg confirm;

    confirm.applicationServiceId = requestMsg.applicationServiceId;
    confirm.header               = requestMsg.header;
    confirm.protocol             = requestMsg.protocol;
    confirm.replyType            = E2eDataConfirmMsg::ReplyType::OK;
    confirm.rfnIdentifier        = requestMsg.rfnIdentifier;

    std::vector<unsigned char> confirmBytes;

    e2eMessageFactory.serialize(confirm, confirmBytes);

    std::unique_ptr<cms::BytesMessage> bytesMsg { producerSession->createBytesMessage() };

    bytesMsg->writeBytes(confirmBytes);

    confirmProducer->send(bytesMsg.get());
}


auto E2eSimulator::parseE2eDtRequestPayload(const std::vector<unsigned char>& payload, const RfnIdentifier &rfnId) -> std::unique_ptr<e2edt_packet>
{
    //  parse the payload into the CoAP packet - the MESSAGE_NON and REQUEST_GET are default values that will be overwritten
    Protocols::scoped_pdu_ptr request_pdu(coap_pdu_init(COAP_MESSAGE_NON, COAP_REQUEST_GET, COAP_INVALID_TID, COAP_MAX_PDU_SIZE));

    auto mutablePayload = payload;

    coap_pdu_parse(mutablePayload.data(), mutablePayload.size(), request_pdu);

    if( request_pdu->hdr->type != COAP_MESSAGE_CON )
    {
        CTILOG_INFO(dout, "Received unhandled type ("<< request_pdu->hdr->type <<") for rfnIdentifier "<< rfnId);
        return nullptr;
    }

    CTILOG_INFO(dout, "Received CONfirmable packet ("<< request_pdu->hdr->id <<") for rfnIdentifier "<< rfnId);

    auto e2edtRequest = std::make_unique<e2edt_packet>();

    e2edtRequest->id     = request_pdu->hdr->id;
    e2edtRequest->status = request_pdu->hdr->code;
    e2edtRequest->token  = coap_decode_var_bytes(request_pdu->hdr->token, request_pdu->hdr->token_length);

    //  Extract the data from the packet
    unsigned char *data;
    size_t len;

    coap_get_data(request_pdu, &len, &data);

    e2edtRequest->payload.assign(data, data + len);

    return e2edtRequest;
}


std::vector<unsigned char> E2eSimulator::buildE2eDtReplyPayload(const e2edt_packet& replyContents)
{
    Protocols::scoped_pdu_ptr reply_pdu(coap_pdu_init(COAP_MESSAGE_ACK, replyContents.status, replyContents.id, COAP_MAX_PDU_SIZE));

    //  add token to reply
    unsigned char reply_token_buf[4];

    const unsigned reply_token_len = coap_encode_var_bytes(reply_token_buf, replyContents.token);

    coap_add_token(reply_pdu, reply_token_len, reply_token_buf);

    //  add data to reply
    coap_add_data(reply_pdu, replyContents.payload.size(), replyContents.payload.data());

    const unsigned char *raw_reply_pdu = reinterpret_cast<unsigned char *>(reply_pdu->hdr);

    return { raw_reply_pdu,
             raw_reply_pdu + reply_pdu->length };
}


void E2eSimulator::sendE2eDataIndication(const E2eDataRequestMsg &requestMsg, const std::vector<unsigned char>& payload)
{
    E2eDataIndicationMsg indication;

    indication.applicationServiceId = requestMsg.applicationServiceId;
    indication.highPriority  = requestMsg.highPriority;
    indication.payload       = payload;
    indication.protocol      = requestMsg.protocol;
    indication.rfnIdentifier = requestMsg.rfnIdentifier;
    indication.security      = requestMsg.security;

    std::vector<unsigned char> indicationBytes;

    e2eMessageFactory.serialize(indication, indicationBytes);

    std::unique_ptr<cms::BytesMessage> bytesMsg { producerSession->createBytesMessage() };

    bytesMsg->writeBytes(indicationBytes);

    indicationProducer->send(bytesMsg.get());
}


std::vector<unsigned char> E2eSimulator::buildRfnResponse(const std::vector<unsigned char> &request, const unsigned char applicationServiceId, const RfnIdentifier& rfnId)
{
    switch( applicationServiceId )
    {
        case static_cast<unsigned char>(ApplicationServiceIdentifiers::ChannelManager):
        {
            if( ! request.empty() )
            {
                switch( request[0] )
                {
                    case 0x84:
                    case 0x86:
                        return RfnMeter::DataStreamingConfig(request, rfnId);
                }
            }
        }
    }
    return {};
}

}
}
