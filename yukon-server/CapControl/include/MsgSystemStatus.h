#pragma once

#include "MsgCapControlMessage.h"

class SystemStatus : public CapControlMessage
{
    public:
        DECLARE_COLLECTABLE( SystemStatus );

    private:
        typedef CapControlMessage Inherited;

    public:
        SystemStatus(bool state);
        virtual ~SystemStatus();

        SystemStatus& operator=(const SystemStatus& right);
        virtual CtiMessage* replicateMessage() const;

        bool getState() const { return _systemState; }

    private:
        bool _systemState;

        SystemStatus(){};
};
