#include "precompiled.h"

#include "message.h"
#include "con_mgr_vg.h"
#include "ctivangogh.h"
#include "exe_signal.h"
#include "msg_signal.h"

using namespace std;

YukonError_t CtiSignalExecutor::ServerExecute(CtiServer *Svr)
{
    CtiVanGogh *VG = (CtiVanGogh *)Svr;

    const int msgType = getMessage()->isA();

    try
    {
        switch( msgType )
        {
            case MSG_SIGNAL:
            {
                return VG->PostSignalMessage(*(CtiSignalMsg*)getMessage());
            }

            default:
            {
                CTILOG_ERROR(dout, "Unexpected Message Type ("<< msgType <<")");
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return ClientErrors::None;
}

CtiSignalExecutor::CtiSignalExecutor(CtiMessage *p = NULL) :
CtiExecutor(p)
{
}

CtiSignalExecutor::CtiSignalExecutor(const CtiSignalExecutor& aRef)
{
    *this = aRef;
}

CtiSignalExecutor::~CtiSignalExecutor()
{
}
