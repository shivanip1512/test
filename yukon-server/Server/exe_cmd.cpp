/*-----------------------------------------------------------------------------*
*
* File:   exe_cmd
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/SERVER/exe_cmd.cpp-arc  $
* REVISION     :  $Revision: 1.7.34.1 $
* DATE         :  $Date: 2008/11/13 17:23:38 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include <iostream>
#include <exception>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw\thr\threadid.h>

#include "dlldefs.h"
#include "con_mgr.h"
#include "message.h"

// #include "que_exec.h"
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
