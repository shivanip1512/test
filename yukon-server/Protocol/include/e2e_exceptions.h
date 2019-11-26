#pragma once

#include <string>

namespace Cti::Protocols::E2e {

    struct E2eException : std::runtime_error
    {
        using std::runtime_error::runtime_error;
    };
    struct UnexpectedAck : E2eException
    {
        UnexpectedAck(unsigned short unexpected, unsigned short expected) :
            E2eException("Unexpected ACK: " + std::to_string(unexpected) + ", expected " + std::to_string(expected))
        {}
        UnexpectedAck(unsigned short unexpected) :
            E2eException("Unexpected ACK: " + std::to_string(unexpected) + ", no outbounds recorded")
        {}
    };
    struct UnknownRequestMethod : E2eException
    {
        UnknownRequestMethod(unsigned short id, int method) :
            E2eException("Unknown request method " + std::to_string(method) + " for packet id " + std::to_string(id))
        {}
    };
    struct ResetReceived    : E2eException { ResetReceived()                      : E2eException("Reset packet received") {} };
    struct PayloadTooLarge  : E2eException { PayloadTooLarge()                    : E2eException("Payload too large")     {} };
    struct DuplicatePacket  : E2eException { DuplicatePacket(int id)              : E2eException("Duplicate packet, id: " + std::to_string(id)) {} };
    struct RequestInactive  : E2eException { RequestInactive(unsigned long token) : E2eException("Response received for inactive token " + std::to_string(token)) {} };
    struct InvalidBlockSize : E2eException { InvalidBlockSize(uint8_t szx)        : E2eException("Invalid block size: " + std::to_string(szx)) {} };
}
