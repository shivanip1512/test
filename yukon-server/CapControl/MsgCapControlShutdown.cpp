#include "precompiled.h"

#include "MsgCapControlShutdown.h"
#include "ccid.h"

RWDEFINE_COLLECTABLE( CtiCCShutdown, CTICCSHUTDOWN_ID )

void CtiCCShutdown::restoreGuts(RWvistream& strm)
{
    Inherited::restoreGuts(strm);
}

void CtiCCShutdown::saveGuts(RWvostream& strm) const
{
    Inherited::saveGuts(strm);
}
