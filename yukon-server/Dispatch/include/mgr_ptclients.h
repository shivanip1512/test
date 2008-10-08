/*-----------------------------------------------------------------------------*
*
* File:   mgr_ptclients
*
* mgr_ptclients.h      7/7/99
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/INCLUDE/mgr_ptclients.h-arc  $
* REVISION     :  $Revision: 1.23 $
* DATE         :  $Date: 2008/10/08 20:44:58 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __MGR_PTCLIENTS_H__
#define __MGR_PTCLIENTS_H__
#pragma warning( disable : 4786)

#include <list>

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
public:
    struct ReasonabilityLimitStruct
    {
        double highLimit;
        double lowLimit;
    };
private:
   typedef std::map<LONG, WeakPointMap>             ConnectionMgrPointMap;
   typedef std::map<LONG, ReasonabilityLimitStruct> ReasonabilityLimitMap;
   typedef std::set<CtiTablePointLimit>             PointLimitSet;
   typedef std::set<CtiTablePointAlarming>          PointAlarmingSet;
   typedef std::multimap<long, CtiTablePointProperty *> PointPropertyMap;
   typedef std::map<long, CtiDynamicPointDispatch *>    DynamicPointDispatchMap;
   typedef std::map<long, CtiDynamicPointDispatch *>::iterator DynamicPointDispatchIterator;
   typedef std::map<long, CtiPointConnection>           PointConnectionMap;

   ConnectionMgrPointMap    _conMgrPointMap;
   ReasonabilityLimitMap    _reasonabilityLimits;
   PointLimitSet            _limits;
   PointAlarmingSet         _alarming;
   PointPropertyMap         _properties;
   PointConnectionMap       _pointConnectionMap;

   // Store for the dynamic data on a point. It reflects dynamic database data.
   // This should be is removed from on point deletion but not expiration.
   DynamicPointDispatchMap  _dynamic; 

   typedef CtiPointManager Inherited;

   void refreshAlarming           (LONG pntID, LONG paoID, const std::set<long> &ids = std::set<long>());
   void refreshProperties         (LONG pntID, LONG paoID, const std::set<long> &ids = std::set<long>());
   void refreshReasonabilityLimits(LONG pntID, LONG paoID, const std::set<long> &ids = std::set<long>());
   void refreshPointLimits        (LONG pntID, LONG paoID, const std::set<long> &ids = std::set<long>());
   void RefreshDynamicData        (LONG pntID = 0,         const std::set<long> &ids = std::set<long>());
   void processPointDynamicData   (LONG pntID, LONG paoID, const std::set<long> &ids = std::set<long>());

   void getDirtyRecordList(list<CtiTablePointDispatch> &updateList);
   void writeRecordsToDB  (list<CtiTablePointDispatch> &updateList);
   void removeOldDynamicData();

protected:

   virtual void removePoint(Inherited::ptr_type pTempCtiPoint, bool isExpiration = false);

public:

   CtiPointClientManager();

   virtual ~CtiPointClientManager();
   virtual void refreshList(LONG pntID = 0, LONG paoID = 0, CtiPointType_t pntType = InvalidPointType);
   virtual void refreshListByPointIDs(const std::set<long> &ids);

   virtual Inherited::ptr_type getEqual(LONG Pt);
   bool                      checkEqual(LONG Pt);

   void DumpList(void);
   virtual void DeleteList(void);

   int InsertConnectionManager(CtiServer::ptr_type CM, const CtiPointRegistrationMsg &aReg, bool debugprint = false);
   int RemoveConnectionManager(CtiServer::ptr_type CM);
   bool pointHasConnection(LONG pointID, const CtiServer::ptr_type &Conn);

   CtiTime findNextNearestArchivalTime();
   void scanForArchival(const CtiTime &Now, CtiFIFOQueue<CtiTableRawPointHistory> &Que);

   void validateConnections();
   void storeDirtyRecords();

   bool hasReasonabilityLimits(LONG pointid);
   ReasonabilityLimitStruct getReasonabilityLimits(LONG pointID);
   CtiTablePointLimit       getPointLimit(LONG pointID, LONG limitNum);  //  is copying the table cheap/fast enough?
   CtiTablePointAlarming    getAlarming  (LONG pointID);                 //    if not, we'll need to return smart pointers
   CtiDynamicPointDispatch *getDynamic   (LONG pointID);    //  I have the feeling that dynamic data and point properties should
   bool                     setDynamic   (long pointID, CtiDynamicPointDispatch *point);
   int  getProperty (LONG pointID, unsigned int property);  //    be smart pointers, since we're playing fast and loose with
   bool hasProperty (LONG pointID, unsigned int property);  //    deletions and reloads

   WeakPointMap getRegistrationMap(LONG mgrID);

};

#endif                  // #ifndef __MGR_PTCLIENTS_H__
