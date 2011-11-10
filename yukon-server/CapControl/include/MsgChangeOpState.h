#pragma once

#include <string>

#include "MsgItemCommand.h"

class ChangeOpState : public ItemCommand
{
    RWDECLARE_COLLECTABLE( ChangeOpState )

    private:
        typedef ItemCommand Inherited;

    public:
        ChangeOpState();
        ChangeOpState(int bankId, const std::string& opStateName);
        ChangeOpState(const ChangeOpState& msg);
        ~ChangeOpState();

        const std::string& getOpStateName();
        void setOpStateName(const std::string& opStateName);

        void restoreGuts(RWvistream& iStream);
        void saveGuts(RWvostream& oStream) const;

        ChangeOpState& operator=(const ChangeOpState& right);

        virtual CtiMessage* replicateMessage() const;

    private:
        std::string _opStateName;
};
