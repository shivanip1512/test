
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*-----------------------------------------------------------------------------*
*
*    FILE NAME: fdrlodestarimport.cpp
*
*    DATE: 05/22/2003
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrlodestarimport.cpp-arc  $
*    REVISION     :  $Revision: 1.6 $
*    DATE         :  $Date: 2004/04/08 20:03:16 $
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
#include "fdrlodestarimport.h"


// Constructors, Destructor, and Operators
CtiFDR_LodeStarImportBase::CtiFDR_LodeStarImportBase(RWCString &aInterface)
: CtiFDRTextFileBase(aInterface)
{  
    // init these lists so they have something
    CtiFDRManager   *recList = new CtiFDRManager(getInterfaceName(),RWCString(FDR_INTERFACE_RECEIVE)); 
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


USHORT CtiFDR_LodeStarImportBase::ForeignToYukonQuality (RWCString aQuality)
{
    USHORT Quality = NonUpdatedQuality;

    //fixThis;
    if (!aQuality.compareTo (" ",RWCString::ignoreCase))
        Quality = NormalQuality;
    /*if (!aQuality.compareTo ("B",RWCString::ignoreCase))
        Quality = NonUpdatedQuality;
    if (!aQuality.compareTo ("M",RWCString::ignoreCase))
        Quality = ManualQuality;*/

	return(Quality);
}

bool CtiFDR_LodeStarImportBase::fillUpMissingTimeStamps(CtiMultiMsg* multiDispatchMsg,const RWTime& savedStartTime,const RWTime& savedStopTime,long stdLsSecondsPerInterval)
{
    bool returnBool = true;
    RWTime oldTimeStamp = RWTime(RWDate(1,1,1990));
    RWOrdered pointDataList = multiDispatchMsg->getData();
    
    if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Number of pointDataList.entries(): " << pointDataList.entries() << endl;
    }
    for(long i=0;i<pointDataList.entries();i++)
    {
        CtiPointDataMsg* currentPointData = (CtiPointDataMsg*)pointDataList[i];
        if( currentPointData->getTime().seconds() <= oldTimeStamp.seconds() )
        {
            currentPointData->setTime(RWTime(savedStartTime.seconds()+(stdLsSecondsPerInterval*(i+1))));
        }
    }
    if( savedStopTime.seconds() != savedStartTime.seconds()+(pointDataList.entries()*stdLsSecondsPerInterval)-getSubtractValue() )
    {
        returnBool = false;
    }

    return returnBool;
}

int CtiFDR_LodeStarImportBase::readConfig( void )
{    
    int         successful = TRUE;
    RWCString   tempStr;

    tempStr = getCparmValueAsString(getKeyInterval());
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

    tempStr = getCparmValueAsString(getKeyFilename());
    if (tempStr.length() > 0)
    {
        setFileName(tempStr);
    }
    else
    {
        setFileName(RWCString ("yukon.txt"));
    }

    tempStr = getCparmValueAsString(getKeyDrivePath());
    if (tempStr.length() > 0)
    {
        setDriveAndPath(tempStr);
    }
    else
    {
        setDriveAndPath(RWCString ("\\yukon\\server\\import"));
    }

    tempStr = getCparmValueAsString(getKeyDBReloadRate());
    if (tempStr.length() > 0)
    {
        setReloadRate (atoi(tempStr));
    }
    else
    {
        setReloadRate (3600);
    }

    tempStr = getCparmValueAsString(getKeyQueueFlushRate());
    if (tempStr.length() > 0)
    {
        setQueueFlushRate (atoi(tempStr));
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
        if (!tempStr.compareTo ("true",RWCString::ignoreCase))
        {
            setDeleteFileAfterImport(true);
        }
    }

    setRenameSaveFileAfterImport(true);
    tempStr = getCparmValueAsString(getKeyRenameSave());
    if (tempStr.length() > 0)
    {
        if (!tempStr.compareTo ("false",RWCString::ignoreCase))
        {
            setRenameSaveFileAfterImport(false);
        }
    }

    if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Text import file name " << getFileName() << endl;
        dout << RWTime() << " Text import directory " << getDriveAndPath() << endl;
        dout << RWTime() << " Text import interval " << getInterval() << endl;
        dout << RWTime() << " Text import dispatch queue flush rate " << getQueueFlushRate() << endl;
        dout << RWTime() << " Text import db reload rate " << getReloadRate() << endl;

        if (shouldDeleteFileAfterImport())
            dout << RWTime() << " Import file will be deleted after import" << endl;
        else
            dout << RWTime() << " Import file will NOT be deleted after import" << endl;

        if (shouldRenameSaveFileAfterImport())
            dout << RWTime() << " Import file will be renamed and saved after import" << endl;
        else
            dout << RWTime() << " Import file will NOT be rename and saved after import" << endl;
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
    RWCString           tempString1;
    RWCString           tempString2;
    RWCString           translationName;
    RWCString           translationDrivePath;
    RWCString           translationFilename;
    bool                foundPoint = false;
    RWDBStatus          listStatus;
    CHAR fileName[200];
    CHAR fileName2[200];
    
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

                            // now we have a customer identifier
                            if ( !tempString2.isNull() )
                            {
                                translationName = tempString2;

                                // next token is the channel
                                if (!(tempString1 = nextTranslate(";")).isNull())
                                {
                                    RWCTokenizer nextTempToken(tempString1);

                                    // do not care about the first part
                                    nextTempToken(":");

                                    tempString2 = nextTempToken(":");

                                    // now we have a channel
                                    if ( !tempString2.isNull() )
                                    {
                                        translationName += " ";
                                        translationName += tempString2;
                                        translationName.toUpper();
    
                                        if (!(tempString1 = nextTranslate(";")).isNull())
                                        {

                                            RWCTokenizer nextTempToken(tempString1);

                                            // do not care about the first part
                                            nextTempToken(":");

                                            tempString2 = nextTempToken(";");
                                            tempString2(0,tempString2.length()) = tempString2 (1,(tempString2.length()-1));

                                            // now we have a Drive/Path                                            
                                            if ( !tempString2.isNull() )
                                            {
                                                translationDrivePath = tempString2;
                                                translationDrivePath.toUpper();
                                                setDriveAndPath(translationDrivePath);
                                                if (!(tempString1 = nextTranslate(";")).isNull())
                                                {

                                                    RWCTokenizer nextTempToken(tempString1);

                                                    // do not care about the first part
                                                    nextTempToken(":");

                                                    tempString2 = nextTempToken(";");
                                                    tempString2(0,tempString2.length()) = tempString2 (1,(tempString2.length()-1));

                                                    // now we have a Drive/Path                                            
                                                    if ( !tempString2.isNull() )
                                                    {
                                                        translationFilename = tempString2;
                                                        translationFilename.toLower();
                                                        setFileName(translationFilename);
                                                    }
                                                }
                                            }

                                        }
                                        CtiFDR_LodeStarInfoTable tempFileInfoList (translationDrivePath, translationFilename);
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
                                            dout << RWTime() << " Point ID " << translationPoint->getPointID();
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
void CtiFDR_LodeStarImportBase::threadFunctionReadFromFile( void )
{
    RWRunnableSelf  pSelf = rwRunnable( );
    INT retVal=0,tries=0;
    RWTime         timeNow;
    RWTime         refreshTime(rwEpoch);
    RWCString action,desc;
    CHAR fileName[200];
    CHAR fileNameAndPath[250];
    WIN32_FIND_DATA* fileData = new WIN32_FIND_DATA();
    FILE* fptr;
    char workBuffer[500];  // not real sure how long each line possibly is
    int attemptCounter=0;
    RWDBStatus          listStatus;
    CtiFDRPoint *       fdrPoint;
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
                 for (int fileIndex = 0; fileIndex < getFileInfoList().size(); fileIndex++) 
                 {
                     if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                     {
                         CtiLockGuard<CtiLogger> doubt_guard(dout);
                         dout << "JULIE:  ***** " << getFileInfoList()[fileIndex].getLodeStarFileName() << " ***** "  << endl;
                     }
                     _snprintf(fileName, 200, "%s\\%s",getFileInfoList()[fileIndex].getLodeStarDrivePath(),getFileInfoList()[fileIndex].getLodeStarFileName());
                     FindFirstFile(fileName, fileData);
                     _snprintf(fileNameAndPath, 250, "%s\\%s",getFileInfoList()[fileIndex].getLodeStarDrivePath(),fileData->cFileName);
                     fptr = fopen(fileNameAndPath, "r");
                     while ((fptr == NULL) && (attemptCounter < 10))
                     {
                         attemptCounter++;
                         pSelf.sleep(1000);
                         pSelf.serviceCancellation( );
                     }

                     if( fptr == NULL )
                     {
                         /*{
                             CtiLockGuard<CtiLogger> doubt_guard(dout);
                             dout << RWTime() << " " << getInterfaceName() << "'s file " << RWCString (fileName) << " was either not found or could not be opened" << endl;
                         }*/
                     }
                     else
                     {
                         vector<RWCString>     recordVector;

                         // load list in the command vector
                         while ( fgets( (char*) workBuffer, 500, fptr) != NULL )
                         {
                             RWCString entry (workBuffer);
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
                             if ((RWCString) tempTest1 == "0001") 
                             {
                                 if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                                 {
                                     CtiLockGuard<CtiLogger> doubt_guard(dout);
                                     dout << "LodeStar STANDARD total lines = "<< totalLines << endl;
                                 }
                                   
                             }
                             else if ((RWCString) tempTest2 == "00000001") 
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
                             RWCString savedCustomerIdentifier = RWCString();
                             RWTime savedStartTime = RWTime(RWDate(1,1,1990));
                             RWTime savedStopTime = RWTime(RWDate(1,1,1990));
                             CtiMultiMsg* multiDispatchMsg = new CtiMultiMsg();
                             while( lineCnt < totalLines )
                             {

                                 savedCustomerIdentifier = getCustomerIdentifier();
                                 savedStartTime = getlodeStarStartTime();
                                 savedStopTime = getlodeStarStopTime();
                                 if (decodeFirstHeaderRecord(recordVector[lineCnt]))
                                 {    
                                     if( multiDispatchMsg->getCount() > 0 )
                                     {
                                         secondsPerInterval = getlodeStarSecsPerInterval();
                                         if( fillUpMissingTimeStamps(multiDispatchMsg,savedStartTime,savedStopTime,secondsPerInterval) )
                                         {
                                             queueMessageToDispatch(multiDispatchMsg);
                                             multiDispatchMsg = new CtiMultiMsg();
                                         }
                                         else
                                         {
                                             delete multiDispatchMsg;
                                             multiDispatchMsg = new CtiMultiMsg();
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
                                     charPtr = recordVector[lineCnt].data();
                                     for (int xyz = 0; xyz < recordVector[lineCnt].length(); xyz++)
                                     {
                                         if (!isspace(*charPtr))
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
                                 if( fillUpMissingTimeStamps(multiDispatchMsg,savedStartTime,savedStopTime,secondsPerInterval) )
                                 {
                                     queueMessageToDispatch(multiDispatchMsg);
                                 }
                                 else
                                 {
                                     delete multiDispatchMsg;
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

                             _snprintf(newFileName, 250, "%s%s",fileNameAndPath,".bak");
                             MoveFile(oldFileName,newFileName);

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
                 refreshTime = RWTime() - (RWTime().seconds() % getInterval()) + getInterval();
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
        dout << RWTime() << " Fatal Error:  CtiFDRTextIMport::threadFunctionReadFromFile  " << getInterfaceName() << " is dead! " << endl;
    }
}

