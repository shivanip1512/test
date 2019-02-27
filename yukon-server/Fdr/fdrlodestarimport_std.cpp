#include "precompiled.h"

#include <wininet.h>
#include <fcntl.h>
#include <io.h>

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
#include "fdrlodestarimport_std.h"

using std::string;
using std::endl;
using std::vector;

CtiFDR_StandardLodeStar *stdLodeStarObj;

const CHAR * CtiFDR_StandardLodeStar::KEY_INTERVAL = "FDR_STD_LODESTARIMPORT_INTERVAL";
const CHAR * CtiFDR_StandardLodeStar::KEY_FILENAME = "FDR_STD_LODESTARIMPORT_FILENAME";
const CHAR * CtiFDR_StandardLodeStar::KEY_IMPORT_BASE_PATH = "FDR_STD_LODESTARIMPORT_DRIVE_AND_PATH";
const CHAR * CtiFDR_StandardLodeStar::KEY_DB_RELOAD_RATE = "FDR_STD_LODESTARIMPORT_DB_RELOAD_RATE";
const CHAR * CtiFDR_StandardLodeStar::KEY_QUEUE_FLUSH_RATE = "FDR_STD_LODESTARIMPORT_QUEUE_FLUSH_RATE";
const CHAR * CtiFDR_StandardLodeStar::KEY_DELETE_FILE = "FDR_STD_LODESTARIMPORT_DELETE_FILE";
const CHAR * CtiFDR_StandardLodeStar::KEY_RENAME_SAVE_FILE = "FDR_STD_LODESTARIMPORT_RENAME_SAVE_FILE";

// Constructors, Destructor, and Operators
CtiFDR_StandardLodeStar::CtiFDR_StandardLodeStar()
: CtiFDR_LodeStarImportBase(string("LODESTAR_STD")),
    _stdLsCustomerIdentifier(string()),
    _pointId(0),
    _stdLsChannel(0),
    _stdLsStartTime(CtiTime(CtiDate(1,1,1990))),
    _stdLsStopTime(CtiTime(CtiDate(1,1,1990))),
    _stdLsIntervalsPerHour(0),
    _stdLsUnitOfMeasure(0),
    _stdLsAltFormat(0),
    _stdLsFiller(string()),
    _stdLsSecondsPerInterval(0),
    _stdLsMeterStartReading(0.0),
    _stdLsMeterStopReading(0.0),
    _stdLsMeterMultiplier(0.0),
    _stdLsMeterOffset(0.0),
    _stdLsPulseMultiplier(0.0),
    _stdLsPulseOffset(0.0),
    _stdLsDescriptor(string()),
    _stdLsAltPulseMultiplier(0.0),
    _stdLsPopulation(0.0),
    _stdLsWeight(0.0),
    _fileImportBaseDrivePath(string("c:\\yukon\\server\\import"))
{
    init();
}

BOOL CtiFDR_StandardLodeStar::init( void )
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
* Function Name: CtiFDR_StandardLodeStar::run()
*
* Description: runs the interface
*
**************************************************
*/
BOOL CtiFDR_StandardLodeStar::run( void )
{
    // crank up the base class
    Inherited::run();
    return TRUE;
}


/*************************************************
* Function Name: CtiFDR_StandardLodeStar::stop()
*
* Description: stops all threads
*
**************************************************
*/
BOOL CtiFDR_StandardLodeStar::stop( void )
{
    Inherited::stop();
    return TRUE;
}

vector<CtiFDR_LodeStarInfoTable> CtiFDR_StandardLodeStar::getFileInfoList() const
{
    return _fileInfoList;
}
vector< CtiFDR_LodeStarInfoTable > & CtiFDR_StandardLodeStar::getFileInfoList ()
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

string CtiFDR_StandardLodeStar::getCustomerIdentifier(void)
{
    return _stdLsCustomerIdentifier;
}
CtiTime CtiFDR_StandardLodeStar::getlodeStarStartTime(void)
{
    return _stdLsStartTime;
}
CtiTime CtiFDR_StandardLodeStar::getlodeStarStopTime(void)
{
    return _stdLsStopTime;
}

long  CtiFDR_StandardLodeStar::getlodeStarSecsPerInterval(void)
{
    return _stdLsSecondsPerInterval;
}
long  CtiFDR_StandardLodeStar::getlodeStarPointId(void)
{
    return _pointId;
}

void CtiFDR_StandardLodeStar::reinitialize(void)
{
    _stdLsFiller                 = "";
    _stdLsMeterStartReading     = 0.0;
    _stdLsMeterStopReading      = 0.0;
    _stdLsMeterMultiplier       = 0.0;
    _stdLsMeterOffset           = 0.0;
    _stdLsPulseMultiplier       = 0.0;
    _stdLsPulseOffset           = 0.0;
    _stdLsAltPulseMultiplier    = 0.0;
    _stdLsDescriptor             = "";
    _stdLsPopulation            = 0.0;
    _stdLsWeight                = 0.0;
    return;
}

const CHAR * CtiFDR_StandardLodeStar::getKeyInterval()
{
    return KEY_INTERVAL;
}
const CHAR * CtiFDR_StandardLodeStar::getKeyFilename()
{
    return KEY_FILENAME;
}
const CHAR * CtiFDR_StandardLodeStar::getKeyImportDrivePath()
{
    return KEY_IMPORT_BASE_PATH;
}
const string& CtiFDR_StandardLodeStar::getFileImportBaseDrivePath()
{
    return _fileImportBaseDrivePath;
}
const string& CtiFDR_StandardLodeStar::setFileImportBaseDrivePath(string importBase)
{
    _fileImportBaseDrivePath = importBase;
    return _fileImportBaseDrivePath;
}
const CHAR * CtiFDR_StandardLodeStar::getKeyDBReloadRate()
{
    return KEY_DB_RELOAD_RATE;
}
const CHAR * CtiFDR_StandardLodeStar::getKeyQueueFlushRate()
{
    return KEY_QUEUE_FLUSH_RATE;
}
const CHAR * CtiFDR_StandardLodeStar::getKeyDeleteFile()
{
    return KEY_DELETE_FILE;
}
const CHAR * CtiFDR_StandardLodeStar::getKeyRenameSave()
{
    return KEY_RENAME_SAVE_FILE;
}
int CtiFDR_StandardLodeStar::getSubtractValue()
{
    return 60;
}
int CtiFDR_StandardLodeStar::getExpectedNumOfEntries()
{
    return _stdLsExpectedNumEntries;
}



CtiTime CtiFDR_StandardLodeStar::ForeignToYukonTime (string aTime, CHAR aDstFlag)
{
    /* JULIE: std lodestar format is MMDDYYHHMM */
    struct tm ts;
    CtiTime retVal;

    if (aTime.length() == 10)
    {
        if (sscanf (aTime.c_str(),
                    "%2ld%2ld%2ld%2ld%2ld",
                    &ts.tm_mon,
                    &ts.tm_mday,
                    &ts.tm_year,
                    &ts.tm_hour,
                    &ts.tm_min/*&ts.tm_sec*/) != 5)
        {
            retVal = PASTDATE;
        }
        else
        {
            CtiTime beginDST = CtiTime().beginDST(ts.tm_year + 2000);
            CtiTime endDST = CtiTime().endDST(ts.tm_year + 2000);

            ts.tm_year += (2000 - 1900);   // std lodestar year is 2 digit.
                                           // 02 + 2000 - 1900 = 102yrs from 1900
            ts.tm_mon--;
            ts.tm_sec = 0;

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

bool CtiFDR_StandardLodeStar::decodeFirstHeaderRecord(string& aLine, int fileIndex)
{
    bool                retCode = false;
    bool                isFirstHeaderFlag = true;
    bool                headerRecordValidFlag = true;
    string           tempString1;
    char tempTest[40];
    char temp[1];
    const char          *tempCharPtr;
    CtiFDRPoint         point;
    int                 fieldNumber = 1;
    string           tempStartTimeStr;
    string           tempStopTimeStr;
    bool tmppointFound = true;

    /****************************
    * the first header has of the following format
    * sort code (0001),customer identifier,channel,start time,stop time,intervals-per-hour,
    * unit-of-measure, alternate format, filler
    *****************************
    */

    try
    {
        tempCharPtr = aLine.c_str();
        while (tmppointFound == true && (fieldNumber <= 9) && isFirstHeaderFlag && headerRecordValidFlag/* && (*tempCharPtr != '\0')*/)
        {
            switch (fieldNumber)
            {
                case 1:
                    {
                        strncpy(tempTest, tempCharPtr, 4);
                        tempTest[4] = '\0';
                        tempString1 = (string) tempTest;
                        if( tempString1 != "0001" )
                        {
                            isFirstHeaderFlag = false;
                            fieldNumber = 100;
                        }
                        tempCharPtr += 4;
                        break;
                    }
                case 2:
                    {
                        strncpy(tempTest, tempCharPtr, 20);
                        tempTest[20] = '\0';
                        _stdLsCustomerIdentifier = (string) tempTest;
                        tempCharPtr += 20;
                        break;
                    }
                case 3:
                    {
                        strncpy(tempTest, tempCharPtr, 1);
                        tempTest[1] = '\0';
                        _stdLsChannel = atol(tempTest);


                        boost::char_separator<char> sep(" ");
                        Boost_char_tokenizer tokenizer(_stdLsCustomerIdentifier, sep);
                        Boost_char_tokenizer::iterator tok_iter = tokenizer.begin();

                        string      tokenStrPartCID;// = tokenizer(" ");
                        string      tokenStr = "";
                        bool firstTime = true;
                        while ( tok_iter != tokenizer.end() )
                        {
                            tokenStrPartCID = *tok_iter;tok_iter++;
                            if (firstTime)
                            {
                                tokenStr = tokenStrPartCID;
                                firstTime = false;
                            }
                            else
                            {
                                tokenStr = tokenStr + " " + tokenStrPartCID;
                            }
                        }
                        CHAR keyString[80];
                        _snprintf(keyString,80,"%s %d %s %s",tokenStr.c_str(),_stdLsChannel, getFileInfoList()[fileIndex].getLodeStarFolderName().c_str(), getFileInfoList()[fileIndex].getLodeStarFileName().c_str());
                        //_snprintf(keyString,80,"%s %d %s %s",tokenStrPartCID,_stdLsChannel,getDriveAndPath(),getFileName());
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CTILOG_DEBUG(dout, "STD: Looking for CUST_ID/CHANNEL keyString : "<< keyString);
                        }
                        bool pointFound = findTranslationNameInList(string(keyString), getReceiveFromList(), point);
                        if( pointFound )
                        {
                            _pointId = point.getPointID();
                            if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                            {
                                CTILOG_DEBUG(dout, "STD: PointID "<< _pointId <<" found in TranslationTable");
                            }
                        }
                        else
                        {
                            CTILOG_ERROR(dout, "Translation for Customer Id: "<< _stdLsCustomerIdentifier <<
                                    " and Channel: "<< _stdLsChannel <<" from file "<< getFileInfoList()[fileIndex].getLodeStarFileName() <<
                                    " was not found");

                            CHAR tempIdStr[80];
                            CHAR tempChanStr[80];
                            string desc = string ("Lodestar point is not listed in the translation table");
                            _snprintf(tempIdStr,80,"%s", _stdLsCustomerIdentifier.c_str());
                            _snprintf(tempChanStr,80,"%d", _stdLsChannel);
                            CHAR tempBigStr[256];
                            _snprintf(tempBigStr,256,"%s%s%s%s", "Customer Id: ",tempIdStr, "; Channel: ", tempChanStr);
                            string action = string(tempBigStr);
                            logEvent (desc,action);
                            _pointId = 0;
                            tmppointFound = false;
                        }
                        tempCharPtr += 1;
                        break;
                    }
                case 4:
                    {
                        //Can't yet convert the timestamp string to a CtiTime because we don't have the DST flag yet
                        strncpy(tempTest, tempCharPtr, 10);
                        tempTest[10] = '\0';
                        tempStartTimeStr = (string) tempTest;
                        tempCharPtr += 10;
                        break;
                    }

                case 5:
                    {
                        //Can't yet convert the timestamp string to a CtiTime because we don't have the DST flag yet
                        strncpy(tempTest, tempCharPtr, 10);
                        tempTest[10] = '\0';
                        tempStopTimeStr =  (string) tempTest;
                        tempCharPtr += 10;
                        break;
                    }
                case 6:
                    {
                        strncpy(tempTest, tempCharPtr, 2);
                        tempTest[2] = '\0';
                        _stdLsIntervalsPerHour = atol(tempTest);
                        _stdLsSecondsPerInterval = ((float) (1/(float)_stdLsIntervalsPerHour))*3600;
                        tempCharPtr += 2;
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            Cti::FormattedList loglist;
                            loglist.add("STD: IntervalsPerHour")   << _stdLsIntervalsPerHour;
                            loglist.add("STD: SecondsPerInterval") << _stdLsSecondsPerInterval;

                            CTILOG_DEBUG(dout, loglist);
                        }

                        string stdLsDSTFlag = "Y";
                        //Now we can convert the
                        _stdLsStartTime = ForeignToYukonTime(tempStartTimeStr, stdLsDSTFlag[0]);
                        if( _stdLsStartTime == PASTDATE )
                        {
                            headerRecordValidFlag = false;
                            CTILOG_ERROR(dout, "Could not parse Lodestar start timestamp: "<< tempString1 <<" for Customer Identifier: "<< _stdLsCustomerIdentifier);
                        }

                        _stdLsStopTime = ForeignToYukonTime(tempStopTimeStr, stdLsDSTFlag[0]);
                        if( _stdLsStopTime == PASTDATE )
                        {
                            headerRecordValidFlag = false;
                            CTILOG_ERROR(dout, "Could not parse Lodestar stop timestamp: "<< tempString1 <<" for Customer Identifier: "<< _stdLsCustomerIdentifier);
                        }
                        _stdLsExpectedNumEntries = (_stdLsStopTime.seconds() - _stdLsStartTime.seconds() + getSubtractValue())/_stdLsSecondsPerInterval;
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            Cti::FormattedList loglist;
                            loglist.add("STD: StartTime")               <<_stdLsStartTime;
                            loglist.add("STD: StopTime")                <<_stdLsStopTime;
                            loglist.add("STD: Num Of Entries Expected") <<_stdLsExpectedNumEntries;

                            CTILOG_DEBUG(dout, loglist);
                        }
                        break;
                    }
                case 7:
                    {
                        strncpy(tempTest, tempCharPtr, 2);
                        tempTest[2] = '\0';
                        _stdLsUnitOfMeasure = atol(tempTest);
                        tempCharPtr += 2;
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CTILOG_DEBUG(dout, "STD: UnitOfMeasure : "<< _stdLsUnitOfMeasure);
                        }
                        break;
                    }
                case 8:
                    {
                        strncpy(tempTest, tempCharPtr, 1);
                        tempTest[1] = '\0';
                        _stdLsAltFormat = atol(tempTest);
                        tempCharPtr += 1;
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CTILOG_DEBUG(dout, "STD: AltFormat : "<< _stdLsAltFormat);
                        }
                        break;
                    }

                case 9:
                    {
                        int count = 0;
                        while (*tempCharPtr != '\0' && count < 30)
                        {
                            strncpy(temp, tempCharPtr, 1);
                            tempTest[count] = temp[0];
                            tempCharPtr+=1;
                            count++;
                        }
                        tempTest[count] = '\0';
                        _stdLsFiller = (string) tempTest;
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CTILOG_DEBUG(dout, "STD: Filler : "<< _stdLsFiller);
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

    if( headerRecordValidFlag && isFirstHeaderFlag )
    {
        retCode = true;
    }

   return retCode;
}

bool CtiFDR_StandardLodeStar::decodeSecondHeaderRecord(string& aLine)
{
    bool                retCode = false;
    bool                isSecondHeaderFlag = true;
    bool                headerRecordValidFlag = true;
    string           tempString1;
    char tempTest[40];
    char temp = 0;
    const char*         tempCharPtr;
    char                *tempStr;
    int                 fieldNumber = 1;


    /****************************
    * the second header has of the following format
    * sort code (0002),meter start reading,meter stop reading,meter multiplier,meter offset,pulse multiplier,pulse offset,
    * seconds per interval,unit of measure,basic unit code,time zone,population, weight
    *****************************
    */

    try
    {
        CtiFDRPoint fdrPoint;
        bool pointFound = findPointIdInList(_pointId, getReceiveFromList(), fdrPoint);

        tempCharPtr = aLine.c_str();
        while (pointFound && (fieldNumber <= 7) && isSecondHeaderFlag && headerRecordValidFlag  && (*tempCharPtr != '\0'))
        {
            switch (fieldNumber)
            {
                case 1:
                    {
                        strncpy(tempTest, tempCharPtr, 4);
                        tempTest[4] = '\0';
                        tempString1 = (string) tempTest;
                        if(tempString1 != "0002" )
                        {
                            isSecondHeaderFlag = false;
                            fieldNumber = 100;
                        }
                        tempCharPtr += 4;
                        break;
                    }
                case 2:
                    {
                        strncpy(tempTest, tempCharPtr, 7);
                        tempTest[7] = '\0';
                        _stdLsMeterStartReading = (atof(tempTest)) *(10e-2);                        tempCharPtr += 7;
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CTILOG_DEBUG(dout, "STD: MeterStartReading : "<< _stdLsMeterStartReading);
                        }
                        break;
                    }
                case 3:
                    {
                        strncpy(tempTest, tempCharPtr, 7);
                        tempTest[7] = '\0';
                        _stdLsMeterStopReading = (atof(tempTest)) *(10e-2);
                        tempCharPtr += 7;
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CTILOG_DEBUG(dout, "STD: MeterStopReading : "<< _stdLsMeterStopReading);
                        }
                        break;
                    }
                case 4:
                    {
                        strncpy(tempTest, tempCharPtr, 15);
                        tempTest[15] = '\0';
                        _stdLsMeterMultiplier = (atof(tempTest)) *(10e-6);
                        tempCharPtr += 15;
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CTILOG_DEBUG(dout, "STD: MeterMultiplier : "<< _stdLsMeterMultiplier);
                        }
                        break;
                    }

                case 5:
                    {
                        strncpy(tempTest, tempCharPtr, 15);
                        tempTest[15] = '\0';
                        _stdLsPulseMultiplier = (atof(tempTest)) *(10e-6);
                        tempCharPtr += 15;
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CTILOG_DEBUG(dout, "STD: PulseMultiplier : "<< _stdLsPulseMultiplier);
                        }
                        break;
                    }
                case 6:
                    {
                        strncpy(tempTest, tempCharPtr, 16);
                        tempTest[16] = '\0';
                        _stdLsMeterOffset = (atof(tempTest)) *(10e-6);
                        tempCharPtr += 16;
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CTILOG_DEBUG(dout, "STD: MeterOffset : "<< _stdLsMeterOffset);
                        }
                        break;
                    }
                case 7:
                    {
                        strncpy(tempTest, tempCharPtr, 16);
                        tempTest[16] = '\0';
                        _stdLsPulseOffset = (atof(tempTest)) *(10e-6);
                        tempCharPtr += 16;
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CTILOG_DEBUG(dout, "STD: PulseOffset : "<< _stdLsPulseOffset);
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

bool CtiFDR_StandardLodeStar::decodeThirdHeaderRecord(string& aLine)
{
    bool                retCode = false;
    bool                isThirdHeaderFlag = true;
    bool                headerRecordValidFlag = true;
    string           tempString1;
    char tempTest[40];
    char temp = 0;
    const char*               tempCharPtr;
    char                *tempStr;
    int                 fieldNumber = 1;


    /****************************
    * the third header has of the following format
    * sort code (0003),description
    *****************************
    */

    try
    {
        CtiFDRPoint fdrPoint;
        bool pointFound = findPointIdInList(_pointId, getReceiveFromList(), fdrPoint);

        tempCharPtr = aLine.c_str();

        while (pointFound && (fieldNumber <= 5) && isThirdHeaderFlag && headerRecordValidFlag && (*tempCharPtr != '\0'))
        {
            switch (fieldNumber)
            {
                case 1:
                    {
                        strncpy(tempTest, tempCharPtr, 4);
                        tempTest[4] = '\0';
                        tempString1 = (string) tempTest;
                        if( tempString1 != "0003" )
                        {
                            isThirdHeaderFlag = false;
                            fieldNumber = 100;
                        }
                        tempCharPtr += 4;
                        break;
                    }
                case 2:
                    {
                        strncpy(tempTest, tempCharPtr, 40);
                        tempTest[39] = '\0';
                        _stdLsDescriptor = (string) tempTest;
                        tempCharPtr += 40;
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CTILOG_DEBUG(dout, "STD: Descriptor : " <<_stdLsDescriptor);
                        }
                        break;
                    }
                case 3:
                    {
                        strncpy(tempTest, tempCharPtr, 15);
                        tempTest[15] = '\0';
                        _stdLsAltPulseMultiplier = (atof(tempTest)) *(10e-16);
                        tempCharPtr += 15;
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CTILOG_DEBUG(dout, "STD: AltPulseMultiplier : "<<_stdLsAltPulseMultiplier);
                        }
                        break;
                    }
                case 4:
                    {
                        strncpy(tempTest, tempCharPtr, 9);
                        tempTest[9] = '\0';
                        _stdLsPopulation = atof(tempTest);
                        tempCharPtr += 9;
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CTILOG_DEBUG(dout, "STD: Population : " <<_stdLsPopulation);
                        }
                        break;
                    }
                case 5:
                    {
                        strncpy(tempTest, tempCharPtr, 12);
                        tempTest[12] = '\0';
                        _stdLsWeight = (atof(tempTest))*(10e-6);
                        tempCharPtr += 12;
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CTILOG_DEBUG(dout, "STD: Weight : " <<_stdLsWeight);
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

bool CtiFDR_StandardLodeStar::decodeFourthHeaderRecord(string& aLine)
{
    bool                retCode = false;
    bool                isFourthHeaderFlag = true;
    bool                headerRecordValidFlag = true;
    string           tempString1;
    char tempTest[41];
    char temp[1];
    char temp2 = 0;

    const char*         tempCharPtr;
    char                *tempStr;
    int                 fieldNumber = 1;


    /****************************
    * the fourth header has of the following format
    * sort code (0004),time stamp,origin
    *****************************
    */

    try
    {
        CtiFDRPoint fdrPoint;
        bool pointFound = findPointIdInList(_pointId, getReceiveFromList(), fdrPoint);

        tempCharPtr = aLine.c_str();

        while (pointFound && (fieldNumber <= 3) && isFourthHeaderFlag && headerRecordValidFlag && (*tempCharPtr != '\0'))
        {
            switch (fieldNumber)
            {
                case 1:
                    {
                        strncpy(tempTest, tempCharPtr, 4);
                        tempTest[4] = '\0';
                        tempString1 = (string) tempTest;
                        if( tempString1 != "0004" )
                        {
                            isFourthHeaderFlag = false;
                        }
                        tempCharPtr+=4;
                        break;
                    }
                case 2:
                    {
                        int count = 0;
                        while (*tempCharPtr != '\0' && count < 40)
                        {
                            strncpy(temp, tempCharPtr, 1);
                            tempTest[count] = temp[0];
                            tempCharPtr+=1;
                            count++;
                        }
                        tempTest[count] = '\0';
                        _stdLsDescriptor = (string) tempTest;
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CTILOG_DEBUG(dout, "STD: Descriptor : " <<_stdLsDescriptor);
                        }
                        break;
                    }
                case 3:
                    {
                        int count = 0;
                        while (*tempCharPtr != '\0' && count < 30)
                        {
                            strncpy(temp, tempCharPtr, 1);
                            tempTest[count] = temp[0];
                            tempCharPtr+=1;
                            count++;
                        }
                        tempTest[count] = '\0';
                        _stdLsFiller = (string) tempTest;
                        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                        {
                            CTILOG_DEBUG(dout, "STD: Filler : " <<_stdLsFiller);
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

bool CtiFDR_StandardLodeStar::decodeDataRecord(string& aLine, CtiMultiMsg* multiDispatchMsg)
{
    bool                retCode = false;
    bool                isDataRecordFlag = true;
    bool                dataRecordValidFlag = true;
    string           tempString1;
    char tempTest[40];
    char temp = 0;
    const char*               tempCharPtr;
    int                 fieldNumber = 1;
    double              intervalValue;
    int                 intervalStatus;
    unsigned            importedQuality;
    string           _stdLsFiller;
    long tempSortCode = 0;


    /****************************
    * the third header has of the following format
    * sort code (1000-9999),interval value,lodestar status code, interval start
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
        tempCharPtr = aLine.c_str();

        while ( pointFound && (fieldNumber <= 3) && dataRecordValidFlag && isDataRecordFlag && (*tempCharPtr != '\0'))
        {
            switch (fieldNumber)
            {
                case 1:
                    {
                        strncpy(tempTest, tempCharPtr, 4);
                        tempTest[4] = '\0';
                        tempSortCode = atol(tempTest);
                        if( tempSortCode < 1000 || tempSortCode > 9999 )
                        {
                            isDataRecordFlag = false;
                        }
                        tempCharPtr += 4;
                        break;
                    }
                case 2:
                    {
                        for (int i = 0; i < 12; i++)
                        {
                            strncpy(tempTest, tempCharPtr, 5);
                            tempTest[5] = '\0';
                            if (_stdLsAltFormat)
                            {
                                intervalValue = (atof(tempTest)) * _stdLsAltPulseMultiplier;
                            }
                            else
                            {
                                intervalValue = (atof(tempTest)) * _stdLsPulseMultiplier;
                            }
                            tempCharPtr += 5;

                            strncpy(tempTest, tempCharPtr, 1);
                            tempTest[1] = '\0';
                            importedQuality = ForeignToYukonQuality(tempTest);
                            intervalStatus = atoi(tempTest);
                            tempCharPtr += 1;

                            // apply our multiplier and offset
                            intervalValue *= pointMultiplier;
                            intervalValue += pointOffset;

                            /*if (intervalValue == 0 && intervalStatus == 9)
                            {
                                i= 12;
                            }
                            else
                            { */
                            if (multiDispatchMsg->getCount() < getExpectedNumOfEntries())
                            {

                                CtiPointDataMsg* pointData = new CtiPointDataMsg(_pointId,intervalValue,importedQuality,fdrPoint.getPointType());
                                pointData->setTime(CtiTime(CtiDate(1,1,1990)));
                                pointData->setTags(TAG_POINT_LOAD_PROFILE_DATA);
                                if (intervalValue == 0 && intervalStatus == 9)
                                {
                                    //CHAR tempRecStr[80];
                                    //CHAR tempIntStr[80];

                                    string desc = string ("Lodestar point interval data is invalid");
                                    //_snprintf(tempIdStr,80,"%s", _stdLsCustomerIdentifier);
                                    //_snprintf(tempChanStr,80,"%d", _stdLsChannel);
                                    CHAR tempBigStr[256];
                                    _snprintf(tempBigStr,256,"%s%d%s%d", "Record: ",tempSortCode, "; Interval: ", i+1);
                                    string action = string(tempBigStr);
                                    logEvent (desc,action);

                                    pointData->setTags(TAG_INVALID_LODESTAR_READING);
                                }

                                multiDispatchMsg->insert(pointData);
                           }
                        }
                        break;
                    }
                case 3:
                    {

                        strncpy(tempTest, tempCharPtr, 1);
                        tempTest[1] = '\0';
                        _stdLsFiller = (string) tempTest;
                        tempCharPtr += 4;  \
                        break;
                    }
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
        stdLodeStarObj = new CtiFDR_StandardLodeStar();

        // now start it up
        return stdLodeStarObj->run();
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

        stdLodeStarObj->stop();
        delete stdLodeStarObj;
        stdLodeStarObj = 0;

        return 0;
    }


#ifdef __cplusplus
}
#endif

