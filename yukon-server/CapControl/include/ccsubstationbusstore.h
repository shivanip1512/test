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

    RWOrdered* getCCSubstationBuses(LONG secondsFrom1901);
    RWOrdered* getCCCapBankStates(LONG secondsFrom1901);
    RWOrdered* getCCGeoAreas(LONG secondsFrom1901);

    static CtiCCSubstationBusStore* getInstance();
    static void deleteInstance();

    void dumpAllDynamicData();
    BOOL isValid();
    void setValid(BOOL valid);
    BOOL getReregisterForPoints();
    void setReregisterForPoints(BOOL reregister);
    BOOL getReloadFromAMFMSystemFlag();
    void setReloadFromAMFMSystemFlag(BOOL reload);

    void verifySubBusAndFeedersStates();
    void resetDailyOperations();

    bool UpdateBusDisableFlagInDB(CtiCCSubstationBus* bus);
    bool UpdateFeederDisableFlagInDB(CtiCCFeeder* feeder);
    bool UpdateCapBankDisableFlagInDB(CtiCCCapBank* capbank);
    bool UpdateCapBankInDB(CtiCCCapBank* capbank);
    bool UpdateFeederBankListInDB(CtiCCFeeder* feeder);

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

    void feederReconfigureM3IAMFM(LONG capid, RWCString capname, DOUBLE kvarrating,
                                  RWCString fixedswitched, RWCString cbcmodel,
                                  LONG cbcserialnumber, RWCString location,
                                  LONG capswitchingorder, RWCString enableddisabled,
                                  LONG feederid, RWCString feedername, RWCString typeofcapchange);
    void capBankMovedToDifferentFeeder(CtiCCFeeder* oldFeeder, CtiCCCapBank* movedCapBank,
                                       LONG feederid, LONG capswitchingorder);
    void capBankDifferentOrderSameFeeder(CtiCCFeeder* currentFeeder, CtiCCCapBank* currentCapBank,
                                         LONG capswitchingorder);
    void capOutOfServiceM3IAMFM(LONG feederid, LONG capid, RWCString enableddisabled, RWCString fixedswitched);
    void feederOutOfServiceM3IAMFM(LONG feederid, RWCString fixedswitched, RWCString enableddisabled);

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
    RWDBDateTime _lastdbreloadtime;

    //The singleton instance of CtiCCSubstationBusStore
    static CtiCCSubstationBusStore* _instance;

    //Possible static strings
    static const RWCString m3iAMFMInterfaceString;

    static const RWCString feederReconfigureM3IAMFMChangeTypeString;
    static const RWCString capOutOfServiceM3IAMFMChangeTypeString;
    static const RWCString feederOutOfServiceM3IAMFMChangeTypeString;
    static const RWCString aimImportM3IAMFMChangeTypeString;

    static const RWCString m3iAMFMEnabledString;
    static const RWCString m3iAMFMDisabledString;
    static const RWCString m3iAMFMFixedString;
    static const RWCString m3iAMFMSwitchedString;

    //mutable RWRecursiveLock<RWMutexLock> _storemutex;
};

#endif

