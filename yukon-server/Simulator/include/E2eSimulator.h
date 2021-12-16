#pragma once

#include "amq_connection.h"

#include <proton/session.hpp>
#include <proton/message.hpp>
#include <proton/messaging_handler.hpp>

namespace Cti {
    struct RfnIdentifier;
}
namespace Cti::Messaging::Qpid {
    class ManagedConsumer;
    class ManagedProducer;
}
namespace Cti::Messaging::Rfn {
    class NetworkManagerRequestHeader;
    class E2eDataRequestMsg;
}

namespace Cti::Protocols::Coap {
    enum class RequestMethod;
}

namespace Cti::Simulator {

struct e2edt_packet;
struct e2edt_request_packet;
struct e2edt_reply_packet;

class E2eSimulator : public proton::messaging_handler
{
public:

    E2eSimulator();
    ~E2eSimulator();  //  defer unique_ptr deletion 

    void stop();

    void on_session_open(proton::session& s) override;

private:

    proton::session _session;

    std::mutex _sessionMutex;
    std::condition_variable _sessionCv;
    bool _sessionReady;

    using Bytes = std::vector<unsigned char>;

    std::unique_ptr<Messaging::Qpid::ManagedConsumer> batteryNodeGetRequestConsumer;
    std::unique_ptr<Messaging::Qpid::ManagedConsumer> batteryNodeSetRequestConsumer;

    std::unique_ptr<Messaging::Qpid::ManagedConsumer> e2eRequestConsumer;
    std::unique_ptr<Messaging::Qpid::ManagedProducer> e2eConfirmProducer;
    std::unique_ptr<Messaging::Qpid::ManagedProducer> e2eIndicationProducer;

    void handleBatteryNodeGetChannelConfigRequest(const proton::message& msg);
    void handleBatteryNodeSetChannelConfigRequest(const proton::message& msg);
    void handleE2eDtRequest(const proton::message& msg);

    void delayProcessing(float delay, const Messaging::Rfn::E2eDataRequestMsg requestMsg);
    void processE2eDtRequest(const Messaging::Rfn::E2eDataRequestMsg requestMsg);

    void sendNetworkManagerRequestAck(const Messaging::Rfn::NetworkManagerRequestHeader&, const std::string & destination);
    void sendE2eDataConfirm(const Messaging::Rfn::E2eDataRequestMsg&);

    std::unique_ptr<e2edt_packet> parseE2eDtRequestPayload(const Bytes&, const RfnIdentifier&);

    Bytes buildE2eDtReply(const e2edt_reply_packet&) const;
    Bytes buildE2eDtRequest(const e2edt_request_packet&) const;
    Bytes buildE2eRequestNotAcceptable(unsigned id, unsigned long token) const;

    void sendE2eDataIndication(const Messaging::Rfn::E2eDataRequestMsg &, const Bytes&);

    auto handleStatusRequest(const Messaging::ActiveMQConnectionManager::MessageDescriptor& md)
            -> std::unique_ptr<Messaging::ActiveMQConnectionManager::SerializedMessage>;
    auto handleConfigurationRequest(const Messaging::ActiveMQConnectionManager::MessageDescriptor& md)
            -> std::unique_ptr<Messaging::ActiveMQConnectionManager::SerializedMessage>;
};

}