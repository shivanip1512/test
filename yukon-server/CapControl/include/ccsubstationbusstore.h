#pragma once

#include "ccarea.h"
#include "ccsparea.h"
#include "ccid.h"
#include "ccstate.h"
#include "ccmessage.h"
#include "ccstatsobject.h"
#include "CapControlPointDataHandler.h"
#include "PointDataListener.h"
#include "ccutil.h"

#include "StrategyManager.h"
#include "StrategyLoader.h"
#include "ZoneManager.h"
#include "ZoneLoader.h"
#include "VoltageRegulatorManager.h"
#include "VoltageRegulatorLoader.h"
#include "AttributeService.h"
#include "DatabaseDaoFactory.h"

#include "EventLogEntry.h"

#include <boost/thread.hpp>

struct CcDbReloadInfo
{
   long objectId;
   unsigned char action:2;
   unsigned char objecttype:4;
   unsigned char filler:2;

   CcDbReloadInfo(long objectId, long action, long objectType) :
       objectId(objectId), action(action), objecttype(objectType)
   {};

   bool operator==(const CcDbReloadInfo& r) const
   {
      return this->objectId == r.objectId;
   }
   std::string typeOfAction() {
       if (action == 0)
       {
           return "Add";
       }
       else if (action == 1)
       {
           return "Delete";
       }
       else
       {
           return "Update";
       }
   }

};

class MaxKvarObject
{
public:
    MaxKvarObject()
    {
        value = 0;
        paoid = 0;
        timestamp = CtiTime();
    };
    MaxKvarObject(long v, long i, CtiTime t)
    {
        value = v;
        paoid = i;
        timestamp = t;
    };

    void setValue( long v )
    {
        value = v;
    };
    long getValue()
    {
        return value;
    };
    void setPaoId( long i )
    {
        paoid = i;
    };
    long getPaoId()
    {
        return paoid;
    };
    void setTimestamp( CtiTime t )
    {
        timestamp = t;
    };
    CtiTime getTimestamp()
    {
        return timestamp;
    };

private:
    long value;
    long paoid;
    CtiTime timestamp;
};

typedef std::map     < long, CtiCCSpecialPtr > PaoIdToSpecialAreaMap;
typedef std::multimap< long, CtiCCSpecialPtr > PointIdToSpecialAreaMultiMap;

typedef std::map     < long, CtiCCAreaPtr > PaoIdToAreaMap;
typedef std::multimap< long, CtiCCAreaPtr > PointIdToAreaMultiMap;

typedef std::map     < long, CtiCCSubstationUnqPtr > PaoIdToSubstationMap;  //  the owning container
typedef std::multimap< long, CtiCCSubstationPtr > PointIdToSubstationMultiMap;  //  these pointers are just non-owning references

typedef std::map     < long, CtiCCSubstationBusPtr > PaoIdToSubBusMap;
typedef std::multimap< long, CtiCCSubstationBusPtr > PointIdToSubBusMultiMap;

typedef std::map     < long, CtiCCFeederPtr > PaoIdToFeederMap;
typedef std::multimap< long, CtiCCFeederPtr > PointIdToFeederMultiMap;

typedef std::map     < long, CtiCCCapBankPtr > PaoIdToCapBankMap;
typedef std::multimap< long, CtiCCCapBankPtr > PointIdToCapBankMultiMap;

typedef std::map     < long, long > ChildToParentMap;
typedef std::multimap< long, long > ChildToParentMultiMap;

typedef std::map     < long, MaxKvarObject > CapBankIdToKvarMap;

typedef std::multimap< long, long > PaoIdToPointIdMultiMap;

typedef std::set<long> PaoIdSet;

class CtiCCSubstationBusStore : public PointDataListener
{
public:

    CtiCCSubstationBusStore();
    virtual ~CtiCCSubstationBusStore();

    typedef enum
    {
        PaobjectSubBusMap = 0,
        PaobjectFeederMap,
        PaobjectCapBankMap,
        PointIdSubBusMap,
        PointIdSubstationMap,
        PointIdFeederMap,
        PointIdCapBankMap,
        StrategyIdStrategyMap,
        FeederIdSubBusIdMap,
        CapBankIdSubBusIdMap,
        CapBankIdFeederIdMap

    } CtiCapControlMapType;

    bool testDatabaseConnectivity() const;
    void processAnyDBChangesOrResets( const CtiTime & rightNow );

    CtiCCSubstationBus_vec* getCCSubstationBuses();
    const CtiCCSubstation_vec& getCCSubstations();
    CtiCCState_vec* getCCCapBankStates();
    CtiCCArea_vec* getCCGeoAreas();
    CtiCCSpArea_vec* getCCSpecialAreas();

    static CtiCCSubstationBusStore* getInstance();
    static void deleteInstance();
    static void setInstance(CtiCCSubstationBusStore* substationBusStore);

    void dumpAllDynamicData();
    bool isValid();
    void setValid(bool valid);
    bool getReloadFromAMFMSystemFlag();
    void setReloadFromAMFMSystemFlag(bool reload);
    bool get2wayFlagUpdate();
    void set2wayFlagUpdate(bool flag);

    void verifySubBusAndFeedersStates();
    void resetDailyOperations();
    void calculateParentPowerFactor(long subBusId);

    bool UpdateBusVerificationFlagsInDB(CtiCCSubstationBus* bus);
    virtual bool UpdatePaoDisableFlagInDB(CapControlPao* pao, bool disableFlag, bool forceFullReload = false);
    bool UpdateCapBankOperationalStateInDB(CtiCCCapBank* capbank);
    bool UpdateCapBankInDB(CtiCCCapBank* capbank);
    bool UpdateFeederBankListInDB(CtiCCFeeder* feeder);
    virtual bool UpdateFeederSubAssignmentInDB(CtiCCSubstationBus* bus);

    static bool InsertCCEventLogInDB(const Cti::CapControl::EventLogEntry &msg);

    bool findSpecialAreaByPointID(long point_id, PointIdToSpecialAreaMultiMap::iterator &begin, PointIdToSpecialAreaMultiMap::iterator &end);
    bool findAreaByPointID       (long point_id, PointIdToAreaMultiMap::iterator        &begin, PointIdToAreaMultiMap::iterator        &end);
    bool findSubBusByPointID     (long point_id, PointIdToSubBusMultiMap::iterator      &begin, PointIdToSubBusMultiMap::iterator      &end);
    bool findSubstationByPointID (long point_id, PointIdToSubstationMultiMap::iterator  &begin, PointIdToSubstationMultiMap::iterator  &end);
    bool findFeederByPointID     (long point_id, PointIdToFeederMultiMap::iterator      &begin, PointIdToFeederMultiMap::iterator      &end);
    bool findCapBankByPointID    (long point_id, PointIdToCapBankMultiMap::iterator     &begin, PointIdToCapBankMultiMap::iterator     &end);
    std::pair<PaoIdToPointIdMultiMap::iterator, PaoIdToPointIdMultiMap::iterator> getSubsWithAltSubID(int altSubId);

    CtiCCSubstationPtr findSubstationByPAObjectID(long paobject_id);
    CtiCCAreaPtr findAreaByPAObjectID(long paobject_id);
    CtiCCSpecialPtr findSpecialAreaByPAObjectID(long paobject_id);
    CtiCCSubstationBusPtr findSubBusByPAObjectID(long paobject_id);
    CtiCCFeederPtr findFeederByPAObjectID(long paobject_id);
    CtiCCCapBankPtr findCapBankByPAObjectID(long paobject_id);
    CtiCCSubstationBusPtr findSubBusByCapBankID(long cap_bank_id);

    long findAreaIDbySubstationID(long substationId);
    bool findSpecialAreaIDbySubstationID(long substationId, ChildToParentMultiMap::iterator &begin, ChildToParentMultiMap::iterator &end);
    long findSubstationIDbySubBusID(long subBusId);
    long findSubBusIDbyFeederID(long feederId);
    long findSubBusIDbyCapBankID(long capBankId);
    long findFeederIDbyCapBankID(long capBankId);
    long findCapBankIDbyCbcID(long cbcId);

    void insertItemsIntoMap(int mapType, long* first, long* second);
    void removeItemsFromMap(int mapType, long first);

    void deleteCapBank(long capBankId);
    void deleteFeeder(long feederId);
    void deleteSubBus(long subBusId);
    void deleteSubstation(long substationId);
    void deleteArea(long areaId);
    void deleteSpecialArea(long areaId);

    void reloadCapBankFromDatabase(long capBankId, PaoIdToCapBankMap *paobject_capbank_map,
                                   PaoIdToFeederMap *paobject_feeder_map,
                                   PaoIdToSubBusMap *paobject_subbus_map,
                                   PointIdToCapBankMultiMap *pointid_capbank_map,
                                   ChildToParentMap *capbank_subbus_map,
                                   ChildToParentMap *capbank_feeder_map,
                                   ChildToParentMap *feeder_subbus_map,
                                   ChildToParentMap *cbc_capbank_map);
    void reloadMonitorPointsFromDatabase(long subBusId, PaoIdToCapBankMap *paobject_capbank_map,
                                   PaoIdToFeederMap *paobject_feeder_map,
                                   PaoIdToSubBusMap *paobject_subbus_map,
                                   PointIdToCapBankMultiMap *pointid_capbank_map,
                                   PointIdToSubBusMultiMap *pointid_subbus_map);
    void reloadFeederFromDatabase(long feederId,
                                  PaoIdToFeederMap *paobject_feeder_map,
                                  PaoIdToSubBusMap *paobject_subbus_map,
                                  PointIdToFeederMultiMap *pointid_feeder_map,
                                  ChildToParentMap *feeder_subbus_map);
    void reloadSubBusFromDatabase(long subBusId,
                                  PaoIdToSubBusMap *paobject_subbus_map,
                                  PaoIdToSubstationMap *paobject_substation_map,
                                  PointIdToSubBusMultiMap *pointid_subbus_map,
                                  PaoIdToPointIdMultiMap *altsub_sub_idmap,
                                  ChildToParentMap *subbus_substation_map,
                                  CtiCCSubstationBus_vec *cCSubstationBuses );
    void reloadSubstationFromDatabase(long substationId, PaoIdToSubstationMap *paobject_substation_map,
                                      PaoIdToAreaMap *paobject_area_map,
                                      PaoIdToSpecialAreaMap *paobject_specialarea_map,
                                      PointIdToSubstationMultiMap *pointid_station_map,
                                      ChildToParentMap *substation_area_map,
                                      ChildToParentMultiMap *substation_specialarea_map,
                                      CtiCCSubstation_vec *ccSubstations);
    void reloadAreaFromDatabase(long areaId,
                                  PaoIdToAreaMap *paobject_area_map,
                                  PointIdToAreaMultiMap *pointid_area_map,
                                  CtiCCArea_vec *ccGeoAreas);
    void reloadSpecialAreaFromDatabase(PaoIdToSpecialAreaMap *paobject_specialarea_map,
                                  PointIdToSpecialAreaMultiMap *pointid_specialarea_map,
                                  CtiCCSpArea_vec *ccSpecialAreas);
    void reloadTimeOfDayStrategyFromDatabase(long strategyId);
    bool reloadStrategyFromDatabase(long strategyId);
    void reloadMiscFromDatabase();
    void reloadMapOfBanksToControlByLikeDay(long subbusId, long feederId,
                                      ChildToParentMap *controlid_action_map,
                                      CtiTime &lastSendTime, int fallBackConstant);
    void reloadOperationStatsFromDatabase(long paoId, PaoIdToCapBankMap *paobject_capbank_map,
                                                        PaoIdToFeederMap *paobject_feeder_map,
                                                        PaoIdToSubBusMap *paobject_subbus_map,
                                                        PaoIdToSubstationMap *paobject_substation_map,
                                                        PaoIdToAreaMap *paobject_area_map,
                                                        PaoIdToSpecialAreaMap *paobject_specialarea_map );
    void reloadAndAssignHolidayStrategysFromDatabase(long strategyId);
    bool loadCapBankMonitorPoint(CtiCCMonitorPointPtr currentMonPoint, std::set< std::pair<long, int> >  &requiredPointResponses,
                                                        PaoIdToCapBankMap *paobject_capbank_map,
                                                        PaoIdToFeederMap *paobject_feeder_map,
                                                        PaoIdToSubBusMap *paobject_subbus_map,
                                                        PointIdToCapBankMultiMap *pointid_capbank_map);

    void assignStrategyAtBus(CtiCCSubstationBusPtr bus, long stratId);
    void assignStrategyAtFeeder(CtiCCFeederPtr feeder, long stratId);
    void assignStrategyToCCObject(Cti::RowReader& dbRdr, Cti::CapControl::CapControlType objectType);
    std::string getDbTableString(Cti::CapControl::CapControlType objectType);
    std::string getDbColumnString(Cti::CapControl::CapControlType objectType);

    template<class T>
    T findInMap(const long paoId, const std::map< long, T > *paobjectMap)
    {
        if (paobjectMap->find(paoId) == paobjectMap->end())
            return NULL;
        return paobjectMap->find(paoId)->second;
    }

    void setOperationSuccessPercents( CapControlPao & object,
                                      const CCStatsObject & userDef,
                                      const CCStatsObject & daily,
                                      const CCStatsObject & weekly,
                                      const CCStatsObject & monthly );

    void setConfirmationSuccessPercents( CapControlPao & object,
                                         const CCStatsObject & userDef,
                                         const CCStatsObject & daily,
                                         const CCStatsObject & weekly,
                                         const CCStatsObject & monthly );

    void incrementConfirmationPercentTotals( CapControlPao & object,
                                             CCStatsObject & userDef,
                                             CCStatsObject & daily,
                                             CCStatsObject & weekly,
                                             CCStatsObject & monthly );

    void incrementOperationPercentTotals( CapControlPao & object,
                                          CCStatsObject & userDef,
                                          CCStatsObject & daily,
                                          CCStatsObject & weekly,
                                          CCStatsObject & monthly );

    void cascadeAreaStrategySettings(CtiCCAreaBasePtr object);
    void locateOrphans(Cti::CapControl::PaoIdVector *orphanCaps, Cti::CapControl::PaoIdVector *orphanFeeders, PaoIdToCapBankMap paobject_capbank_map,
                       PaoIdToFeederMap paobject_feeder_map, ChildToParentMap capbank_feeder_map, ChildToParentMap feeder_subbus_map);
    bool isCapBankOrphan(long capBankId);
    bool isFeederOrphan(long feederId);
    void removeFromOrphanList(long ccId);


    std::list <CcDbReloadInfo> getDBReloadList() { return _reloadList; };
    CapBankList getUnsolicitedCapBankList() {return _unsolicitedCapBanks;};
    CapBankList getUnexpecteedUnsolicitedList() {return _unexpectedUnsolicited;};
    CapBankList getRejectedControlCapBankList() {return _rejectedCapBanks;};

    void insertDBReloadList(CcDbReloadInfo x);
    void checkDBReloadList();
    bool handleAreaDBChange(long reloadId, BYTE reloadAction, unsigned long &msgBitMask, unsigned long &msgSubsBitMask,
                           PaoIdSet &modifiedBusIdsSet,  PaoIdSet &modifiedStationIdsSet, CtiMultiMsg_vec &capMessages );
    void handleCapBankDBChange(long reloadId, BYTE reloadAction, unsigned long &msgBitMask, unsigned long &msgSubsBitMask,
                               PaoIdSet &modifiedBusIdsSet,  PaoIdSet &modifiedStationIdsSet, CtiMultiMsg_vec &capMessages );
    void handleFeederDBChange(long reloadId, BYTE reloadAction, unsigned long &msgBitMask, unsigned long &msgSubsBitMask,
                               PaoIdSet &modifiedBusIdsSet,  PaoIdSet &modifiedStationIdsSet, CtiMultiMsg_vec &capMessages );
    void handleSubBusDBChange(long reloadId, BYTE reloadAction, unsigned long &msgBitMask, unsigned long &msgSubsBitMask,
                               PaoIdSet &modifiedBusIdsSet,  PaoIdSet &modifiedStationIdsSet, CtiMultiMsg_vec &capMessages );
    void handleSubstationDBChange(long reloadId, BYTE reloadAction, unsigned long &msgBitMask, unsigned long &msgSubsBitMask,
                               PaoIdSet &modifiedBusIdsSet,  PaoIdSet &modifiedStationIdsSet, CtiMultiMsg_vec &capMessages );
    bool handleSpecialAreaDBChange(long reloadId, BYTE reloadAction, unsigned long &msgBitMask, unsigned long &msgSubsBitMask,
                               PaoIdSet &modifiedBusIdsSet,  PaoIdSet &modifiedStationIdsSet, CtiMultiMsg_vec &capMessages );
    void handleStrategyDBChange(long reloadId, BYTE reloadAction, unsigned long &msgBitMask, unsigned long &msgSubsBitMask,
                               PaoIdSet &modifiedBusIdsSet,  PaoIdSet &modifiedStationIdsSet, CtiMultiMsg_vec &capMessages );
    void handleZoneDBChange(long reloadId, BYTE reloadAction, unsigned long &msgBitMask, unsigned long &msgSubsBitMask,
                               PaoIdSet &modifiedBusIdsSet,  PaoIdSet &modifiedStationIdsSet, CtiMultiMsg_vec &capMessages );
    void handleVoltageRegulatorDBChange(long reloadId, BYTE reloadAction, unsigned long &msgBitMask, unsigned long &msgSubsBitMask,
                               PaoIdSet &modifiedBusIdsSet,  PaoIdSet &modifiedStationIdsSet, CtiMultiMsg_vec &capMessages );
    void handleMonitorPointDBChange(long reloadId, BYTE reloadAction, unsigned long &msgBitMask, unsigned long &msgSubsBitMask,
                             PaoIdSet &modifiedBusIdsSet,  PaoIdSet &modifiedStationIdsSet, CtiMultiMsg_vec &capMessages );

    void updateModifiedStationsAndBusesSets(Cti::CapControl::PaoIdVector stationIdList, unsigned long &msgBitMask, unsigned long &msgSubsBitMask,
                               PaoIdSet &modifiedSubsSet,  PaoIdSet &modifiedStationIdsSet);
    void initializeAllPeakTimeFlagsAndMonitorPoints(bool setTargetVarFlag = false);
    void createAndSendClientMessages( unsigned long &msgBitMask, unsigned long &msgSubsBitMask, PaoIdSet &modifiedBusIdsSet,
                                      PaoIdSet &modifiedStationIdsSet, CtiMultiMsg_vec &capMessages);
    CtiCCSubstationBus_set getAllSubBusesByIds(PaoIdSet modifiedBusIdsSet);
    void addVectorIdsToSet(const Cti::CapControl::PaoIdVector idVector, PaoIdSet &idSet);
    void updateSubstationObjectSet(long substationId, CtiCCSubstation_set &modifiedStationsSet);
    void updateAreaObjectSet(long areaId, CtiCCArea_set &modifiedAreasSet);
    void clearDBReloadList();
    void insertUnsolicitedCapBankList(CtiCCCapBankPtr x);
    void removeCapbankFromUnsolicitedCapBankList(CtiCCCapBankPtr x);
    void clearUnsolicitedCapBankList();
    void checkUnsolicitedList();
    void insertUnexpectedUnsolicitedList(CtiCCCapBankPtr x);
    void removeFromUnexpectedUnsolicitedList(CtiCCCapBankPtr x);
    void clearUnexpectedUnsolicitedList();
    void checkUnexpectedUnsolicitedList();
    void insertRejectedCapBankList(CtiCCCapBankPtr x);
    void removeCapbankFromRejectedCapBankList(CtiCCCapBankPtr x);
    void clearRejectedCapBankList();
    void checkRejectedList();

    void setLinkStatusPointId(long pointId);
    long getLinkStatusPointId(void);

    void setLinkStatusFlag(bool flag);
    bool getLinkStatusFlag(void);

    bool getVoltReductionSystemDisabled();
    void setVoltReductionSystemDisabled(bool disableFlag);
    void checkAndUpdateVoltReductionFlagsByBus(CtiCCSubstationBusPtr bus);

    const CtiTime& getLinkDropOutTime() const;
    void  setLinkDropOutTime(const CtiTime& dropOutTime);

    PaoIdToSubBusMap* getPAOSubMap();
    PaoIdToAreaMap & getPAOAreaMap();
    PaoIdToSubstationMap & getPAOStationMap();
    PaoIdToSpecialAreaMap & getPAOSpecialAreaMap();

    static const std::string CAP_CONTROL_DBCHANGE_MSG_SOURCE;
    static const std::string CAP_CONTROL_RELOAD_DBCHANGE_MSG_SOURCE;

    bool getStoreRecentlyReset();
    void setStoreRecentlyReset(bool flag);

    CtiCriticalSection & getMux() { return _storeMutex; };

    /* Relating to Max Kvar Cparm */
    bool addKVAROperation( long capbankId, long kvar );
    bool removeKVAROperation( long capbankId );

    void createOperationStatPointDataMsgs(CtiMultiMsg_vec& pointChanges, CtiCCCapBank* cap, CtiCCFeeder* feed, CtiCCSubstationBus* bus,
                                  CtiCCSubstationPtr station, CtiCCAreaPtr area, CtiCCSpecialPtr spArea);

    void setControlStatusAndIncrementFailCount(CtiMultiMsg_vec& pointChanges, long status, CtiCCCapBank* cap);
    void setControlStatusAndIncrementOpCount(CtiMultiMsg_vec& pointChanges, long status, CtiCCCapBank* cap,
                                             bool controlRecentlySentFlag);
    void getSubBusParentInfo(CtiCCSubstationBus* bus, long &spAreaId, long &areaId, long &stationId);
    void getFeederParentInfo(CtiCCFeeder* feeder, long &spAreaId, long &areaId, long &stationId);

    //For unit tests only
protected:
    CtiCCSubstationBusStore(Cti::Test::use_in_unit_tests_only&);

    void addAreaToPaoMap(CtiCCAreaPtr area);
    void addSubstationToPaoMap(CtiCCSubstationUnqPtr&& station, Cti::Test::use_in_unit_tests_only&);
    void addSubBusToPaoMap(CtiCCSubstationBusPtr bus);
    void addSubBusToAltBusMap(CtiCCSubstationBusPtr bus);
    void addFeederToPaoMap(CtiCCFeederPtr feeder);
    void addCapBankToCBCMap(CtiCCCapBankPtr capbank);

public:
    CtiCCSubstationBus_vec getSubBusesByAreaId(int areaId);
    CtiCCSubstationBus_vec getSubBusesBySpecialAreaId(int areaId);
    CtiCCSubstationBus_vec getSubBusesByStationId(int stationId);
    CtiCCSubstationBus_vec getSubBusesByFeederId(int feederId);
    CtiCCSubstationBus_vec getSubBusesByCapControlByIdAndType(int paoId, Cti::CapControl::CapControlType type);

    CapBankList getCapBanksByPaoId(int paoId);
    CapBankList getCapBanksByPaoIdAndType(int paoId, Cti::CapControl::CapControlType type);
    CtiCCSubstationBus_vec getAllSubBusesByIdAndType(int paoId, Cti::CapControl::CapControlType type);
    Cti::CapControl::CapControlType determineTypeById(int paoId);

    CapControlPointDataHandler& getPointDataHandler();
    bool handlePointDataByPaoId( const int paoId, const CtiPointDataMsg & message ) override;

    bool isAnyBankOpen(int paoId, Cti::CapControl::CapControlType type);
    bool isAnyBankClosed(int paoId, Cti::CapControl::CapControlType type);

    void executeAllStrategies() const;

    void startThreads();
    void stopThreads();

private:

    using DynamicDumpFn = void (CtiCCSubstationBusStore::*)(void);

    CtiCCSubstationBusStore::CtiCCSubstationBusStore(DynamicDumpFn);

    /* Relating to Max Kvar Cparm */
    long isKVARAvailable( long kvarNeeded );

    void reset();
    //bool CtiCCSubstationBusStore::findPointId(long pointId);
    void checkAMFMSystemForUpdates();
    void handleAMFMChanges(Cti::RowReader& rdr);
    void shutdown();

    void feederReconfigureM3IAMFM( std::string& capacitor_id_string, long circt_id_normal,
                                   std::string& circt_nam_normal, long circt_id_current,
                                   std::string& circt_name_current, CtiTime& switch_datetime,
                                   std::string& owner, std::string& capacitor_name, std::string& kvar_rating,
                                   std::string& cap_fs, std::string& cbc_model, std::string& serial_no,
                                   std::string& location, std::string& switching_seq, std::string& cap_disable_flag,
                                   std::string& cap_disable_type, std::string& inoperable_bad_order_equipnote,
                                   std::string& open_tag_note, std::string& cap_change_type );
    void capBankMovedToDifferentFeeder(CtiCCFeeder* oldFeeder, CtiCCCapBank* movedCapBank,
                                       long feederid, long capswitchingorder);
    void capBankDifferentOrderSameFeeder(CtiCCFeeder* currentFeeder, CtiCCCapBank* currentCapBank,
                                         long capswitchingorder);
    void capOutOfServiceM3IAMFM(long feederid, long capid, std::string& enableddisabled, std::string& fixedswitched);
    void feederOutOfServiceM3IAMFM(long feederid, std::string& fixedswitched, std::string& enableddisabled);

    bool updateDisableFlag(unsigned int paoid, bool isDisabled);

    void doResetThr();
    void doAMFMThr();
    void doOpStatsThr();
    bool deleteCapControlMaps();

    CtiCCSubstationBus_vec  *_ccSubstationBuses;
    CtiCCState_vec  *_ccCapBankStates;
    CtiCCSubstation_vec _ccSubstations;
    CtiCCArea_vec *_ccGeoAreas;
    CtiCCSpArea_vec *_ccSpecialAreas;

    boost::thread   _resetThr;
    boost::thread   _amfmThr;
    boost::thread   _opStatThr;

    bool _isvalid;
    bool _storeRecentlyReset;
    bool _reloadfromamfmsystemflag;
    CtiTime _lastdbreloadtime;
    CtiTime _lastindividualdbreloadtime;
    bool _2wayFlagUpdate;

    long _linkStatusPointId;
    bool _linkStatusFlag;
    CtiTime _linkDropOutTime;

    bool _voltReductionSystemDisabled;

    //  Can't override virtual functions in a destructor, so we have to manually override by function pointer.
    const DynamicDumpFn _dynamicDumpFn;
    void noOp();
    void dumpAllDynamicDataImpl();

    CapControlPointDataHandler _pointDataHandler;
    std::unique_ptr<AttributeService> _attributeService;

    //The singleton instance of CtiCCSubstationBusStore
    static CtiCCSubstationBusStore* _instance;

    //Possible static strings
    static const std::string m3iAMFMInterfaceString;

    static const std::string m3iAMFMChangeTypeCircuitOutOfService;
    static const std::string m3iAMFMChangeTypeCircuitReturnToService;
    static const std::string m3iAMFMChangeTypeCapOutOfService;
    static const std::string m3iAMFMChangeTypeCapReturnedToService;
    static const std::string m3iAMFMChangeTypeCircuitReconfigured;
    static const std::string m3iAMFMChangeTypeCircuitReconfiguredToNormal;
    //static const string M3IAMFMCapChangeTypeString;

    static const std::string m3iAMFMEnabledString;
    static const std::string m3iAMFMDisabledString;
    static const std::string m3iAMFMFixedString;
    static const std::string m3iAMFMSwitchedString;

    static const std::string m3iAMFMNullString;

    PaoIdToSpecialAreaMap _paobject_specialarea_map;
    PaoIdToAreaMap        _paobject_area_map;
    PaoIdToSubstationMap  _paobject_substation_map;
    PaoIdToSubBusMap      _paobject_subbus_map;
    PaoIdToFeederMap      _paobject_feeder_map;
    PaoIdToCapBankMap     _paobject_capbank_map;

    PointIdToSpecialAreaMultiMap _pointid_specialarea_map;
    PointIdToAreaMultiMap        _pointid_area_map;
    PointIdToSubstationMultiMap  _pointid_station_map;
    PointIdToSubBusMultiMap      _pointid_subbus_map;
    PointIdToFeederMultiMap      _pointid_feeder_map;
    PointIdToCapBankMultiMap     _pointid_capbank_map;

    std::unique_ptr<StrategyManager> _strategyManager;

    Cti::CapControl::ZoneManager _zoneManager;

protected:
    std::unique_ptr<Cti::CapControl::VoltageRegulatorManager> _voltageRegulatorManager;

    void setAttributeService( std::unique_ptr<AttributeService> service );
    void setStrategyManager ( std::unique_ptr<StrategyManager> strategyManager );

private:
    ChildToParentMultiMap _substation_specialarea_map;

    ChildToParentMap _substation_area_map;
    ChildToParentMap _subbus_substation_map;
    ChildToParentMap _feeder_subbus_map;
    ChildToParentMap _capbank_subbus_map;
    ChildToParentMap _capbank_feeder_map;
    ChildToParentMap _cbc_capbank_map;

    PaoIdToPointIdMultiMap _altsub_sub_idmap;

    CapBankList _unsolicitedCapBanks;
    CapBankList _unexpectedUnsolicited;
    CapBankList _rejectedCapBanks;

    std::list <CcDbReloadInfo> _reloadList;
    Cti::CapControl::PaoIdVector   _orphanedCapBanks;
    Cti::CapControl::PaoIdVector   _orphanedFeeders;

    CapBankIdToKvarMap _maxKvarMap;

    Cti::CapControl::DaoFactory::SharedPtr _daoFactory;

    mutable CtiCriticalSection _storeMutex;

public:

    Cti::CapControl::ZoneManager & getZoneManager();
    bool reloadZoneFromDatabase(const long zoneId);

    Cti::CapControl::VoltageRegulatorManager *getVoltageRegulatorManager();
    bool reloadVoltageRegulatorFromDatabase(const long regulatorId);

    static AttributeService &getAttributeService();



    using PointIdToPaoMultiMap = std::multimap< long, CapControlPao * >;

    PointIdToPaoMultiMap & getPointIDToPaoMultiMap() { return _pointID_to_pao; }

private:

    // opStats stuff

    enum StatDurationWindow
    {
        UserDef,
        Daily,
        Weekly,
        Monthly
    };

    using StatCache = std::map< std::pair< long, StatDurationWindow >, long >;

    // commStats

    struct CommStat
    {
        long    bankID,
                attemptCount,
                errorCount;
        StatDurationWindow  window;
    };

    using CommStatCache = std::vector< CommStat >;

    void reCalculateOperationStatsFromDatabase( StatCache & counts, StatCache & failures );
    void reCalculateConfirmationStatsFromDatabase( CommStatCache & cache );

    // these 6 functions need the store mux locked
    void resetAllOperationStats();
    void resetAllConfirmationStats();
    void populateOperationStats( const StatCache & counts, const StatCache & failures );
    void populateCommStats( const CommStatCache & cache );
    void reCalculateAllStats( );
    void createAllStatsPointDataMsgs(CtiMultiMsg_vec& pointChanges);

private:

    PointIdToPaoMultiMap    _pointID_to_pao;
};
