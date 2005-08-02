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

#include "observe.h"
#include "ccsubstationbus.h"
#include "ccid.h"

class CtiCCSubstationBusStore : public RWMonitor< RWRecursiveLock< RWMutexLock > >
{
public:   

    RWOrdered* getCCSubstationBuses(ULONG secondsFrom1901);
    RWOrdered* getCCCapBankStates(ULONG secondsFrom1901);
    RWOrdered* getCCGeoAreas(ULONG secondsFrom1901);

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

    CtiCCSubstationBusPtr findSubBusByPointID(long point_id);
    CtiCCFeederPtr findFeederByPointID(long point_id);
    CtiCCCapBankPtr findCapBankByPointID(long point_id);
    CtiCCSubstationBusPtr findSubBusByPAObjectID(long paobject_id);
    CtiCCFeederPtr findFeederByPAObjectID(long paobject_id);
    CtiCCCapBankPtr findCapBankByPAObjectID(long paobject_id);
    CtiCCStrategyPtr findStrategyByStrategyID(long strategy_id);

    long findSubBusIDbyFeederID(long feederId);
    long findSubBusIDbyCapBankID(long capBankId);
    long findFeederIDbyCapBankID(long capBankId);


    static const RWCString CAP_CONTROL_DBCHANGE_MSG_SOURCE;

    RWRecursiveLock<RWMutexLock> & getMux() { return mutex(); };

private:

    //Don't allow just anyone to create or destroy control areas
    CtiCCSubstationBusStore();
    virtual ~CtiCCSubstationBusStore();

    void reset();
    void checkAMFMSystemForUpdates();
    void handleAMFMChanges(RWDBReader& rdr);
    void shutdown();

    void feederReconfigureM3IAMFM( RWCString& capacitor_id_string, LONG circt_id_normal,
                                   RWCString& circt_nam_normal, LONG circt_id_current,
                                   RWCString& circt_name_current, RWDBDateTime& switch_datetime,
                                   RWCString& owner, RWCString& capacitor_name, RWCString& kvar_rating,
                                   RWCString& cap_fs, RWCString& cbc_model, RWCString& serial_no,
                                   RWCString& location, RWCString& switching_seq, RWCString& cap_disable_flag,
                                   RWCString& cap_disable_type, RWCString& inoperable_bad_order_equipnote,
                                   RWCString& open_tag_note, RWCString& cap_change_type );
    void capBankMovedToDifferentFeeder(CtiCCFeeder* oldFeeder, CtiCCCapBank* movedCapBank,
                                       LONG feederid, LONG capswitchingorder);
    void capBankDifferentOrderSameFeeder(CtiCCFeeder* currentFeeder, CtiCCCapBank* currentCapBank,
                                         LONG capswitchingorder);
    void capOutOfServiceM3IAMFM(LONG feederid, LONG capid, RWCString& enableddisabled, RWCString& fixedswitched);
    void feederOutOfServiceM3IAMFM(LONG feederid, RWCString& fixedswitched, RWCString& enableddisabled);

    void doResetThr();
    void doAMFMThr();

    RWOrdered* _ccSubstationBuses;
    RWOrdered* _ccCapBankStates;
    RWOrdered* _ccGeoAreas;

    RWThread _resetthr;
    RWThread _amfmthr;

    BOOL _isvalid;
    BOOL _reregisterforpoints;
    BOOL _reloadfromamfmsystemflag;
    BOOL _wassubbusdeletedflag;
    RWDBDateTime _lastdbreloadtime;

    //The singleton instance of CtiCCSubstationBusStore
    static CtiCCSubstationBusStore* _instance;

    //Possible static strings
    static const RWCString m3iAMFMInterfaceString;

    static const RWCString m3iAMFMChangeTypeCircuitOutOfService;
    static const RWCString m3iAMFMChangeTypeCircuitReturnToService;
    static const RWCString m3iAMFMChangeTypeCapOutOfService;
    static const RWCString m3iAMFMChangeTypeCapReturnedToService;
    static const RWCString m3iAMFMChangeTypeCircuitReconfigured;
    static const RWCString m3iAMFMChangeTypeCircuitReconfiguredToNormal;
    //static const RWCString M3IAMFMCapChangeTypeString;

    static const RWCString m3iAMFMEnabledString;
    static const RWCString m3iAMFMDisabledString;
    static const RWCString m3iAMFMFixedString;
    static const RWCString m3iAMFMSwitchedString;

    static const RWCString m3iAMFMNullString;


    //map< long, CtiCCSubstationBusPtr > _points_subbus_map;

    map< long, CtiCCSubstationBusPtr > _paobject_subbus_map;
    map< long, CtiCCFeederPtr > _paobject_feeder_map;
    map< long, CtiCCCapBankPtr > _paobject_capbank_map;

    map< long, CtiCCSubstationBusPtr > _pointid_subbus_map;
    map< long, CtiCCFeederPtr > _pointid_feeder_map;
    map< long, CtiCCCapBankPtr > _pointid_capbank_map;

    map< long, CtiCCStrategyPtr > _strategyid_strategy_map;

    map< long, long > _feeder_subbus_map; 
    map< long, long > _capbank_subbus_map;
    map< long, long > _capbank_feeder_map;

    list <long> _pointIdList;

    //mutable RWRecursiveLock<RWMutexLock> _storemutex;
};

#endif

