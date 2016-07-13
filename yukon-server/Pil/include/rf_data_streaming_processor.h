#pragma once

#include "dlldefs.h"
#include "rfn_e2e_messenger.h"
#include "PointAttribute.h"
#include "pointdefs.h"
#include "msg_pdata.h"

namespace Cti {
namespace Pil {

class IM_EX_CTIPIL RfDataStreamingProcessor
{
public:

    void tick();

    void start();

protected:

    struct Value
    {
        Attribute attribute;
        std::chrono::system_clock::time_point timestamp;
        double value;
        PointQuality_t quality;
    };

    struct DeviceReport
    {
        RfnIdentifier rfnId;
        std::vector<Value> values;
    };

    using Packet = Messaging::Rfn::E2eMessenger::Indication;

    static DeviceReport processPacket(const Packet& p);
    std::unique_ptr<CtiPointDataMsg> processDeviceReport(const DeviceReport &deviceReport);

private:

    using PacketQueue = std::vector<Packet>;

    void handleStatistics();

    using Mutex     = std::mutex;
    using LockGuard = std::lock_guard<std::mutex>;

    Mutex        _packetMux;
    PacketQueue  _packets;

    std::vector<std::unique_ptr<CtiPointDataMsg>> _results;
};

}
}
