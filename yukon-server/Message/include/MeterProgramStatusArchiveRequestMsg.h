#pragma once

#include "rfn_identifier.h"

#include <chrono>

namespace Cti::Messaging::Pil {

    enum class IM_EX_MSG ProgrammingStatus
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
    struct IM_EX_MSG MeterProgramStatusArchiveRequestMsg
    {
        RfnIdentifier rfnIdentifier;
        std::string configurationId;
        ProgrammingStatus status;
        YukonError_t error;
        std::chrono::system_clock::time_point timeStamp;
    };

    IM_EX_MSG void sendMeterProgramStatusUpdate(const MeterProgramStatusArchiveRequestMsg & msg);
}
