#include "precompiled.h"

#include "E2eSimulator.h"

#include "RfnMeter.h"
#include "RfDa.h"

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
#include "amq_queues.h"
#include "CParms.h"

#include "prot_dnpSlave.h"

extern "C" {
#include "coap/pdu.h"
#include "coap/block.h"
}

#include <random>
#include <future>

using namespace Cti::Messaging::ActiveMQ;
using namespace Cti::Messaging::Rfn;
using namespace std::chrono_literals;

using Cti::Messaging::Serialization::MessageFactory;
using Cti::Messaging::Serialization::MessagePtr;

using Cti::Logging::Vector::Hex::operator<<;
using Cti::Logging::Range::Hex::operator<<;

namespace Cti::Messaging::Rfn {
DLLIMPORT MessageFactory<E2eMsg> e2eMessageFactory;
DLLIMPORT MessageFactory<NetworkManagerBase> nmMessageFactory;
}
    
namespace Cti::Simulator {

struct e2edt_packet
{
    std::vector<unsigned char> payload;
    unsigned token;
    unsigned id;
};

struct e2edt_request_packet : e2edt_packet
{
    bool confirmable;
    std::string path;
    std::optional<size_t> block;
    std::optional<size_t> blockSize;
    Protocols::Coap::RequestMethod method;
};

struct e2edt_reply_packet : e2edt_packet
{
    Protocols::Coap::ResponseCode status;
};

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

std::mt19937_64 rd { static_cast<unsigned long long>(std::time(nullptr)) };
std::uniform_real_distribution<double> dist{ 0.0, 1.0 };

void E2eSimulator::handleE2eDtRequest(const cms::Message* msg)
{
    CTILOG_INFO(dout, "Received message on RFN E2E queue");

    if( const auto b = dynamic_cast<const cms::BytesMessage*>(msg) )
    {
        const auto buf = std::unique_ptr<unsigned char> { b->getBodyBytes() };

        std::vector<unsigned char> payload { buf.get(), buf.get() + b->getBodyLength() };

        CTILOG_INFO(dout, "Received BytesMessage on RFN E2E queue, attempting to decode as E2eDataRequest");

        auto e2eMsg = e2eMessageFactory.deserialize("com.eaton.eas.yukon.networkmanager.e2e.rfn.E2eDataRequest", payload);

        if( E2eDataRequestMsg *requestMsgPtr = dynamic_cast<E2eDataRequestMsg *>(e2eMsg.get()) )
        {
            e2eMsg.release();
            std::unique_ptr<E2eDataRequestMsg> requestMsg { requestMsgPtr };

            CTILOG_INFO(dout, "Got new RfnE2eDataRequestMsg" << requestMsg->payload);

            if( requestMsg->payload.size() > COAP_MAX_PDU_SIZE )
            {
                CTILOG_INFO(dout, "Payload too large: " << requestMsg->payload.size());
                return;
            }

            //  NM timeout
            if( dist(rd) < gConfigParms.getValueAsDouble("SIMULATOR_RFN_NM_TIMEOUT_CHANCE") )
            {
                CTILOG_INFO(dout, "Not sending NM acknowledgement for " << requestMsg->rfnIdentifier);
                return;
            }
                
            sendNetworkManagerRequestAck(requestMsg->header, msg->getCMSReplyTo());

            //  NM queue timeout
            if( dist(rd) < gConfigParms.getValueAsDouble("SIMULATOR_RFN_NM_QUEUE_TIMEOUT_CHANCE") )
            {
                CTILOG_INFO(dout, "Not sending E2E confirm for " << requestMsg->rfnIdentifier);
                return;
            }

            float delay = dist(rd) * gConfigParms.getValueAsDouble("SIMULATOR_RFN_NM_QUEUE_DELAY_SECONDS", 3);

            std::thread {
                [this](float delay, E2eDataRequestMsg requestMsg) {

                    CTILOG_INFO(dout, "Delaying " << delay << " seconds");
                    Sleep(delay * 1000);

                    sendE2eDataConfirm(requestMsg);

                    //  E2E timeout
                    if( dist(rd) < gConfigParms.getValueAsDouble("SIMULATOR_RFN_E2E_TIMEOUT_CHANCE") )
                    {
                        CTILOG_INFO(dout, "Not sending E2E indication for " << requestMsg.rfnIdentifier);
                        return;
                    }

                    if( isAsid_E2eDt(requestMsg.applicationServiceId) )
                    {
                        if( auto e2edtRequest = parseE2eDtRequestPayload(requestMsg.payload, requestMsg.rfnIdentifier) )
                        {
                            //e2edtRequest->method

                            //  Request Unacceptable
                            if( dist(rd) < gConfigParms.getValueAsDouble("SIMULATOR_RFN_E2E_REQUEST_NOT_ACCEPTABLE_CHANCE") )
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

                            const auto replyPacket = buildRfnResponse(*e2edtRequest, requestMsg.applicationServiceId, requestMsg.rfnIdentifier);

                            sendE2eDataIndication(requestMsg, replyPacket);
                        }
                        else
                        {
                            CTILOG_INFO(dout, "Could not parse E2EDT request for " << requestMsg.rfnIdentifier);
                        }
                    }
                    else if( isAsid_Dnp3(requestMsg.applicationServiceId) )
                    {
                        if( auto dnp3Response = buildDnp3Response(requestMsg.payload); 
                            ! dnp3Response.empty() )
                        {
                            sendE2eDataIndication(requestMsg, dnp3Response);
                        }
                        else
                        {
                            CTILOG_INFO(dout, "Not sending DNP3 response for " << requestMsg.rfnIdentifier);
                        }
                    }
                }, delay, *requestMsg }.detach();
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


auto E2eSimulator::parseE2eDtRequestPayload(const std::vector<unsigned char>& payload, const RfnIdentifier &rfnId) -> std::unique_ptr<e2edt_request_packet>
{
    auto request_pdu = Protocols::Coap::scoped_pdu_ptr::parse(payload);

    auto e2edtRequest = std::make_unique<e2edt_request_packet>();

    if( request_pdu->hdr->type == COAP_MESSAGE_CON )
    {
        CTILOG_INFO(dout, "Received CONfirmable packet (" << request_pdu->hdr->id << ") for rfnIdentifier " << rfnId);
        e2edtRequest->confirmable = true;
    }
    else if( request_pdu->hdr->type == COAP_MESSAGE_NON )
    {
        CTILOG_INFO(dout, "Received NONconfirmable packet (" << request_pdu->hdr->id << ") for rfnIdentifier " << rfnId);
        e2edtRequest->confirmable = false;
    }
    else if( request_pdu->hdr->type == COAP_MESSAGE_ACK )
    {
        CTILOG_INFO(dout, "Received ACKnowledge packet (" << request_pdu->hdr->id << ") for rfnIdentifier " << rfnId);
        e2edtRequest->confirmable = false;
    }
    else
    {
        CTILOG_INFO(dout, "Received unhandled type ("<< request_pdu->hdr->type <<") for rfnIdentifier "<< rfnId);
        return nullptr;
    }

    e2edtRequest->id     = request_pdu->hdr->id;
    e2edtRequest->token  = coap_decode_var_bytes(request_pdu->hdr->token, request_pdu->hdr->token_length);

    coap_opt_iterator_t opt_iter;

    for( auto option = coap_check_option(request_pdu, COAP_OPTION_URI_PATH, &opt_iter); option; option = coap_option_next(&opt_iter) )
    {
        e2edtRequest->path += "/" + std::string(reinterpret_cast<const char *>(coap_opt_value(option)), coap_opt_length(option));
    }

    e2edtRequest->method = static_cast<Protocols::Coap::RequestMethod>(request_pdu->hdr->code);

    //  Extract the data from the packet
    unsigned char *data;
    size_t len;

    coap_get_data(request_pdu, &len, &data);

    e2edtRequest->payload.assign(data, data + len);

    return e2edtRequest;
}


std::vector<unsigned char> E2eSimulator::buildE2eDtReplyPayload(const e2edt_reply_packet& replyContents) const
{
    return Protocols::Coap::scoped_pdu_ptr::make_data_ack(replyContents.token, replyContents.id, replyContents.payload).as_bytes();
}


std::vector<unsigned char> E2eSimulator::buildE2eRequestNotAcceptable(unsigned id, unsigned long token) const
{
    auto reply_pdu = Protocols::Coap::scoped_pdu_ptr::make_ack(token, id, Protocols::Coap::ResponseCode::NotAcceptable);

    return reply_pdu.as_bytes();
}


std::vector<unsigned char> E2eSimulator::buildE2eDtRequestPayload(const e2edt_request_packet& requestContents) const
{
    auto request_pdu = Protocols::Coap::scoped_pdu_ptr::make_get_request(requestContents.token, requestContents.id);

    std::array<unsigned char, 1024> allOptions;

    size_t allOptionLength = allOptions.size();
    
    std::basic_string<unsigned char> path { requestContents.path.cbegin(), requestContents.path.cend() };

    auto options = coap_split_path(path.data(), path.size(), allOptions.data(), &allOptionLength);

    for( auto buf = allOptions.data(); options--; )
    {
        coap_add_option(request_pdu, COAP_OPTION_URI_PATH,
                coap_opt_length(buf),
                coap_opt_value(buf));

        buf += coap_opt_size(buf);
    }

    if( requestContents.block && requestContents.blockSize )
    {
        unsigned char buf[4];

        unsigned len = coap_encode_var_bytes(buf, (*requestContents.block << 4) | *requestContents.blockSize);

        coap_add_option(request_pdu, COAP_OPTION_BLOCK2, len, buf);
    }

    //  add data to reply
    coap_add_data(request_pdu, requestContents.payload.size(), requestContents.payload.data());

    const unsigned char *raw_reply_pdu = reinterpret_cast<unsigned char *>(request_pdu->hdr);

    return { raw_reply_pdu,
             raw_reply_pdu + request_pdu->length };
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


std::vector<unsigned char> E2eSimulator::buildRfnResponse(const e2edt_request_packet& request, const ApplicationServiceIdentifiers asid, const RfnIdentifier& rfnId)
{
    switch( request.method )
    {
        case Protocols::Coap::RequestMethod::Get:
        {
            e2edt_reply_packet replyPacket;

            replyPacket.id = request.id;
            replyPacket.token = request.token;
            replyPacket.payload = buildRfnGetResponse(request.payload, asid, rfnId);
            replyPacket.status = Protocols::Coap::ResponseCode::Content;

            return buildE2eDtReplyPayload(replyPacket);
        }
        case Protocols::Coap::RequestMethod::Post:
        {
            return buildRfnGetRequest(request, asid, rfnId);
        }
        default:
            CTILOG_INFO(dout, "Received unknown method (" << static_cast<int>(request.method) << ") for rfnIdentifier " << rfnId);
            return {};
    }
}

std::vector<unsigned char> E2eSimulator::buildRfnGetResponse(const std::vector<unsigned char> &request, const ApplicationServiceIdentifiers asid, const RfnIdentifier& rfnId)
{
    switch( asid )
    {
        case ApplicationServiceIdentifiers::ChannelManager:
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
            break;
        }
        case ApplicationServiceIdentifiers::HubMeterCommandSet:
        {
            if( ! request.empty() )
            {
                switch( request[0] )
                {
                    case 0x35:
                        return RfDa::Dnp3Address(request, rfnId);
                }
            }
            break;
        }
    }

    return {};
}

std::vector<unsigned char> E2eSimulator::buildRfnGetRequest(const e2edt_request_packet& post_request, const ApplicationServiceIdentifiers asid, const RfnIdentifier& rfnId)
{
    switch( asid )
    {
        case ApplicationServiceIdentifiers::ChannelManager:
        {
            if( ! post_request.payload.empty() )
            {
                switch( post_request.payload[0] )
                {
                    case 0x90:
                        e2edt_request_packet request;

                        request.id = post_request.id;
                        request.confirmable = true;
                        request.method = Protocols::Coap::RequestMethod::Get;
                        const auto meterProgramInfo = RfnMeter::RequestMeterProgram(post_request.payload, rfnId);
                        request.path = meterProgramInfo.path;
                        request.token = post_request.token;

                        return buildE2eDtRequestPayload(request);
                }
            }
            break;
        }
    }

    return {};
}

std::vector<unsigned char> E2eSimulator::buildDnp3Response(const std::vector<unsigned char>& request)
{
    Protocols::DnpSlaveProtocol prot;

    const std::vector<char> requestAsChar { request.begin(), request.end() };

    auto requestId = prot.identifyRequest(requestAsChar.data(), requestAsChar.size());

    std::vector<unsigned char> response;

    if( requestId.first == Protocols::DnpSlaveProtocol::Commands::Class1230Read )
    {
        std::vector<Protocols::DnpSlave::output_point> points;
        using PT = Protocols::DnpSlave::PointType;

        points.emplace_back(17,   true,  PT::AnalogInput,  331);
        points.emplace_back(170,  true,  PT::BinaryInput,  true);
        points.emplace_back(1700, true,  PT::Accumulator,  3310);
        points.emplace_back(34,   true,  PT::AnalogOutput, 662);
        points.emplace_back(35,   false, PT::AnalogOutput, 662);
        points.emplace_back(340,  true,  PT::BinaryOutput, true);
        points.emplace_back(341,  false, PT::BinaryOutput, true);

        prot.setScanCommand(std::move(points));
    }
    else if( requestId.first == Protocols::DnpSlaveProtocol::Commands::DelayMeasurement )
    {
        prot.setDelayMeasurementCommand( 0ms );
    }
    else
    {
        return response;
    }

    CtiXfer xfer;

    while( ! prot.isTransactionComplete() )
    {
        if( prot.generate(xfer) == ClientErrors::None )
        {
            if( xfer.getOutBuffer() != NULL && xfer.getOutCount() > 0 )
            {
                auto packet = arrayToRange(xfer.getOutBuffer(), xfer.getOutCount());

                CTILOG_INFO(dout, "Generated DNP3 packet:" << packet);

                response.insert(response.end(), packet.begin(), packet.end());
            }

            prot.decode(xfer);
        }
        else 
        {
            CTILOG_WARN(dout, "Was not able to generate scan response.");
        }
    }

    return response;
}

}