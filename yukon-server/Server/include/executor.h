#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*-----------------------------------------------------------------------------*
*
* File:   executor
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/SERVER/INCLUDE/executor.h-arc  $
* REVISION     :  $Revision: 1.3.90.1 $
* DATE         :  $Date: 2008/11/13 17:23:38 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#ifndef __SVREXECUTOR_H__
#define __SVREXECUTOR_H__


#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>

#include "message.h"
#include "dlldefs.h"

// Forward Declarations
class CtiConnectionManager;
class CtiServer;

class IM_EX_CTISVR CtiExecutor
{
private:
   CtiMessage *msg_;

public:
   CtiExecutor(CtiMessage *msg = NULL);// :msg_(msg){}

   virtual ~CtiExecutor();
   CtiConnectionManager* getConnectionHandle();
   const CtiConnectionManager* getConnectionHandle() const;

   CtiMessage*  getMessage();

   /*
    *  The ServerExecute MUST call releaseMessage() if the message is passed on back to the
    *  client.  Otherwise, the message will be deleted from the queue and a wicked cascade will
    *  occur.
    */
   void releaseMessage();


   virtual INT  ServerExecute(CtiServer *Svr) = 0;

};

#endif //#ifndef __SVREXECUTOR_H__


