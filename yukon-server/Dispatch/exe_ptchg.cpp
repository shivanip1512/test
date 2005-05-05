/*-----------------------------------------------------------------------------*
*
* File:   exe_ptchg
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/exe_ptchg.cpp-arc  $
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2005/05/05 17:08:11 $
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
#include "exe_ptchg.h"
#include "msg_dbchg.h"
#include "msg_commerrorhistory.h"
#include "msg_pcreturn.h"

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

CtiPointChangeExecutor::CtiPointChangeExecutor(CtiMessage *p) :
   CtiExecutor(p)
{}

CtiPointChangeExecutor::~CtiPointChangeExecutor()
   {}
