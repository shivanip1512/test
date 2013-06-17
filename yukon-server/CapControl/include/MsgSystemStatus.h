#pragma once

#include "MsgCapControlMessage.h"

class SystemStatus : public CapControlMessage
{
    RWDECLARE_COLLECTABLE( SystemStatus );

    private:
        typedef CapControlMessage Inherited;

    public:
        SystemStatus(bool state);
        virtual ~SystemStatus();

        void restoreGuts(RWvistream& iStream);
        void saveGuts(RWvostream& oStream) const;

        SystemStatus& operator=(const SystemStatus& right);
        virtual CtiMessage* replicateMessage() const;

    private:
        bool _systemState;

        SystemStatus(){};
};
