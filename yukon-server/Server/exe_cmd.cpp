/*-----------------------------------------------------------------------------*
*
* File:   exe_cmd
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/SERVER/exe_cmd.cpp-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/02/17 19:02:58 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <windows.h>
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

INT CtiCommandExecutor::ServerExecute(CtiServer *Svr)
{
    INT nRet = NoError;      // Everything was ok, please clean up my message memory

    CtiCommandMsg* Cmd = (CtiCommandMsg*)getMessage();

    try
    {
        Svr->commandMsgHandler( Cmd );
    }
    catch(...)
    {
        {
            cout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return nRet;
}
CtiCommandExecutor::CtiCommandExecutor(CtiMessage *p) :
CtiExecutor(p)
{}

CtiCommandExecutor::~CtiCommandExecutor()
{}
