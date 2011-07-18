/*-----------------------------------------------------------------------------*
*
* File:   pttrigger
*
* Date:   5/16/2006
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/pttrigger.cpp-arc  $
* REVISION     :  $Revision: 1.5.2.1 $
* DATE         :  $Date: 2008/11/18 20:11:28 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include <boost/shared_ptr.hpp>
#include "boostutil.h"
using boost::shared_ptr;

#include "mgr_point.h"
#include "pt_base.h"
#include "pt_dyn_dispatch.h"
#include "tbl_pt_trigger.h"
#include "pttrigger.h"
#include "database_reader.h"
using namespace std;

CtiPointTriggerManager::CtiPointTriggerManager()
{

}

CtiPointTriggerManager::~CtiPointTriggerManager()
{
    _pointIDMap.clear();
    _verificationIDMap.clear();
    _triggerIDMap.clear();
}

void CtiPointTriggerManager::refreshList(long ptID, CtiPointManager& pointMgr)
{
    bool     rowFound = false;

    CtiTime start, stop;

    try
    {
        LockGuard guard(_mapMux);

        {
            start = start.now();
            if(DebugLevel & 0x00010000)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Looking for Trigger Information" << endl;
            }

            const string sql = CtiTablePointTrigger::getSQLCoreStatement(ptID);

            //Pick just the 1 point if thats all I requested
            if(ptID != 0)
            {
                erase(ptID);
            }

            Cti::Database::DatabaseConnection connection;
            Cti::Database::DatabaseReader rdr(connection, sql);

            if( ptID != 0 )
            {
                rdr << ptID;
            }

            rdr.execute();
            if(DebugLevel & 0x00010000 || !rdr.isValid())
            {
                string loggedSQLstring = rdr.asString();
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << loggedSQLstring << endl;
                }
            }
            refreshTriggerData(ptID, rdr, pointMgr);
            if(DebugLevel & 0x00010000)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Done Looking for Trigger Information" << endl;
            }
            if((stop = stop.now()).seconds() - start.seconds() > 5 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << stop.seconds() - start.seconds() << " seconds for Trigger Info " << endl;
            }
        }
    }
    catch(RWExternalErr e )
    {
        //Make sure the list is cleared
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Attempting to clear trigger list..." << endl;
        }
        _pointIDMap.clear();
        _verificationIDMap.clear();
        _triggerIDMap.clear();

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "refreshList:  " << e.why() << endl;
        }
        RWTHROW(e);

    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
}

void CtiPointTriggerManager::refreshTriggerData(long pointID, Cti::RowReader& rdr, CtiPointManager &pointMgr)
{
    LONG        ptID = 0;
    spiterator tempIter;
    pair<spiterator,bool> insertResult1, insertResult2;
    bool rowFound = false;

    LockGuard  guard(_mapMux);

    while( rdr() )
    {
        rowFound = true;

        rdr["pointid"] >> ptID;            // get the PointID
        tempIter = _pointIDMap.find(ptID);

        if( tempIter != _pointIDMap.end() )
        {
            /*
             *  The point just returned from the rdr already was in my list.  We need to
             *  update my list entry to the CTIDBG_new settings!
             */
            long origTrigID = tempIter->second->dbTriggerData.getTriggerID();
            long origVerificationID = tempIter->second->dbTriggerData.getVerificationID();
            tempIter->second->dbTriggerData.DecodeDatabaseReader(rdr);        // Fills himself in from the reader

            if( tempIter->second->dbTriggerData.getVerificationID() != origVerificationID )
            {
                //My verification ID has changed, change is Verification in points and continue
                //Verification points are currently unique (can only verify 1 point at a time), so we know we can
                //Set the old point to be false.

                if( origVerificationID != 0 )
                {
                    _verificationIDMap.erase(origVerificationID);
                }

                if( tempIter->second->dbTriggerData.getVerificationID() != 0 )
                {
                    _verificationIDMap.insert(coll_type::value_type(tempIter->second->dbTriggerData.getVerificationID(), tempIter->second));
                }
            }

            if( tempIter->second->dbTriggerData.getTriggerID() != origTrigID )
            {
                // The trigger ID for this point has changed (or gone away?)
                trig_coll_type::iterator triggerIter = _triggerIDMap.find(origTrigID);
                CtiPointSPtr oldTriggerPoint = pointMgr.getPoint(origTrigID);

                if( triggerIter != _triggerIDMap.end() )
                {
                    //Remove from the trigger map
                    triggerIter->second.erase(ptID);
                    if( triggerIter->second.empty() )
                    {
                        //If this trigger ID triggers no one else, remove this map entry
                        _triggerIDMap.erase(origTrigID);
                    }
                }

                //Re-add this trigger point into the trigger point map
                triggerIter = _triggerIDMap.find(tempIter->second->dbTriggerData.getTriggerID());
                if( triggerIter != _triggerIDMap.end() )
                {
                    triggerIter->second.insert(coll_type::value_type(ptID, tempIter->second));
                }
                else
                {
                    //If this trigger point is not known to trigger any other id's, we need to
                    //Add it fresh to the map and set is trigger point = true
                    coll_type tempPointMap;
                    tempPointMap.insert(coll_type::value_type(ptID, tempIter->second));
                    _triggerIDMap.insert(trig_coll_type::value_type(tempIter->second->dbTriggerData.getTriggerID(), tempPointMap));

                    CtiPointSPtr newTriggerPoint = pointMgr.getPoint(tempIter->second->dbTriggerData.getTriggerID());
                }
            }
        }
        else
        {
            PtVerifyTrigger *tempTrigger = CTIDBG_new PtVerifyTrigger();
            PtVerifyTriggerSPtr tempTriggerSPtr(tempTrigger);
            tempTriggerSPtr->lastTriggerValue = 0;
            tempTriggerSPtr->dbTriggerData.DecodeDatabaseReader(rdr);        // Fills himself in from the reader

            // Add it to my lists....
            insertResult1 = _pointIDMap.insert(coll_type::value_type(ptID, tempTriggerSPtr)); // Stuff it in the list

            if( tempTriggerSPtr->dbTriggerData.getTriggerID() != 0 )
            {
                trig_coll_type::iterator triggerIter = _triggerIDMap.find(tempTriggerSPtr->dbTriggerData.getTriggerID());
                if( triggerIter != _triggerIDMap.end() )
                {
                    insertResult2 = triggerIter->second.insert(coll_type::value_type(ptID, tempTriggerSPtr));
                }
                else
                {
                    coll_type tempPointMap;
                    insertResult2 = tempPointMap.insert(coll_type::value_type(ptID, tempTriggerSPtr));
                    _triggerIDMap.insert(trig_coll_type::value_type(tempTriggerSPtr->dbTriggerData.getTriggerID(), tempPointMap));
                }
            }

            //This is currently not checked for correctness..
            if(  tempTriggerSPtr->dbTriggerData.getVerificationID() != 0 )
            {
                _verificationIDMap.insert(coll_type::value_type(tempTriggerSPtr->dbTriggerData.getVerificationID(), tempTriggerSPtr));
            }


            if( (!insertResult2.second && tempTriggerSPtr->dbTriggerData.getTriggerID() != 0) || !insertResult1.second )
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint **** insert error detected " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << CtiTime() << " Point Map insert result: " << insertResult1.second << " Trigger Map insert result: " << insertResult2.second << " " << endl;
                }
            }
        }
    }

    if( !rowFound && pointID )
    {
        //Ok, the update has shown us that there is no longer data for this particular point, but the point
        //Was not deleted, we must erase it!

        erase(pointID);
    }
}

void CtiPointTriggerManager::erase(long ptID)
{
    spiterator pointIter;
    int startCount = 0, endCount = 0;

    {
        LockGuard guard(_mapMux);
        pointIter = _pointIDMap.find(ptID);

        if( pointIter != _pointIDMap.end() )
        {
            if( pointIter->second->dbTriggerData.getTriggerID() != 0 )
            {
                trig_coll_type::iterator trigIter = _triggerIDMap.find(pointIter->second->dbTriggerData.getTriggerID());

                if( trigIter != _triggerIDMap.end() )
                {
                    trigIter->second.erase(ptID);
                    if( trigIter->second.empty() )
                    {
                        _triggerIDMap.erase(pointIter->second->dbTriggerData.getTriggerID());
                    }
                }
            }

            if( pointIter->second->dbTriggerData.getVerificationID() != 0 )
            {
                _verificationIDMap.erase(pointIter->second->dbTriggerData.getVerificationID());
            }

            startCount = _pointIDMap.size();
            _pointIDMap.erase(ptID);
            endCount = _pointIDMap.size();
        }

    }

    if( startCount && (startCount-endCount) != 1 )
    {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** odd behavior trying to erase pointid: "<< ptID << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << CtiTime() << " Start Count: " << startCount << " End Count: " << endCount << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

bool CtiPointTriggerManager::isATriggerPoint(long pointID) const
{
    LockGuard guard(_mapMux);

    return _triggerIDMap.find(pointID) != _triggerIDMap.end();
}

bool CtiPointTriggerManager::isAVerificationPoint(long pointID) const
{
    LockGuard guard(_mapMux);

    return _verificationIDMap.find(pointID) != _verificationIDMap.end();
}

//You must must must!!! mux all calls that use this iterator using getMux()!!!!!
CtiPointTriggerManager::coll_type* CtiPointTriggerManager::getPointIteratorFromTrigger(long triggerID)
{
    coll_type *retVal = NULL;
    spiterator pointIter;
    trig_coll_type::iterator tempIter;

    LockGuard guard(_mapMux);
    tempIter = _triggerIDMap.find(triggerID);

    if( tempIter != _triggerIDMap.end() )
    {
        retVal = &(tempIter->second);
    }

    return retVal;
}

PtVerifyTriggerSPtr CtiPointTriggerManager::getPointTriggerFromPoint(long pointID)
{
    PtVerifyTriggerSPtr trigger;
    spiterator tempIter;

    LockGuard guard(_mapMux);
    tempIter = _pointIDMap.find(pointID);

    if( tempIter != _pointIDMap.end() )
    {
        trigger = tempIter->second;
    }

    return trigger;
}

//This function assumes (as does all of this code) that you cannot have more then 1
//ID verified by any given verification point (uniqueness)
PtVerifyTriggerSPtr CtiPointTriggerManager::getPointTriggerFromVerificationID(long verificationID)
{
    PtVerifyTriggerSPtr trigger;
    spiterator tempIter;

    LockGuard guard(_mapMux);
    tempIter = _verificationIDMap.find(verificationID);

    if( tempIter != _verificationIDMap.end() )
    {
        trigger = tempIter->second;
    }

    return trigger;
}

CtiMutex& CtiPointTriggerManager::getMux()
{
    return _mapMux;
}
