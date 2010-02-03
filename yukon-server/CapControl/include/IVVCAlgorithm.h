#pragma once

#include "ccsubstationbus.h"
#include "IVVCStrategy.h"

class IVVCAlgorithm
{
    public:
        static void execute(IVVCStatePtr p, CtiCCSubstationBusPtr subbus, IVVCStrategy* strategy);
};

