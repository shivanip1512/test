#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*-----------------------------------------------------------------------------*
*
* File:   exe_ptchg
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/exe_ptchg.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2003/12/30 21:46:25 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


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
      case MSG_POINTREGISTRATION:
         {
            try
            {
               nRet = VG->registration((CtiVanGoghConnectionManager*)(getMessage()->getConnectionHandle()),
                                       *(CtiPointRegistrationMsg*)getMessage());

            }
            catch(...)
            {
               {
                  cout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
               }
            }

            break;
         }
      case MSG_DBCHANGE:
         {
            try
            {
               nRet = VG->postDBChange(*(CtiDBChangeMsg*)getMessage());
            }
            catch(...)
            {
               {
                  cout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
               }
            }
            break;
         }
      case MSG_MULTI:
      case MSG_SIGNAL:
      case MSG_POINTDATA:
      case MSG_PCRETURN:
      case MSG_TAG:
         {
            try
            {
               nRet = VG->processMessage(getMessage());
            }
            catch(...)
            {
               {
                  cout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
               }
            }
            break;
         }
      case MSG_LMCONTROLHISTORY:
         {
            try
            {
               nRet = VG->processControlMessage((CtiLMControlHistoryMsg*)getMessage());
            }
            catch(...)
            {
               {
                  cout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
               }
            }
            break;
         }
      case MSG_COMMERRORHISTORY:
          {
              nRet = VG->processCommErrorMessage((CtiCommErrorHistoryMsg*)getMessage());
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
         cout << "**** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
      }
   }

   return nRet;
}

CtiPointChangeExecutor::CtiPointChangeExecutor(CtiMessage *p) :
   CtiExecutor(p)
{}

CtiPointChangeExecutor::~CtiPointChangeExecutor()
   {}
