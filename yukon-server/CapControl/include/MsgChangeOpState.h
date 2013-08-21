#pragma once

#include <string>

#include "MsgItemCommand.h"

class ChangeOpState : public ItemCommand
{
    public:
        DECLARE_COLLECTABLE( ChangeOpState );

    private:
        typedef ItemCommand Inherited;

    public:
        ChangeOpState();
        ChangeOpState(int bankId, const std::string& opStateName);
        ChangeOpState(const ChangeOpState& msg);
        ~ChangeOpState();

        const std::string& getOpStateName() const;
        void setOpStateName(const std::string& opStateName);

        ChangeOpState& operator=(const ChangeOpState& right);

        virtual CtiMessage* replicateMessage() const;

    private:
        std::string _opStateName;
};
