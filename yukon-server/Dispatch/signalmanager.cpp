/*-----------------------------------------------------------------------------*
*
* File:   signalmanager
*
* Date:   8/13/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.23 $
* DATE         :  $Date: 2008/04/10 21:04:49 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"


#include "dbaccess.h"
#include "logger.h"
#include "guard.h"
#include "pointdefs.h"
#include "sema.h"
#include "signalmanager.h"
#include "tbl_dyn_ptalarming.h"
#include "tbl_pt_alarm.h"
#include "database_connection.h"
#include "database_transaction.h"


using std::pair;
using std::make_pair;
using std::string;
using std::endl;

extern string AlarmTagsToString(UINT tags);
extern string& TrimAlarmTagText(string &text);

CtiSignalManager::CtiSignalManager() :
_dirty(false)
{
}

CtiSignalManager::~CtiSignalManager()
{
    SigMgrMap_t::iterator itr;

    CtiLockGuard< CtiMutex > tlg(_mux, 5000);
    while(!tlg.isAcquired())
    {
        CTILOG_WARN(dout, "Unable to acquire exclusion lock. Will retry");
        tlg.tryAcquire(5000);
    }

    if(tlg.isAcquired())
    {
        for(itr = _map.begin(); itr != _map.end(); itr++)
        {
            SigMgrMap_t::value_type vt = *itr;
            CtiSignalMsg *pSig = vt.second;

            if(pSig)
            {
                delete pSig;
                pSig = 0;
            }
        }
    }
}

CtiSignalManager& CtiSignalManager::addSignal(const CtiSignalMsg &sig, bool markDirty/*=true*/)
{
    try
    {
        // Events are now allowed
        if( (sig.getTags() & SIGNAL_MANAGER_MASK) != 0 )
        {
            CtiLockGuard< CtiMutex > tlg(_mux, 5000);
            while(!tlg.isAcquired())
            {
                CTILOG_WARN(dout, "Unable to acquire exclusion lock. Will retry");
                tlg.tryAcquire(5000);
            }

            if(sig.getCondition() >= 0)
            {
                if(sig.getSignalCategory() >= SignalEvent)
                {
                    if( markDirty )
                    {
                        setDirty(true, sig.getId());
                    }

                    SigMgrMap_t::key_type key = make_pair( sig.getId(), sig.getCondition() );
                    pair< SigMgrMap_t::iterator, bool > ip = _map.insert( make_pair( key, (CtiSignalMsg*)sig.replicateMessage() ) );

                    if(ip.second != true)
                    {
                        if(isDebugLudicrous())
                        {
                            CTILOG_DEBUG(dout, "INSERT COLLISION");
                        }

                        SigMgrMap_t::iterator itr = ip.first;
                        {
                            if(itr != _map.end())
                            {
                                SigMgrMap_t::value_type vt = *itr;
                                CtiSignalMsg *pOriginalSig = vt.second;

                                if(pOriginalSig)
                                {
                                    if(isDebugLudicrous())
                                    {
                                        CTILOG_DEBUG(dout, "ORIGINAL: "<< *pOriginalSig);
                                    }

                                    *pOriginalSig = sig;

                                    if(isDebugLudicrous())
                                    {
                                        CTILOG_DEBUG(dout, "NEW: "<< *pOriginalSig);
                                    }

                                }
                            }
                        }
                    }
                    else
                    {
                        _pointMap.insert(make_pair(sig.getId(), ip.first->second));
                    }
                }
            }
            else
            {
                CTILOG_ERROR(dout, "Unexpected signal condition ("<< sig.getCondition() <<")");
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return *this;
}

CtiSignalMsg * CtiSignalManager::clearAlarms(long pointid)
{
    CtiSignalMsg *pSigActive = NULL;
    bool found = false;
    UINT tags = 0;
    SigMgrMap_t::key_type key;
    SigMgrMap_t::iterator itr;

    int maxVal = __max(CtiTablePointAlarming::invalidstatusstate, CtiTablePointAlarming::invalidnumericstate);

    for(int i=0; i<maxVal; i++)
    {
        key = make_pair(pointid, i);
        itr = _map.find( key );

        if(itr != _map.end())
        {
            found = true;
            SigMgrMap_t::value_type vt = *itr;
            CtiSignalMsg *&pOriginalSig = vt.second;

            pOriginalSig->resetTags(TAG_ACTIVE_ALARM);

            tags = ( pOriginalSig->getTags() & SIGNAL_MANAGER_MASK );
            if(tags == 0)
            {
                // This guy has already been acknowledged and now has been cleared.  Get it out of the table!
                removeFromMaps(pointid, i);
                CtiTableDynamicPointAlarming::Delete(pointid, i);
                pOriginalSig = 0;
            }
        }
    }

    if( found )
    {
        pSigActive = CTIDBG_new CtiSignalMsg(pointid, 0, "Alarms Cleared");
    }
    return pSigActive;

}

// This is now based on the TAG_ACTIVE_CONDITION more than the TAG_ACTIVE_ALARM
// This may not set the alarm to active even though the active parameter is true!!
// If getSignalCategory() == SignalEvent then the alarm is active.
CtiSignalMsg*  CtiSignalManager::setAlarmActive(long pointid, int alarm_condition, bool active)
{
    bool didit = false;
    CtiSignalMsg *pSig = 0;

    try
    {
        CtiLockGuard< CtiMutex > tlg(_mux, 5000);
        while(!tlg.isAcquired())
        {
            CTILOG_WARN(dout, "Unable to acquire exclusion lock. Will retry");
            tlg.tryAcquire(5000);
        }

        UINT tags;

        SigMgrMap_t::key_type key = make_pair( pointid, alarm_condition );
        SigMgrMap_t::iterator itr = _map.find( key );

        if(itr != _map.end())
        {
            tags = 0;
            SigMgrMap_t::value_type vt = *itr;
            CtiSignalMsg *&pOriginalSig = vt.second;

            if(pOriginalSig)
            {
                if( ((pOriginalSig->getTags() & (TAG_ACTIVE_CONDITION | TAG_ACTIVE_ALARM)) != 0) != active )     // We must be changing it!
                {
                    setDirty(true, pOriginalSig->getId());

                    if(active)
                    {
                        pOriginalSig->setTags(TAG_ACTIVE_CONDITION);
                        if( pOriginalSig->getSignalCategory() > SignalEvent)
                        {
                            pOriginalSig->setTags(TAG_ACTIVE_ALARM);
                        }
                    }
                    else
                    {
                        pOriginalSig->resetTags(TAG_ACTIVE_CONDITION);
                        pOriginalSig->resetTags(TAG_ACTIVE_ALARM);
                    }

                    tags = ( pOriginalSig->getTags() & SIGNAL_MANAGER_MASK );

                    if(tags == 0)
                    {
                        didit = true;

                        // This guy has already been acknowledged and now has been cleared.  Get it out of the table!
                        removeFromMaps(pointid, alarm_condition);
                        pSig = pOriginalSig;        // Just return the original.
                        pOriginalSig = 0;

                        CtiTableDynamicPointAlarming::Delete(pointid, alarm_condition);
                    }
                    else
                    {
                        // Return a copy of the original.
                        pSig = (CtiSignalMsg*)pOriginalSig->replicateMessage();
                    }
                }
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return pSig;
}

CtiSignalMsg*  CtiSignalManager::setAlarmAcknowledged(long pointid, int alarm_condition, bool acked)
{
    bool didit = false;
    CtiSignalMsg *pSig = 0;

    CtiLockGuard< CtiMutex > tlg(_mux, 5000);
    while(!tlg.isAcquired())
    {
        CTILOG_WARN(dout, "Unable to acquire exclusion lock. Will retry");
        tlg.tryAcquire(5000);
    }

    try
    {
        UINT tags;
        SigMgrMap_t::key_type key = make_pair( pointid, alarm_condition );
        SigMgrMap_t::iterator itr = _map.find( key );

        if(itr != _map.end())
        {
            tags = 0;
            SigMgrMap_t::value_type vt = *itr;
            CtiSignalMsg *&pOriginalSig = vt.second;

            if(pOriginalSig)
            {
                if( ((pOriginalSig->getTags() & TAG_UNACKNOWLEDGED_ALARM) != 0) == acked )     // We must be changing it!
                {
                    setDirty(true, pOriginalSig->getId());

                    if(acked)
                    {
                        pOriginalSig->resetTags(TAG_UNACKNOWLEDGED_ALARM);
                    }
                    else
                    {
                        pOriginalSig->setTags(TAG_UNACKNOWLEDGED_ALARM);
                    }

                    tags = ( pOriginalSig->getTags() & SIGNAL_MANAGER_MASK );

                    if(tags == 0)
                    {
                        didit = true;

                        // This guy has already cleared and now been acknowledged.  Get it out of the table!
                        removeFromMaps(pointid, alarm_condition);
                        pSig = pOriginalSig;        // Just return the original.
                        pOriginalSig = 0;

                        CtiTableDynamicPointAlarming::Delete(pointid, alarm_condition);
                    }
                    else
                    {
                        // Return a copy of the original.
                        pSig = (CtiSignalMsg*)pOriginalSig->replicateMessage();
                    }
                }
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return pSig;
}

bool CtiSignalManager::isAlarmed(long pointid, int alarm_condition) const                 // The manager has an active and/or unacknowledged alarm on this condition for this point.
{
    CtiLockGuard< CtiMutex > tlg(_mux, 5000);
    while(!tlg.isAcquired())
    {
        CTILOG_WARN(dout, "Unable to acquire exclusion lock. Will retry");
        tlg.tryAcquire(5000);
    }

    return ((getConditionTags(pointid,alarm_condition) & MASK_ANY_ALARM ) != 0x00000000);
}

bool CtiSignalManager::isAlarmActive(long pointid, int alarm_condition) const             // The manager has an active alarm on this condition for this point.  It could be acknowledged or otherwise
{
    CtiLockGuard< CtiMutex > tlg(_mux, 5000);
    while(!tlg.isAcquired())
    {
        CTILOG_WARN(dout, "Unable to acquire exclusion lock. Will retry");
        tlg.tryAcquire(5000);
    }

    return ((getConditionTags(pointid,alarm_condition) & TAG_ACTIVE_ALARM ) == TAG_ACTIVE_ALARM);
}

bool CtiSignalManager::isAlarmUnacknowledged(long pointid, int alarm_condition) const      // The manager has an unacknowledged alarm on this condition for this point.  It could be active or otherwise
{
    CtiLockGuard< CtiMutex > tlg(_mux, 5000);
    while(!tlg.isAcquired())
    {
        CTILOG_WARN(dout, "Unable to acquire exclusion lock. Will retry");
        tlg.tryAcquire(5000);
    }

    return ((getConditionTags(pointid,alarm_condition) & TAG_UNACKNOWLEDGED_ALARM ) == TAG_UNACKNOWLEDGED_ALARM);
}

UINT CtiSignalManager::getConditionTags(long pointid, int alarm_condition) const      // The manager has an unacknowledged alarm on this condition for this point.  It could be active or otherwise
{
    UINT tags = 0x00000000;

    CtiLockGuard< CtiMutex > tlg(_mux, 5000);
    while(!tlg.isAcquired())
    {
        CTILOG_WARN(dout, "Unable to acquire exclusion lock. Will retry");
        tlg.tryAcquire(5000);
    }

    try
    {
        SigMgrMap_t::key_type key = make_pair( pointid, alarm_condition );
        SigMgrMap_t::const_iterator itr = _map.find( key );

        if(itr != _map.end())
        {
            SigMgrMap_t::value_type vt = *itr;
            CtiSignalMsg *&pOriginalSig = vt.second;

            if(pOriginalSig)
            {
                tags = (pOriginalSig->getTags() & SIGNAL_MANAGER_MASK );
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return tags;
}

UINT CtiSignalManager::getTagMask(long pointid) const // Returns the bitwise OR of all alarms on this point
{
    CtiLockGuard< CtiMutex > tlg(_mux, 5000);
    while(!tlg.isAcquired())
    {
        CTILOG_WARN(dout, "Unable to acquire exclusion lock. Will retry");
        tlg.tryAcquire(5000);
    }

    UINT mask = 0x00000000;

    // Look through all pending alarms until both bits are set or until all alarms are studied.
    PointSignalMap_t::const_iterator itr;
    itr = _pointMap.find(pointid);

    if( itr != _pointMap.end() )
    {
        for(; itr != _pointMap.end() && itr->first == pointid; itr++)
        {
            PointSignalMap_t::value_type     vt      = *itr;
            PointSignalMap_t::key_type       key     = vt.first;
            CtiSignalMsg                     *pSig   = vt.second;

            if(pSig && key == pointid)
            {
                mask |= (pSig->getTags() & SIGNAL_MANAGER_MASK);

                if(mask == SIGNAL_MANAGER_MASK)
                {
                    break;      // No point in looking any further.  We have ack and active already indicated for this pointid.
                }
            }
        }
    }

    return mask;
}

CtiSignalMsg* CtiSignalManager::getAlarm(long pointid, int alarm_condition) const        // Returns a copy of the alarm for this pointid and alarmcondition.  Could return NULL
{
    CtiLockGuard< CtiMutex > tlg(_mux, 5000);
    while(!tlg.isAcquired())
    {
        CTILOG_WARN(dout, "Unable to acquire exclusion lock. Will retry");
        tlg.tryAcquire(5000);
    }

    try
    {
        SigMgrMap_t::key_type key = make_pair( pointid, alarm_condition );
        SigMgrMap_t::const_iterator itr = _map.find( key );

        if(itr != _map.end())
        {
            SigMgrMap_t::value_type vt = *itr;
            CtiSignalMsg *pOriginalSig = vt.second;

            if(pOriginalSig)
            {
                return (CtiSignalMsg*)(pOriginalSig->replicateMessage());
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return NULL;
}


CtiMultiMsg* CtiSignalManager::getPointSignals(long pointid) const
{
    CtiLockGuard< CtiMutex > tlg(_mux, 5000);
    while(!tlg.isAcquired())
    {
        CTILOG_WARN(dout, "Unable to acquire exclusion lock. Will retry");
        tlg.tryAcquire(5000);
    }

    std::auto_ptr<CtiMultiMsg>       pMulti(new CtiMultiMsg);
    PointSignalMap_t::const_iterator itr;

    itr = _pointMap.find(pointid);
    if( itr != _pointMap.end() )
    {
        try
        {
            for(; itr != _pointMap.end() && itr->first == pointid; itr++)
            {
                PointSignalMap_t::value_type vt = *itr;
                PointSignalMap_t::key_type   key = vt.first;
                CtiSignalMsg *pOriginalSig = vt.second;

                if(key == pointid && pOriginalSig)
                {
                    std::auto_ptr<CtiSignalMsg> pSig((CtiSignalMsg*)(pOriginalSig->replicateMessage()));
                    pSig->setText( TrimAlarmTagText((string &)pSig->getText())+ AlarmTagsToString(pSig->getTags()) );

                    if(pMulti.get())
                    {
                        pMulti->insert(pSig.release());
                    }
                }
            }
        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
        }
    }

    return pMulti.release();
}

CtiMultiMsg* CtiSignalManager::getAllAlarmSignals() const
{
    CtiLockGuard< CtiMutex > tlg(_mux, 5000);
    while(!tlg.isAcquired())
    {
        CTILOG_WARN(dout, "Unable to acquire exclusion lock. Will retry");
        tlg.tryAcquire(5000);
    }

    std::auto_ptr<CtiMultiMsg>  pMulti;
    SigMgrMap_t::const_iterator itr;

    try
    {
        for(itr = _map.begin(); itr != _map.end(); itr++)
        {
            SigMgrMap_t::value_type vt = *itr;
            SigMgrMap_t::key_type   key = vt.first;
            CtiSignalMsg *pOriginalSig = vt.second;

            if(pOriginalSig && (pOriginalSig->getTags() & MASK_ANY_ALARM))
            {
                std::auto_ptr<CtiSignalMsg> pSig((CtiSignalMsg*)(pOriginalSig->replicateMessage()));
                pSig->setText( TrimAlarmTagText((string &)pSig->getText())+ AlarmTagsToString(pSig->getTags()) );

                if( ! pMulti.get() )
                {
                    pMulti.reset(new CtiMultiMsg);
                }

                pMulti->insert(pSig.release());
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return pMulti.release();
}

size_t CtiSignalManager::entries() const
{
    CtiLockGuard< CtiMutex > tlg(_mux, 5000);
    while(!tlg.isAcquired())
    {
        CTILOG_WARN(dout, "Unable to acquire exclusion lock. Will retry");
        tlg.tryAcquire(5000);
    }

    return _map.size();
}

size_t CtiSignalManager::pointMapEntries() const
{
    CtiLockGuard< CtiMutex > tlg(_mux, 5000);
    while(!tlg.isAcquired())
    {
        CTILOG_WARN(dout, "Unable to acquire exclusion lock. Will retry");
        tlg.tryAcquire(5000);
    }

    return _pointMap.size();
}

bool CtiSignalManager::empty() const
{
    CtiLockGuard< CtiMutex > tlg(_mux, 5000);
    while(!tlg.isAcquired())
    {
        CTILOG_WARN(dout, "Unable to acquire exclusion lock. Will retry");
        tlg.tryAcquire(5000);
    }

    return _map.empty();
}

bool CtiSignalManager::dirty() const
{
    CtiLockGuard< CtiMutex > tlg(_mux, 5000);
    while(!tlg.isAcquired())
    {
        CTILOG_WARN(dout, "Unable to acquire exclusion lock. Will retry");
        tlg.tryAcquire(5000);
    }

    return _dirty;
}

//This is still a "global" dirty flag, there is no individual resetting of dirty.
//You should never ever call setDirty(false, <anything but 0>)
void CtiSignalManager::setDirty(bool flag, long paoID)
{
    CtiLockGuard< CtiMutex > tlg(_mux, 5000);
    while(!tlg.isAcquired())
    {
        CTILOG_WARN(dout, "Unable to acquire exclusion lock. Will retry");
        tlg.tryAcquire(5000);
    }

    if( flag == true )
    {
        _dirtySignals.insert(paoID);
    }
    else
    {
        _dirtySignals.clear();
    }

    _dirty = flag;
    return;
}

UINT CtiSignalManager::writeDynamicSignalsToDB()
{
    CtiLockGuard< CtiMutex > tlg(_mux, 5000);
    while(!tlg.isAcquired())
    {
        CTILOG_WARN(dout, "Unable to acquire exclusion lock. Will retry");
        tlg.tryAcquire(5000);
    }

    CtiSignalMsg *pSig = 0;
    SigMgrMap_t::iterator itr;

    UINT count = 0;

    try
    {
        if(!empty() && dirty())
        {
            Cti::Database::DatabaseConnection   conn;

            if ( ! conn.isValid() )
            {
                CTILOG_ERROR(dout, "Invalid Connection to Database");
                return 0;
            }

            CtiTime start;

            bool updateError = false;

            for(itr = _map.begin(); itr != _map.end(); itr++)
            {
                SigMgrMap_t::value_type vt = *itr;
                SigMgrMap_t::key_type   key = vt.first;

                pSig = vt.second;

                if(pSig && _dirtySignals.find(pSig->getId()) != _dirtySignals.end())
                {
                    CtiTableDynamicPointAlarming ptAlm;

                    ptAlm.setPointID( pSig->getId() );
                    ptAlm.setAlarmCondition( pSig->getCondition() );
                    ptAlm.setCategoryID( pSig->getSignalCategory() );
                    ptAlm.setAlarmTime( pSig->getMessageTime() );
                    ptAlm.setAction( pSig->getText() );
                    ptAlm.setDescription( pSig->getAdditionalInfo() );
                    ptAlm.setTags( pSig->getTags() & SIGNAL_MANAGER_MASK );
                    ptAlm.setLogID( pSig->getLogID() );

                    ptAlm.setSOE( pSig->getSOE() );
                    ptAlm.setLogType( pSig->getLogType() );
                    ptAlm.setUser( pSig->getUser() );

                    if( ! ptAlm.Update( conn ) )
                    {
                        CTILOG_ERROR(dout, "Writing dynamic signals to Database failed"<<
                                ptAlm);

                        updateError = true; // an update has fail, do not reset the dirty flag
                    }
                    else
                    {
                        count++;
                    }
                }
            }

            CtiTime stop;

            if((stop.seconds() - start.seconds()) > 5)
            {
                CTILOG_INFO(dout, "Writing dynamic signals took "<< (stop.seconds() - start.seconds()) <<" seconds and wrote "<< count <<" entries");
            }

            if(! updateError)
            {
                setDirty(false, 0);
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return count;
}


CtiMultiMsg* CtiSignalManager::getCategorySignals(unsigned category) const
{
    CtiLockGuard< CtiMutex > tlg(_mux, 5000);
    while(!tlg.isAcquired())
    {
        CTILOG_WARN(dout, "Unable to acquire exclusion lock. Will retry");
        tlg.tryAcquire(5000);
    }

    std::auto_ptr<CtiMultiMsg> pMulti(new CtiMultiMsg);

    SigMgrMap_t::const_iterator itr;

    try
    {
        for(itr = _map.begin(); itr != _map.end(); itr++)
        {
            SigMgrMap_t::value_type vt = *itr;
            SigMgrMap_t::key_type   key = vt.first;
            CtiSignalMsg *pOriginalSig = vt.second;

            if( pOriginalSig->getSignalCategory() == category )
            {
                std::auto_ptr<CtiSignalMsg> pSig((CtiSignalMsg*)(pOriginalSig->replicateMessage()));
                pSig->setText( TrimAlarmTagText((string&)pSig->getText())+ AlarmTagsToString(pSig->getTags()) );

                pMulti->insert(pSig.release()); // Insert into the multi.
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return pMulti.release();
}

void CtiSignalManager::removeFromMaps(long pointID, int condition)
{
    SigMgrMap_t::iterator itr = _map.find(make_pair(pointID, condition));
    if( itr != _map.end() )
    {
        _map.erase( itr );
    }

    PointSignalMap_t::iterator pointIter = _pointMap.find(pointID);
    if( pointIter != _pointMap.end() )
    {
        do
        {
            CtiSignalMsg* pSignal = pointIter->second;
            if( pSignal && pSignal->getCondition() == condition )
            {
                pointIter = _pointMap.erase(pointIter);
            }
            else
            {
                pointIter++;
            }

        } while(pointIter != _pointMap.end() && pointIter->first == pointID);
    }
}
