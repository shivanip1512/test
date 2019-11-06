#pragma once

#include "dlldefs.h"
#include "rfn_e2e_messenger.h"
#include "Attribute.h"
#include "pointdefs.h"
#include "msg_pdata.h"

class CtiDeviceManager;
class CtiPointManager;

namespace Cti {
namespace Pil {

class IM_EX_CTIPIL RfDataStreamingProcessor
{
public:
    
    using ResultVector = std::vector<std::unique_ptr<CtiPointDataMsg>>;

    RfDataStreamingProcessor( CtiDeviceManager& deviceManager, CtiPointManager& pointManager );

    ResultVector tick();

    void start();

protected:

    struct Value
    {
        uint16_t metricId;
        std::chrono::system_clock::time_point timestamp;
        double value;
        PointQuality_t quality;
    };

    friend Cti::StreamBufferSink& operator<<(Cti::StreamBufferSink& os, const Value& v);

    struct DeviceReport
    {
        RfnIdentifier rfnId;
        std::vector<Value> values;
    };

    friend Cti::StreamBufferSink& operator<<(Cti::StreamBufferSink& os, const DeviceReport& dr);

    using Packet = Messaging::Rfn::E2eMessenger::Indication;

    static DeviceReport processPacket(const Packet& p);
    ResultVector processDeviceReport(const DeviceReport &deviceReport);

private:

    using PacketQueue = std::vector<Packet>;

    void handleStatistics();

    using Mutex     = std::mutex;
    using LockGuard = std::lock_guard<std::mutex>;

    Mutex        _packetMux;
    PacketQueue  _packets;

    ResultVector _results;

    CtiDeviceManager & _deviceManager;
    CtiPointManager  & _pointManager;
};

}
}
