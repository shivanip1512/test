#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*-----------------------------------------------------------------------------*
*
*    FILE NAME: fdrtextexport.cpp
*
*    DATE: 04/11/2003
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrtextexport.cpp-arc  $
*    REVISION     :  $Revision: 1.4 $
*    DATE         :  $Date: 2004/09/24 14:36:53 $
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
      $Log: fdrtextexport.cpp,v $
      Revision 1.4  2004/09/24 14:36:53  eschmit
      Added Boost includes and libraries, misc fixes for ptime support

      Revision 1.3  2003/10/20 20:28:17  dsutton
      Bug:  The application wasn't locking the point list down before looking for a
      point.  The database reload does lock the list down before reloading.  If the
      lookup and the reload happened at the same time,  the lookup lost and threw
      and exception

      Revision 1.2  2003/04/22 20:44:44  dsutton
      Interfaces FDRTextExport and FDRTextImport and all the pieces needed
      to make them compile and work

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
#include "fdrtextexport.h"


CtiFDR_TextExport * textExportInterface;

const CHAR * CtiFDR_TextExport::KEY_INTERVAL = "FDR_TEXTEXPORT_INTERVAL";
const CHAR * CtiFDR_TextExport::KEY_FILENAME = "FDR_TEXTEXPORT_FILENAME";
const CHAR * CtiFDR_TextExport::KEY_DRIVE_AND_PATH = "FDR_TEXTEXPORT_DRIVE_AND_PATH";
const CHAR * CtiFDR_TextExport::KEY_DB_RELOAD_RATE = "FDR_TEXTEXPORT_DB_RELOAD_RATE";
const CHAR * CtiFDR_TextExport::KEY_QUEUE_FLUSH_RATE = "FDR_TEXTEXPORT_QUEUE_FLUSH_RATE";
const CHAR * CtiFDR_TextExport::KEY_APPEND_FILE = "FDR_TEXTEXPORT_APPEND_FILE";


// Constructors, Destructor, and Operators
CtiFDR_TextExport::CtiFDR_TextExport()
: CtiFDRTextFileBase(RWCString("TEXTEXPORT"))
{  
    // init these lists so they have something
    CtiFDRManager   *recList = new CtiFDRManager(getInterfaceName(),RWCString(FDR_INTERFACE_SEND)); 
    getSendToList().setPointList (recList);
    recList = NULL;
    init();

}

CtiFDR_TextExport::~CtiFDR_TextExport()
{
}


bool CtiFDR_TextExport::shouldAppendToFile() const
{
    return _appendFlag;
}

CtiFDR_TextExport &CtiFDR_TextExport::setAppendToFile (bool aFlag)
{
    _appendFlag = aFlag;
    return *this;
}

BOOL CtiFDR_TextExport::init( void )
{
    // init the base class
    Inherited::init();    
    _threadWriteToFile = rwMakeThreadFunction(*this, 
                                               &CtiFDR_TextExport::threadFunctionWriteToFile);

    if (!readConfig( ))
    {
        return FALSE;
    }

    loadTranslationLists();
    return TRUE;
}
/*************************************************
* Function Name: CtiFDR_TextExport::run()
*
* Description: runs the interface
* 
**************************************************
*/
BOOL CtiFDR_TextExport::run( void )
{
    // crank up the base class
    Inherited::run();

    // startup our interfaces
    _threadWriteToFile.start();

    return TRUE;
}


/*************************************************
* Function Name: CtiFDR_TextExport::stop()
*
* Description: stops all threads 
* 
**************************************************
*/
BOOL CtiFDR_TextExport::stop( void )
{
    _threadWriteToFile.requestCancellation();
    Inherited::stop();
    return TRUE;
}


RWCString CtiFDR_TextExport::YukonToForeignTime (RWTime aTime)
{
    CHAR workBuffer[50];
    RWDate date(aTime);
    RWCString retVal;

    if (aTime.isValid())
    {
        _snprintf (workBuffer,
                       50,
                    "%02ld/%02ld/%04ld %02ld:%02ld:%02ld",
                       date.month(),
                       date.dayOfMonth(),
                       date.year(),
                       aTime.hour(),
                       aTime.minute(),
                       aTime.second());

        retVal = RWCString (workBuffer);
    }
    else
    {
        retVal = RWCString ("01/01/1990 00:00:00");
    }
    return retVal;
}

CHAR CtiFDR_TextExport::YukonToForeignQuality (USHORT aQuality)
{
    CHAR quality='B';

    if (aQuality == NormalQuality)
        quality = 'G';
    if (aQuality == NonUpdatedQuality)
        quality = 'B';
    if (aQuality == ManualQuality)
        quality = 'M';

	return(quality);
}

CHAR CtiFDR_TextExport::YukonToForeignDST (bool aFlag)
{
    CHAR retVal='S';

    if (aFlag)
    {
        retVal='D';
    }
    return retVal;
}

int CtiFDR_TextExport::readConfig( void )
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
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Text Export file name " << getFileName() << endl;
        dout << RWTime() << " Text Export directory " << getDriveAndPath() << endl;
        dout << RWTime() << " Text Export interval " << getInterval() << endl;
        dout << RWTime() << " Text Export dispatch queue flush rate " << getQueueFlushRate() << endl;
        dout << RWTime() << " Text Export db reload rate " << getReloadRate() << endl;

        if (shouldAppendToFile())
            dout << RWTime() << " Export will append to existing" << endl;
        else
            dout << RWTime() << " Export will overwrite existing file" << endl;

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
bool CtiFDR_TextExport::loadTranslationLists()
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

bool CtiFDR_TextExport::sendMessageToForeignSys ( CtiMessage *aMessage )
{   
    bool retVal = true;
    CtiPointDataMsg     *localMsg = (CtiPointDataMsg *)aMessage;
    CtiFDRPoint point;

    // need to update this in my list always
    updatePointByIdInList (getSendToList(), localMsg);

    return retVal;
}
#if 0
bool CtiFDRSocketInterface::sendMessageToForeignSys ( CtiMessage *aMessage )
{   
    bool retVal = true;
    CtiPointDataMsg     *localMsg = (CtiPointDataMsg *)aMessage;
    CtiFDRPoint point;

    // need to update this in my list always
    updatePointByIdInList (getSendToList(), localMsg);

    // if this is a response to a registration, do nothing
    if (localMsg->getTags() & TAG_POINT_MOA_REPORT)
    {
        findPointIdInList (localMsg->getId(), getSendToList(), point);

        if (getDebugLevel () & STARTUP_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " MOA registration tag set, point " << localMsg->getId() << " will not be sent to " << getInterfaceName() << endl;
        }
        retVal = false;
    }
    else
    {
        // see if the point exists;
        retVal = findPointIdInList (localMsg->getId(), getSendToList(), point);

        if (retVal == false)
        {
            if (getDebugLevel () & STARTUP_FDR_DEBUGLEVEL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Translation for point " << localMsg->getId() << " to " << getInterfaceName() << " not found " << endl;;
            }
        }
        else
        {
            /*******************************
            * if the timestamp is less than 01-01-2000 (completely arbitrary number)
            * then dont' route the point because it is uninitialized
            * note: uninitialized points come across as 11-10-1990 
            ********************************
            */
            if (point.getLastTimeStamp() < RWTime(RWDate(1,1,2001)))
            {
                if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " PointId " << point.getPointID();
                    dout << " was not sent to " << getInterfaceName() << " because it hasn't been initialized " << endl;
                }
                retVal = false;
            }
            else
            {
                // if we haven't registered, don't bother
                if (isRegistered())
                {
                    try 
                    {
                        retVal = buildAndWriteToForeignSystem (point);
                    }
                    catch (...)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " " << __FILE__ << " (" << __LINE__ << " **** Checkpoint **** building msg error" << endl;
                    }
                }
            }
        }
    }
   return retVal;
}
#endif
/**************************************************************************
* Function Name: CtiFDRT_TextExport::threadFunctionWriteToFile (void )
*
* Description: thread that waits and then grabs the file for processing
* 
***************************************************************************
*/
void CtiFDR_TextExport::threadFunctionWriteToFile( void )
{
    RWRunnableSelf  pSelf = rwRunnable( );
    INT retVal=0,tries=0;
    RWTime         timeNow;
    RWTime         refreshTime(rwEpoch);
    RWCString action,desc;
    CHAR fileName[200];
    FILE* fptr;
    char workBuffer[500];  // not real sure how long each line possibly is
    CtiFDRPoint *       translationPoint = NULL;

    try
    {
        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " Initializing CtiFDR_TextExport::threadFunctionWriteToFile " << endl;
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
                    CtiLockGuard<CtiMutex> sendGuard(getSendToList().getMutex());  
                    CtiFDRManager::CTIFdrPointIterator  myIterator(getSendToList().getPointList()->getMap());

                    for ( ; myIterator(); )
                    {
                        translationPoint = myIterator.value();

                        for (int x=0; x < translationPoint->getDestinationList().size(); x++)
                        {
                            if (translationPoint->getPointType() == StatusPointType)
                            {
                                _snprintf (workBuffer,500,"1,%s,%d,%c,%s,%c\n",
                                           translationPoint->getDestinationList()[x].getTranslation(),
                                           (int)translationPoint->getValue(),
                                           YukonToForeignQuality(translationPoint->getQuality()),
                                           YukonToForeignTime(translationPoint->getLastTimeStamp()),
                                           YukonToForeignDST (translationPoint->getLastTimeStamp().isDST())
                                           );

                                if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " Exporting pointid " << translationPoint->getDestinationList()[x].getTranslation() ;
                                    dout << " value " << (int)translationPoint->getValue() << " to file " << RWCString(fileName) << endl;
                                }
                            }
                            else
                            {
                                _snprintf (workBuffer,500,"1,%s,%f,%c,%s,%c\n",
                                           translationPoint->getDestinationList()[x].getTranslation(),
                                           translationPoint->getValue(),
                                           YukonToForeignQuality(translationPoint->getQuality()),
                                           YukonToForeignTime(translationPoint->getLastTimeStamp()),
                                           YukonToForeignDST (translationPoint->getLastTimeStamp().isDST())
                                           );
                                if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << RWTime() << " Exporting pointid " << translationPoint->getDestinationList()[x].getTranslation() ;
                                    dout << " value " << translationPoint->getValue() << " to file " << RWCString(fileName) << endl;
                                }
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
        dout << "CANCELLATION CtiFDRTextExportBase::threadFunctionWriteToFile in interface " <<getInterfaceName()<< endl;
    }

    // try and catch the thread death
    catch ( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Fatal Error:  CtiFDRTextExportBase::threadFunctionWriteToFile  " << getInterfaceName() << " is dead! " << endl;
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
        textExportInterface = new CtiFDR_TextExport();

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



