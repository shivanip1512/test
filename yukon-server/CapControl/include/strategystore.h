/*---------------------------------------------------------------------------
        Filename:  strategystore.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiCCStrategyStore.
                        CtiCCStrategyStore maintains a pool of
                        CtiCCStrategy handles.
                       

        Initial Date:  8/21/2000
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2000
---------------------------------------------------------------------------*/

#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTICCSTRATEGYSTORE_H
#define CTICCSTRATEGYSTORE_H


#include <rw/tvslist.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/onlyptr.h>
#include <rw/thr/thread.h>
#include <rw/collect.h>

#include "observe.h"
#include "strategy.h"
#include "ccid.h"
#include "strategylist.h"
#include "state.h"

class CtiCCStrategyStore
{
public:   

    RWOrdered &Strategies();
    CtiCCStrategyList* StrategyList();
    RWOrdered* StateList();
    RWOrdered* AreaList();

    bool UpdateStrategy(CtiCCStrategy* strat);
    bool UpdateCapBank(CtiCapBank* bank);

    static CtiCCStrategyStore* Instance();
    static void DeleteInstance();

    void dumpAllDynamicData();
    bool isValid() const;
    void notValid();
    bool getReregisterForPoints() const;
    void setReregisterForPoints(bool reregister);
                                       
private:

    //Don't allow just anyone to create or destroy strategy stores
    CtiCCStrategyStore();
    virtual ~CtiCCStrategyStore();

    void reset();
    void shutdown();

    void doResetThr();

    CtiCCStrategyList* _strategyList;
    RWOrdered* _stateList;
    RWOrdered* _areaList;

    RWThread _resetthr;

    bool _isvalid;
    bool _reregisterforpoints;
    bool _doreset;

    //The singleton instance of CtiCCStrategyStore
    static CtiCCStrategyStore* _instance;
    
    mutable RWRecursiveLock<RWMutexLock> _mutex;
};

#endif

