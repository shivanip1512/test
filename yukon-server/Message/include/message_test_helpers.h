#pragma once

#include "rfn_e2e_messenger.h"

namespace Cti::Test {

struct test_E2eMessenger : Cti::Messaging::Rfn::E2eMessenger
{
    std::vector<Request> messages;

    Indication::Callback indicationHandler;

    void setE2eDtHandler(Indication::Callback callback) override
    {
        indicationHandler = callback;
    }

    void serializeAndQueue(const Request& req, const Cti::Messaging::Rfn::ApplicationServiceIdentifiers asid, Confirm::Callback callback, TimeoutCallback timeout) override
    {
        messages.push_back(req);

        Confirm ack;
        ack.rfnIdentifier = req.rfnIdentifier;
        ack.error = ClientErrors::None;

        callback(ack);  //  call the confirm callback immediately
    }
};

struct Override_E2eMessenger
{
    std::unique_ptr<Cti::Messaging::Rfn::E2eMessenger> oldMessenger;
    test_E2eMessenger* testMessenger;

    Override_E2eMessenger()
    {
        auto newMessenger = std::make_unique<test_E2eMessenger>();
        testMessenger = newMessenger.get();
        oldMessenger = std::exchange(Cti::Messaging::Rfn::gE2eMessenger, std::move(newMessenger));
    }

    ~Override_E2eMessenger()
    {
        //  Put the old one back
        Cti::Messaging::Rfn::gE2eMessenger = std::move(oldMessenger);
    }
};

}