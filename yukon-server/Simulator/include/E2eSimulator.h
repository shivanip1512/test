#pragma once

#include "rfn_asid.h"

namespace cms {
class Session;
class Message;
class Destination;
}

namespace Cti {
    struct RfnIdentifier;
}
namespace Cti::Messaging::ActiveMQ {
    class ManagedConnection;
    class ManagedConsumer;
    class MessageListener;
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

class E2eSimulator
{
public:

    E2eSimulator();
    ~E2eSimulator();  //  defer unique_ptr deletion 

    void stop();

private:

    using Bytes = std::vector<unsigned char>;

    std::unique_ptr<Messaging::ActiveMQ::ManagedConnection> conn;

    std::unique_ptr<cms::Session> consumerSession;
    std::unique_ptr<cms::Session> producerSession;

    std::unique_ptr<Messaging::ActiveMQ::ManagedConsumer> requestConsumer;
    std::unique_ptr<Messaging::ActiveMQ::MessageListener> requestListener;
                    
    std::unique_ptr<Messaging::ActiveMQ::ManagedProducer> confirmProducer;
    std::unique_ptr<Messaging::ActiveMQ::ManagedProducer> indicationProducer;

    void handleE2eDtRequest(const cms::Message *msg);
    void delayProcessing(float delay, const Messaging::Rfn::E2eDataRequestMsg requestMsg);

    void sendNetworkManagerRequestAck(const Messaging::Rfn::NetworkManagerRequestHeader&, const cms::Destination*);
    void sendE2eDataConfirm(const Messaging::Rfn::E2eDataRequestMsg&);

    std::unique_ptr<e2edt_packet> parseE2eDtRequestPayload(const Bytes&, const RfnIdentifier&);

    Bytes buildE2eDtReply(const e2edt_reply_packet&) const;
    Bytes buildE2eDtRequest(const e2edt_request_packet&) const;
    Bytes buildE2eRequestNotAcceptable(unsigned id, unsigned long token) const;

    void sendE2eDataIndication(const Messaging::Rfn::E2eDataRequestMsg &, const Bytes&);

    Bytes buildResponse(const e2edt_request_packet& request, const Messaging::Rfn::ApplicationServiceIdentifiers asid, const RfnIdentifier& rfnId);
    Bytes processRfnPostRequest(const e2edt_request_packet& request, const Messaging::Rfn::ApplicationServiceIdentifiers asid, const RfnIdentifier& rfnId);
};

}