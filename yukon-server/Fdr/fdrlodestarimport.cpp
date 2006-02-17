/*-----------------------------------------------------------------------------*
*
*    FILE NAME: fdrlodestarimport.cpp
*
*    DATE: 05/22/2003
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrlodestarimport.cpp-arc  $
*    REVISION     :  $Revision: 1.19 $
*    DATE         :  $Date: 2006/02/17 17:04:31 $
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

#include <windows.h>
#include <wininet.h>
#include <fcntl.h>
#include <io.h>

#include <rw/ctoken.h>
#include "ctitime.h"
#include "ctidate.h"

#include "cparms.h"
#include "msg_cmd.h"
#include "pointtypes.h"

#include "dllbase.h"
#include "logger.h"
#include "guard.h"
#include "fdrtextfilebase.h"
#include "fdrlodestarimport.h"

#include "utility.h"

using std::transform;
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
    if (!stringCompareIgnoreCase(aQuality," "))
        Quality = NormalQuality;
    /*if (!stringCompareIgnoreCase(aQuality,"B"))
        Quality = NonUpdatedQuality;
    if (!stringCompareIgnoreCase(aQuality,"M"))
        Quality = ManualQuality;*/

	return(Quality);
}

bool CtiFDR_LodeStarImportBase::fillUpMissingTimeStamps(CtiMultiMsg* multiDispatchMsg, RWTPtrSlist< CtiMultiMsg > &dispatchList, const CtiTime& savedStartTime,const CtiTime& savedStopTime,long stdLsSecondsPerInterval)
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


        pData = new CtiPointDataMsg;
        pData = (CtiPointDataMsg *)currentPointData->replicateMessage();

        msgPtr->insert(pData); 

        if (msgCnt >= 500 || i == (nbrPoints - 1) || i > nbrPoints)
        {
            msgCnt = 0;
            dispatchList.insert(msgPtr);
            msgPtr = NULL;
            if (i < nbrPoints)
                msgPtr = new CtiMultiMsg;
        }
        else
            msgCnt++;

        pData = NULL;

    }
    /*if (msgPtr != NULL)
    {   
        delete msgPtr;
        msgPtr = NULL;
    } */
    if( savedStopTime.seconds() != savedStartTime.seconds()+(nbrPoints*stdLsSecondsPerInterval)-getSubtractValue() )
    {
        if (getDebugLevel() & MAJOR_DETAIL_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() <<"returning FALSE!!!" << endl;
        }
        dispatchList.clearAndDestroy();
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
        if (!stringCompareIgnoreCase(tempStr,"true"))
        {
            setDeleteFileAfterImport(true);
        }
    }

    setRenameSaveFileAfterImport(true);
    tempStr = getCparmValueAsString(getKeyRenameSave());
    if (tempStr.length() > 0)
    {
        if (!stringCompareIgnoreCase(tempStr,"false"))
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
*				specified direction
* 
*************************************************************************
*/
bool CtiFDR_LodeStarImportBase::loadTranslationLists()
{
    bool                successful(FALSE);
    CtiFDRPoint *       translationPoint = NULL;
    string           tempString1;
    string           tempString2;
    string           translationName;
    string           translationDrivePath;
    string           translationFilename;
    string           translationFolderName;
    bool                foundPoint = false;
    RWDBStatus          listStatus;
    CHAR fileName[200];
    CHAR fileName2[200];
    
    try
    {
        getFileInfoList().clear();
        // make a list with all received points
        CtiFDRManager   *pointList = new CtiFDRManager(getInterfaceName(),
                                                       string (FDR_INTERFACE_RECEIVE));
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

                        boost::char_separator<char> sep1(";");
                        Boost_char_tokenizer nextTranslate(translationPoint->getDestinationList()[x].getTranslation(), sep1);
                        Boost_char_tokenizer::iterator tok_iter = nextTranslate.begin(); 

                        if (!(tempString1 = *tok_iter).empty())
                        {
                            boost::char_separator<char> sep2(":");
                            Boost_char_tokenizer nextTempToken(tempString1, sep2);
                            Boost_char_tokenizer::iterator tok_iter1 = nextTempToken.begin(); 

                            tok_iter1++;
                            Boost_char_tokenizer nextTempToken_(tok_iter1.base(), tok_iter1.end(), sep1);


                            tempString2 = *nextTempToken_.begin();
                            tempString2.replace(0,tempString2.length(), tempString2.substr(1,(tempString2.length()-1)));

                            // now we have a customer identifier
                            if ( !tempString2.empty() )
                            {
                                translationName = tempString2;

                                // next token is the channel
                                tok_iter++;
                                if (!(tempString1 = *tok_iter).empty())
                                {
                                    boost::char_separator<char> sep2(":");
                                    Boost_char_tokenizer nextTempToken(tempString1, sep2);
                                    Boost_char_tokenizer::iterator tok_iter1 = nextTempToken.begin(); 

                                    tok_iter1++;
                                    tempString2 = *tok_iter1;

                                    // now we have a channel
                                    if ( !tempString2.empty() )
                                    {
                                        translationName += " ";
                                        translationName += tempString2;

                                        transform(translationName.begin(), translationName.end(), translationName.begin(), toupper);

                                        if (!(tempString1 = *(++tok_iter)).empty())
                                        {
                                            boost::char_separator<char> sep2(":");
                                            Boost_char_tokenizer nextTempToken(tempString1, sep2);
                                            Boost_char_tokenizer::iterator tok_iter1 = nextTempToken.begin(); 

                                            tok_iter1++;
                                            Boost_char_tokenizer nextTempToken_(tok_iter1.base(), tok_iter1.end(), sep1);


                                            tempString2 = *nextTempToken_.begin();

                                            tempString2.replace(0,tempString2.length(), tempString2.substr(1,(tempString2.length()-1)));

                                            // now we have a Drive/Path
                                            if ( !tempString2.empty() )
                                            {
                                                translationFolderName = tempString2;
                                                transform(translationFolderName.begin(), translationFolderName.end(), translationFolderName.begin(), tolower);

                                                

                                                translationDrivePath = getFileImportBaseDrivePath();
                                                translationDrivePath += tempString2;
                                                transform(translationDrivePath.begin(), translationDrivePath.end(), translationDrivePath.begin(), toupper);
                                                setDriveAndPath(translationDrivePath);

                                                translationName += " ";
                                                translationName += tempString2;
                                                transform(translationName.begin(), translationName.end(), translationName.begin(), toupper);
                                                if (!(tempString1 = *(++tok_iter)).empty())
                                                {
                                                    boost::char_separator<char> sep2(":");
                                                    Boost_char_tokenizer nextTempToken(tempString1, sep2);
                                                    Boost_char_tokenizer::iterator tok_iter1 = nextTempToken.begin(); 

                                                    tok_iter1++;
                                                    Boost_char_tokenizer nextTempToken_(tok_iter1.base(), tok_iter1.end(), sep1);


                                                    tempString2 = *nextTempToken_.begin();

                                                    tempString2.replace(0,tempString2.length(), tempString2.substr(1,(tempString2.length()-1)));

                                                    // now we have a Drive/Path
                                                    if ( !tempString2.empty() )
                                                    {
                                                        translationFilename = tempString2;
                                                        transform(translationFilename.begin(), translationFilename.end(), translationFilename.begin(), tolower);                                                        
                                                        setFileName(translationFilename);

                                                        translationName += " ";
                                                        translationName += translationFilename;
                                                        transform(translationName.begin(), translationName.end(), translationName.begin(), toupper);
                                                    }
                                                }
                                            }

                                        }
                                        CtiFDR_LodeStarInfoTable tempFileInfoList (translationDrivePath, translationFilename, translationFolderName);
                                        _snprintf(fileName, 200, "%s\\%s",tempFileInfoList.getLodeStarDrivePath(),tempFileInfoList.getLodeStarFileName());
                                        int matchFlag = 0;
                                        for (int xx = 0; xx < getFileInfoList().size(); xx++)
                                        {
                                            _snprintf(fileName2, 200, "%s\\%s",getFileInfoList()[xx].getLodeStarDrivePath(),getFileInfoList()[xx].getLodeStarFileName());
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
                                        successful = true;

                                        if (getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << CtiTime() << " Point ID " << translationPoint->getPointID();
                                            dout << " translated: " << translationName << endl;
                                        }
                                    }
                                }
                            }
                        }   // first token invalid
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
            dout << CtiTime() << " " << __FILE__ << " (" << __LINE__ << ") db read code " << listStatus.errorCode()  << endl;
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
    FILE* fptr;
    char workBuffer[1500];  // not real sure how long each line possibly is
    int attemptCounter=0;
    RWDBStatus          listStatus;
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


                        HANDLE hSearch;
                         _snprintf(fileName, 200, "%s\\%s",getFileInfoList()[fileIndex].getLodeStarDrivePath(),getFileInfoList()[fileIndex].getLodeStarFileName());
                         hSearch = FindFirstFile(fileName, fileData);
                                                           

                         //_snprintf(fileNameAndPath, 250, "%s",fileName);
                         _snprintf(fileNameAndPath, 250, "%s\\%s",getFileInfoList()[fileIndex].getLodeStarDrivePath(),fileData->cFileName);
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
                                             RWTPtrSlist< CtiMultiMsg > dispatchList;
                                             dispatchList.clearAndDestroy();
                                             if( fillUpMissingTimeStamps(multiDispatchMsg, dispatchList, savedStartTime,savedStopTime,secondsPerInterval) )
                                             {
                                                 CtiMultiMsg *dispatchMsg = NULL;

                                                 while( !dispatchList.isEmpty())
                                                 {       
                                                     dispatchMsg = dispatchList.get();
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
                                                 dispatchList.clearAndDestroy();
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
                                     RWTPtrSlist< CtiMultiMsg > dispatchList;
                                     dispatchList.clearAndDestroy();
                                     if( fillUpMissingTimeStamps(multiDispatchMsg, dispatchList, savedStartTime,savedStopTime,secondsPerInterval) )
                                     {
                                         while( !dispatchList.isEmpty())
                                         {       
                                             CtiMultiMsg *dispatchMsg = (CtiMultiMsg*)dispatchList.get();
                                             queueMessageToDispatch(dispatchMsg);
                                         }
                                         dispatchList.clearAndDestroy();
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
                                 string tempTime = timestamp.asString().erase(16);
                                 tempTime = tempTime.replace(10,1,"_");
                                 tempTime = tempTime.replace(2,1,"_");
                                 tempTime = tempTime.replace(5,1,"_");
                                 tempTime = tempTime.erase(13,1);

                                 _snprintf(newFileName, 250, "%s%s%s",fileNameAndPath, ".", tempTime);
                                 MoveFileEx(oldFileName,newFileName, MOVEFILE_REPLACE_EXISTING | MOVEFILE_COPY_ALLOWED);

                                 DWORD lastError = GetLastError();
                                 if( lastError )
                                 {
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

