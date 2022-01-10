#include "precompiled.h"

#include "E2eSimulator.h"
#include "e2e_packet.h"

#include "RfnMeter.h"
#include "RfDa.h"

#include "message_factory.h"
#include "RfnE2eDataRequestMsg.h"
#include "RfnE2eDataConfirmMsg.h"
#include "RfnE2eDataIndicationMsg.h"
#include "RfnWaterNodeMessaging.h"
#include "NetworkManagerRequest.h"
#include "FieldSimulatorMsg.h"

#include "prot_e2eDataTransfer.h"
#include "coap_helper.h"

#include "rfn_asid.h"

#include "amq_util.h"
#include "amq_connection.h"
#include "amq_constants.h"
#include "amq_queues.h"
#include "CParms.h"
#include "utility.h"
#include "database_reader.h"
#include "sql_util.h"

extern "C" {
#include "coap/pdu.h"
#include "coap/block.h"
}

#include <proton/message.hpp>

#include <boost/algorithm/string/split.hpp>

#include <random>
#include <future>
#include <map>

using namespace Cti::Messaging::Qpid;
using namespace Cti::Messaging::Rfn;

using Cti::Messaging::ActiveMQConnectionManager;

using Cti::Messaging::Serialization::MessageFactory;
using Cti::Messaging::Serialization::MessagePtr;
using Cti::Messaging::Serialization::MessageSerializer;

using Cti::Logging::Vector::Hex::operator<<;
using Cti::Logging::Range::Hex::operator<<;

namespace Cti::Messaging::Rfn {
DLLIMPORT MessageFactory<E2eMsg> e2eMessageFactory;
DLLIMPORT MessageFactory<NetworkManagerBase> nmMessageFactory;
}

namespace {
const std::set<std::pair<std::string, std::string>> rfDaManufacturerModels {
    { "CPS", "CBC-8000" },
    { "CPS", "CBC-GEN" },
    { "CPS", "VR-CL7" },
    { "CPS", "VR-GEN" },
    { "CPS", "RECL-F4D" },
    { "CPS", "RECL-GEN" },
    { "CPS", "GEN-DA" },
    { "NON-CPS", "CBC-GEN" },
    { "NON-CPS", "VR-GEN" },
    { "NON-CPS", "RECL-GEN" },
    { "NON-CPS", "GEN-DA" },
};

bool isRfDa(const Cti::RfnIdentifier rfnIdentifier)
{
    return rfDaManufacturerModels.count({ rfnIdentifier.manufacturer,
                                          rfnIdentifier.model });
}

namespace Settings {

    std::string deviceGroup;
    unsigned deviceConfigFailureRate = 0;

    std::vector<std::string> getDeviceGroupSegments()
    {
        constexpr auto pathSeparator = '/';

        std::vector<std::string> result;

        if( ! deviceGroup.empty() && deviceGroup[0] == pathSeparator )
        {
            //  Exclude the root
            boost::split(result, deviceGroup.substr(1), Cti::is_char{ pathSeparator });
        }

        return result;
    }
}
}

namespace Cti::Simulator {

E2eSimulator::~E2eSimulator() = default;

E2eSimulator::E2eSimulator()
    : _sessionReady{ false }
{
    // initialize the session
    // jmoc - gotta fix this too
    //_session = Cti::Messaging::ActiveMQConnectionManager::getSession(*this);

    // waiting for the session to be completely initialized and open
    {
        std::unique_lock<std::mutex> lock(_sessionMutex);

        _sessionCv.wait_for(
            lock,
            std::chrono::seconds{ 30 },
            [this]()
            {
                return _sessionReady;
            });
    }

    // jmoc -- if timeout??
}

void E2eSimulator::on_session_open(proton::session& s)
{
    _session = s;

    e2eRequestConsumer = createQueueConsumer(s, Queues::OutboundQueue::NetworkManagerE2eDataRequest.name,
        [this](proton::message& m)
        {
            handleE2eDtRequest(m);
        });

    e2eConfirmProducer = createQueueProducer(s, Queues::InboundQueue::NetworkManagerE2eDataConfirm.name);
    e2eIndicationProducer = createQueueProducer(s, Queues::InboundQueue::NetworkManagerE2eDataIndication.name);

    batteryNodeGetRequestConsumer = createQueueConsumer(s, Queues::OutboundQueue::GetBatteryNodeChannelConfigRequest.name,
        [this](proton::message& m)
        {
            handleBatteryNodeGetChannelConfigRequest(m);
        });

    batteryNodeSetRequestConsumer = createQueueConsumer(s, Queues::OutboundQueue::SetBatteryNodeChannelConfigRequest.name,
        [this](proton::message& m)
        {
            handleBatteryNodeSetChannelConfigRequest(m);
        });

    Messaging::ActiveMQConnectionManager::registerReplyHandler(
        Queues::InboundQueue::FieldSimulatorStatusRequest,
        [this](const Messaging::ActiveMQConnectionManager::MessageDescriptor& md) {
            return handleStatusRequest(md);
        });

    Messaging::ActiveMQConnectionManager::registerReplyHandler(
        Queues::InboundQueue::FieldSimulatorModifyConfiguration,
        [this](const Messaging::ActiveMQConnectionManager::MessageDescriptor& md) {
            return handleConfigurationRequest(md);
        });

    CTILOG_INFO(dout, "E2EDT listener registered");

    {
        std::unique_lock<std::mutex> lock(_sessionMutex);

        _sessionReady = true;
    }
}


void E2eSimulator::stop()
{
    e2eRequestConsumer.reset();
    e2eIndicationProducer.reset();
    e2eConfirmProducer.reset();

    batteryNodeGetRequestConsumer.reset();
    batteryNodeSetRequestConsumer.reset();
}

std::mt19937_64 gen { static_cast<unsigned long long>(std::time(nullptr)) };
std::uniform_real_distribution<double> dist{ 0.0, 1.0 };

void E2eSimulator::handleBatteryNodeGetChannelConfigRequest(const proton::message& msg)
{
    using Messaging::Serialization::MessageSerializer;
    using namespace Messaging::Rfn;

    CTILOG_INFO(dout, "Received message on RF Battery Node Get Channel Configuration queue");

    Bytes payload{ proton::get<proton::binary>(msg.body()) };

    CTILOG_INFO(dout, "Received BytesMessage, attempting to decode as RfnGetChannelConfigRequestMessage");

    if (auto getMsg = MessageSerializer<RfnGetChannelConfigRequestMessage>::deserialize(payload))
    {
        CTILOG_INFO(dout, "Got new RfnGetChannelConfigRequestMessage for " << getMsg->rfnIdentifier);

        RfnGetChannelConfigReplyMessage replyMsg;

        replyMsg.rfnIdentifier = getMsg->rfnIdentifier;
        RfnGetChannelConfigReplyMessage::ChannelInfo channel{
            "gal",  //  UOM
            {},     //  Modifiers
            77,     //  Channel number
            true    //  Enabled
        };
        replyMsg.channelInfo.insert(channel);
        replyMsg.recordingInterval = 1800;
        replyMsg.replyCode = RfnGetChannelConfigReplyMessage::SUCCESS;
        replyMsg.reportingInterval = 86400;

        if (auto serializedReply = Messaging::Serialization::serialize(replyMsg);
            !serializedReply.empty())
        {
            auto tempQueueProducer = createDestinationProducer( _session, msg.reply_to() );

            proton::message m;

            m.to(msg.reply_to());
            m.body(proton::binary{ std::cbegin(serializedReply), std::cend(serializedReply) });

            tempQueueProducer->send(m);
        }
    }
}

void E2eSimulator::handleBatteryNodeSetChannelConfigRequest(const proton::message& msg)
{
    using Messaging::Serialization::MessageSerializer;
    using namespace Messaging::Rfn;

    CTILOG_INFO(dout, "Received message on RF Battery Node Set Channel Configuration queue");

    Bytes payload{ proton::get<proton::binary>(msg.body()) };

    CTILOG_INFO(dout, "Received BytesMessage, attempting to decode as RfnSetChannelConfigRequestMessage");

    if (auto setMsg = MessageSerializer<RfnSetChannelConfigRequestMessage>::deserialize(payload))
    {
        CTILOG_INFO(dout, "Got new RfnSetChannelConfigRequestMessage for " << setMsg->rfnIdentifier);

        RfnSetChannelConfigReplyMessage replyMsg{ 0 };  //  Success

        if (auto serializedReply = Messaging::Serialization::serialize(replyMsg);
            !serializedReply.empty())
        {
            auto tempQueueProducer = createDestinationProducer( _session, msg.reply_to());

            proton::message m;

            m.to(msg.reply_to());
            m.body(proton::binary{ std::cbegin(serializedReply), std::cend(serializedReply) });

            tempQueueProducer->send(m);
        }
    }
}

void E2eSimulator::handleE2eDtRequest(const proton::message& msg)
{
    CTILOG_INFO(dout, "Received message on RFN E2E queue");

    Bytes payload { proton::get<proton::binary>( msg.body() ) };

    CTILOG_INFO(dout, "Received BytesMessage on RFN E2E queue, attempting to decode as E2eDataRequest");

    auto e2eMsg = e2eMessageFactory.deserialize("com.eaton.eas.yukon.networkmanager.e2e.rfn.E2eDataRequest", payload);

    if (E2eDataRequestMsg* requestMsgPtr = dynamic_cast<E2eDataRequestMsg*>(e2eMsg.get()))
    {
        std::unique_ptr<E2eDataRequestMsg> requestMsg{ requestMsgPtr };
        e2eMsg.release();

        CTILOG_INFO(dout, "Got new RfnE2eDataRequestMsg" << requestMsg->payload);

        if (requestMsg->payload.size() > COAP_MAX_PDU_SIZE)
        {
            CTILOG_INFO(dout, "Payload too large: " << requestMsg->payload.size());
            return;
        }

        //  NM timeout
        if (dist(gen) < gConfigParms.getValueAsDouble("SIMULATOR_RFN_NM_TIMEOUT_CHANCE"))
        {
            CTILOG_INFO(dout, "Not sending NM acknowledgement for " << requestMsg->rfnIdentifier);
            return;
        }

        sendNetworkManagerRequestAck(requestMsg->header, msg.reply_to());

        //  NM queue timeout
        if (dist(gen) < gConfigParms.getValueAsDouble("SIMULATOR_RFN_NM_QUEUE_TIMEOUT_CHANCE"))
        {
            CTILOG_INFO(dout, "Not sending E2E confirm for " << requestMsg->rfnIdentifier);
            return;
        }

        float delay = dist(gen) * gConfigParms.getValueAsDouble("SIMULATOR_RFN_NM_QUEUE_DELAY_SECONDS", 3);

        std::thread{ &E2eSimulator::delayProcessing, this, delay, *requestMsg }.detach();
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


double getDeviceFailureChance(RfnIdentifier rfnIdentifier)
{
    const auto groupSegments = Settings::getDeviceGroupSegments();

    std::string sql = Database::getRfnAddressInDeviceGroupSql(groupSegments.size());

    Cti::Database::DatabaseConnection connection;
    Cti::Database::DatabaseReader rdr(connection, sql);

    for( const auto& groupSegment : groupSegments )
    {
        rdr << groupSegment;
    }

    rdr << rfnIdentifier.manufacturer;
    rdr << rfnIdentifier.model;
    rdr << rfnIdentifier.serialNumber;

    rdr.execute();

    if( rdr() && rdr[0].as<int>() > 0 )
    {
        return Settings::deviceConfigFailureRate / 100.0;
    }

    return 0;
}



void E2eSimulator::processE2eDtRequest(const E2eDataRequestMsg requestMsg)
{
    const auto requestSender = [this, &requestMsg](const e2edt_request_packet& request) {
        if( auto serializedE2eRequest = buildE2eDtRequest(request);
            ! serializedE2eRequest.empty() )
        {
            sendE2eDataIndication(requestMsg, serializedE2eRequest);
        }
    };
    const auto replySender = [this, &requestMsg](const e2edt_reply_packet& reply) {
        if( auto serializedE2eReply = buildE2eDtReply(reply);
            ! serializedE2eReply.empty() )
        {
            sendE2eDataIndication(requestMsg, serializedE2eReply);
        }
    };

    if( isAsid_E2eDt(requestMsg.applicationServiceId) )
    {
        auto msgPtr = parseE2eDtRequestPayload(requestMsg.payload, requestMsg.rfnIdentifier);
                        
        if( auto e2edtRequest = dynamic_cast<const e2edt_request_packet*>(msgPtr.get()) )
        {
            auto failureChance = dist(gen);

            //  Request Unacceptable
            if( failureChance < gConfigParms.getValueAsDouble("SIMULATOR_RFN_E2E_REQUEST_NOT_ACCEPTABLE_CHANCE")
                || failureChance < getDeviceFailureChance(requestMsg.rfnIdentifier) )
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

            if( isRfDa(requestMsg.rfnIdentifier) )
            {
                RfDa::processRequest({ replySender }, *e2edtRequest, requestMsg.rfnIdentifier, requestMsg.applicationServiceId);
            }
            else
            {
                RfnMeter::processRequest({ requestSender }, { replySender }, *e2edtRequest, requestMsg.rfnIdentifier, requestMsg.applicationServiceId);
            }
        }
        else if( auto e2edtReply = dynamic_cast<const e2edt_reply_packet*>(msgPtr.get()) )
        {
            if( isRfDa(requestMsg.rfnIdentifier) )
            {
                CTILOG_INFO(dout, "Reply packet received for RfDa device type " << requestMsg.rfnIdentifier << ", discarding");
            }
            else
            {
                RfnMeter::processReply({ requestSender }, *e2edtReply, requestMsg.rfnIdentifier, requestMsg.applicationServiceId);
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


void E2eSimulator::sendNetworkManagerRequestAck(const NetworkManagerRequestHeader &header, const std::string & destination)
{
    NetworkManagerRequestAck requestAck;

    requestAck.header = header;

    Bytes requestAckBytes;

    nmMessageFactory.serialize(requestAck, requestAckBytes);

    auto tempQueueProducer = createDestinationProducer( _session, destination);

    proton::message m;

    m.to(destination);
    m.body(proton::binary{ std::cbegin(requestAckBytes), std::cend(requestAckBytes) });

    tempQueueProducer->send(m);
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

    proton::message m;

   // m.to(destination);    // jmoc -- do we need this?  and what is the actual string here...?  maybe we should be able to get from the producer...
    m.body(proton::binary{ std::cbegin(confirmBytes), std::cend(confirmBytes) });

    e2eConfirmProducer->send(m);
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
        if( auto blockSize = Protocols::Coap::BlockSize::ofSzx(block.szx) )
        {
            packet->block = { block.num, !!block.m, *blockSize };
        }
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

    if( ! request.path.empty() )
    {
        auto itr = request.path.cbegin();

        if( *itr == '/' )
        {
            //  Trim off any leading slash - it's implicit
            ++itr;
        }
    
        const std::vector<unsigned char> path { itr, request.path.cend() };

        auto options = coap_split_path(path.data(), path.size(), allOptions.data(), &allOptionLength);

        for( auto buf = allOptions.data(); options--; buf += coap_opt_size(buf) )
        {
            coap_add_option(request_pdu, COAP_OPTION_URI_PATH,
                coap_opt_length(buf),
                coap_opt_value(buf));
        }
    }

    if( request.block )
    {
        unsigned char buf[4];

        unsigned len = coap_encode_var_bytes(buf, (request.block->num << 4) | request.block->blockSize.getSzx());

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

    CTILOG_INFO(dout, "Sending E2eDataIndicationMsg to " << requestMsg.rfnIdentifier << "\n" << payload);

    e2eMessageFactory.serialize(indication, indicationBytes);

    proton::message m;

    // m.to(destination);    // jmoc -- do we need this?  and what is the actual string here...?  maybe we should be able to get from the producer...
    m.body(proton::binary{ std::cbegin(indicationBytes), std::cend(indicationBytes) });

    e2eIndicationProducer->send(m);
}

auto E2eSimulator::handleStatusRequest(const ActiveMQConnectionManager::MessageDescriptor& md)
        -> std::unique_ptr<ActiveMQConnectionManager::SerializedMessage>
{
    CTILOG_INFO(dout, "Received message on Status queue, attempting to decode as FieldSimulatorStatusRequest");

    auto request = MessageSerializer<Messaging::FieldSimulator::StatusRequestMsg>::deserialize(md.msg); 

    if( ! request )
    {
        return nullptr;
    }

    Messaging::FieldSimulator::StatusResponseMsg response;

    response.settings.deviceConfigFailureRate = 
        Settings::deviceConfigFailureRate;
    response.settings.deviceGroup = 
        Settings::deviceGroup;

    return std::make_unique<ActiveMQConnectionManager::SerializedMessage>(
        Messaging::Serialization::serialize(response));
}

auto E2eSimulator::handleConfigurationRequest(const ActiveMQConnectionManager::MessageDescriptor& md)
        -> std::unique_ptr<ActiveMQConnectionManager::SerializedMessage>
{
    CTILOG_INFO(dout, "Received message on Configuration queue");

    auto request = MessageSerializer<Messaging::FieldSimulator::ModifyConfigurationRequestMsg>::deserialize(md.msg);

    if( ! request )
    {
        return nullptr;
    }

    Settings::deviceConfigFailureRate = 
        request->settings.deviceConfigFailureRate;
    Settings::deviceGroup = 
        request->settings.deviceGroup;
    
    Messaging::FieldSimulator::ModifyConfigurationResponseMsg response;

    response.success = true;
    response.settings = request->settings;

    return std::make_unique<ActiveMQConnectionManager::SerializedMessage>(
        Messaging::Serialization::serialize(response));
}

}
