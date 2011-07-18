/*-----------------------------------------------------------------------------*
*
* File:   exe_reg
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/SERVER/exe_reg.cpp-arc  $
* REVISION     :  $Revision: 1.5.24.1 $
* DATE         :  $Date: 2008/11/13 17:23:38 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw\thr\threadid.h>
#include "message.h"
#include "exe_reg.h"
//#include "que_exec.h"
#include "con_mgr.h"
#include "con_mgr_vg.h"
#include "server_b.h"
#include "msg_reg.h"

INT CtiRegistrationExecutor::ServerExecute(CtiServer *Svr)
{
    INT nRet = NoError;

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
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return nRet;
}
CtiRegistrationExecutor::CtiRegistrationExecutor(CtiMessage *p) :
   CtiExecutor(p)
{}

CtiRegistrationExecutor::~CtiRegistrationExecutor()
{}


