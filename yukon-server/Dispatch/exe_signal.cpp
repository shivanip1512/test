/*-----------------------------------------------------------------------------*
*
* File:   exe_signal
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/exe_signal.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/02/10 23:23:50 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <windows.h>
#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw\thr\threadid.h>
#include "message.h"
#include "con_mgr_vg.h"
#include "ctivangogh.h"
#include "exe_signal.h"
#include "msg_signal.h"

INT CtiSignalExecutor::ServerExecute(CtiServer *Svr)
{
    INT nRet = NoError;

    CtiVanGogh *VG = (CtiVanGogh *)Svr;

    try
    {
        switch(getMessage()->isA())
        {
        case MSG_SIGNAL:
            {
                nRet = VG->PostSignalMessage(*(CtiSignalMsg*)getMessage());
                break;
            }
        default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                break;
            }
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
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
