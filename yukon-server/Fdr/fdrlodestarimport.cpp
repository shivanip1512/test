#include "precompiled.h"

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
        CTILOG_DEBUG(dout, "Number of pointDataList.entries(): "<< nbrPoints <<
                endl <<" savedStopTime.seconds() : "<< savedStopTime.seconds() <<"savedStartTime.seconds() :"<< savedStartTime.seconds() <<
                endl <<" savedStartTime.seconds()+(pointDataList.entries()*stdLsSecondsPerInterval)-getSubtractValue() "<< savedStartTime.seconds()+(nbrPoints*stdLsSecondsPerInterval)-getSubtractValue());
    }

    msgPtr = new CtiMultiMsg;
    CtiPointDataMsg* currentPointData = NULL;
    CtiPointDataMsg* pData = NULL;
    for(long i=0;i<nbrPoints;i++)
    {
        currentPointData = (CtiPointDataMsg *)pointDataList[i];
        if (getDebugLevel() & MAJOR_DETAIL_FDR_DEBUGLEVEL)
        {
            CTILOG_DEBUG(dout, "point: "<< i <<"currentPointData->getTime().seconds(): "<<currentPointData->getTime().seconds()<<" oldTimeStamp.seconds() "<< oldTimeStamp.seconds());
        }
        if( currentPointData->getTime().seconds() <= oldTimeStamp.seconds() )
        {
            //if ENH :59 seconds end time subtract value should be 1,
            //if ENH :00 seconds end time subtract value should be 60
            currentPointData->setTime(CtiTime(savedStartTime.seconds()+(stdLsSecondsPerInterval*(i+1))-getSubtractValue()));
        }
        if (getDebugLevel() & MAJOR_DETAIL_FDR_DEBUGLEVEL)
        {
            CTILOG_DEBUG(dout, "point: "<< i <<" currentPointData->getTime(): "<< currentPointData->getTime());
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
            CTILOG_DEBUG(dout, "returning FALSE!!!" <<
                    endl <<"NBR of Points in file: ("<<nbrPoints<<") * Seconds Per Interval ("<< stdLsSecondsPerInterval <<") != StopTime ("<<savedStopTime<<") - StartTime ("<<savedStartTime<<") "<<
                    endl <<"Calculation: "<<((nbrPoints*stdLsSecondsPerInterval)-getSubtractValue()) <<" != "<< (savedStopTime.seconds() - savedStartTime.seconds()));
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
        Cti::FormattedList loglist;

        loglist.add("Text import file name")                 << getFileName();
        loglist.add("Text import directory")                 << getDriveAndPath();
        loglist.add("Text import interval")                  << getInterval();
        loglist.add("Text import dispatch queue flush rate") << getQueueFlushRate();
        loglist.add("Text import db reload rate")            << getReloadRate();

        if (shouldDeleteFileAfterImport())
            loglist <<"Import file will be deleted after import";
        else
            loglist <<"Import file will NOT be deleted after import";

        if (shouldRenameSaveFileAfterImport())
            loglist <<"Import file will be renamed and saved after import";
        else
            loglist <<"Import file will NOT be rename and saved after import";

        CTILOG_DEBUG(dout, loglist);
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
            CTILOG_DEBUG(dout, "Parsing Yukon Point ID "<< translationPoint->getPointID() <<
                    " translate: " << translationPoint->getDestinationList()[x].getTranslation());
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
            CTILOG_DEBUG(dout, "Point ID "<< translationPoint->getPointID() <<" translated: "<< translationName);
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
                                 CTILOG_DEBUG(dout, "FILE_"<< fileIndex+1 <<"   "<< fileData->cFileName);
                             }
                         }
                         else
                         {
                             if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                             {
                                 CTILOG_DEBUG(dout, "FILE_"<< fileIndex+1 <<"   "<< fileName <<" NOT FOUND");
                             }
                         }
                     }
                     catch(...)
                     {
                         CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                     }

                     try
                     {
                         fptr = fopen(fileNameAndPath, "r");
                     }
                     catch(...)
                     {
                         CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
                         CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                     }

                     try
                     {
                         if( fptr == NULL )
                         {
                             // CTILOG_ERROR(dout, getInterfaceName() <<"'s file "<< fileName <<" was either not found or could not be opened");
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
                                         CTILOG_DEBUG(dout, "LodeStar STANDARD total lines = "<< totalLines);
                                     }
                                 }
                                 else if ((string) tempTest2 == "00000001")
                                 {
                                     if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                                     {
                                         CTILOG_DEBUG(dout, "LodeStar ENHANCED total lines = "<< totalLines);
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

                                                 CTILOG_ERROR(dout, "Not sending a multi msg for customer: " << savedCustomerIdentifier);
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
                                                 CTILOG_ERROR(dout, "Invalid record type in interface "<< getInterfaceName() <<" record:"<< recordVector[lineCnt] <<" line number: "<< lineCnt);
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

                                         CTILOG_ERROR(dout, "Not sending a multi msg for customer: " << savedCustomerIdentifier);
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
                                    CTILOG_ERROR(dout, "Invalid fileNameAndPath \""<< fileNameAndPath <<"\"");
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
                                    CTILOG_ERROR(dout, "MoveFile failed with error code: "<< lastError);
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
                         CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                     }
                 }
                 refreshTime = CtiTime() - (CtiTime::now().seconds() % getInterval()) + getInterval();
             }
        }
    }

    catch ( RWCancellation & )
    {
        CTILOG_INFO(dout, "CANCELLATION of threadFunctionReadFromFile in interface "<< getInterfaceName());
    }

    // try and catch the thread death
    catch ( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "threadFunctionReadFromFile in interface "<< getInterfaceName() <<" is dead!");
    }
}

