/*-----------------------------------------------------------------------------*
*
* File:   exe_email
*
* Date:   4/9/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/exe_email.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/02/10 23:23:50 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"




#include <windows.h>
#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <rw\thr\threadid.h>
#include "message.h"
#include "con_mgr_vg.h"
#include "ctivangogh.h"
#include "exe_email.h"
#include "logger.h"

INT CtiEmailExecutor::ServerExecute(CtiServer *Svr)
{
    INT nRet = NoError;

    CtiVanGogh *VG = (CtiVanGogh *)Svr;

    try
    {
        switch(getMessage()->isA())
        {
        case MSG_EMAIL:
            {
                try
                {
                    nRet = VG->mail(*(CtiEmailMsg*)getMessage());
                }
                catch(...)
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }

                break;
            }
        default:
            {
                {
                    cout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

CtiEmailExecutor::CtiEmailExecutor(CtiMessage *p) :
CtiExecutor(p)
{
}

CtiEmailExecutor::~CtiEmailExecutor()
{
}



