/*-----------------------------------------------------------------------------*
*
* File:   executor
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/SERVER/executor.cpp-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/02/10 23:24:03 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <windows.h>
#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include "executor.h"
#include "con_mgr.h"


CtiExecutor::~CtiExecutor()
{

   // cout << " Dead CtiExecutor..." << endl;

   if( msg_ != NULL )
   {
      // cout << " Dead Message (CtiExecutor...)" << endl;
      delete msg_;      // get rid of the message here
      msg_ = NULL;
   }
}

CtiMessage*  CtiExecutor::getMessage()
{
   return msg_;
}

void CtiExecutor::releaseMessage()
{
   // This method releases the message from being deleted upon destruction of this object.

   msg_ = NULL;      // Be darn sure someone else is handling this..
}

CtiConnectionManager* CtiExecutor::getConnectionHandle()
{
   CtiConnectionManager    *conManager = NULL;

   if(msg_)
   {
      conManager = (CtiConnectionManager*)msg_->getConnectionHandle();
   }

   return conManager;
}

const CtiConnectionManager* CtiExecutor::getConnectionHandle() const
{
   CtiConnectionManager    *conManager = NULL;

   if(msg_)
   {
      conManager = (CtiConnectionManager*)msg_->getConnectionHandle();
   }

   return conManager;
}
CtiExecutor::CtiExecutor(CtiMessage *msg) :
   msg_(msg)
{}

