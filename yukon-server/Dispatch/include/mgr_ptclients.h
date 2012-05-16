#pragma once

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
#include "tbl_pt_property.h"

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
   typedef std::multimap<long, CtiTablePointPropertySPtr> PointPropertyMap;
   typedef std::map<long, CtiDynamicPointDispatchSPtr>  DynamicPointDispatchMap;
   typedef DynamicPointDispatchMap::const_iterator      DynamicPointDispatchIterator;
   typedef std::map<long, CtiPointConnection>           PointConnectionMap;

   // The weak pointers contained in this map are no longer guaranteed to exist.
   // They should be assumed to exist only immediately after being entered, then never used again!
   ConnectionMgrPointMap    _conMgrPointMap;
   ReasonabilityLimitMap    _reasonabilityLimits;
   PointLimitSet            _limits;
   PointAlarmingSet         _alarming;
   //Properties are always loaded for all points
   //Properties should be reserved for things that are not frequently used and need to be always loaded
   PointPropertyMap         _properties;
   PointConnectionMap       _pointConnectionMap;

   typedef std::pair<PointPropertyMap::const_iterator, PointPropertyMap::const_iterator> PointPropertyRange;

   // Store for the dynamic data on a point. It reflects dynamic database data.
   // This should be is removed from on point deletion but not expiration.
   DynamicPointDispatchMap  _dynamic;

   typedef CtiPointManager Inherited;

   void refreshAlarming           (LONG pntID, LONG paoID = 0, const std::set<long> &ids = std::set<long>());
   void refreshProperties         (LONG pntID, LONG paoID = 0, const std::set<long> &ids = std::set<long>());
   void refreshReasonabilityLimits(LONG pntID, LONG paoID = 0, const std::set<long> &ids = std::set<long>());
   void refreshPointLimits        (LONG pntID, LONG paoID = 0, const std::set<long> &ids = std::set<long>());
   void RefreshDynamicData        (LONG pntID = 0,             const std::set<long> &ids = std::set<long>());
   void processPointDynamicData   (LONG pntID, const std::set<long> &ids = std::set<long>());
   void refreshArchivalList       (LONG pntID, LONG paoID = 0, const std::set<long> &ids = std::set<long>());

   void getDirtyRecordList(std::list<CtiTablePointDispatch> &updateList);
   void writeRecordsToDB  (std::list<CtiTablePointDispatch> &updateList);
   void removeOldDynamicData();

   CtiDynamicPointDispatchSPtr getDynamic(unsigned long pointID) const;

   void removePoint(long point, bool isExpiration = false);

   void addAlarming(CtiTablePointAlarming &table);
   void removeAlarming(unsigned long pointID);

   friend class Test_CtiPointClientManager;

public:

   virtual ~CtiPointClientManager();
   virtual std::set<long> refreshList(LONG pntID = 0, LONG paoID = 0, CtiPointType_t pntType = InvalidPointType);
   virtual void refreshListByPointIDs(const std::set<long> &ids);

   void updatePoints(LONG pntID, LONG paoID, CtiPointType_t pntType = InvalidPointType);
   void loadAllStaticData();

   virtual Inherited::ptr_type getPoint(LONG Pt, LONG pao = 0);
   Inherited::ptr_type         getCachedPoint(LONG Pt);
   bool                        isPointLoaded(LONG Pt);
   CtiPointManager::ptr_type   getOffsetTypeEqual(LONG pao, INT offset, CtiPointType_t type);
   CtiPointManager::ptr_type   getControlOffsetEqual(LONG pao, INT offset);

   void DeleteList(void);
   virtual void expire (long pid);
   virtual void erase  (long pid);
   //virtual void refresh(long pid);

   int InsertConnectionManager(CtiServer::ptr_type CM, const CtiPointRegistrationMsg &aReg, bool debugprint = false);
   int RemoveConnectionManager(CtiServer::ptr_type CM);
   bool pointHasConnection(LONG pointID, const CtiServer::ptr_type &Conn);

   CtiTime findNextNearestArchivalTime();
   void scanForArchival(const CtiTime &Now, CtiFIFOQueue<CtiTableRawPointHistory> &Que);

   void validateConnections();
   void storeDirtyRecords();

   bool hasReasonabilityLimits(CtiPointSPtr point);
   ReasonabilityLimitStruct getReasonabilityLimits(CtiPointSPtr point) const;
   CtiTablePointLimit       getPointLimit(CtiPointSPtr point, LONG limitNum) const;  //  is copying the table cheap/fast enough?
   CtiTablePointAlarming    getAlarming  (CtiPointSPtr point) const;                 //    if not, we'll need to return smart pointers
   CtiDynamicPointDispatchSPtr getDynamic   (CtiPointSPtr point) const;
   bool                     setDynamic   (long pointID, CtiDynamicPointDispatchSPtr &point);
   int  getProperty (LONG point, unsigned int property) const;
   bool hasProperty (LONG point, unsigned int property) const;
   void getPointsWithProperty(unsigned int propertyID, std::vector<long> &points);
   //  I have the feeling that dynamic data and point properties should
   //    be smart pointers, since we're playing fast and loose with
   //    deletions and reloads
   WeakPointMap getRegistrationMap(LONG mgrID);

};

class Test_CtiPointClientManager : public CtiPointClientManager
{
private:
    typedef CtiPointClientManager Inherited;
public:
    void addAlarming(CtiTablePointAlarming &table) { Inherited::addAlarming(table); }
    void removeAlarming(unsigned long pointID)     { Inherited::removeAlarming(pointID); }
};
