#include "precompiled.h"

#include "message.h"
#include "con_mgr_vg.h"
#include "ctivangogh.h"
#include "exe_ptchg.h"
#include "msg_dbchg.h"
#include "msg_pcreturn.h"

using namespace std;

YukonError_t CtiPointChangeExecutor::ServerExecute(CtiServer *Svr)
{
   YukonError_t nRet = ClientErrors::None;

    CtiVanGogh *VG = (CtiVanGogh *)Svr;

    try
    {
        const int msgType = getMessage()->isA();
        switch( msgType )
        {
        case MSG_MULTI:
        case MSG_SIGNAL:
        case MSG_POINTDATA:
        case MSG_PCRETURN:
        case MSG_TAG:
        case MSG_LMCONTROLHISTORY:
        case MSG_POINTREGISTRATION:
        case MSG_DBCHANGE:
            {
                try
                {
                   nRet = VG->processMessage(getMessage());
                }
                catch(...)
                {
                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                }
                break;
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

   return nRet;
}

CtiPointChangeExecutor::CtiPointChangeExecutor(CtiMessage *p) :
   CtiExecutor(p)
{}

CtiPointChangeExecutor::~CtiPointChangeExecutor()
   {}
