#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   executorfactory
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/SERVER/executorfactory.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:20:03 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <windows.h>
#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include "executorfactory.h"

CtiExecutor* CtiExecutorFactory::getExecutor(CtiMessage* msg)
{
   CtiExecutor *Ex = NULL;

   switch(msg->isA())
   {
   case MSG_COMMAND:
      {
         Ex = new CtiCommandExecutor(msg);
         break;
      }
   case MSG_REGISTER:
      {
         Ex = new CtiRegistrationExecutor(msg);
         break;
      }
   default:
      {
         cout << "CtiExecutorFactory failed to manufacture an executor for " << msg->isA() << endl;
         break;
      }
   }

   return Ex;
}

CtiExecutorFactory::CtiExecutorFactory()
   {}
CtiExecutorFactory::~CtiExecutorFactory()
   {}

