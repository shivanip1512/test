/*-----------------------------------------------------------------------------*
*
* File:   exe_ptchg
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/exe_ptchg.cpp-arc  $
* REVISION     :  $Revision: 1.10.2.1 $
* DATE         :  $Date: 2008/11/13 17:23:49 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <iostream>

#include <rw\thr\threadid.h>

#include "message.h"
#include "con_mgr_vg.h"
#include "ctivangogh.h"
#include "exe_ptchg.h"
#include "msg_dbchg.h"
#include "msg_commerrorhistory.h"
#include "msg_pcreturn.h"

using namespace std;

INT CtiPointChangeExecutor::ServerExecute(CtiServer *Svr)
{
   INT nRet = NoError;

   CtiVanGogh *VG = (CtiVanGogh *)Svr;

   try
   {
      switch(getMessage()->isA())
      {
      case MSG_MULTI:
      case MSG_SIGNAL:
      case MSG_POINTDATA:
      case MSG_PCRETURN:
      case MSG_TAG:
      case MSG_LMCONTROLHISTORY:
      case MSG_COMMERRORHISTORY:
      case MSG_POINTREGISTRATION:
      case MSG_DBCHANGE:
         {
            try
            {
               nRet = VG->processMessage(getMessage());
            }
            catch(...)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
           dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
       }
   }

   return nRet;
}

CtiPointChangeExecutor::CtiPointChangeExecutor(CtiMessage *p) :
   CtiExecutor(p)
{}

CtiPointChangeExecutor::~CtiPointChangeExecutor()
   {}
