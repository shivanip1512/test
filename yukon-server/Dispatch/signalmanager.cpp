

/*-----------------------------------------------------------------------------*
*
* File:   signalmanager
*
* Date:   8/13/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2004/05/19 14:53:21 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)

#include "dbaccess.h"
#include "logger.h"
#include "guard.h"
#include "pointdefs.h"
#include "sema.h"
#include "signalmanager.h"
#include "tbl_dyn_ptalarming.h"

extern RWCString AlarmTagsToString(UINT tags);

CtiSignalManager::CtiSignalManager() :
_dirty(false)
{
}

CtiSignalManager::CtiSignalManager(const CtiSignalManager& aRef) :
_dirty(false)
{
    *this = aRef;
}

CtiSignalManager::~CtiSignalManager()
{
    SigMgrMap_t::iterator itr;

    CtiLockGuard< CtiMutex > tlg(_mux, 5000);
    while(!tlg.isAcquired())
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
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

CtiSignalManager& CtiSignalManager::operator=(const CtiSignalManager& aRef)
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

CtiSignalManager& CtiSignalManager::addSignal(const CtiSignalMsg &sig)                                               // The manager adds an active and unacknowledged alarm on this condition for this point.
{
    try
    {
        if( (sig.getSignalCategory() > SignalEvent && (sig.getTags() & MASK_ANY_ALARM) != 0) )
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

            if(sig.getCondition() >= 0)
            {
                if(sig.getSignalCategory() > SignalEvent)
                {
                    setDirty(true);

                    SigMgrMap_t::key_type key = make_pair( sig.getId(), sig.getCondition() );
                    pair< SigMgrMap_t::iterator, bool > ip = _map.insert( make_pair( key, (CtiSignalMsg*)sig.replicateMessage() ) );

                    if(ip.second != true)
                    {
                        if(DebugLevel & DEBUGLEVEL_LUDICROUS)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** INSERT COLLISION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }

                        SigMgrMap_t::iterator itr = ip.first;
                        {
                            if(itr != _map.end())
                            {
                                SigMgrMap_t::value_type vt = *itr;
                                CtiSignalMsg *pOriginalSig = vt.second;

                                if(pOriginalSig)
                                {
                                    if(DebugLevel & DEBUGLEVEL_LUDICROUS)
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                        dout << " ORIGINAL :" << endl;

                                        pOriginalSig->dump();
                                    }

                                    *pOriginalSig = sig;

                                    if(DebugLevel & DEBUGLEVEL_LUDICROUS)
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                        dout << " NEW :" << endl;

                                        pOriginalSig->dump();
                                    }

                                }
                            }
                        }
                    }
                }
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

    return *this;
}

CtiSignalMsg*  CtiSignalManager::setAlarmActive(long pointid, int alarm_condition, bool active)
{
    bool didit = false;
    CtiSignalMsg *pSig = 0;

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
                if( ((pOriginalSig->getTags() & TAG_ACTIVE_ALARM) != 0) != active )     // We must be changing it!
                {
                    setDirty(true);

                    if(active)
                    {
                        pOriginalSig->setTags(TAG_ACTIVE_ALARM);
                    }
                    else
                    {
                        pOriginalSig->resetTags(TAG_ACTIVE_ALARM);
                    }

                    tags = ( pOriginalSig->getTags() & MASK_ANY_ALARM );

                    if(tags == 0)
                    {
                        didit = true;

                        // This guy has already been acknowledged and now has been cleared.  Get it out of the table!
                        _map.erase( itr );
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
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
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
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
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
                    setDirty(true);

                    if(acked)
                    {
                        pOriginalSig->resetTags(TAG_UNACKNOWLEDGED_ALARM);
                    }
                    else
                    {
                        pOriginalSig->setTags(TAG_UNACKNOWLEDGED_ALARM);
                    }

                    tags = ( pOriginalSig->getTags() & MASK_ANY_ALARM );

                    if(tags == 0)
                    {
                        didit = true;

                        // This guy has already cleared and now been acknowledged.  Get it out of the table!
                        _map.erase( itr );
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
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return pSig;
}

bool CtiSignalManager::isAlarmed(long pointid, int alarm_condition) const                 // The manager has an active and/or unacknowledged alarm on this condition for this point.
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

    return ((getConditionTags(pointid,alarm_condition) & MASK_ANY_ALARM ) != 0x00000000);
}
bool CtiSignalManager::isAlarmActive(long pointid, int alarm_condition) const             // The manager has an active alarm on this condition for this point.  It could be acknowledged or otherwise
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

    return ((getConditionTags(pointid,alarm_condition) & TAG_ACTIVE_ALARM ) == TAG_ACTIVE_ALARM);
}
bool CtiSignalManager::isAlarmUnacknowledged(long pointid, int alarm_condition) const      // The manager has an unacknowledged alarm on this condition for this point.  It could be active or otherwise
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

    return ((getConditionTags(pointid,alarm_condition) & TAG_UNACKNOWLEDGED_ALARM ) == TAG_UNACKNOWLEDGED_ALARM);
}

UINT CtiSignalManager::getConditionTags(long pointid, int alarm_condition) const      // The manager has an unacknowledged alarm on this condition for this point.  It could be active or otherwise
{
    UINT tags = 0x00000000;

    CtiLockGuard< CtiMutex > tlg(_mux, 5000);
    while(!tlg.isAcquired())
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
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
                tags = (pOriginalSig->getTags() & MASK_ANY_ALARM );
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

    return tags;
}

UINT CtiSignalManager::getAlarmMask(long pointid) const // Returns the bitwise OR of all alarms on this point
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

    UINT mask = 0x00000000;

    // Look through all pending alarms until both bits are set or until all alarms are studied.
    SigMgrMap_t::const_iterator itr;

    for(itr = _map.begin(); itr != _map.end(); itr++)
    {
        SigMgrMap_t::value_type     vt      = *itr;
        SigMgrMap_t::key_type       key     = vt.first;
        CtiSignalMsg                *pSig   = vt.second;

        if(pSig && key.first == pointid)
        {
            mask |= (pSig->getTags() & MASK_ANY_ALARM);

            if(mask == MASK_ANY_ALARM)
            {
                break;      // No point in looking any further.  We have ack and active already indicated for this pointid.
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
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
        tlg.tryAcquire(5000);
    }

    CtiSignalMsg *pSig = 0;

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
                pSig = (CtiSignalMsg*)(pOriginalSig->replicateMessage());
            }
        }
    }
    catch(...)
    {
        delete pSig;
        pSig = 0;
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return pSig;
}


CtiMultiMsg* CtiSignalManager::getPointSignals(long pointid) const
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
    CtiSignalMsg *pSig = 0;
    SigMgrMap_t::const_iterator itr;

    try
    {
        for(itr = _map.begin(); itr != _map.end(); itr++)
        {
            SigMgrMap_t::value_type vt = *itr;
            SigMgrMap_t::key_type   key = vt.first;
            CtiSignalMsg *pOriginalSig = vt.second;

            if(key.first == pointid && pOriginalSig)
            {
                pSig = (CtiSignalMsg*)(pOriginalSig->replicateMessage());
                pSig->setText( pSig->getText()+ AlarmTagsToString(pSig->getTags()) );

                if(!pMulti)
                {
                    pMulti = new CtiMultiMsg;
                }
                if(pMulti)
                {
                    pMulti->insert(pSig);
                }
            }
        }
    }
    catch(...)
    {
        delete pSig;
        pSig = 0;
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return pMulti;
}

size_t CtiSignalManager::entries() const
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

    return _map.size();
}

bool CtiSignalManager::empty() const
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

    return _map.empty();
}

bool CtiSignalManager::dirty() const
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

void CtiSignalManager::setDirty(bool set)
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

UINT CtiSignalManager::writeDynamicSignalsToDB()
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

    CtiSignalMsg *pSig = 0;
    SigMgrMap_t::iterator itr;

    UINT count = 0;

    try
    {
        if(!empty() && dirty())
        {
            RWCString dpa("dyn_pt_alm");
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();

            if(conn.isValid())
            {
                conn.beginTransaction(dpa);

                for(itr = _map.begin(); conn.isValid() && itr != _map.end(); itr++)
                {
                    SigMgrMap_t::value_type vt = *itr;
                    SigMgrMap_t::key_type   key = vt.first;

                    pSig = vt.second;

                    if(pSig)
                    {
                        CtiTableDynamicPointAlarming ptAlm;

                        ptAlm.setPointID( pSig->getId() );
                        ptAlm.setAlarmCondition( pSig->getCondition() );
                        ptAlm.setCategoryID( pSig->getSignalCategory() );
                        ptAlm.setAlarmTime( pSig->getMessageTime() );
                        ptAlm.setAction( pSig->getText() );
                        ptAlm.setDescription( pSig->getAdditionalInfo() );
                        ptAlm.setTags( pSig->getTags() & MASK_ANY_ALARM );
                        ptAlm.setLogID( pSig->getLogID() );

                        ptAlm.setSOE( pSig->getSOE() );
                        ptAlm.setLogType( pSig->getLogType() );
                        ptAlm.setUser( pSig->getUser() );

                        ptAlm.Update( conn );
                        count++;
                    }

                    pSig = 0;
                }

                conn.commitTransaction(dpa);
            }

            setDirty(false);
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return count;
}
