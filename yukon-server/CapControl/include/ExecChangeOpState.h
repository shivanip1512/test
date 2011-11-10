#pragma once

#include <string>

#include "MsgChangeOpState.h"
#include "ccexecutor.h"

class ChangeOpStateExecutor : public CtiCCExecutor
{
    public:
        ChangeOpStateExecutor(ChangeOpState* message);

        virtual ~ChangeOpStateExecutor();
        virtual void execute();

    private:
        int _bankId;
        std::string _newState;
        std::string _userName;
};
