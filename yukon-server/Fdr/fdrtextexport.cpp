#include "yukon.h"
#include <wininet.h>
#include <fcntl.h>
#include <io.h>

/** include files **/
#include "ctitime.h"
#include "ctidate.h"

#include "cparms.h"
#include "msg_cmd.h"
#include "pointtypes.h"

#include "dllbase.h"
#include "logger.h"
#include "guard.h"
#include "fdrtextfilebase.h"
#include "fdrtextexport.h"
#include "utility.h"

CtiFDR_TextExport * textExportInterface;

const CHAR * CtiFDR_TextExport::KEY_INTERVAL = "FDR_TEXTEXPORT_INTERVAL";
const CHAR * CtiFDR_TextExport::KEY_FILENAME = "FDR_TEXTEXPORT_FILENAME";
const CHAR * CtiFDR_TextExport::KEY_DRIVE_AND_PATH = "FDR_TEXTEXPORT_DRIVE_AND_PATH";
const CHAR * CtiFDR_TextExport::KEY_DB_RELOAD_RATE = "FDR_TEXTEXPORT_DB_RELOAD_RATE";
const CHAR * CtiFDR_TextExport::KEY_QUEUE_FLUSH_RATE = "FDR_TEXTEXPORT_QUEUE_FLUSH_RATE";
const CHAR * CtiFDR_TextExport::KEY_APPEND_FILE = "FDR_TEXTEXPORT_APPEND_FILE";
const CHAR * CtiFDR_TextExport::KEY_FORMAT = "FDR_TEXTEXPORT_FORMAT";

// Constructors, Destructor, and Operators
CtiFDR_TextExport::CtiFDR_TextExport()
: CtiFDRTextFileBase(string("TEXTEXPORT"))
{
    // init these lists so they have something
    CtiFDRManager   *recList = new CtiFDRManager(getInterfaceName(),string(FDR_INTERFACE_SEND));
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


string CtiFDR_TextExport::YukonToForeignTime (CtiTime aTime)
{
    CHAR workBuffer[50];
    CtiDate aDate(aTime);
    string retVal;

    if (aTime.isValid())
    {
        _snprintf (workBuffer,
                       50,
                    "%02ld/%02ld/%04ld %02ld:%02ld:%02ld",
                       aDate.month(),
                       aDate.dayOfMonth(),
                       aDate.year(),
                       aTime.hour(),
                       aTime.minute(),
                       aTime.second());

        retVal = string (workBuffer);
    }
    else
    {
        retVal = string ("01/01/1990 00:00:00");
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
    string   tempStr;

    tempStr = getCparmValueAsString(KEY_INTERVAL);
    if (tempStr.length() > 0)
    {
        if (atoi (tempStr.c_str()) <=1)
        {
            setInterval(1);
        }
        else
        {
            setInterval(atoi(tempStr.c_str()));
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
        setFileName(string ("yukon.txt"));
    }

    tempStr = getCparmValueAsString(KEY_DRIVE_AND_PATH);
    if (tempStr.length() > 0)
    {
        setDriveAndPath(tempStr);
    }
    else
    {
        setDriveAndPath(string ("\\yukon\\server\\Export"));
    }

    tempStr = getCparmValueAsString(KEY_DB_RELOAD_RATE);
    if (tempStr.length() > 0)
    {
        setReloadRate (atoi(tempStr.c_str()));
    }
    else
    {
        setReloadRate (86400);
    }

    tempStr = getCparmValueAsString(KEY_QUEUE_FLUSH_RATE);
    if (tempStr.length() > 0)
    {
        setQueueFlushRate (atoi(tempStr.c_str()));
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
        if (string_equal(tempStr,"true"))
        {
            setAppendToFile(true);
        }
    }
    tempStr = getCparmValueAsString(KEY_FORMAT);
    _format = formatOne;
    if (tempStr.length() > 0)
    {
        if (string_equal(tempStr,"survalent"))
        {
            _format = survalent;
        }
    }


    if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);

        dout << CtiTime() << " Text Export file name " << getFileName() << endl;
        dout << CtiTime() << " Text Export directory " << getDriveAndPath() << endl;
        dout << CtiTime() << " Text Export interval " << getInterval() << endl;
        dout << CtiTime() << " Text Export dispatch queue flush rate " << getQueueFlushRate() << endl;
        dout << CtiTime() << " Text Export db reload rate " << getReloadRate() << endl;
        if (_format == survalent)
        {
            dout << CtiTime() << " Text Export format set to Survalent" << endl;
        }
        else
        {
            dout << CtiTime() << " Text Export format set to default" << endl;
        }

        if (shouldAppendToFile())
            dout << CtiTime() << " Export will append to existing" << endl;
        else
            dout << CtiTime() << " Export will overwrite existing file" << endl;

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
bool CtiFDR_TextExport::loadTranslationLists()
{
    bool successful = true;
    bool foundPoint = false;

    try
    {
        // make a list with all received points
        CtiFDRManager   *pointList = new CtiFDRManager(getInterfaceName(),
                                                       string (FDR_INTERFACE_SEND));

        // if status is ok, we were able to read the database at least
        if ( pointList->loadPointList())
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
                CtiFDRManager::spiterator  myIterator = pointList->getMap().begin();

                for ( ; myIterator != pointList->getMap().end(); ++myIterator )
                {
                    foundPoint = true;
                    if (!translateSinglePoint(myIterator->second)) {
                        successful = false;
                    }
                }

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
                        //Means there was nothing in the list, wait until next db change or reload
                        //Setting true so we do not reload in 60 seconds.
                        successful = true;
                        if (getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " No points defined for use by interface " << getInterfaceName() << endl;
                        }
                    }
                    else
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Error in translating points " << getInterfaceName() << endl;
                        }
                    }
                }
                setLinkStatusID(getClientLinkStatusID (getInterfaceName()));
            }
            else
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Error loading (Receive) points for " << getInterfaceName() << " : Empty data set returned " << endl;
                successful = false;
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
            successful = false;
        }

    }   // end try block

    catch (RWExternalErr e )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Error loading translation lists for " << getInterfaceName() << endl;
        RWTHROW(e);
    }

    // try and catch the thread death
    catch ( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Error loading translation lists for " << getInterfaceName() << endl;
    }

    return successful;
}

bool CtiFDR_TextExport::translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList)
{
    bool successful = false;
    string tempString2;

    for (int x = 0; x < translationPoint->getDestinationList().size(); x++)
    {
        if (getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Parsing Yukon Point ID " << translationPoint->getPointID();
            //dout << " translate: " << translationPoint->getDestinationList()[x].getTranslation() << endl;
            dout << " translate: " << translationPoint->getDestinationList()[x].getTranslation() << endl;
        }

        tempString2 = translationPoint->getDestinationList()[x].getTranslationValue("Point ID");
        // now we have a point id
        if ( !tempString2.empty() )
        {
            translationPoint->getDestinationList()[x].setTranslation (tempString2);
            successful = true;
        }

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
    CtiTime         timeNow;
    CtiTime         refreshTime(PASTDATE);
    string action,desc;
    CHAR fileName[200];
    FILE* fptr;
    char workBuffer[500];  // not real sure how long each line possibly is
    CtiFDRPointSPtr translationPoint;
    CtiTime lastWrite(0UL);

    try
    {
        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Initializing CtiFDR_TextExport::threadFunctionWriteToFile " << endl;
        }

        // first output is 15 seconds after startup
        refreshTime = CtiTime() + 15;

        for ( ; ; )
        {
            pSelf.serviceCancellation( );
            pSelf.sleep (1000);

            timeNow = CtiTime();

            // now is the time to write the file
            if (timeNow >= refreshTime)
            {
//                {
//                    CtiLockGuard<CtiLogger> doubt_guard(dout);
//                    dout << CtiTime() << " " << __FILE__ << " (" << __LINE__ << " **** Checkpoint **** dumping file" << endl;
//                }

                _snprintf (fileName, 200, "%s\\%s",getDriveAndPath().c_str(),getFileName().c_str());

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
                    dout << CtiTime() << " " << getInterfaceName() << "'s file " << string (fileName) << " could not be opened" << endl;
                }
                else
                {
                    CtiLockGuard<CtiMutex> sendGuard(getSendToList().getMutex());
                    CtiFDRManager* mgrPtr = getSendToList().getPointList();
                    CtiFDRManager::readerLock guard(mgrPtr->getLock());

                    CtiFDRManager::spiterator  myIterator = mgrPtr->getMap().begin();

                    bool firstSurvalentPass=true;


                    for ( ; myIterator != mgrPtr->getMap().end(); ++myIterator)
                    {
                        translationPoint = (*myIterator).second;

                        /*****************************************
                        * This is a hack for a proof of concept with the survalent scada system DLS
                        ******************************************
                        */
                        if (_format == survalent)
                        {
                            if (firstSurvalentPass)
                            {
                                struct tm tmbuf;
                                refreshTime.extract(&tmbuf);
                                char timeBuffer[200];
                                char timeWorker[50];

                                /*****************************************
                                * couldn't get this to give me a timezone abbreviation so it was
                                * built manually (according to microsoft there is a registery setting that
                                * governs this, go figure DLS
                                ******************************************
                                */
                                strftime (timeBuffer,200,"%a %b %d %H:%M:%S",&tmbuf);

                                strftime (timeWorker,50,"%Z",&tmbuf);
                                if (timeWorker[0]=='E')
                                {
                                    strcat (timeBuffer," EST");
                                }
                                else if (timeWorker[0]=='C')
                                {
                                    strcat (timeBuffer," CST");
                                }
                                else if (timeWorker[0]=='M')
                                {
                                    strcat (timeBuffer," MST");
                                }
                                else
                                {
                                    strcat (timeBuffer," PST");
                                }
                                // set daylight flag as expected
                                if (tmbuf.tm_isdst)
                                {
                                    timeBuffer[strlen(timeBuffer)-1] = 'D';
                                }

                                // grab the year
                                strftime (timeWorker,50,"%Y",&tmbuf);

                                fprintf (fptr,"%s %s\n",timeBuffer,timeWorker);
                                fprintf (fptr,"TAG_NAME                              OUTPUT       VALID\n");
                                fprintf (fptr,"=========================================================\n");
                                firstSurvalentPass = false;
                            }

                            processPointToSurvalent (fptr,translationPoint,lastWrite);
                        }
                        else
                        {
                            // if data is older than 2001, it can't be valid
                            if (translationPoint->getLastTimeStamp() < CtiTime(CtiDate(1,1,2001)))
                            {
                                //if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " PointId " << translationPoint->getPointID();
                                    dout << " was not exported to  " << string(fileName) << " because the timestamp (" << translationPoint->getLastTimeStamp() << ") was out of range " << endl;
                                }
                            }
                            else
                            {
                                for (int x=0; x < translationPoint->getDestinationList().size(); x++)
                                {
                                    if (translationPoint->getPointType() == StatusPointType)
                                    {
                                        _snprintf (workBuffer,500,"1,%s,%d,%c,%s,%c\n",
                                                   translationPoint->getDestinationList()[x].getTranslation().c_str(),
                                                   (int)translationPoint->getValue(),
                                                   YukonToForeignQuality(translationPoint->getQuality()),
                                                   YukonToForeignTime(translationPoint->getLastTimeStamp()).c_str(),
                                                   YukonToForeignDST (translationPoint->getLastTimeStamp().isDST())
                                                   );

                                        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << CtiTime() << " Exporting pointid " << translationPoint->getDestinationList()[x].getTranslation() ;
                                            dout << " value " << (int)translationPoint->getValue() << " to file " << string(fileName) << endl;
                                        }
                                    }
                                    else
                                    {
                                        _snprintf (workBuffer,500,"1,%s,%f,%c,%s,%c\n",
                                                   translationPoint->getDestinationList()[x].getTranslation().c_str(),
                                                   translationPoint->getValue(),
                                                   YukonToForeignQuality(translationPoint->getQuality()),
                                                   YukonToForeignTime(translationPoint->getLastTimeStamp()).c_str(),
                                                   YukonToForeignDST (translationPoint->getLastTimeStamp().isDST())
                                                   );
                                        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << CtiTime() << " Exporting pointid " << translationPoint->getDestinationList()[x].getTranslation() ;
                                            dout << " value " << translationPoint->getValue() << " to file " << string(fileName) << endl;
                                        }
                                    }
                                    fprintf (fptr,workBuffer);
                                }
                            }
                        }
                    }
                    fclose(fptr);
                    lastWrite = CtiTime::now();
                }
                refreshTime = CtiTime() - (CtiTime::now().seconds() % getInterval()) + getInterval();
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
        dout << CtiTime() << " Fatal Error:  CtiFDRTextExportBase::threadFunctionWriteToFile  " << getInterfaceName() << " is dead! " << endl;
    }
}

/**************************************************************************
* Function Name: CtiFDRT_TextExport::processPointToSurvalent
*
* Description: This was added as a proof of concept for Trinity Valley.  Their survalent
*               SCADA system takes this file as input. It was assumed we'd build a proper
*               interface for this SCADA vendor if necessary
*
*   NOTE:  File must have the three heading lines as shown and then one line per point

Tue Oct  4 14:55:04 EDT 2005
TAG_NAME                         OUTPUT VALID
=============================================
LAU04B_KWH                        398.4 0
LAU04A_KWH                          360 0
...
***************************************************************************
*/
void CtiFDR_TextExport::processPointToSurvalent (FILE* aFilePtr, CtiFDRPointSPtr aPoint, CtiTime aLastWrite)
{
//    static CtiTime lastWrite;
    int quality;
    char workBuffer[1024];

    try
    {
        /*****************************************************
        * Survalent has two qualities
        *      0=Normal
        *      1=Not normal
        *
        * one special case to note is if we haven't received an update since the last time we
        * wrote the file, we need to mark the data as invalid
        ******************************************************
        */
        // if we haven't received an update since last write, point is invalid
        if ((aPoint->getQuality() == NormalQuality) &&
            (aLastWrite < aPoint->getLastTimeStamp()))
        {
            quality = 0;
        }
        else
        {
            quality = 1;
        }

        if (aPoint->getPointType() == StatusPointType)
        {
            _snprintf (workBuffer,1024,"%s     %d    %d\n",
                       aPoint->getDestinationList()[0].getTranslation().c_str(),
                       (int)aPoint->getValue(),
                       quality);

            if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Exporting status pointid " << aPoint->getDestinationList()[0].getTranslation() ;
                dout << " value " << (int)aPoint->getValue() << " to Survalent file" << endl;
            }
        }
        else
        {
            _snprintf (workBuffer,1024,"%s     %.1f    %d\n",
                       aPoint->getDestinationList()[0].getTranslation().c_str(),
                       aPoint->getValue(),
                       quality);
            if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Exporting analog pointid " << aPoint->getDestinationList()[0].getTranslation() ;
                dout << " value " << aPoint->getValue() << " to Survalent file " << endl;
            }
        }
        fprintf (aFilePtr,workBuffer);
    }
    catch ( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " CtiFDRTextExport::processPointToSurvalent() function has an un-caught exception " << endl;
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



