#pragma once

#include "dlldefs.h"
#include "prot_e2eDataTransfer.h"
#include "dev_rfn.h"
#include "cmd_rfn.h"
#include "rfn_asid.h"
#include "rfn_e2e_messenger.h"
#include "mgr_device.h"
//#include "RfnEdgeDrMessaging.h"
#include "RfnBroadcastMessaging.h"

#include <boost/ptr_container/ptr_deque.hpp>

#include <chrono>

namespace Cti::Messaging::Rfn {
    struct MeterProgramStatusArchiveRequestMsg;
//    struct RfnBroadcastRequest;
  //  struct RfnBroadcastReply;
    struct EdgeDrBroadcastRequest;

}

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

    RfnRequestManager(CtiDeviceManager& DeviceManager);

    using ConfigNotificationPtr = std::unique_ptr<Devices::Commands::RfnConfigNotificationCommand>;
    using Bytes = std::vector<unsigned char>;
    using Block = Protocols::Coap::Block;
    using BlockSize = Protocols::Coap::BlockSize;
    using EndpointMessage = Protocols::E2e::EndpointMessage;

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

    using Token = std::uint32_t;

    void tick();

    void submitRequests(RfnDeviceRequestList requests);

    using BroadcastTimeoutCallback = std::function<void()>;
    using BroadcastResponseCallback = std::function<void(Messaging::Rfn::RfnBroadcastReply&)>;

    void submitBroadcastRequest( Messaging::Rfn::RfnBroadcastRequest & request,
                                 BroadcastResponseCallback  responded,
                                 std::chrono::seconds       timeout,
                                 BroadcastTimeoutCallback   timedOut );

    ResultQueue getResults(unsigned max);
    UnsolicitedReports getUnsolicitedReports();

    void  cancelByGroupMessageId(long groupMessageId);
    size_t countByGroupMessageId(long groupMessageId);

    void start();

protected:

    virtual EndpointMessage handleE2eDtIndication(const Bytes& payload, const RfnIdentifier endpointId);
    virtual Bytes createE2eDtRequest(const Bytes& payload, const RfnIdentifier endpointId, const Token token);
    virtual Bytes createE2eDtPost(const Bytes& payload, const RfnIdentifier endpointId, const Token token);
    virtual Bytes createE2eDtPut(const Bytes& payload, const RfnIdentifier endpointId);
    virtual Bytes createE2eDtBlockContinuation(const BlockSize blockSize, const int blockNum, const RfnIdentifier endpointId, const Token token);
    virtual Bytes createE2eDtReply(const unsigned short id, const Bytes& payload, const Token token);
    virtual Bytes createE2eDtBlockReply(const unsigned short id, const Bytes& payload, const Token token, Block block);
    virtual void sendMeterProgramStatusUpdate(Messaging::Rfn::MeterProgramStatusArchiveRequestMsg msg);

    virtual bool isE2eServerDisabled() const;

private:

    using RfnIdentifierSet = std::set<RfnIdentifier>;

    RfnIdentifierSet handleConfirms();
    RfnIdentifierSet handleIndications();
    RfnIdentifierSet handleTimeouts();
    void             handleReplies();
    void             handleNewRequests(const RfnIdentifierSet &recentCompletions);
    void             postResults();
    void             reportStatistics();

    using ApplicationServiceIdentifiers = Messaging::Rfn::ApplicationServiceIdentifiers;
    using PriorityClass = Messaging::Rfn::PriorityClass;

    Protocols::E2eDataTransferProtocol _e2edt;

    struct PacketInfo
    {
        Bytes    payloadSent;
        CtiTime  timeSent;
        std::chrono::seconds retransmissionDelay;
        unsigned retransmits;
        unsigned maxRetransmits;
    };

    enum class AckType {
        Success,
        BadRequest
    };

    PacketInfo sendE2eDataRequestPacket(const Bytes& e2ePacket, const ApplicationServiceIdentifiers &asid, const PriorityClass priorityClass, const RfnIdentifier &rfnIdentifier, const unsigned priority, const long groupMessageId, const CtiTime timeout);
    void sendE2eDataAck  (const unsigned short id, const AckType ackType, const ApplicationServiceIdentifiers &asid, const PriorityClass priorityClass, const RfnIdentifier &rfnIdentifier);
    void sendMeterProgrammingBlock(const unsigned short id, const Bytes data, const ApplicationServiceIdentifiers& asid, const RfnIdentifier& rfnIdentifier, const Token token, const Block block);
    void updateMeterProgrammingProgress(Devices::RfnDevice& rfnDevice, const std::string& guid, const size_t totalSent);

    void checkForNewRequest(const RfnIdentifier &rfnId);

    template <typename ValueType>
    using RfnIdTo = std::map<RfnIdentifier, ValueType>;

    using IndicationQueue  = std::vector<Messaging::Rfn::E2eMessenger::Indication>;
    using ConfirmQueue     = std::vector<Messaging::Rfn::E2eMessenger::Confirm>;
    using ExpirationCauses = RfnIdTo<YukonError_t>;
    using RequestHeap      = std::vector<RfnDeviceRequest>;
    using RfnIdToRequestHeap = RfnIdTo<RequestHeap>;

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

    CtiDeviceManager   & _deviceManager;

    struct RfnRequestIdentifier
    {
        RfnIdentifier rfnId;
        Token token;
    };

    using RfnTimeouts = std::multimap<std::chrono::system_clock::time_point, RfnRequestIdentifier>;

    RfnTimeouts    _awaitingIndications;
    RfnIdTo<Token> _activeTokens;

    struct ActiveRfnRequest
    {
        RfnDeviceRequest request;
        PacketInfo currentPacket;
        std::chrono::system_clock::time_point timeout;
        Devices::Commands::RfnCommand::RfnResponsePayload response;
    };

    Mutex _activeRequestsMux;  //  needed for countByGroupMessageId
    RfnIdTo<ActiveRfnRequest> _activeRequests;

    struct MeterProgrammingRequest
    {
        std::chrono::system_clock::time_point timeout;
        std::string guid;
        Token token;
    };

    RfnTimeouts _awaitingMeterProgrammingRequest;
    RfnIdTo<MeterProgrammingRequest> _meterProgrammingRequests;

    // Broadcast requests
    struct BroadcastCallbacks
    {
        BroadcastResponseCallback response;
        BroadcastTimeoutCallback  timeout;
    };

    std::multimap<std::chrono::system_clock::time_point, long long> _broadcastTimeouts;
    std::map<long long, BroadcastCallbacks> _broadcastCallbacks;

    using BroadcastReplyQueue = std::vector<Messaging::Rfn::RfnBroadcastReply>;

    Mutex                _broadcastReplyMux;
    BroadcastReplyQueue  _broadcastReplies;

    using SerializedMessage = std::vector<unsigned char>;

    void handleRfnBroadcastReplyMsg( const SerializedMessage & msg );


    //
    
    using OptionalResult = std::optional<RfnDeviceResult>;

    void                  handleNodeOriginated     (const CtiTime Now, const RfnIdentifier rfnIdentifier, const EndpointMessage & message, const ApplicationServiceIdentifiers asid);
    OptionalResult        handleResponse           (const CtiTime Now, const RfnIdentifier rfnIdentifier, const EndpointMessage & message);
    void                  handleBlockContinuation  (const CtiTime Now, const RfnIdentifier rfnIdentifier, ActiveRfnRequest & activeRequest, const Token token, const Bytes& payload, const Block block);
    RfnDeviceResult       handleCommandResponse    (const CtiTime Now, const RfnIdentifier rfnIdentifier, ActiveRfnRequest & activeRequest, const Token token, const Bytes& payload);
    RfnDeviceResult       handleCommandError       (const CtiTime Now, const RfnIdentifier rfnIdentifier, ActiveRfnRequest & activeRequest, const YukonError_t error);
};

}