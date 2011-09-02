#pragma once

#include "yukon.h"
#include "msg_pdata.h"

class PointDataListener
{
    public:
        virtual bool handlePointDataByPaoId(int paoId, CtiPointDataMsg* message)=0;
};
