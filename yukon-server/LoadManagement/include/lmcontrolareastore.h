/*---------------------------------------------------------------------------
        Filename:  controlareastore.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiLMControlAreaStore.
                        CtiLMControlAreaStore maintains a pool of
                        CtiLMControlArea handles.
                       

        Initial Date:  2/1/2001
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/

#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#ifndef CTILMCONTROLAREASTORE_H
#define CTILMCONTROLAREASTORE_H


#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/onlyptr.h>
#include <rw/thr/thread.h>
#include <rw/collect.h>

#include "observe.h"
#include "lmcontrolarea.h"
#include "lmcontrolareatrigger.h"
#include "lmprogrambase.h"
#include "lmid.h"

class CtiLMControlAreaStore : public RWMonitor< RWRecursiveLock< RWMutexLock > >
{
public:   

    RWOrdered* getControlAreas(LONG secondsFrom1901);

    static CtiLMControlAreaStore* getInstance();
    static void deleteInstance();

    void dumpAllDynamicData();
    bool isValid();
    void setValid(bool valid);
    bool getReregisterForPoints();
    void setReregisterForPoints(bool reregister);

    bool UpdateControlAreaDisableFlagInDB(CtiLMControlArea* controlArea);
    bool UpdateProgramDisableFlagInDB(CtiLMProgramBase* program);
    bool UpdateTriggerInDB(CtiLMControlArea* controlArea, CtiLMControlAreaTrigger* trigger);

    static const RWCString LOAD_MANAGEMENT_DBCHANGE_MSG_SOURCE;

    RWRecursiveLock<RWMutexLock> & getMux() { return mutex(); };

private:

    //Don't allow just anyone to create or destroy control areas
    CtiLMControlAreaStore();
    virtual ~CtiLMControlAreaStore();

    void reset();
    void shutdown();

    void doResetThr();

    bool checkMidnightDefaultsForReset();

    RWOrdered* _controlAreas;
        
    RWThread _resetthr;

    bool _isvalid;
    bool _reregisterforpoints;
    RWDBDateTime _lastdbreloadtime;

    //The singleton instance of CtiLMControlAreaStore
    static CtiLMControlAreaStore* _instance;
};

#endif

