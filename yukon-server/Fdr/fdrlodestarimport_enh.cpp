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
#include "fdrlodestarimport.h"
#include "fdrlodestarimport_enh.h"

using std::endl;
using std::vector;
using std::string;

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
                            CTILOG_DEBUG(dout, "ENH: Looking for CUST_ID/CHANNEL keyString : "<< keyString);
                        }
                        bool pointFound = findTranslationNameInList(string(keyString), getReceiveFromList(), point);
                        if( pointFound )
                        {
                            _pointId = point.getPointID();
                            if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                            {
                                CTILOG_DEBUG(dout, "ENH: PointID "<< _pointId <<" found in TranslationTable");
                            }
                        }
                        else
                        {
                            CTILOG_ERROR(dout, "Translation for Customer Id: "<< _lsCustomerIdentifier <<" and Channel: "<< _lsChannel <<" from file "<< getFileInfoList()[fileIndex].getLodeStarFileName() <<" was not found");

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
                            CTILOG_ERROR(dout, "Could not parse Lodestar start timestamp: "<< tempString1 <<" for Customer Identifier: "<< _lsCustomerIdentifier);
                        }

                        _lsStopTime = ForeignToYukonTime(tempStopTimeStr, _lsDSTFlag[0]);
                        if( _lsStopTime == PASTDATE )
                        {
                            headerRecordValidFlag = false;
                            CTILOG_ERROR(dout, "Could not parse Lodestar stop timestamp: "<< tempString1 <<" for Customer Identifier: "<< _lsCustomerIdentifier);
                        }
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            Cti::FormattedList loglist;
                            loglist.add("ENH: StartTime") << _lsStartTime;
                            loglist.add("ENH: StopTime")  << _lsStopTime;

                            CTILOG_DEBUG(dout, loglist);
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
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
                            CTILOG_DEBUG(dout, "ENH: MeterStartReading : "<< _lsMeterStartReading);
                        }
                        break;
                    }
                case 3:
                    {
                        _lsMeterStopReading = atof(tempString1.c_str());
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CTILOG_DEBUG(dout, "ENH: MeterStopReading : "<< _lsMeterStopReading);
                        }
                        break;
                    }
                case 4:
                    {
                        _lsMeterMultiplier = atof(tempString1.c_str());
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CTILOG_DEBUG(dout, "ENH: MeterMultiplier : "<< _lsMeterMultiplier);
                        }
                        break;
                    }

                case 5:
                    {
                        _lsMeterOffset = atof(tempString1.c_str());
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CTILOG_DEBUG(dout, "ENH: MeterOffset : "<< _lsMeterOffset);
                        }
                        break;
                    }
                case 6:
                    {
                        _lsPulseMultiplier = atof(tempString1.c_str());
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CTILOG_DEBUG(dout, "ENH: PulseMultiplier : "<< _lsPulseMultiplier);
                        }
                        break;
                    }
                case 7:
                    {
                        _lsPulseOffset = atof(tempString1.c_str());
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CTILOG_DEBUG(dout, "ENH: PulseOffset : " <<_lsPulseOffset);
                        }
                        break;
                    }
                case 8:
                    {
                        _lsSecondsPerInterval = atol(tempString1.c_str());
                        _lsExpectedNumEntries = (_lsStopTime.seconds() -  _lsStartTime.seconds() + getSubtractValue())/_lsSecondsPerInterval;
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            Cti::FormattedList loglist;
                            loglist.add("ENH: SecondsPerInterval")      << _lsSecondsPerInterval;
                            loglist.add("ENH: Num Of Entries Expected") << _lsExpectedNumEntries;

                            CTILOG_DEBUG(dout, loglist);
                        }
                        break;
                    }
                case 9:
                    {
                        _lsUnitOfMeasure = atol(tempString1.c_str());
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CTILOG_DEBUG(dout, "ENH: UnitOfMeasure : " <<_lsUnitOfMeasure);
                        }
                        break;
                    }
                case 10:
                    {
                        _lsBasicUnitCode = atol(tempString1.c_str());
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CTILOG_DEBUG(dout, "ENH: BasicUnitCode : " <<_lsBasicUnitCode);
                        }
                        break;
                    }
                case 11:
                    {
                        _lsTimeZone = atol(tempString1.c_str());
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CTILOG_DEBUG(dout, "ENH: TimeZone : " <<_lsTimeZone);
                        }
                        break;
                    }
                case 12:
                    {
                        _lsPopulation = atof(tempString1.c_str());
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CTILOG_DEBUG(dout, "ENH: Population : " <<_lsPopulation);
                        }
                        break;
                    }
                case 13:
                    {
                        _lsWeight = atof(tempString1.c_str());
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CTILOG_DEBUG(dout, "ENH: Weight : " <<_lsWeight);
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
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
                            CTILOG_DEBUG(dout, "ENH: Descriptor : " <<_lsDescriptor);
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
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
                                CTILOG_DEBUG(dout, "ENH: TimeStamp : "<<_lsTimeStamp);
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
                                CTILOG_DEBUG(dout, "ENH: Origin : "<<_lsOrigin);
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
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
                        CTILOG_ERROR(dout, "Could not parse optional interval start timestamp: "<< tempString1 <<" for Point Id: "<< _pointId);
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
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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




