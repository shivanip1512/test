#pragma once

#include "DispatchConnection.h"

class CapControlDispatchConnection : public DispatchConnection
{
    public:
        CapControlDispatchConnection(const std::string &connectionName, Que_t *inQ = NULL, int tt = 3);
        virtual void writeIncomingMessageToQueue(CtiMessage *msgPtr);
};
