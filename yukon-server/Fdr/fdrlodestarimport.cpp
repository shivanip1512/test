/*-----------------------------------------------------------------------------*
*
*    FILE NAME: fdrlodestarimport.cpp
*
*    DATE: 05/22/2003
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrlodestarimport.cpp-arc  $
*    REVISION     :  $Revision: 1.36.2.1 $
*    DATE         :  $Date: 2008/11/13 17:23:47 $
*
*
*    AUTHOR: Josh Wolberg
*
*    PURPOSE:  LodeStar import
*
*    DESCRIPTION:
*
*    ---------------------------------------------------
*    History:
      $Log: fdrlodestarimport.cpp,v $
      Revision 1.36  2008/10/02 23:57:15  tspar
      YUK-5013 Full FDR reload should not happen with every point

      YUKRV-325  review changes

      Revision 1.35  2008/09/23 15:14:58  tspar
      YUK-5013 Full FDR reload should not happen with every point db change

      Review changes. Most notable is mgr_fdrpoint.cpp now encapsulates CtiSmartMap instead of extending from rtdb.

      Revision 1.34  2008/09/15 21:08:48  tspar
      YUK-5013 Full FDR reload should not happen with every point db change

      Changed interfaces to handle points on an individual basis so they can be added
      and removed by point id.

      Changed the fdr point manager to use smart pointers to help make this transition possible.

      Revision 1.33  2008/08/13 22:42:52  jrichter
      YUK-3163
      FDR doesn't log reason for failure to import LSE data

      Revision 1.32  2008/06/25 17:08:41  mfisher
      YUK-6109 utility.h contains excessive delete_<type> functions
      Consolidated duplicate functions

      Revision 1.31  2007/08/08 14:10:05  tspar
      YUK-4192

      This was limited to lodestar and textimport only.  Text time format was changed and these functions expected a certain format.
      Changed both to be more adaptable, also put the seconds back into the filename to keep names unique. In case import cycles are ever less than 60 seconds.

      Revision 1.30  2007/07/17 16:53:39  jrichter
      YUK-3163
      FDR doesn't log reason for failure to import LSE data

      Revision 1.29  2007/04/06 18:58:06  jrichter
      BUG ID: 923
      fdrlodestar enhance debug/send message to system log on corrupt file data

      Revision 1.28  2006/08/24 14:44:16  jrichter
      BUG FIX:
      -initialize *fptr = NULL, set attemptCounter = 0 on each full loop.

      Revision 1.27  2006/06/07 22:34:04  tspar
      _snprintf  adding .c_str() to all strings. Not having this does not cause compiler errors, but does cause runtime errors. Also tweaks and fixes to FDR due to some differences in STL / RW

      Revision 1.26  2006/06/06 22:12:29  tspar
      quick fix. _snprintf issues

      Revision 1.25  2006/06/06 19:16:22  tspar
      Tokenizer fixed

      Revision 1.24  2006/06/02 15:38:49  tspar
      Changes to the loadTranslationLists()

      Revision 1.23  2006/06/01 21:17:30  tspar
      Changes to the loadTranslationLists()

      Revision 1.22  2006/05/23 17:17:43  tspar
      bug fix: boost iterator used incorrectly in loop.

      Revision 1.21  2006/04/24 14:47:33  tspar
      RWreplace: replacing a few missed or new Rogue Wave elements

      Revision 1.20  2006/03/02 23:03:19  tspar
      Phase Three: Final  phase of RWTPtrSlist replacement.

      Revision 1.19  2006/02/17 17:04:31  tspar
      CtiMultiMsg:  replaced RWOrdered with vector<RWCollectable*> throughout the tree

      Revision 1.18  2006/01/03 20:23:37  tspar
      Moved non RW string utilities from rwutil.h to utility.h

      Revision 1.17  2005/12/20 17:17:13  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.16  2005/09/30 21:01:02  jrichter
      allows fileNames to have wildcards.  *.lse, *.ls, etc.

      Revision 1.15  2005/08/17 17:42:48  jrichter
      Merged  changes from 3.1.  handled massive point data with list of multimsg.  handled white space in data record for optional interval time field, handled massively long file format (extended workbuffer to 1500 bytes)

      Revision 1.14  2005/02/17 19:02:58  mfisher
      Removed space before CVS comment header, moved #include "yukon.h" after CVS header

      Revision 1.13  2005/02/10 23:23:51  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.12  2004/09/29 17:47:47  dsutton
      Updated all interfaces to default the db reload rate to once a day (86400)

      Revision 1.11  2004/09/24 14:36:52  eschmit
      Added Boost includes and libraries, misc fixes for ptime support

      Revision 1.10  2004/08/18 21:46:01  jrichter
      1.  Added try{} catch(..) blocks to threadReadFromFile function to try and pinpoint where thread was killed.
      2.  Cleared out fileInfoList to get a fresh list of files upon each loadTranslationList call (so files aren't read once the point they reference is deleted from database).
      3.  Added path/filename to translationName, so points located in duplicate files (with different names) are not reprocessed and sent multiple times.

      Revision 1.9  2004/07/14 19:27:27  jrichter
      modified lodestar files to work when fdr is run on systems where yukon is not on c drive.

      Revision 1.8  2004/06/15 19:34:00  jrichter
      Added FDR lodestar tag point def / fixed time stamp issue / modified backup file to append time stamp

      Revision 1.7  2004/05/10 19:00:15  jrichter
      Removed unnecessary debug.

      Revision 1.6  2004/04/08 20:03:16  jrichter
      jrichter1 Lodestar changes to handle standard format and files are read in based on point parameters.

      Revision 1.5  2004/04/06 21:10:17  jrichter
      jrichter1 Lodestar changes to handle standard format and files are read in based on point parameters.

      Revision 1.4  2003/08/18 20:28:37  jwolberg
      Fixed a problem where an invalid customer id or channel would cause fdr to insert the values into rawpointhistory with the previous pointid.

      Revision 1.3  2003/07/18 21:46:14  jwolberg
      Fixes based on answers to questions asked of Xcel.

      Revision 1.2  2003/06/09 16:14:21  jwolberg
      Added FDR LodeStar interface.


*
*
*
*    Copyright (C) 2000 Cannon Technologies, Inc.  All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <wininet.h>
#include <fcntl.h>
#include <io.h>

#include "ctitime.h"
#include "ctidate.h"

#include "cparms.h"
#include "msg_cmd.h"
#include "msg_signal.h"
#include "pointtypes.h"

#include "dllbase.h"
#include "logger.h"
#include "guard.h"
#include "fdrtextfilebase.h"
#include "fdrlodestarimport.h"

#include "utility.h"

using std::transform;
using std::list;
using std::string;
using std::endl;
using std::vector;

// Constructors, Destructor, and Operators
CtiFDR_LodeStarImportBase::CtiFDR_LodeStarImportBase(string &aInterface)
: CtiFDRTextFileBase(aInterface)
{
    // init these lists so they have something
    CtiFDRManager   *recList = new CtiFDRManager(getInterfaceName(),string(FDR_INTERFACE_RECEIVE));
    getReceiveFromList().setPointList (recList);
    recList = NULL;
    init();
}

CtiFDR_LodeStarImportBase::~CtiFDR_LodeStarImportBase()
{
}
bool CtiFDR_LodeStarImportBase::shouldDeleteFileAfterImport() const
{
    return _deleteFileAfterImportFlag;
}

CtiFDR_LodeStarImportBase &CtiFDR_LodeStarImportBase::setDeleteFileAfterImport (bool aFlag)
{
    _deleteFileAfterImportFlag = aFlag;
    return *this;
}

bool CtiFDR_LodeStarImportBase::shouldRenameSaveFileAfterImport() const
{
    return _renameSaveFileAfterImportFlag;
}

CtiFDR_LodeStarImportBase &CtiFDR_LodeStarImportBase::setRenameSaveFileAfterImport (bool aFlag)
{
    _renameSaveFileAfterImportFlag = aFlag;
    return *this;
}

BOOL CtiFDR_LodeStarImportBase::init( void )
{
    // init the base class
    Inherited::init();
    _threadReadFromFile = rwMakeThreadFunction(*this,
                                               &CtiFDR_LodeStarImportBase::threadFunctionReadFromFile);

    return TRUE;
}
/*************************************************
* Function Name: CtiFDR_LodeStarImportBase::run()
*
* Description: runs the interface
*
**************************************************
*/
BOOL CtiFDR_LodeStarImportBase::run( void )
{
    // crank up the base class
    Inherited::run();

    // startup our interfaces
    _threadReadFromFile.start();

    return TRUE;
}


/*************************************************
* Function Name: CtiFDR_LodeStarImportBase::stop()
*
* Description: stops all threads
*
**************************************************
*/
BOOL CtiFDR_LodeStarImportBase::stop( void )
{
    _threadReadFromFile.requestCancellation();
    Inherited::stop();
    return TRUE;
}


USHORT CtiFDR_LodeStarImportBase::ForeignToYukonQuality (string aQuality)
{
    USHORT Quality = NonUpdatedQuality;

    //fixThis;
    if (ciStringEqual(aQuality," "))
        Quality = NormalQuality;
    /*if (ciStringEqual(aQuality,"B"))
        Quality = NonUpdatedQuality;
    if (ciStringEqual(aQuality,"M"))
        Quality = ManualQuality;*/

    return(Quality);
}

bool CtiFDR_LodeStarImportBase::fillUpMissingTimeStamps(CtiMultiMsg* multiDispatchMsg, list< CtiMultiMsg* > &dispatchList,
                                                        const CtiTime& savedStartTime,const CtiTime& savedStopTime,
                                                        long stdLsSecondsPerInterval, string savedCustomerIdentifier, string FileName)
{
    bool returnBool = true;
    int msgCnt = 0;
    CtiTime oldTimeStamp = CtiTime(CtiDate(1,1,1990));
    CtiMultiMsg_vec pointDataList = multiDispatchMsg->getData();
    CtiMultiMsg* msgPtr;
    int nbrPoints = pointDataList.size();

    if (getDebugLevel() & MAJOR_DETAIL_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Number of pointDataList.entries(): " << nbrPoints << endl;
        dout << CtiTime() << " savedStopTime.seconds() : " << savedStopTime.seconds() <<"savedStartTime.seconds() :"<< savedStartTime.seconds()<< endl;
        dout << CtiTime() << " savedStartTime.seconds()+(pointDataList.entries()*stdLsSecondsPerInterval)-getSubtractValue() " << savedStartTime.seconds()+(nbrPoints*stdLsSecondsPerInterval)-getSubtractValue()<< endl;
    }

    msgPtr = new CtiMultiMsg;
    CtiPointDataMsg* currentPointData = NULL;
    CtiPointDataMsg* pData = NULL;
    for(long i=0;i<nbrPoints;i++)
    {
        currentPointData = (CtiPointDataMsg *)pointDataList[i];
        if (getDebugLevel() & MAJOR_DETAIL_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() <<"point: "<<i<<"currentPointData->getTime().seconds(): " <<currentPointData->getTime().seconds()<< " oldTimeStamp.seconds() " << oldTimeStamp.seconds() << endl;
        }
        if( currentPointData->getTime().seconds() <= oldTimeStamp.seconds() )
        {
            //if ENH :59 seconds end time subtract value should be 1,
            //if ENH :00 seconds end time subtract value should be 60
            currentPointData->setTime(CtiTime(savedStartTime.seconds()+(stdLsSecondsPerInterval*(i+1))-getSubtractValue()));
        }
        if (getDebugLevel() & MAJOR_DETAIL_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() <<"point: " <<i<< " currentPointData->getTime(): " << currentPointData->getTime() << endl;
        }


        pData = (CtiPointDataMsg *)currentPointData->replicateMessage();

        msgPtr->insert(pData);

        if (msgCnt >= 500 || i == (nbrPoints - 1) || i > nbrPoints)
        {
            msgCnt = 0;
            dispatchList.push_back(msgPtr);
            msgPtr = NULL;
            if (i < nbrPoints)
                msgPtr = new CtiMultiMsg;
        }
        else
            msgCnt++;

        pData = NULL;

    }

    if( savedStopTime.seconds() != savedStartTime.seconds()+(nbrPoints*stdLsSecondsPerInterval)-getSubtractValue() )
    {
        string text = "NbrPoints * SecondsPerInterval != StopTime - StartTime";
        string additional = FileName + " : " +savedCustomerIdentifier;
        additional += " - Possible File Corruption!";

        if (getDebugLevel() & MAJOR_DETAIL_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() <<" returning FALSE!!!" << endl;
            dout << CtiTime() <<" NBR of Points in file: ("<<nbrPoints<<") * Seconds Per Interval ("<<stdLsSecondsPerInterval
                 <<") != StopTime ("<<savedStopTime<<") - StartTime ("<<savedStartTime<<") "<<endl;
            dout << CtiTime() << " Calculation: "<<((nbrPoints*stdLsSecondsPerInterval)-getSubtractValue()) << " != "
                              << (savedStopTime.seconds() - savedStartTime.seconds()) <<endl;
        }
        queueMessageToDispatch(new CtiSignalMsg(SYS_PID_SYSTEM,1,text,additional,GeneralLogType,SignalEvent,"fdr lodestar"));

        delete_container(dispatchList);
        dispatchList.clear();
        returnBool = false;
    }
    //multiDispatchMsg = NULL;
    return returnBool;
}

int CtiFDR_LodeStarImportBase::readConfig( void )
{
    int         successful = TRUE;
    string   tempStr;

    tempStr = getCparmValueAsString(getKeyInterval());
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

    tempStr = getCparmValueAsString(getKeyFilename());
    if (tempStr.length() > 0)
    {
        setFileName(tempStr);
    }
    else
    {
        setFileName(string ("yukon.txt"));
    }

    tempStr = getCparmValueAsString(getKeyImportDrivePath());
    if (tempStr.length() > 0)
    {
        setFileImportBaseDrivePath(tempStr);
    }
    else
    {
        setFileImportBaseDrivePath(string ("c:\\yukon\\server\\import"));
    }

    tempStr = getCparmValueAsString(getKeyDBReloadRate());
    if (tempStr.length() > 0)
    {
        setReloadRate (atoi(tempStr.c_str()));
    }
    else
    {
        setReloadRate (86400);
    }

    tempStr = getCparmValueAsString(getKeyQueueFlushRate());
    if (tempStr.length() > 0)
    {
        setQueueFlushRate (atoi(tempStr.c_str()));
    }
    else
    {
        // default to one second for stec, its only 2 points
        setQueueFlushRate (1);
    }

    setDeleteFileAfterImport(false);
    tempStr = getCparmValueAsString(getKeyDeleteFile());
    if (tempStr.length() > 0)
    {
        if (ciStringEqual(tempStr,"true"))
        {
            setDeleteFileAfterImport(true);
        }
    }

    setRenameSaveFileAfterImport(true);
    tempStr = getCparmValueAsString(getKeyRenameSave());
    if (tempStr.length() > 0)
    {
        if (ciStringEqual(tempStr,"false"))
        {
            setRenameSaveFileAfterImport(false);
        }
    }

    if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Text import file name " << getFileName() << endl;
        dout << CtiTime() << " Text import directory " << getDriveAndPath() << endl;
        dout << CtiTime() << " Text import interval " << getInterval() << endl;
        dout << CtiTime() << " Text import dispatch queue flush rate " << getQueueFlushRate() << endl;
        dout << CtiTime() << " Text import db reload rate " << getReloadRate() << endl;

        if (shouldDeleteFileAfterImport())
            dout << CtiTime() << " Import file will be deleted after import" << endl;
        else
            dout << CtiTime() << " Import file will NOT be deleted after import" << endl;

        if (shouldRenameSaveFileAfterImport())
            dout << CtiTime() << " Import file will be renamed and saved after import" << endl;
        else
            dout << CtiTime() << " Import file will NOT be rename and saved after import" << endl;
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
bool CtiFDR_LodeStarImportBase::loadTranslationLists()
{
    bool successful = true;
    bool foundPoint = false;

    try
    {
        getFileInfoList().clear();
        // make a list with all received points
        CtiFDRManager   *pointList = new CtiFDRManager(getInterfaceName(),
                                                       string (FDR_INTERFACE_RECEIVE));

        // if status is ok, we were able to read the database at least
        if ( pointList->loadPointList() )
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

                for ( ; myIterator != pointList->getMap().end(); ++myIterator)
                {
                    foundPoint = true;
                    bool temp = translateSinglePoint(myIterator->second);

                    if (temp == false)
                    {
                        successful = false;
                    }
                }   // end for interator


                // lock the receive list and remove the old one
                CtiLockGuard<CtiMutex> receiveGuard(getReceiveFromList().getMutex());
                if (getReceiveFromList().getPointList() != NULL)
                {
                    getReceiveFromList().deletePointList();
                }
                getReceiveFromList().setPointList (pointList);

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
            dout << CtiTime() << " " << __FILE__ << " (" << __LINE__ << ")"  << endl;
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

bool CtiFDR_LodeStarImportBase::translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList)
{
    bool successful = true;

    string           tempString1;
    string           translationName;
    string           translationDrivePath;
    string           translationFilename;
    string           translationFolderName;
    CHAR fileName[200];
    CHAR fileName2[200];

    for (int x = 0; x < translationPoint->getDestinationList().size(); x++)
    {
        if (getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Parsing Yukon Point ID " << translationPoint->getPointID();
            dout << " translate: " << translationPoint->getDestinationList()[x].getTranslation() << endl;
        }
        //123 1 C:/ASD ASD.EXE
        translationName = translationPoint->getDestinationList()[x].getTranslationValue("Customer");
        if ( translationName.empty() )
            successful = false;
        string translationChannel = translationPoint->getDestinationList()[x].getTranslationValue("Channel");
        if ( translationChannel.empty() )
            successful = false;

        translationName += (string)" " + translationChannel;


        translationFolderName = translationPoint->getDestinationList()[x].getTranslationValue("DrivePath");
        if ( translationFolderName.empty() )
            successful = false;
        translationDrivePath = getFileImportBaseDrivePath();

        translationName += (string)" " + translationFolderName;
        translationDrivePath += translationFolderName;

        translationFilename = translationPoint->getDestinationList()[x].getTranslationValue("Filename");
        if ( translationFilename.empty() )
            successful = false;
        translationName += (string)" " + translationFilename;
        transform(translationName.begin(), translationName.end(), translationName.begin(), toupper);


        CtiFDR_LodeStarInfoTable tempFileInfoList (translationDrivePath, translationFilename, translationFolderName);
        string t1 = tempFileInfoList.getLodeStarDrivePath();
        string t2 = tempFileInfoList.getLodeStarFileName();
        _snprintf(fileName, 200, "%s\\%s",t1.c_str(),t2.c_str());
        int matchFlag = 0;
        for (int xx = 0; xx < getFileInfoList().size(); xx++)
        {
            string t3 = getFileInfoList()[xx].getLodeStarDrivePath();
            string t4 = getFileInfoList()[xx].getLodeStarFileName();
            _snprintf(fileName2, 200, "%s\\%s",t3.c_str(),t4.c_str());
            if (!strcmp(fileName,fileName2))
            {
                matchFlag = 1;
            }

        }
        if (!matchFlag)
        {
            getFileInfoList().push_back(tempFileInfoList);
        }
        translationPoint->getDestinationList()[x].setTranslation(translationName);


        if (getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Point ID " << translationPoint->getPointID();
            dout << " translated: " << translationName << endl;//
        }
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
void CtiFDR_LodeStarImportBase::threadFunctionReadFromFile( void )
{
    RWRunnableSelf  pSelf = rwRunnable( );
    INT retVal=0,tries=0;
    CtiTime         timeNow;
    CtiTime         refreshTime(PASTDATE);
    string action,desc;
    CHAR fileName[200];
    CHAR fileNameAndPath[250];
    WIN32_FIND_DATA* fileData = new WIN32_FIND_DATA();
    FILE* fptr = NULL;
    char workBuffer[1500];  // not real sure how long each line possibly is
    int attemptCounter=0;
    CtiFDRPoint *       fdrPoint;
    try
    {
        for ( ; ; )
        {
            pSelf.serviceCancellation( );
            pSelf.sleep (1000);

            timeNow = CtiTime();
             // now is the time to get the file
             if (timeNow >= refreshTime)
             {
                 for (int fileIndex = 0; fileIndex < getFileInfoList().size(); fileIndex++)
                 {
                     try
                     {

                        attemptCounter = 0;
                        HANDLE hSearch;
                        string t1 = getFileInfoList()[fileIndex].getLodeStarDrivePath();
                        string t2 = getFileInfoList()[fileIndex].getLodeStarFileName();
                         _snprintf(fileName, 200, "%s\\%s",t1.c_str(),t2.c_str());
                         hSearch = FindFirstFile(fileName, fileData);


                         //_snprintf(fileNameAndPath, 250, "%s",fileName);
                         string t3 = getFileInfoList()[fileIndex].getLodeStarDrivePath();
                         string t4 = fileData->cFileName;
                         _snprintf(fileNameAndPath, 250, "%s\\%s",t3.c_str(),t4.c_str());
                         if (hSearch != INVALID_HANDLE_VALUE)
                         {

                             if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                             {
                                 CtiLockGuard<CtiLogger> doubt_guard(dout);
                                 dout << "  ***** FILE_"<<fileIndex+1<<"   " << fileData->cFileName << " ***** "  << endl;
                             }
                         }
                         else
                         {
                             if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                             {
                                 CtiLockGuard<CtiLogger> doubt_guard(dout);
                                 dout << "  ***** FILE_"<<fileIndex+1<<"   " << fileName << " NOT FOUND ***** "  << endl;
                             }
                         }
                     }
                     catch(...)
                     {
                         CtiLockGuard<CtiLogger> logger_guard(dout);
                         dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                     }
                     try
                 {
                     //fptr = fopen(fileNameAndPath, "r");
                     fptr = fopen(fileNameAndPath, "r");
                 }
                 catch(...)
                 {
                     CtiLockGuard<CtiLogger> logger_guard(dout);
                     dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                 }

                     try
                     {
                         while ((fptr == NULL) && (attemptCounter < 10))
                         {
                             attemptCounter++;
                             pSelf.sleep(1000);
                             pSelf.serviceCancellation( );
                         }
                     }
                     catch(...)
                     {
                         CtiLockGuard<CtiLogger> logger_guard(dout);
                         dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                     }
                     try
                     {
                         if( fptr == NULL )
                         {
                             /*{
                                 CtiLockGuard<CtiLogger> doubt_guard(dout);
                                 dout << CtiTime() << " " << getInterfaceName() << "'s file " << string (fileName) << " was either not found or could not be opened" << endl;
                             }*/
                         }
                         else
                         {
                             vector<string>     recordVector;

                             // load list in the command vector
                             while ( fgets( (char*) workBuffer, 1500, fptr) != NULL )
                             {
                                 string entry (workBuffer);
                                 recordVector.push_back (entry);
                             }

                             fclose(fptr);

                             if( ferror( fptr ) != 0 )
                             {
                                 recordVector.erase(recordVector.begin(), recordVector.end());
                             }
                             else
                             {
                                 // retrieve each line in order
                                 int totalLines = recordVector.size();
                                 int lineCnt = 0;
                                 long secondsPerInterval = 0;
                                 char tempTest1[9];
                                 char tempTest2[9];
                                 strncpy(tempTest1,recordVector[lineCnt].data(),4);
                                 tempTest1[4] = '\0';
                                 strncpy(tempTest2,recordVector[lineCnt].data(),8);
                                 tempTest2[8] = '\0';
                                 if ((string) tempTest1 == "0001")
                                 {
                                     if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                                     {
                                         CtiLockGuard<CtiLogger> doubt_guard(dout);
                                         dout << "LodeStar STANDARD total lines = "<< totalLines << endl;
                                     }

                                 }
                                 else if ((string) tempTest2 == "00000001")
                                 {
                                     if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                                     {
                                         CtiLockGuard<CtiLogger> doubt_guard(dout);
                                         dout << "LodeStar ENHANCED total lines = "<< totalLines << endl;
                                     }
                                 }
                                 else
                                 {
                                     lineCnt = totalLines;
                                 }

                                 //information obtained from the fourth header record
                                 string savedCustomerIdentifier = string();
                                 CtiTime savedStartTime = CtiTime(CtiDate(1,1,1990));
                                 CtiTime savedStopTime = CtiTime(CtiDate(1,1,1990));
                                 CtiMultiMsg* multiDispatchMsg = new CtiMultiMsg();
                                 while( lineCnt < totalLines )
                                 {

                                     savedCustomerIdentifier = getCustomerIdentifier();
                                     savedStartTime = getlodeStarStartTime();
                                     savedStopTime = getlodeStarStopTime();
                                     if (decodeFirstHeaderRecord(recordVector[lineCnt], fileIndex))
                                     {
                                         if( multiDispatchMsg->getCount() > 0 )
                                         {
                                             secondsPerInterval = getlodeStarSecsPerInterval();
                                             list< CtiMultiMsg* > dispatchList;
                                             delete_container(dispatchList);
                                             dispatchList.clear();
                                             if( fillUpMissingTimeStamps(multiDispatchMsg, dispatchList,
                                                                         savedStartTime,savedStopTime,
                                                                         secondsPerInterval, savedCustomerIdentifier,
                                                                         fileNameAndPath) )
                                             {
                                                 CtiMultiMsg *dispatchMsg = NULL;

                                                 while( !dispatchList.empty())
                                                 {
                                                     dispatchMsg = dispatchList.front();dispatchList.pop_front();
                                                     queueMessageToDispatch(dispatchMsg);
                                                 }
                                                 //dispatchList.clearAndDestroy();
                                                 if (multiDispatchMsg != NULL)
                                                 {
                                                     delete multiDispatchMsg;
                                                     multiDispatchMsg = new CtiMultiMsg();
                                                 }
                                             }
                                             else
                                             {
                                                 delete_container(dispatchList);
                                                 dispatchList.clear();
                                                 if (multiDispatchMsg != NULL)
                                                 {
                                                     delete multiDispatchMsg;
                                                     multiDispatchMsg = new CtiMultiMsg();
                                                 }
                                                 {
                                                     CtiLockGuard<CtiLogger> doubt_guard(dout);
                                                     dout << "Not sending a multi msg for customer: " << savedCustomerIdentifier << endl;
                                                 }
                                             }
                                         }
                                         reinitialize();
                                     }
                                     else if(decodeSecondHeaderRecord(recordVector[lineCnt]) ||
                                         decodeThirdHeaderRecord(recordVector[lineCnt]) ||
                                         decodeFourthHeaderRecord(recordVector[lineCnt]) ||
                                         decodeDataRecord(recordVector[lineCnt], multiDispatchMsg))
                                     {
                                         //do nothing because the extraction of settings or point values are handled in the decode methods themselves
                                     }
                                     else
                                     {
                                         const char *charPtr;
                                         charPtr = recordVector[lineCnt].c_str();
                                         for (int xyz = 0; xyz < recordVector[lineCnt].length(); xyz++)
                                         {
                                             if (!::isspace(*charPtr))
                                             {
                                                 CtiLockGuard<CtiLogger> doubt_guard(dout);
                                                 dout << "Invalid record type in interface " << getInterfaceName() << " record:" << recordVector[lineCnt] << " line number: " << lineCnt << endl;
                                                 xyz = recordVector[lineCnt].length();
                                             }
                                             charPtr++;
                                         }
                                     }
                                     lineCnt++;
                                 }
                                 recordVector.erase(recordVector.begin(), recordVector.end());

                                 if( multiDispatchMsg->getCount() > 0 )
                                 {
                                     secondsPerInterval = getlodeStarSecsPerInterval();
                                     list< CtiMultiMsg* > dispatchList;
                                     delete_container(dispatchList);
                                     dispatchList.clear();
                                     if( fillUpMissingTimeStamps(multiDispatchMsg, dispatchList,
                                                                 savedStartTime,savedStopTime,
                                                                 secondsPerInterval, savedCustomerIdentifier,
                                                                 fileNameAndPath) )
                                     {
                                         while( !dispatchList.empty())
                                         {
                                             CtiMultiMsg *dispatchMsg = (CtiMultiMsg*)dispatchList.front();dispatchList.pop_front();
                                             queueMessageToDispatch(dispatchMsg);
                                         }
                                         delete_container(dispatchList);
                                         dispatchList.clear();
                                     }
                                     else
                                     {
                                         //dispatchList.clearAndDestroy();
                                         if (multiDispatchMsg != NULL)
                                         {
                                             delete multiDispatchMsg;
                                             multiDispatchMsg = NULL;
                                         }
                                         {
                                             CtiLockGuard<CtiLogger> doubt_guard(dout);
                                             dout << "Not sending a multi msg for customer: " << savedCustomerIdentifier << endl;
                                         }
                                     }
                                 }
                             }
                             if( shouldRenameSaveFileAfterImport() )
                             {
                                CHAR oldFileName[250];
                                strcpy(oldFileName,fileNameAndPath);
                                CHAR newFileName[250];
                                CHAR* periodPtr = strchr(fileNameAndPath,'.');//reverse lookup
                                if( periodPtr )
                                {
                                 *periodPtr = NULL;
                                }
                                else
                                {
                                 CtiLockGuard<CtiLogger> doubt_guard(dout);
                                 dout << "Uh Sir" << endl;
                                }
                                CtiTime timestamp= CtiTime();
                                string tempTime = timestamp.asString();

                                bool slashesInString = true;
                                while( slashesInString )
                                {
                                    int pos = tempTime.find("/");
                                    if( pos == string::npos )
                                    {
                                        slashesInString = false;
                                        pos = tempTime.find(":");
                                        while (pos != string::npos )
                                        {
                                            tempTime = tempTime.erase(pos,1);
                                            pos = tempTime.find(":");
                                        }
                                    }
                                    else
                                    {
                                        tempTime = tempTime.replace(pos,1,"_");
                                    }
                                }

                                _snprintf(newFileName, 250, "%s%s%s%s", fileNameAndPath, ".", tempTime.c_str(),".txt" );

                                bool success = MoveFileEx(oldFileName,newFileName, MOVEFILE_REPLACE_EXISTING | MOVEFILE_COPY_ALLOWED);
                                if ( !success )
                                {
                                    DWORD lastError = GetLastError();
                                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                                    dout << "Last Error Code: " << lastError << " for MoveFile()" << endl;
                                }
                             }
                             if( shouldDeleteFileAfterImport() )
                             {
                                 DeleteFile(fileName);
                             }
                         }
                     }
                     catch(...)
                     {
                         CtiLockGuard<CtiLogger> logger_guard(dout);
                         dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
                     }
                 }
                 refreshTime = CtiTime() - (CtiTime::now().seconds() % getInterval()) + getInterval();
             }
        }
    }

    catch ( RWCancellation &cancellationMsg )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "CANCELLATION CtiFDRTextImport::threadFunctionReadFromFile in interface " <<getInterfaceName()<< endl;
    }

    // try and catch the thread death
    catch ( ... )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Fatal Error:  CtiFDRTextIMport::threadFunctionReadFromFile  " << getInterfaceName() << " is dead! " << endl;
    }
}

