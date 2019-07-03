#include "precompiled.h"
#include <wininet.h>

#include <string>
#include <fcntl.h>
#include <io.h>

/** include files **/
#include "ctitime.h"
#include "ctidate.h"

#include "cparms.h"
#include "msg_cmd.h"
#include "pointtypes.h"
#include "numstr.h"

#include "dllbase.h"
#include "logger.h"
#include "guard.h"
#include "fdrtextfilebase.h"
#include "fdrbepc.h"
#include "ctitokenizer.h"

#include "utility.h"

using std::string;
using std::endl;

CtiFDR_BEPC * textExportInterface;

const CHAR * CtiFDR_BEPC::KEY_INTERVAL = "FDR_BEPC_INTERVAL";
const CHAR * CtiFDR_BEPC::KEY_DRIVE_AND_PATH = "FDR_BEPC_DRIVE_AND_PATH";
const CHAR * CtiFDR_BEPC::KEY_DB_RELOAD_RATE = "FDR_BEPC_DB_RELOAD_RATE";
const CHAR * CtiFDR_BEPC::KEY_QUEUE_FLUSH_RATE = "FDR_BEPC_QUEUE_FLUSH_RATE";
const CHAR * CtiFDR_BEPC::KEY_APPEND_FILE = "FDR_BEPC_APPEND_FILE";

#pragma warning(push)
#pragma warning(disable:4355) // disable the warning generated by worker thread using "this" in constructor initializer List

// Constructors, Destructor, and Operators
CtiFDR_BEPC::CtiFDR_BEPC()
:   CtiFDRTextFileBase(string("BEPC")),
    _appendFlag(false),
    _threadWriteToFile(Cti::WorkerThread::Function(&CtiFDR_BEPC::threadFunctionWriteToFile, this).name("writeToFile"))
{
    // init these lists so they have something
    CtiFDRManager   *recList = new CtiFDRManager(getInterfaceName(),string(FDR_INTERFACE_SEND));
    getSendToList().setPointList (recList);
    recList = NULL;
}

#pragma warning(pop)

CtiFDR_BEPC::~CtiFDR_BEPC()
{
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
    _threadWriteToFile.interrupt();
    _threadWriteToFile.tryJoinOrTerminateFor(Cti::Timing::Chrono::seconds(10));

    Inherited::stop();
    return TRUE;
}


string CtiFDR_BEPC::YukonToForeignTime (CtiTime aTime)
{
    string retVal = string ("200001010000");

    if (aTime.isValid())
    {
        char workBuffer[50];

        // get a gmtime from our time
        long longTime = aTime.seconds();

        time_t tPtr = longTime;
        struct tm *gmTime = CtiTime::gmtime_r(&tPtr);
        _snprintf (workBuffer,
                       50,
                   "%04ld%02ld%02ld%02ld%02ld",
                   gmTime->tm_year+1900,
                   gmTime->tm_mon+1,
                   gmTime->tm_mday,
                   gmTime->tm_hour,
                   gmTime->tm_min);

        retVal = string (workBuffer);
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

bool CtiFDR_BEPC::readConfig( void )
{
    string tempStr;

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
        setReloadRate (3600);
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
        if (ciStringEqual("true", tempStr) )
        {
            setAppendToFile(true);
        }
    }

    if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
    {
        Cti::FormattedList loglist;

        loglist.add("BEPC directory")                 << getDriveAndPath();
        loglist.add("BEPC interval")                  << getInterval();
        loglist.add("BEPC dispatch queue flush rate") << getQueueFlushRate();
        loglist.add("BPEC db reload rate")            << getReloadRate();

        if (shouldAppendToFile())
        {
            loglist <<"Export will append to existing file";
        }
        else
        {
            loglist <<"Export will overwrite existing file";
        }

        CTILOG_DEBUG(dout, loglist);
    }

    return true;
}


/************************************************************************
* Function Name: CtiFDRTextFileBase::loadTranslationLists()
*
* Description: Creates a collection of points and their translations for the
*                               specified direction
*
*************************************************************************
*/
bool CtiFDR_BEPC::loadTranslationLists()
{
    bool successful = false;
    bool foundPoint = false;
    bool loadPointStatus = false;

    try
    {
        // make a list with all received points
        CtiFDRManager   *pointList = new CtiFDRManager(getInterfaceName(),
                                                       string (FDR_INTERFACE_SEND));

        // keep the status
        loadPointStatus = pointList->loadPointList();

        // if status is ok, we were able to read the database at least
        if ( loadPointStatus )
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
                CtiFDRManager::coll_type pointMap = pointList->getMap();
                CtiFDRManager::spiterator myIterator = pointMap.begin();

                coopIdToFileName.clear();
                for ( ; myIterator != pointMap.end(); ++myIterator)
                {
                    foundPoint = true;
                    successful = translateSinglePoint(myIterator->second);
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
                        // means there was nothing in the list, wait until next db change or reload
                        successful = true;
                        if (getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
                        {
                            CTILOG_DEBUG(dout, "No points defined for use by interface "<< getInterfaceName());
                        }
                    }
                }
                setLinkStatusID(getClientLinkStatusID (getInterfaceName()));
            }
            else
            {
                CTILOG_ERROR(dout, "Could not load (Receive) points for "<< getInterfaceName() <<" : Empty data set returned");
                successful = false;
            }
        }
        else
        {
            CTILOG_ERROR(dout, "Unable to load points from database for "<< getInterfaceName());
            successful = false;
        }

    }
    // try and catch the thread death
    catch ( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "Failed to load translation lists for "<< getInterfaceName());
    }

    return successful;
}

bool CtiFDR_BEPC::translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList)
{
    bool successful = false;
    string fileName;
    string coopId;

    int size = translationPoint->getDestinationList().size();

    for ( int x = 0; x < size; x++)
    {
        if (getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
        {
            CTILOG_DEBUG(dout, "Parsing "<< x <<"/"<< size <<". Yukon Point ID "  << translationPoint->getPointID() <<
                    " translate: " << translationPoint->getDestinationList()[x].getTranslation());
        }

        coopId = translationPoint->getDestinationList()[x].getTranslationValue("Coop Id");
        if ( !coopId.empty() )
        {
            successful = true;


            fileName = translationPoint->getDestinationList()[x].getTranslationValue("Filename");
            if (!fileName.empty())
            {
                coopIdToFileName.insert(std::pair<string,string>(coopId,fileName));
                translationPoint->getDestinationList()[x].setTranslation (coopId.c_str());
            }
            else
            {
                successful = false;
            }
        }
        else
        {
            successful = false;
        }
    }

    return successful;
}

void CtiFDR_BEPC::cleanupTranslationPoint(CtiFDRPointSPtr &translationPoint, bool recvList)
{
    if (!recvList)
    {
        if (translationPoint.get() == NULL)
        {
            return;
        }

        int size = translationPoint->getDestinationList().size();
        for ( int i = 0 ; i < size; i++) {
            string coopId = translationPoint->getDestinationList()[i].getTranslation();
            if (coopId != "")
            {
                std::transform(coopId.begin(), coopId.end(), coopId.begin(), toupper);


                coopIdToFileName.erase(coopId);
            }
        }
    }
}

void CtiFDR_BEPC::sendMessageToForeignSys ( CtiMessage *aMessage )
{
    CtiPointDataMsg *localMsg = static_cast<CtiPointDataMsg *>(aMessage);

    // need to update this in my list always
    updatePointByIdInList (getSendToList(), localMsg);
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
    CtiTime timeNow;
    CtiTime refreshTime(PASTDATE);
    CtiFDRPoint translationPoint;

    string fileName;
    FILE* fptr;

    try
    {
        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
        {
            CTILOG_DEBUG(dout, "Initializing threadFunctionWriteToFile");
        }

        // first output is 15 seconds after startup
        refreshTime = CtiTime() + 15;

        for ( ; ; )
        {
            Cti::WorkerThread::sleepFor(Cti::Timing::Chrono::milliseconds(1000));

            timeNow = CtiTime();

            // now is the time to write the file
            if (timeNow >= refreshTime)
            {
                std::map<string,string>::iterator itr = coopIdToFileName.begin();

                for ( ; itr != coopIdToFileName.end(); itr++)
                {
                    string fName = (*itr).second;
                    string coopId = (*itr).first;

                    fileName = getDriveAndPath() + "\\" + fName;

                    if (shouldAppendToFile())
                    {
                        fptr = fopen( fileName.c_str(), "a");
                    }
                    else
                    {
                        fptr = fopen( fileName.c_str(), "w");
                    }

                    if ( fptr == NULL )
                    {
                        CTILOG_ERROR(dout, getInterfaceName() <<"'s file "<< fileName <<" could not be opened");
                    }
                    else
                    {
                        bool flag = false;

                        {
                            CtiLockGuard<CtiMutex> sendGuard(getSendToList().getMutex());
                            flag = findTranslationNameInList (coopId, getSendToList(), translationPoint);
                        }

                        if (flag)
                        {
                            // if data is older than 2001, it can't be valid
                            if (translationPoint.getLastTimeStamp() < CtiTime(CtiDate(1,1,2001)))
                            {
                                CTILOG_ERROR(dout, "PointId "<< translationPoint.getPointID() <<" was not exported to "<< fileName <<
                                        " because the timestamp ("<< translationPoint.getLastTimeStamp() <<") is out of range");
                            }
                            else
                            {
                                // value is expected to be an integer so cast the float

                                string time = YukonToForeignTime(translationPoint.getLastTimeStamp());
                                //Intentional Casting of float to int
                                string value = CtiNumStr((int)translationPoint.getValue()).toString();
                                string workBuffer = coopId + "," + time  + "," + value + ",0,0,0\n";

                                if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                                {
                                    CTILOG_DEBUG(dout, "Exporting pointid "<< translationPoint.getDestinationList()[0].getTranslation() <<
                                            " value "<< (int)translationPoint.getValue() <<" to file "<< fileName);
                                }

                                fprintf (fptr,workBuffer.c_str());
                            }
                        }
                        fclose(fptr);
                    }
                }
                refreshTime = CtiTime() - (CtiTime::now().seconds() % getInterval()) + getInterval();
            }
        }
    }

    catch ( Cti::WorkerThread::Interrupted & )
    {
        CTILOG_INFO(dout, "CANCELLATION of threadFunctionWriteToFile in interface "<< getInterfaceName());
    }

    // try and catch the thread death
    catch ( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "threadFunctionWriteToFile in interface "<< getInterfaceName() <<" is dead!");
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
        textExportInterface->init();

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




