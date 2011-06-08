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


#include "dbaccess.h"
#include "hashkey.h"
#include "resolvers.h"

#include "fdr.h"
#include "fdrdebuglevel.h"
#include "mgr_fdrpoint.h"
#include "FdrException.h"

#include "logger.h"
#include "guard.h"
#include "utility.h"

using std::string;
using std::endl;

/** local definitions **/
const CHAR * CtiFDRManager::TBLNAME_FDRTRANSLATION =     "FDRTranslation";
const CHAR * CtiFDRManager::COLNAME_FDRTRANSLATION =     "TRANSLATION";
const CHAR * CtiFDRManager::COLNAME_FDRDESTINATION =     "DESTINATION";
const CHAR * CtiFDRManager::COLNAME_FDR_POINTID =        "PointID";
const CHAR * CtiFDRManager::COLNAME_FDRINTERFACETYPE =   "INTERFACETYPE";
const CHAR * CtiFDRManager::COLNAME_FDRDIRECTIONTYPE =   "DIRECTIONTYPE";

const CHAR * CtiFDRManager::TBLNAME_PTANALOG =           "PointAnalog";
const CHAR * CtiFDRManager::TBLNAME_PTACCUM  =           "PointAccumulator";
const CHAR * CtiFDRManager::COLNAME_PTANALOG_MULT =      "MULTIPLIER";
const CHAR * CtiFDRManager::COLNAME_PTANALOG_OFFSET =    "DATAOFFSET";
const CHAR * CtiFDRManager::COLNAME_PTANALOG_POINTID =   "PointID";
const CHAR * CtiFDRManager::TBLNAME_PTBASE =           "Point";
const CHAR * CtiFDRManager::COLNAME_PTBASE_POINTID =   "PointID";
const CHAR * CtiFDRManager::COLNAME_PTBASE_POINTTYPE =   "PointType";

// constructors, destructors, operators first

CtiFDRManager::CtiFDRManager(const string & aInterfaceName):
iInterfaceName(aInterfaceName),
iWhereSelectStr()
{
}

CtiFDRManager::CtiFDRManager(const string & aInterfaceName, string & aWhereSelectStr):
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
bool CtiFDRManager::loadPointList()
{
    bool functionSuccess;

    ResetBreakAlloc();

    try
    {
        std::stringstream ss;

        static const string sql =  "SELECT FDR.PointID, FDR.TRANSLATION, FDR.DESTINATION, FDR.DIRECTIONTYPE, PT.PointType, "
                                     "PAL.MULTIPLIER, PAL.DATAOFFSET, PAC.MULTIPLIER, PAC.DATAOFFSET "
                                   "FROM FDRTranslation FDR, Point PT "
                                     "LEFT OUTER JOIN PointAnalog PAL ON PT.PointID = PAL.PointID "
                                     "LEFT OUTER JOIN PointAccumulator PAC ON PT.PointID = PAC.PointID "
                                   "WHERE FDR.PointID = PT.PointID";

        ss << sql;

        if(getWhereSelectStr() != "")
        {
            ss << " AND FDR.INTERFACETYPE = '" << getInterfaceName() << "'";

            // we now have control options so put those into the same list as a default
            if(getWhereSelectStr() == string (FDR_INTERFACE_SEND))
            {
                ss << " AND (FDR.DIRECTIONTYPE = '" << getWhereSelectStr() << "' OR FDR.DIRECTIONTYPE = '";
                ss << string(FDR_INTERFACE_SEND_FOR_CONTROL) << "')";
            }
            else if(getWhereSelectStr() == string (FDR_INTERFACE_RECEIVE))
            {
                ss << " AND (FDR.DIRECTIONTYPE = '" << getWhereSelectStr()  << "' OR FDR.DIRECTIONTYPE = '";
                ss << string(FDR_INTERFACE_RECEIVE_FOR_CONTROL)             << "' OR FDR.DIRECTIONTYPE = '";
                ss << string(FDR_INTERFACE_RECEIVE_FOR_ANALOG_OUTPUT)       << "')";
            }
            else
            {
                ss << " AND FDR.DIRECTIONTYPE = '" << getWhereSelectStr() << "'";
            }
        }

        std::map<long,CtiFDRPointSPtr> fdrTempMap;
        functionSuccess = getPointsFromDB(ss,fdrTempMap);

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

        functionSuccess = false;
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << __FILE__ << " (" << __LINE__ << ") loadPointList: unknown exception" << endl;
        }
        pointMap.removeAll(NULL, 0);

        functionSuccess = false;
    }

    return functionSuccess;
}


bool CtiFDRManager::loadPoint(long pointId, CtiFDRPointSPtr & point)
{
    bool functionStatus = false;

    ResetBreakAlloc();

    try
    {
        std::stringstream ss;

        static const string sql =  "SELECT FDR.PointID, FDR.TRANSLATION, FDR.DESTINATION, FDR.DIRECTIONTYPE, PT.PointType, "
                                     "PAL.MULTIPLIER, PAL.DATAOFFSET, PAC.MULTIPLIER, PAC.DATAOFFSET "
                                   "FROM FDRTranslation FDR, Point PT "
                                     "LEFT OUTER JOIN PointAnalog PAL ON PT.PointID = PAL.PointID "
                                     "LEFT OUTER JOIN PointAccumulator PAC ON PT.PointID = PAC.PointID "
                                   "WHERE FDR.PointID = PT.PointID AND PT.PointID = ";

        ss << sql << pointId;

        if(getWhereSelectStr() != "")
        {
            ss << " AND FDR.INTERFACETYPE = '" << getInterfaceName() << "'";

            // we now have control options so put those into the same list as a default
            if(getWhereSelectStr() == string (FDR_INTERFACE_SEND))
            {
                ss << " AND (FDR.DIRECTIONTYPE = '" << getWhereSelectStr() << "' OR FDR.DIRECTIONTYPE = '";
                ss << string(FDR_INTERFACE_SEND_FOR_CONTROL) << "')";
            }
            else if(getWhereSelectStr() == string (FDR_INTERFACE_RECEIVE))
            {
                ss << " AND (FDR.DIRECTIONTYPE = '" << getWhereSelectStr()  << "' OR FDR.DIRECTIONTYPE = '";
                ss << string(FDR_INTERFACE_RECEIVE_FOR_CONTROL)             << "' OR FDR.DIRECTIONTYPE = '";
                ss << string(FDR_INTERFACE_RECEIVE_FOR_ANALOG_OUTPUT)       << "')";
            }
            else
            {
                ss << " AND FDR.DIRECTIONTYPE = '" << getWhereSelectStr();
            }
        }

        std::map<long,CtiFDRPointSPtr > fdrTempMap;
        functionStatus = getPointsFromDB(ss,fdrTempMap);

        if (fdrTempMap.size() == 1)
        {
            std::map<long,CtiFDRPointSPtr >::iterator itr = fdrTempMap.begin();
            point = (*itr).second;
            pointMap.insert((*itr).second->getPointID(),(*itr).second);
        }
        else if (fdrTempMap.size() > 1)
        {
            throw FdrDatabaseException();
        }

        //Cleanup
        fdrTempMap.clear();
        if(getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
        {
            string loggedSQLstring = ss.str();
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << loggedSQLstring << endl;
            }
        }
    }
    catch(RWExternalErr e)
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
    catch (FdrDatabaseException e)
    {
        functionStatus = false;
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " DB Error: expected only one translation for point: " << pointId << endl;
        }
    }
    catch(...)
    {
        functionStatus = false;
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << __FILE__ << " (" << __LINE__ << ") loadPointList: unknown exception" << endl;
        }
        pointMap.removeAll(NULL, 0);
    }

    return functionStatus;
}

bool CtiFDRManager::getPointsFromDB(const std::stringstream &ss, std::map<long,CtiFDRPointSPtr >& fdrPtrMap)
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
    bool functionSuccess;

    Cti::Database::DatabaseConnection connection;
    Cti::Database::DatabaseReader rdr(connection, ss.str());

    rdr.execute();

    if( rdr.isValid() )
    {
        if(getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
        {
            string loggedSQLstring = rdr.asString();
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " " << loggedSQLstring << endl;
            }
        }

        while( rdr() )
        {

            rdr[0]      >> pointID;
            rdr[1]   >> translation;
            rdr[2]   >> destination;
            rdr[3] >> direction;
            rdr[4] >> tmp;
            if(!rdr[5].isNull())
            {
                rdr[5]    >> multiplier;
            }
            else
            {
                rdr[7]    >> multiplier;
            }
            if(!rdr[6].isNull())
            {
                rdr[6]  >> dataOffset;
            }
            else
            {
                rdr[8]    >> dataOffset;
            }


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
                   direction == string(FDR_INTERFACE_RECEIVE_FOR_CONTROL) ||
                   direction == string(FDR_INTERFACE_RECEIVE_FOR_ANALOG_OUTPUT))
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

        return true;
    }

    return false;
}

/************************************************************************
* Function Name: CtiFDRManager::reloadPointList()
*
* Description: clear's the collection and calls loadPointList()
*
*
*************************************************************************
*/
bool CtiFDRManager::refreshPointList()
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

bool CtiFDRManager::addFDRPointId(long myPointId, CtiFDRPointSPtr & point)
{
    return loadPoint(myPointId,point);
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
