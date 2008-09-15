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
        // Make sure all objects that that store results
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        // are out of scope when the release is called

        RWDBDatabase db = conn.database();
        RWDBTable fdrTranslation = db.table(TBLNAME_FDRTRANSLATION);
        RWDBTable pointBaseTable = db.table(TBLNAME_PTBASE);
        RWDBTable pointAnalogTable = db.table(TBLNAME_PTANALOG);
        RWDBSelector selector = db.selector();

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

        selector.where( selector.where() && fdrTranslation[COLNAME_FDR_POINTID] == pointBaseTable[COLNAME_PTBASE_POINTID] && pointBaseTable[COLNAME_PTBASE_POINTID].leftOuterJoin(pointAnalogTable[COLNAME_PTANALOG_POINTID]));

        if(getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << selector.asString() << endl;
        }

        std::map<long,boost::shared_ptr<CtiFDRPoint> > fdrTempMap;
        retStatus = getPointsFromDB(conn,selector,fdrTempMap);
        
        //move all from tempMap to main Map.
        for (std::map<long,boost::shared_ptr<CtiFDRPoint> >::iterator itr = fdrTempMap.begin(); itr != fdrTempMap.end(); itr++) 
        {
            Map.insert(std::pair<long,boost::shared_ptr<CtiFDRPoint> >((*itr).second->getPointID(),(*itr).second));
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
        Map.clear();
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
        Map.clear();
    }
    return retStatus;
}


RWDBStatus CtiFDRManager::loadPoint(long pointId)
{
    RWDBStatus  retStatus = RWDBStatus::ok;

    ResetBreakAlloc();

    try
    {
        // Make sure all objects that that store results
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        // are out of scope when the release is called

        RWDBDatabase db = conn.database();
        RWDBTable fdrTranslation = db.table(TBLNAME_FDRTRANSLATION);
        RWDBTable pointBaseTable = db.table(TBLNAME_PTBASE);
        RWDBTable pointAnalogTable = db.table(TBLNAME_PTANALOG);
        RWDBSelector selector = db.selector();

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

        selector.where( selector.where() && fdrTranslation[COLNAME_FDR_POINTID] == pointBaseTable[COLNAME_PTBASE_POINTID] && pointBaseTable[COLNAME_PTBASE_POINTID].leftOuterJoin(pointAnalogTable[COLNAME_PTANALOG_POINTID]));
        //only difference from loadpointlist... 
        selector.where( selector.where() && pointBaseTable[COLNAME_FDR_POINTID] == pointId);


        std::map<long,boost::shared_ptr<CtiFDRPoint> > fdrTempMap;
        retStatus = getPointsFromDB(conn,selector,fdrTempMap);
        
        //should be only 1?

        //move all from tempMap to main Map.
        for (std::map<long,boost::shared_ptr<CtiFDRPoint> >::iterator itr = fdrTempMap.begin(); itr != fdrTempMap.end(); itr++) 
        {
            Map.insert(std::pair<long,boost::shared_ptr<CtiFDRPoint> >((*itr).second->getPointID(),(*itr).second));
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
        Map.clear();
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
        Map.clear();
    }

    return retStatus;
}

RWDBStatus CtiFDRManager::getPointsFromDB(RWDBConnection& conn, RWDBSelector& selector, std::map<long,boost::shared_ptr<CtiFDRPoint> >& fdrPtrMap)
{
    boost::shared_ptr<CtiFDRPoint> fdrPtr;
    long   pointID;
    string translation;
    string destination;
    string direction;
    double multiplier=0.0;
    double dataOffset=0;
    string tmp;
    CtiPointType_t pointType = InvalidPointType;

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

            
            std::map< long, boost::shared_ptr<CtiFDRPoint> >::iterator itr = fdrPtrMap.find(pointID);

            if( itr != fdrPtrMap.end() )
                fdrPtr = (*itr).second;
            else
                fdrPtr = boost::shared_ptr<CtiFDRPoint>(new CtiFDRPoint( pointID ));

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
                fdrPtrMap.insert( std::pair<long,boost::shared_ptr<CtiFDRPoint> >(pointID, fdrPtr));
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
* Description: deletes the collection and calls loadPointList()
*
*
*************************************************************************
*/
RWDBStatus CtiFDRManager::refreshPointList()
//CtiFDRManager & CtiFDRManager::refreshPointList()
{
    // first clear list
    if( Map.size() > 0 )
    { 
        Map.clear();
    }

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
shared_ptr<CtiFDRPoint> CtiFDRManager::findFDRPointID(LONG myPointID)
{
    boost::shared_ptr<CtiFDRPoint> pFdrPoint;

    try
    {
        if( Map.size() > 0 )
        {
            MapIterator itr = Map.find(myPointID);
            if (itr != Map.end() ) {
                pFdrPoint = (*itr).second;
            }
        }
    }
    catch(RWExternalErr e )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "findFDRPointID:  " << e.why() << endl;
        RWTHROW(e);
    }

    return pFdrPoint;
}


shared_ptr<CtiFDRPoint> CtiFDRManager::removeFDRPointID(long myPointId)
{
    shared_ptr<CtiFDRPoint> pFdrPoint = findFDRPointID(myPointId);

    if (pFdrPoint.get() != NULL)
    {
        Map.erase(myPointId);
    }

    return pFdrPoint;
}

bool CtiFDRManager::addFDRPointId(long myPointId)
{
    RWDBStatus loaded = loadPoint(myPointId);

    return (loaded.errorCode() == RWDBStatus::ok);
}

void CtiFDRManager::printIds(CtiLogger& dout)
{
    boost::shared_ptr<CtiFDRPoint> pFdrPoint;

    try
    {
        MapIterator itr = Map.begin();
        for (;itr != Map.end(); itr++ )
        {
			int pid = (*itr).second->getPointID();
            dout << pid << " ";
        }
    }
    catch(RWExternalErr e )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "printIds:  " << e.why() << endl;
        RWTHROW(e);
    }
}


