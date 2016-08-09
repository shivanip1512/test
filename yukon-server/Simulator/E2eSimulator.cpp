#include "precompiled.h"

#include "E2eSimulator.h"

#include "message_factory.h"
#include "RfnE2eDataRequestMsg.h"
#include "RfnE2eDataConfirmMsg.h"
#include "RfnE2eDataIndicationMsg.h"
#include "NetworkManagerRequest.h"

#include "coap_helper.h"

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
        std::vector<unsigned char> payload { 
            b->getBodyBytes(), 
            b->getBodyBytes() + b->getBodyLength() };

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

            {
                NetworkManagerRequestAck requestAck;
    
                requestAck.header = requestMsg->header;

                std::vector<unsigned char> requestAckBytes;

                nmMessageFactory.serialize(requestAck, requestAckBytes);

                auto tempQueueProducer = createDestinationProducer(*producerSession, msg->getCMSReplyTo());
                
                std::unique_ptr<cms::BytesMessage> bytesMsg { producerSession->createBytesMessage() };

                bytesMsg->writeBytes(requestAckBytes);

                tempQueueProducer->send(bytesMsg.get());
            }

            {
                E2eDataConfirmMsg confirm;

                confirm.applicationServiceId = requestMsg->applicationServiceId;
                confirm.header               = requestMsg->header;
                confirm.protocol             = requestMsg->protocol;
                confirm.replyType            = E2eDataConfirmMsg::ReplyType::OK;
                confirm.rfnIdentifier        = requestMsg->rfnIdentifier;

                std::vector<unsigned char> confirmBytes;

                e2eMessageFactory.serialize(confirm, confirmBytes);

                std::unique_ptr<cms::BytesMessage> bytesMsg { producerSession->createBytesMessage() };

                bytesMsg->writeBytes(confirmBytes);

                confirmProducer->send(bytesMsg.get());
            }

            //  parse the payload into the CoAP packet - the MESSAGE_NON and REQUEST_GET are default values that will be overwritten
            Protocols::scoped_pdu_ptr request_pdu(coap_pdu_init(COAP_MESSAGE_NON, COAP_REQUEST_GET, COAP_INVALID_TID, COAP_MAX_PDU_SIZE));

            coap_pdu_parse(&requestMsg->payload.front(), requestMsg->payload.size(), request_pdu);

            if( request_pdu->hdr->type != COAP_MESSAGE_CON )
            {
                CTILOG_INFO(dout, "Received unhandled type ("<< request_pdu->hdr->type <<") for rfnIdentifier "<< requestMsg->rfnIdentifier);
                return;
            }

            CTILOG_INFO(dout, "Received CONfirmable packet ("<< request_pdu->hdr->id <<") for rfnIdentifier "<< requestMsg->rfnIdentifier);
            
            ///
            //  Decode the token
            auto request_token = coap_decode_var_bytes(request_pdu->hdr->token, request_pdu->hdr->token_length);

            //  Extract the data from the packet
            unsigned char *data;
            size_t len;

            coap_get_data(request_pdu, &len, &data);

            std::vector<unsigned char> request_data { data, data + len };

            Protocols::scoped_pdu_ptr reply_pdu(coap_pdu_init(COAP_MESSAGE_ACK, COAP_RESPONSE_205_CONTENT, request_pdu->hdr->id, COAP_MAX_PDU_SIZE));

            //  add token to reply
            unsigned char reply_token_buf[4];

            const unsigned reply_token_len = coap_encode_var_bytes(reply_token_buf, request_token);

            coap_add_token(reply_pdu, reply_token_len, reply_token_buf);

            //  Fibonacci!  1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233
            const std::vector<unsigned char> reply_data { 0x01, 0x01, 0x02, 0x03, 0x05, 0x08, 0x0d, 0x15, 0x22, 0x37, 0x59, 0x90, 0xe9 };

            //  add data to reply
            coap_add_data(reply_pdu, reply_data.size(), reply_data.data());

            const unsigned char *raw_reply_pdu = reinterpret_cast<unsigned char *>(reply_pdu->hdr);

            {
                E2eDataIndicationMsg indication;

                indication.applicationServiceId = requestMsg->applicationServiceId;
                indication.highPriority = requestMsg->highPriority;
                indication.payload.assign( raw_reply_pdu, raw_reply_pdu + reply_pdu->length );
                indication.protocol = requestMsg->protocol;
                indication.rfnIdentifier = requestMsg->rfnIdentifier;
                indication.security = requestMsg->security;

                std::vector<unsigned char> indicationBytes;

                e2eMessageFactory.serialize(indication, indicationBytes);

                std::unique_ptr<cms::BytesMessage> bytesMsg { producerSession->createBytesMessage() };

                bytesMsg->writeBytes(indicationBytes);

                indicationProducer->send(bytesMsg.get());
            }
        }
    }
}

}
}
