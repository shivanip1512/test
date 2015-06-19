#pragma once

#include "rfn_asid.h"
#include "rfn_identifier.h"

#include "RfnE2eDataIndicationMsg.h"
#include "RfnE2eDataConfirmMsg.h"
#include "RfnE2eDataRequestMsg.h"
#include "NetworkManagerRequest.h"

#include "readers_writer_lock.h"

#include "worker_thread.h"

#include "yukon.h"

#include <boost/ptr_container/ptr_deque.hpp>
#include <boost/optional.hpp>

#include <mutex>

namespace Cti {
namespace Messaging {
namespace Rfn {

class IM_EX_RFN_E2E E2eMessenger
{
public:

    E2eMessenger();
    virtual ~E2eMessenger();

    template<class Msg>
    struct CallbackFor
    {
        using Callback = std::function<void (const Msg &)>;
    };

    struct Message
    {
        RfnIdentifier rfnIdentifier;
    };

    struct PayloadMessage : Message
    {
        std::vector<unsigned char> payload;
    };

    struct Request : PayloadMessage
    {
        CtiTime expiration;
        unsigned long long groupId;
        unsigned char priority : 7;  //  7 bits - this needs to fit into a signed byte
    };

    struct Indication : PayloadMessage, CallbackFor<Indication>
    {
    };

    struct Confirm : Message, CallbackFor<Confirm>
    {
        boost::optional<YukonError_t> error;
    };

    using TimeoutCallback = std::function<void ()>;

    static void registerE2eDtHandler(Indication::Callback callback);
    static void registerDnpHandler  (Indication::Callback callback, const RfnIdentifier rfnid);

    static void sendE2eDt    (const Request &req, const ApplicationServiceIdentifiers asid, Confirm::Callback callback, TimeoutCallback timeout);
    static void sendE2eAp_Dnp(const Request &req, Confirm::Callback callback, TimeoutCallback timeout);

    static void cancelByGroupId(const long groupId);

protected:

    virtual void serializeAndQueue(const Request &req, Confirm::Callback callback, TimeoutCallback timeout, const ApplicationServiceIdentifiers asid);
    virtual void setE2eDtHandler(Indication::Callback callback);
    virtual void processTimeouts();

    virtual void cancel(const long id, NetworkManagerCancelRequest::CancelType cancelType);

private:

    using SerializedMessage = std::vector<unsigned char>;

    using CallbacksPerRfnIdentifier = std::map<RfnIdentifier, Indication::Callback>;

    readers_writer_lock_t _callbackMux;
    boost::optional<Indication::Callback> _e2edtCallback;
    CallbacksPerRfnIdentifier _dnp3Callbacks;

    long long _sessionId;
    long long _messageId;

    void handleRfnE2eDataIndicationMsg(const SerializedMessage &msg);
    void handleRfnE2eDataConfirmMsg   (const SerializedMessage &msg);

    WorkerThread _timeoutProcessor;

    struct MessageCallbacks
    {
        Confirm::Callback confirm;
        TimeoutCallback   timeout;
    };

    using Expirations        = std::multimap<CtiTime, long long>;
    using MessageIdCallbacks = std::map<long long, MessageCallbacks>;
    using RfnIdToMessageId   = std::map<RfnIdentifier, long long>;
    using Mutex     = std::mutex;
    using LockGuard = std::lock_guard<std::mutex>;

    Mutex              _expirationMux;
    Expirations        _pendingExpirations;
    MessageIdCallbacks _pendingCallbacks;
    RfnIdToMessageId   _pendingMessageIds;
};

extern IM_EX_RFN_E2E std::unique_ptr<E2eMessenger> gE2eMessenger;

}
}
}

