#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*-----------------------------------------------------------------------------*
*
*    FILE NAME: fdrlodestar_enh.cpp
*
*    DATE: 03/22/2004
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrlodestarimport.cpp-arc  $
*    REVISION     :  $Revision: 1.3 $
*    DATE         :  $Date: 2004/06/15 19:34:00 $
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
      $Log: fdrlodestarimport_enh.cpp,v $
      Revision 1.3  2004/06/15 19:34:00  jrichter
      Added FDR lodestar tag point def / fixed time stamp issue / modified backup file to append time stamp

      Revision 1.2  2004/04/08 20:03:16  jrichter
      jrichter1 Lodestar changes to handle standard format and files are read in based on point parameters.

      Revision 1.1  2004/04/06 21:10:17  jrichter
      jrichter1 Lodestar changes to handle standard format and files are read in based on point parameters.

     

*
*
*
*    Copyright (C) 2004 Cannon Technologies, Inc.  All rights reserved.
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
#include "fdrlodestarimport_enh.h"

CtiFDR_EnhancedLodeStar *enhLodeStarObj;

const CHAR * CtiFDR_EnhancedLodeStar::KEY_INTERVAL = "FDR_ENH_LODESTARIMPORT_INTERVAL";
const CHAR * CtiFDR_EnhancedLodeStar::KEY_FILENAME = "FDR_ENH_LODESTARIMPORT_FILENAME";
const CHAR * CtiFDR_EnhancedLodeStar::KEY_DRIVE_AND_PATH = "FDR_ENH_LODESTARIMPORT_DRIVE_AND_PATH";
const CHAR * CtiFDR_EnhancedLodeStar::KEY_DB_RELOAD_RATE = "FDR_ENH_LODESTARIMPORT_DB_RELOAD_RATE";
const CHAR * CtiFDR_EnhancedLodeStar::KEY_QUEUE_FLUSH_RATE = "FDR_ENH_LODESTARIMPORT_QUEUE_FLUSH_RATE";
const CHAR * CtiFDR_EnhancedLodeStar::KEY_DELETE_FILE = "FDR_ENH_LODESTARIMPORT_DELETE_FILE";
const CHAR * CtiFDR_EnhancedLodeStar::KEY_RENAME_SAVE_FILE = "FDR_ENH_LODESTARIMPORT_RENAME_SAVE_FILE";


// Constructors, Destructor, and Operators
CtiFDR_EnhancedLodeStar::CtiFDR_EnhancedLodeStar()
: CtiFDR_LodeStarImportBase(RWCString("LODESTAR_ENH")),
  _lsCustomerIdentifier(RWCString()),
  _pointId(0),
  _lsChannel(0),
  _lsStartTime(RWTime(RWDate(1,1,1990))),
  _lsStopTime(RWTime(RWDate(1,1,1990))),
  _lsDSTFlag(RWCString("Y")),
  _lsInvalidRecordFlag(RWCString("Y")),
  _lsMeterStartReading(0.0),
  _lsMeterStopReading(0.0),
  _lsMeterMultiplier(0.0),
  _lsMeterOffset(0.0),
  _lsPulseMultiplier(0.0),
  _lsPulseOffset(0.0),
  _lsSecondsPerInterval(0),
  _lsUnitOfMeasure(0),
  _lsBasicUnitCode(0),
  _lsTimeZone(0),
  _lsPopulation(0.0),
  _lsWeight(0.0),
  _lsDescriptor(RWCString()),
  _lsTimeStamp(RWTime(RWDate(1,1,1990))),
  _lsOrigin(RWCString())  
{ 
    
    init();
    _fileInfoList.empty();
}

CtiFDR_EnhancedLodeStar::~CtiFDR_EnhancedLodeStar()
{
    _fileInfoList.erase (_fileInfoList.begin(),_fileInfoList.end());
}


BOOL CtiFDR_EnhancedLodeStar::init( void )
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
* Function Name: CtiFDR_EnhancedLodeStar::run()
*
* Description: runs the interface
* 
**************************************************
*/
BOOL CtiFDR_EnhancedLodeStar::run( void )
{
    // crank up the base class
    Inherited::run();

    return TRUE;
}


/*************************************************
* Function Name: CtiFDR_EnhancedLodeStar::stop()
*
* Description: stops all threads 
* 
**************************************************
*/
BOOL CtiFDR_EnhancedLodeStar::stop( void )
{
    Inherited::stop();
    return TRUE;
}

vector<CtiFDR_LodeStarInfoTable> CtiFDR_EnhancedLodeStar::getFileInfoList() const
{
    return _fileInfoList;
}
vector< CtiFDR_LodeStarInfoTable > & CtiFDR_EnhancedLodeStar::getFileInfoList ()
{
    return _fileInfoList;
}


/*CtiFDR_EnhancedLodeStar& CtiFDR_EnhancedLodeStar::setFileInfoList  (CtiFDR_EnhancedLodeStar &aList)
{
    _fileInfoList = aList.getFileInfoList();
    return *this;
}

CtiFDR_EnhancedLodeStar& CtiFDR_EnhancedLodeStar::setFileInfoList (vector< CtiFDR_LodeStarInfoTable > &aList)
{
    _fileInfoList = aList;
    return *this;
}
*/

RWCString CtiFDR_EnhancedLodeStar::getCustomerIdentifier(void)
{
    return _lsCustomerIdentifier;
}
RWTime CtiFDR_EnhancedLodeStar::getlodeStarStartTime(void)
{
    return _lsStartTime;
}
RWTime CtiFDR_EnhancedLodeStar::getlodeStarStopTime(void)
{
    return _lsStopTime;
}

long  CtiFDR_EnhancedLodeStar::getlodeStarSecsPerInterval(void)
{
    return _lsSecondsPerInterval;
}
long  CtiFDR_EnhancedLodeStar::getlodeStarPointId(void)
{
    return _pointId;
}

void CtiFDR_EnhancedLodeStar::reinitialize(void)
{
    _lsMeterStartReading     = 0.0;
    _lsMeterStopReading      = 0.0;
    _lsMeterMultiplier       = 0.0;
    _lsMeterOffset           = 0.0;
    _lsPulseMultiplier       = 0.0;
    _lsPulseOffset           = 0.0;
    _lsSecondsPerInterval    = 0;
    _lsUnitOfMeasure         = 0;
    _lsBasicUnitCode         = 0;
    _lsTimeZone              = 0;
    _lsPopulation            = 0.0;
    _lsWeight                = 0.0;
    _lsDescriptor            = "";
    _lsTimeStamp             = RWTime(RWDate(1,1,1990));
    _lsOrigin                = "";
    return;
}

const CHAR * CtiFDR_EnhancedLodeStar::getKeyInterval()
{
    return KEY_INTERVAL;
}
const CHAR * CtiFDR_EnhancedLodeStar::getKeyFilename()
{
    return KEY_FILENAME;
}
const CHAR * CtiFDR_EnhancedLodeStar::getKeyDrivePath()
{
    return KEY_DRIVE_AND_PATH;
}
const CHAR * CtiFDR_EnhancedLodeStar::getKeyDBReloadRate()
{
    return KEY_DB_RELOAD_RATE;
}
const CHAR * CtiFDR_EnhancedLodeStar::getKeyQueueFlushRate()
{
    return KEY_QUEUE_FLUSH_RATE;
}
const CHAR * CtiFDR_EnhancedLodeStar::getKeyDeleteFile()
{
    return KEY_DELETE_FILE;
}
const CHAR * CtiFDR_EnhancedLodeStar::getKeyRenameSave()
{
    return KEY_RENAME_SAVE_FILE;
}
int CtiFDR_EnhancedLodeStar::getSubtractValue()
{
    return 1;
}
int CtiFDR_EnhancedLodeStar::getExpectedNumOfEntries()
{
    return _lsExpectedNumEntries;
}


RWTime CtiFDR_EnhancedLodeStar::ForeignToYukonTime (RWCString aTime, CHAR aDstFlag)
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

bool CtiFDR_EnhancedLodeStar::decodeFirstHeaderRecord(RWCString& aLine)
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
                        _lsCustomerIdentifier = tempString1;
                        break;
                    }
                case 3:
                    {
                        _lsChannel = atol(tempString1);
                        CHAR keyString[80];
                        //_snprintf(keyString,80,"%s %d %s %s",_lsCustomerIdentifier,_lsChannel,getDriveAndPath(),getFileName());
                        _snprintf(keyString,80,"%s %d",_lsCustomerIdentifier,_lsChannel);
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " ENH: Looking for CUST_ID/CHANNEL keyString: " <<keyString << "..."<<endl;
                        }  
                        bool pointFound = findTranslationNameInList(RWCString(keyString), getReceiveFromList(), point);
                        if( pointFound )
                        {
                            _pointId = point.getPointID();
                            if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " ENH: PointID "<<_pointId<< " found in TranslationTable" <<endl;
                            }
                        }
                        else
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " Translation for Customer Id: " << _lsCustomerIdentifier << " and Channel: " << _lsChannel << " from file " << getFileName() << " was not found" << endl;
                            }
                            CHAR tempIdStr[80];
                            CHAR tempChanStr[80];
                            RWCString desc = RWCString ("Lodestar point is not listed in the translation table");
                            _snprintf(tempIdStr,80,"%s", _lsCustomerIdentifier);
                            _snprintf(tempChanStr,80,"%d", _lsChannel);
                            CHAR tempBigStr[256];
                            _snprintf(tempBigStr,256,"%s%s%s%s", "Customer Id: ",tempIdStr, "; Channel: ", tempChanStr);
                            RWCString action = RWCString(tempBigStr);
                            logEvent (desc,action);
                            _pointId = 0;
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
                        _lsDSTFlag = tempString1;
    
                        //Now we can convert the 
                        _lsStartTime = ForeignToYukonTime(tempStartTimeStr, _lsDSTFlag.data()[0]);
                        if( _lsStartTime == rwEpoch )
                        {
                            headerRecordValidFlag = false;
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " Could not parse Lodestar start timestamp: " << tempString1 << " for Customer Identifier: " << _lsCustomerIdentifier << endl;
                            }
                        }
    
                        _lsStopTime = ForeignToYukonTime(tempStopTimeStr, _lsDSTFlag.data()[0]);
                        if( _lsStopTime == rwEpoch )
                        {
                            headerRecordValidFlag = false;
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " Could not parse Lodestar stop timestamp: " << tempString1 << " for Customer Identifier: " << _lsCustomerIdentifier << endl;
                            }
                        }
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " ENH: StartTime: " <<_lsStartTime << "..."<<endl;
                            dout << RWTime() << " ENH: StopTime: " <<_lsStopTime << "..."<<endl;
                        }
                        break;
                    }
                case 7:
                    {
                        _lsInvalidRecordFlag = tempString1;
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

bool CtiFDR_EnhancedLodeStar::decodeSecondHeaderRecord(RWCString& aLine)
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
                        _lsMeterStartReading = atof(tempString1);
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " ENH: MeterStartReading: " <<_lsMeterStartReading << "..."<<endl;
                        }
                        break;
                    }
                case 3:
                    {
                        _lsMeterStopReading = atof(tempString1);
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " ENH: MeterStopReading: " <<_lsMeterStopReading << "..."<<endl;
                        }
                        break;
                    }
                case 4:
                    {
                        _lsMeterMultiplier = atof(tempString1);
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " ENH: MeterMultiplier: " <<_lsMeterMultiplier << "..."<<endl;
                        }
                        break;
                    }
    
                case 5:
                    {
                        _lsMeterOffset = atof(tempString1);
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " ENH: MeterOffset: " <<_lsMeterOffset << "..."<<endl;
                        }
                        break;
                    }
                case 6:
                    {
                        _lsPulseMultiplier = atof(tempString1);
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " ENH: PulseMultiplier: " <<_lsPulseMultiplier << "..."<<endl;
                        }
                        break;
                    }
                case 7:
                    {
                        _lsPulseOffset = atof(tempString1);
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " ENH: PulseOffset: " <<_lsPulseOffset << "..."<<endl;
                        }
                        break;
                    }
                case 8:
                    {
                        _lsSecondsPerInterval = atol(tempString1);
                        _lsExpectedNumEntries = (_lsStopTime.seconds() -  _lsStartTime.seconds() + getSubtractValue())/_lsSecondsPerInterval;
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " ENH: SecondsPerInterval: " <<_lsSecondsPerInterval << "..."<<endl;
                            dout << RWTime() << " ENH: Num Of Entries Expected: " <<_lsExpectedNumEntries << "..."<<endl;
                        }
                        break;
                    }
                case 9:
                    {
                        _lsUnitOfMeasure = atol(tempString1);
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " ENH: UnitOfMeasure: " <<_lsUnitOfMeasure << "..."<<endl;
                        }
                        break;
                    }
                case 10:
                    {
                        _lsBasicUnitCode = atol(tempString1);
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " ENH: BasicUnitCode: " <<_lsBasicUnitCode << "..."<<endl;
                        }
                        break;
                    }
                case 11:
                    {
                        _lsTimeZone = atol(tempString1);
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " ENH: TimeZone: " <<_lsTimeZone << "..."<<endl;
                        }
                        break;
                    }
                case 12:
                    {
                        _lsPopulation = atof(tempString1);
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " ENH: Population: " <<_lsPopulation << "..."<<endl;
                        }
                        break;
                    }
                case 13:
                    {
                        _lsWeight = atof(tempString1);
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " ENH: Weight: " <<_lsWeight << "..."<<endl;
                        }
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

bool CtiFDR_EnhancedLodeStar::decodeThirdHeaderRecord(RWCString& aLine)
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
                        _lsDescriptor = tempString1;
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " ENH: Descriptor: " <<_lsDescriptor << "..."<<endl;
                        }
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

bool CtiFDR_EnhancedLodeStar::decodeFourthHeaderRecord(RWCString& aLine)
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
                        _lsTimeStamp = ForeignToYukonTime(tempString1,'A');
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " ENH: TimeStamp: " <<_lsTimeStamp << "..."<<endl;
                        }
                        break;
                    }
                case 3:
                    {
                        _lsOrigin = tempString1;
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " ENH: Origin: " <<_lsOrigin << "..."<<endl;
                        }
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

bool CtiFDR_EnhancedLodeStar::decodeDataRecord(RWCString& aLine, CtiMultiMsg* multiDispatchMsg)
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
        bool pointFound = findPointIdInList(_pointId, getReceiveFromList(), fdrPoint);

        double pointMultiplier = 1.0;
        double pointOffset = 0.0;
        if( pointFound )
        {
            pointMultiplier = fdrPoint.getMultiplier();
            pointOffset = fdrPoint.getOffset();
        }

        while( _pointId > 0 && getToken(&tempCharPtr,tempString1) && isDataRecordFlag && dataRecordValidFlag )
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
                CtiPointDataMsg* pointData = new CtiPointDataMsg(_pointId,intervalValue,importedQuality,fdrPoint.getPointType());
                if( tempString1.length() > 0 )
                {
                    RWTime optionalTime = ForeignToYukonTime(tempString1,'A');
                    if( optionalTime == rwEpoch )
                    {
                        pointData->setTime(RWTime(RWDate(1,1,1990)));
                        dataRecordValidFlag = false;
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Could not parse optional interval start timestamp: " << tempString1 << " for Point Id: " << _pointId << endl;
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
        enhLodeStarObj = new CtiFDR_EnhancedLodeStar();

        // now start it up
        return enhLodeStarObj->run();
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

        enhLodeStarObj->stop();
        delete enhLodeStarObj;
        enhLodeStarObj = 0;

        return 0;
    }


#ifdef __cplusplus
}
#endif




