#pragma once

#include "dlldefs.h"
#include "dev_rfn.h"
#include "rfn_asid.h"
#include "rfn_e2e_messenger.h"

#include <boost/ptr_container/ptr_deque.hpp>

namespace Cti {
namespace Pil {

class IM_EX_CTIPIL RfDataStreamingProcessor
{
public:

	using ResultVector = std::vector<std::unique_ptr<CtiMultiMsg>>;

	ResultVector tick();

    void start();

private:

	using Packet = Messaging::Rfn::E2eMessenger::Indication;
	using PacketQueue = std::vector<Packet>;

	std::unique_ptr<CtiMultiMsg> processPacket(const Packet& p);
    void handleStatistics();

    using Mutex     = std::mutex;
    using LockGuard = std::lock_guard<std::mutex>;

    Mutex        _packetMux;
    PacketQueue  _packets;

    ResultVector _results;
};

}
}
