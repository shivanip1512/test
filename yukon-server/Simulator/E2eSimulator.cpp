#include "precompiled.h"

#include "E2eSimulator.h"
#include "e2e_packet.h"

#include "RfnMeter.h"
#include "RfDa.h"

#include "message_factory.h"
#include "RfnE2eDataRequestMsg.h"
#include "RfnE2eDataConfirmMsg.h"
#include "RfnE2eDataIndicationMsg.h"
#include "NetworkManagerRequest.h"

#include "prot_e2eDataTransfer.h"
#include "coap_helper.h"

#include "rfn_asid.h"

#include "amq_util.h"
#include "amq_connection.h"
#include "amq_constants.h"
#include "amq_queues.h"
#include "CParms.h"

extern "C" {
#include "coap/pdu.h"
#include "coap/block.h"
}

#include <random>
#include <future>
#include <map>

using namespace Cti::Messaging::ActiveMQ;
using namespace Cti::Messaging::Rfn;

using Cti::Messaging::Serialization::MessageFactory;
using Cti::Messaging::Serialization::MessagePtr;

using Cti::Logging::Vector::Hex::operator<<;
using Cti::Logging::Range::Hex::operator<<;

namespace Cti::Messaging::Rfn {
DLLIMPORT MessageFactory<E2eMsg> e2eMessageFactory;
DLLIMPORT MessageFactory<NetworkManagerBase> nmMessageFactory;
}
    
namespace Cti::Simulator {

E2eSimulator::~E2eSimulator() = default;

E2eSimulator::E2eSimulator()
{
    conn = std::make_unique<ManagedConnection>(Broker::protocol + Broker::defaultHost + ":" + Broker::defaultPort);

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

std::mt19937_64 gen { static_cast<unsigned long long>(std::time(nullptr)) };
std::uniform_real_distribution<double> dist{ 0.0, 1.0 };

void E2eSimulator::handleE2eDtRequest(const cms::Message* msg)
{
    CTILOG_INFO(dout, "Received message on RFN E2E queue");

    if( const auto b = dynamic_cast<const cms::BytesMessage*>(msg) )
    {
        const auto buf = std::unique_ptr<unsigned char>{ b->getBodyBytes() };

        Bytes payload { buf.get(), buf.get() + b->getBodyLength() };

        CTILOG_INFO(dout, "Received BytesMessage on RFN E2E queue, attempting to decode as E2eDataRequest");

        auto e2eMsg = e2eMessageFactory.deserialize("com.eaton.eas.yukon.networkmanager.e2e.rfn.E2eDataRequest", payload);

        if( E2eDataRequestMsg *requestMsgPtr = dynamic_cast<E2eDataRequestMsg *>(e2eMsg.get()) )
        {
            std::unique_ptr<E2eDataRequestMsg> requestMsg{ requestMsgPtr };
            e2eMsg.release();

            CTILOG_INFO(dout, "Got new RfnE2eDataRequestMsg" << requestMsg->payload);

            if( requestMsg->payload.size() > COAP_MAX_PDU_SIZE )
            {
                CTILOG_INFO(dout, "Payload too large: " << requestMsg->payload.size());
                return;
            }

            //  NM timeout
            if( dist(gen) < gConfigParms.getValueAsDouble("SIMULATOR_RFN_NM_TIMEOUT_CHANCE") )
            {
                CTILOG_INFO(dout, "Not sending NM acknowledgement for " << requestMsg->rfnIdentifier);
                return;
            }

            sendNetworkManagerRequestAck(requestMsg->header, msg->getCMSReplyTo());

            //  NM queue timeout
            if( dist(gen) < gConfigParms.getValueAsDouble("SIMULATOR_RFN_NM_QUEUE_TIMEOUT_CHANCE") )
            {
                CTILOG_INFO(dout, "Not sending E2E confirm for " << requestMsg->rfnIdentifier);
                return;
            }

            float delay = dist(gen) * gConfigParms.getValueAsDouble("SIMULATOR_RFN_NM_QUEUE_DELAY_SECONDS", 3);

            std::thread { &E2eSimulator::delayProcessing, this, delay, *requestMsg }.detach();
        }
    }
}


void E2eSimulator::delayProcessing(float delay, const E2eDataRequestMsg requestMsg)
{
    CTILOG_INFO(dout, "Delaying " << delay << " seconds");
    Sleep(delay * 1000);

    sendE2eDataConfirm(requestMsg);

    //  E2E timeout
    if( dist(gen) < gConfigParms.getValueAsDouble("SIMULATOR_RFN_E2E_TIMEOUT_CHANCE") )
    {
        CTILOG_INFO(dout, "Not sending E2E indication for " << requestMsg.rfnIdentifier);
        return;
    }

    processE2eDtRequest(requestMsg);
}

void E2eSimulator::processE2eDtRequest(const E2eDataRequestMsg requestMsg)
{
    if( isAsid_E2eDt(requestMsg.applicationServiceId) )
    {
        auto msgPtr = parseE2eDtRequestPayload(requestMsg.payload, requestMsg.rfnIdentifier);
                        
        if( auto e2edtRequest = dynamic_cast<const e2edt_request_packet*>(msgPtr.get()) )
        {
            //  Request Unacceptable
            if( dist(gen) < gConfigParms.getValueAsDouble("SIMULATOR_RFN_E2E_REQUEST_NOT_ACCEPTABLE_CHANCE") )
            {
                CTILOG_INFO(dout, "Sending E2E Request Not Acceptable for " << requestMsg.rfnIdentifier);

                const auto e2edtReply = buildE2eRequestNotAcceptable(e2edtRequest->id, e2edtRequest->token);

                sendE2eDataIndication(requestMsg, e2edtReply);

                CTILOG_INFO(dout, "Delaying 4 seconds for E2E Request Not Acceptable repeat");

                Sleep(4000);

                CTILOG_INFO(dout, "Sending E2E Request Not Acceptable repeat");

                sendE2eDataIndication(requestMsg, e2edtReply);

                return;
            }

            if( const auto replyPacket = buildResponse(*e2edtRequest, requestMsg.applicationServiceId, requestMsg.rfnIdentifier); 
                ! replyPacket.empty() )
            {
                sendE2eDataIndication(requestMsg, replyPacket);
            }
        }
        else if( auto e2edtReply = dynamic_cast<const e2edt_reply_packet*>(msgPtr.get()) )
        {
            if( auto request = RfnMeter::processReply(*e2edtReply, requestMsg.rfnIdentifier) )
            {
                const auto e2edtRequest = buildE2eDtRequest(*request);

                sendE2eDataIndication(requestMsg, e2edtRequest);
            }
        }
        else
        {
            CTILOG_INFO(dout, "Could not parse E2EDT request for " << requestMsg.rfnIdentifier);
        }
    }
    else if( isAsid_Dnp3(requestMsg.applicationServiceId) )
    {
        if( auto dnp3Response = RfDa::buildDnp3Response(requestMsg.payload); 
            ! dnp3Response.empty() )
        {
            sendE2eDataIndication(requestMsg, dnp3Response);
        }
        else
        {
            CTILOG_INFO(dout, "Not sending DNP3 response for " << requestMsg.rfnIdentifier);
        }
    }
}


void E2eSimulator::sendNetworkManagerRequestAck(const NetworkManagerRequestHeader &header, const cms::Destination* tempQueue)
{
    NetworkManagerRequestAck requestAck;

    requestAck.header = header;

    Bytes requestAckBytes;

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

    Bytes confirmBytes;

    e2eMessageFactory.serialize(confirm, confirmBytes);

    std::unique_ptr<cms::BytesMessage> bytesMsg { producerSession->createBytesMessage() };

    bytesMsg->writeBytes(confirmBytes);

    confirmProducer->send(bytesMsg.get());
}


auto E2eSimulator::parseE2eDtRequestPayload(const Bytes& payload, const RfnIdentifier &rfnId) -> std::unique_ptr<e2edt_packet>
{
    auto request_pdu = Protocols::Coap::scoped_pdu_ptr::parse(payload);

    std::unique_ptr<e2edt_packet> packet;

    switch( const auto type = request_pdu->hdr->type )
    {
        case COAP_MESSAGE_CON:
        case COAP_MESSAGE_NON:
        {
            auto e2edtRequest = std::make_unique<e2edt_request_packet>();

            e2edtRequest->confirmable = (type == COAP_MESSAGE_CON);
            auto typeStr = e2edtRequest->confirmable
                ? "CONfirmable"
                : "NONconfirmable";

            CTILOG_INFO(dout, "Received " << typeStr << " packet (" << request_pdu->hdr->id << ") for rfnIdentifier " << rfnId);

            e2edtRequest->method = static_cast<Protocols::Coap::RequestMethod>(request_pdu->hdr->code);

            packet = std::move(e2edtRequest);
            
            break;
        }
        case COAP_MESSAGE_ACK:
        {
            auto e2edtReply = std::make_unique<e2edt_reply_packet>();

            CTILOG_INFO(dout, "Received ACKnowledge packet (" << request_pdu->hdr->id << ") for rfnIdentifier " << rfnId);

            e2edtReply->status = static_cast<Protocols::Coap::ResponseCode>(request_pdu->hdr->code);

            packet = std::move(e2edtReply);

            break;
        }
        default:
        {
            CTILOG_INFO(dout, "Received unhandled type (" << request_pdu->hdr->type << ") for rfnIdentifier " << rfnId);
            return nullptr;
        }
    }

    packet->id     = request_pdu->hdr->id;
    packet->token  = coap_decode_var_bytes(request_pdu->hdr->token, request_pdu->hdr->token_length);

    coap_opt_iterator_t opt_iter;

    for( auto option = coap_check_option(request_pdu, COAP_OPTION_URI_PATH, &opt_iter); option; option = coap_option_next(&opt_iter) )
    {
        packet->path += "/" + std::string(reinterpret_cast<const char *>(coap_opt_value(option)), coap_opt_length(option));
    }

    if( coap_block_t block; coap_get_block(request_pdu, COAP_OPTION_BLOCK2, &block) )
    {
        packet->block = { block.num, !! block.m, block.szx };
    }

    //  Extract the data from the packet
    unsigned char *data;
    size_t len;

    coap_get_data(request_pdu, &len, &data);

    packet->payload.assign(data, data + len);

    return packet;
}


auto E2eSimulator::buildE2eDtReply(const e2edt_reply_packet& replyContents) const -> Bytes
{
    return Protocols::Coap::scoped_pdu_ptr::make_data_ack(replyContents.token, replyContents.id, replyContents.payload).as_bytes();
}


auto E2eSimulator::buildE2eRequestNotAcceptable(unsigned id, unsigned long token) const -> Bytes
{
    auto reply_pdu = Protocols::Coap::scoped_pdu_ptr::make_ack(token, id, Protocols::Coap::ResponseCode::NotAcceptable);

    return reply_pdu.as_bytes();
}


auto E2eSimulator::buildE2eDtRequest(const e2edt_request_packet& request) const -> Bytes
{
    auto request_pdu = Protocols::Coap::scoped_pdu_ptr::make_confirmable_request(request.method, request.token, request.id);

    std::array<unsigned char, 1024> allOptions;

    size_t allOptionLength = allOptions.size();
    
    std::basic_string<unsigned char> path { request.path.cbegin(), request.path.cend() };

    auto options = coap_split_path(path.data(), path.size(), allOptions.data(), &allOptionLength);

    for( auto buf = allOptions.data(); options--; buf += coap_opt_size(buf) )
    {
        coap_add_option(request_pdu, COAP_OPTION_URI_PATH,
                coap_opt_length(buf),
                coap_opt_value(buf));
    }

    if( request.block )
    {
        unsigned char buf[4];

        unsigned len = coap_encode_var_bytes(buf, (request.block->num << 4) | request.block->size.szx);

        coap_add_option(request_pdu, COAP_OPTION_BLOCK2, len, buf);
    }

    //  add data to reply
    coap_add_data(request_pdu, request.payload.size(), request.payload.data());

    const unsigned char *raw_reply_pdu = reinterpret_cast<unsigned char *>(request_pdu->hdr);

    return { raw_reply_pdu,
             raw_reply_pdu + request_pdu->length };
}


void E2eSimulator::sendE2eDataIndication(const E2eDataRequestMsg &requestMsg, const Bytes& payload)
{
    E2eDataIndicationMsg indication;

    indication.applicationServiceId = requestMsg.applicationServiceId;
    indication.highPriority  = requestMsg.highPriority;
    indication.payload       = payload;
    indication.protocol      = requestMsg.protocol;
    indication.rfnIdentifier = requestMsg.rfnIdentifier;
    indication.security      = requestMsg.security;

    Bytes indicationBytes;

    e2eMessageFactory.serialize(indication, indicationBytes);

    std::unique_ptr<cms::BytesMessage> bytesMsg { producerSession->createBytesMessage() };

    bytesMsg->writeBytes(indicationBytes);

    indicationProducer->send(bytesMsg.get());
}


auto E2eSimulator::buildResponse(const e2edt_request_packet& request, const ApplicationServiceIdentifiers asid, const RfnIdentifier& rfnId) -> Bytes
{
    switch( request.method )
    {
        case Protocols::Coap::RequestMethod::Get:
        {
            using ASIDs = ApplicationServiceIdentifiers;

            //  Eventually we will split to RfnMeter vs RfDa based on model/manufacturer
            static std::map<ASIDs, std::function<Bytes (Bytes, const RfnIdentifier&)>>
                asidHandlers {
                    { ASIDs::ChannelManager, &RfnMeter::doChannelManagerRequest },
                    { ASIDs::HubMeterCommandSet, &RfDa::doHubMeterRequest } };

            if( auto handler = mapFind(asidHandlers, asid) )
            {
                e2edt_reply_packet replyPacket;

                replyPacket.id = request.id;
                replyPacket.token = request.token;
                replyPacket.payload = (*handler)(request.payload, rfnId);
                replyPacket.status = Protocols::Coap::ResponseCode::Content;

                return buildE2eDtReply(replyPacket);
            }

            CTILOG_INFO(dout, "Received unhandled ASID (" << static_cast<int>(asid) << ") for rfnIdentifier " << rfnId);
            return {};
        }
        case Protocols::Coap::RequestMethod::Post:
        {
            //  The only POST we process at present is the Set Meter Configuration request, which results in a GET request back to Yukon.
            return processRfnPostRequest(request, asid, rfnId);
        }
        default:
        {
            CTILOG_INFO(dout, "Received unknown method (" << static_cast<int>(request.method) << ") for rfnIdentifier " << rfnId);
            return {};
        }
    }
}

auto E2eSimulator::processRfnPostRequest(const e2edt_request_packet& post_request, const ApplicationServiceIdentifiers asid, const RfnIdentifier& rfnId) -> Bytes
{
    switch( asid )
    {
        case ApplicationServiceIdentifiers::ChannelManager:
        {
            if( auto request = RfnMeter::processChannelManagerPost(post_request, rfnId) )
            {
                return buildE2eDtRequest(*request);
            }
        }
    }

    return {};
}

}