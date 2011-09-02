#pragma once

#include "yukon.h"
#include "msg_pdata.h"

class UpdatablePao
{
    public:
        virtual void handlePointData(CtiPointDataMsg* message)=0;
};
