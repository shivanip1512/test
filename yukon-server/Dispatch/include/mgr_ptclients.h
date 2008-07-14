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
* REVISION     :  $Revision: 1.15 $
* DATE         :  $Date: 2008/07/14 14:49:55 $
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
#include "tbl_pt_limit.h"
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
   typedef map<LONG, PointMap>         ConnectionMgrPointMap;
   typedef map<LONG, pair<double, double> > ReasonabilityLimitMap;
   typedef set<CtiTablePointLimit>     PointLimitSet;
   typedef ConnectionMgrPointMap::iterator ConMgrPtMapIter;
   typedef map<LONG, CtiPointConnection> PointConnectionMap;

   ConnectionMgrPointMap _conMgrPointMap;
   ReasonabilityLimitMap _reasonabilityLimits;
   PointLimitSet         _limits;
   PointConnectionMap    _pointConnectionMap;

   typedef CtiPointManager Inherited;

   void refreshReasonabilityLimits(LONG pntID, LONG paoID);
   void refreshPointLimits(LONG pntID, LONG paoID);
   void processPointDynamicData(LONG pntID);

protected:

   virtual void removePoint(Inherited::ptr_type pTempCtiPoint);

public:

   CtiPointClientManager();

   virtual ~CtiPointClientManager();
   virtual void refreshList(BOOL (*fn)(CtiPoint *,void*) = isPoint, void *d = NULL, LONG pntID = 0, LONG paoID = 0, CtiPointType_t pntType = InvalidPointType);

   void DumpList(void);
   virtual void DeleteList(void);

   int InsertConnectionManager(CtiServer::ptr_type CM, const CtiPointRegistrationMsg &aReg, bool debugprint = false);
   int RemoveConnectionManager(CtiServer::ptr_type CM);
   bool pointHasConnection(LONG pointID, const CtiServer::ptr_type &Conn);

   CtiTime findNextNearestArchivalTime();
   void scanForArchival(const CtiTime &Now, CtiFIFOQueue<CtiTableRawPointHistory> &Que);

   void validateConnections();
   void storeDirtyRecords();
   void RefreshDynamicData(LONG id = 0);

   bool hasReasonabilityLimits(LONG pointid);
   pair<DOUBLE, DOUBLE> getReasonabilityLimits(LONG pointID);
   CtiTablePointLimit getPointLimit(LONG pointID, LONG limitNum);

   PointMap getRegistrationMap(LONG mgrID);

};

#endif                  // #ifndef __MGR_PTCLIENTS_H__
