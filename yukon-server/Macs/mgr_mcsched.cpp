/*-----------------------------------------------------------------------------*
*
* File:   mgr_mcsched
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/mgr_mcsched.cpp-arc  $
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2005/02/10 23:23:53 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <algorithm>

#include "mgr_mcsched.h"
#include "dbaccess.h"
#include "utility.h"

ostream& operator<<( ostream& ostrm, CtiMCScheduleManager& mgr )
{
    CtiMCSchedule *sched = NULL;
    try
    {
        CtiLockGuard< RWRecursiveLock<class RWMutexLock> > guard(mgr.getMux() );

        ostrm << " " << mgr.getMap().entries() << " schedules are loaded." << endl;

        CtiMCScheduleManager::CtiRTDBIterator itr(mgr.getMap());

        for(;itr();)
        {
            sched = itr.value();

            {
                CtiLockGuard< RWRecursiveLock<class RWMutexLock> > sched_guard(sched->getMux());
                ostrm << RWTime() << endl << *sched << endl;
            }
        }
    }
    catch(RWExternalErr e )
    {
        CtiLockGuard<CtiLogger> guard(dout);
        ostrm <<      RWTime()
        <<      " operator<<(ostream&,const CtiMCScheduleManager mgr) - "
        <<      e.why()
        <<      endl;
    }
    return ostrm;
}


bool CtiMCScheduleManager::refreshAllSchedules()
{
    bool success = true;

    // Put all the schedules from the database into a temporary map
    // and copy it to the real one when we know that it was successful
    // so we can continue to function when the database is not cooperating
    // and so we don't block more time critical threads needlessly
    RWTPtrHashMap<CtiHashKey, CtiMCSchedule, my_hash<CtiHashKey> , equal_to<CtiHashKey> > temp_map;
    try
    {
        if( !retrieveSimpleSchedules( temp_map ) )
            success = false;

        if( !retrieveScriptedSchedules( temp_map ) )
            success = false;

    }
    catch(RWExternalErr e )
    {
        success = false;
        {
            CtiLockGuard<CtiLogger> guard(dout);
            dout << RWTime() << " Exception:  " << e.why() << endl;
        }
    }
    catch(...)
    {
        success = false;
        {
            CtiLockGuard<CtiLogger> guard(dout);
            dout
            << RWTime()
            << " Unknown exception occured in CtiMCScheduleManager::refreshAllSchedules()"
            << endl;
        }
    }

    if( success )
    {
        // Lock down our working map and attempt to copy in the
        // schedules from our temporary map
        RWRecursiveLock<RWMutexLock>::LockGuard guard( getMux() );

        // clean up the old schedules and replace them with
        // the new ones
        Map.clearAndDestroy();
        Map = temp_map;
    }
    else
    {
        // Clean up the temporary map since it was never copied to
        // the real one
        temp_map.clearAndDestroy();

        {
            CtiLockGuard<CtiLogger> guard(dout);
            dout << RWTime() << " An error occured retrieving schedules from the database" << endl;
        }
    }

    return success;
}

/*----------------------------------------------------------------------------
  updateAllSchedules

  Persists all the schedules that have their dirty flag set.  Is this the
  intended use of the dirty flag?
 ----------------------------------------------------------------------------*/

bool CtiMCScheduleManager::updateAllSchedules()
{
    bool ret_val = true;

    RWRecursiveLock<RWMutexLock>::LockGuard guard( getMux() );

    CtiMCScheduleManager::CtiRTDBIterator itr(getMap());
    CtiMCSchedule* sched;

    for(;itr();)
    {
        sched = itr.value();

        {
            CtiLockGuard< RWRecursiveLock<class RWMutexLock> > sched_guard(sched->getMux());

            // Is this schedule already persisted in the database?
            if( !sched->getUpdatedFlag() )
            {

                // add the schedule to the database
                if( gMacsDebugLevel & MC_DEBUG_DB )
                {
                    CtiLockGuard< CtiLogger > g(dout);
                    dout << RWTime() << " Inserting schedule into the database:  " << sched->getScheduleID() << endl;
                }

                sched->Insert();                
            }
            else  // The schedule is in the database but the version in memory
                  // isn't consistent with the database
                if( sched->isDirty() )
            {
                if( gMacsDebugLevel & MC_DEBUG_DB )
                {
                    CtiLockGuard< CtiLogger > g(dout);
                    dout << RWTime() << " Update schedule in the database:  " << sched->getScheduleID() << endl;
                }

                sched->Update();
            }
        }
    }

    // Delete any schedules from the database as necessary
    for( set< CtiMCSchedule* >::iterator iter = _schedules_to_delete.begin();
       iter != _schedules_to_delete.end();
       iter++ )
    {
        if( gMacsDebugLevel & MC_DEBUG_DB )
        {
            CtiLockGuard< CtiLogger > g(dout);
            dout << RWTime() << " Deleteing schedule from the database:  " << (*iter)->getScheduleID() << endl;
        }

        delete *iter;
    }

    _schedules_to_delete.clear();

    return ret_val;
}

/*
    addSchedule

    Assigns a schedule id to the given schedule and puts it into the
    schedule map.

    Returns a pointer to the internal CtiMCSchedule, which is
    not the same as the address of the one passed in.

    Returns NULL to indicate failure.
*/
CtiMCSchedule* CtiMCScheduleManager::addSchedule(const CtiMCSchedule& sched)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( getMux() );
    
    // Don't allow a new schedule with the same name as any existing schedule
    if( getID(sched.getScheduleName()) != -1)
    {
	return NULL;
    }

    // We have to assign it a schedule id
    long id = nextScheduleID();

    CtiMCSchedule* sched_to_add = (CtiMCSchedule*) sched.replicateMessage();
    sched_to_add->setScheduleID( id );
	
    if( !Map.insert( new CtiHashKey(id), sched_to_add ) )
    {
        // Failed!
        delete sched_to_add;
        return NULL;
    }

    return sched_to_add;
}

bool CtiMCScheduleManager::updateSchedule(const CtiMCSchedule& sched)
{
    bool ret_val = false;
    long id = sched.getScheduleID();

    if( id > 0 )
    {
        RWRecursiveLock<RWMutexLock>::LockGuard guard( getMux() );

        //Don't allow a schedule to have the same name as any existing schedule
	long temp_id = getID(sched.getScheduleName());

	if( temp_id != -1 && temp_id != id)
	{
	    return false;
	}
	
        CtiMCSchedule* sched_to_update = findSchedule( id );

        if( sched_to_update != NULL )
        {
            *sched_to_update = sched;
            ret_val = true;
        }
    }

    return ret_val;
}

bool CtiMCScheduleManager::deleteSchedule(long sched_id)
{
    bool ret_val = false;

    RWRecursiveLock<RWMutexLock>::LockGuard guard( getMux() );

    CtiHashKey key(sched_id);
    CtiMCSchedule* to_delete = Map.findValue(&key);

    // If the schedule exists, remove it from the database
    // and then remove it from memory
    if( to_delete != NULL && to_delete->Delete() )
    {
        CtiHashKey* key_ptr = NULL;
        key_ptr = Map.remove( &key );

        if( key_ptr != NULL )
        {

            delete key_ptr;
            //delete to_delete;

            pair< set< CtiMCSchedule* >::iterator, bool > result = _schedules_to_delete.insert( to_delete );

            if( result.second )
            {
                if( gMacsDebugLevel & MC_DEBUG_DB )
                {
                    CtiLockGuard< CtiLogger > g(dout);
                    dout << RWTime() << " Successfully added:  " << to_delete << " to the delete set" << endl;
                }
            }
            else
            {
                CtiLockGuard< CtiLogger > g(dout);
                dout << RWTime() << " Failed to add:  " << to_delete << " it was already in the set" << endl;
            }

            ret_val = true;
        }
    }

    return ret_val;
}

CtiMCSchedule* CtiMCScheduleManager::findSchedule(long id)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( getMux() );
    CtiHashKey key(id);
    return Map.findValue(&key);
}

/*----------------------------------------------------------------------------
  getID

  Returns the id of a schedule with a name of name
  -1 if no schedule was found
----------------------------------------------------------------------------*/

long CtiMCScheduleManager::getID(const string& name)
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( getMux() );

    CtiMCScheduleManager::CtiRTDBIterator itr(Map);

    CtiMCSchedule *sched = NULL;
    for(;itr();)
    {
        sched = itr.value();

        if( sched != NULL && string( sched->getScheduleName().data()) == name )
            return sched->getScheduleID();
    }

    return -1;
}

bool CtiMCScheduleManager::retrieveSimpleSchedules(
                                                  RWTPtrHashMap
                                                  < CtiHashKey, CtiMCSchedule, my_hash<CtiHashKey> , equal_to<CtiHashKey> >&
                                                  sched_map )
{
    bool success = true;
    string sql;

    try
    {
        {
            // Make sure all objects that that store results
            // are out of scope when the release is called
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();


            RWDBDatabase   db       = conn.database();
            RWDBSelector   selector = conn.database().selector();
            RWDBTable      pao_table;
            RWDBTable      mc_sched_table;
            RWDBTable      mc_simple_sched_table;
            RWDBReader     rdr;
            RWDBStatus     status;


            //First grab the simple schedules
            CtiTblPAO().getSQL( db, pao_table, selector );
            CtiMCSchedule::getSQL( db, mc_sched_table, selector );
            CtiTableMCSimpleSchedule::getSQL( db, mc_simple_sched_table, selector );
            selector.where( selector.where() &&
                            pao_table["category"] == RWDBExpr("Schedule") &&
                            pao_table["type"] == RWDBExpr("Simple") &&
                            mc_sched_table["scheduleid"] == pao_table["paobjectid"] &&
                            mc_simple_sched_table["scheduleid"] == pao_table["paobjectid"] &&
                            mc_sched_table["scheduleid"] == mc_simple_sched_table["scheduleid"]);

            //mc_simple_sched_table["scheduleid"] == mc_sched_table["scheduleid"] &&
            //mc_sched_table["scheduletype"]==RWDBExpr("Simple") );

            rdr = selector.reader(conn);
            sql = selector.asString();
            status = selector.status();

            if( status.errorCode() == RWDBStatus::ok )
            {
                while( rdr() )
                {
                    long id;
                    rdr["scheduleid"] >> id;

                    CtiMCSchedule* temp_sched = new CtiMCSchedule();
                    // Add it to the map
                    temp_sched->setUpdatedFlag();   // Mark it updated
                    sched_map.insert( new CtiHashKey(id), temp_sched );

                    if( !temp_sched->DecodeDatabaseReader(rdr) )
                    {
                        success = false;
                        break;
                    }
                }
            }
            else
            {
                success = false;
            }
        }
    }
    catch(RWExternalErr e )
    {
        success = false;

        {
            CtiLockGuard<CtiLogger> guard(dout);
            dout << RWTime() << " Exception:  " << e.why() << endl;
        }
    }
    catch(...)
    {
        success = false;

        {
            CtiLockGuard<CtiLogger> guard(dout);
            dout << RWTime() << " An unkown exception occured"  << endl;
        }
    }

    if( !success )
    {
        {
            CtiLockGuard<CtiLogger> guard(dout);
            dout << RWTime() << " An error occured executing the following sql:"  << endl;
            dout << RWTime() << " " << sql << endl;
        }
    }

    return success;
}

bool CtiMCScheduleManager::retrieveScriptedSchedules(
                                                    RWTPtrHashMap
                                                    < CtiHashKey, CtiMCSchedule, my_hash<CtiHashKey> , equal_to<CtiHashKey> >&
                                                    sched_map )
{
    bool success = true;
    string sql;

    try
    {
        {
            // Make sure all objects that that store results
            // are out of scope when the release is called
            CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
            RWDBConnection conn = getConnection();


            RWDBDatabase   db       = conn.database();
            RWDBSelector   selector = conn.database().selector();
            RWDBTable      pao_table;
            RWDBTable      mc_sched_table;
            RWDBReader     rdr;

            RWDBStatus     status;

            CtiTblPAO().getSQL( db, pao_table, selector );
            CtiMCSchedule::getSQL( db, mc_sched_table, selector );
            selector.where( pao_table["category"] == RWDBExpr("Schedule") &&
                            pao_table["type"] == RWDBExpr("Script") &&
                            mc_sched_table["scheduleid"] == pao_table["paobjectid"] );


            //    selector.where() &&  mc_sched_table["scheduletype"]==RWDBExpr("Script") );

            rdr = selector.reader(conn);

            sql = selector.asString();
            status = selector.status();

            if( status.errorCode() == RWDBStatus::ok )
            {
                while( rdr() )
                {
                    long id;

                    rdr["scheduleid"] >> id;

                    CtiMCSchedule* temp_sched = new CtiMCSchedule();
                    // Add it to the map
                    temp_sched->setUpdatedFlag();   // Mark it updated
                    sched_map.insert( new CtiHashKey(id), temp_sched );

                    if( !temp_sched->DecodeDatabaseReader(rdr) )
                    {
                        success = false;
                        break;
                    }
                }
            }
            else
            {
                success = false;
            }
        }
    }
    catch(RWExternalErr e )
    {
        success = false;

        {
            CtiLockGuard<CtiLogger> guard(dout);
            dout << RWTime() << " Exception:  " << e.why() << endl;
        }
    }
    catch(...)
    {
        success = false;

        {
            CtiLockGuard<CtiLogger> guard(dout);
            dout << RWTime() << " An unkown exception occured"  << endl;
        }
    }

    if( !success )
    {
        {
            CtiLockGuard<CtiLogger> guard(dout);
            dout << RWTime() << " An error occured executing the following sql:"  << endl;
            dout << RWTime() << " " << sql << endl;
        }
    }


    return success;
}

long CtiMCScheduleManager::nextScheduleID()
{
    return PAOIdGen();
}

