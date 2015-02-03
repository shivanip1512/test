#include "precompiled.h"

#include "dlldefs.h"
#include "con_mgr.h"
#include "message.h"

#include "server_b.h"
#include "msg_cmd.h"

#include "exe_cmd.h"
#include "logger.h"

YukonError_t CtiCommandExecutor::ServerExecute(CtiServer *Svr)
{
    YukonError_t nRet = ClientErrors::None;      // Everything was ok, please clean up my message memory

    CtiCommandMsg* Cmd = (CtiCommandMsg*)getMessage();

    try
    {
        Svr->commandMsgHandler( Cmd );
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return nRet;
}
CtiCommandExecutor::CtiCommandExecutor(CtiMessage *p) :
CtiExecutor(p)
{}

CtiCommandExecutor::~CtiCommandExecutor()
{}
