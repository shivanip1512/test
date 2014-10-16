/*-----------------------------------------------------------------------------*
*
* File:   exe_signal
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/exe_signal.cpp-arc  $
* REVISION     :  $Revision: 1.6.24.1 $
* DATE         :  $Date: 2008/11/13 17:23:49 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw\thr\threadid.h>
#include "message.h"
#include "con_mgr_vg.h"
#include "ctivangogh.h"
#include "exe_signal.h"
#include "msg_signal.h"

YukonError_t CtiSignalExecutor::ServerExecute(CtiServer *Svr)
{
    YukonError_t nRet = ClientErrors::None;

    CtiVanGogh *VG = (CtiVanGogh *)Svr;

    try
    {
        const int msgType = getMessage()->isA();
        switch( msgType )
        {
        case MSG_SIGNAL:
            {
                nRet = VG->PostSignalMessage(*(CtiSignalMsg*)getMessage());
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
