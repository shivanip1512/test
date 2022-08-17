#pragma once

#include "streamConnection.h"
#include "amq_connection.h"
#include "dlldefs.h"

#include <queue>
#include <mutex>

namespace Cti {

template <class Outbound, class Inbound>
class IM_EX_MSG StreamAmqConnection : public StreamConnection
{
public:
    StreamAmqConnection(const Messaging::ActiveMQ::Queues::OutboundQueue &outbound, const Messaging::ActiveMQ::Queues::InboundQueue &inbound);

    bool write(const Outbound &out, const Chrono &timeout);
    std::unique_ptr<Inbound> read(const Chrono &timeout, const HANDLE *hAbort);

private:
    bool isValid () const override;

    size_t write   (const void *buf, int len, const Chrono& timeout)                 override;
    size_t read    (void *buf, int len, const Chrono& timeout, const HANDLE *hAbort) override;
    size_t peek    (void *buf, int len)                                              override;

    void onMessage(const Messaging::ActiveMQConnectionManager::SerializedMessage &);

    const Messaging::ActiveMQ::Queues::OutboundQueue &_outbound;

    using Inbounds = std::queue<Messaging::ActiveMQConnectionManager::SerializedMessage>;
    Inbounds   _inbound;
    std::mutex _inboundMux;
    HANDLE     _inboundEvent;
};

} // namespace Cti

