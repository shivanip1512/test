#include "precompiled.h"

#include "dbaccess.h"
#include "logger.h"
#include "guard.h"
#include "numstr.h"
#include "pointdefs.h"
#include "sema.h"
#include "tagmanager.h"
#include "tbl_dyn_pttag.h"
#include "database_connection.h"
#include "database_reader.h"
#include "database_transaction.h"

using namespace std;

extern string AlarmTagsToString(UINT tags);

CtiTagManager::CtiTagManager() :
_dirty(false)
{
}

CtiTagManager::~CtiTagManager()
{
    TagMgrMap_t::iterator itr;

    CtiLockGuard< CtiMutex > tlg(_mux, 5000);
    while(!tlg.isAcquired())
    {
        CTILOG_ERROR(dout, "Unable to acquire exclusion lock. Will retry");
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

CtiTagManager::Actions CtiTagManager::processTagMsg(CtiTagMsg &tag)
{
    auto resultAction = Actions::None;
    int instance = tag.getInstanceID();

    try
    {
        CtiLockGuard< CtiMutex > tlg(_mux, 5000);
        while(!tlg.isAcquired())
        {
            CTILOG_ERROR(dout, "Unable to acquire exclusion lock. Will retry");
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
                CTILOG_ERROR(dout, "Unknown tag action "<< tag.getAction() <<" ACH!");
                break;
            }

        }

        bool post = isPointControlInhibited(tag.getPointID());

        if(pre != post)
        {
            if(pre == true)
            {
                // We were inhibited and now are not.
                resultAction = Actions::PointInhibitRemove;
            }
            else
            {
                // We were not inhibited but now are.
                resultAction = Actions::PointControlInhibit;
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return resultAction;
}

CtiTagMsg* CtiTagManager::getTagMsg(long instanceid) const
{
    CtiLockGuard< CtiMutex > tlg(_mux, 5000);
    while(!tlg.isAcquired())
    {
        CTILOG_ERROR(dout, "Unable to acquire exclusion lock. Will retry");
        tlg.tryAcquire(5000);
    }

    std::auto_ptr<CtiTagMsg> pTagMsg;

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
                pTagMsg.reset((CtiTagMsg*)(pOriginalTag->replicateMessage()));
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return pTagMsg.release();
}

size_t CtiTagManager::entries() const
{
    CtiLockGuard< CtiMutex > tlg(_mux, 5000);
    while(!tlg.isAcquired())
    {
        CTILOG_ERROR(dout, "Unable to acquire exclusion lock. Will retry");
        tlg.tryAcquire(5000);
    }

    return _dynamicTagMsgMap.size();
}

bool CtiTagManager::empty() const
{
    CtiLockGuard< CtiMutex > tlg(_mux, 5000);
    while(!tlg.isAcquired())
    {
        CTILOG_ERROR(dout, "Unable to acquire exclusion lock. Will retry");
        tlg.tryAcquire(5000);
    }

    return _dynamicTagMsgMap.empty();
}

bool CtiTagManager::dirty() const
{
    CtiLockGuard< CtiMutex > tlg(_mux, 5000);
    while(!tlg.isAcquired())
    {
        CTILOG_ERROR(dout, "Unable to acquire exclusion lock. Will retry");
        tlg.tryAcquire(5000);
    }

    return _dirty;
}

void CtiTagManager::setDirty(bool flag)
{
    CtiLockGuard< CtiMutex > tlg(_mux, 5000);
    while(!tlg.isAcquired())
    {
        CTILOG_ERROR(dout, "Unable to acquire exclusion lock. Will retry");
        tlg.tryAcquire(5000);
    }

    _dirty = flag;
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
                CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
            }

            sleep( 5000 );
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    CTILOG_INFO(dout, "exiting");

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

bool CtiTagManager::addTag(CtiTagMsg *pTag)
{
    bool failure = true;

    TagMgrMap_t::key_type key = pTag->getInstanceID();
    pair< TagMgrMap_t::iterator, bool > ip = _dynamicTagMsgMap.insert( make_pair( key, pTag ) );

    if(ip.second != true)
    {
        if(isDebugLudicrous())
        {
            CTILOG_DEBUG(dout, "INSERT COLLISION");
        }

        TagMgrMap_t::iterator itr = ip.first;
        if(itr != _dynamicTagMsgMap.end())
        {
            TagMgrMap_t::value_type vt = *itr;
            CtiTagMsg *pOriginalTag = vt.second;

            if(pOriginalTag)
            {
                if(isDebugLudicrous())
                {
                    CTILOG_DEBUG(dout, "ORIGINAL: "<< *pOriginalTag);
                }

                *pOriginalTag = *pTag;
                failure = false;

                if(isDebugLudicrous())
                {
                    CTILOG_DEBUG(dout, "NEW: "<< *pOriginalTag);
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

        if(!tag.getUser().empty())
        {
            pTag->setUser(tag.getUser());
        }
        if(!tag.getDescriptionStr().empty())
        {
            pTag->setDescriptionStr(tag.getDescriptionStr());
        }
        if(!tag.getReferenceStr().empty())
        {
            pTag->setReferenceStr(tag.getReferenceStr());
        }
        if(!tag.getTaggedForStr().empty())
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

    string actn;
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
        actn = string("Unknown ") + CtiNumStr(Tag.getAction());
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

    string actn;
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
        actn = string("Unknown ") + CtiNumStr(Tag.getAction());
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

            CTILOG_INFO(dout, "Updated the dynamic table map!  Tag intance " << oldTag.getInstanceId());;
        }
    }
}

void CtiTagManager::processDynamicQueue()
{
    if(!_dynTagLogMap.empty())
    {
        CtiLockGuard< CtiMutex > tlg(_mux, 5000);
        if(tlg.isAcquired())
        {
            Cti::Database::DatabaseConnection   conn;

            if ( ! conn.isValid() )
            {
                CTILOG_ERROR(dout, "Invalid Connection to Database");
                return;
            }

            TagTblDynamicMap_t::iterator itr = _dynTagLogMap.begin();

            while( itr != _dynTagLogMap.end() )
            {
                TagTblDynamicMap_t::value_type vt = *itr;
                CtiTableDynamicTag &Tag = vt.second;

                if( ! Tag.Update(conn) )
                {
                    CTILOG_ERROR(dout, "DB Update failed"<<
                            Tag);
                    ++itr;
                }
                else
                {
                    _dynTagLogMap.erase( itr++ );
                }
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
            Cti::Database::DatabaseConnection   conn;

            if ( ! conn.isValid() )
            {
                CTILOG_ERROR(dout, "Invalid Connection to Database");
                return;
            }

            boost::scoped_ptr<CtiTableTagLog> pTag;

            while( pTag.reset(_tagLogQueue.getQueue(500)), pTag )
            {
                if( ! pTag->Update(conn) )
                {
                    CTILOG_ERROR(dout, "DB Update failed"<<
                            *pTag);
                    break;
                }
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
        CTILOG_ERROR(dout, "Unable to acquire exclusion lock. Will retry");
        tlg.tryAcquire(5000);
    }

    std::auto_ptr<CtiMultiMsg> pMulti;
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
                std::auto_ptr<CtiTagMsg> pTag((CtiTagMsg*)(pOriginalTag->replicateMessage()));

                if( ! pMulti.get() )
                {
                    pMulti.reset(new CtiMultiMsg);
                }

                pMulti->insert(pTag.release());
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return pMulti.release();
}

CtiMultiMsg* CtiTagManager::getAllPointTags() const
{
    CtiLockGuard< CtiMutex > tlg(_mux, 5000);
    while(!tlg.isAcquired())
    {
        CTILOG_ERROR(dout, "Unable to acquire exclusion lock. Will retry");
        tlg.tryAcquire(5000);
    }

    std::auto_ptr<CtiMultiMsg> pMulti;
    TagMgrMap_t::const_iterator itr;

    try
    {
        for(itr = _dynamicTagMsgMap.begin(); itr != _dynamicTagMsgMap.end(); itr++)
        {
            TagMgrMap_t::value_type vt = *itr;
            TagMgrMap_t::key_type   key = vt.first;
            CtiTagMsg *pOriginalTag = vt.second;

            if( pOriginalTag )
            {
                std::auto_ptr<CtiTagMsg> pTag((CtiTagMsg*)(pOriginalTag->replicateMessage()));

                if( ! pMulti.get() )
                {
                    pMulti.reset(new CtiMultiMsg);
                }

                pMulti->insert(pTag.release());
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return pMulti.release();
}


int CtiTagManager::loadDynamicTags()
{
    int loadcount = 0;

    {
        static const string sql = CtiTableDynamicTag::getSQLCoreStatement();

        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader rdr(connection, sql);
        rdr.execute();

        while( rdr() )
        {
            CtiTableDynamicTag dynTag;
            dynTag.DecodeDatabaseReader(rdr);

            std::auto_ptr<CtiTagMsg> pTag(new CtiTagMsg);

            pTag->setInstanceID(dynTag.getInstanceId());
            pTag->setPointID(dynTag.getPointId());
            pTag->setTagID(dynTag.getTagId());
            pTag->setUser(dynTag.getUserName());
            pTag->setAction(CtiTagMsg::ReportAction);                                      //dynTag.getActionStr());
            pTag->setDescriptionStr(dynTag.getDescriptionStr());
            pTag->setTagTime(dynTag.getTagTime());
            pTag->setReferenceStr(dynTag.getReferenceStr());
            pTag->setTaggedForStr(dynTag.getTaggedForStr());

            if( ! addTag(pTag.release()) ) loadcount++;
        }

        if( ! rdr.isValid() )
        {
            CTILOG_ERROR(dout, "DB read failed for SQL query: "<< rdr.asString());
        }
    }

    return loadcount;
}

int CtiTagManager::loadStaticTags()
{
    int loadcount = 0;

    {
        static const string sql = CtiTableTag::getSQLCoreStatement();

        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader rdr(connection, sql);
        rdr.execute();

        while( rdr() )
        {
            CtiTableTag staticTag;
            staticTag.DecodeDatabaseReader(rdr);
            loadcount++;

            pair< TagTblMap_t::iterator, bool > ip = _staticTagTableMap.insert( make_pair(staticTag.getTagId(), staticTag) );

            if(ip.second != true)
            {
                CTILOG_WARN(dout, "Insert collision");
            }
        }

        if( ! rdr.isValid() )
        {
            CTILOG_ERROR(dout, "DB read failed for SQL query: "<< rdr.asString());
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
            CTILOG_ERROR(dout, "Unable to acquire exclusion lock. Will retry");
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
        CTILOG_ERROR(dout, "Unable to acquire exclusion lock. Will retry");
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
                    TagTblMap_t::value_type tVt = *tagitr;
                    CtiTableTag Tag = tVt.second;

                    if( Tag.getInhibit() )
                    {
                        // This tag causes inhibits to occur!

                        inhibit = true;
                        break;                  // The for loop!
                    }
                }
            }
        }
    }

    return inhibit;
}
