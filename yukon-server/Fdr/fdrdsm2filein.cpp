#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*-----------------------------------------------------------------------------*
*
*    FILE NAME: fdrDsm2Filein.cpp
*
*    DATE: 04/11/2003
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrDSm2Filein.cpp-arc  $
*    REVISION     :  $Revision: 1.3 $
*    DATE         :  $Date: 2004/09/27 23:33:28 $
*
*
*    AUTHOR: David Sutton
*
*    PURPOSE:  ascii import
*
*    DESCRIPTION:
*
*    ---------------------------------------------------
*    History:
      $Log: fdrdsm2filein.cpp,v $
      Revision 1.3  2004/09/27 23:33:28  mfisher
      changes/updates for Boost compatibility

      Revision 1.2  2004/03/24 22:38:51  dsutton
      Added a text file interface to FDR to allow Yukon users to import data formated
      for DSM2's filein format.  Revision 1.0

      Revision 1.1.2.1  2004/03/23 22:11:42  dsutton
      Added a text file interface to FDR to allow Yukon users to import data formated
      for DSM2's filein format.  Revision 1.0


*
*
*
*    Copyright (C) 2000 Cannon Technologies, Inc.  All rights reserved.
*-----------------------------------------------------------------------------*
*/
#include <windows.h>
#include <wininet.h>
#include <fcntl.h>
#include <io.h>

/** include files **/
#include <rw/cstring.h>
#include <rw/ctoken.h>
#include <rw/rwtime.h>
#include <rw/rwdate.h>

#include "cparms.h"
#include "msg_cmd.h"
#include "pointtypes.h"

#include "dllbase.h"
#include "logger.h"
#include "guard.h"
#include "fdrtextfilebase.h"
#include "fdrdsm2filein.h"


CtiFDR_Dsm2Filein * textImportInterface;

const CHAR * CtiFDR_Dsm2Filein::KEY_INTERVAL = "FDR_DSM2FILEIN_INTERVAL";
const CHAR * CtiFDR_Dsm2Filein::KEY_FILENAME = "FDR_DSM2FILEIN_FILENAME";
const CHAR * CtiFDR_Dsm2Filein::KEY_DRIVE_AND_PATH = "FDR_DSM2FILEIN_DRIVE_AND_PATH";
const CHAR * CtiFDR_Dsm2Filein::KEY_DB_RELOAD_RATE = "FDR_DSM2FILEIN_DB_RELOAD_RATE";
const CHAR * CtiFDR_Dsm2Filein::KEY_QUEUE_FLUSH_RATE = "FDR_DSM2FILEIN_QUEUE_FLUSH_RATE";
const CHAR * CtiFDR_Dsm2Filein::KEY_DELETE_FILE = "FDR_DSM2FILEIN_DELETE_FILE";
const CHAR * CtiFDR_Dsm2Filein::KEY_USE_SYSTEM_TIME = "FDR_DSM2FILEIN_USE_SYSTEM_TIME";


// Constructors, Destructor, and Operators
CtiFDR_Dsm2Filein::CtiFDR_Dsm2Filein()
: CtiFDRTextFileBase(RWCString("DSM2FILEIN"))
{
    // init these lists so they have something
    CtiFDRManager   *recList = new CtiFDRManager(getInterfaceName(),RWCString(FDR_INTERFACE_RECEIVE));
    getReceiveFromList().setPointList (recList);
    recList = NULL;
    init();

}

CtiFDR_Dsm2Filein::~CtiFDR_Dsm2Filein()
{
}


bool CtiFDR_Dsm2Filein::shouldDeleteFileAfterImport() const
{
    return _deleteFileAfterImportFlag;
}

CtiFDR_Dsm2Filein &CtiFDR_Dsm2Filein::setDeleteFileAfterImport (bool aFlag)
{
    _deleteFileAfterImportFlag = aFlag;
    return *this;
}


bool CtiFDR_Dsm2Filein::useSystemTime() const
{
    return _useSystemTime;
}

CtiFDR_Dsm2Filein &CtiFDR_Dsm2Filein::setUseSystemTime (bool aFlag)
{
    _useSystemTime = aFlag;
    return *this;
}
BOOL CtiFDR_Dsm2Filein::init( void )
{
    // init the base class
    Inherited::init();
    _threadReadFromFile = rwMakeThreadFunction(*this,
                                               &CtiFDR_Dsm2Filein::threadFunctionReadFromFile);

    if (!readConfig( ))
    {
        return FALSE;
    }

    loadTranslationLists();
    return TRUE;
}
/*************************************************
* Function Name: CtiFDR_Dsm2Filein::run()
*
* Description: runs the interface
*
**************************************************
*/
BOOL CtiFDR_Dsm2Filein::run( void )
{
    // crank up the base class
    Inherited::run();

    // startup our interfaces
    _threadReadFromFile.start();

    return TRUE;
}


/*************************************************
* Function Name: CtiFDR_Dsm2Filein::stop()
*
* Description: stops all threads
*
**************************************************
*/
BOOL CtiFDR_Dsm2Filein::stop( void )
{
    _threadReadFromFile.requestCancellation();
    Inherited::stop();
    return TRUE;
}


RWTime CtiFDR_Dsm2Filein::ForeignToYukonTime (RWCString aTime)
{
    struct tm ts;
    RWTime retVal;

    if (aTime.length() == 19)
    {
        try
        {
            if (sscanf (aTime.data(),
                        "%2ld/%2ld/%4ld %2ld:%2ld:%2ld",
                        &ts.tm_mon,
                        &ts.tm_mday,
                        &ts.tm_year,
                        &ts.tm_hour,
                        &ts.tm_min,
                        &ts.tm_sec) != 6)
            {
                retVal = rwEpoch;
            }
            else
            {
                ts.tm_year -= 1900;
                ts.tm_mon--;
                ts.tm_isdst = RWTime().isDST();

                try
                {
                    retVal = RWTime(&ts);

                    // if RWTime can't make a time ???
                    if (!retVal.isValid())
                    {
                        retVal = rwEpoch;
                    }
                }
                catch (...)
                {
                    retVal = rwEpoch;
                }
            }
        }
        catch (...)
        {
            retVal = rwEpoch;
        }
    }
    else
        retVal = rwEpoch;
    return retVal;
}

USHORT CtiFDR_Dsm2Filein::ForeignToYukonQuality (RWCString aQuality)
{
    USHORT Quality = NonUpdatedQuality;

    if (!aQuality.compareTo ("N",RWCString::ignoreCase))
        Quality = NormalQuality;
    if (!aQuality.compareTo ("M",RWCString::ignoreCase))
        Quality = ManualQuality;

    return(Quality);
}


bool CtiFDR_Dsm2Filein::processFunctionOne (RWCString &aLine, CtiMessage **aRetMsg)
{
    bool retCode = false;
    bool pointValidFlag=true;
    RWCString tempString1;                // Will receive each token
    RWCTokenizer cmdLine(aLine);           // Tokenize the string a
    CtiFDRPoint         point;
    int fieldNumber=1,quality;
    double value;
    CHAR   action[60];
    RWCString linetimestamp,translationName,desc;
    RWTime pointtimestamp;


    /****************************
    * function 1 is of the following format
    * function,id,value,quality,timestamp,daylight savings flag
    *****************************
    */
    while (!(tempString1 = cmdLine(",\r\n")).isNull() && pointValidFlag)
    {
        switch (fieldNumber)
        {
            case 1:
                {
                    // this the function number so we do nothing with this
                    break;
                }
            case 2:
                {
                    // throw on the function header for the translation name
                    translationName=tempString1;
                    tempString1="1-----" + tempString1;

                    // lock the list while we're returning the point
                    {
                        CtiLockGuard<CtiMutex> receiveGuard(getReceiveFromList().getMutex());
                        pointValidFlag = findTranslationNameInList (tempString1, getReceiveFromList(), point);
                    }

                    if (pointValidFlag != true)
                    {
                        if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Translation for point " << translationName;
                            dout << " from " << getFileName() << " was not found" << endl;
                        }
                        desc = getFileName() + RWCString ("'s point ") + translationName + RWCString( " is not listed in the translation table");
                        _snprintf(action,60,"%s", translationName);
                        logEvent (desc,RWCString (action));
                    }

                    break;
                }
            case 3:
                {
                    value = atof(tempString1);
                    break;
                }
            case 4:
                {
                    quality = ForeignToYukonQuality(tempString1);
                    break;
                }

            case 5:
                {
                    linetimestamp=tempString1;
                    break;
                }
            case 6:
                {
                    linetimestamp=linetimestamp + " " + tempString1;

                    if (useSystemTime())
                    {
                        pointtimestamp=RWTime();
                    }
                    else
                    {
                        pointtimestamp = ForeignToYukonTime (linetimestamp);
                        if (pointtimestamp == rwEpoch)
                        {
                            pointValidFlag = false;
                        }
                    }
                    break;
                }
            default:
                break;
        }
        fieldNumber++;
    }

    if (pointValidFlag)
    {
        retCode = buildAndAddPoint (point,value,pointtimestamp,quality,translationName,aRetMsg);
    }
    return retCode;
}


bool CtiFDR_Dsm2Filein::buildAndAddPoint (CtiFDRPoint &aPoint,
                                          DOUBLE aValue,
                                          RWTime aTimestamp,
                                          int aQuality,
                                          RWCString aTranslationName,
                                          CtiMessage **aRetMsg)
{
    RWCString desc;
    CHAR   action[60];
    bool retCode = false;

    // figure out what it should be now
    switch (aPoint.getPointType())
    {
        case AnalogPointType:
        case PulseAccumulatorPointType:
        case DemandAccumulatorPointType:
        case CalculatedPointType:
        {
            DOUBLE value = aValue;

            value *= aPoint.getMultiplier();
            value += aPoint.getOffset();

            *aRetMsg = new CtiPointDataMsg(aPoint.getPointID(),
                                           value,
                                           aQuality,
                                        AnalogPointType);
                                    ((CtiPointDataMsg *)*aRetMsg)->setTime(aTimestamp);

            if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Analog point " << aTranslationName;
                dout << " value " << value << " from " << getFileName() << " assigned to point " << aPoint.getPointID() << endl;;
            }
            retCode = true;
            break;
        }
        case StatusPointType:
        {
            // check for control functions
            if (aPoint.isControllable())
            {
                // make sure the value is valid
                if ((aValue == OPENED) || (aValue == CLOSED))
                {
                    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Control point " << aTranslationName;
                        if (aValue == OPENED)
                        {
                            dout << " control: Open " ;
                        }
                        else
                        {
                            dout << " control: Closed " ;
                        }

                        dout <<" from " << getFileName() << " and processed for point " << aPoint.getPointID() << endl;;
                    }

                    // build the command message and send the control
                    *aRetMsg = new CtiCommandMsg(CtiCommandMsg::ControlRequest);

                    ((CtiCommandMsg *)*aRetMsg)->insert( -1 );                // This is the dispatch token and is unimplemented at this time
                    ((CtiCommandMsg *)*aRetMsg)->insert(0);                   // device id, unknown at this point, dispatch will find it
                    ((CtiCommandMsg *)*aRetMsg)->insert(aPoint.getPointID());  // point for control
                    ((CtiCommandMsg *)*aRetMsg)->insert(aValue);
                    retCode = true;

                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Invalid control state " << aValue;
                        dout << " for " << aTranslationName << " received from " << getFileName() << endl;
                    }
                    CHAR state[20];
                    _snprintf (state,20,"%.0f",aValue);
                    desc = getFileName() + RWCString (" control point received with an invalid state ") + RWCString (state);
                    _snprintf(action,60,"%s for pointID %d",
                              aTranslationName,
                              aPoint.getPointID());
                    logEvent (desc,RWCString (action));
                }
            }
            else
            {
                if ((aValue == OPENED) || (aValue == CLOSED))
                {
                    RWCString tracestate;
                    if (aValue == OPENED)
                    {
                        tracestate = RWCString ("Open");
                    }
                    else
                    {
                        tracestate = RWCString ("Closed");
                    }
                    *aRetMsg = new CtiPointDataMsg(aPoint.getPointID(),
                                                aValue,
                                                aQuality,
                                                StatusPointType);
                                            ((CtiPointDataMsg*)*aRetMsg)->setTime(aTimestamp);
                    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Status point " << aTranslationName;
                        dout << " new state: " << tracestate;
                        dout <<" from " << getFileName() << " assigned to point " << aPoint.getPointID() << endl;;
                    }
                    retCode = true;
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " Status point " << aTranslationName;
                    dout << " received an invalid state " << (int)aValue;
                    dout <<" from " << getFileName() << " for point " << aPoint.getPointID() << endl;;

                    CHAR state[20];
                    _snprintf (state,20,"%.0f",aValue);
                    desc = getFileName() + RWCString (" status point received with an invalid state ") + RWCString (state);
                    _snprintf(action,60,"%s for pointID %d",
                              aTranslationName,
                              aPoint.getPointID());
                    logEvent (desc,RWCString (action));

                }
            }
            break;
        }
    }
    return retCode;
}


bool CtiFDR_Dsm2Filein::validateAndDecodeLine (RWCString &aLine, CtiMessage **aRetMsg)
{
    bool retCode = false;
    bool flag;
    aLine.toLower();
    RWCString tempString1;                // Will receive each token
    RWCTokenizer cmdLine(aLine);           // Tokenize the string a
    CtiFDRPoint         point;
    int function;

    // grab the function number
    if (!(tempString1 = cmdLine(",\r\n")).isNull())
    {
        function = atoi (tempString1);

        // check the function number
        switch (function)
        {
            case 1:
            {
                /****************************
                * function 1 is of the following format
                * id,value,quality,timestamp,daylight savings flag
                *****************************
                */
                retCode=processFunctionOne (aLine,aRetMsg);
                break;
            }
            default:
                break;

        }
    }

    return retCode;
}



int CtiFDR_Dsm2Filein::readConfig( void )
{
    int         successful = TRUE;
    RWCString   tempStr;

    tempStr = getCparmValueAsString(KEY_INTERVAL);
    if (tempStr.length() > 0)
    {
        if (atoi (tempStr) <=1)
        {
            setInterval(1);
        }
        else
        {
            setInterval(atoi(tempStr));
        }
    }
    else
    {
        setInterval(900);
    }

    tempStr = getCparmValueAsString(KEY_FILENAME);
    if (tempStr.length() > 0)
    {
        setFileName(tempStr);
    }
    else
    {
        setFileName(RWCString ("yukon.txt"));
    }

    tempStr = getCparmValueAsString(KEY_DRIVE_AND_PATH);
    if (tempStr.length() > 0)
    {
        setDriveAndPath(tempStr);
    }
    else
    {
        setDriveAndPath(RWCString ("\\yukon\\server\\import"));
    }

    tempStr = getCparmValueAsString(KEY_DB_RELOAD_RATE);
    if (tempStr.length() > 0)
    {
        setReloadRate (atoi(tempStr));
    }
    else
    {
        setReloadRate (3600);
    }

    tempStr = getCparmValueAsString(KEY_QUEUE_FLUSH_RATE);
    if (tempStr.length() > 0)
    {
        setQueueFlushRate (atoi(tempStr));
    }
    else
    {
        // default to one second for stec, its only 2 points
        setQueueFlushRate (1);
    }

    setDeleteFileAfterImport(true);
    tempStr = getCparmValueAsString(KEY_DELETE_FILE);
    if (tempStr.length() > 0)
    {
        if (!tempStr.compareTo ("false",RWCString::ignoreCase))
        {
            setDeleteFileAfterImport (false);
        }
    }

    setUseSystemTime(false);
    tempStr = getCparmValueAsString(KEY_USE_SYSTEM_TIME);
    if (tempStr.length() > 0)
    {
        if (!tempStr.compareTo ("true",RWCString::ignoreCase))
        {
            setUseSystemTime (true);
        }
    }

    if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " DSM2 filein file name " << getFileName() << endl;
        dout << RWTime() << " DSM2 filein directory " << getDriveAndPath() << endl;
        dout << RWTime() << " DSM2 filein interval " << getInterval() << endl;
        dout << RWTime() << " DSM2 filein dispatch queue flush rate " << getQueueFlushRate() << endl;
        dout << RWTime() << " DSM2 filein db reload rate " << getReloadRate() << endl;

        if (shouldDeleteFileAfterImport())
            dout << RWTime() << " DSM2 filein file will be deleted after import" << endl;
        else
            dout << RWTime() << " DSM2 filein file will NOT be deleted after import" << endl;
        if (useSystemTime())
            dout << RWTime() << " DSM2 filein will stamp data with time at import" << endl;
        else
            dout << RWTime() << " DSM2 filein file stamp data according to time included in the import" << endl;


    }



    return successful;
}


/************************************************************************
* Function Name: CtiFDRTextFileBase::loadTranslationLists()
*
* Description: Creates a collection of points and their translations for the
*               specified direction
*
*************************************************************************
*/
bool CtiFDR_Dsm2Filein::loadTranslationLists()
{
    bool                successful(FALSE);
    CtiFDRPoint *       translationPoint = NULL;
    RWCString           tempString1;
    RWCString           tempString2;
    RWCString           translationName;
    bool                foundPoint = false;
    RWDBStatus          listStatus;

    try
    {
        // make a list with all received points
        CtiFDRManager   *pointList = new CtiFDRManager(getInterfaceName(),
                                                       RWCString (FDR_INTERFACE_RECEIVE));

        // keep the status
        listStatus = pointList->loadPointList();

        // if status is ok, we were able to read the database at least
        if ( listStatus.errorCode() == (RWDBStatus::ok))
        {
            /**************************************
            * seeing occasional problems where we get empty data sets back
            * and there should be info in them,  we're checking this to see if
            * is reasonable if the list may now be empty
            * the 2 entry thing is completly arbitrary
            ***************************************
            */
            if (((pointList->entries() == 0) && (getReceiveFromList().getPointList()->entries() <= 2)) ||
                (pointList->entries() > 0))
            {

                // get iterator on send list
                CtiFDRManager::CTIFdrPointIterator  myIterator(pointList->getMap());
                int x;
                RWCString translation_name;

                for ( ; myIterator(); )
                {
                    foundPoint = true;
                    translationPoint = myIterator.value();

                    for (x=0; x < translationPoint->getDestinationList().size(); x++)
                    {
                        if (getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << "Parsing Yukon Point ID " << translationPoint->getPointID();
                            //dout << " translate: " << translationPoint->getDestinationList()[x].getTranslation() << endl;
                            dout << " translate: " << translationPoint->getDestinationList()[x].getTranslation() << endl;
                        }
                        RWCTokenizer nextTranslate(translationPoint->getDestinationList()[x].getTranslation());

                        if (!(tempString1 = nextTranslate(";")).isNull())
                        {
                            RWCTokenizer nextTempToken(tempString1);

                            // do not care about the first part
                            nextTempToken(":");

                            tempString2 = nextTempToken(";");
                            tempString2(0,tempString2.length()) = tempString2 (1,(tempString2.length()-1));

                            // now we have a point id
                            if ( !tempString2.isNull() )
                            {
                                translation_name = tempString2 + "-----";
                                //translation_name += RWCString ('-----');

                                if (!(tempString1 = nextTranslate(";")).isNull())
                                {
                                    RWCTokenizer nextTempToken(tempString1);

                                    // do not care about the first part
                                    nextTempToken(":");

                                    tempString2 = nextTempToken(";");
                                    tempString2(0,tempString2.length()) = tempString2 (1,(tempString2.length()-1));
                                    if (!tempString2.isNull())
                                    {
                                        translation_name+=tempString2;
                                        translationPoint->getDestinationList()[x].setTranslation (translation_name);
                                        successful = true;
                                    }

                                }
                            }
                        }   // first token invalid
                    }
                }   // end for interator

                // lock the receive list and remove the old one
                {
                    CtiLockGuard<CtiMutex> receiveGuard(getReceiveFromList().getMutex());
                    if (getReceiveFromList().getPointList() != NULL)
                    {
                        getReceiveFromList().deletePointList();
                    }
                    getReceiveFromList().setPointList (pointList);
                }

                pointList=NULL;
                if (!successful)
                {
                    if (!foundPoint)
                    {
                        // means there was nothing in the list, wait until next db change or reload
                        successful = true;
                        if (getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " No points defined for use by interface " << getInterfaceName() << endl;
                        }
                    }
                }
                setLinkStatusID(getClientLinkStatusID (getInterfaceName()));
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Error loading (Receive) points for " << getInterfaceName() << " : Empty data set returned " << endl;
                successful = false;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " " << __FILE__ << " (" << __LINE__ << ") db read code " << listStatus.errorCode()  << endl;
            successful = false;
        }

    }   // end try block

    catch (RWExternalErr e )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Error loading translation lists for " << getInterfaceName() << endl;
        RWTHROW(e);
    }

    // try and catch the thread death
    catch ( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Error loading translation lists for " << getInterfaceName() << endl;
    }

    return successful;
}

/**************************************************************************
* Function Name: CtiFDRTextFileBase::threadFunctionReadFromFile (void )
*
* Description: thread that waits and then grabs the file for processing
*
***************************************************************************
*/
void CtiFDR_Dsm2Filein::threadFunctionReadFromFile( void )
{
    RWRunnableSelf  pSelf = rwRunnable( );
    INT retVal=0,tries=0;
    RWTime         timeNow;
    RWTime         refreshTime(rwEpoch);
    RWCString action,desc;
    CHAR fileName[200];
    FILE* fptr;
    char workBuffer[500];  // not real sure how long each line possibly is
    int attemptCounter=0;

    try
    {
        for ( ; ; )
        {
            pSelf.serviceCancellation( );
            pSelf.sleep (1000);

            timeNow = RWTime();

            // now is the time to get the file
            if (timeNow >= refreshTime)
            {
                _snprintf (fileName, 200, "%s\\%s",getDriveAndPath(),getFileName());

                fptr = fopen( fileName, "r");
                while ((fptr == NULL) && (attemptCounter < 10))
                {
                    attemptCounter++;
                    pSelf.sleep(1000);
                    pSelf.serviceCancellation( );
                }

                if( fptr == NULL )
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " " << getInterfaceName() << "'s file " << RWCString (fileName) << " was either not found or could not be opened" << endl;
                    }
                }
                else
                {
                    vector<RWCString>     pointVector;

                    // load list in the command vector
                    while ( fgets( (char*) workBuffer, 500, fptr) != NULL )
                    {
                        RWCString entry (workBuffer);
                        pointVector.push_back (entry);
                    }

                    fclose(fptr);
                    if( ferror( fptr ) != 0 )
                    {
                        pointVector.erase(pointVector.begin(), pointVector.end());
                    }
                    else
                    {
                        // retrieve each line in order
                        int totalLines = pointVector.size();
                        int lineCnt = 0;
                        CtiMessage      *retMsg=NULL;
                        while (lineCnt < totalLines )
                        {
                            if(validateAndDecodeLine( pointVector[lineCnt], &retMsg ))
                            {
                                queueMessageToDispatch (retMsg);
                            }
                            lineCnt++;
                        }
                        pointVector.erase(pointVector.begin(), pointVector.end());
                    }
                }

                if (shouldDeleteFileAfterImport())
                {
                    DeleteFile (fileName);
                }

                refreshTime = RWTime() - (RWTime::now().seconds() % getInterval()) + getInterval();
            }
        }
    }

    catch ( RWCancellation &cancellationMsg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "CANCELLATION CtiFDRDsm2Filein::threadFunctionReadFromFile in interface " <<getInterfaceName()<< endl;
    }

    // try and catch the thread death
    catch ( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Fatal Error:  CtiFDRTextIMport::threadFunctionReadFromFile  " << getInterfaceName() << " is dead! " << endl;
    }
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
        textImportInterface = new CtiFDR_Dsm2Filein();

        // now start it up
        return textImportInterface->run();
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

        textImportInterface->stop();
        delete textImportInterface;
        textImportInterface = 0;

        return 0;
    }


#ifdef __cplusplus
}
#endif


