/*---------------------------------------------------------------------------
        Filename:  ccsubstationbusstore.h

        Programmer:  Josh Wolberg

        Description:    Header file for CtiCCSubstationBusStore.
                        CtiCCSubstationBusStore maintains a pool of
                        CtiCCSubstationBus handles.


        Initial Date:  8/27/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/

#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTICCSUBSTATIONBUSSTORE_H
#define CTICCSUBSTATIONBUSSTORE_H


#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/onlyptr.h>
#include <rw/thr/thread.h>
#include <rw/collect.h>
#include <rw/collstr.h>

#include "observe.h"
#include "ccarea.h"
#include "ccsparea.h"
#include "ccid.h"
#include "ccstate.h"
#include "ccmessage.h"
#include "ccstatsobject.h"
#include "LoadTapChanger.h"

#include "ccutil.h"

using std::multimap;
typedef std::set<RWCollectable*> CtiMultiMsg_set;
using std::pair;

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


class CtiCCSubstationBusStore
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

    bool UpdateAreaDisableFlagInDB(CtiCCArea* bus);
    bool UpdateSpecialAreaDisableFlagInDB(CtiCCSpecial* area);
    bool UpdateBusDisableFlagInDB(CtiCCSubstationBus* bus);
    bool UpdateSubstationDisableFlagInDB(CtiCCSubstation* station);
    bool UpdateBusVerificationFlagsInDB(CtiCCSubstationBus* bus);
    bool UpdateFeederDisableFlagInDB(CtiCCFeeder* feeder);
    virtual bool UpdateCapBankDisableFlagInDB(CtiCCCapBank* capbank);
    bool UpdateCapBankOperationalStateInDB(CtiCCCapBank* capbank);
    bool UpdateCapBankInDB(CtiCCCapBank* capbank);
    bool UpdateFeederBankListInDB(CtiCCFeeder* feeder);
    bool UpdateFeederSubAssignmentInDB(CtiCCSubstationBus* bus);

    bool InsertCCEventLogInDB(CtiCCEventLogMsg* msg);

    bool findSpecialAreaByPointID(long point_id, std::multimap< long, CtiCCSpecialPtr >::iterator       &begin, std::multimap< long, CtiCCSpecialPtr >::iterator       &end);
    bool findAreaByPointID       (long point_id, std::multimap< long, CtiCCAreaPtr >::iterator          &begin, std::multimap< long, CtiCCAreaPtr >::iterator          &end);
    bool findSubBusByPointID     (long point_id, std::multimap< long, CtiCCSubstationBusPtr >::iterator &begin, std::multimap< long, CtiCCSubstationBusPtr >::iterator &end);
    bool findSubstationByPointID (long point_id, std::multimap< long, CtiCCSubstationPtr >::iterator    &begin, std::multimap< long, CtiCCSubstationPtr >::iterator    &end);
    bool findFeederByPointID     (long point_id, std::multimap< long, CtiCCFeederPtr >::iterator        &begin, std::multimap< long, CtiCCFeederPtr >::iterator        &end);
    bool findCapBankByPointID    (long point_id, std::multimap< long, CtiCCCapBankPtr >::iterator       &begin, std::multimap< long, CtiCCCapBankPtr >::iterator       &end);
    int getNbrOfAreasWithPointID (long point_id);
    int getNbrOfSpecialAreasWithPointID(long point_id) ;
    int getNbrOfSubBusesWithPointID(long point_id);
    int getNbrOfSubstationsWithPointID(long point_id);
    int getNbrOfSubsWithAltSubID(long altSubId);
    pair<multimap<long,long>::iterator,multimap<long,long>::iterator> getSubsWithAltSubID(long altSubId);
    int getNbrOfFeedersWithPointID(long point_id);
    int getNbrOfCapBanksWithPointID(long point_id);

    CtiCCSubstationPtr findSubstationByPAObjectID(long paobject_id);
    CtiCCAreaPtr findAreaByPAObjectID(long paobject_id);
    CtiCCSpecialPtr findSpecialAreaByPAObjectID(long paobject_id);
    CtiCCSubstationBusPtr findSubBusByPAObjectID(long paobject_id);
    CtiCCFeederPtr findFeederByPAObjectID(long paobject_id);
    CtiCCCapBankPtr findCapBankByPAObjectID(long paobject_id);
    CtiCCStrategyPtr findStrategyByStrategyID(long strategy_id);
    LoadTapChangerPtr findLtcById(long ltcId);

    long findAreaIDbySubstationID(long substationId);
    long findSpecialAreaIDbySubstationID(long substationId);
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
    void deleteStrategy(long strategyId);
    void deleteLtcById(long ltcId);

    void reloadCapBankFromDatabase(long capBankId, map< long, CtiCCCapBankPtr > *paobject_capbank_map,
                                   map< long, CtiCCFeederPtr > *paobject_feeder_map,
                                   map< long, CtiCCSubstationBusPtr > *paobject_subbus_map,
                                   multimap< long, CtiCCCapBankPtr > *pointid_capbank_map,
                                   map< long, long> *capbank_subbus_map,
                                   map< long, long> *capbank_feeder_map,
                                   map< long, long> *feeder_subbus_map,
                                   map< long, long> *cbc_capbank_map);
    void reloadMonitorPointsFromDatabase(long capBankId, map< long, CtiCCCapBankPtr > *paobject_capbank_map,
                                   map< long, CtiCCFeederPtr > *paobject_feeder_map,
                                   map< long, CtiCCSubstationBusPtr > *paobject_subbus_map,
                                   multimap< long, CtiCCCapBankPtr > *pointid_capbank_map);
    void reloadFeederFromDatabase(long feederId, map< long, CtiCCStrategyPtr > *strategy_map,
                                  map< long, CtiCCFeederPtr > *paobject_feeder_map,
                                  map< long, CtiCCSubstationBusPtr > *paobject_subbus_map,
                                  multimap< long, CtiCCFeederPtr > *pointid_feeder_map,
                                  map< long, long> *feeder_subbus_map);
    void reloadSubBusFromDatabase(long subBusId, map< long, CtiCCStrategyPtr > *strategy_map,
                                  map< long, CtiCCSubstationBusPtr > *paobject_subbus_map,
                                  map< long, CtiCCSubstationPtr > *paobject_substation_map,
                                  multimap< long, CtiCCSubstationBusPtr > *pointid_subbus_map,
                                  multimap<long, long> *altsub_sub_idmap,
                                  map< long, long> *subbus_substation_map,
                                  CtiCCSubstationBus_vec *cCSubstationBuses );
    void reloadSubstationFromDatabase(long substationId, map< long, CtiCCSubstationPtr > *paobject_substation_map,
                                      map <long, CtiCCAreaPtr> *paobject_area_map,
                                      map <long, CtiCCSpecialPtr> *paobject_specialarea_map,
                                      multimap< long, CtiCCSubstationPtr > *pointid_station_map,
                                      map< long, long> *substation_area_map,
                                      map< long, long> *substation_specialarea_map,
                                      CtiCCSubstation_vec *ccSubstations);
    void reloadAreaFromDatabase(long areaId, map< long, CtiCCStrategyPtr > *strategy_map,
                                  map< long, CtiCCAreaPtr > *paobject_area_map,
                                  multimap< long, CtiCCAreaPtr > *pointid_area_map,
                                  CtiCCArea_vec *ccGeoAreas);
    void reloadSpecialAreaFromDatabase(long areaId, map< long, CtiCCStrategyPtr > *strategy_map,
                                  map< long, CtiCCSpecialPtr > *paobject_specialarea_map,
                                  multimap< long, CtiCCSpecialPtr > *pointid_specialarea_map,
                                  CtiCCSpArea_vec *ccSpecialAreas);
    void reloadTimeOfDayStrategyFromDatabase(long strategyId, map< long, CtiCCStrategyPtr > *strategy_map);
    void reloadStrategyFromDatabase(long strategyId, map< long, CtiCCStrategyPtr > *strategy_map);
    void reloadMiscFromDatabase();
    void reloadMapOfBanksToControlByLikeDay(long subbusId, long feederId,
                                      map< long, long> *controlid_action_map,
                                      CtiTime &lastSendTime, int fallBackConstant);
    void reloadOperationStatsFromDatabase(RWDBConnection& conn, long paoId, map< long, CtiCCCapBankPtr > *paobject_capbank_map,
                                                        map< long, CtiCCFeederPtr > *paobject_feeder_map,
                                                        map< long, CtiCCSubstationBusPtr > *paobject_subbus_map,
                                                        map< long, CtiCCSubstationPtr > *paobject_substation_map,
                                                        map< long, CtiCCAreaPtr > *paobject_area_map,
                                                        map< long, CtiCCSpecialPtr > *paobject_specialarea_map );
    void reloadAndAssignHolidayStrategysFromDatabase(long strategyId, map< long, CtiCCStrategyPtr > *strategy_map);
    void reloadLtcFromDatabase(long ltcId);

    void reCalculateOperationStatsFromDatabase( );
    void resetAllOperationStats();
    void resetAllConfirmationStats();
    void reCalculateConfirmationStatsFromDatabase( );
    void reCalculateAllStats( );


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

    void cascadeStrategySettingsToChildren(LONG spAreaId, LONG areaId, LONG subBusId);


    void locateOrphans(list<long> *orphanCaps, list<long> *orphanFeeders, map<long, CtiCCCapBankPtr> paobject_capbank_map,
                       map<long, CtiCCFeederPtr> paobject_feeder_map, map<long, long> capbank_feeder_map, map<long, long> feeder_subbus_map);
    BOOL isCapBankOrphan(long capBankId);
    BOOL isFeederOrphan(long feederId);
    void removeFromOrphanList(long ccId);


    list <CC_DBRELOAD_INFO> getDBReloadList() { return _reloadList; };
    list <CtiCCCapBankPtr> getUnsolicitedCapBankList() {return _unsolicitedCapBanks;};
    list <CtiCCCapBankPtr> getRejectedControlCapBankList() {return _rejectedCapBanks;};

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
    void updateModifiedStationsAndBusesSets(list <LONG>* stationIdList, ULONG &msgBitMask, ULONG &msgSubsBitMask,
                               CtiMultiMsg_set &modifiedSubsSet,  CtiMultiMsg_set &modifiedStationsSet);
    void registerForAdditionalPoints(CtiMultiMsg_set &modifiedSubsSet,  CtiMultiMsg_set &modifiedStationsSet);
    void initializeAllPeakTimeFlagsAndMonitorPoints(BOOL setTargetVarFlag = FALSE);
    void createAndSendClientMessages( ULONG &msgBitMask, ULONG &msgSubsBitMask, CtiMultiMsg_set &modifiedSubsSet,
                                      CtiMultiMsg_set &modifiedStationsSet, CtiMultiMsg_vec &capMessages);
    void addSubstationObjectsToSet(list <LONG> *subBusIds, CtiMultiMsg_set &modifiedSubsSet);
    void addSubBusObjectsToSet(list <LONG> *subBusIds, CtiMultiMsg_set &modifiedSubsSet);
    void updateSubstationObjectSet(LONG substationId, CtiMultiMsg_set &modifiedStationsSet);
    void updateAreaObjectSet(LONG areaId, CtiMultiMsg_set &modifiedAreasSet);
    void clearDBReloadList();
    void insertUnsolicitedCapBankList(CtiCCCapBankPtr x);
    void removeCapbankFromUnsolicitedCapBankList(CtiCCCapBankPtr x);
    void clearUnsolicitedCapBankList();
    void checkUnsolicitedList();
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

    map <long, CtiCCSubstationBusPtr>* getPAOSubMap();
    map <long, CtiCCAreaPtr>* getPAOAreaMap();
    map <long, CtiCCSubstationPtr>* getPAOStationMap();
    map <long, CtiCCSpecialPtr>* getPAOSpecialAreaMap();
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

    void addAreaToPaoMap(CtiCCAreaPtr area);
    void addSubstationToPaoMap(CtiCCSubstationPtr station);
    void addSubBusToPaoMap(CtiCCSubstationBusPtr bus);
    void addFeederToPaoMap(CtiCCFeederPtr feeder);

    std::vector<CtiCCSubstationBusPtr> getSubBusesByAreaId(int areaId);
    std::vector<CtiCCSubstationBusPtr> getSubBusesBySpecialAreaId(int areaId);
    std::vector<CtiCCSubstationBusPtr> getSubBusesByStationId(int stationId);
    std::vector<CtiCCSubstationBusPtr> getSubBusesByFeederId(int feederId);
    std::vector<CtiCCSubstationBusPtr> getSubBusesByCapControlByIdAndType(int paoId, CapControlType type);

    std::vector<CtiCCCapBankPtr> getCapBanksByPaoId(int paoId);
    std::vector<CtiCCCapBankPtr> getCapBanksByPaoIdAndType(int paoId, CapControlType type);
    CapControlType determineTypeById(int paoId);

private:

    /* Relating to Max Kvar Cparm */
    long isKVARAvailable( long kvarNeeded );

    void startThreads();
    void reset();
    //bool CtiCCSubstationBusStore::findPointId(long pointId);
    void checkAMFMSystemForUpdates();
    void handleAMFMChanges(RWDBReader& rdr);
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

    map< long, CtiCCSpecialPtr > _paobject_specialarea_map;
    map< long, CtiCCAreaPtr > _paobject_area_map;
    map< long, CtiCCSubstationPtr > _paobject_substation_map;
    map< long, CtiCCSubstationBusPtr > _paobject_subbus_map;
    map< long, CtiCCFeederPtr > _paobject_feeder_map;
    map< long, CtiCCCapBankPtr > _paobject_capbank_map;
    map< long, LoadTapChangerPtr> _paobject_ltc_map;

    multimap< long, CtiCCAreaPtr > _pointid_area_map;
    multimap< long, CtiCCSpecialPtr > _pointid_specialarea_map;
    multimap< long, CtiCCSubstationBusPtr > _pointid_subbus_map;
    multimap< long, CtiCCSubstationPtr > _pointid_station_map;
    multimap< long, CtiCCFeederPtr > _pointid_feeder_map;
    multimap< long, CtiCCCapBankPtr > _pointid_capbank_map;

    map< long, CtiCCStrategyPtr > _strategyid_strategy_map;

    map< long, long > _substation_specialarea_map;
    map< long, long > _substation_area_map;
    map< long, long > _subbus_substation_map;
    map< long, long > _feeder_subbus_map;
    map< long, long > _capbank_subbus_map;
    map< long, long > _capbank_feeder_map;
    map< long, long > _cbc_capbank_map;
    map< long, long > _ltc_subbus_map;

    multimap< long, long > _altsub_sub_idmap;

    list <CtiCCCapBankPtr> _unsolicitedCapBanks;
    list <CtiCCCapBankPtr> _rejectedCapBanks;

    list <CC_DBRELOAD_INFO> _reloadList;
    list <long> _orphanedCapBanks;
    list <long> _orphanedFeeders;

    map< long, MaxKvarObject > maxKvarMap;

    mutable RWRecursiveLock<RWMutexLock> _storeMutex;
};

#endif

