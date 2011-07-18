/*-----------------------------------------------------------------------------*
*
* File:   vgexe_factory
*
* Date:   7/17/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/vgexe_factory.cpp-arc  $
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2005/07/19 22:48:53 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"


#include <windows.h>
#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include "executorfactory.h"
#include "vgexe_factory.h"
#include "ctibase.h"

CtiExecutor* CtiVanGoghExecutorFactory::getExecutor(CtiMessage* msg)
{
   CtiExecutor *Ex = NULL;

   switch(msg->isA())
   {
   case MSG_PCRETURN:
   case MSG_MULTI:
   case MSG_POINTDATA:
   case MSG_POINTREGISTRATION:
   case MSG_DBCHANGE:
   case MSG_SIGNAL:
   case MSG_LMCONTROLHISTORY:
   case MSG_COMMERRORHISTORY:
   case MSG_TAG:
      {
         Ex = CTIDBG_new CtiPointChangeExecutor(msg);
         break;
      }
   default:
      {
         Ex = Inherited::getExecutor(msg);

         if(!Ex)
            cout << "CtiVanGoghExecutor failed to manufacture an executor for " << msg->isA() << endl;

         break;
      }
   }
   return Ex;
}

CtiVanGoghExecutorFactory::CtiVanGoghExecutorFactory(){}
CtiVanGoghExecutorFactory::~CtiVanGoghExecutorFactory(){}

