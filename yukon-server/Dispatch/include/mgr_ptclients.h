#ifndef __MGR_PTCLIENTS_H__
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   mgr_ptclients
*
* mgr_ptclients.h      7/7/99
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/INCLUDE/mgr_ptclients.h-arc  $
* REVISION     :  $Revision: 1.12 $
* DATE         :  $Date: 2007/10/24 14:51:29 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#define __MGR_PTCLIENTS_H__

#include <list>
using std::list;

#include <rw/db/connect.h>

#include <rw\tpslist.h>
#include <rw\thr\mutex.h>
#include <rw/thr/recursiv.h>

#include "dlldefs.h"
#include "mgr_point.h"
#include "msg_pdata.h"
#include "ptconnect.h"
#include "pt_dyn_dispatch.h"
#include "queue.h"
#include "rtdb.h"
#include "tbl_rawpthistory.h"

// Forward Declarations
class CtiSignalMsg;
class CtiConnectionManager;
class CtiPointRegistrationMsg;


class IM_EX_CTIVANGOGH CtiPointClientManager : public CtiPointManager
{
private:
   typedef map<LONG, CtiPointWPtr>     PointMap;
   typedef map< LONG, PointMap >       ConfigMgrPointMap;
   typedef ConfigMgrPointMap::iterator ConMgrPtMapIter;
   typedef PointMap::iterator          PtMapIter;

   ConfigMgrPointMap _conMgrPointMap;

public:

   typedef CtiPointManager Inherited;

   CtiPointClientManager();

   virtual ~CtiPointClientManager();
   virtual void refreshList(BOOL (*fn)(CtiPoint *,void*) = isPoint, void *d = NULL, LONG pntID = 0, LONG paoID = 0);

   void DumpList(void);
   virtual void DeleteList(void);
   virtual void removeSinglePoint(Inherited::ptr_type pTempCtiPoint);

   int InsertConnectionManager(CtiServer::ptr_type CM, const CtiPointRegistrationMsg &aReg, bool debugprint = false);
   int RemoveConnectionManager(CtiServer::ptr_type CM);

   CtiTime findNextNearestArchivalTime();
   void scanForArchival(const CtiTime &Now, CtiFIFOQueue<CtiTableRawPointHistory> &Que);

   void validateConnections();
   void storeDirtyRecords();
   void RefreshDynamicData(LONG id = 0);

   PointMap getRegistrationMap(LONG mgrID);

};

#endif                  // #ifndef __MGR_PTCLIENTS_H__
