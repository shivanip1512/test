#pragma once

#include "dlldefs.h"
#include "yukon.h"  //  for YukonError_t
#include "rfn_identifier.h"

#include <boost/optional.hpp>
#include <boost/random.hpp>

#include <vector>
#include <map>

namespace Cti::Protocols {

//  Also known as E2EDT.
//  System spec:   http://portal.cooperpowereas.net/sites/Ops/MarylandEngineeringGroup/Shared%20Documents/System/10446%20E2E%20Data%20Transfer%20System%20Specification_R1.2.docx
//  Firmware spec: http://portal.cooperpowereas.net/sites/Ops/MarylandEngineeringGroup/Shared%20Documents/PSGT-Firmware/Features/E2EDT/Design/E2E%20Data%20Transfer%20-%20Software%20Design%20Document.docx
//
//  This class is designed to manage a single device's E2EDT state.
class IM_EX_PROT E2eDataTransferProtocol : private boost::noncopyable
{
public:

    using Bytes = std::vector<unsigned char>;

    struct BlockSize
    {
        unsigned szx;

        size_t getSize() const { return 16 << szx; }
    };

    struct Block
    {
        unsigned num;
        bool more;
        BlockSize size;

        size_t start() const { return size.getSize() * (num + 0); }
        size_t end() const   { return size.getSize() * (num + 1); }
    };

    struct EndpointMessage
    {
        bool nodeOriginated;

        unsigned short id;

        int code;  //  Either a ResponseCode or a RequestMethod.

        unsigned long token;

        std::string path;

        Bytes data;

        bool confirmable;

        std::optional<Block> block;
    };

    struct E2eException : std::exception
    {
        const std::string reason;

        E2eException(std::string reason_) : reason(reason_) {}

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
    struct UnknownRequestMethod : E2eException
    {
        UnknownRequestMethod(unsigned short id, int method) :
            E2eException("Unknown request method " + std::to_string(method) + " for packet id " + std::to_string(id))
        {}
    };
    struct ResetReceived   : E2eException { ResetReceived()                      : E2eException("Reset packet received") {} };
    struct PayloadTooLarge : E2eException { PayloadTooLarge()                    : E2eException("Payload too large")     {} };
    struct DuplicatePacket : E2eException { DuplicatePacket(int id)              : E2eException("Duplicate packet, id: " + std::to_string(id)) {} };
    struct RequestInactive : E2eException { RequestInactive(unsigned long token) : E2eException("Response received for inactive token " + std::to_string(token)) {} };

    enum
    {
        MaxOutboundPayload = 1000
    };

    E2eDataTransferProtocol();

    //  throws PayloadTooLarge
    Bytes sendRequest(const Bytes &payload, const RfnIdentifier endpointId, const unsigned long token);

    //  throws PayloadTooLarge
    Bytes sendPost   (const Bytes &payload, const RfnIdentifier endpointId, const unsigned long token);

    //  throws PayloadTooLarge
    Bytes sendReply  (const Bytes &payload, const RfnIdentifier endpointId, const unsigned long token);

    //  throws PayloadTooLarge
    Bytes sendBlockReply(const Bytes &payload, const RfnIdentifier endpointId, const unsigned long token, const Block block);

    Bytes sendAck(const unsigned short id);
    Bytes sendBadRequest(const unsigned short id);

    Bytes sendBlockContinuation(const BlockSize size, const unsigned num, const RfnIdentifier endpointId, const unsigned long token);

    //  throws E2eException
    EndpointMessage handleIndication(const Bytes &payload, const RfnIdentifier endpointId);
    static YukonError_t translateIndicationCode(const unsigned short code, const RfnIdentifier endpointId);

    void handleTimeout(const RfnIdentifier endpointId);

protected:

    virtual unsigned short getOutboundId();

private:

    struct RequestId
    {
        unsigned short id;
        bool active;
    };

    using EndpointIds = std::map<RfnIdentifier, unsigned short>;
    using RequestIds = std::map<RfnIdentifier, RequestId>;

    RequestIds  _outboundIds;
    EndpointIds _inboundIds;

    boost::random::mt19937 _generator;

    unsigned short getOutboundIdForEndpoint(const RfnIdentifier endpointId);
};

}