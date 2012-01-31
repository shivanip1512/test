/*-----------------------------------------------------------------------------*
*
* File:   mgr_mcsched
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/mgr_mcsched.cpp-arc  $
* REVISION     :  $Revision: 1.15.24.1 $
* DATE         :  $Date: 2008/11/21 20:56:59 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include <algorithm>

#include "mgr_mcsched.h"
#include "dbaccess.h"
#include "utility.h"
#include "database_connection.h"
#include "database_reader.h"
using namespace std;

ostream& operator<<( ostream& ostrm, CtiMCScheduleManager& mgr )
{
    CtiMCSchedule *sched = NULL;
    try
    {
        CtiLockGuard<CtiMutex> guard(mgr.getMux() );

        ostrm << " " << mgr.getMap().size() << " schedules are loaded." << endl;

        CtiMCScheduleManager::MapIterator itr = mgr.getMap().begin();

        for( ; itr != mgr.getMap().end() ; ++itr)
        {
            sched = (*itr).second;

            {
                CtiLockGuard<CtiMutex> sched_guard(sched->getMux());
                ostrm << CtiTime() << endl << *sched << endl;
            }
        }
    }
    catch(RWExternalErr e )
    {
        CtiLockGuard<CtiLogger> guard(dout);
        ostrm <<      CtiTime()
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
    map<long, CtiMCSchedule* > temp_map;
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
            dout << CtiTime() << " Exception:  " << e.why() << endl;
        }
    }
    catch(...)
    {
        success = false;
        {
            CtiLockGuard<CtiLogger> guard(dout);
            dout
            << CtiTime()
            << " Unknown exception occured in CtiMCScheduleManager::refreshAllSchedules()"
            << endl;
        }
    }

    if( success )
    {
        // Lock down our working map and attempt to copy in the
        // schedules from our temporary map
        CtiLockGuard<CtiMutex> guard( getMux() );

        // clean up the old schedules and replace them with
        // the new ones

        //deletes memory pointed to in the value pointer
        for (MapIterator itr = Map.begin(); itr != Map.end(); itr++) {
            delete (*itr).second;
        }
        Map.clear();
        Map = temp_map;
    }
    else
    {
        // Clean up the temporary map since it was never copied to
        // the real one

        for (MapIterator itr = temp_map.begin(); itr != temp_map.end(); itr++) {
            delete (*itr).second;
        }
        temp_map.clear();
        {
            CtiLockGuard<CtiLogger> guard(dout);
            dout << CtiTime() << " An error occured retrieving schedules from the database" << endl;
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

    CtiLockGuard<CtiMutex> guard( getMux() );

    CtiMCScheduleManager::MapIterator itr = getMap().begin();
    CtiMCSchedule* sched;

    for(; itr != getMap().end() ; ++itr)
    {
        sched = (*itr).second;

        {
            CtiLockGuard<CtiMutex> sched_guard(sched->getMux());

            // Is this schedule already persisted in the database?
            if( !sched->getUpdatedFlag() )
            {

                // add the schedule to the database
                if( gMacsDebugLevel & MC_DEBUG_DB )
                {
                    CtiLockGuard< CtiLogger > g(dout);
                    dout << CtiTime() << " Inserting schedule into the database:  " << sched->getScheduleID() << endl;
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
                    dout << CtiTime() << " Update schedule in the database:  " << sched->getScheduleID() << endl;
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
            dout << CtiTime() << " Deleteing schedule from the database:  " << (*iter)->getScheduleID() << endl;
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
    CtiLockGuard<CtiMutex> guard( getMux() );

    // Don't allow a new schedule with the same name as any existing schedule
    if( getID(sched.getScheduleName()) != -1)
    {
    return NULL;
    }

    // We have to assign it a schedule id
    long id = nextScheduleID();

    if ( id == 0 )
    {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << CtiTime() << " **** ERROR **** Invalid Connection to Database.  " << __FILE__ << " (" << __LINE__ << ")" << std::endl;

        return NULL;
    }

    CtiMCSchedule* sched_to_add = (CtiMCSchedule*) sched.replicateMessage();
    sched_to_add->setScheduleID( id );


    std::pair< std::map<long,CtiMCSchedule*>::iterator,bool> aPair =
        Map.insert( std::pair<long,CtiMCSchedule*>(id, sched_to_add) );
    if( !aPair.second )
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
        CtiLockGuard<CtiMutex> guard( getMux() );

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

    CtiLockGuard<CtiMutex> guard( getMux() );


    CtiMCSchedule* to_delete;
    MapIterator itr = Map.find(sched_id);
    if ( itr != Map.end() )
        to_delete = (*itr).second;
    else
        to_delete = NULL;

    // If the schedule exists, remove it from the database
    // and then remove it from memory
    if( to_delete != NULL && to_delete->Delete() )
    {
        long key_p = NULL;
        MapIterator itr2 = Map.find( sched_id );

        if (itr2 != Map.end() ){
            Map.erase( sched_id );
            key_p =  (*itr2).first;
        }
        else
            key_p = 0;

        if( key_p != 0 )
        {

            pair< set< CtiMCSchedule* >::iterator, bool > result = _schedules_to_delete.insert( to_delete );

            if( result.second )
            {
                if( gMacsDebugLevel & MC_DEBUG_DB )
                {
                    CtiLockGuard< CtiLogger > g(dout);
                    dout << CtiTime() << " Successfully added:  " << to_delete << " to the delete set" << endl;
                }
            }
            else
            {
                CtiLockGuard< CtiLogger > g(dout);
                dout << CtiTime() << " Failed to add:  " << to_delete << " it was already in the set" << endl;
            }

            ret_val = true;
        }
    }

    return ret_val;
}

CtiMCSchedule* CtiMCScheduleManager::findSchedule(long id)
{
    CtiLockGuard<CtiMutex> guard( getMux() );

    MapIterator itr = Map.find(id);
    if ( itr != Map.end() ) {
        return (*itr).second;
    }else
        return NULL;
}

/*----------------------------------------------------------------------------
  getID

  Returns the id of a schedule with a name of name
  -1 if no schedule was found
----------------------------------------------------------------------------*/

long CtiMCScheduleManager::getID(const string& name)
{
    CtiLockGuard<CtiMutex> guard( getMux() );

    CtiMCScheduleManager::MapIterator itr = Map.begin();

    CtiMCSchedule *sched = NULL;
    for(; itr != Map.end() ; ++itr)
    {
        sched = (*itr).second;

        if( sched != NULL && string( sched->getScheduleName().c_str()) == name )
            return sched->getScheduleID();
    }

    return -1;
}

bool CtiMCScheduleManager::retrieveSimpleSchedules(
                                                  std::map
                                                  < long, CtiMCSchedule* >
                                                  &sched_map )
{
    bool success = true;
    static const string sql =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, YP.description, "
                                   "YP.disableflag, YP.paostatistics, MAC.scheduleid, MAC.categoryname, "
                                   "MAC.holidayscheduleid, MAC.commandfile, MAC.currentstate, MAC.startpolicy, "
                                   "MAC.stoppolicy, MAC.lastruntime, MAC.lastrunstatus, MAC.startday, MAC.startmonth, "
                                   "MAC.startyear, MAC.starttime, MAC.stoptime, MAC.validweekdays, MAC.duration, "
                                   "MAC.manualstarttime, MAC.manualstoptime, MAC.template, MSS.scheduleid, "
                                   "MSS.targetpaobjectid, MSS.startcommand, MSS.stopcommand, MSS.repeatinterval "
                               "FROM YukonPAObject YP, MACSchedule MAC, MACSimpleSchedule MSS "
                               "WHERE YP.category = 'Schedule' AND YP.type = 'Simple' AND "
                                   "MAC.scheduleid = YP.paobjectid AND MSS.scheduleid = YP.paobjectid AND "
                                   "MAC.scheduleid = MSS.scheduleid";

    try
    {
        {
            // Make sure all objects that that store results
            // are out of scope when the release is called
            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection, sql);
            rdr.execute();

            if( rdr.isValid() )
            {
                while( rdr() )
                {
                    long id;
                    rdr["scheduleid"] >> id;

                    CtiMCSchedule* temp_sched = new CtiMCSchedule();
                    // Add it to the map
                    temp_sched->setUpdatedFlag();   // Mark it updated
                    sched_map.insert( std::pair<long,CtiMCSchedule*>(id, temp_sched) );

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
            dout << CtiTime() << " Exception:  " << e.why() << endl;
        }
    }
    catch(...)
    {
        success = false;

        {
            CtiLockGuard<CtiLogger> guard(dout);
            dout << CtiTime() << " An unkown exception occured"  << endl;
        }
    }

    if( !success )
    {
        {
            CtiLockGuard<CtiLogger> guard(dout);
            dout << CtiTime() << " An error occured executing the following sql:"  << endl;
            dout << CtiTime() << " " << sql << endl;
        }
    }

    return success;
}

bool CtiMCScheduleManager::retrieveScriptedSchedules(
                                                    std::map
                                                    < long, CtiMCSchedule* >
                                                    &sched_map )
{
    bool success = true;
    static const string sql =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, YP.description, "
                                   "YP.disableflag, YP.paostatistics, MAC.scheduleid, MAC.categoryname, "
                                   "MAC.holidayscheduleid, MAC.commandfile, MAC.currentstate, MAC.startpolicy, "
                                   "MAC.stoppolicy, MAC.lastruntime, MAC.lastrunstatus, MAC.startday, MAC.startmonth, "
                                   "MAC.startyear, MAC.starttime, MAC.stoptime, MAC.validweekdays, MAC.duration, "
                                   "MAC.manualstarttime, MAC.manualstoptime, MAC.template "
                               "FROM YukonPAObject YP, MACSchedule MAC "
                               "WHERE YP.category = 'Schedule' AND YP.type = 'Script' AND MAC.scheduleid = YP.paobjectid";

    try
    {
        {
            // Make sure all objects that that store results
            // are out of scope when the release is called
            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection, sql);
            rdr.execute();

            if( rdr.isValid() )
            {
                while( rdr() )
                {
                    long id;

                    rdr["scheduleid"] >> id;

                    CtiMCSchedule* temp_sched = new CtiMCSchedule();

                    // Add it to the map
                    temp_sched->setUpdatedFlag();   // Mark it updated
                    sched_map.insert( std::pair<long,CtiMCSchedule*>(id, temp_sched) );

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
            dout << CtiTime() << " Exception:  " << e.why() << endl;
        }
    }
    catch(...)
    {
        success = false;

        {
            CtiLockGuard<CtiLogger> guard(dout);
            dout << CtiTime() << " An unkown exception occured"  << endl;
        }
    }

    if( !success )
    {
        {
            CtiLockGuard<CtiLogger> guard(dout);
            dout << CtiTime() << " An error occured executing the following sql:"  << endl;
            dout << CtiTime() << " " << sql << endl;
        }
    }


    return success;
}

long CtiMCScheduleManager::nextScheduleID()
{
    return PAOIdGen();
}

