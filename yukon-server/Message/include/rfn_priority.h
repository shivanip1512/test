#pragma once

namespace Cti::Messaging::Rfn {

enum class PriorityClass : bool
{
    //  Underlying high/low network priority for the given priority class type
    MeterRead = true,
    MeterDisconnect = true,
    DeviceConfiguration = false,
    MeterProgramming = false,
    RfDa = true
};

}