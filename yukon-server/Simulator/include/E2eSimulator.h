#pragma once

namespace cms {
class Session;
class Message;
}

namespace Cti {
namespace Messaging {
namespace ActiveMQ {
    class ManagedConnection;
    class ManagedConsumer;
    class MessageListener;
    class ManagedProducer;
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
};

}
}