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
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 15:58:29 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#define __PTCONNECT_H__

#include <rw\cstring.h>
#include <rw\rwtime.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>
#include <rw\thr\mutex.h>
#include <rw/tpslist.h>

#include "dlldefs.h"
#include "hashkey.h"
#include "pt_base.h"
#include "con_mgr.h"
#include "msg_pdata.h"

class CtiPointChange;
class CtiConnectionManager;


class IM_EX_CTIVANGOGH CtiPointConnection : public RWMonitor< RWRecursiveLock< RWMutexLock > >
{
protected:

   // Vector of managers who care about this point.
   RWTPtrSlist<CtiConnectionManager>   ConnectionManagerCollection;

public:

   CtiPointConnection();
   virtual ~CtiPointConnection();
   void AddConnectionManager(CtiConnectionManager *cm);
   void RemoveConnectionManager(CtiConnectionManager *cm);
   int PostPointChangeToConnections(const CtiPointDataMsg& Msg);

   CtiPointConnection& operator=(const CtiPointConnection &aRef);

   RWTPtrSlist<CtiConnectionManager>& getManagerList();
   RWTPtrSlist<CtiConnectionManager>  getManagerList() const;
};

#endif // __PTCONNECT_H__
