#pragma once

#include "message.h"
#include "ccexecutor.h"

class CtiCCExecutorFactory
{
    public:
        static std::unique_ptr<CtiCCExecutor> createExecutor(const CtiMessage* message);
};
