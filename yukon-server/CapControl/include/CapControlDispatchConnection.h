#pragma once

#include "DispatchConnection.h"

class CapControlDispatchConnection : public DispatchConnection
{
    public:
        CapControlDispatchConnection(const string &connectionName, const int &port, const string &host, Que_t *inQ = NULL, int tt = 3);
        virtual void writeIncomingMessageToQueue(CtiMessage *msgPtr);
};
