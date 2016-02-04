#include "precompiled.h"

#include "message.h"
#include "exe_reg.h"
#include "con_mgr.h"
#include "con_mgr_vg.h"
#include "server_b.h"
#include "msg_reg.h"

YukonError_t CtiRegistrationExecutor::ServerExecute(CtiServer *Svr)
{
    if( auto Msg = getMessage() )
    {
        if( auto Msg = dynamic_cast<CtiRegistrationMsg*>(getMessage()) )
        {
            if( auto sptr = Svr->findConnectionManager(Msg->getConnectionHandle()) )
            {
                sptr->setClientName(Msg->getAppName());
                sptr->setClientAppId(Msg->getAppId());
                sptr->setClientUnique(Msg->getAppIsUnique());

                return Svr->clientRegistration(sptr);
            }

            CTILOG_WARN(dout, "could not find server for registration message" << *Msg);

            return ClientErrors::Abnormal;
        }

        CTILOG_ERROR(dout, "Message was not a CtiRegistrationMsg");

        return ClientErrors::Abnormal;
    }

    CTILOG_ERROR(dout, "getMessage() was null");

    return ClientErrors::Memory;
}
CtiRegistrationExecutor::CtiRegistrationExecutor(CtiMessage *p) :
   CtiExecutor(p)
{}

CtiRegistrationExecutor::~CtiRegistrationExecutor()
{}


