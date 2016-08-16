#pragma once

namespace cms {
class Session;
class Message;
class Destination;
}

namespace Cti {
    struct RfnIdentifier;

namespace Messaging {
namespace ActiveMQ {
    class ManagedConnection;
    class ManagedConsumer;
    class MessageListener;
    class ManagedProducer;
}
namespace Rfn {
    class NetworkManagerRequestHeader;
    class E2eDataRequestMsg;
}
}

namespace Simulator {

class E2eSimulator
{
public:

    E2eSimulator();
    ~E2eSimulator();  //  defer unique_tr deletion 

    void stop();

private:

    std::unique_ptr<Messaging::ActiveMQ::ManagedConnection> conn;

    std::unique_ptr<cms::Session> consumerSession;
    std::unique_ptr<cms::Session> producerSession;

    std::unique_ptr<Messaging::ActiveMQ::ManagedConsumer> requestConsumer;
    std::unique_ptr<Messaging::ActiveMQ::MessageListener> requestListener;
                    
    std::unique_ptr<Messaging::ActiveMQ::ManagedProducer> confirmProducer;
    std::unique_ptr<Messaging::ActiveMQ::ManagedProducer> indicationProducer;

    void handleE2eDtRequest(const cms::Message *msg);

    void sendNetworkManagerRequestAck(const Messaging::Rfn::NetworkManagerRequestHeader&, const cms::Destination*);
    void sendE2eDataConfirm(const Messaging::Rfn::E2eDataRequestMsg&);

    struct e2edt_packet
    {
        std::vector<unsigned char> payload;
        unsigned token;
        unsigned id;
        unsigned char status;
    };

    std::unique_ptr<e2edt_packet> parseE2eDtRequestPayload(const std::vector<unsigned char>&, const RfnIdentifier&);

    std::vector<unsigned char> buildE2eDtReplyPayload(const e2edt_packet&);

    void sendE2eDataIndication(const Messaging::Rfn::E2eDataRequestMsg &, const std::vector<unsigned char>&);

    std::vector<unsigned char> buildRfnResponse(const std::vector<unsigned char>& request, const unsigned char applicationServiceId, const RfnIdentifier& rfnId);
};

}
}