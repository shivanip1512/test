#pragma once

#include "message.h"

class CtiCCShutdown : public CtiMessage
{
    public:
        DECLARE_COLLECTABLE( CtiCCShutdown )

    public:
        typedef CtiMessage Inherited;

        CtiCCShutdown() : Inherited() {} ;
};
