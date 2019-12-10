#pragma once

#include "rfn_identifier.h"

#include <set>
#include <map>
#include <chrono>

namespace Cti::Messaging::Pil {

    enum class ProgrammingStatus
    {
        Idle, 
        Uploading,
        Confirming,
        Failed,
        Initiating,
        Canceled,
        Mismatched
    };

    //  In the Porter namespace, since the serializer assumes the message source is Porter
    struct MeterProgramStatusArchiveRequestMsg
    {
        RfnIdentifier rfnIdentifier;
        std::string configurationId;
        ProgrammingStatus status;
        YukonError_t error;
        std::chrono::system_clock::time_point timeStamp;
    };
}