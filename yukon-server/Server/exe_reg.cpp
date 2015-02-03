#include "precompiled.h"

#include "message.h"
#include "exe_reg.h"
#include "con_mgr.h"
#include "con_mgr_vg.h"
#include "server_b.h"
#include "msg_reg.h"

YukonError_t CtiRegistrationExecutor::ServerExecute(CtiServer *Svr)
{
    YukonError_t nRet = ClientErrors::None;

    CtiRegistrationMsg   *Msg = (CtiRegistrationMsg*)getMessage();

    CtiServer::ptr_type sptr = Svr->findConnectionManager((long)getConnectionHandle());

    if(sptr)
    {
        sptr->setClientName(Msg->getAppName());
        sptr->setClientAppId(Msg->getAppId());
        sptr->setClientUnique(Msg->getAppIsUnique());

        nRet = Svr->clientRegistration(sptr);
    }
    else
    {
        CTILOG_WARN(dout, "sptr is null");
    }

    return nRet;
}
CtiRegistrationExecutor::CtiRegistrationExecutor(CtiMessage *p) :
   CtiExecutor(p)
{}

CtiRegistrationExecutor::~CtiRegistrationExecutor()
{}


