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

#include <map>

#include <rw/vstream.h>
#include <rw/thr/onlyptr.h>
#include <rw/thr/thread.h>
#include <rw/collect.h>
#include <rw/tvordvec.h>

#include "observe.h"
#include "lmcontrolarea.h"
#include "lmcontrolareatrigger.h"
#include "lmprogrambase.h"
#include "lmid.h"

class CtiLMSavedProjectionQueue
{//equivalent to an inner class, only used for saving projection queues

public:
    CtiLMSavedProjectionQueue(LONG pointId, const std::vector<CtiLMProjectionPointEntry>& projectionEntryList);
    CtiLMSavedProjectionQueue(const CtiLMSavedProjectionQueue& savedProjectionQueue);

    virtual ~CtiLMSavedProjectionQueue();

    LONG getPointId() const;
    const std::vector<CtiLMProjectionPointEntry>& getProjectionEntryList() const;

    CtiLMSavedProjectionQueue& setPointId(LONG pointId);
    CtiLMSavedProjectionQueue& setProjectionEntryList(const std::vector<CtiLMProjectionPointEntry>& projectionEntryList);

    CtiLMSavedProjectionQueue& operator=(const CtiLMSavedProjectionQueue& right);

    //int operator==(const CtiLMProjectionPointEntry& right) const;
    //int operator!=(const CtiLMProjectionPointEntry& right) const;

private:
    LONG _pointId;
    std::vector<CtiLMProjectionPointEntry> _projectionEntryList;
};


class CtiLMSavedControlString
{//equivalent to an inner class, only used for saving control strings

public:
    CtiLMSavedControlString(LONG paoId, const std::string& controlString);
    CtiLMSavedControlString(const CtiLMSavedControlString& savedControlString);

    virtual ~CtiLMSavedControlString();

    LONG getPAOId() const;
    const std::string& getControlString() const;

    CtiLMSavedControlString& setPAOId(LONG paoId);
    CtiLMSavedControlString& setControlString(const std::string& controlstr);

    CtiLMSavedControlString& operator=(const CtiLMSavedControlString& right);

    //int operator==(const CtiLMSavedControlString& right) const;
    //int operator!=(const CtiLMSavedControlString& right) const;

private:
    LONG _paoId;
    std::string _controlString;
};


class CtiLMControlAreaStore
{
public:   

    std::vector<CtiLMControlArea*>* getControlAreas(ULONG secondsFrom1901 = CtiTime().seconds());
    bool findProgram(LONG programID, CtiLMProgramBaseSPtr& program = CtiLMProgramBaseSPtr(), CtiLMControlArea** controlArea = NULL);
    
    CtiLMGroupPtr             findGroupByPointID       (long point_id);
    std::vector<CtiLMControlArea*> findControlAreasByPointID(long point_id);
    
    static CtiLMControlAreaStore* getInstance();
    static void deleteInstance();

    void dumpAllDynamicData();
    void updateAllDynamicData();
    
    bool isValid();
    void setValid(bool valid);
    bool getReregisterForPoints();
    void setReregisterForPoints(bool reregister);
    bool getWasControlAreaDeletedFlag();
    void setWasControlAreaDeletedFlag(bool wasDeleted);

    bool UpdateControlAreaDisableFlagInDB(CtiLMControlArea* controlArea);
    bool UpdateProgramDisableFlagInDB(CtiLMProgramBaseSPtr program);
    bool UpdateGroupDisableFlagInDB(CtiLMGroupPtr& group);
    bool UpdateTriggerInDB(CtiLMControlArea* controlArea, CtiLMControlAreaTrigger* trigger);

    void saveAnyProjectionData();
    void attachProjectionData(CtiLMControlAreaTrigger* trigger);
    void saveAnyControlStringData();
    void attachControlStringData(CtiLMGroupPtr& group);

    CtiLMGroupPtr getLMGroup(long groupID);
    CtiLMProgramBaseSPtr getLMProgram(long programID);
    CtiLMControlArea* getLMControlArea(long controlAreaID);

    static const std::string LOAD_MANAGEMENT_DBCHANGE_MSG_SOURCE;

private:

    //Don't allow just anyone to create or destroy control areas
    CtiLMControlAreaStore();
    virtual ~CtiLMControlAreaStore();
    
    void reset();
    void shutdown();

    void doResetThr();

    bool checkMidnightDefaultsForReset();

    std::vector<CtiLMControlArea*>* _controlAreas;
    std::map< long, CtiLMGroupPtr > _point_group_map;
    std::multimap<long, long > _point_control_area_map;
    std::map< long, CtiLMGroupPtr > _all_group_map;
    std::map< long, CtiLMProgramBaseSPtr > _all_program_map;
    std::map< long, CtiLMControlArea*> _all_control_area_map;

    RWThread _resetthr;

    bool _isvalid;
    bool _reregisterforpoints;
    bool _wascontrolareadeletedflag;
    CtiTime _lastdbreloadtime;
    std::vector<CtiLMSavedProjectionQueue> _projectionQueues;
    std::vector<CtiLMSavedControlString> _controlStrings;

    //The singleton instance of CtiLMControlAreaStore
    static CtiLMControlAreaStore* _instance;
};

#endif

