
/*-----------------------------------------------------------------------------*
*
* File:   tagmanager
*
* Class:  CtiTagManager
* Date:   12/22/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2007/10/24 14:51:29 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __TAGMANAGER_H__
#define __TAGMANAGER_H__

#include <map>
#include <set>
#include <utility>

#include "dlldefs.h"
#include "msg_tag.h"
#include "msg_multi.h"
#include "mutex.h"
#include "queue.h"
#include "thread.h"
#include "tbl_dyn_pttag.h"
#include "tbl_tag.h"
#include "tbl_taglog.h"



class IM_EX_CTIVANGOGH CtiTagManager : public CtiThread
{
public:

    typedef std::map< int, CtiTagMsg* >    TagMgrMap_t;
    typedef std::map< int, CtiTableTag >   TagTblMap_t;

    typedef std::map< int, CtiTableDynamicTag >   TagTblDynamicMap_t;

    enum {
        ActionNone,
        ActionPointControlInhibit,
        ActionPointInhibitRemove
    };

protected:

    CtiFIFOQueue< CtiTableTagLog >            _tagLogQueue;
    // This is a vector of the rows in the dynamic table which need to be Deleted.
    std::vector< int > _dynamicLogRemovals;

    TagTblDynamicMap_t _dynTagLogMap;
    TagTblMap_t _staticTagTableMap;     // Holds STATIC tag information frmo the table called "Tags"
    TagMgrMap_t _dynamicTagMsgMap;      // Holds the currently active tag messages.
    bool _dirty;
    mutable CtiMutex _mux;

    enum
    {
        TM_THREAD_TERMINATED = CtiThread::LAST
    };

    void run();

private:

    bool addTag(CtiTagMsg *&pTag);                                  // Assumes _mux is acquired.

    bool addInstance(int instance, CtiTagMsg &tag);           // Assumes _mux is acquired.
    bool updateInstance(int instance, CtiTagMsg &tag);        // Assumes _mux is acquired.
    bool removeInstance(int instance, CtiTagMsg &tag);        // Assumes _mux is acquired.
    void queueTagLogEntry(const CtiTagMsg &Tag);
    void queueDynamicTagLogEntry(const CtiTagMsg &Tag);

    void processDynamicRemovals();
    void processTagLogQueue();
    void processDynamicQueue();

public:

    CtiTagManager();
    CtiTagManager(const CtiTagManager& aRef);
    virtual ~CtiTagManager();

    CtiTagManager& operator=(const CtiTagManager& aRef);

    int processTagMsg(CtiTagMsg &tag);

    CtiTagMsg* getTagMsg(long instanceid) const;
    CtiMultiMsg* getPointTags(long pointid) const;
    CtiMultiMsg* getAllPointTags() const;

    bool verifyTagMsg(CtiTagMsg &pTag);
    int loadStaticTags();
    int loadDynamicTags();      // This method loads the dynamic tags and converts them to CtiTagMsg's for sending to clients.
    UINT writeDynamicTagsToDB();

    size_t entries() const;
    bool empty() const;

    bool dirty() const;
    void setDirty(bool set = true);
    bool isPointControlInhibited(LONG pid);

};
#endif // #ifndef __TAGMANAGER_H__
