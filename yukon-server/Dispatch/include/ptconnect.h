#ifndef __PTCONNECT_H__
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   ptconnect
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/INCLUDE/ptconnect.h-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2007/11/12 16:47:40 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#define __PTCONNECT_H__

#include "ctitime.h"
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>
#include <rw\thr\mutex.h>

#include "dlldefs.h"
#include "hashkey.h"
#include "pt_base.h"
#include "con_mgr.h"
#include "msg_pdata.h"
#include "server_b.h"

class CtiPointChange;
class CtiConnectionManager;


class IM_EX_CTIVANGOGH CtiPointConnection : public RWMonitor< RWRecursiveLock< RWMutexLock > >
{
protected:

   // Vector of managers who care about this point.
   list< CtiServer::ptr_type >   ConnectionManagerCollection;

public:

   CtiPointConnection();
   virtual ~CtiPointConnection();
   void AddConnectionManager(CtiServer::ptr_type cm);
   void RemoveConnectionManager(CtiServer::ptr_type cm);
   int PostPointChangeToConnections(const CtiPointDataMsg& Msg);

   CtiPointConnection& operator=(const CtiPointConnection &aRef);

   list< CtiServer::ptr_type >& getManagerList();
   list< CtiServer::ptr_type >  getManagerList() const;
};

#endif // __PTCONNECT_H__
