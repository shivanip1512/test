#pragma once

#include <rw/vstream.h>
#include <rw/thr/onlyptr.h>
#include <rw/thr/thread.h>
#include <rw/collect.h>

#include "observe.h"
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

#include "MsgCapControlEventLog.h"

typedef std::set<RWCollectable*> CtiMultiMsg_set;

struct CC_DBRELOAD_INFO
{
   LONG objectId;
   unsigned char action:2;
   unsigned char objecttype:4;
   unsigned char filler:2;
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

typedef std::map     < long, CtiCCSubstationPtr > PaoIdToSubstationMap;
typedef std::multimap< long, CtiCCSubstationPtr > PointIdToSubstationMultiMap;

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

    CtiCCSubstationBus_vec* getCCSubstationBuses(ULONG secondsFrom1901, bool checkReload = false);
    CtiCCSubstation_vec* getCCSubstations(ULONG secondsFrom1901, bool checkReload = false);
    CtiCCState_vec* getCCCapBankStates(ULONG secondsFrom1901);
    CtiCCArea_vec* getCCGeoAreas(ULONG secondsFrom1901, bool checkReload = false);
    CtiCCSpArea_vec* getCCSpecialAreas(ULONG secondsFrom1901, bool checkReload = false);

    static CtiCCSubstationBusStore* getInstance(bool startThreads=true);
    static void deleteInstance();
    static void setInstance(CtiCCSubstationBusStore* substationBusStore);

    void dumpAllDynamicData();
    BOOL isValid();
    void setValid(BOOL valid);
    BOOL getReregisterForPoints();
    void setReregisterForPoints(BOOL reregister);
    BOOL getReloadFromAMFMSystemFlag();
    void setReloadFromAMFMSystemFlag(BOOL reload);
    BOOL getWasSubBusDeletedFlag();
    void setWasSubBusDeletedFlag(BOOL wasDeleted);
    BOOL get2wayFlagUpdate();
    void set2wayFlagUpdate(BOOL flag);

    void verifySubBusAndFeedersStates();
    void resetDailyOperations();
    void calculateParentPowerFactor(LONG subBusId);

    bool UpdateBusVerificationFlagsInDB(CtiCCSubstationBus* bus);
    virtual bool UpdatePaoDisableFlagInDB(CapControlPao* pao, bool disableFlag);
    bool UpdateCapBankOperationalStateInDB(CtiCCCapBank* capbank);
    bool UpdateCapBankInDB(CtiCCCapBank* capbank);
    bool UpdateFeederBankListInDB(CtiCCFeeder* feeder);
    bool UpdateFeederSubAssignmentInDB(CtiCCSubstationBus* bus);

    bool InsertCCEventLogInDB(CtiCCEventLogMsg* msg);

    bool findSpecialAreaByPointID(long point_id, PointIdToSpecialAreaMultiMap::iterator &begin, PointIdToSpecialAreaMultiMap::iterator &end);
    bool findAreaByPointID       (long point_id, PointIdToAreaMultiMap::iterator        &begin, PointIdToAreaMultiMap::iterator        &end);
    bool findSubBusByPointID     (long point_id, PointIdToSubBusMultiMap::iterator      &begin, PointIdToSubBusMultiMap::iterator      &end);
    bool findSubstationByPointID (long point_id, PointIdToSubstationMultiMap::iterator  &begin, PointIdToSubstationMultiMap::iterator  &end);
    bool findFeederByPointID     (long point_id, PointIdToFeederMultiMap::iterator      &begin, PointIdToFeederMultiMap::iterator      &end);
    bool findCapBankByPointID    (long point_id, PointIdToCapBankMultiMap::iterator     &begin, PointIdToCapBankMultiMap::iterator     &end);
    int getNbrOfAreasWithPointID (long point_id);
    int getNbrOfSpecialAreasWithPointID(long point_id) ;
    int getNbrOfSubBusesWithPointID(long point_id);
    int getNbrOfSubstationsWithPointID(long point_id);
    int getNbrOfSubsWithAltSubID(long altSubId);
    std::pair<PaoIdToPointIdMultiMap::iterator, PaoIdToPointIdMultiMap::iterator> getSubsWithAltSubID(int altSubId);
    int getNbrOfFeedersWithPointID(long point_id);
    int getNbrOfCapBanksWithPointID(long point_id);

    CtiCCSubstationPtr findSubstationByPAObjectID(long paobject_id);
    CtiCCAreaPtr findAreaByPAObjectID(long paobject_id);
    CtiCCSpecialPtr findSpecialAreaByPAObjectID(long paobject_id);
    CtiCCSubstationBusPtr findSubBusByPAObjectID(long paobject_id);
    CtiCCFeederPtr findFeederByPAObjectID(long paobject_id);
    CtiCCCapBankPtr findCapBankByPAObjectID(long paobject_id);

    long findAreaIDbySubstationID(long substationId);
    bool findSpecialAreaIDbySubstationID(long substationId, ChildToParentMultiMap::iterator &begin, ChildToParentMultiMap::iterator &end);
    long findSubstationIDbySubBusID(long subBusId);
    long findSubBusIDbyFeederID(long feederId);
    long findSubBusIDbyCapBankID(long capBankId);
    long findFeederIDbyCapBankID(long capBankId);
    long findCapBankIDbyCbcID(long cbcId);

    long findSubIDbyAltSubID(long altSubId, int index);

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
    void reloadMonitorPointsFromDatabase(long capBankId, PaoIdToCapBankMap *paobject_capbank_map,
                                   PaoIdToFeederMap *paobject_feeder_map,
                                   PaoIdToSubBusMap *paobject_subbus_map,
                                   PointIdToCapBankMultiMap *pointid_capbank_map);
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
    void reloadStrategyParametersFromDatabase(long strategyId);

    void reCalculateOperationStatsFromDatabase( );
    void resetAllOperationStats();
    void resetAllConfirmationStats();
    void reCalculateConfirmationStatsFromDatabase( );
    void reCalculateAllStats( );
    void assignStrategyAtBus(CtiCCSubstationBusPtr bus, long stratId);
    void assignStrategyAtFeeder(CtiCCFeederPtr feeder, long stratId);
    void assignStrategyToCCObject(Cti::RowReader& dbRdr, Cti::CapControl::CapControlType objectType);
    string getDbTableString(Cti::CapControl::CapControlType objectType);
    string getDbColumnString(Cti::CapControl::CapControlType objectType);

    template<class T>
    void setOperationSuccessPercents(const T &object, CCStatsObject userDef, CCStatsObject daily, CCStatsObject weekly, CCStatsObject monthly)
    {
        object->getOperationStats().setUserDefOpSuccessPercent( userDef.getAverage() );
        object->getOperationStats().setUserDefOpCount( userDef.getOpCount() );
        object->getOperationStats().setUserDefConfFail( userDef.getFailCount() );
        object->getOperationStats().setDailyOpSuccessPercent(  daily.getAverage() );
        object->getOperationStats().setDailyOpCount( daily.getOpCount() );
        object->getOperationStats().setDailyConfFail( daily.getFailCount() );
        object->getOperationStats().setWeeklyOpSuccessPercent( weekly.getAverage() );
        object->getOperationStats().setWeeklyOpCount( weekly.getOpCount() );
        object->getOperationStats().setWeeklyConfFail( weekly.getFailCount() );
        object->getOperationStats().setMonthlyOpSuccessPercent( monthly.getAverage() );
        object->getOperationStats().setMonthlyOpCount( monthly.getOpCount() );
        object->getOperationStats().setMonthlyConfFail( monthly.getFailCount() );
    };

    template<class T>
    void setConfirmationSuccessPercents(const T &object, CCStatsObject userDef, CCStatsObject daily, CCStatsObject weekly, CCStatsObject monthly)
    {
        object->getConfirmationStats().setUserDefCommSuccessPercent( userDef.getAverage() );
        object->getConfirmationStats().setUserDefCommCount( userDef.getOpCount() );
        object->getConfirmationStats().setUserDefCommFail( userDef.getFailCount() );
        object->getConfirmationStats().setDailyCommSuccessPercent(  daily.getAverage() );
        object->getConfirmationStats().setDailyCommCount( daily.getOpCount() );
        object->getConfirmationStats().setDailyCommFail( daily.getFailCount() );
        object->getConfirmationStats().setWeeklyCommSuccessPercent( weekly.getAverage() );
        object->getConfirmationStats().setWeeklyCommCount( weekly.getOpCount() );
        object->getConfirmationStats().setWeeklyCommFail( weekly.getFailCount() );
        object->getConfirmationStats().setMonthlyCommSuccessPercent( monthly.getAverage() );
        object->getConfirmationStats().setMonthlyCommCount( monthly.getOpCount() );
        object->getConfirmationStats().setMonthlyCommFail( monthly.getFailCount() );

    };

    template<class T>
    void incrementConfirmationPercentTotals(const T &object, CCStatsObject &userDef, CCStatsObject &daily,
                                                          CCStatsObject &weekly, CCStatsObject &monthly)
    {

        if (object->getConfirmationStats().getUserDefCommCount() > 0)
        {
            userDef.incrementTotal( object->getConfirmationStats().getUserDefCommSuccessPercent());
            userDef.incrementOpCount(1);
        }
        if (object->getConfirmationStats().getDailyCommCount() > 0)
        {
            daily.incrementTotal(object->getConfirmationStats().getDailyCommSuccessPercent());
            daily.incrementOpCount(1);
        }
        if (object->getConfirmationStats().getWeeklyCommCount() > 0)
        {
            weekly.incrementTotal(object->getConfirmationStats().getWeeklyCommSuccessPercent());
            weekly.incrementOpCount(1);
        }
        if (object->getConfirmationStats().getMonthlyCommCount() > 0)
        {
            monthly.incrementTotal(object->getConfirmationStats().getMonthlyCommSuccessPercent());
            monthly.incrementOpCount(1);
        }
    };

    template<class T>
    void incrementOperationPercentTotals(const T &object, CCStatsObject &userDef, CCStatsObject &daily,
                                                          CCStatsObject &weekly, CCStatsObject &monthly)
    {
        if (object->getOperationStats().getUserDefOpCount() > 0)
        {
            userDef.incrementTotal(object->getOperationStats().getUserDefOpSuccessPercent());
            userDef.incrementOpCount(1);
        }
        if (object->getOperationStats().getDailyOpCount() > 0)
        {
            daily.incrementTotal(object->getOperationStats().getDailyOpSuccessPercent());
            daily.incrementOpCount(1);
        }
        if (object->getOperationStats().getWeeklyOpCount() > 0)
        {
            weekly.incrementTotal(object->getOperationStats().getWeeklyOpSuccessPercent());
            weekly.incrementOpCount(1);
        }
        if (object->getOperationStats().getMonthlyOpCount() > 0)
        {
            monthly.incrementTotal(object->getOperationStats().getMonthlyOpSuccessPercent());
            monthly.incrementOpCount(1);
        }
    };


    void cascadeAreaStrategySettings(CtiCCAreaBase* object);
    void locateOrphans(Cti::CapControl::PaoIdVector *orphanCaps, Cti::CapControl::PaoIdVector *orphanFeeders, PaoIdToCapBankMap paobject_capbank_map,
                       PaoIdToFeederMap paobject_feeder_map, ChildToParentMap capbank_feeder_map, ChildToParentMap feeder_subbus_map);
    BOOL isCapBankOrphan(long capBankId);
    BOOL isFeederOrphan(long feederId);
    void removeFromOrphanList(long ccId);


    std::list <CC_DBRELOAD_INFO> getDBReloadList() { return _reloadList; };
    CapBankList getUnsolicitedCapBankList() {return _unsolicitedCapBanks;};
    CapBankList getUnexpecteedUnsolicitedList() {return _unexpectedUnsolicited;};
    CapBankList getRejectedControlCapBankList() {return _rejectedCapBanks;};

    void insertDBReloadList(CC_DBRELOAD_INFO x);
    void checkDBReloadList();
    bool handleAreaDBChange(LONG reloadId, BYTE reloadAction, ULONG &msgBitMask, ULONG &msgSubsBitMask,
                           CtiMultiMsg_set &modifiedSubsSet,  CtiMultiMsg_set &modifiedStationsSet, CtiMultiMsg_vec &capMessages );
    void handleCapBankDBChange(LONG reloadId, BYTE reloadAction, ULONG &msgBitMask, ULONG &msgSubsBitMask,
                               CtiMultiMsg_set &modifiedSubsSet,  CtiMultiMsg_set &modifiedStationsSet, CtiMultiMsg_vec &capMessages );
    void handleFeederDBChange(LONG reloadId, BYTE reloadAction, ULONG &msgBitMask, ULONG &msgSubsBitMask,
                               CtiMultiMsg_set &modifiedSubsSet,  CtiMultiMsg_set &modifiedStationsSet, CtiMultiMsg_vec &capMessages );
    void handleSubBusDBChange(LONG reloadId, BYTE reloadAction, ULONG &msgBitMask, ULONG &msgSubsBitMask,
                               CtiMultiMsg_set &modifiedSubsSet,  CtiMultiMsg_set &modifiedStationsSet, CtiMultiMsg_vec &capMessages );
    void handleSubstationDBChange(LONG reloadId, BYTE reloadAction, ULONG &msgBitMask, ULONG &msgSubsBitMask,
                               CtiMultiMsg_set &modifiedSubsSet,  CtiMultiMsg_set &modifiedStationsSet, CtiMultiMsg_vec &capMessages );
    bool handleSpecialAreaDBChange(LONG reloadId, BYTE reloadAction, ULONG &msgBitMask, ULONG &msgSubsBitMask,
                               CtiMultiMsg_set &modifiedSubsSet,  CtiMultiMsg_set &modifiedStationsSet, CtiMultiMsg_vec &capMessages );
    void handleStrategyDBChange(LONG reloadId, BYTE reloadAction, ULONG &msgBitMask, ULONG &msgSubsBitMask,
                               CtiMultiMsg_set &modifiedSubsSet,  CtiMultiMsg_set &modifiedStationsSet, CtiMultiMsg_vec &capMessages );
    void handleZoneDBChange(LONG reloadId, BYTE reloadAction, ULONG &msgBitMask, ULONG &msgSubsBitMask,
                               CtiMultiMsg_set &modifiedSubsSet,  CtiMultiMsg_set &modifiedStationsSet, CtiMultiMsg_vec &capMessages );
    void handleVoltageRegulatorDBChange(LONG reloadId, BYTE reloadAction, ULONG &msgBitMask, ULONG &msgSubsBitMask,
                               CtiMultiMsg_set &modifiedSubsSet,  CtiMultiMsg_set &modifiedStationsSet, CtiMultiMsg_vec &capMessages );
    void updateModifiedStationsAndBusesSets(Cti::CapControl::PaoIdVector stationIdList, ULONG &msgBitMask, ULONG &msgSubsBitMask,
                               CtiMultiMsg_set &modifiedSubsSet,  CtiMultiMsg_set &modifiedStationsSet);
    void registerForAdditionalPoints(CtiMultiMsg_set &modifiedSubsSet,  CtiMultiMsg_set &modifiedStationsSet);
    void initializeAllPeakTimeFlagsAndMonitorPoints(BOOL setTargetVarFlag = FALSE);
    void createAndSendClientMessages( ULONG &msgBitMask, ULONG &msgSubsBitMask, CtiMultiMsg_set &modifiedSubsSet,
                                      CtiMultiMsg_set &modifiedStationsSet, CtiMultiMsg_vec &capMessages);
    void addSubstationObjectsToSet(Cti::CapControl::PaoIdVector subBusIds, CtiMultiMsg_set &modifiedSubsSet);
    void addSubBusObjectsToSet(Cti::CapControl::PaoIdVector subBusIds, CtiMultiMsg_set &modifiedSubsSet);
    void updateSubstationObjectSet(LONG substationId, CtiMultiMsg_set &modifiedStationsSet);
    void updateAreaObjectSet(LONG areaId, CtiMultiMsg_set &modifiedAreasSet);
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

    void setRegMask(LONG mask);
    LONG getRegMask(void);

    void setLinkStatusPointId(LONG pointId);
    LONG getLinkStatusPointId(void);

    void setLinkStatusFlag(BOOL flag);
    BOOL getLinkStatusFlag(void);

    BOOL getVoltReductionSystemDisabled();
    void setVoltReductionSystemDisabled(BOOL disableFlag);
    LONG getVoltDisabledCount();
    void setVoltDisabledCount(LONG value);
    void checkAndUpdateVoltReductionFlagsByBus(CtiCCSubstationBusPtr bus);

    const CtiTime& getLinkDropOutTime() const;
    void  setLinkDropOutTime(const CtiTime& dropOutTime);

    PaoIdToSubBusMap* getPAOSubMap();
    PaoIdToAreaMap* getPAOAreaMap();
    PaoIdToSubstationMap* getPAOStationMap();
    PaoIdToSpecialAreaMap* getPAOSpecialAreaMap();

    static const string CAP_CONTROL_DBCHANGE_MSG_SOURCE;
    static const string CAP_CONTROL_DBCHANGE_MSG_SOURCE2;
    static void sendUserQuit(void *who);
    static void periodicComplain( void *la );

    BOOL getStoreRecentlyReset();
    void setStoreRecentlyReset(BOOL flag);

    RWRecursiveLock<RWMutexLock> & getMux() { return _storeMutex; };

    /* Relating to Max Kvar Cparm */
    bool addKVAROperation( long capbankId, long kvar );
    bool removeKVAROperation( long capbankId );

    void resetAllOpStats();
    void createOperationStatPointDataMsgs(CtiMultiMsg_vec& pointChanges, CtiCCCapBank* cap, CtiCCFeeder* feed, CtiCCSubstationBus* bus,
                                  CtiCCSubstation* station, CtiCCArea* area, CtiCCSpecial* spArea);
    void createAllStatsPointDataMsgs(CtiMultiMsg_vec& pointChanges);

    void setControlStatusAndIncrementFailCount(CtiMultiMsg_vec& pointChanges, LONG status, CtiCCCapBank* cap);
    void setControlStatusAndIncrementOpCount(CtiMultiMsg_vec& pointChanges, LONG status, CtiCCCapBank* cap,
                                             BOOL controlRecentlySentFlag);
    void getSubBusParentInfo(CtiCCSubstationBus* bus, LONG &spAreaId, LONG &areaId, LONG &stationId);
    void getFeederParentInfo(CtiCCFeeder* feeder, LONG &spAreaId, LONG &areaId, LONG &stationId);

    //For unit tests only
protected:
    void addAreaToPaoMap(CtiCCAreaPtr area);
    void addSubstationToPaoMap(CtiCCSubstationPtr station);
    void addSubBusToPaoMap(CtiCCSubstationBusPtr bus);
    void addSubBusToAltBusMap(CtiCCSubstationBusPtr bus);
    void addFeederToPaoMap(CtiCCFeederPtr feeder);

public:
    CtiCCSubstationBus_vec getSubBusesByAreaId(int areaId);
    CtiCCSubstationBus_vec getSubBusesBySpecialAreaId(int areaId);
    CtiCCSubstationBus_vec getSubBusesByStationId(int stationId);
    CtiCCSubstationBus_vec getSubBusesByFeederId(int feederId);
    CtiCCSubstationBus_vec getSubBusesByCapControlByIdAndType(int paoId, Cti::CapControl::CapControlType type);

    CtiCCCapBankPtr getCapBankByPaoId(int paoId);
    CapBankList getCapBanksByPaoId(int paoId);
    CapBankList getCapBanksByPaoIdAndType(int paoId, Cti::CapControl::CapControlType type);
    Cti::CapControl::CapControlType determineTypeById(int paoId);

    CapControlPointDataHandler& getPointDataHandler();
    virtual bool handlePointDataByPaoId(int paoId, CtiPointDataMsg* message);

    bool isAnyBankOpen(int paoId, Cti::CapControl::CapControlType type);
    bool isAnyBankClosed(int paoId, Cti::CapControl::CapControlType type);

    //Setter for unit tests.
    void setAttributeService(AttributeService attributeService);

    void executeAllStrategies() const;

private:

    /* Relating to Max Kvar Cparm */
    long isKVARAvailable( long kvarNeeded );

    void startThreads();
    void stopThreads();

    void reset();
    //bool CtiCCSubstationBusStore::findPointId(long pointId);
    void checkAMFMSystemForUpdates();
    void handleAMFMChanges(Cti::RowReader& rdr);
    void shutdown();

    void feederReconfigureM3IAMFM( string& capacitor_id_string, LONG circt_id_normal,
                                   string& circt_nam_normal, LONG circt_id_current,
                                   string& circt_name_current, CtiTime& switch_datetime,
                                   string& owner, string& capacitor_name, string& kvar_rating,
                                   string& cap_fs, string& cbc_model, string& serial_no,
                                   string& location, string& switching_seq, string& cap_disable_flag,
                                   string& cap_disable_type, string& inoperable_bad_order_equipnote,
                                   string& open_tag_note, string& cap_change_type );
    void capBankMovedToDifferentFeeder(CtiCCFeeder* oldFeeder, CtiCCCapBank* movedCapBank,
                                       LONG feederid, LONG capswitchingorder);
    void capBankDifferentOrderSameFeeder(CtiCCFeeder* currentFeeder, CtiCCCapBank* currentCapBank,
                                         LONG capswitchingorder);
    void capOutOfServiceM3IAMFM(LONG feederid, LONG capid, string& enableddisabled, string& fixedswitched);
    void feederOutOfServiceM3IAMFM(LONG feederid, string& fixedswitched, string& enableddisabled);

    bool updateDisableFlag(unsigned int paoid, bool isDisabled);

    void doResetThr();
    void doAMFMThr();
    void doOpStatsThr();
    BOOL deleteCapControlMaps();

    CtiCCSubstationBus_vec  *_ccSubstationBuses;
    CtiCCState_vec  *_ccCapBankStates;
    CtiCCSubstation_vec *_ccSubstations;
    CtiCCArea_vec *_ccGeoAreas;
    CtiCCSpArea_vec *_ccSpecialAreas;

    RWThread _resetthr;
    RWThread _amfmthr;
    RWThread _opstatsthr;

    BOOL _isvalid;
    BOOL _storeRecentlyReset;
    BOOL _reregisterforpoints;
    LONG _regMask;
    BOOL _reloadfromamfmsystemflag;
    BOOL _wassubbusdeletedflag;
    CtiTime _lastdbreloadtime;
    CtiTime _lastindividualdbreloadtime;
    BOOL _2wayFlagUpdate;

    LONG _linkStatusPointId;
    BOOL _linkStatusFlag;
    CtiTime _linkDropOutTime;

    BOOL _voltReductionSystemDisabled;
    int  _voltDisabledCount;

    CapControlPointDataHandler _pointDataHandler;
    AttributeService _attributeService;

    //The singleton instance of CtiCCSubstationBusStore
    static CtiCCSubstationBusStore* _instance;

    //Possible static strings
    static const string m3iAMFMInterfaceString;

    static const string m3iAMFMChangeTypeCircuitOutOfService;
    static const string m3iAMFMChangeTypeCircuitReturnToService;
    static const string m3iAMFMChangeTypeCapOutOfService;
    static const string m3iAMFMChangeTypeCapReturnedToService;
    static const string m3iAMFMChangeTypeCircuitReconfigured;
    static const string m3iAMFMChangeTypeCircuitReconfiguredToNormal;
    //static const string M3IAMFMCapChangeTypeString;

    static const string m3iAMFMEnabledString;
    static const string m3iAMFMDisabledString;
    static const string m3iAMFMFixedString;
    static const string m3iAMFMSwitchedString;

    static const string m3iAMFMNullString;

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

    StrategyManager _strategyManager;

    Cti::CapControl::ZoneManager _zoneManager;

protected:
    boost::shared_ptr<Cti::CapControl::VoltageRegulatorManager> _voltageRegulatorManager;

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

    std::list <CC_DBRELOAD_INFO> _reloadList;
    Cti::CapControl::PaoIdVector   _orphanedCapBanks;
    Cti::CapControl::PaoIdVector   _orphanedFeeders;

    CapBankIdToKvarMap _maxKvarMap;

    Cti::CapControl::DaoFactory::SharedPtr _daoFactory;

    mutable RWRecursiveLock<RWMutexLock> _storeMutex;

public:

    Cti::CapControl::ZoneManager & getZoneManager();
    bool reloadZoneFromDatabase(const long zoneId);

    boost::shared_ptr<Cti::CapControl::VoltageRegulatorManager> getVoltageRegulatorManager();
    bool reloadVoltageRegulatorFromDatabase(const long regulatorId);

};
