

/*-----------------------------------------------------------------------------*
*
* File:   tagmanager
*
* Date:   12/22/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2004/01/05 15:38:52 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)


#include "dbaccess.h"
#include "logger.h"
#include "guard.h"
#include "numstr.h"
#include "pointdefs.h"
#include "sema.h"
#include "tagmanager.h"
#include "tbl_dyn_pttag.h"

extern RWCString AlarmTagsToString(UINT tags);

CtiTagManager::CtiTagManager() :
_dirty(false)
{
}

CtiTagManager::CtiTagManager(const CtiTagManager& aRef) :
_dirty(false)
{
    *this = aRef;
}

CtiTagManager::~CtiTagManager()
{
    TagMgrMap_t::iterator itr;

    CtiLockGuard< CtiMutex > tlg(_mux, 5000);
    while(!tlg.isAcquired())
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** CtiTagManager Destructor Attempting to acquire exclusion **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        tlg.tryAcquire(5000);
    }

    if(tlg.isAcquired())
    {
        for(itr = _dynamicTagMsgMap.begin(); itr != _dynamicTagMsgMap.end(); itr++)
        {
            TagMgrMap_t::value_type vt = *itr;
            CtiTagMsg *pTagMsg = vt.second;

            if(pTagMsg)
            {
                delete pTagMsg;
                pTagMsg = 0;
            }
        }

        processDynamicQueue();
        processTagLogQueue();
        processDynamicRemovals();
    }
}

CtiTagManager& CtiTagManager::operator=(const CtiTagManager& aRef)
{
    if(this != &aRef)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    return *this;
}

int CtiTagManager::processTagMsg(CtiTagMsg &tag)
{
    int resultAction = ActionNone;
    int instance = tag.getInstanceID();

    try
    {
        CtiLockGuard< CtiMutex > tlg(_mux, 5000);
        while(!tlg.isAcquired())
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            tlg.tryAcquire(5000);
        }

        bool pre = isPointControlInhibited(tag.getPointID());

        setDirty(true);

        switch(tag.getAction())
        {
        case (CtiTagMsg::AddAction):
            {
                addInstance(instance, tag);
                break;
            }
        case (CtiTagMsg::UpdateAction):
            {
                updateInstance(instance, tag);
                break;
            }
        case (CtiTagMsg::RemoveAction):
            {
                removeInstance(instance, tag);
                break;
            }
        default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << " Unknown tag action " << tag.getAction() << " ACH!" << endl;
                }

                break;
            }

        }

        bool post = isPointControlInhibited(tag.getPointID());

        if(pre != post)
        {
            if(pre == true)
            {
                // We were inhibited and now are not.
                resultAction = ActionPointInhibitRemove;
            }
            else
            {
                // We were not inhibited but now are.
                resultAction = ActionPointControlInhibit;
            }
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return resultAction;
}

CtiTagMsg* CtiTagManager::getTagMsg(long instanceid) const
{
    CtiLockGuard< CtiMutex > tlg(_mux, 5000);
    while(!tlg.isAcquired())
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        tlg.tryAcquire(5000);
    }

    CtiTagMsg *pTagMsg = 0;

    try
    {
        TagMgrMap_t::key_type key = instanceid;
        TagMgrMap_t::const_iterator itr = _dynamicTagMsgMap.find( key );

        if(itr != _dynamicTagMsgMap.end())
        {
            TagMgrMap_t::value_type vt = *itr;
            CtiTagMsg *pOriginalTag = vt.second;

            if(pOriginalTag)
            {
                pTagMsg = (CtiTagMsg*)(pOriginalTag->replicateMessage());
            }
        }
    }
    catch(...)
    {
        delete pTagMsg;
        pTagMsg = 0;
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return pTagMsg;
}

size_t CtiTagManager::entries() const
{
    CtiLockGuard< CtiMutex > tlg(_mux, 5000);
    while(!tlg.isAcquired())
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        tlg.tryAcquire(5000);
    }

    return _dynamicTagMsgMap.size();
}

bool CtiTagManager::empty() const
{
    CtiLockGuard< CtiMutex > tlg(_mux, 5000);
    while(!tlg.isAcquired())
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        tlg.tryAcquire(5000);
    }

    return _dynamicTagMsgMap.empty();
}

bool CtiTagManager::dirty() const
{
    CtiLockGuard< CtiMutex > tlg(_mux, 5000);
    while(!tlg.isAcquired())
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        tlg.tryAcquire(5000);
    }

    return _dirty;
}

void CtiTagManager::setDirty(bool set)
{
    CtiLockGuard< CtiMutex > tlg(_mux, 5000);
    while(!tlg.isAcquired())
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        tlg.tryAcquire(5000);
    }

    _dirty = set;
    return;
}

void CtiTagManager::run()
{
    try
    {
        int stc = loadStaticTags();
        int dtc = loadDynamicTags();

        while( !isSet(SHUTDOWN) )
        {
            try
            {
                CtiLockGuard< CtiMutex > tlg(_mux, 5000);
                if(tlg.isAcquired())
                {
                    processDynamicQueue();
                    processTagLogQueue();
                    processDynamicRemovals();
                }
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            sleep( 5000 );
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " CtiTagManager::run() is exiting." << endl;
    }

    set(TM_THREAD_TERMINATED);
}


bool CtiTagManager::addInstance(int instance, CtiTagMsg &tag)
{
    bool failure = true;

    CtiTagMsg *pTag = (CtiTagMsg*)tag.replicateMessage();
    pTag->setInstanceID( instance );

    queueTagLogEntry(*pTag);
    queueDynamicTagLogEntry(*pTag);

    failure = addTag( pTag );

    return failure;
}

bool CtiTagManager::addTag(CtiTagMsg *&pTag)
{
    bool failure = true;

    TagMgrMap_t::key_type key = pTag->getInstanceID();
    pair< TagMgrMap_t::iterator, bool > ip = _dynamicTagMsgMap.insert( make_pair( key, pTag ) );

    if(ip.second != true)
    {
        if(DebugLevel & DEBUGLEVEL_LUDICROUS)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** INSERT COLLISION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        TagMgrMap_t::iterator itr = ip.first;
        if(itr != _dynamicTagMsgMap.end())
        {
            TagMgrMap_t::value_type vt = *itr;
            CtiTagMsg *pOriginalTag = vt.second;

            if(pOriginalTag)
            {
                if(DebugLevel & DEBUGLEVEL_LUDICROUS)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << " ORIGINAL :" << endl;

                    pOriginalTag->dump();
                }

                *pOriginalTag = *pTag;
                failure = false;

                if(DebugLevel & DEBUGLEVEL_LUDICROUS)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << " NEW :" << endl;

                    pOriginalTag->dump();
                }

            }
        }

        delete pTag;     // Clean up the memory!
    }
    else
    {
        failure = false;
    }

    return failure;
}

bool CtiTagManager::updateInstance(int instance, CtiTagMsg &tag)
{
    bool failure = true;

    TagMgrMap_t::key_type key = instance;
    TagMgrMap_t::const_iterator itr = _dynamicTagMsgMap.find( key );

    if(itr != _dynamicTagMsgMap.end())
    {
        queueTagLogEntry(tag);
        queueDynamicTagLogEntry(tag);

        TagMgrMap_t::value_type vt = *itr;
        CtiTagMsg *pOriginalTag = vt.second;

        if(pOriginalTag)
        {
            *pOriginalTag = tag;
            failure = false;
        }
    }

    return failure;
}

bool CtiTagManager::removeInstance(int instance, CtiTagMsg &tag)
{
    bool failure = true;

    _dynamicLogRemovals.push_back( instance );     // Tag this for removal from the dynamic table!

    TagMgrMap_t::key_type key = instance;
    TagMgrMap_t::const_iterator itr = _dynamicTagMsgMap.find( key );

    if(itr != _dynamicTagMsgMap.end())
    {
        TagMgrMap_t::value_type vt = *itr;
        CtiTagMsg *pTag = vt.second;

        pTag->setAction( CtiTagMsg::RemoveAction );
        pTag->setTagTime( tag.getTagTime() );

        if(!tag.getUser().isNull())
        {
            pTag->setUser(tag.getUser());
        }
        if(!tag.getDescriptionStr().isNull())
        {
            pTag->setDescriptionStr(tag.getDescriptionStr());
        }
        if(!tag.getReferenceStr().isNull())
        {
            pTag->setReferenceStr(tag.getReferenceStr());
        }
        if(!tag.getTaggedForStr().isNull())
        {
            pTag->setTaggedForStr(tag.getTaggedForStr());
        }

        queueTagLogEntry(*pTag);

        if(pTag) delete pTag;
    }

    size_t numdun = _dynamicTagMsgMap.erase( key );

    if(numdun > 0)
    {
        failure = false;
    }

    return failure;
}

void CtiTagManager::queueTagLogEntry(const CtiTagMsg &Tag)
{
    // We need to generate a TagLog entry here!
    CtiTableTagLog *tlog = new CtiTableTagLog;
    tlog->setInstanceId( Tag.getInstanceID() );
    tlog->setPointId( Tag.getPointID() );
    tlog->setTagId( Tag.getTagID() );
    tlog->setUserName( Tag.getUser() );

    RWCString actn;
    switch(Tag.getAction())
    {
    case (CtiTagMsg::AddAction):
        actn = "Tag Added";
        break;
    case (CtiTagMsg::UpdateAction):
        actn = "Tag Updated";
        break;
    case (CtiTagMsg::RemoveAction):
        actn = "Tag Removed";
        break;
    case (CtiTagMsg::ReportAction):
        actn = "Tag Report";
        break;
    default:
        actn = RWCString("Unknown ") + CtiNumStr(Tag.getAction());
        break;
    }
    tlog->setActionStr( actn );

    tlog->setDescriptionStr( Tag.getDescriptionStr() );
    tlog->setTagTime( Tag.getTagTime() );
    tlog->setReferenceStr( Tag.getReferenceStr() );
    tlog->setTaggedForStr( Tag.getTaggedForStr() );

    _tagLogQueue.putQueue( tlog );
}

void CtiTagManager::queueDynamicTagLogEntry(const CtiTagMsg &Tag)
{
    // We need to generate a TagLog entry here!
    CtiTableDynamicTag tlog;
    tlog.setInstanceId( Tag.getInstanceID() );
    tlog.setPointId( Tag.getPointID() );
    tlog.setTagId( Tag.getTagID() );
    tlog.setUserName( Tag.getUser() );

    RWCString actn;
    switch(Tag.getAction())
    {
    case (CtiTagMsg::AddAction):
        actn = "Tag Added";
        break;
    case (CtiTagMsg::UpdateAction):
        actn = "Tag Updated";
        break;
    case (CtiTagMsg::RemoveAction):
        actn = "Tag Removed";
        break;
    case (CtiTagMsg::ReportAction):
        actn = "Tag Report";
        break;
    default:
        actn = RWCString("Unknown ") + CtiNumStr(Tag.getAction());
        break;
    }
    tlog.setActionStr( actn );

    tlog.setDescriptionStr( Tag.getDescriptionStr() );
    tlog.setTagTime( Tag.getTagTime() );
    tlog.setReferenceStr( Tag.getReferenceStr() );
    tlog.setTaggedForStr( Tag.getTaggedForStr() );

    pair< TagTblDynamicMap_t::iterator, bool > ip = _dynTagLogMap.insert( make_pair(tlog.getInstanceId(), tlog) );

    if(ip.second != true)
    {
        TagTblDynamicMap_t::iterator itr = ip.first;
        if(itr != _dynTagLogMap.end())
        {
            TagTblDynamicMap_t::value_type vt = *itr;
            CtiTableDynamicTag &oldTag = vt.second;

            oldTag = tlog;

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Updated the dynamic table map!  Tag intance " << oldTag.getInstanceId() << endl;
            }
        }
    }
}

void CtiTagManager::processDynamicQueue()
{
    bool failed = false;

    if(!_dynTagLogMap.empty())
    {
        CtiLockGuard< CtiMutex > tlg(_mux, 5000);
        if(tlg.isAcquired())
        {
            RWCString dpa("dyn_tag");
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();

            if(conn.isValid())
            {
                TagTblDynamicMap_t::iterator itr;
                conn.beginTransaction(dpa);

                for(itr = _dynTagLogMap.begin(); itr != _dynTagLogMap.end(); itr++)
                {
                    TagTblDynamicMap_t::value_type vt = *itr;
                    CtiTableDynamicTag &Tag = vt.second;

                    if( Tag.Update(conn).errorCode() != RWDBStatus::ok &&
                        Tag.Update(conn).errorCode() != RWDBStatus::endOfFetch )
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            Tag.dump();
                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            dout << " Error code " << Tag.Update(conn).errorCode() << endl;
                        }

                        failed = true;
                        break;
                    }
                }

                conn.commitTransaction(dpa);

                if(!failed) _dynTagLogMap.clear();
            }
        }
    }
}

void CtiTagManager::processTagLogQueue()
{
    if(_tagLogQueue.entries() > 0)
    {
        CtiLockGuard< CtiMutex > tlg(_mux, 5000);
        if(tlg.isAcquired())
        {
            CtiTableTagLog *pTag = 0;

            RWCString dpa("dyn_tag");
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();

            if(conn.isValid())
            {
                conn.beginTransaction(dpa);

                while((pTag = _tagLogQueue.getQueue(500)) != 0)
                {
                    if( pTag->Update(conn).errorCode() != RWDBStatus::ok )
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            dout << " Unable to update TagLog. RWDBStatus::errorCode() = " << pTag->Update().errorCode() << endl;
                            pTag->dump();
                        }

                        break;
                    }
                    else
                        delete pTag;
                }

                conn.commitTransaction(dpa);
            }
        }
    }
}

/*
 *  This method removes DynamicTags from the db table.  The manner in which it operates could allow a dirty shutdown to
 *  re-enable a tag on the next startup.  This is better than the alternative.
 */
void CtiTagManager::processDynamicRemovals()
{
    if(!_dynamicLogRemovals.empty())
    {
        CtiLockGuard< CtiMutex > tlg(_mux, 5000);
        if(tlg.isAcquired())
        {
            int removeit;

            while(!_dynamicLogRemovals.empty())
            {
                removeit = _dynamicLogRemovals.back();
                _dynamicLogRemovals.pop_back();

#if 0
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << " Trying to remove " << removeit << endl;
                }
#endif

                CtiTableDynamicTag removeTag;

                removeTag.setInstanceId(removeit);
                removeTag.Delete();
            }
        }
    }
}

CtiMultiMsg* CtiTagManager::getPointTags(long pointid) const
{
    CtiLockGuard< CtiMutex > tlg(_mux, 5000);
    while(!tlg.isAcquired())
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        tlg.tryAcquire(5000);
    }

    CtiMultiMsg *pMulti = 0;
    CtiTagMsg *pTag = 0;
    TagMgrMap_t::const_iterator itr;

    try
    {
        for(itr = _dynamicTagMsgMap.begin(); itr != _dynamicTagMsgMap.end(); itr++)
        {
            TagMgrMap_t::value_type vt = *itr;
            TagMgrMap_t::key_type   key = vt.first;
            CtiTagMsg *pOriginalTag = vt.second;

            if(pOriginalTag && pOriginalTag->getPointID() == pointid)
            {
                pTag = (CtiTagMsg*)(pOriginalTag->replicateMessage());

                if(!pMulti)
                {
                    pMulti = new CtiMultiMsg;
                }
                if(pMulti)
                {
                    pMulti->insert(pTag);
                }
            }
        }
    }
    catch(...)
    {
        delete pTag;
        pTag = 0;
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return pMulti;
}


int CtiTagManager::loadDynamicTags()
{
    int loadcount = 0;

    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        RWDBDatabase   db       = conn.database();
        RWDBSelector   selector = conn.database().selector();
        RWDBTable      keyTable;
        RWDBReader     rdr;

        CtiTableDynamicTag::getSQL( db, keyTable, selector );

        rdr = selector.reader( conn );

        if(rdr.status().errorCode() != RWDBStatus::ok)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << selector.asString() << endl;
        }

        while( rdr() )
        {
            CtiTableDynamicTag dynTag;
            dynTag.DecodeDatabaseReader(rdr);

            CtiTagMsg *pTag = new CtiTagMsg;

            pTag->setInstanceID(dynTag.getInstanceId());
            pTag->setPointID(dynTag.getPointId());
            pTag->setTagID(dynTag.getTagId());
            pTag->setUser(dynTag.getUserName());
            pTag->setAction(CtiTagMsg::ReportAction);                                      //dynTag.getActionStr());
            pTag->setDescriptionStr(dynTag.getDescriptionStr());
            pTag->setTagTime(dynTag.getTagTime().rwtime());
            pTag->setReferenceStr(dynTag.getReferenceStr());
            pTag->setTaggedForStr(dynTag.getTaggedForStr());

            if( !addTag( pTag ) ) loadcount++;

        }
    }

    return loadcount;
}

int CtiTagManager::loadStaticTags()
{
    int loadcount = 0;

    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        RWDBDatabase   db       = conn.database();
        RWDBSelector   selector = conn.database().selector();
        RWDBTable      keyTable;
        RWDBReader     rdr;

        CtiTableTag::getSQL( db, keyTable, selector );

        rdr = selector.reader( conn );

        if(rdr.status().errorCode() != RWDBStatus::ok)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << selector.asString() << endl;
        }

        while( rdr() )
        {
            CtiTableTag staticTag;
            staticTag.DecodeDatabaseReader(rdr);
            loadcount++;

            pair< TagTblMap_t::iterator, bool > ip = _staticTagTableMap.insert( make_pair(staticTag.getTagId(), staticTag) );

            if(ip.second != true)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** CtiTableTag **** Insert collision. " << endl;
                }
            }
        }
    }

    return loadcount;
}

bool CtiTagManager::verifyTagMsg(CtiTagMsg &pTag)
{
    bool allocated = false;

    if(pTag.getAction() == CtiTagMsg::AddAction)
    {
        int instance = CtiTableTagLog::getMaxInstanceId();
        pTag.setInstanceID(instance);
        allocated = true;
    }
    else if(pTag.getAction() == CtiTagMsg::RemoveAction && pTag.getPointID() <= 0)
    {
        CtiLockGuard< CtiMutex > tlg(_mux, 5000);
        while(!tlg.isAcquired())
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            tlg.tryAcquire(5000);
        }

        TagMgrMap_t::const_iterator itr = _dynamicTagMsgMap.find( pTag.getInstanceID() );

        if(itr != _dynamicTagMsgMap.end())
        {
            TagMgrMap_t::value_type vt = *itr;
            CtiTagMsg *pOriginalTag = vt.second;

            if(pOriginalTag)
            {
                pTag.setPointID(pOriginalTag->getPointID());
            }
        }
    }

    return allocated;
}

bool CtiTagManager::isPointControlInhibited(LONG pid)
{
    bool inhibit = false;

    if(pid <= 0)
    {
        return inhibit;
    }

    CtiLockGuard< CtiMutex > tlg(_mux, 5000);
    while(!tlg.isAcquired())
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** CtiTagManager isPointControlInhibited() attempting to acquire exclusion **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        tlg.tryAcquire(5000);
    }

    if(tlg.isAcquired())
    {
        TagMgrMap_t::iterator itr;

        for(itr = _dynamicTagMsgMap.begin(); itr != _dynamicTagMsgMap.end(); itr++)
        {
            TagMgrMap_t::value_type vt = *itr;
            CtiTagMsg *pTagMsg = vt.second;

            if(pTagMsg && pTagMsg->getPointID() == pid)
            {
                TagTblMap_t::const_iterator tagitr = _staticTagTableMap.find( pTagMsg->getTagID() );

                if(tagitr != _staticTagTableMap.end())
                {
                    TagTblMap_t::value_type vt = *tagitr;
                    CtiTableTag Tag = vt.second;

                    if( Tag.getInhibit() )
                    {
                        // This tag causes inhibits to occur!
                        #if 0
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            dout << " Point " << pid << " is control inhibited due to tag instance " << pTagMsg->getInstanceID() << endl;
                        }
                        #endif

                        inhibit = true;
                        break;                  // The for loop!
                    }
                }
            }
        }
    }

    return inhibit;
}
