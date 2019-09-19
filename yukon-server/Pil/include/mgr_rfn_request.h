#pragma once

#include "dlldefs.h"
#include "prot_e2eDataTransfer.h"
#include "dev_rfn.h"
#include "cmd_rfn.h"
#include "rfn_asid.h"
#include "rfn_e2e_messenger.h"

#include <boost/ptr_container/ptr_deque.hpp>

namespace Cti::Pil {

struct RfnDeviceRequest
{
    struct Parameters
    {
        RfnIdentifier rfnIdentifier;
        long deviceId;
        std::string commandString;
        int priority;
        long userMessageId;
        long groupMessageId;
        ConnectionHandle connectionHandle;
    };

    RfnDeviceRequest(Parameters parameters_, unsigned long rfnRequestId_, Devices::Commands::RfnCommandPtr command_)
        :   parameters(parameters_),
            rfnRequestId(rfnRequestId_),
            command(std::move(command_))
    {}

    RfnDeviceRequest(RfnDeviceRequest&&) = default;

    RfnDeviceRequest& operator=(RfnDeviceRequest&&) = default;

    Parameters parameters;
    unsigned long rfnRequestId;
    Devices::Commands::RfnCommandPtr command;

    bool operator<(const RfnDeviceRequest &rhs) const
    {
        return (parameters.priority < rhs.parameters.priority) ||
              !(rhs.parameters.priority < parameters.priority) && (rhs.rfnRequestId - rfnRequestId) < 0x8000'0000;  //  handle unsigned wraparound
    }
};

struct RfnDeviceResult
{
    RfnDeviceResult(RfnDeviceRequest request_, Devices::Commands::RfnCommandResultList commandResults_) :
        request(std::move(request_)),
        commandResults(commandResults_)
    {
    }

    RfnDeviceResult(RfnDeviceResult&&) = default;

    RfnDeviceResult& operator=(RfnDeviceResult&&) = default;

    RfnDeviceRequest request;
    Devices::Commands::RfnCommandResultList commandResults;
};

class IM_EX_CTIPIL RfnRequestManager
{
public:

    using ConfigNotificationPtr = std::unique_ptr<Devices::Commands::RfnConfigNotificationCommand>;

    struct UnsolicitedReport
    {
        UnsolicitedReport( RfnIdentifier rfnId_,  ConfigNotificationPtr command_)
            :   rfnId(rfnId_),
                command(std::move(command_))
        { }

        RfnIdentifier rfnId;
        ConfigNotificationPtr command;
    };

    using ResultQueue          = std::deque<RfnDeviceResult>;
    using RfnDeviceRequestList = std::vector<RfnDeviceRequest>;
    using UnsolicitedReports   = std::vector<UnsolicitedReport>;

    void tick();

    void submitRequests(RfnDeviceRequestList requests);

    ResultQueue getResults(unsigned max);
    UnsolicitedReports getUnsolicitedReports();

    void  cancelByGroupMessageId(long groupMessageId);
    size_t countByGroupMessageId(long groupMessageId);

    void start();

protected:

    virtual Protocols::E2eDataTransferProtocol::EndpointMessage handleE2eDtIndication(const std::vector<unsigned char> &payload, const RfnIdentifier endpointId);
    virtual std::vector<unsigned char> sendE2eDtRequest(const std::vector<unsigned char> &payload, const RfnIdentifier endpointId, const unsigned long token);
    virtual std::vector<unsigned char> sendE2eDtReply  (const std::vector<unsigned char> &payload, const RfnIdentifier endpointId, const unsigned long token);

private:

    using RfnIdentifierSet = std::set<RfnIdentifier>;

    RfnIdentifierSet handleConfirms();
    RfnIdentifierSet handleIndications();
    RfnIdentifierSet handleTimeouts();
    void             handleNewRequests(const RfnIdentifierSet &recentCompletions);
    void             postResults();
    void             reportStatistics();

    typedef Messaging::Rfn::ApplicationServiceIdentifiers ApplicationServiceIdentifiers;

    Protocols::E2eDataTransferProtocol _e2edt;

    struct PacketInfo
    {
        std::vector<unsigned char> payloadSent;
        CtiTime  timeSent;
        unsigned retransmissionDelay;
        unsigned retransmits;
        unsigned maxRetransmits;
    };

    PacketInfo sendE2eDataRequestPacket(const std::vector<unsigned char> &e2ePacket, const ApplicationServiceIdentifiers &asid, const RfnIdentifier &rfnIdentifier, const unsigned priority, const long groupMessageId, const CtiTime timeout);
    void sendE2eDataAck(const std::vector<unsigned char> &e2eAck, const ApplicationServiceIdentifiers &asid,  const RfnIdentifier &rfnIdentifier);

    void checkForNewRequest(const RfnIdentifier &rfnId);

    using IndicationQueue  = std::vector<Messaging::Rfn::E2eMessenger::Indication>;
    using ConfirmQueue     = std::vector<Messaging::Rfn::E2eMessenger::Confirm>;
    using ExpirationCauses = std::map<RfnIdentifier, YukonError_t>;
    using RequestHeap      = std::vector<RfnDeviceRequest>;
    using RfnIdToRequestHeap = std::map<RfnIdentifier, RequestHeap>;

    using Mutex     = std::mutex;
    using LockGuard = std::lock_guard<std::mutex>;

    Mutex                _indicationMux;
    IndicationQueue      _indications;

    Mutex                _confirmMux;
    ConfirmQueue         _confirms;

    Mutex                _expirationMux;
    ExpirationCauses     _expirations;

    Mutex                _submittedRequestsMux;
    RfnDeviceRequestList _submittedRequests;

    Mutex                _resultsMux;
    ResultQueue          _results;
    ResultQueue          _resultsPerTick;
    UnsolicitedReports   _unsolicitedReports;
    UnsolicitedReports   _unsolicitedReportsPerTick;

    Mutex                _pendingRequestsMux;
    RfnIdToRequestHeap   _pendingRequests;

    struct RfnRequestIdentifier
    {
        RfnIdentifier rfnId;
        unsigned long token;
    };

    using RfnTimeouts = std::multimap<CtiTime, RfnRequestIdentifier>;
    using NodeTokens = std::map<RfnIdentifier, unsigned long>;

    RfnTimeouts _awaitingIndications;
    NodeTokens  _activeTokens;

    struct ActiveRfnRequest
    {
        RfnDeviceRequest request;
        PacketInfo currentPacket;
        std::chrono::system_clock::time_point timeout;
        Devices::Commands::RfnCommand::RfnResponsePayload response;
    };

    typedef std::map<RfnIdentifier, ActiveRfnRequest> RfnIdToActiveRequest;

    Mutex                _activeRequestsMux;
    RfnIdToActiveRequest _activeRequests;

    using OptionalResult = std::optional<RfnDeviceResult>;

    ConfigNotificationPtr handleNodeOriginated     (const CtiTime Now, const RfnIdentifier rfnIdentifier, const Protocols::E2eDataTransferProtocol::EndpointMessage & message);
    OptionalResult        handleResponse           (const CtiTime Now, const RfnIdentifier rfnIdentifier, const Protocols::E2eDataTransferProtocol::EndpointMessage & message);
    void                  handleBlockContinuation  (const CtiTime Now, const RfnIdentifier rfnIdentifier, ActiveRfnRequest & activeRequest, const Protocols::E2eDataTransferProtocol::EndpointMessage & message);
    RfnDeviceResult       handleCommandResponse    (const CtiTime Now, const RfnIdentifier rfnIdentifier, ActiveRfnRequest & activeRequest, const Protocols::E2eDataTransferProtocol::EndpointMessage & message);
    RfnDeviceResult       handleCommandError       (const CtiTime Now, const RfnIdentifier rfnIdentifier, ActiveRfnRequest & activeRequest, const YukonError_t error);
};

}