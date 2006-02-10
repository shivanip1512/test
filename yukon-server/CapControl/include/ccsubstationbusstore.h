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
#include "ccsubstationbus.h"
#include "ccid.h"
#include "ccstate.h"
#include "ccmessage.h"


using std::multimap;

struct CC_DBRELOAD_INFO
{
   LONG objectId;
   unsigned char action:2;
   unsigned char objecttype:3;
   unsigned char filler:3;
};

class CtiCCSubstationBusStore : public RWMonitor< RWRecursiveLock< RWMutexLock > >
{
public:   

    typedef enum 
    {
        Unknown = 0,
        CapBank,
        Feeder,
        SubBus,
        Strategy,
        Schedule
    } CtiCapControlObjectType;

    typedef enum
    {
        PaobjectSubBusMap = 0,
        PaobjectFeederMap,
        PaobjectCapBankMap,
        PointIdSubBusMap,
        PointIdFeederMap,
        PointIdCapBankMap,
        StrategyIdStrategyMap,
        FeederIdSubBusIdMap,
        CapBankIdSubBusIdMap,
        CapBankIdFeederIdMap

    } CtiCapControlMapType;

    CtiCCSubstationBus_vec* getCCSubstationBuses(ULONG secondsFrom1901);
    CtiCCState_vec* getCCCapBankStates(ULONG secondsFrom1901);
    CtiCCGeoArea_vec* getCCGeoAreas(ULONG secondsFrom1901);

    static CtiCCSubstationBusStore* getInstance();
    static void deleteInstance();

    void dumpAllDynamicData();
    BOOL isValid();
    void setValid(BOOL valid);
    BOOL getReregisterForPoints();
    void setReregisterForPoints(BOOL reregister);
    BOOL getReloadFromAMFMSystemFlag();
    void setReloadFromAMFMSystemFlag(BOOL reload);
    BOOL getWasSubBusDeletedFlag();
    void setWasSubBusDeletedFlag(BOOL wasDeleted);

    void verifySubBusAndFeedersStates();
    void resetDailyOperations();

    bool UpdateBusDisableFlagInDB(CtiCCSubstationBus* bus);
    bool UpdateBusVerificationFlagsInDB(CtiCCSubstationBus* bus);
    bool UpdateFeederDisableFlagInDB(CtiCCFeeder* feeder);
    bool UpdateCapBankDisableFlagInDB(CtiCCCapBank* capbank);
    bool UpdateCapBankInDB(CtiCCCapBank* capbank);
    bool UpdateFeederBankListInDB(CtiCCFeeder* feeder);

    bool InsertCCEventLogInDB(CtiCCEventLogMsg* msg);

    CtiCCSubstationBusPtr findSubBusByPointID(long point_id, int index);
    CtiCCFeederPtr findFeederByPointID(long point_id, int index);
    CtiCCCapBankPtr findCapBankByPointID(long point_id, int index);
    int getNbrOfSubBusesWithPointID(long point_id);
    int getNbrOfSubsWithAltSubID(long altSubId);
    int getNbrOfFeedersWithPointID(long point_id);
    int getNbrOfCapBanksWithPointID(long point_id);
    CtiCCSubstationBusPtr findSubBusByPAObjectID(long paobject_id);
    CtiCCFeederPtr findFeederByPAObjectID(long paobject_id);
    CtiCCCapBankPtr findCapBankByPAObjectID(long paobject_id);
    CtiCCStrategyPtr findStrategyByStrategyID(long strategy_id);

    long findSubBusIDbyFeederID(long feederId);
    long findSubBusIDbyCapBankID(long capBankId);
    long findFeederIDbyCapBankID(long capBankId);

    long findSubIDbyAltSubID(long altSubId);

    void insertItemsIntoMap(int mapType, long* first, long* second);
    void removeItemsFromMap(int mapType, long first);


    void deleteCapBank(long capBankId);
    void deleteFeeder(long feederId);
    void deleteSubBus(long subBusId);
    void deleteStrategy(long strategyId);

    void reloadCapBankFromDatabase(long capBankId, map< long, CtiCCCapBankPtr > *paobject_capbank_map,
                                   map< long, CtiCCFeederPtr > *paobject_feeder_map,
                                   multimap< long, CtiCCCapBankPtr > *pointid_capbank_map,
                                   map< long, long> *capbank_subbus_map,
                                   map< long, long> *capbank_feeder_map,
                                   map< long, long> *feeder_subbus_map);
    void reloadFeederFromDatabase(long feederId, map< long, CtiCCStrategyPtr > *strategy_map, 
                                  map< long, CtiCCFeederPtr > *paobject_feeder_map,
                                  map< long, CtiCCSubstationBusPtr > *paobject_subbus_map,
                                  multimap< long, CtiCCFeederPtr > *pointid_feeder_map, 
                                  map< long, long> *feeder_subbus_map);
    void reloadSubBusFromDatabase(long subBusId, map< long, CtiCCStrategyPtr > *strategy_map, 
                                  map< long, CtiCCSubstationBusPtr > *paobject_subbus_map,
                                  multimap< long, CtiCCSubstationBusPtr > *pointid_subbus_map, 
                                  multimap<long, long> *altsub_sub_idmap, 
                                  CtiCCSubstationBus_vec *cCSubstationBuses );
    void reloadStrategyFromDataBase(long strategyId, map< long, CtiCCStrategyPtr > *strategy_map);
    void reloadCapBankStatesFromDatabase();
    void reloadGeoAreasFromDatabase();
    void locateOrphans(list<long> *orphanCaps, list<long> *orphanFeeders, map<long, CtiCCCapBankPtr> paobject_capbank_map,
                       map<long, CtiCCFeederPtr> paobject_feeder_map, map<long, long> capbank_feeder_map, map<long, long> feeder_subbus_map);

    list <CC_DBRELOAD_INFO> getDBReloadList() { return _reloadList; };
    void insertDBReloadList(CC_DBRELOAD_INFO x);
    void checkDBReloadList();

    map <long, CtiCCSubstationBusPtr>* getPAOSubMap();

    static const string CAP_CONTROL_DBCHANGE_MSG_SOURCE;

    RWRecursiveLock<RWMutexLock> & getMux() { return mutex(); };

private:

    //Don't allow just anyone to create or destroy control areas
    CtiCCSubstationBusStore();
    virtual ~CtiCCSubstationBusStore();

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

    CtiCCSubstationBus_vec  *_ccSubstationBuses;
    CtiCCState_vec  *_ccCapBankStates;
    CtiCCGeoArea_vec *_ccGeoAreas;

    RWThread _resetthr;
    RWThread _amfmthr;

    BOOL _isvalid;
    BOOL _reregisterforpoints;
    BOOL _reloadfromamfmsystemflag;
    BOOL _wassubbusdeletedflag;
    CtiTime _lastdbreloadtime;

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

    map< long, CtiCCSubstationBusPtr > _paobject_subbus_map;
    map< long, CtiCCFeederPtr > _paobject_feeder_map;
    map< long, CtiCCCapBankPtr > _paobject_capbank_map;

    multimap< long, CtiCCSubstationBusPtr > _pointid_subbus_map;
    multimap< long, CtiCCFeederPtr > _pointid_feeder_map;
    multimap< long, CtiCCCapBankPtr > _pointid_capbank_map;

    map< long, CtiCCStrategyPtr > _strategyid_strategy_map;

    map< long, long > _feeder_subbus_map; 
    map< long, long > _capbank_subbus_map;
    map< long, long > _capbank_feeder_map;

    multimap< long, long > _altsub_sub_idmap;

    list <CC_DBRELOAD_INFO> _reloadList;
    list <long> _orphanedCapBanks;
    list <long> _orphanedFeeders;

    //mutable RWRecursiveLock<RWMutexLock> _storemutex;
};

#endif

