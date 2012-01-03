#pragma once

#include "message.h"

class CtiCCShutdown : public CtiMessage
{
    RWDECLARE_COLLECTABLE( CtiCCShutdown )

    public:
        typedef CtiMessage Inherited;

        CtiCCShutdown() : Inherited() {} ;

        void restoreGuts( RWvistream& );
        void saveGuts( RWvostream&) const;
};
