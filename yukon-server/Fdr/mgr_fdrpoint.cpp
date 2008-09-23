/*****************************************************************************
*
*    FILE NAME: mgr_fdrpoint.cpp
*
*    DATE: 10/26/2000
*
*    AUTHOR: Ben Wallace
*
*    PURPOSE: class CtiFDRManager manages a collection of FDRPoints
*
*    DESCRIPTION: Manages a collection of FDRPoints which are points
*                 that are sent or received from other systems.  Translation
*                 information is also included in our for id or renaming.
*
*
*
*    Copyright (C) 2000 Cannon Technologies, Inc.  All rights reserved.
****************************************************************************/
#include "yukon.h"

#include <rw/db/db.h>

#include "dbaccess.h"
#include "hashkey.h"
#include "resolvers.h"

#include "fdr.h"
#include "fdrdebuglevel.h"
#include "mgr_fdrpoint.h"

#include "logger.h"
#include "guard.h"
#include "utility.h"

#include "rwutil.h"

/** local definitions **/
const CHAR * CtiFDRManager::TBLNAME_FDRTRANSLATION =     "FDRTranslation";
const CHAR * CtiFDRManager::COLNAME_FDRTRANSLATION =     "TRANSLATION";
const CHAR * CtiFDRManager::COLNAME_FDRDESTINATION =     "DESTINATION";
const CHAR * CtiFDRManager::COLNAME_FDR_POINTID =        "PointID";
const CHAR * CtiFDRManager::COLNAME_FDRINTERFACETYPE =   "INTERFACETYPE";
const CHAR * CtiFDRManager::COLNAME_FDRDIRECTIONTYPE =   "DIRECTIONTYPE";

const CHAR * CtiFDRManager::TBLNAME_PTANALOG =           "PointAnalog";
const CHAR * CtiFDRManager::COLNAME_PTANALOG_MULT =      "MULTIPLIER";
const CHAR * CtiFDRManager::COLNAME_PTANALOG_OFFSET =    "DATAOFFSET";
const CHAR * CtiFDRManager::COLNAME_PTANALOG_POINTID =   "PointID";
const CHAR * CtiFDRManager::TBLNAME_PTBASE =           "Point";
const CHAR * CtiFDRManager::COLNAME_PTBASE_POINTID =   "PointID";
const CHAR * CtiFDRManager::COLNAME_PTBASE_POINTTYPE =   "PointType";

// constructors, destructors, operators first

CtiFDRManager::CtiFDRManager(string & aInterfaceName):
iInterfaceName(aInterfaceName),
iWhereSelectStr()
{
}

CtiFDRManager::CtiFDRManager(string & aInterfaceName, string & aWhereSelectStr):
iInterfaceName(aInterfaceName),
iWhereSelectStr(aWhereSelectStr)
{
}

CtiFDRManager::~CtiFDRManager()
{
}

//**************************************************
// start getters and setters

string CtiFDRManager::getInterfaceName()
{
    return iInterfaceName;
}

CtiFDRManager & CtiFDRManager::setInterfaceName(string & aInterfaceName)
{
    iInterfaceName = aInterfaceName;
    return *this;
}

string CtiFDRManager::getWhereSelectStr()
{
    return iWhereSelectStr;
}

CtiFDRManager & CtiFDRManager::setWhereSelectStr(string & aWhereSelectStr)
{
    iWhereSelectStr = aWhereSelectStr;
    return *this;
}

// end Getters and Setters
//**********************************************************************************

/************************************************************************
* Function Name: CtiFDRManager::loadPointList()
*
* Description: loads a list of FDRPoints from the database for
*              a given interface
*
*************************************************************************
*/
RWDBStatus CtiFDRManager::loadPointList()
{
    RWDBStatus  retStatus = RWDBStatus::ok;

    ResetBreakAlloc();

    try
    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        // are out of scope when the release is called

        RWDBDatabase db = conn.database();
        RWDBTable fdrTranslation = db.table(TBLNAME_FDRTRANSLATION);
        RWDBTable pointBaseTable = db.table(TBLNAME_PTBASE);
        RWDBTable pointAnalogTable = db.table(TBLNAME_PTANALOG);

        RWDBSelector selector = db.selector();
        buildFDRPointSelector(db,selector,fdrTranslation,pointBaseTable,pointAnalogTable);

        selector.where( selector.where() && fdrTranslation[COLNAME_FDR_POINTID] == pointBaseTable[COLNAME_PTBASE_POINTID] && pointBaseTable[COLNAME_PTBASE_POINTID].leftOuterJoin(pointAnalogTable[COLNAME_PTANALOG_POINTID]));

        if(getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << selector.asString() << endl;
        }

        std::map<long,CtiFDRPointSPtr> fdrTempMap;
        retStatus = getPointsFromDB(selector,fdrTempMap);
        
        //move all from tempMap to main Map.
        for (std::map<long,CtiFDRPointSPtr>::iterator itr = fdrTempMap.begin(); itr != fdrTempMap.end(); itr++) 
        {
            pointMap.insert((*itr).second->getPointID(),(*itr).second);
        }
        fdrTempMap.clear();
    }
    catch(RWExternalErr e )
    {
        //Make sure the list is cleared
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Attempting to clear FDR Point list..." << endl;
        }
        pointMap.removeAll(NULL, 0);
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "loadFDRList:  " << e.why() << endl;
        }
        RWTHROW(e);
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << __FILE__ << " (" << __LINE__ << ") loadPointList: unknown exception" << endl;
        }     
        pointMap.removeAll(NULL, 0);
    }
    return retStatus;
}


RWDBStatus CtiFDRManager::loadPoint(long pointId)
{
    RWDBStatus  retStatus = RWDBStatus::ok;

    ResetBreakAlloc();

    try
    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        // are out of scope when the release is called

        RWDBDatabase db = conn.database();
        RWDBTable fdrTranslation = db.table(TBLNAME_FDRTRANSLATION);
        RWDBTable pointBaseTable = db.table(TBLNAME_PTBASE);
        RWDBTable pointAnalogTable = db.table(TBLNAME_PTANALOG);

        RWDBSelector selector = db.selector();
        buildFDRPointSelector(db,selector,fdrTranslation,pointBaseTable,pointAnalogTable);

        selector.where( selector.where() && fdrTranslation[COLNAME_FDR_POINTID] == pointBaseTable[COLNAME_PTBASE_POINTID] && pointBaseTable[COLNAME_PTBASE_POINTID].leftOuterJoin(pointAnalogTable[COLNAME_PTANALOG_POINTID]));
        selector.where( selector.where() && pointBaseTable[COLNAME_FDR_POINTID] == pointId);

        std::map<long,CtiFDRPointSPtr > fdrTempMap;
        retStatus = getPointsFromDB(selector,fdrTempMap);
        
        //should be only 1?

        //move all from tempMap to main Map.
        for (std::map<long,CtiFDRPointSPtr >::iterator itr = fdrTempMap.begin(); itr != fdrTempMap.end(); itr++) 
        {
            pointMap.insert((*itr).second->getPointID(),(*itr).second);
        }
        fdrTempMap.clear();

        if(getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << selector.asString() << endl;
        }
    }
    catch(RWExternalErr e )
    {
        //Make sure the list is cleared
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Attempting to clear FDR Point list..." << endl;
        }
        pointMap.removeAll(NULL, 0);
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "loadFDRList:  " << e.why() << endl;
        }
        RWTHROW(e);
    }
    catch(...)
    {
        retStatus = RWDBStatus::ok;
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << __FILE__ << " (" << __LINE__ << ") loadPointList: unknown exception" << endl;
        }     
        pointMap.removeAll(NULL, 0);
    }

    return retStatus;
}

void CtiFDRManager::buildFDRPointSelector(RWDBDatabase& db, RWDBSelector& selector, RWDBTable& fdrTranslation, RWDBTable& pointBaseTable, RWDBTable& pointAnalogTable)
{
   // Make sure all objects that that store results

    selector << fdrTranslation[COLNAME_FDR_POINTID]
    << fdrTranslation[COLNAME_FDRTRANSLATION]
    << fdrTranslation[COLNAME_FDRDESTINATION]
    << fdrTranslation[COLNAME_FDRDIRECTIONTYPE]
    << pointBaseTable[COLNAME_PTBASE_POINTTYPE]
    << pointAnalogTable[COLNAME_PTANALOG_MULT]
    << pointAnalogTable[COLNAME_PTANALOG_OFFSET];


    selector.from( fdrTranslation);
    selector.from( pointBaseTable );
    selector.from( pointAnalogTable);


    if(getWhereSelectStr() != "")
    {
        // we now have control options so put those into the same list as a default
        if(getWhereSelectStr() == string (FDR_INTERFACE_SEND))
        {
            // use a direction clause
            selector.where(fdrTranslation[COLNAME_FDRINTERFACETYPE] == getInterfaceName().c_str() && (
                                                                                             fdrTranslation[COLNAME_FDRDIRECTIONTYPE] == getWhereSelectStr().c_str() ||
                                                                                             fdrTranslation[COLNAME_FDRDIRECTIONTYPE] == string(FDR_INTERFACE_SEND_FOR_CONTROL).c_str() ));
        }
        else if(getWhereSelectStr() == string (FDR_INTERFACE_RECEIVE))
        {
            // use a direction clause
            selector.where(fdrTranslation[COLNAME_FDRINTERFACETYPE] == getInterfaceName().c_str() && (
                                                                                             fdrTranslation[COLNAME_FDRDIRECTIONTYPE] == getWhereSelectStr().c_str() ||
                                                                                             fdrTranslation[COLNAME_FDRDIRECTIONTYPE] == string(FDR_INTERFACE_RECEIVE_FOR_CONTROL).c_str()));
        }
        else
        {
            // use a direction clause
            selector.where(fdrTranslation[COLNAME_FDRINTERFACETYPE] == getInterfaceName().c_str() &&
                           fdrTranslation[COLNAME_FDRDIRECTIONTYPE] == getWhereSelectStr().c_str());
        }
    }
    else
    {
        // at least get the distination
        selector.where(fdrTranslation[COLNAME_FDRINTERFACETYPE] == getInterfaceName().c_str());
    }
}

RWDBStatus CtiFDRManager::getPointsFromDB(RWDBSelector& selector, std::map<long,CtiFDRPointSPtr >& fdrPtrMap)
{
    CtiFDRPointSPtr fdrPtr;
    long   pointID;
    string translation;
    string destination;
    string direction;
    double multiplier=0.0;
    double dataOffset=0;
    string tmp;
    CtiPointType_t pointType = InvalidPointType;

    RWDBConnection conn = getConnection();

    RWDBStatus retStatus = selector.execute(conn).status();

    if(retStatus.errorCode() == RWDBStatus::ok)
    {
        RWDBReader  rdr = selector.reader( conn );

        while( rdr() )
        {

            rdr[COLNAME_FDR_POINTID]      >> pointID;
            rdr[COLNAME_FDRTRANSLATION]   >> translation;
            rdr[COLNAME_FDRDESTINATION]   >> destination;
            rdr[COLNAME_FDRDIRECTIONTYPE] >> direction;
            rdr[COLNAME_PTBASE_POINTTYPE] >> tmp;
            rdr[COLNAME_PTANALOG_MULT]    >> multiplier;
            rdr[COLNAME_PTANALOG_OFFSET]  >> dataOffset;

            
            std::map< long, CtiFDRPointSPtr >::iterator itr = fdrPtrMap.find(pointID);

            if( itr != fdrPtrMap.end() )
                fdrPtr = (*itr).second;
            else
                fdrPtr = CtiFDRPointSPtr(new CtiFDRPoint( pointID ));

            if( fdrPtrMap.size() == 0 ||  itr == fdrPtrMap.end() )
            {
                fdrPtr->setPointType ((CtiPointType_t) resolvePointType(tmp));

                if((fdrPtr->getPointType() == AnalogPointType) ||
                   (fdrPtr->getPointType() == PulseAccumulatorPointType) ||
                   (fdrPtr->getPointType() == DemandAccumulatorPointType) ||
                   (fdrPtr->getPointType() == CalculatedPointType))
                {
                    fdrPtr->setMultiplier(multiplier);
                    fdrPtr->setOffset (dataOffset);
                }

                // set controllable
                if(direction == string(FDR_INTERFACE_SEND_FOR_CONTROL) ||
                   direction == string(FDR_INTERFACE_RECEIVE_FOR_CONTROL))
                {
                    fdrPtr->setControllable (true);
                }
                else
                {
                    fdrPtr->setControllable (false);
                }

                CtiFDRDestination tmpDestination (fdrPtr.get(), translation, destination);
                fdrPtr->getDestinationList().push_back(tmpDestination);
                fdrPtrMap.insert( std::pair<long,CtiFDRPointSPtr >(pointID, fdrPtr));
            }
            else
            {
                /**********************
                * add the current destination to the list
                ***********************
                */
                CtiFDRDestination tmpDestination (fdrPtr.get(), translation, destination);
                fdrPtr->getDestinationList().push_back(tmpDestination);
            }
        }
    }

    return retStatus;
}

/************************************************************************
* Function Name: CtiFDRManager::reloadPointList()
*
* Description: clear's the collection and calls loadPointList()
*
*
*************************************************************************
*/
RWDBStatus CtiFDRManager::refreshPointList()
{

    pointMap.removeAll(NULL, 0);

    return loadPointList();
}

/************************************************************************
* Function Name: CtiFDRManager::findFDRPointID(LONG myPointID)
*
* Description: finds a point by PointId in the collection. Returns
*              Null if not found.
*
*************************************************************************
*/
CtiFDRPointSPtr CtiFDRManager::findFDRPointID(LONG myPointID)
{
    CtiFDRPointSPtr pFdrPoint;

    pFdrPoint = pointMap.find(myPointID);

    return pFdrPoint;
}

CtiFDRPointSPtr CtiFDRManager::removeFDRPointID(long myPointId)
{
    CtiFDRPointSPtr pFdrPoint = pointMap.remove(myPointId);
    return pFdrPoint;
}

bool CtiFDRManager::addFDRPointId(long myPointId)
{
    RWDBStatus loaded = loadPoint(myPointId);

    return (loaded.errorCode() == RWDBStatus::ok);
}

void CtiFDRManager::printIds(CtiLogger& dout)
{
    CtiSmartMap<CtiFDRPoint>::coll_type Map = pointMap.getMap();
    readerLock guard(pointMap.getLock());
    CtiFDRManager::spiterator itr = Map.begin();

    for (;itr != Map.end(); itr++ )
    {
        long pid = (*itr).first;
        dout << pid << " ";
    }
}

size_t CtiFDRManager::entries()
{
    return pointMap.entries();
}

CtiFDRManager::ptr_type CtiFDRManager::find(bool (*testFun)(ptr_type&, void*),void* d)
{
    return pointMap.find(testFun,d);
}

CtiFDRManager::coll_type & CtiFDRManager::getMap()
{
    return pointMap.getMap();
}

CtiFDRManager::lock & CtiFDRManager::getLock()
{
    return pointMap.getLock();
}
