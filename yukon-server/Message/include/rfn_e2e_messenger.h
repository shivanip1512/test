#pragma once

#include "rfn_asid.h"
#include "rfn_identifier.h"

#include "RfnE2eDataIndicationMsg.h"
#include "RfnE2eDataConfirmMsg.h"
#include "RfnE2eDataRequestMsg.h"

#include "readers_writer_lock.h"

#include "yukon.h"

#include <boost/ptr_container/ptr_deque.hpp>
#include <boost/optional.hpp>

namespace Cti {
namespace Messaging {
namespace Rfn {

class IM_EX_RFN_E2E E2eMessenger
{
public:

    E2eMessenger();

    template<class Msg>
    struct CallbackFor
    {
        typedef boost::function<void (const Msg &)> Callback;
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
        unsigned char priority : 7;  //  7 bits - this needs to fit into a signed byte
    };

    struct Indication : PayloadMessage, CallbackFor<Indication>
    {
    };

    struct Confirm : Message, CallbackFor<Confirm>
    {
        boost::optional<YukonError_t> error;
    };

    static void registerE2eDtHandler(Indication::Callback callback);
    static void registerDnpHandler  (Indication::Callback callback, const RfnIdentifier rfnid);

    static void sendE2eDt    (const Request &msg, const ApplicationServiceIdentifiers asid, Confirm::Callback callback);
    static void sendE2eAp_Dnp(const Request &msg, Confirm::Callback callback);

protected:

    virtual void serializeAndQueue(const E2eDataRequestMsg &msg, Confirm::Callback callback);
    virtual void setE2eDtHandler(Indication::Callback callback);

private:

    typedef std::vector<unsigned char> SerializedMessage;

    typedef std::map<RfnIdentifier, Indication::Callback> CallbacksPerRfnIdentifier;

    readers_writer_lock_t _callbackMux;
    boost::optional<Indication::Callback> _e2edtCallback;
    CallbacksPerRfnIdentifier _dnp3Callbacks;

    void handleRfnE2eDataIndicationMsg(const SerializedMessage &msg);
    void handleRfnE2eDataConfirmMsg   (const SerializedMessage &msg, Confirm::Callback callback);
};

extern IM_EX_RFN_E2E std::auto_ptr<E2eMessenger> gE2eMessenger;

}
}
}

