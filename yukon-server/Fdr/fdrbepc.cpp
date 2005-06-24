#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*-----------------------------------------------------------------------------*
*
*    FILE NAME: fdrbepc.cpp
*
*    DATE: 12/31/2004
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrbepc.cpp-arc  $
*    REVISION     :  $Revision: 1.2 $
*    DATE         :  $Date: 2005/06/24 20:07:59 $
*
*
*    AUTHOR: David Sutton
*
*    PURPOSE:  ascii export
*
*    DESCRIPTION: 
*
*    ---------------------------------------------------
*    History: 
      $Log: fdrbepc.cpp,v $
      Revision 1.2  2005/06/24 20:07:59  dsutton
      New interface for KEM electric to send information to their
      power supplier Basin Electric

      Revision 1.1.4.1  2005/01/06 17:07:23  dsutton
      Added a new text file export from FDR.  Format is specified by Basin Electric
      for their members as a way to send the member's system load, controlled load
      available load, etc.  We currently have only the system load available in our
      system so the rest are defaulted to zero

*
*
*
*    Copyright (C) 2000 Cannon Technologies, Inc.  All rights reserved.
*-----------------------------------------------------------------------------*
*/
#include "yukon.h"
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
#include "fdrbepc.h"


CtiFDR_BEPC * textExportInterface;

const CHAR * CtiFDR_BEPC::KEY_INTERVAL = "FDR_BEPC_INTERVAL";
const CHAR * CtiFDR_BEPC::KEY_FILENAME = "FDR_BEPC_FILENAME";
const CHAR * CtiFDR_BEPC::KEY_DRIVE_AND_PATH = "FDR_BEPC_DRIVE_AND_PATH";
const CHAR * CtiFDR_BEPC::KEY_DB_RELOAD_RATE = "FDR_BEPC_DB_RELOAD_RATE";
const CHAR * CtiFDR_BEPC::KEY_QUEUE_FLUSH_RATE = "FDR_BEPC_QUEUE_FLUSH_RATE";
const CHAR * CtiFDR_BEPC::KEY_APPEND_FILE = "FDR_BEPC_APPEND_FILE";
const CHAR * CtiFDR_BEPC::KEY_COOP_ID = "FDR_BEPC_COOP_ID";

const CHAR * CtiFDR_BEPC::KEY_TOTAL_LOAD_KW = "TOTAL LOAD KW";

// Constructors, Destructor, and Operators
CtiFDR_BEPC::CtiFDR_BEPC()
: CtiFDRTextFileBase(RWCString("BEPC"))
{  
    // init these lists so they have something
    CtiFDRManager   *recList = new CtiFDRManager(getInterfaceName(),RWCString(FDR_INTERFACE_SEND)); 
    getSendToList().setPointList (recList);
    recList = NULL;
    init();

}

CtiFDR_BEPC::~CtiFDR_BEPC()
{
}

RWCString & CtiFDR_BEPC::getCoopID()
{
    return _coopid;
}

RWCString  CtiFDR_BEPC::getCoopID() const
{
    return _coopid;
}

CtiFDR_BEPC &CtiFDR_BEPC::setCoopID (RWCString aID)
{
    _coopid = aID;
    return *this;
}

bool CtiFDR_BEPC::shouldAppendToFile() const
{
    return _appendFlag;
}

CtiFDR_BEPC &CtiFDR_BEPC::setAppendToFile (bool aFlag)
{
    _appendFlag = aFlag;
    return *this;
}

BOOL CtiFDR_BEPC::init( void )
{
    // init the base class
    Inherited::init();    
    _threadWriteToFile = rwMakeThreadFunction(*this, 
                                               &CtiFDR_BEPC::threadFunctionWriteToFile);

    if (!readConfig( ))
    {
        return FALSE;
    }

    loadTranslationLists();
    return TRUE;
}
/*************************************************
* Function Name: CtiFDR_BEPC::run()
*
* Description: runs the interface
* 
**************************************************
*/
BOOL CtiFDR_BEPC::run( void )
{
    // crank up the base class
    Inherited::run();

    // startup our interfaces
    _threadWriteToFile.start();

    return TRUE;
}


/*************************************************
* Function Name: CtiFDR_BEPC::stop()
*
* Description: stops all threads 
* 
**************************************************
*/
BOOL CtiFDR_BEPC::stop( void )
{
    _threadWriteToFile.requestCancellation();
    Inherited::stop();
    return TRUE;
}


RWCString CtiFDR_BEPC::YukonToForeignTime (RWTime aTime)
{
    CHAR workBuffer[50];
    RWDate date;
    RWCString retVal;
    struct tm *gmTime;
    RWTime newTime;

    if (aTime.isValid())
    {
        // get a gmtime from our time
        LONG longTime=aTime.seconds()-rwEpoch;
        gmTime=gmtime(&longTime);

        _snprintf (workBuffer,
                       50,
                   "%04ld%02ld%02ld%02ld%02ld",
                   gmTime->tm_year+1900,
                   gmTime->tm_mon+1,
                   gmTime->tm_mday,
                   gmTime->tm_hour,
                   gmTime->tm_min);

        retVal = RWCString (workBuffer);
    }
    else
    {
        retVal = RWCString ("200001010000");
    }
    return retVal;
}

CHAR CtiFDR_BEPC::YukonToForeignQuality (USHORT aQuality)
{
    CHAR quality='B';
    return(quality);
}

CHAR CtiFDR_BEPC::YukonToForeignDST (bool aFlag)
{
    CHAR retVal='S';
    return retVal;
}

int CtiFDR_BEPC::readConfig( void )
{    
    int         successful = TRUE;
    RWCString   tempStr;
    bool        defaultID=true;

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

    tempStr = getCparmValueAsString(KEY_DRIVE_AND_PATH);
    if (tempStr.length() > 0)
    {
        setDriveAndPath(tempStr);
    }
    else
    {
        setDriveAndPath(RWCString ("\\yukon\\server\\Export"));
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


    tempStr = getCparmValueAsString(KEY_COOP_ID);
    if (tempStr.length() > 0)
    {
        defaultID=false;
        setCoopID(tempStr);
    }
    else
    {
        setCoopID(RWCString ("CTI"));
    }
    setFileName (RWCString(getCoopID())+RWCString("_total.txt"));


    // we build this to coopid_total.  If different, we can change here
    tempStr = getCparmValueAsString(KEY_FILENAME);
    if (tempStr.length() > 0)
    {
        setFileName(tempStr);
    }

    setAppendToFile(false);
    tempStr = getCparmValueAsString(KEY_APPEND_FILE);
    if (tempStr.length() > 0)
    {
        if (!tempStr.compareTo ("true",RWCString::ignoreCase))
        {
            setAppendToFile(true);
        }
    }

    if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
    {
        if (defaultID)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << "---------------------------------------------" << endl;
            dout << RWTime() << "---------------------------------------------" << endl;
            dout << RWTime() << "---------------------------------------------" << endl;
            dout << RWTime() << "---------------------------------------------" << endl;
            dout << RWTime() << endl << endl << "Coop ID has not been defined !!!" << endl << endl;
            dout << RWTime() << "---------------------------------------------" << endl;
            dout << RWTime() << "---------------------------------------------" << endl;
            dout << RWTime() << "---------------------------------------------" << endl;

        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " BEPC Coop ID " << getCoopID() << endl;
            dout << RWTime() << " BEPC file name " << getFileName() << endl;
            dout << RWTime() << " BEPC directory " << getDriveAndPath() << endl;
            dout << RWTime() << " BEPC interval " << getInterval() << endl;
            dout << RWTime() << " BEPC dispatch queue flush rate " << getQueueFlushRate() << endl;
            dout << RWTime() << " BPEC db reload rate " << getReloadRate() << endl;

            if (shouldAppendToFile())
                dout << RWTime() << " Export will append to existing" << endl;
            else
                dout << RWTime() << " Export will overwrite existing file" << endl;
        }
    }



    return successful;
}


/************************************************************************
* Function Name: CtiFDRTextFileBase::loadTranslationLists()
*
* Description: Creates a collection of points and their translations for the 
*				specified direction
* 
*************************************************************************
*/
bool CtiFDR_BEPC::loadTranslationLists()
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
                                                       RWCString (FDR_INTERFACE_SEND));

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
                                translationPoint->getDestinationList()[x].setTranslation (tempString2);
                                successful = true;
                            }
                        }   // first token invalid
                    }
                }   // end for interator

                {
                    // lock the send list and remove the old one
                    CtiLockGuard<CtiMutex> sendGuard(getSendToList().getMutex());  
                    if (getSendToList().getPointList() != NULL)
                    {
                        getSendToList().deletePointList();
                    }
                    getSendToList().setPointList (pointList);
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

bool CtiFDR_BEPC::sendMessageToForeignSys ( CtiMessage *aMessage )
{   
    bool retVal = true;
    CtiPointDataMsg     *localMsg = (CtiPointDataMsg *)aMessage;
    CtiFDRPoint point;

    // need to update this in my list always
    updatePointByIdInList (getSendToList(), localMsg);

    return retVal;
}

/**************************************************************************
* Function Name: CtiFDRT_BEPC::threadFunctionWriteToFile (void )
*
* Description: thread that waits and then grabs the file for processing
* 
***************************************************************************
*/
void CtiFDR_BEPC::threadFunctionWriteToFile( void )
{
    RWRunnableSelf  pSelf = rwRunnable( );
    INT retVal=0,tries=0;
    RWTime         timeNow;
    RWTime         refreshTime(rwEpoch);
    RWCString action,desc;
    CHAR fileName[200];
    FILE* fptr;
    char workBuffer[500];  // not real sure how long each line possibly is
    CtiFDRPoint        translationPoint;

    try
    {
        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Initializing CtiFDR_BEPC::threadFunctionWriteToFile " << endl;
        }

        // first output is 15 seconds after startup
        refreshTime = RWTime() + 15;

        for ( ; ; )
        {
            pSelf.serviceCancellation( );
            pSelf.sleep (1000);

            timeNow = RWTime();

            // now is the time to write the file
            if (timeNow >= refreshTime)
            {
//                {
//                    CtiLockGuard<CtiLogger> doubt_guard(dout);
//                    dout << RWTime() << " " << __FILE__ << " (" << __LINE__ << " **** Checkpoint **** dumping file" << endl;
//                }

                _snprintf (fileName, 200, "%s\\%s",getDriveAndPath(),getFileName());

                // check if we need to append
                if (shouldAppendToFile())
                {
                    fptr = fopen( fileName, "a");
                }
                else
                {
                    fptr = fopen( fileName, "w");
                }

                if ( fptr == NULL )
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " " << getInterfaceName() << "'s file " << RWCString (fileName) << " could not be opened" << endl;
                }
                else
                {
                    bool flag = false;

                    {
                        CtiLockGuard<CtiMutex> sendGuard(getSendToList().getMutex());  
                        flag = findTranslationNameInList (RWCString (KEY_TOTAL_LOAD_KW), getSendToList(), translationPoint);
                    }

                    if (flag)
                    {
                        // if data is older than 2001, it can't be valid
                        if (translationPoint.getLastTimeStamp() < RWTime(RWDate(1,1,2001)))
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " PointId " << translationPoint.getPointID();
                                dout << " was not exported to  " << RWCString (fileName) << " because the timestamp (" << translationPoint.getLastTimeStamp() << ") was out of range " << endl;
                            }
                        }
                        else
                        {   
                            // value is expected to be an integer so cast the float
                            _snprintf (workBuffer,500,"%s,%s,%d,0,0,0\n",
                                       getCoopID(),
                                       YukonToForeignTime(translationPoint.getLastTimeStamp()),
                                       (int)translationPoint.getValue());

                            if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " Exporting pointid " << translationPoint.getDestinationList()[0].getTranslation() ;
                                dout << " value " << (int)translationPoint.getValue() << " to file " << RWCString(fileName) << endl;
                            }
                            fprintf (fptr,workBuffer);
                        }
                    }
                    fclose(fptr);
                }
                refreshTime = RWTime() - (RWTime::now().seconds() % getInterval()) + getInterval();
            }
        }
    }

    catch ( RWCancellation &cancellationMsg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "CANCELLATION CtiFDRBEPCBase::threadFunctionWriteToFile in interface " <<getInterfaceName()<< endl;
    }

    // try and catch the thread death
    catch ( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Fatal Error:  CtiFDRBEPCBase::threadFunctionWriteToFile  " << getInterfaceName() << " is dead! " << endl;
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
        textExportInterface = new CtiFDR_BEPC();

        // now start it up
        return textExportInterface->run();
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

        textExportInterface->stop();
        delete textExportInterface;
        textExportInterface = 0;

        return 0;
    }


#ifdef __cplusplus
}
#endif




