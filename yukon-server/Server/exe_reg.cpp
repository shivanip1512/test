#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*-----------------------------------------------------------------------------*
*
* File:   exe_reg
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/SERVER/exe_reg.cpp-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 16:00:47 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include <windows.h>
#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw\thr\threadid.h>
#include "message.h"
#include "exe_reg.h"
//#include "que_exec.h"
#include "con_mgr.h"
#include "con_mgr_vg.h"
// #include "ctivangogh.h"
#include "server_b.h"
#include "msg_reg.h"
#include "yukon.h"

INT CtiRegistrationExecutor::ServerExecute(CtiServer *Svr)
{
   INT nRet = NoError;

   CtiRegistrationMsg   *Msg = (CtiRegistrationMsg*)getMessage();
   CtiConnectionManager *CM  = getConnectionHandle();

   CM->setClientName(Msg->getAppName());
   CM->setClientAppId(Msg->getAppId());
   CM->setClientUnique(Msg->getAppIsUnique());

   nRet = Svr->clientRegistration(CM);

   return nRet;
}
CtiRegistrationExecutor::CtiRegistrationExecutor(CtiMessage *p) :
   CtiExecutor(p)
{}

CtiRegistrationExecutor::~CtiRegistrationExecutor()
{}


