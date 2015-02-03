#include "precompiled.h"

#include "executorfactory.h"
#include "pil_exefct.h"
#include "exe_pcreq.h"

#include "message.h"
#include "logger.h"

CtiExecutor* CtiPILExecutorFactory::getExecutor(CtiMessage* msg)
{
    switch( msg->isA() )
    {
        case MSG_PCREQUEST:
        case MSG_MULTI:
        {
            return new CtiRequestExecutor(msg);
        }
    }

    if( CtiExecutor *Ex = Inherited::getExecutor(msg) )
    {
        return Ex;
    }

    CTILOG_WARN(dout,  "Failed to manufacture an executor for " << msg->isA());

    return NULL;
}


