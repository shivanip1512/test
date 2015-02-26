#pragma once

#include "dlldefs.h"

#include <boost/optional.hpp>
#include <boost/random.hpp>

#include <vector>
#include <map>

namespace Cti {
namespace Protocols {

//  Also known as E2EDT.
//  System spec:   http://portal.cooperpowereas.net/sites/Ops/MarylandEngineeringGroup/Shared%20Documents/System/10446%20E2E%20Data%20Transfer%20System%20Specification_R1.2.docx
//  Firmware spec: http://portal.cooperpowereas.net/sites/Ops/MarylandEngineeringGroup/Shared%20Documents/PSGT-Firmware/Features/E2EDT/Design/E2E%20Data%20Transfer%20-%20Software%20Design%20Document.docx
//
//  This class is designed to manage a single device's E2EDT state.
class IM_EX_PROT E2eDataTransferProtocol : private boost::noncopyable
{
public:

    struct EndpointResponse
    {
        unsigned long token;

        std::vector<unsigned char> data;  //  bytes payload

        std::vector<unsigned char> ack;
        std::vector<unsigned char> blockContinuation;
    };

    struct E2eException : std::exception
    {
        const std::string reason;

        E2eException(std::string &&reason_) : reason(reason_) {}

        const char * what() const override
        {
            return reason.c_str();
        }
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
    struct ResetReceived        : E2eException { ResetReceived()         : E2eException("Reset packet received")     {} };
    struct PayloadTooLarge      : E2eException { PayloadTooLarge()       : E2eException("Payload too large")         {} };
    struct RequestNotAcceptable : E2eException { RequestNotAcceptable()  : E2eException("Request not acceptable")    {} };
    struct BadRequest           : E2eException { BadRequest(int code)    : E2eException("Bad request: " + std::to_string(code)) {} };
    struct DuplicatePacket      : E2eException { DuplicatePacket(int id) : E2eException("Duplicate packet, id: " + std::to_string(id)) {} };

    enum
    {
        MaxOutboundPayload = 1000
    };

    E2eDataTransferProtocol();

    //  throws PayloadTooLarge
    std::vector<unsigned char> sendRequest(const std::vector<unsigned char> &payload, const long endpointId, const unsigned long token);

    //  throws E2eException
    EndpointResponse handleIndication(const std::vector<unsigned char> &payload, const long endpointId);

    void handleTimeout(const long endpointId);

protected:

    virtual unsigned short getOutboundId();

private:

    std::map<long, unsigned short> _outboundIds;
    std::map<long, unsigned short> _inboundIds;

    boost::random::mt19937 _generator;

    unsigned short getOutboundIdForEndpoint(long endpointId);
    std::vector<unsigned char> sendBlockContinuation(const unsigned size, const unsigned num, const long endpointId, const unsigned long token);
    std::vector<unsigned char> sendAck(const unsigned short id);
};

}
}
