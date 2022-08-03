#pragma once

#include "dlldefs.h"
#include "rfn_e2e_messenger.h"

class CtiDeviceManager;

namespace Cti::Pil
{

class IM_EX_CTIPIL RfDerProcessor
{
public:
    
    RfDerProcessor( CtiDeviceManager & deviceManager );

    void tick();

    void start();

protected:

    using Packet = Messaging::Rfn::E2eMessenger::Indication;

private:

    using PacketQueue = std::vector<Packet>;

    using Mutex     = std::mutex;
    using LockGuard = std::lock_guard<std::mutex>;

    Mutex        _packetMux;
    PacketQueue  _packets;

    CtiDeviceManager & _deviceManager;
};

}

