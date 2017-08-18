#pragma once

#include "yukon.h"

class CtiPointDataMsg;


class PointDataListener
{
    public:
        virtual bool handlePointDataByPaoId( const int paoId, const CtiPointDataMsg & message ) = 0;
};
