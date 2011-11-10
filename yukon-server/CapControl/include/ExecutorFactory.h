#pragma once

#include "message.h"
#include "ccexecutor.h"

class CtiCCExecutorFactory
{
    public:
        static std::auto_ptr<CtiCCExecutor> createExecutor(const CtiMessage* message);
};
