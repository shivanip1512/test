#include "yukon.h"

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
****************************************************************************
*/


/** include files **/
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

CtiFDRManager::CtiFDRManager(RWCString & aInterfaceName):
iInterfaceName(aInterfaceName),
iWhereSelectStr()
{
}

CtiFDRManager::CtiFDRManager(RWCString & aInterfaceName, RWCString & aWhereSelectStr):
iInterfaceName(aInterfaceName),
iWhereSelectStr(aWhereSelectStr)
{
}

CtiFDRManager::~CtiFDRManager()
{
}

//**************************************************
// start getters and setters

RWCString CtiFDRManager::getInterfaceName()
{
    return iInterfaceName;
}

CtiFDRManager & CtiFDRManager::setInterfaceName(RWCString & aInterfaceName)
{
    iInterfaceName = aInterfaceName;
    return *this;
}

RWCString CtiFDRManager::getWhereSelectStr()
{
    return iWhereSelectStr;
}

CtiFDRManager & CtiFDRManager::setWhereSelectStr(RWCString & aWhereSelectStr)
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
    CtiFDRPoint *pTempFdrPoint = NULL;
    LONG        pointID;
    RWCString   translation;
    RWCString   destination;
    RWCString   direction;
    double      multiplier=0.0;
    double      dataOffset=0;
    RWCString tmp;
    CtiPointType_t pointType = InvalidPointType;
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
            if(getWhereSelectStr() == RWCString (FDR_INTERFACE_SEND))
            {
                // use a direction clause
                selector.where(fdrTranslation[COLNAME_FDRINTERFACETYPE] == getInterfaceName() && (
                                                                                                 fdrTranslation[COLNAME_FDRDIRECTIONTYPE] == getWhereSelectStr() ||
                                                                                                 fdrTranslation[COLNAME_FDRDIRECTIONTYPE] == RWCString(FDR_INTERFACE_SEND_FOR_CONTROL) ));
            }
            else if(getWhereSelectStr() == RWCString (FDR_INTERFACE_RECEIVE))
            {
                // use a direction clause
                selector.where(fdrTranslation[COLNAME_FDRINTERFACETYPE] == getInterfaceName() && (
                                                                                                 fdrTranslation[COLNAME_FDRDIRECTIONTYPE] == getWhereSelectStr() ||
                                                                                                 fdrTranslation[COLNAME_FDRDIRECTIONTYPE] == RWCString(FDR_INTERFACE_RECEIVE_FOR_CONTROL)));
            }
            else
            {
                // use a direction clause
                selector.where(fdrTranslation[COLNAME_FDRINTERFACETYPE] == getInterfaceName() &&
                               fdrTranslation[COLNAME_FDRDIRECTIONTYPE] == getWhereSelectStr());
            }
        }
        else
        {
            // at least get the distination
            selector.where(fdrTranslation[COLNAME_FDRINTERFACETYPE] == getInterfaceName());
        }

        selector.where( selector.where() && fdrTranslation[COLNAME_FDR_POINTID] == pointBaseTable[COLNAME_PTBASE_POINTID] && pointBaseTable[COLNAME_PTBASE_POINTID].leftOuterJoin(pointAnalogTable[COLNAME_PTANALOG_POINTID]));

        if(getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " " << selector.asString() << endl;
        }

        // check execute status to stop the exception when database is gone
        retStatus = selector.execute(conn).status();

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

                // check if the point id exists
                CtiHashKey key(pointID);
                pTempFdrPoint = Map.findValue(&key);

                if( Map.entries() == 0 ||  pTempFdrPoint == NULL)
                {
                    /*********************
                    * if the point id isn't in my list, add it and
                    * the current destination
                    **********************
                    */
                    pTempFdrPoint = new CtiFDRPoint( pointID );
                    pTempFdrPoint->setPointType ((CtiPointType_t) resolvePointType(tmp));

                    if((pTempFdrPoint->getPointType() == AnalogPointType) ||
                       (pTempFdrPoint->getPointType() == PulseAccumulatorPointType) ||
                       (pTempFdrPoint->getPointType() == DemandAccumulatorPointType) ||
                       (pTempFdrPoint->getPointType() == CalculatedPointType))
                    {
                        pTempFdrPoint->setMultiplier(multiplier);
                        pTempFdrPoint->setOffset (dataOffset);
                    }

                    // set controllable
                    if(direction == RWCString(FDR_INTERFACE_SEND_FOR_CONTROL) ||
                       direction == RWCString(FDR_INTERFACE_RECEIVE_FOR_CONTROL))
                    {
                        pTempFdrPoint->setControllable (true);
                    }
                    else
                    {
                        pTempFdrPoint->setControllable (false);
                    }

                    CtiFDRDestination tmpDestination (translation,destination);
                    pTempFdrPoint->getDestinationList().push_back(tmpDestination);

                    Map.insert( new CtiHashKey(pointID), pTempFdrPoint);
                }
                else
                {
                    /**********************
                    * add the current destination to the list
                    ***********************
                    */
                    CtiFDRDestination tmpDestination (translation,destination);
                    pTempFdrPoint->getDestinationList().push_back(tmpDestination);
                }
            }
        }
    }

    catch(RWExternalErr e )
    {
        //Make sure the list is cleared
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Attempting to clear FDR Point list..." << endl;
        }
        Map.clearAndDestroy();
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
            dout << RWTime() << " " << __FILE__ << " (" << __LINE__ << ") loadPointList: unknown exception" << endl;
        }
        Map.clearAndDestroy();
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
    if( Map.entries() > 0 )
    {
        Map.clearAndDestroy();
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
CtiFDRPoint * CtiFDRManager::findFDRPointID(LONG myPointID)
{
    CtiFDRPoint *   pFdrPoint = NULL;
    CtiHashKey      key(myPointID);

    try
    {
        if( Map.entries() > 0 )
        {
            pFdrPoint = Map.findValue(&key);
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




