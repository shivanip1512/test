#pragma once

#include "yukon.h"
#include "msg_pdata.h"

class PointDataListener
{
    public:
        virtual bool handlePointDataByPaoId( const int paoId, const CtiPointDataMsg & message ) = 0;
};
