#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*-----------------------------------------------------------------------------*
*
*    FILE NAME: fdrcygnet.cpp
*
*    DATE: 10/01/2000
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive$
*    REVISION     :  $Revision: 1.7 $
*    DATE         :  $Date: 2004/10/22 20:58:54 $
*
*
*    AUTHOR: Ben Wallace
*
*    PURPOSE: Interface to the Cygnet Foreign System
*
*    DESCRIPTION: This class implements an interface that retrieves point data
*                 from a Foreign System.  The data is status and Analog data.
*                 This interface only receives at this time.  It links with
*                 a Cygnet library provided by Visual Systems.  The library
*                 and headers provide access to their API.
*
*    ---------------------------------------------------
*    History:
      $Log: fdrcygnet.cpp,v $
      Revision 1.7  2004/10/22 20:58:54  mfisher
      localized boost ptime references to restore some semblance of sanity to build times

      Revision 1.6  2004/09/29 17:47:47  dsutton
      Updated all interfaces to default the db reload rate to once a day (86400)

      Revision 1.5  2004/09/24 14:36:52  eschmit
      Added Boost includes and libraries, misc fixes for ptime support

      Revision 1.4  2002/05/24 18:01:32  alauinger
      ben change

      Revision 1.3  2002/04/16 15:58:31  softwarebuild
      20020416_1031_2_16

      Revision 1.2  2002/04/15 15:18:54  cplender

      This is an update due to the freezing of PVCS on 4/13/2002

*
*
*
*
*    Copyright (C) 2000 Cannon Technologies, Inc.  All rights reserved.
*-----------------------------------------------------------------------------*
*/



/** include files **/
#include <windows.h>
#include <math.h>
#include <stdlib.h>
#include <iostream>

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <stdio.h>

/* -----------------------------------
| CygNet include files & define
|
|
| they handle packing and
| cdecl for calls
--------------------------------------
*/

#define _WINDLL         // needed for Cygnet dcl.h header

#include <dcl.h>
#include <cvs.h>

// End CygNet include files & define
//------------------------------------

/** include files **/
#include <rw/cstring.h>
#include <rw/ctoken.h>
#include <rw/rwtime.h>

#include "cparms.h"
#include "msg_multi.h"
#include "msg_ptreg.h"
#include "msg_cmd.h"
#include "message.h"
#include "msg_reg.h"
#include "msg_ptreg.h"
#include "msg_pdata.h"
#include "msg_signal.h"
#include "connection.h"
#include "pointtypes.h"
#include "dllbase.h"
#include "logger.h"
#include "guard.h"

// this class header
#include "fdrcygnet.h"
#include "fdrpointlist.h"


/** local definitions **/
#define FDR_CYGNET_VERSION "1.2"

/** global used to start the interface by c functions **/
CtiFDRCygnet * myInferface;

const CHAR * CtiFDRCygnet::KEY_SCAN_RATE_SECONDS = "FDR_CYGNET_SCAN_SECONDS";
const CHAR * CtiFDRCygnet::KEY_ANALOG_SERVICE_NAME = "FDR_CYGNET_ANALOG_SERVICE";
const CHAR * CtiFDRCygnet::KEY_STATUS_SERVICE_NAME = "FDR_CYGNET_STATUS_SERVICE";
const CHAR * CtiFDRCygnet::KEY_HI_REASONABILITY_FILTER = "FDR_CYGNET_HI_FILTER";
const CHAR * CtiFDRCygnet::KEY_DEBUG_MODE = "FDR_CYGNET_DEBUG_MODE";
const CHAR * CtiFDRCygnet::KEY_DB_RELOAD_RATE = "FDR_CYGNET_DB_RELOAD_RATE";

// Constructors, Destructor, and Operators
CtiFDRCygnet::CtiFDRCygnet()
: CtiFDRInterface(RWCString("CYGNET")),
iHiReasonabilityFilter(0.0)
{
    init();
}


CtiFDRCygnet::~CtiFDRCygnet()
{
}

/*************************************************
* Function Name: CtiFDRCygnet::init
*
* Description: create threads and loads config
*              but does not start the interface
*
**************************************************
*/
BOOL CtiFDRCygnet::init( void )
{
    BOOL retVal = FALSE;

    iAnalogServiceState = CSTATE_NOT_INSTALLED;
    iStatusServiceState = CSTATE_NOT_INSTALLED;

    // init the base class
    if (!Inherited::init())
    {
        retVal = FALSE;
    }
    else
    {

        if ( !readConfig( ) )
        {
            retVal = FALSE;
        }

        if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << " Loading Cygnet translation list: loadTranslationList()" << endl;
        }
        loadTranslationLists();

        // create a Cygnet Get Data thread object
        iThreadGetCygnetData = rwMakeThreadFunction(*this,
                                                    &CtiFDRCygnet::threadFunctionGetDataFromCygnet);

    }


    return retVal;
}


// ************************************
// Begin getters and setters

ULONG CtiFDRCygnet::getScanRateSeconds() const
{
    return iScanRateSeconds;
}

void CtiFDRCygnet::setScanRateSeconds(const ULONG mySeconds)
{
    iScanRateSeconds = mySeconds;
}

RWCString CtiFDRCygnet::getAnalogServiceName() const
{
    return iAnalogServiceName;
}

RWCString CtiFDRCygnet::getStatusServiceName() const
{
    return iStatusServiceName;
}


CtiFDRCygnet::ConnectState CtiFDRCygnet::getAnalogServiceState() const
{
    return iAnalogServiceState;
}

CtiFDRCygnet & CtiFDRCygnet::setAnalogServiceState(const CtiFDRCygnet::ConnectState myCState)
{
    iAnalogServiceState = myCState;
    return *this;
}

CtiFDRCygnet::ConnectState CtiFDRCygnet::getStatusServiceState() const
{
    return iStatusServiceState;
}

CtiFDRCygnet & CtiFDRCygnet::setStatusServiceState(const CtiFDRCygnet::ConnectState myCState)
{
    iStatusServiceState = myCState;
    return *this;
}

double CtiFDRCygnet::getHiReasonabilityFilter() const
{
    return iHiReasonabilityFilter;
}

CtiFDRCygnet & CtiFDRCygnet::setHiReasonabilityFilter(const double myValue)
{
    iHiReasonabilityFilter = myValue;
    return *this;
}


// End getters and setters
// ************************************


/*************************************************
* Function Name: CtiFDRCygnet::run()
*
* Description: runs the interface
*
**************************************************
*/
BOOL CtiFDRCygnet::run( void )
{

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime( ) << " ----- Starting FDR Cygnet Version " << FDR_CYGNET_VERSION << endl;
    }

    // crank up the base class
    Inherited::run();

    // startup our interfaces
    iThreadGetCygnetData.start();

    return TRUE;
}

/*************************************************
* Function Name: CtiFDRCygnet::stop()
*
* Description: stops all threads
*
**************************************************
*/
BOOL CtiFDRCygnet::stop( void )
{
    //
    // FIXFIXFIX  - may need to add exception handling here
    //

    iThreadGetCygnetData.requestCancellation();

    // stop the base class
    Inherited::stop();

    return TRUE;
}


/*************************************************
* Function Name: CtiFDRCygnet::config()
*
* Description: loads cparm config values
*
**************************************************
*/
int CtiFDRCygnet::readConfig( void )
{
    int         successful = TRUE;
    RWCString   tempStr;


    tempStr = getCparmValueAsString(KEY_SCAN_RATE_SECONDS);
    if (tempStr.length() > 0)
    {
        iScanRateSeconds = atol(tempStr);
    }
    else if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
    {
        iScanRateSeconds = 300;
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << " Could not find Cygnet Scan Rate defaulting to " << iScanRateSeconds << " seconds" << endl;
    }

    tempStr = getCparmValueAsString(KEY_ANALOG_SERVICE_NAME);
    if (tempStr.length() > 0 && tempStr.length() <= 18)
    {
        iAnalogServiceName = tempStr;
    }
    else if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << " Cygnet Analog Service is not defined." << endl;
    }

    tempStr = getCparmValueAsString(KEY_STATUS_SERVICE_NAME);
    if (tempStr.length() > 0 && tempStr.length() <= 18)
    {
        iStatusServiceName = tempStr;
    }
    else if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << " Cygnet Status Service is not defined." << endl;
    }

    tempStr = getCparmValueAsString(KEY_DB_RELOAD_RATE);
    if (tempStr.length() > 0)
    {
        setReloadRate (atoi(tempStr));
    }
    else
    {
        setReloadRate (86400);
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << " Database will be reloaded " << getReloadRate() << " seconds" << endl;
    }

    tempStr = getCparmValueAsString(KEY_HI_REASONABILITY_FILTER);
    if (tempStr.length() > 0)
    {
        setHiReasonabilityFilter(atof(tempStr));
    }

    tempStr = getCparmValueAsString(KEY_DEBUG_MODE);
    if (tempStr.length() > 0)
        setInterfaceDebugMode (true);
    else
        setInterfaceDebugMode (false);



    if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);

        if (getHiReasonabilityFilter() > 0)
            dout << " Using Cygnet High Reasonability Filter: " << getHiReasonabilityFilter() << endl;
        else
            dout << " No Cygnet High Reasonability Filter is in use" << endl;

    }

    return successful;
}



/**************************************************************************
* Function Name: CtiFDRCygnet::getDataFromCygnetThreadFunction( void )
*
* Description: thread that get the periodic triggers getting data
*
***************************************************************************
*/
void CtiFDRCygnet::threadFunctionGetDataFromCygnet( void )
{
    RWRunnableSelf  pSelf = rwRunnable( );
    ULONG   nextScanTime;
    time_t  timeNow;


    try
    {
        for ( ; ; )
        {

            //  while i'm not getting anything
            nextScanTime = calculateNextSendTime();
            ::time (&timeNow);

            do
            {
                pSelf.serviceCancellation( );
                pSelf.sleep(1000);

                // get now
                ::time (&timeNow);

            } while ( timeNow < nextScanTime );


            if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime::now() << " - Getting Data from CYGNET Interface - " << endl;
            }

            // this returns true if already connected
            if (connectToAnalogService())
            {
                retreiveAnalogPoints();
            }

            // this returns true if already connected
            if (connectToStatusService())
            {
                retreiveStatusPoints();
            }
        }
    }

    catch ( RWCancellation &cancellationMsg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        return;
    }
}


/**************************************************************************
* Function Name: CtiFDRCygnet::sendMessageToForeignSys ()
*
* Description: We do not send data to Cygnet but we must provide this
*              function for all FDR Interfaces.
*
***************************************************************************
*/
bool CtiFDRCygnet::sendMessageToForeignSys ( CtiMessage *aMessage )
{
    // message is deleted in fdrinterface thread
    return TRUE;
}

/**************************************************************************
* Function Name: CtiFDRCygnet::processMessageFromForeignSystem ()
*
* Description: Cygnet doesn't initiate data output on its own, we must request it
*               from the system.  Because of this,
*               We must provide this function for all FDR Interfaces.
*
***************************************************************************
*/
INT CtiFDRCygnet::processMessageFromForeignSystem (CHAR *data)
{
    return NORMAL;
}
/************************************************************************
* Function Name: CtiFDRCygnet::connectToAnalogService()
*
* Description: Connect the Cygnet Analog Service (call their API)
*
*
*************************************************************************
*/
bool CtiFDRCygnet::connectToAnalogService()
{
    int                 returnValue;
    bool                successful(FALSE);
    RWCString           logDesc;
    RWCString           logInfo;

    CtiSignalMsg  *     pEventLog    = NULL;

    if (getAnalogServiceState() == CSTATE_NORMAL)
    {
        successful = TRUE;
    }
    else
    {
        if (getAnalogServiceName().length() > 0)
        {
            // there is a service define - try to connect
            if (isInterfaceInDebugMode())
            {
                // pretend we have a cygnet interface
                returnValue = 0;

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << " Simulation of Cygnet Interface for debugging" << endl;
                }
            }
            else
            {
                returnValue = DnaClientSvcDirConnect(getAnalogServiceName());
            }

            if (returnValue != 0 )
            {
                // Error codes are defined in DCL.h
                setAnalogServiceState(CSTATE_FAILED);
                logDesc = "Connect Fail Cygnet Analog Service";
                logInfo = "SRV Name: " + getAnalogServiceName();

                if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " " << logDesc << " " << logInfo;
                    dout << "  return code: " << returnValue << endl;
                }

            }
            else
            {
                // connection established
                logDesc = "Connected Cygnet Analog Service";
                logInfo = "";

                successful = TRUE;
                setAnalogServiceState(CSTATE_NORMAL);
                if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " " << logDesc << endl;
                }
            }
        }
        else
        {
            logDesc = "No Cygnet Analog Service Defined";
            logInfo = "";
            if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << logDesc << endl;
            }
        }

        pEventLog = new CtiSignalMsg(0, //FIXFIXFIX change this later to FDR System Point
                                     0,
                                     logDesc,
                                     logInfo,
                                     GeneralLogType);

        // comsumes memory
        sendMessageToDispatch(pEventLog);
    }

    return successful;
}

/************************************************************************
* Function Name: CtiFDRCygnet::connectToStatusService()
*
* Description: Connect the Cygnet Status Service (call their API)
*
*
*************************************************************************
*/
bool CtiFDRCygnet::connectToStatusService()
{
    int                 returnValue;
    bool                successful(FALSE);
    RWCString           logDesc;
    RWCString           logInfo;

    CtiSignalMsg  *     pEventLog    = NULL;


    if (getStatusServiceState() == CSTATE_NORMAL)
    {
        successful = TRUE;
    }
    else
    {
        if (getStatusServiceName().length() > 0)
        {
            if (isInterfaceInDebugMode())
            {
                // pretend we have a cygnet interface
                returnValue = 0;

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "Simulation of Cygnet Status Interface for debugging" << endl;
                }
            }
            else
            {
                returnValue = DnaClientSvcDirConnect( getStatusServiceName() );
            }

            if (returnValue != 0 )
            {
                // Error codes are defined in DCL.h
                setStatusServiceState(CSTATE_FAILED);

                logDesc = "Connect fail Cygnet Status Service";
                logInfo = "SRV Name: " + getStatusServiceName();

                if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " " << logDesc <<  " " << logInfo;
                    dout << " return code: " << returnValue << endl;
                }
            }
            else
            {
                // connection established
                successful = TRUE;
                setStatusServiceState(CSTATE_NORMAL);

                logDesc = "Connected to Cygnet Status Service";
                logInfo = "";

                if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " " << logDesc << endl;
                }
            }
        }
        else
        {
            logDesc = "No Cygnet Status Service Defined";
            logInfo = "";

            if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << logDesc << endl;
            }
        }


        pEventLog = new CtiSignalMsg(0, //FIXFIXFIX change this later to FDR System Point
                                     0,
                                     logDesc,
                                     logInfo,
                                     GeneralLogType);

        // comsumes memory
        sendMessageToDispatch(pEventLog);
    }

    return successful;
}



/************************************************************************
* Function Name: CtiFDRCygnet::retreiveAnalogPoints()
*
* Description: Retrieve all Analog Points from the Cygnet Service
*              using their API and Send to Dispatch.
*
*************************************************************************
*/
bool CtiFDRCygnet::retreiveAnalogPoints()
{
    int     returnValue;
    bool    successful(FALSE);
    bool    sendNoneUpdate;

    CtiPointDataMsg   *pData      = NULL;
    CtiMultiMsg       *pMultiData = NULL;

    CtiCommandMsg     *pCmdMsg    =  NULL;

    USHORT      bytesReceived;
    double      myNewValue = 0;
    double      myHiReasonabilityFilter;
    char        charValue[32];

    RWTime      myNewTime;

    RWCString   desc("Cygnet Update");  // not sure if this is needed

    CtiFDRPoint *       point = NULL;

    RT_GET_NAMED_REC_REQ    CygnetRequest;
    RT_GET_NAMED_REC_RESP   CygnetResponse;

    memset( &CygnetRequest, 0, sizeof(RT_GET_NAMED_REC_REQ) );

    RWCString myAnalogServ(getAnalogServiceName());
    myHiReasonabilityFilter = getHiReasonabilityFilter();

    // loop through all analog points
    CtiLockGuard<CtiMutex> guard(getReceiveFromList().getMutex());
    CtiFDRManager::CTIFdrPointIterator  myIterator(getReceiveFromList().getPointList()->getMap());
    int x;

    pMultiData = new CtiMultiMsg;
    int messCount = 0;
    int firstPass=true;

    for ( ; myIterator(); )
    {
        // get the data from the list
        sendNoneUpdate = FALSE;
        point = myIterator.value();

        if ((point->getPointType() == AnalogPointType) ||
            (point->getPointType() == PulseAccumulatorPointType) ||
            (point->getPointType() == DemandAccumulatorPointType) ||
            (point->getPointType() == CalculatedPointType))
            {

                for (x=0; x < point->getDestinationList().size(); x++)
                {
                    if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Requesting Cygnet Analog ID: " << point->getDestinationList()[x].getTranslation();
                        dout << " Yukon Pt ID: " << point->getPointID() << endl;
                    }
                    else if ( (firstPass) && (getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL) )
                    {
                        // this limits the debug by only logging info about the first item
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " (Min Debug) Requesting Cygnet Analog ID: " << point->getDestinationList()[x].getTranslation();
                        dout << " Yukon Pt ID: " << point->getPointID() << endl;
                    }

                    // Each message has its own unique type
                    CygnetRequest.header.type = RT_GET_NAMED_REC;

                    // copy Cynget point ID to the array (element 0 only for now)
                    strcpy(CygnetRequest.names[0], point->getDestinationList()[x].getTranslation());

                    // set the number of points (36 is max)
                    CygnetRequest.header.count = 1;

                    if (isInterfaceInDebugMode())
                    {
                        // pretend we have a cygnet interface
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << "Getting Analog from Cygnet: " << CygnetRequest.names[0] << endl;
                        }

                        returnValue = 0;

                        //returnValue = 1;  // test Nonupdated block

                        // fake time
                        time(&CygnetResponse.recs[0].time);

                        CygnetResponse.header.err = 0;
                        CygnetResponse.header.count = 1;


                        ltoa(rand(), CygnetResponse.recs[0].val, 10);
                    }
                    else
                    {
                        // need to get our data before continuing
                        returnValue = DclCall(myAnalogServ,
                                              &CygnetRequest,
                                              sizeof(RT_GET_NAMED_REC_REQ),
                                              &CygnetResponse,
                                              sizeof(RT_GET_NAMED_REC_RESP),
                                              &bytesReceived);
                    }

                    if (!returnValue && CygnetResponse.header.count == 1 && !CygnetResponse.header.err )
                    {
                        // time stamp from CygNet
                        myNewTime = RWTime((long)(((long)CygnetResponse.recs[0].time) + rwEpoch) );

                        if (myNewTime > point->getLastTimeStamp())
                        {
                            // fill in the value because time stamp has changed
                            memcpy(&charValue, &CygnetResponse.recs[0].val, sizeof(CygnetResponse.recs[0].val));
                            charValue[sizeof(CygnetResponse.recs[0].val)] = '\0';

                            myNewValue = atof(charValue);


                            // use the multiplier and offset
                            myNewValue *= point->getMultiplier();
                            myNewValue += point->getOffset();

                            if ( (myHiReasonabilityFilter > 0) && (myNewValue > myHiReasonabilityFilter) )
                            {
                                // above high reasonability filter so toss it out
                                sendNoneUpdate = TRUE;

                                if (getDebugLevel() & EXPECTED_ERR_FDR_DEBUGLEVEL)
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << "Cygnet ID: " << point->getDestinationList()[x].getTranslation();
                                    dout << " was above HiReasonabilityFilter: " << myHiReasonabilityFilter;
                                    dout << " Hi Value was: " << myNewValue << endl;
                                }

                            }
                            else
                            {
                                if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << "Updating Cygnet ID: " << point->getDestinationList()[x].getTranslation();
                                    dout << " to New Value: " << myNewValue;
                                    dout << " New Time: " << myNewTime << endl;

                                }
                                else if ( (firstPass) && (getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL) )
                                {
                                    // this limits the debug by only logging info about the first item
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << "Updating Cygnet ID: " << point->getDestinationList()[x].getTranslation();
                                    dout << " to New Value: " << myNewValue;
                                    dout << " New Time: " << myNewTime << endl;
                                }

                                pData = new CtiPointDataMsg(point->getPointID(),
                                                            myNewValue,
                                                            NormalQuality,
                                                            AnalogPointType,
                                                            desc);

                                pData->setTime(myNewTime);

                                // save the last time for future check
                                point->setLastTimeStamp(myNewTime);

                                // consumes a delete memory
                                //sendMessageToDispatch(pData);

                                //PUT into Multi Message
                                pMultiData->getData( ).insert( pData );

                                ++messCount;

                            }
                        }
                        else
                        {
                            // old time stamp
                            if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << "OLD time for Cygnet ID: " << point->getDestinationList()[x].getTranslation() << " was " << myNewTime << endl;
                            }
                            else if ( (firstPass) && (getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL) )
                            {
                                // this limits the debug by only logging info about the first item
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << "OLD time for Cygnet ID: " << point->getDestinationList()[x].getTranslation() << " was " << myNewTime << endl;
                            }

                        }
                    }
                    else
                    {
                        // failed so set to nonupdated
                        sendNoneUpdate = true;

                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << " Cygnet ID: " << point->getDestinationList()[x].getTranslation();
                            dout << " failed and is NonUpdated " << endl;
                        }
                        else if ( (firstPass) && (getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL) )
                        {
                            // this limits the debug by only logging info about the first item
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << " Cygnet ID: " << point->getDestinationList()[x].getTranslation();
                            dout << " failed and is NonUpdated " << endl;
                        }
                    }

                    if (sendNoneUpdate == TRUE)
                    {
                        // failed so set to nonupdated
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Cygnet ID: " << point->getDestinationList()[x].getTranslation();
                            dout << " failed and is NonUpdated" << endl;
                        }

                        pCmdMsg = new CtiCommandMsg(CtiCommandMsg::UpdateFailed);

                        pCmdMsg->insert( -1 );                          // This is the dispatch token and is unimplemented at this time
                        pCmdMsg->insert(OP_POINTID);                    // This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
                        pCmdMsg->insert(point->getPointID());  // The id (device or point which failed)
                        pCmdMsg->insert(ScanRateGeneral);               // One of ScanRateGeneral,ScanRateAccum,ScanRateStatus,ScanRateIntegrity, or if unknown -> ScanRateInvalid defined in yukon.h
                        pCmdMsg->insert(UnknownError);                  // The error number from dsm2.h or yukon.h which was reported.

                        // consumes and deletes pData memory
                        //sendMessageToDispatch(pCmdMsg);

                        pMultiData->getData( ).insert( pCmdMsg );

                        ++messCount;

                    }

                    if (messCount > 499)
                    {
                        // send in blocks of 500
                        sendMessageToDispatch(pMultiData);
                        pMultiData = new CtiMultiMsg;
                        messCount = 0;

                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Posting 500 Analog Messages to Dispatch" << endl;
                        }
                    }
                }
            }
        firstPass=false;

    }   // end iterator


    if (messCount > 0)
    {
        // send last block
        sendMessageToDispatch(pMultiData);

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Posting " << messCount << " Analog Messages to Dispatch" << endl;
        }
    }
    else
    {
        // clean memory
        delete pMultiData;
    }

    return successful;
}

/************************************************************************
* Function Name: CtiFDRCygnet::retreiveStatusPoints()
*
* Description: Retrieve all Status Points from the Cygnet Service
*              using their API and Send to Dispatch.
*
*************************************************************************
*/
bool CtiFDRCygnet::retreiveStatusPoints()
{
    int     returnValue;
    bool    successful(FALSE);
    char    charValue[32];
    char    myBuffer[32];

    CtiPointDataMsg   *pData      = NULL;
    CtiCommandMsg     *pCmdMsg    = NULL;
    CtiMultiMsg       *pMultiData = NULL;

    USHORT      bytesReceived;
    double      myNewValue = 0;
    RWTime      myNewTime;
    CtiFDRPoint *       point = NULL;

    bool        sendNoneUpdate;

    RWCString   desc("Cygnet Update");  // not sure if this is needed

    RT_GET_NAMED_REC_REQ    CygnetRequest;
    RT_GET_NAMED_REC_RESP   CygnetResponse;

    memset( &CygnetRequest, 0, sizeof(RT_GET_NAMED_REC_REQ) );

    RWCString myStatusServ(getStatusServiceName());

    // loop through all analog points
    CtiLockGuard<CtiMutex> guard(getReceiveFromList().getMutex());
    CtiFDRManager::CTIFdrPointIterator  myIterator(getReceiveFromList().getPointList()->getMap());
    int x;

    pMultiData = new CtiMultiMsg;
    int messCount = 0;

    int firstPass=true;

    for ( ; myIterator(); )
    {
        sendNoneUpdate = FALSE;

        point = myIterator.value();

        if (point->getPointType() == StatusPointType)
        {
            for (x=0; x < point->getDestinationList().size(); x++)
            {
                if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Requesting Cygnet Status ID: " << point->getDestinationList()[x].getTranslation();
                    dout << " for Yukon Pt ID: " << point->getPointID() << endl;
                }
                else if ( (firstPass) && (getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL) )
                {
                    // this limits the debug by only logging info about the first item
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Requesting Cygnet Status ID: " << point->getDestinationList()[x].getTranslation();
                    dout << " for Yukon Pt ID: " << point->getPointID() << endl;
                }

                // Each message has its own unique type
                CygnetRequest.header.type = RT_GET_NAMED_REC;

                // copy Cynget point ID to the array (element 0 only for now)
                strcpy(CygnetRequest.names[0], point->getDestinationList()[x].getTranslation());

                // set the number of points (36 is max)
                CygnetRequest.header.count = 1;

                if (isInterfaceInDebugMode())
                {
                    // pretend we have a cygnet interface
                    if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << "Getting Status from Cygnet: " << CygnetRequest.names[0] << endl;
                    }
                    returnValue = 0;
                    char temp[32];
                    //returnValue = 1;  // test Nonupdated block

                    CygnetResponse.header.err = 0;
                    CygnetResponse.header.count = 1;

                    // fake time
                    time(&CygnetResponse.recs[0].time);

                    //CygnetResponse.recs[0].time -= 300;  // test 5 minutes behind

                    // some kind of Random status
                    itoa(rand(), temp, 10);
                    if (temp[0] >  '4')
                    {
                        itoa(1, CygnetResponse.recs[0].val, 10);
                    }
                    else
                    {
                        itoa(0, CygnetResponse.recs[0].val, 10);
                    }
                }
                else
                {
                    // need to get our data before continuing
                    returnValue = DclCall(myStatusServ,
                                          &CygnetRequest,
                                          sizeof(RT_GET_NAMED_REC_REQ),
                                          &CygnetResponse,
                                          sizeof(RT_GET_NAMED_REC_RESP),
                                          &bytesReceived);
                }

                if (!returnValue && CygnetResponse.header.count == 1 && !CygnetResponse.header.err )
                {
                    // time stamp from CygNet

                    //myNewTime = RWTime((long)(((long)CygnetResponse.recs[0].time) + rwEpoch) );

                    // do not check time stamp on status points - always send to Dispatch

                    /*  fill in the value..
                        cygnet open   is a 0    Yukon open   is a 0 but be safe and use define
                        cygnet closed is a 1       Yukon closed is a 1 but be safe and use define
                    */
                    memcpy(&charValue, &CygnetResponse.recs[0].val, sizeof(CygnetResponse.recs[0].val));
                    charValue[sizeof(CygnetResponse.recs[0].val)] = '\0';

                    if (atoi(charValue) == Cygnet_Open)
                    {
                        myNewValue = Yukon_Open;
                    }
                    else if (atoi(charValue) == Cygnet_Closed)
                    {
                        myNewValue = Yukon_Closed;
                    }
                    else
                    {
                        // unexpected value
                        sendNoneUpdate = true;

                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << "Cygnet ID: " << point->getDestinationList()[x].getTranslation();
                        dout << " Invalid Status Value: " << charValue << endl;
                    }

                    if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << "Updating Cygnet ID: " << point->getDestinationList()[x].getTranslation();
                        dout << " to New Raw Value: " << myNewValue;
                        dout << " New Time: " << myNewTime <<  endl;

                        // some specific Cygnet Info
                        //memcpy(&myBuffer, &CygnetResponse.recs[0].val, 16);
                        //myBuffer[16] = '\0';
                        //dout << "Expanded Cygnet Real Value: " << charValue;
                        //
                        //memcpy(&myBuffer, &CygnetResponse.recs[0].desc, 24);
                        //myBuffer[24] = '\0';
                        //dout << " Descr: " << myBuffer <<  endl;

                    }
                    else if ( (firstPass) && (getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL) )
                    {
                        // this limits the debug by only logging info about the first item
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << "Updating Cygnet ID: " << point->getDestinationList()[x].getTranslation();
                        dout << " to New Raw Value: " << myNewValue;
                        dout << " New Time: " << myNewTime << endl;
                    }

                    if (sendNoneUpdate == false)
                    {
                        pData = new CtiPointDataMsg(point->getPointID(),
                                                    myNewValue,
                                                    NormalQuality,
                                                    StatusPointType,
                                                    desc);

                        pData->setTime(myNewTime);

                        // consumes a delete memory
                        //sendMessageToDispatch(pData);

                        // PUT into Multi Message
                        pMultiData->getData( ).insert( pData );

                        ++messCount;

                    }

                    // do not need at this time for status points - save the last time for future check
                    //iStatusPointList.getIdMapList()[index]->setLastTimeStamp(myNewTime);

                }
                else
                {
                    // failed so set to nonupdated
                    sendNoneUpdate = true;

                    if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << " Cygnet ID: " << point->getDestinationList()[x].getTranslation();
                        dout << " failed and is NonUpdated " << endl;
                    }
                    else if ( (firstPass) && (getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL) )
                    {
                        // this limits the debug by only logging info about the first item
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << " Cygnet ID: " << point->getDestinationList()[x].getTranslation();
                        dout << " failed and is NonUpdated " << endl;
                    }
                }


                if (sendNoneUpdate == true)
                {
                    pCmdMsg = new CtiCommandMsg(CtiCommandMsg::UpdateFailed);

                    pCmdMsg->insert( -1 );                          // This is the dispatch token and is unimplemented at this time
                    pCmdMsg->insert(OP_POINTID);                    // This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
                    pCmdMsg->insert(point->getPointID());  // The id (device or point which failed)
                    pCmdMsg->insert(ScanRateGeneral);               // One of ScanRateGeneral,ScanRateAccum,ScanRateStatus,ScanRateIntegrity, or if unknown -> ScanRateInvalid defined in yukon.h
                    pCmdMsg->insert(UnknownError);                  // The error number from dsm2.h or yukon.h which was reported.


                    // consumes and deletes pData memory
                    //sendMessageToDispatch(pCmdMsg);

                    //- PUT into Multi Message
                    pMultiData->getData( ).insert( pCmdMsg );

                    ++messCount;

                }

                if (messCount > 499)
                {
                    // send in blocks of 500
                    sendMessageToDispatch(pMultiData);
                    pMultiData = new CtiMultiMsg;
                    messCount = 0;

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Posting 500 Status Messages to Dispatch" << endl;
                    }

                }
            }

        }
        firstPass=false;
    }   // end iterator


    if (messCount > 0)
    {
        // send last block
        sendMessageToDispatch(pMultiData);

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Posting " << messCount << " Status Messages to Dispatch" << endl;
        }
    }
    else
    {
        // clean memory
        delete pMultiData;
    }

    return successful;
}


bool CtiFDRCygnet::loadTranslationLists()
{
    bool retCode = true;

/*
    if(getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "-- Cygnet Analog Points Loaded currently: " << iAnalogPointList.getMap().entries() << endl;
        dout << "-- Cygnet Status Points Loaded currently: " << iStatusPointList.getMap().entries() << endl;
    }
*/
    retCode = loadLists(getReceiveFromList());

    if (!retCode)
    {
        if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Translation list reload for FDRCygnet failed, keeping original points " << endl;
        }
    }

    return retCode;
}

/************************************************************************
* Function Name: CtiFDRCygnet::loadTranslationList()
*
* Description: Creates a seperate collection of Status and Analog Point
*              IDs and Cygnet IDs for translation.
*
*************************************************************************
*/
bool CtiFDRCygnet::loadLists(CtiFDRPointList &aList)
{
    bool                successful(FALSE);
    int                 analogCount = 0;
    int                 statusCount = 0;

    CtiFDRPoint *       translationPoint = NULL;
    RWCString           myTranslateName;
    RWCString           tempString1;
    RWCString           tempString2;
    bool                foundPoint = false;

    try
    {
        // make a list with all received points
        CtiFDRManager   *pointList = new CtiFDRManager(getInterfaceName(),
                                                       RWCString (FDR_INTERFACE_RECEIVE));

        if (pointList->loadPointList().errorCode() == (RWDBStatus::ok))
        {
            CtiLockGuard<CtiMutex> sendGuard(aList.getMutex());
            if (aList.getPointList() != NULL)
            {
                aList.deletePointList();
            }
            aList.setPointList (pointList);

            // get iterator on list
            CtiFDRManager::CTIFdrPointIterator  myIterator(aList.getPointList()->getMap());
            int x;

            for ( ; myIterator(); )
            {
                foundPoint = true;
                translationPoint = myIterator.value();

                for (x=0; x < translationPoint->getDestinationList().size(); x++)
                {

                    if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << "Parsing Yukon Point ID" << translationPoint->getPointID();
                        dout << " translate: " << translationPoint->getDestinationList()[x].getTranslation() << endl;
                    }

                    // there should only be one destination for cygnet points
                    RWCTokenizer nextTranslate(translationPoint->getDestinationList()[x].getTranslation());

                    if (!(tempString1 = nextTranslate(";")).isNull())
                    {
                        // this in the from of POINTID:xxxx
                        RWCTokenizer nextTempToken(tempString1);

                        // do not care about the first part
                        nextTempToken(":");

                        tempString2 = nextTempToken(":");

                        // now we have a id with a :
                        if ( !tempString2.isNull() )
                        {
                            translationPoint->getDestinationList()[x].setTranslation (tempString2);
                            successful = true;

                            if (translationPoint->getPointType() == StatusPointType)
                            {
                                statusCount++;
                            }
                            else
                            {
                                analogCount++;
                            }
                        } // end if null

                    } // end first token
                }
            }   // end for interator

            pointList=NULL;

            if (getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "-- Cygnet Analog Points Loaded: " << analogCount << endl;
                dout << "-- Cygnet Status Points Loaded: " << statusCount << endl;
            }

            if (!successful)
            {
                if (!foundPoint)
                {
                    // means there was nothing in the list, wait until next db change or reload
                    successful = true;
                    if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " No points defined for use by interface " << getInterfaceName() << endl;
                    }
                }
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << " db read code " << pointList->loadPointList().errorCode() << endl;
            successful = false;
        }
    }   // end try block

    catch (RWExternalErr e )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "loadTranslationList():  " << e.why() << endl;
        RWTHROW(e);
    }

    return successful;
}
/************************************************************************
* Function Name: CtiFDRCygnet::calculateNextSendTime()
*
* Description: Calculate when we should do our next download
*              of all Analog and Status values
*
*************************************************************************
*/
ULONG CtiFDRCygnet::calculateNextSendTime()
{
    time_t   timeNow;
    ULONG    secondsPastHour;
    ULONG    interval = getScanRateSeconds();

    ULONG    tempTime;
    // get now
    ::time (&timeNow);

    // check where we sit
    secondsPastHour = timeNow % 3600L;

    if ((secondsPastHour % interval) == 0)
    {
        tempTime = timeNow + interval;
    }
    else
    {
        // calcuates next  on interval time and adds one interval
        tempTime =  timeNow + (interval - (secondsPastHour % interval));
    }

    return tempTime;
}




/****************************************************************************************
*
*      Here Starts some C functions that are used to Start the
*      Interface and Stop it from the Main() of FDR.EXE.
*
*/

#ifdef __cplusplus
extern "C" {
#endif

/************************************************************************
* Function Name: Extern C int RunInterface(void)
*
* Description: This is used to Start the Interface from the Main()
*              of FDR.EXE. Each interface it Dynamicly loaded and
*              this function creates a global FDRCygnet Object and then
*              calls its run method to cank it up.
*
*************************************************************************
*/

    DLLEXPORT int RunInterface(void)
    {

        // make a point to the interface
        myInferface = new CtiFDRCygnet();

        // now start it up
        return myInferface->run();
    }

/************************************************************************
* Function Name: Extern C int StopInterface(void)
*
* Description: This is used to Stop the Interface from the Main()
*              of FDR.EXE. Each interface it Dynamicly loaded and
*              this function stops a global FDRCygnet Object and then
*              deletes it.
*
*************************************************************************
*/
    DLLEXPORT int StopInterface( void )
    {

        myInferface->stop();
        delete myInferface;
        myInferface = 0;

        return 0;
    }


#ifdef __cplusplus
}
#endif

