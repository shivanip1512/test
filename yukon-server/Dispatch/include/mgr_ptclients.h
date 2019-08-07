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
class CtiPointRegistrationMsg;

class CtiPointClientManager : public CtiPointManager
{
public:
    struct ReasonabilityLimitStruct
    {
        double highLimit;
        double lowLimit;
    };
private:
   typedef std::map<long, std::set<long>>           ConnectionMgrPointMap;
   typedef std::map<LONG, ReasonabilityLimitStruct> ReasonabilityLimitMap;
   typedef std::set<CtiTablePointLimit>             PointLimitSet;
   typedef std::set<CtiTablePointAlarming>          PointAlarmingSet;
   typedef std::multimap<long, CtiTablePointPropertySPtr> PointPropertyMap;
   typedef std::map<long, CtiDynamicPointDispatchSPtr>  DynamicPointDispatchMap;
   typedef DynamicPointDispatchMap::const_iterator      DynamicPointDispatchIterator;
   typedef std::map<long, CtiPointConnection>           PointConnectionMap;

   struct ParameterizedIdQuery
   {
      std::string sql;
      std::vector<long> pointIds;
   };

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
   void processPointDynamicData   (LONG pntID, const std::set<long> &ids = std::set<long>());
   void refreshArchivalList       (LONG pntID, LONG paoID = 0, const std::set<long> &ids = std::set<long>());

   void refreshDynamicDataForSinglePoint(const long pointId);
   void refreshDynamicDataForPointSet   (const std::set<long> &ids);
   void refreshDynamicDataForAllPoints  ();

   void loadDynamicPoint(Cti::Database::DatabaseReader &rdr);

   void executeDynamicDataQueries(const std::vector<ParameterizedIdQuery> &queries);

   auto generatePointDataSqlStatements(const std::vector<int>& ids)->std::vector<ParameterizedIdQuery>;

   using DynamicPointDispatchList = std::vector<CtiDynamicPointDispatchSPtr>;
   DynamicPointDispatchList getDirtyRecordList();
   void writeRecordsToDB(const DynamicPointDispatchList& records);
   void removeOldDynamicData();

   virtual CtiDynamicPointDispatchSPtr getDynamic(unsigned long pointID) const;

   void removePoint(long point, bool isExpiration = false);

protected:  //  for unit test access

   // Used for unit tests
   void refreshPoints( std::set<long> &pointIdsFound, Cti::RowReader& rdr );
   std::set<long> getRegistrationSet(LONG mgrID, Cti::Test::use_in_unit_tests_only&);

   std::vector<ParameterizedIdQuery> generateSqlStatements(const std::set<long> &pointIds);

   void addAlarming(CtiTablePointAlarming &table);
   void removeAlarming(unsigned long pointID);

public:

   virtual ~CtiPointClientManager();
   virtual std::set<long> refreshList(LONG pntID = 0, LONG paoID = 0, CtiPointType_t pntType = InvalidPointType);
   virtual void refreshListByPointIDs(const std::set<long> &ids);

   void updatePoints(LONG pntID, LONG paoID, CtiPointType_t pntType = InvalidPointType);
   void loadAllStaticData();

   virtual Inherited::ptr_type getPoint(LONG Pt, LONG pao = 0);
   virtual Inherited::ptr_type getCachedPoint(LONG Pt);
   bool                        isPointLoaded(LONG Pt);
   CtiPointManager::ptr_type   getOffsetTypeEqual(LONG pao, INT offset, CtiPointType_t type);
   CtiPointManager::ptr_type   getControlOffsetEqual(LONG pao, INT offset);

   struct PointDataResults
   {
       std::vector<std::unique_ptr<CtiPointDataMsg>> pointData;
       std::vector<int> missingIds;
   };

   PointDataResults getCurrentPointData(const std::vector<int>& pointIds);

   void DeleteList(void);
   virtual void expire (long pid);
   virtual void erase  (long pid);
   //virtual void refresh(long pid);

   enum class DebugPrint
   {
       False,
       True
   };

   //  Returns a set of all points currently registered on the specified ConnectionManager.
   std::set<long> InsertConnectionManager(CtiServer::ptr_type &CM, const CtiPointRegistrationMsg &aReg, DebugPrint debugprint = DebugPrint::False);

   //  Remove all points from the specified ConnectionManager.
   void removePointsFromConnectionManager( CtiServer::ptr_type &CM, DebugPrint debugprint = DebugPrint::False );

   bool pointHasConnection(LONG pointID, const CtiServer::ptr_type &Conn);

   CtiTime findNextNearestArchivalTime();
   std::vector<std::unique_ptr<CtiTableRawPointHistory>> scanForArchival(const CtiTime &Now);

   void storeDirtyRecords();

   ReasonabilityLimitStruct getReasonabilityLimits(const CtiPointBase &point) const;
   CtiTablePointLimit       getPointLimit(const CtiPointBase &point, LONG limitNum) const;  //  is copying the table cheap/fast enough?
   CtiTablePointAlarming    getAlarming  (const CtiPointBase &point) const;          //    if not, we'll need to return smart pointers
   virtual CtiDynamicPointDispatchSPtr getDynamic(const CtiPointBase &point) const;
   bool                     setDynamic   (long pointID, CtiDynamicPointDispatchSPtr &point);
   int  getProperty (LONG point, unsigned int property) const;
   bool hasProperty (LONG point, unsigned int property) const;
   void getPointsWithProperty(unsigned int propertyID, std::vector<long> &points);
   //  I have the feeling that dynamic data and point properties should
   //    be smart pointers, since we're playing fast and loose with
   //    deletions and reloads
};

