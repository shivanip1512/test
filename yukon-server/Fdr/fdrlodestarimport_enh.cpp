/*-----------------------------------------------------------------------------*
*
*    FILE NAME: fdrlodestar_enh.cpp
*
*    DATE: 03/22/2004
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrlodestarimport.cpp-arc  $
*    REVISION     :  $Revision: 1.14.20.1 $
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
      $Log: fdrlodestarimport_enh.cpp,v $
      Revision 1.14.20.1  2008/11/13 17:23:47  jmarks
      YUK-5273 Upgrade Yukon tool chain to Visual Studio 2005/2008

      Responded to reviewer comments again.

      I eliminated excess references to windows.h .

      This still left over 100 references to it where "yukon.h" or "precompiled.h" was not obviously included.  Some other chaining of references could still be going on, and of course it is potentially possible that not all the files in the project that include windows.h actually need it - I didn't check for that.

      None-the-less, I than added the NOMINMAX define right before each place where windows.h is still included.
      Special note:  std::min<LONG>(TimeOut, 500); is still required for compilation.

      In this process I occasionally deleted a few empty lines, and when creating the define, also added some.

      This may not have affected every file in the project, but while mega-editing it certainly seemed like it did.

      Revision 1.14  2007/04/10 23:47:20  tspar
      Added even more protection against bad input when tokenizing.

      assigning a string from an iterator at  .end() will assert.

      Revision 1.13  2006/06/07 22:34:04  tspar
      _snprintf  adding .c_str() to all strings. Not having this does not cause compiler errors, but does cause runtime errors. Also tweaks and fixes to FDR due to some differences in STL / RW

      Revision 1.12  2006/06/02 18:17:55  dsutton
      Added support to use the yukon point offset and multiplier on the values
      being imported

      Revision 1.11  2006/05/23 17:17:43  tspar
      bug fix: boost iterator used incorrectly in loop.

      Revision 1.10  2006/02/08 20:12:23  jrichter
      BUG:  Fixed getSubtractValue to compare stopTime seconds format instead of the mistaken startTime.

      Revision 1.9  2005/12/20 17:17:13  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.8  2005/08/17 17:42:48  jrichter
      Merged  changes from 3.1.  handled massive point data with list of multimsg.  handled white space in data record for optional interval time field, handled massively long file format (extended workbuffer to 1500 bytes)

      Revision 1.7  2005/06/15 23:57:21  jrichter
      Corrected DST issue with file that ran over Spring Ahead timeframe...

      Revision 1.6  2005/02/10 23:23:51  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.5  2004/08/18 21:46:01  jrichter
      1.  Added try{} catch(..) blocks to threadReadFromFile function to try and pinpoint where thread was killed.
      2.  Cleared out fileInfoList to get a fresh list of files upon each loadTranslationList call (so files aren't read once the point they reference is deleted from database).
      3.  Added path/filename to translationName, so points located in duplicate files (with different names) are not reprocessed and sent multiple times.

      Revision 1.4  2004/07/14 19:27:27  jrichter
      modified lodestar files to work when fdr is run on systems where yukon is not on c drive.

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
#include "fdrlodestarimport.h"
#include "fdrlodestarimport_enh.h"

CtiFDR_EnhancedLodeStar *enhLodeStarObj;

const CHAR * CtiFDR_EnhancedLodeStar::KEY_INTERVAL = "FDR_ENH_LODESTARIMPORT_INTERVAL";
const CHAR * CtiFDR_EnhancedLodeStar::KEY_FILENAME = "FDR_ENH_LODESTARIMPORT_FILENAME";
const CHAR * CtiFDR_EnhancedLodeStar::KEY_IMPORT_BASE_PATH = "FDR_ENH_LODESTARIMPORT_DRIVE_AND_PATH";
const CHAR * CtiFDR_EnhancedLodeStar::KEY_DB_RELOAD_RATE = "FDR_ENH_LODESTARIMPORT_DB_RELOAD_RATE";
const CHAR * CtiFDR_EnhancedLodeStar::KEY_QUEUE_FLUSH_RATE = "FDR_ENH_LODESTARIMPORT_QUEUE_FLUSH_RATE";
const CHAR * CtiFDR_EnhancedLodeStar::KEY_DELETE_FILE = "FDR_ENH_LODESTARIMPORT_DELETE_FILE";
const CHAR * CtiFDR_EnhancedLodeStar::KEY_RENAME_SAVE_FILE = "FDR_ENH_LODESTARIMPORT_RENAME_SAVE_FILE";


// Constructors, Destructor, and Operators
CtiFDR_EnhancedLodeStar::CtiFDR_EnhancedLodeStar()
: CtiFDR_LodeStarImportBase(string("LODESTAR_ENH")),
  _lsCustomerIdentifier(string()),
  _pointId(0),
  _lsChannel(0),
  _lsStartTime(CtiTime(CtiDate(1,1,1990))),
  _lsStopTime(CtiTime(CtiDate(1,1,1990))),
  _lsDSTFlag(string("Y")),
  _lsInvalidRecordFlag(string("Y")),
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
  _lsDescriptor(string()),
  _lsTimeStamp(CtiTime(CtiDate(1,1,1990))),
  _lsOrigin(string()),
  _fileImportBaseDrivePath(string("c:\\yukon\\server\\import"))
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

string CtiFDR_EnhancedLodeStar::getCustomerIdentifier(void)
{
    return _lsCustomerIdentifier;
}
CtiTime CtiFDR_EnhancedLodeStar::getlodeStarStartTime(void)
{
    return _lsStartTime;
}
CtiTime CtiFDR_EnhancedLodeStar::getlodeStarStopTime(void)
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
    _lsTimeStamp             = CtiTime(CtiDate(1,1,1990));
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
const CHAR * CtiFDR_EnhancedLodeStar::getKeyImportDrivePath()
{
    return KEY_IMPORT_BASE_PATH;
}
const string& CtiFDR_EnhancedLodeStar::getFileImportBaseDrivePath()
{
    return _fileImportBaseDrivePath;
}
const string& CtiFDR_EnhancedLodeStar::setFileImportBaseDrivePath(string importBase)
{
    _fileImportBaseDrivePath = importBase;
    return _fileImportBaseDrivePath;
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
    if (_lsStopTime.second() == 0)
    {
        return 60;
    }
    else // == 59 seconds...
        return 1;
}
int CtiFDR_EnhancedLodeStar::getExpectedNumOfEntries()
{
    return _lsExpectedNumEntries;
}


CtiTime CtiFDR_EnhancedLodeStar::ForeignToYukonTime (string aTime, CHAR aDstFlag)
{//format is 'YYYYMMDDHHMMSS' note: hours are military
    struct tm ts;
    CtiTime retVal;

    if (aTime.length() == 14)
    {
        if (sscanf (aTime.c_str(),
                    "%4ld%2ld%2ld%2ld%2ld%2ld",
                    &ts.tm_year,
                    &ts.tm_mon,
                    &ts.tm_mday,
                    &ts.tm_hour,
                    &ts.tm_min,
                    &ts.tm_sec) != 6)
        {
            retVal = PASTDATE;
        }
        else
        {
            CtiTime beginDST = CtiTime().beginDST(ts.tm_year);
            CtiTime endDST = CtiTime().endDST(ts.tm_year);


            ts.tm_year -= 1900;
            ts.tm_mon--;

            CtiTime tempTime =  CtiTime(&ts);

            if (aDstFlag == 'Y' || aDstFlag == 'y')
            {
               if ( tempTime.seconds() < endDST.seconds() &&
                    tempTime.seconds() >= beginDST.seconds() )
               {
                   ts.tm_isdst = TRUE;
               }
               else
                   ts.tm_isdst = FALSE;
            }
            else
            {
                ts.tm_isdst = FALSE;
            }

            try
            {
                retVal = CtiTime(&ts);

                // if CtiTime can't make a time ???
                if (!retVal.isValid())
                {
                    retVal = PASTDATE;
                }
            }
            catch (...)
            {
                retVal = PASTDATE;
            }
        }
    }
    else
        retVal = PASTDATE;
    return retVal;
}

/**********************
* used to retrieve comma separated data
* strtok wouldn't work because of multiple
* tokens in a row ,,,,
***********************
*/

/*TS*** Replaced with a Boost Tokenizer
bool getToken(char **InBuffer, string &outBuffer)
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
************TS */
bool CtiFDR_EnhancedLodeStar::decodeFirstHeaderRecord(string& aLine, int fileIndex)
{
        bool                retCode = false;
    bool                isFirstHeaderFlag = true;
    bool                headerRecordValidFlag = true;
    string           tempString1;// Will receive each token

    boost::char_separator<char> sep("\r\n");
    Boost_char_tokenizer cmdLine(aLine, sep);
    Boost_char_tokenizer::iterator tok_iter = cmdLine.begin();

    string tokedStr = "";
    if( tok_iter != cmdLine.end() )
    {
        tokedStr = *tok_iter;
    }
    CtiFDRPoint         point;
    int                 fieldNumber = 1;
    string           tempStartTimeStr = "";
    string           tempStopTimeStr = "";

    /****************************
    * the first header has of the following format
    * sort code (00000001),customer identifier,channel,start time,stop time,DST flag,invalid record
    *****************************
    */

    try
    {
        boost::char_separator<char> sep1(",", 0, boost::keep_empty_tokens);
        Boost_char_tokenizer header(tokedStr, sep1);
        Boost_char_tokenizer::iterator iter = header.begin();

        while ( (iter != header.end()) && isFirstHeaderFlag && headerRecordValidFlag)
        {
            tempString1 = *iter;iter++;
            switch (fieldNumber)
            {
                case 1:
                    {
                        if( tempString1 != "00000001" )
                        {
                            isFirstHeaderFlag = false;
                            fieldNumber = 100;
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
                        _lsChannel = atol(tempString1.c_str());
                        CHAR keyString[80];
                        //TS- Error's out
                        //_snprintf(keyString,80,"%s %d %s %s",_lsCustomerIdentifier,_lsChannel, getFileInfoList()[fileIndex].getLodeStarFolderName().c_str(), getFileInfoList()[fileIndex].getLodeStarFileName().c_str());
                        string t1 = getFileInfoList()[fileIndex].getLodeStarFolderName();
                        string t2 = getFileInfoList()[fileIndex].getLodeStarFileName();
                        _snprintf(keyString,80,"%s %d %s %s",_lsCustomerIdentifier.c_str(),_lsChannel, t1.c_str(), t2.c_str());
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " ENH: Looking for CUST_ID/CHANNEL keyString: " <<keyString << "..."<<endl;
                        }
                        bool pointFound = findTranslationNameInList(string(keyString), getReceiveFromList(), point);
                        if( pointFound )
                        {
                            _pointId = point.getPointID();
                            if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " ENH: PointID "<<_pointId<< " found in TranslationTable" <<endl;
                            }
                        }
                        else
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " Translation for Customer Id: " << _lsCustomerIdentifier << " and Channel: " << _lsChannel << " from file " << getFileInfoList()[fileIndex].getLodeStarFileName() << " was not found" << endl;
                            }
                            CHAR tempIdStr[80];
                            CHAR tempChanStr[80];
                            string desc = string ("Lodestar point is not listed in the translation table");
                            _snprintf(tempIdStr,80,"%s", _lsCustomerIdentifier.c_str());
                            _snprintf(tempChanStr,80,"%d", _lsChannel);
                            CHAR tempBigStr[256];
                            _snprintf(tempBigStr,256,"%s%s%s%s", "Customer Id: ",tempIdStr, "; Channel: ", tempChanStr);
                            string action = string(tempBigStr);
                            logEvent (desc,action);
                            _pointId = 0;
                        }
                        break;
                    }
                case 4:
                    {
                        //Can't yet convert the timestamp string to a CtiTime because we don't have the DST flag yet
                        tempStartTimeStr = tempString1;
                        break;
                    }

                case 5:
                    {
                        //Can't yet convert the timestamp string to a CtiTime because we don't have the DST flag yet
                        tempStopTimeStr = tempString1;
                        break;
                    }
                case 6:
                    {
                        _lsDSTFlag = tempString1;

                        //Now we can convert the
                        _lsStartTime = ForeignToYukonTime(tempStartTimeStr, _lsDSTFlag[0]);
                        if( _lsStartTime == PASTDATE )
                        {
                            headerRecordValidFlag = false;
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " Could not parse Lodestar start timestamp: " << tempString1 << " for Customer Identifier: " << _lsCustomerIdentifier << endl;
                            }
                        }

                        _lsStopTime = ForeignToYukonTime(tempStopTimeStr, _lsDSTFlag[0]);
                        if( _lsStopTime == PASTDATE )
                        {
                            headerRecordValidFlag = false;
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " Could not parse Lodestar stop timestamp: " << tempString1 << " for Customer Identifier: " << _lsCustomerIdentifier << endl;
                            }
                        }
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " ENH: StartTime: " <<_lsStartTime << "..."<<endl;
                            dout << CtiTime() << " ENH: StopTime: " <<_lsStopTime << "..."<<endl;
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
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    if( headerRecordValidFlag && isFirstHeaderFlag )
    {
        retCode = true;
    }
    return retCode;
}

bool CtiFDR_EnhancedLodeStar::decodeSecondHeaderRecord(string& aLine)
{
        bool                retCode = false;
    bool                isSecondHeaderFlag = true;
    bool                headerRecordValidFlag = true;
    string           tempString1;// Will receive each token

    boost::char_separator<char> sep("\r\n");
    Boost_char_tokenizer cmdLine(aLine, sep);
    Boost_char_tokenizer::iterator tok_iter = cmdLine.begin();

    string tokedStr = "";
    if( tok_iter != cmdLine.end() )
    {
        tokedStr = *tok_iter;
    }
    int                 fieldNumber = 1;


    /****************************
    * the second header has of the following format
    * sort code (00000002),meter start reading,meter stop reading,meter multiplier,meter offset,pulse multiplier,pulse offset,
    * seconds per interval,unit of measure,basic unit code,time zone,population, weight
    *****************************
    */

    try
    {
        boost::char_separator<char> sep1(",", 0, boost::keep_empty_tokens);
        Boost_char_tokenizer header(tokedStr, sep1);
        Boost_char_tokenizer::iterator iter = header.begin();

        while ((iter != header.end()) && isSecondHeaderFlag && headerRecordValidFlag)
        {
            tempString1 = *iter;iter++;
            switch (fieldNumber)
            {
                case 1:
                    {
                        if( tempString1 != "00000002" )
                        {
                            isSecondHeaderFlag = false;
                            fieldNumber = 100;
                        }
                        break;
                    }
                case 2:
                    {
                        _lsMeterStartReading = atof(tempString1.c_str());
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " ENH: MeterStartReading: " <<_lsMeterStartReading << "..."<<endl;
                        }
                        break;
                    }
                case 3:
                    {
                        _lsMeterStopReading = atof(tempString1.c_str());
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " ENH: MeterStopReading: " <<_lsMeterStopReading << "..."<<endl;
                        }
                        break;
                    }
                case 4:
                    {
                        _lsMeterMultiplier = atof(tempString1.c_str());
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " ENH: MeterMultiplier: " <<_lsMeterMultiplier << "..."<<endl;
                        }
                        break;
                    }

                case 5:
                    {
                        _lsMeterOffset = atof(tempString1.c_str());
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " ENH: MeterOffset: " <<_lsMeterOffset << "..."<<endl;
                        }
                        break;
                    }
                case 6:
                    {
                        _lsPulseMultiplier = atof(tempString1.c_str());
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " ENH: PulseMultiplier: " <<_lsPulseMultiplier << "..."<<endl;
                        }
                        break;
                    }
                case 7:
                    {
                        _lsPulseOffset = atof(tempString1.c_str());
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " ENH: PulseOffset: " <<_lsPulseOffset << "..."<<endl;
                        }
                        break;
                    }
                case 8:
                    {
                        _lsSecondsPerInterval = atol(tempString1.c_str());
                        _lsExpectedNumEntries = (_lsStopTime.seconds() -  _lsStartTime.seconds() + getSubtractValue())/_lsSecondsPerInterval;
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " ENH: SecondsPerInterval: " <<_lsSecondsPerInterval << "..."<<endl;
                            dout << CtiTime() << " ENH: Num Of Entries Expected: " <<_lsExpectedNumEntries << "..."<<endl;
                        }
                        break;
                    }
                case 9:
                    {
                        _lsUnitOfMeasure = atol(tempString1.c_str());
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " ENH: UnitOfMeasure: " <<_lsUnitOfMeasure << "..."<<endl;
                        }
                        break;
                    }
                case 10:
                    {
                        _lsBasicUnitCode = atol(tempString1.c_str());
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " ENH: BasicUnitCode: " <<_lsBasicUnitCode << "..."<<endl;
                        }
                        break;
                    }
                case 11:
                    {
                        _lsTimeZone = atol(tempString1.c_str());
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " ENH: TimeZone: " <<_lsTimeZone << "..."<<endl;
                        }
                        break;
                    }
                case 12:
                    {
                        _lsPopulation = atof(tempString1.c_str());
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " ENH: Population: " <<_lsPopulation << "..."<<endl;
                        }
                        break;
                    }
                case 13:
                    {
                        _lsWeight = atof(tempString1.c_str());
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " ENH: Weight: " <<_lsWeight << "..."<<endl;
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
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    if( headerRecordValidFlag && isSecondHeaderFlag )
    {
        retCode = true;
    }
    return retCode;
}

bool CtiFDR_EnhancedLodeStar::decodeThirdHeaderRecord(string& aLine)
{
        bool                retCode = false;
    bool                isThirdHeaderFlag = true;
    bool                headerRecordValidFlag = true;
    string           tempString1;// Will receive each token
    boost::char_separator<char> sep("\r\n");
    Boost_char_tokenizer cmdLine(aLine, sep);
    Boost_char_tokenizer::iterator tok_iter = cmdLine.begin();

    string tokedStr = "";
    if( tok_iter != cmdLine.end() )
    {
        tokedStr = *tok_iter;
    }
    int                 fieldNumber = 1;


    /****************************
    * the third header has of the following format
    * sort code (00000003),description
    *****************************
    */

    try
    {
        boost::char_separator<char> sep1(",", 0, boost::keep_empty_tokens);
        Boost_char_tokenizer header(tokedStr, sep1);
        Boost_char_tokenizer::iterator iter = header.begin();

        while ((iter != header.end()) && isThirdHeaderFlag && headerRecordValidFlag)
        {
            tempString1 = *iter;iter++;
            switch (fieldNumber)
            {
                case 1:
                    {
                        if( tempString1 != "00000003" )
                        {
                            isThirdHeaderFlag = false;
                            fieldNumber = 100;
                        }
                        break;
                    }
                case 2:
                    {
                        _lsDescriptor = tempString1;
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " ENH: Descriptor: " <<_lsDescriptor << "..."<<endl;
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
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    if( headerRecordValidFlag && isThirdHeaderFlag )
    {
        retCode = true;
    }
    return retCode;
}

bool CtiFDR_EnhancedLodeStar::decodeFourthHeaderRecord(string& aLine)
{
        bool                retCode = false;
    bool                isFourthHeaderFlag = true;
    bool                headerRecordValidFlag = true;
    string           tempString1;// Will receive each token
    boost::char_separator<char> sep("\r\n");
    Boost_char_tokenizer cmdLine(aLine, sep);
    Boost_char_tokenizer::iterator tok_iter = cmdLine.begin();

    string tokedStr = "";
    if( tok_iter != cmdLine.end() )
    {
        tokedStr = *tok_iter;
    }
    int                 fieldNumber = 1;


    /****************************
    * the fourth header has of the following format
    * sort code (00000004),time stamp,origin
    *****************************
    */

    try
    {
        boost::char_separator<char> sep1(",", 0, boost::keep_empty_tokens);
        Boost_char_tokenizer header(tokedStr, sep1);
        Boost_char_tokenizer::iterator iter = header.begin();

        while ((iter != header.end()) && isFourthHeaderFlag && headerRecordValidFlag)
        {
            tempString1 = *iter;iter++;
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
                        if( tempString1.length() > 0 )
                        {
                            CtiTime optionalTime = ForeignToYukonTime(tempString1,'A');
                            _lsTimeStamp = ForeignToYukonTime(tempString1,'A');
                            if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " ENH: TimeStamp: " <<_lsTimeStamp << "..."<<endl;
                            }
                        }
                        break;
                    }
                case 3:
                    {
                        if( tempString1.length() > 0 )
                        {
                            _lsOrigin = tempString1;
                            if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " ENH: Origin: " <<_lsOrigin << "..."<<endl;
                            }
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
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    if( headerRecordValidFlag && isFourthHeaderFlag )
    {
        retCode = true;
    }
    return retCode;
}

bool CtiFDR_EnhancedLodeStar::decodeDataRecord(string& aLine, CtiMultiMsg* multiDispatchMsg)
{
        bool                retCode = false;
    bool                isDataRecordFlag = true;
    bool                dataRecordValidFlag = true;
    string           tempString1;// Will receive each token

    boost::char_separator<char> sep("\r\n");
    Boost_char_tokenizer cmdLine(aLine, sep);
    Boost_char_tokenizer::iterator tok_iter = cmdLine.begin();

    string tokedStr = "";
    if( tok_iter != cmdLine.end() )
    {
        tokedStr = *tok_iter;
    }
    int                 fieldNumber = 1;
    double              intervalValue = 0.0;
    unsigned            importedQuality = 0;

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
        boost::char_separator<char> sep1(",", 0, boost::keep_empty_tokens);
        Boost_char_tokenizer header(tokedStr, sep1);
        Boost_char_tokenizer::iterator iter = header.begin();

        while( _pointId > 0 && (iter != header.end()) && isDataRecordFlag && dataRecordValidFlag )
        {
            tempString1 = *iter;iter++;
            if( fieldNumber == 1 )
            {
                long tempSortCode = atol(tempString1.c_str());
                if( tempSortCode < 10000000 || tempSortCode > 99999999 )
                {
                    isDataRecordFlag = false;
                }
            }
            else if( fieldNumber == 2 )
            {
                intervalValue = atof(tempString1.c_str());

                // apply our multiplier and offset
                intervalValue *= pointMultiplier;
                intervalValue += pointOffset;
            }
            else if( fieldNumber == 3 )
            {
                importedQuality = ForeignToYukonQuality(tempString1);
            }
            else if( fieldNumber == 4 )
            {
                CtiPointDataMsg* pointData = new CtiPointDataMsg(_pointId,intervalValue,importedQuality,fdrPoint.getPointType());
                if( tempString1.length() > 0 && tempString1 != " ")
                {
                    CtiTime optionalTime = ForeignToYukonTime(tempString1,'A');
                    if( optionalTime == PASTDATE )
                    {
                        pointData->setTime(CtiTime(CtiDate(1,1,1990)));
                        dataRecordValidFlag = false;
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " Could not parse optional interval start timestamp: " << tempString1 << " for Point Id: " << _pointId << endl;
                        }
                    }
                    else
                    {
                        pointData->setTime(optionalTime);
                    }
                }
                else
                {
                    pointData->setTime(CtiTime(CtiDate(1,1,1990)));
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
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
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




