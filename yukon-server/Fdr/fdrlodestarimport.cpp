#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*-----------------------------------------------------------------------------*
*
*    FILE NAME: fdrlodestarimport.cpp
*
*    DATE: 05/22/2003
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrlodestarimport.cpp-arc  $
*    REVISION     :  $Revision: 1.4 $
*    DATE         :  $Date: 2003/08/18 20:28:37 $
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


CtiFDR_LodeStarImport * lodeStarImportInterface;

const CHAR * CtiFDR_LodeStarImport::KEY_INTERVAL = "FDR_LODESTARIMPORT_INTERVAL";
const CHAR * CtiFDR_LodeStarImport::KEY_FILENAME = "FDR_LODESTARIMPORT_FILENAME";
const CHAR * CtiFDR_LodeStarImport::KEY_DRIVE_AND_PATH = "FDR_LODESTARIMPORT_DRIVE_AND_PATH";
const CHAR * CtiFDR_LodeStarImport::KEY_DB_RELOAD_RATE = "FDR_LODESTARIMPORT_DB_RELOAD_RATE";
const CHAR * CtiFDR_LodeStarImport::KEY_QUEUE_FLUSH_RATE = "FDR_LODESTARIMPORT_QUEUE_FLUSH_RATE";
const CHAR * CtiFDR_LodeStarImport::KEY_DELETE_FILE = "FDR_LODESTARIMPORT_DELETE_FILE";
const CHAR * CtiFDR_LodeStarImport::KEY_RENAME_SAVE_FILE = "FDR_LODESTARIMPORT_RENAME_SAVE_FILE";


// Constructors, Destructor, and Operators
CtiFDR_LodeStarImport::CtiFDR_LodeStarImport()
: CtiFDRTextFileBase(RWCString("LODESTAR"))
{  
    // init these lists so they have something
    CtiFDRManager   *recList = new CtiFDRManager(getInterfaceName(),RWCString(FDR_INTERFACE_RECEIVE)); 
    getReceiveFromList().setPointList (recList);
    recList = NULL;
    init();

}

CtiFDR_LodeStarImport::~CtiFDR_LodeStarImport()
{
}


bool CtiFDR_LodeStarImport::shouldDeleteFileAfterImport() const
{
    return _deleteFileAfterImportFlag;
}

CtiFDR_LodeStarImport &CtiFDR_LodeStarImport::setDeleteFileAfterImport (bool aFlag)
{
    _deleteFileAfterImportFlag = aFlag;
    return *this;
}

bool CtiFDR_LodeStarImport::shouldRenameSaveFileAfterImport() const
{
    return _renameSaveFileAfterImportFlag;
}

CtiFDR_LodeStarImport &CtiFDR_LodeStarImport::setRenameSaveFileAfterImport (bool aFlag)
{
    _renameSaveFileAfterImportFlag = aFlag;
    return *this;
}

BOOL CtiFDR_LodeStarImport::init( void )
{
    // init the base class
    Inherited::init();    
    _threadReadFromFile = rwMakeThreadFunction(*this, 
                                               &CtiFDR_LodeStarImport::threadFunctionReadFromFile);

    if (!readConfig( ))
    {
        return FALSE;
    }

    loadTranslationLists();
    return TRUE;
}
/*************************************************
* Function Name: CtiFDR_LodeStarImport::run()
*
* Description: runs the interface
* 
**************************************************
*/
BOOL CtiFDR_LodeStarImport::run( void )
{
    // crank up the base class
    Inherited::run();

    // startup our interfaces
    _threadReadFromFile.start();

    return TRUE;
}


/*************************************************
* Function Name: CtiFDR_LodeStarImport::stop()
*
* Description: stops all threads 
* 
**************************************************
*/
BOOL CtiFDR_LodeStarImport::stop( void )
{
    _threadReadFromFile.requestCancellation();
    Inherited::stop();
    return TRUE;
}


RWTime CtiFDR_LodeStarImport::ForeignToYukonTime (RWCString aTime, CHAR aDstFlag)
{//format is 'YYYYMMDDHHMMSS' note: hours are military
    struct tm ts;
    RWTime retVal;

    if (aTime.length() == 14)
    {
        if (sscanf (aTime.data(),
                    "%4ld%2ld%2ld%2ld%2ld%2ld",
                    &ts.tm_year,
                    &ts.tm_mon,
                    &ts.tm_mday,
                    &ts.tm_hour,
                    &ts.tm_min,
                    &ts.tm_sec) != 6)
        {
            retVal = rwEpoch;
        }
        else
        {
            ts.tm_year -= 1900;
            ts.tm_mon--;

            if (aDstFlag == 'Y' || aDstFlag == 'y')
            {
                ts.tm_isdst = TRUE;
            }
            else
            {
                ts.tm_isdst = FALSE;
            }

            try 
            {
                retVal = RWTime(&ts);

                // if RWTime can't make a time ???
                if (!retVal.isValid())
                {
                    retVal = rwEpoch;
                }
            }
            catch (...)
            {
                retVal = rwEpoch;
            }
        }
    }
    else
        retVal = rwEpoch;
    return retVal;
}

USHORT CtiFDR_LodeStarImport::ForeignToYukonQuality (RWCString aQuality)
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

/**********************
* used to retrieve comma separated data
* strtok wouldn't work because of multiple
* tokens in a row ,,,,
***********************
*/
bool getToken(char **InBuffer, RWCString &outBuffer)
{
    bool retVal = true;
    char *ptr;

        // find comma if one exists
    if( *InBuffer == NULL )
    {
        retVal = false;
    } 
    else if((ptr = strchr(*InBuffer, ',')) != NULL) 
    {

            // found one
        *ptr = '\0';
		outBuffer = *InBuffer;
        *InBuffer += outBuffer.length() + 1;

    } 
    else if((ptr = strchr(*InBuffer, '\0')) != NULL) 
    {
        *ptr = '\0';
        outBuffer = *InBuffer;
        *InBuffer = '\0';
    }
    else
    {
         retVal = false;
    }
        // return current buffer
    return retVal;
}

bool CtiFDR_LodeStarImport::decodeFirstHeaderRecord(RWCString& aLine,RWCString& lsCustomerIdentifier,long& pointId,long& lsChannel,RWTime& lsStartTime,RWTime& lsStopTime,RWCString& lsDSTFlag,RWCString& lsInvalidRecordFlag)
{
	bool                retCode = false;
    bool                isFirstHeaderFlag = true;
    bool                headerRecordValidFlag = true;
    RWCString           tempString1;// Will receive each token
    RWCTokenizer        cmdLine(aLine);// Tokenize the string aLine
    RWCString           tokedStr = cmdLine("\r\n");
    char*               tempCharPtr = (char*)tokedStr.data();
    CtiFDRPoint         point;
    int                 fieldNumber = 1;
    RWCString           tempStartTimeStr = "";
    RWCString           tempStopTimeStr = "";

    /****************************
    * the first header has of the following format
    * sort code (00000001),customer identifier,channel,start time,stop time,DST flag,invalid record
    *****************************
    */

    try
    {
        while (getToken(&tempCharPtr,tempString1) && isFirstHeaderFlag && headerRecordValidFlag)
        {
            switch (fieldNumber)
            {
                case 1:
                    {
                        if( tempString1 != "00000001" )
                        {
                            isFirstHeaderFlag = false;
                        }
                        break;
                    }
                case 2:
                    {
                        lsCustomerIdentifier = tempString1;
                        break;
                    }
                case 3:
                    {
                        lsChannel = atol(tempString1);
    
                        CHAR keyString[80];
                        _snprintf(keyString,80,"%s %d",lsCustomerIdentifier,lsChannel);
                        bool pointFound = findTranslationNameInList(RWCString(keyString), getReceiveFromList(), point);
                        if( pointFound )
                        {
                            pointId = point.getPointID();
                        }
                        else
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " Translation for Customer Id: " << lsCustomerIdentifier << " and Channel: " << lsCustomerIdentifier << " from file " << getFileName() << " was not found" << endl;
                            }
                            CHAR tempIdStr[80];
                            CHAR tempChanStr[80];
                            RWCString desc = RWCString ("Lodestar point is not listed in the translation table");
                            _snprintf(tempIdStr,80,"%s", lsCustomerIdentifier);
                            _snprintf(tempChanStr,80,"%d", lsChannel);
                            CHAR tempBigStr[256];
                            _snprintf(tempBigStr,256,"%s%s%s%s", "Customer Id: ",tempIdStr, "; Channel: ", tempChanStr);
                            RWCString action = RWCString(tempBigStr);
                            logEvent (desc,action);
                            pointId = 0;
                        }
                        break;
                    }
                case 4:
                    {
                        //Can't yet convert the timestamp string to a RWTime because we don't have the DST flag yet
                        tempStartTimeStr = tempString1;
                        break;
                    }
    
                case 5:
                    {
                        //Can't yet convert the timestamp string to a RWTime because we don't have the DST flag yet
                        tempStopTimeStr = tempString1;
                        break;
                    }
                case 6:
                    {
                        lsDSTFlag = tempString1;
    
                        //Now we can convert the 
                        lsStartTime = ForeignToYukonTime(tempStartTimeStr, lsDSTFlag.data()[0]);
                        if( lsStartTime == rwEpoch )
                        {
                            headerRecordValidFlag = false;
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " Could not parse Lodestar start timestamp: " << tempString1 << " for Customer Identifier: " << lsCustomerIdentifier << endl;
                            }
                        }
    
                        lsStopTime = ForeignToYukonTime(tempStopTimeStr, lsDSTFlag.data()[0]);
                        if( lsStopTime == rwEpoch )
                        {
                            headerRecordValidFlag = false;
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " Could not parse Lodestar stop timestamp: " << tempString1 << " for Customer Identifier: " << lsCustomerIdentifier << endl;
                            }
                        }
                        break;
                    }
                case 7:
                    {
                        lsInvalidRecordFlag = tempString1;
                        break;
                    }
                default:
                    break;
            }
            fieldNumber++;
        }
    }
    catch(...)
    {
        headerRecordValidFlag = false;
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    if( headerRecordValidFlag && isFirstHeaderFlag )
    {
        retCode = true;
    }
    return retCode;
}

bool CtiFDR_LodeStarImport::decodeSecondHeaderRecord(RWCString& aLine, double& lsMeterStartReading, double& lsMeterStopReading, double& lsMeterMultiplier, double& lsMeterOffset, double& lsPulseMultiplier, double& lsPulseOffset, long& lsSecondsPerInterval, long& lsUnitOfMeasure, long& lsBasicUnitCode, long& lsTimeZone, double& lsPopulation, double& lsWeight)
{
	bool                retCode = false;
    bool                isSecondHeaderFlag = true;
    bool                headerRecordValidFlag = true;
    RWCString           tempString1;// Will receive each token
    RWCTokenizer        cmdLine(aLine);// Tokenize the string aLine
    RWCString           tokedStr = cmdLine("\r\n");
    char*               tempCharPtr = (char*)tokedStr.data();
    int                 fieldNumber = 1;


    /****************************
    * the second header has of the following format
    * sort code (00000002),meter start reading,meter stop reading,meter multiplier,meter offset,pulse multiplier,pulse offset,
    * seconds per interval,unit of measure,basic unit code,time zone,population, weight
    *****************************
    */

    try
    {
        while (getToken(&tempCharPtr,tempString1) && isSecondHeaderFlag && headerRecordValidFlag)
        {
            switch (fieldNumber)
            {
                case 1:
                    {
                        if( tempString1 != "00000002" )
                        {
                            isSecondHeaderFlag = false;
                        }
                        break;
                    }
                case 2:
                    {
                        lsMeterStartReading = atof(tempString1);
                        break;
                    }
                case 3:
                    {
                        lsMeterStopReading = atof(tempString1);
                        break;
                    }
                case 4:
                    {
                        lsMeterMultiplier = atof(tempString1);
                        break;
                    }
    
                case 5:
                    {
                        lsMeterOffset = atof(tempString1);
                        break;
                    }
                case 6:
                    {
                        lsPulseMultiplier = atof(tempString1);
                        break;
                    }
                case 7:
                    {
                        lsPulseOffset = atof(tempString1);
                        break;
                    }
                case 8:
                    {
                        lsSecondsPerInterval = atol(tempString1);
                        break;
                    }
                case 9:
                    {
                        lsUnitOfMeasure = atol(tempString1);
                        break;
                    }
                case 10:
                    {
                        lsBasicUnitCode = atol(tempString1);
                        break;
                    }
                case 11:
                    {
                        lsTimeZone = atol(tempString1);
                        break;
                    }
                case 12:
                    {
                        lsPopulation = atof(tempString1);
                        break;
                    }
                case 13:
                    {
                        lsWeight = atof(tempString1);
                        break;
                    }
                default:
                    break;
            }
            fieldNumber++;
        }
    }
    catch(...)
    {
        headerRecordValidFlag = false;
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    if( headerRecordValidFlag && isSecondHeaderFlag )
    {
        retCode = true;
    }
    return retCode;
}

bool CtiFDR_LodeStarImport::decodeThirdHeaderRecord(RWCString& aLine, RWCString& lsDescriptor)
{
	bool                retCode = false;
    bool                isThirdHeaderFlag = true;
    bool                headerRecordValidFlag = true;
    RWCString           tempString1;// Will receive each token
    RWCTokenizer        cmdLine(aLine);// Tokenize the string aLine
    RWCString           tokedStr = cmdLine("\r\n");
    char*               tempCharPtr = (char*)tokedStr.data();
    int                 fieldNumber = 1;


    /****************************
    * the third header has of the following format
    * sort code (00000003),description
    *****************************
    */

    try
    {
        while (getToken(&tempCharPtr,tempString1) && isThirdHeaderFlag && headerRecordValidFlag)
        {
            switch (fieldNumber)
            {
                case 1:
                    {
                        if( tempString1 != "00000003" )
                        {
                            isThirdHeaderFlag = false;
                        }
                        break;
                    }
                case 2:
                    {
                        lsDescriptor = tempString1;
                        break;
                    }
                default:
                    break;
            }
            fieldNumber++;
        }
    }
    catch(...)
    {
        headerRecordValidFlag = false;
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    if( headerRecordValidFlag && isThirdHeaderFlag )
    {
        retCode = true;
    }
    return retCode;
}

bool CtiFDR_LodeStarImport::decodeFourthHeaderRecord(RWCString& aLine, RWTime& lsTimeStamp, RWCString& lsOrigin)
{
	bool                retCode = false;
    bool                isFourthHeaderFlag = true;
    bool                headerRecordValidFlag = true;
    RWCString           tempString1;// Will receive each token
    RWCTokenizer        cmdLine(aLine);// Tokenize the string aLine
    RWCString           tokedStr = cmdLine("\r\n");
    char*               tempCharPtr = (char*)tokedStr.data();
    int                 fieldNumber = 1;


    /****************************
    * the fourth header has of the following format
    * sort code (00000004),time stamp,origin
    *****************************
    */

    try
    {
        while (getToken(&tempCharPtr,tempString1) && isFourthHeaderFlag && headerRecordValidFlag)
        {
            switch (fieldNumber)
            {
                case 1:
                    {
                        if( tempString1 != "00000004" )
                        {
                            isFourthHeaderFlag = false;
                        }
                        break;
                    }
                case 2:
                    {
                        lsTimeStamp = ForeignToYukonTime(tempString1,'A');
                        break;
                    }
                case 3:
                    {
                        lsOrigin = tempString1;
                        break;
                    }
                default:
                    break;
            }
            fieldNumber++;
        }
    }
    catch(...)
    {
        headerRecordValidFlag = false;
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    if( headerRecordValidFlag && isFourthHeaderFlag )
    {
        retCode = true;
    }
    return retCode;
}

bool CtiFDR_LodeStarImport::decodeDataRecord(RWCString& aLine, long pointId, double lsMeterMultiplier, double lsMeterOffset, double lsPulseMultiplier, double lsPulseOffset, CtiMultiMsg* multiDispatchMsg)
{
	bool                retCode = false;
    bool                isDataRecordFlag = true;
    bool                dataRecordValidFlag = true;
    RWCString           tempString1;// Will receive each token
    RWCTokenizer        cmdLine(aLine);// Tokenize the string aLine
    RWCString           tokedStr = cmdLine("\r\n");
    char*               tempCharPtr = (char*)tokedStr.data();
    int                 fieldNumber = 1;
    double              intervalValue;
    unsigned            importedQuality;

    /****************************
    * the third header has of the following format
    * sort code (10000000-99999999),interval value,lodestar status code, interval start
    *****************************
    */

    try
    {
        CtiFDRPoint fdrPoint;
        bool pointFound = findPointIdInList(pointId, getReceiveFromList(), fdrPoint);

        double pointMultiplier = 1.0;
        double pointOffset = 0.0;
        if( pointFound )
        {
            pointMultiplier = fdrPoint.getMultiplier();
            pointOffset = fdrPoint.getOffset();
        }

        while( pointId > 0 && getToken(&tempCharPtr,tempString1) && isDataRecordFlag && dataRecordValidFlag )
        {
            if( fieldNumber == 1 )
            {
                long tempSortCode = atol(tempString1);
                if( tempSortCode < 10000000 || tempSortCode > 99999999 )
                {
                    isDataRecordFlag = false;
                }
            }
            else if( fieldNumber == 2 )
            {
                intervalValue = atof(tempString1);
            }
            else if( fieldNumber == 3 )
            {
                importedQuality = ForeignToYukonQuality(tempString1);
            }
            else if( fieldNumber == 4 )
            {
                CtiPointDataMsg* pointData = new CtiPointDataMsg(pointId,intervalValue,importedQuality,AnalogPointType);
                if( tempString1.length() > 0 )
                {
                    RWTime optionalTime = ForeignToYukonTime(tempString1,'A');
                    if( optionalTime == rwEpoch )
                    {
                        pointData->setTime(RWTime(RWDate(1,1,1990)));
                        dataRecordValidFlag = false;
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Could not parse optional interval start timestamp: " << tempString1 << " for Point Id: " << pointId << endl;
                        }
                    }
                    else
                    {
                        pointData->setTime(optionalTime);
                    }
                }
                else
                {
                    pointData->setTime(RWTime(RWDate(1,1,1990)));
                }
                pointData->setTags(TAG_POINT_LOAD_PROFILE_DATA);
                multiDispatchMsg->insert(pointData);
                fieldNumber = 1;
            }
            fieldNumber++;
        }
    }
    catch(...)
    {
        dataRecordValidFlag = false;
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << RWTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    if( dataRecordValidFlag && isDataRecordFlag )
    {
        retCode = true;
    }
    return retCode;
}

bool CtiFDR_LodeStarImport::fillUpMissingTimeStamps(CtiMultiMsg* multiDispatchMsg,const RWTime& savedStartTime,const RWTime& savedStopTime,long lsSecondsPerInterval)
{
    bool returnBool = true;
    RWTime oldTimeStamp = RWTime(RWDate(1,1,1990));
    RWOrdered pointDataList = multiDispatchMsg->getData();
    for(long i=0;i<pointDataList.entries();i++)
    {
        CtiPointDataMsg* currentPointData = (CtiPointDataMsg*)pointDataList[i];
        if( currentPointData->getTime().seconds() <= oldTimeStamp.seconds() )
        {
            currentPointData->setTime(RWTime(savedStartTime.seconds()+(lsSecondsPerInterval*(i+1))));
        }
    }

    if( savedStopTime.seconds() != savedStartTime.seconds()+(pointDataList.entries()*lsSecondsPerInterval)-1 )
    {
        returnBool = false;
    }

    return returnBool;
}

int CtiFDR_LodeStarImport::readConfig( void )
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
        setDriveAndPath(RWCString ("\\yukon\\server\\import"));
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

    setDeleteFileAfterImport(false);
    tempStr = getCparmValueAsString(KEY_DELETE_FILE);
    if (tempStr.length() > 0)
    {
        if (!tempStr.compareTo ("true",RWCString::ignoreCase))
        {
            setDeleteFileAfterImport(true);
        }
    }

    setRenameSaveFileAfterImport(true);
    tempStr = getCparmValueAsString(KEY_RENAME_SAVE_FILE);
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
bool CtiFDR_LodeStarImport::loadTranslationLists()
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
void CtiFDR_LodeStarImport::threadFunctionReadFromFile( void )
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
                _snprintf(fileName, 200, "%s\\%s",getDriveAndPath(),getFileName());
                FindFirstFile(fileName, fileData);

                _snprintf(fileNameAndPath, 250, "%s\\%s",getDriveAndPath(),fileData->cFileName);
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

                        //information obtained from the first header record
                        RWCString&  lsCustomerIdentifier = RWCString();
                        long        pointId             = 0;//determined from the Customer Identifier
                        long        lsChannel           = 0;
                        RWTime&     lsStartTime         = RWTime(RWDate(1,1,1990));
                        RWTime&     lsStopTime          = RWTime(RWDate(1,1,1990));
                        RWCString&  lsDSTFlag           = RWCString("Y");
                        RWCString&  lsInvalidRecordFlag = RWCString("Y");

                        //information obtained from the second header record
                        double  lsMeterStartReading     = 0.0;
                        double  lsMeterStopReading      = 0.0;
                        double  lsMeterMultiplier       = 0.0;
                        double  lsMeterOffset           = 0.0;
                        double  lsPulseMultiplier       = 0.0;
                        double  lsPulseOffset           = 0.0;
                        long    lsSecondsPerInterval    = 0;
                        long    lsUnitOfMeasure         = 0;
                        long    lsBasicUnitCode         = 0;
                        long    lsTimeZone              = 0;
                        double  lsPopulation            = 0.0;
                        double  lsWeight                = 0.0;

                        //information obtained from the third header record
                        RWCString& lsDescriptor         = RWCString();

                        //information obtained from the fourth header record
                        RWTime&     lsTimeStamp         = RWTime(RWDate(1,1,1990));
                        RWCString&  lsOrigin            = RWCString();

                        RWCString savedCustomerIdentifier = RWCString();
                        RWTime savedStartTime = RWTime(RWDate(1,1,1990));
                        RWTime savedStopTime = RWTime(RWDate(1,1,1990));
                        CtiMultiMsg* multiDispatchMsg = new CtiMultiMsg();
                        while( lineCnt < totalLines )
                        {
                            savedCustomerIdentifier = lsCustomerIdentifier;
                            savedStartTime = lsStartTime;
                            savedStopTime = lsStopTime;
                            if( decodeFirstHeaderRecord(recordVector[lineCnt],lsCustomerIdentifier,pointId,lsChannel,lsStartTime,lsStopTime,lsDSTFlag,lsInvalidRecordFlag) )
                            {
                                if( multiDispatchMsg->getCount() > 0 )
                                {
                                    if( fillUpMissingTimeStamps(multiDispatchMsg,savedStartTime,savedStopTime,lsSecondsPerInterval) )
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

                                //reinitialize the information obtained from the 2-4 header records
                                lsMeterStartReading     = 0.0;
                                lsMeterStopReading      = 0.0;
                                lsMeterMultiplier       = 0.0;
                                lsMeterOffset           = 0.0;
                                lsPulseMultiplier       = 0.0;
                                lsPulseOffset           = 0.0;
                                lsSecondsPerInterval    = 0;
                                lsUnitOfMeasure         = 0;
                                lsBasicUnitCode         = 0;
                                lsTimeZone              = 0;
                                lsPopulation            = 0.0;
                                lsWeight                = 0.0;
                                lsDescriptor            = "";
                                lsTimeStamp             = RWTime(RWDate(1,1,1990));
                                lsOrigin                = "";
                            }
                            else if( decodeSecondHeaderRecord(recordVector[lineCnt], lsMeterStartReading, lsMeterStopReading, lsMeterMultiplier,
                                                              lsMeterOffset, lsPulseMultiplier, lsPulseOffset, lsSecondsPerInterval,
                                                              lsUnitOfMeasure, lsBasicUnitCode, lsTimeZone, lsPopulation, lsWeight) ||
                                     decodeThirdHeaderRecord(recordVector[lineCnt], lsDescriptor) ||
                                     decodeFourthHeaderRecord(recordVector[lineCnt], lsTimeStamp, lsOrigin) ||
                                     decodeDataRecord(recordVector[lineCnt], pointId, lsMeterMultiplier, lsMeterOffset, lsPulseMultiplier, lsPulseOffset, multiDispatchMsg) )
                            {
                                //do nothing because the extraction of settings or point values are handled in the decode methods themselves
                            }
                            else
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << "Invalid record type in interface " << getInterfaceName() << " record:" << recordVector[lineCnt] << " line number: " << lineCnt << endl;
                            }
                            lineCnt++;
                        }
                        recordVector.erase(recordVector.begin(), recordVector.end());

                        if( multiDispatchMsg->getCount() > 0 )
                        {
                            if( fillUpMissingTimeStamps(multiDispatchMsg,savedStartTime,savedStopTime,lsSecondsPerInterval) )
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
        lodeStarImportInterface = new CtiFDR_LodeStarImport();

        // now start it up
        return lodeStarImportInterface->run();
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

        lodeStarImportInterface->stop();
        delete lodeStarImportInterface;
        lodeStarImportInterface = 0;

        return 0;
    }


#ifdef __cplusplus
}
#endif


