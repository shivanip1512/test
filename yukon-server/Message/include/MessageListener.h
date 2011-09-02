#pragma once

#include "yukon.h"
#include "message.h"

class MessageListener
{
    public:
        //The Listener is expected to process this message fast to not slow down the connection thread.
        virtual void processNewMessage(CtiMessage* message)=0;
};

typedef MessageListener* MessageListenerPtr;

