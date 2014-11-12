#include "precompiled.h"
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

using std::string;
using std::endl;

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

bool CtiFDR_TextExport::readConfig()
{
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
        if (ciStringEqual(tempStr,"true"))
        {
            setAppendToFile(true);
        }
    }
    tempStr = getCparmValueAsString(KEY_FORMAT);
    _format = formatOne;
    if (tempStr.length() > 0)
    {
        if (ciStringEqual(tempStr,"survalent"))
        {
            _format = survalent;
        }
    }

    if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
    {
        Cti::FormattedList loglist;

        loglist.add("Text Export file name")                 << getFileName();
        loglist.add("Text Export directory")                 << getDriveAndPath();
        loglist.add("Text Export interval")                  << getInterval();
        loglist.add("Text Export dispatch queue flush rate") << getQueueFlushRate();
        loglist.add("Text Export db reload rate")            << getReloadRate();

        if (_format == survalent)
            loglist <<"Text Export format set to Survalent";
        else
            loglist <<"Text Export format set to default";

        if (shouldAppendToFile())
            loglist <<"Export will append to existing";
        else
            loglist <<"Export will overwrite existing file";

        CTILOG_DEBUG(dout, loglist)
    }

    return true;
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
                            CTILOG_DEBUG(dout, "No points defined for use by interface " << getInterfaceName());
                        }
                    }
                    else
                    {
                        CTILOG_ERROR(dout, "Failed to translate points for interface "<< getInterfaceName());
                    }
                }
                setLinkStatusID(getClientLinkStatusID (getInterfaceName()));
            }
            else
            {
                CTILOG_ERROR(dout, "Could not load receive points for "<< getInterfaceName() <<" : Empty data set returned");
                successful = false;
            }
        }
        else
        {
            CTILOG_ERROR(dout, "Unable to load points from database for "<< getInterfaceName());
            successful = false;
        }

    }   // end try block

    catch (const RWExternalErr& e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Failed to load translation lists for "<< getInterfaceName());
        RWTHROW(e);
    }

    // try and catch the thread death
    catch ( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "Failed to load translation lists for "<< getInterfaceName());
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
            CTILOG_DEBUG(dout, "Parsing Yukon Point ID "<< translationPoint->getPointID() <<
                    " translate: "<< translationPoint->getDestinationList()[x].getTranslation());
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
            CTILOG_DEBUG(dout, "Initializing Thread for interface "<< getInterfaceName());
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
                    CTILOG_ERROR(dout, getInterfaceName() <<"'s file " << fileName <<" could not be opened");
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
                                CTILOG_ERROR(dout, "PointId " << translationPoint->getPointID() <<
                                        " was not exported to  " << fileName <<" because the timestamp (" << translationPoint->getLastTimeStamp() << ") was out of range");
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
                                            CTILOG_DEBUG(dout, "Exporting pointid "<< translationPoint->getDestinationList()[x].getTranslation() <<
                                                    " value "<< (int)translationPoint->getValue() <<" to file "<< fileName);
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
                                            CTILOG_DEBUG(dout, "Exporting pointid "<< translationPoint->getDestinationList()[x].getTranslation() <<
                                                    " value "<< translationPoint->getValue() <<" to file "<< fileName);
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

    catch ( RWCancellation & )
    {
        CTILOG_INFO(dout, "Thread CANCELLATION for interface "<< getInterfaceName());
    }

    // try and catch the thread death
    catch ( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "Thread for interface"<< getInterfaceName() <<" is dead!");
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
                CTILOG_DEBUG(dout, "Exporting status pointid "<< aPoint->getDestinationList()[0].getTranslation() <<
                        " value "<< (int)aPoint->getValue() <<" to Survalent file");
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
                CTILOG_DEBUG(dout, "Exporting analog pointid "<< aPoint->getDestinationList()[0].getTranslation() <<
                        " value "<< aPoint->getValue() <<" to Survalent file");
            }
        }
        fprintf (aFilePtr,workBuffer);
    }
    catch ( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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



