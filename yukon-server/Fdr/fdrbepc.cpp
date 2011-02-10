#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*-----------------------------------------------------------------------------*
*
*    FILE NAME: fdrbepc.cpp
*
*    DATE: 12/31/2004
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrbepc.cpp-arc  $
*    REVISION     :  $Revision: 1.10.2.1 $
*    DATE         :  $Date: 2008/11/13 17:23:46 $
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
      Revision 1.10.2.1  2008/11/13 17:23:46  jmarks
      YUK-5273 Upgrade Yukon tool chain to Visual Studio 2005/2008

      Responded to reviewer comments again.

      I eliminated excess references to windows.h .

      This still left over 100 references to it where "yukon.h" or "precompiled.h" was not obviously included.  Some other chaining of references could still be going on, and of course it is potentially possible that not all the files in the project that include windows.h actually need it - I didn't check for that.

      None-the-less, I than added the NOMINMAX define right before each place where windows.h is still included.
      Special note:  std::min<LONG>(TimeOut, 500); is still required for compilation.

      In this process I occasionally deleted a few empty lines, and when creating the define, also added some.

      This may not have affected every file in the project, but while mega-editing it certainly seemed like it did.

      Revision 1.10  2008/10/02 23:57:15  tspar
      YUK-5013 Full FDR reload should not happen with every point

      YUKRV-325  review changes

      Revision 1.9  2008/09/23 15:14:57  tspar
      YUK-5013 Full FDR reload should not happen with every point db change

      Review changes. Most notable is mgr_fdrpoint.cpp now encapsulates CtiSmartMap instead of extending from rtdb.

      Revision 1.8  2008/09/15 21:08:48  tspar
      YUK-5013 Full FDR reload should not happen with every point db change

      Changed interfaces to handle points on an individual basis so they can be added
      and removed by point id.

      Changed the fdr point manager to use smart pointers to help make this transition possible.

      Revision 1.7  2007/04/10 23:04:35  tspar
      Added some more protection against bad input when tokenizing.

      Revision 1.6  2006/06/07 22:34:04  tspar
      _snprintf  adding .c_str() to all strings. Not having this does not cause compiler errors, but does cause runtime errors. Also tweaks and fixes to FDR due to some differences in STL / RW

      Revision 1.5  2006/04/24 14:47:32  tspar
      RWreplace: replacing a few missed or new Rogue Wave elements

      Revision 1.4  2006/01/03 20:23:37  tspar
      Moved non RW string utilities from rwutil.h to utility.h

      Revision 1.3  2005/12/20 17:17:12  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

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

CtiFDR_BEPC * textExportInterface;

const CHAR * CtiFDR_BEPC::KEY_INTERVAL = "FDR_BEPC_INTERVAL";
const CHAR * CtiFDR_BEPC::KEY_DRIVE_AND_PATH = "FDR_BEPC_DRIVE_AND_PATH";
const CHAR * CtiFDR_BEPC::KEY_DB_RELOAD_RATE = "FDR_BEPC_DB_RELOAD_RATE";
const CHAR * CtiFDR_BEPC::KEY_QUEUE_FLUSH_RATE = "FDR_BEPC_QUEUE_FLUSH_RATE";
const CHAR * CtiFDR_BEPC::KEY_APPEND_FILE = "FDR_BEPC_APPEND_FILE";

// Constructors, Destructor, and Operators
CtiFDR_BEPC::CtiFDR_BEPC()
: CtiFDRTextFileBase(string("BEPC")),
  _appendFlag(false)
{
    // init these lists so they have something
    CtiFDRManager   *recList = new CtiFDRManager(getInterfaceName(),string(FDR_INTERFACE_SEND));
    getSendToList().setPointList (recList);
    recList = NULL;
}

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


string CtiFDR_BEPC::YukonToForeignTime (CtiTime aTime)
{
    CHAR workBuffer[50];
    string retVal;
    struct tm *gmTime = NULL;
    CtiTime newTime;

    if (aTime.isValid())
    {
        // get a gmtime from our time
        LONG longTime=aTime.seconds();
        gmTime=CtiTime::gmtime_r(reinterpret_cast<const time_t *>(&longTime));

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
    else
    {
        retVal = string ("200001010000");
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
    int successful = TRUE;
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
        if (!stringCompareIgnoreCase("true", tempStr) )
        {
            setAppendToFile(true);
        }
    }

    if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " BEPC directory " << getDriveAndPath() << endl;
        dout << CtiTime() << " BEPC interval " << getInterval() << endl;
        dout << CtiTime() << " BEPC dispatch queue flush rate " << getQueueFlushRate() << endl;
        dout << CtiTime() << " BPEC db reload rate " << getReloadRate() << endl;

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
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " No points defined for use by interface " << getInterfaceName() << endl;
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
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Parsing " << x << "/" << size << ". Yukon Point ID "  << translationPoint->getPointID();
            dout << " translate: " << translationPoint->getDestinationList()[x].getTranslation() << endl;
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

    int retVal = 0;
    int tries = 0;
    CtiTime timeNow;
    CtiTime refreshTime(PASTDATE);
    CtiFDRPoint translationPoint;

    string action,desc;
    string fileName;
    string workBuffer;

    FILE* fptr;

    try
    {
        if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Initializing CtiFDR_BEPC::threadFunctionWriteToFile " << endl;
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
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " " << getInterfaceName() << "'s file " << string (fileName) << " could not be opened" << endl;
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
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " PointId " << translationPoint.getPointID();
                                    dout << " was not exported to  " << string (fileName) << " because the timestamp (" << translationPoint.getLastTimeStamp() << ") was out of range " << endl;
                                }
                            }
                            else
                            {
                                // value is expected to be an integer so cast the float
                                workBuffer = coopId + "," +  YukonToForeignTime(translationPoint.getLastTimeStamp()) + "," + CtiNumStr((int)translationPoint.getValue()).toString() + ",0,0,0\n";

                                if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                                {
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << CtiTime() << " Exporting pointid " << translationPoint.getDestinationList()[0].getTranslation() ;
                                    dout << " value " << (int)translationPoint.getValue() << " to file " << string(fileName) << endl;
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

    catch ( RWCancellation &cancellationMsg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "CANCELLATION CtiFDRBEPCBase::threadFunctionWriteToFile in interface " <<getInterfaceName()<< endl;
    }

    // try and catch the thread death
    catch ( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Fatal Error:  CtiFDRBEPCBase::threadFunctionWriteToFile  " << getInterfaceName() << " is dead! " << endl;
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




