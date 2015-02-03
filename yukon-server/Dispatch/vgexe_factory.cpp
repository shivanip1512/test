#include "precompiled.h"

#include "vgexe_factory.h"
#include "message.h"

#include "executor.h"
#include "exe_ptchg.h"

#include "logger.h"

CtiExecutor* CtiVanGoghExecutorFactory::getExecutor(CtiMessage* msg)
{
    switch(msg->isA())
    {
        case MSG_PCRETURN:
        case MSG_MULTI:
        case MSG_POINTDATA:
        case MSG_POINTREGISTRATION:
        case MSG_DBCHANGE:
        case MSG_SIGNAL:
        case MSG_LMCONTROLHISTORY:
        case MSG_TAG:
        {
            return new CtiPointChangeExecutor(msg);
        }
    }

    if( CtiExecutor *Ex = Inherited::getExecutor(msg) )
    {
        return Ex;
    }

    CTILOG_WARN(dout, "Failed to manufacture an executor for " << msg->isA());

    return NULL;
}

