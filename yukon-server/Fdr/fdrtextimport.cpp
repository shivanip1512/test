#include "precompiled.h"
#include <wininet.h>
#include <fcntl.h>
#include <io.h>
#include <time.h>
#include <string>
#include <fstream>

/** include files **/
#include "ctitime.h"
#include "ctidate.h"
#include "ctistring.h"

#include "cparms.h"
#include "msg_cmd.h"
#include "pointtypes.h"

#include "dllbase.h"
#include "logger.h"
#include "guard.h"
#include "fdrtextfilebase.h"
#include "fdrtextimport.h"
#include "utility.h"

using std::vector;
using std::list;
using std::string;
using std::endl;

CtiFDR_TextImport * textImportInterface;

const CHAR * CtiFDR_TextImport::KEY_INTERVAL = "FDR_TEXTIMPORT_INTERVAL";
const CHAR * CtiFDR_TextImport::KEY_FILENAME = "FDR_TEXTIMPORT_FILENAME";
const CHAR * CtiFDR_TextImport::KEY_LEGACY_DRIVE_AND_PATH = "FDR_TEXTIMPORT_DRIVE_AND_PATH";
const CHAR * CtiFDR_TextImport::KEY_DB_RELOAD_RATE = "FDR_TEXTIMPORT_DB_RELOAD_RATE";
const CHAR * CtiFDR_TextImport::KEY_QUEUE_FLUSH_RATE = "FDR_TEXTIMPORT_QUEUE_FLUSH_RATE";
const CHAR * CtiFDR_TextImport::KEY_DELETE_FILE = "FDR_TEXTIMPORT_DELETE_FILE";
const CHAR * CtiFDR_TextImport::KEY_POINTIMPORT_DEFAULT_PATH = "FDR_TEXTIMPORT_DEFAULT_POINTIMPORT_PATH";//changed
const CHAR * CtiFDR_TextImport::KEY_RENAME_SAVE_FILE = "FDR_TEXTIMPORT_RENAME_SAVE_FILE";


// Constructors, Destructor, and Operators
CtiFDR_TextImport::CtiFDR_TextImport() :
    CtiFDRTextFileBase(string("TEXTIMPORT")),
    _deleteFileAfterImportFlag(false),
    _renameSaveFileAfterImportFlag(false),
    _legacyDrivePath(false)
{

}

CtiFDR_TextImport::~CtiFDR_TextImport()
{
}


bool CtiFDR_TextImport::shouldDeleteFileAfterImport() const
{
    return _deleteFileAfterImportFlag;
}

bool CtiFDR_TextImport::shouldRenameSaveFileAfterImport() const
{
    return _renameSaveFileAfterImportFlag;
}

CtiFDR_TextImport &CtiFDR_TextImport::setRenameSaveFileAfterImport (bool aFlag)
{
    _renameSaveFileAfterImportFlag = aFlag;
    return *this;
}


CtiFDR_TextImport &CtiFDR_TextImport::setDeleteFileAfterImport (bool aFlag)
{
    _deleteFileAfterImportFlag = aFlag;
    return *this;
}

BOOL CtiFDR_TextImport::init( void )
{
    // init these lists so they have something
    CtiFDRManager   *recList = new CtiFDRManager(getInterfaceName(),string(FDR_INTERFACE_RECEIVE));
    getReceiveFromList().setPointList (recList);
    recList = NULL;

    // init the base class
    Inherited::init();
    _threadReadFromFile = rwMakeThreadFunction(*this,
                                               &CtiFDR_TextImport::threadFunctionReadFromFile);

    _legacyDrivePath = false;
    if (!readConfig( ))
    {
        return FALSE;
    }

    loadTranslationLists();
    return TRUE;
}
/*************************************************
* Function Name: CtiFDR_TextImport::run()
*
* Description: runs the interface
*
**************************************************
*/
BOOL CtiFDR_TextImport::run( void )
{
    // crank up the base class
    Inherited::run();

    // startup our interfaces
    _threadReadFromFile.start();

    return TRUE;
}


/*************************************************
* Function Name: CtiFDR_TextImport::stop()
*
* Description: stops all threads
*
**************************************************
*/
BOOL CtiFDR_TextImport::stop( void )
{
    _threadReadFromFile.requestCancellation();
    Inherited::stop();
    return TRUE;
}


CtiTime CtiFDR_TextImport::ForeignToYukonTime (string& aTime, CHAR aDstFlag)
{
    struct tm ts;
    CtiTime retVal;
    int month,day,year,hour,minute,sec;

    if (aTime.length() == 19)
    {
        if (sscanf (aTime.c_str(),
                    "%2ld/%2ld/%4ld %2ld:%2ld:%2ld",
                    &month,
                    &day,
                    &year,
                    &hour,
                    &minute,
                    &sec) != 6)
        {
            retVal = PASTDATE;
        }
        else
        {
            //Constructing a date since boost will exception and return 1/1/1970 if it is invalid.
            CtiDate dateValidCheck = CtiDate(day,month,year);

            if (dateValidCheck == CtiDate(1,1,1970))
            {
                return PASTDATE;
            }

            ts.tm_mon = month - 1;
            ts.tm_mday = day;
            ts.tm_year = year - 1900;
            ts.tm_hour = hour;
            ts.tm_min = minute;
            ts.tm_sec = sec;

            if( aDstFlag == 'D' || aDstFlag == 'd' )
            {
                ts.tm_hour;
                ts.tm_isdst = 1;//true
            }
            else if( aDstFlag == 'S' || aDstFlag == 's' )
            {
                ts.tm_isdst = 0;//false
            }
            else
            {
                //Error Case. No dst flag
                retVal = PASTDATE;
                return retVal;
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
    {
        retVal = PASTDATE;
    }
    return retVal;
}

USHORT CtiFDR_TextImport::ForeignToYukonQuality (char aQuality)
{

    if (aQuality == 'G' || aQuality == 'g')
        return NormalQuality;
    if (aQuality == 'B' || aQuality == 'b')
        return NonUpdatedQuality;
    if (aQuality == 'M' || aQuality == 'm')
        return ManualQuality;

    CTILOG_WARN(dout, "Quality not recognized. Defaulting to NonUpdated. Q: "<< aQuality);

    return NonUpdatedQuality;
}

bool CtiFDR_TextImport::processFunctionOne (Tokenizer& cmdLine, CtiMessage **aRetMsg)
{
    bool retCode = false;
    bool pointValidFlag=true;

    CtiFDRPoint point;
    CtiFDRPointSPtr pointPtr;
    int fieldNumber=1,quality;
    double value;
    string action;
    string linetimestamp,translationName,desc;
    CtiTime pointtimestamp;
    string tempString1;

    /****************************
    * function 1 is of the following format
    * function,id,value,quality,timestamp,daylight savings flag
    *****************************
    */
    try{
        Tokenizer::iterator tok_iter = cmdLine.begin();
        ++tok_iter;
        /** Error Check: unexpected end of input */
        if (tok_iter == cmdLine.end())
        {
            CTILOG_ERROR(dout, "Incorrect number of parameters in input line from file.");
            return retCode;
        }
                tempString1 = string(*tok_iter);
        ++tok_iter;
        //Param 2 - Point Name
        translationName = tempString1;
        {
            CtiLockGuard<CtiMutex> receiveGuard(getReceiveFromList().getMutex());
            std::transform(translationName.begin(), translationName.end(), translationName.begin(), toupper);
            std::map<string,int>::iterator iter = nameToPointId.find(translationName);
            if( iter != nameToPointId.end() ) {
                pointPtr = getReceiveFromList().getPointList()->findFDRPointID(iter->second);
                point = *pointPtr;
                pointValidFlag = true;
            }
            else
            {
                pointValidFlag = false;
            }
        }
        if (pointValidFlag == false)
        {
            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
            {
                CTILOG_DEBUG(dout, "Translation for point "<< translationName <<" from "<< getFileName() <<" was not found");

                desc = getFileName() + string (" point is not listed in the translation table");
                action = translationName;
                logEvent (desc,action);
            }
        }
        else
        {
            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
            {
                CTILOG_DEBUG(dout, "Translation for point " << translationName << " from " << getFileName() << " was found");
            }

            //Param 3 - Value
            /** Error Check: unexpected end of input */
            if (tok_iter == cmdLine.end())
            {
                CTILOG_ERROR(dout, "Incorrect number of parameters in input line from file.");
                return retCode;
            }

            tempString1 = string(*tok_iter);
            ++tok_iter;
            value = atof(tempString1.c_str());
            //Test if the Value is Zero. if it is zero and tempString1[0] is not 0 then this could indicate a problem
            if (value == 0 && tempString1[0] != '0')
            {
                CTILOG_WARN(dout, "Possible bad input from import file. Received a 0.0 value from "<< tempString1);
            }
            //Param 4 - quality
            /** Error Check: unexpected end of input */
            if (tok_iter == cmdLine.end())
            {
                CTILOG_ERROR(dout, "Incorrect number of parameters in input line from file.");
                return retCode;
            }
            tempString1 = string(*tok_iter);
            ++tok_iter;
            quality = ForeignToYukonQuality(tempString1[0]);
            //Input check inside function call.

            //Param 5 - Timestamp | Param 6 - DST Flag
            /** Error Check: unexpected end of input */
            if (tok_iter == cmdLine.end())
            {
                CTILOG_ERROR(dout, "Incorrect number of parameters in input line from file.");
                return retCode;
            }

            tempString1 = string(*tok_iter);
            ++tok_iter;
            linetimestamp = tempString1;
            /** Error Check: unexpected end of input */
            if (tok_iter == cmdLine.end())
            {
                CTILOG_ERROR(dout, "Incorrect number of parameters in input line from file.");
                return retCode;
            }
            tempString1 = string(*tok_iter);
            pointtimestamp = ForeignToYukonTime(linetimestamp, tempString1[0]);

            if (pointtimestamp != PASTDATE && pointValidFlag == true)
            {
                retCode = buildAndAddPoint (point,value,pointtimestamp,quality,translationName,aRetMsg);
            }
            else
            {
                Cti::StreamBuffer logmsg;
                logmsg <<"Input for point invalid, not sending the update. Check log for errors.";
                if (pointtimestamp == PASTDATE)
                {
                    logmsg <<"Timestamp or DST Flag is incorrect: "<< linetimestamp <<" "<< tempString1[0];
                }

                CTILOG_ERROR(dout, logmsg);
           }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "Unable to parse data. Bad input from the file.");
    }

    return retCode;
}


bool CtiFDR_TextImport::buildAndAddPoint (CtiFDRPoint &aPoint,
                                          DOUBLE aValue,
                                          CtiTime aTimestamp,
                                          int aQuality,
                                          string aTranslationName,
                                          CtiMessage **aRetMsg)
{
    string desc;
    CHAR   action[60];
    bool retCode = false;

    // figure out what it should be now
    switch (aPoint.getPointType())
    {
    case AnalogPointType:
    case PulseAccumulatorPointType:
    case DemandAccumulatorPointType:
    case CalculatedPointType:
        {
            DOUBLE value = aValue;

            if (aPoint.isControllable())
            {
                CtiCommandMsg *aoMsg = createAnalogOutputMessage(aPoint.getPointID(), aTranslationName, value);
                sendMessageToDispatch(aoMsg);
            }

            value *= aPoint.getMultiplier();
            value += aPoint.getOffset();

            *aRetMsg = new CtiPointDataMsg(aPoint.getPointID(),
                                           value,
                                           aQuality,
                                           AnalogPointType);
            ((CtiPointDataMsg *)*aRetMsg)->setTime(aTimestamp);

            if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
            {
                CTILOG_DEBUG(dout, "Analog point "<< aTranslationName <<" value "<< value <<" from "<< getFileName() <<" assigned to point "<< aPoint.getPointID());
            }
            retCode = true;
            break;
        }
    case StatusPointType:
        {
            // check for control functions
            if (aPoint.isControllable())
            {
                // make sure the value is valid
                if ((aValue == STATE_OPENED) || (aValue == STATE_CLOSED))
                {
                    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                    {
                        Cti::StreamBuffer logmsg;
                        logmsg << " Control point " << aTranslationName;
                        if (aValue == STATE_OPENED)
                        {
                            logmsg << " control: Open " ;
                        } else
                        {
                            logmsg << " control: Closed " ;
                        }

                        logmsg <<" from " << getFileName() << " and processed for point " << aPoint.getPointID();

                        CTILOG_DEBUG(dout, logmsg);
                    }

                    // build the command message and send the control
                    *aRetMsg = new CtiCommandMsg(CtiCommandMsg::ControlRequest);

                    ((CtiCommandMsg *)*aRetMsg)->insert( -1 );                // This is the dispatch token and is unimplemented at this time
                    ((CtiCommandMsg *)*aRetMsg)->insert(0);                   // device id, unknown at this point, dispatch will find it
                    ((CtiCommandMsg *)*aRetMsg)->insert(aPoint.getPointID());  // point for control
                    ((CtiCommandMsg *)*aRetMsg)->insert(aValue);
                    retCode = true;

                } else
                {
                    CTILOG_ERROR(dout, "Invalid control state "<< aValue <<" for " << aTranslationName <<" received from "<< getFileName());

                    CHAR state[20];
                    _snprintf (state,20,"%.0f",aValue);
                    desc = getFileName() + string (" control point received with an invalid state ") + string (state);
                    _snprintf(action,60,"%s for pointID %d",
                              aTranslationName.c_str(),
                              aPoint.getPointID());
                    logEvent (desc,string (action));
                }
            } else
            {
                if ((aValue == STATE_OPENED) || (aValue == STATE_CLOSED))
                {
                    string tracestate;
                    if (aValue == STATE_OPENED)
                    {
                        tracestate = string ("Open");
                    } else
                    {
                        tracestate = string ("Closed");
                    }
                    *aRetMsg = new CtiPointDataMsg(aPoint.getPointID(),
                                                   aValue,
                                                   aQuality,
                                                   StatusPointType);
                    ((CtiPointDataMsg*)*aRetMsg)->setTime(aTimestamp);
                    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                    {
                        CTILOG_DEBUG(dout, "Status point "<< aTranslationName <<" new state: "<< tracestate <<
                                " from "<< getFileName() <<" assigned to point "<< aPoint.getPointID());
                    }
                    retCode = true;
                } else
                {
                    CTILOG_ERROR(dout, "Status point "<< aTranslationName <<" received an invalid state "<< (int)aValue <<
                            " from "<< getFileName() <<" for point "<< aPoint.getPointID());

                    CHAR state[20];
                    _snprintf (state,20,"%.0f",aValue);
                    desc = getFileName() + string (" status point received with an invalid state ") + string (state);
                    _snprintf(action,60,"%s for pointID %d",
                              aTranslationName.c_str(),
                              aPoint.getPointID());
                    logEvent (desc,string (action));

                }
            }
            break;
        }
    }
    return retCode;
}


bool CtiFDR_TextImport::validateAndDecodeLine (string &aLine, CtiMessage **aRetMsg)
{
    bool retCode = false;
    bool flag;

    std::transform(aLine.begin(), aLine.end(), aLine.begin(), tolower);
    string tempString1;// Will receive each token

    Separator sep(",\r\n", "", boost::keep_empty_tokens);
    Tokenizer cmdLine(aLine, sep);
    Tokenizer::iterator tok_iter = cmdLine.begin();

    CtiFDRPoint         point;
    int function;

    // grab the function number
    if ( tok_iter != cmdLine.end() )
    {
        tempString1 = *tok_iter;
        function = atoi (tempString1.c_str());

        // check the function number
        switch (function)
        {
        case 1:
            {
                /****************************
                * function 1 is of the following format
                * id,value,quality,timestamp,daylight savings flag
                *****************************
                */
                retCode=processFunctionOne (cmdLine,aRetMsg);
                break;
            }
        default:
            break;
        }
    }
    return retCode;
}



bool CtiFDR_TextImport::readConfig()
{
    string   tempStr;

    tempStr = getCparmValueAsString(KEY_INTERVAL);
    if (tempStr.length() > 0)
    {
        if (atoi (tempStr.c_str()) <=1)
        {
            setInterval(1);
        } else
        {
            setInterval(atoi(tempStr.c_str()));
        }
    } else
    {
        setInterval(900);
    }

    tempStr = getCparmValueAsString(KEY_FILENAME);
    if (tempStr.length() > 0)
    {
        setFileName(tempStr);
    } else
    {
        setFileName(string ("yukon.txt"));
    }
    tempStr = getCparmValueAsString(KEY_POINTIMPORT_DEFAULT_PATH);
    if (tempStr.length() > 0)
    {
        setFileImportBaseDrivePath(tempStr);
    } else
    {
        setFileImportBaseDrivePath(CtiString ("c:\\yukon\\server\\import"));
    }

    tempStr = getCparmValueAsString(KEY_LEGACY_DRIVE_AND_PATH);
    if (tempStr.length() > 0)
    {
        _legacyDrivePath = true;
        int pos = tempStr.find_last_of("\\");
        if( pos != tempStr.size()-1 )
        {
            tempStr += "\\";
        }
        setDriveAndPath(tempStr);
    } else
    {
        setDriveAndPath(string ("\\yukon\\server\\import"));
    }

    tempStr = getCparmValueAsString(KEY_DB_RELOAD_RATE);
    if (tempStr.length() > 0)
    {
        setReloadRate (atoi(tempStr.c_str()));
    } else
    {
        setReloadRate (86400);
    }

    tempStr = getCparmValueAsString(KEY_QUEUE_FLUSH_RATE);
    if (tempStr.length() > 0)
    {
        setQueueFlushRate (atoi(tempStr.c_str()));
    } else
    {
        // default to one second for stec, its only 2 points
        setQueueFlushRate (1);
    }

    setDeleteFileAfterImport(true);
    tempStr = getCparmValueAsString(KEY_DELETE_FILE);
    if (tempStr.length() > 0)
    {
        if (ciStringEqual(tempStr,"false"))
        {
            setDeleteFileAfterImport (false);
        }
    }

    setRenameSaveFileAfterImport(false);
    tempStr = getCparmValueAsString(KEY_RENAME_SAVE_FILE);
    if (tempStr.length() > 0)
    {
        if (ciStringEqual(tempStr,"true"))
        {
            setRenameSaveFileAfterImport (true);
        }
    }

    if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
    {
        Cti::FormattedList loglist;

        if ( _legacyDrivePath )
        {
            loglist <<"Legacy method of setting the Drive Path. Please update to the new Method using the Points.";
        }

        loglist.add("Text import file name")                 << getFileName();
        loglist.add("Text import directory")                 << getDriveAndPath();//obsolete?
        loglist.add("Text import interval")                  << getInterval();
        loglist.add("Text import dispatch queue flush rate") << getQueueFlushRate();
        loglist.add("Text import db reload rate")            << getReloadRate();

        if( shouldDeleteFileAfterImport() && shouldRenameSaveFileAfterImport() )
        {
            loglist <<"Configuration Error cannot rename AND delete the input file. Defaulting to rename.\n";
            setRenameSaveFileAfterImport(true);
            setDeleteFileAfterImport(false);
        }
        else if( !shouldDeleteFileAfterImport() && !shouldRenameSaveFileAfterImport() )
        {
            loglist <<"Configuration Error please specify what to do with the file after the import. Defaulting to delete.\n";
            setDeleteFileAfterImport(true);
        }

        if (shouldDeleteFileAfterImport() )
            loglist << " Import file will be deleted after import" << endl;

        if (shouldRenameSaveFileAfterImport() )
            loglist << " Import file will be renamed after import" << endl;

        CTILOG_DEBUG(dout, loglist);
    }

    return true;
}


/************************************************************************
* Function Name: CtiFDRTextFileBase::loadTranslationLists()
*
* Description: Creates a collection of points and their translations for the
*                               specified direction
*
*************************************************************************
*/
bool CtiFDR_TextImport::loadTranslationLists()
{
    bool foundPoint = false;
    bool successful = false;

    try
    {
        // make a list with all received points
        CtiFDRManager *pointList = new CtiFDRManager(getInterfaceName(), string (FDR_INTERFACE_RECEIVE));

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
                if (getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
                {
                    CTILOG_DEBUG(dout, "pointList->entries() " << pointList->entries());
                }

                // get iterator on send list
                CtiFDRManager::spiterator  myIterator = pointList->getMap().begin();
                int x;
                nameToPointId.clear();
                for ( ; myIterator != pointList->getMap().end(); ++myIterator)
                {
                    foundPoint = true;
                    successful = translateSinglePoint(myIterator->second);
                }   // end for interator

                // lock the receive list and remove the old one
                {
                    CtiLockGuard<CtiMutex> receiveGuard(getReceiveFromList().getMutex());
                    if (getReceiveFromList().getPointList() != NULL)
                    {
                        getReceiveFromList().deletePointList();
                    }
                    getReceiveFromList().setPointList (pointList);
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
                            CTILOG_DEBUG(dout, "No points defined for use by interface "<< getInterfaceName());
                        }
                    }
                }
                setLinkStatusID(getClientLinkStatusID (getInterfaceName()));
            }
            else
            {
                CTILOG_ERROR(dout, "Could not load (Receive) points for "<< getInterfaceName() <<" : Empty data set returned ");
                successful = false;
            }
        }
        else
        {
            CTILOG_ERROR(dout, "Unable to load points from database for "<< getInterfaceName());
            successful = false;
        }

    }   // end try block

    catch (RWExternalErr e )
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

bool CtiFDR_TextImport::translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList)
{
    bool successful = false;

    string tempString1;
    string tempString2;
    string pointID;
    CtiString translationName;
    CtiString translationDrivePath;
    CtiString translationFilename;
    CtiString translationFolderName;

    translationDrivePath = getFileImportBaseDrivePath();
    translationFilename = getFileName();

    if (getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
    {
        Cti::FormattedList loglist;
        loglist.add("translationPoint")      << translationPoint->getPointID();
        loglist.add("translationFolderName") << translationFolderName;
        loglist.add("translationDrivePath")  << translationDrivePath;

        CTILOG_DEBUG(dout, loglist);
    }

    for (int x = 0; x < translationPoint->getDestinationList().size(); x++)
    {
        if (getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
        {
            CTILOG_DEBUG(dout, "Parsing Yukon Point ID "<< translationPoint->getPointID() <<
                    " translate: "<< translationPoint->getDestinationList()[x].getTranslation());
        }

        translationName = "";

        pointID = translationPoint->getDestinationList()[x].getTranslationValue("Point ID");

        /* For optimized find**/
        string pointName = pointID;
        std::transform(pointName.begin(), pointName.end(), pointName.begin(), toupper);
        nameToPointId.insert(std::pair<string,int>(pointName,translationPoint->getPointID()));
        /***/

        // now we have a point id
        if ( !pointID.empty() )
        {
            successful = true;

            translationName += " ";
            translationName += pointID;
            translationName.toUpper();

            tempString1 = translationPoint->getDestinationList()[x].getTranslationValue("DrivePath");
            if (!tempString1.empty())
            {
                // now we have a Drive/Path
                translationFolderName = tempString1;
                translationFolderName.toLower();

                if ((translationDrivePath = translationFolderName.match(boost::regex("([A-Z]|[a-z]):\\\\"))).empty())
                {
                    translationDrivePath = getFileImportBaseDrivePath();
                    translationDrivePath.toUpper();
                } else
                    translationDrivePath = translationFolderName;

                if (getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
                {
                    Cti::FormattedList loglist;
                    loglist.add("translationFolderName") << translationFolderName;
                    loglist.add("translationDrivePath")  << translationDrivePath;

                    CTILOG_DEBUG(dout, loglist);
                }

                translationName += " ";
                translationName += tempString1;
                translationName.toUpper();

                tempString1 = translationPoint->getDestinationList()[x].getTranslationValue("Filename");
                if ( !tempString1.empty() )
                {
                    translationFilename = tempString1;
                    translationFilename.toLower();

                    translationName += " ";
                    translationName += translationFilename;
                    translationName.toUpper();
                }
            }
            translationPoint->getDestinationList()[x].setTranslation (pointID);//ts

            if (getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
            {
                CTILOG_DEBUG(dout, "translationFilename: "<< translationFilename <<" translationDrivePath: "<< translationDrivePath);
            }

            //check if its already there before putting it in the list.
            std::vector<CtiFDRTextFileInterfaceParts> *fInfoList = getFileInfoList();
            std::vector<CtiFDRTextFileInterfaceParts>::iterator itr = fInfoList->begin();

            string fn = translationDrivePath + translationFilename;

            bool found = false;
            for( ; itr != fInfoList->end(); itr++ )
            {
                string fn2 = itr->getDriveAndPath() + itr->getFileName();
                if( fn2.compare(fn) == 0 ) {
                    found = true;
                    break;
                }
            }

            if( !found ) {//unique
                CtiFDRTextFileInterfaceParts tempFileInfoList ( translationFilename, translationDrivePath, 0);
                fInfoList->push_back(tempFileInfoList);
            }

            if (getDebugLevel() & DATABASE_FDR_DEBUGLEVEL)
            {
                Cti::FormattedList loglist;
                loglist.add("Point ID")            << translationPoint->getPointID();
                loglist.add("translated")          << translationName;
                loglist.add("FILE INFO LIST SIZE") << getFileInfoList()->size();
                loglist.add("Translation...")      << translationPoint->getDestinationList()[x].getTranslation();

                CTILOG_DEBUG(dout, loglist);
            }
        }
    }

    return successful;
}

void CtiFDR_TextImport::cleanupTranslationPoint(CtiFDRPointSPtr & translationPoint, bool recvList)
{
    if (recvList)
    {
        if (translationPoint.get() == NULL)
        {
            return;
        }

        int size = translationPoint->getDestinationList().size();
        for ( int i = 0 ; i < size; i++) {
            string str = translationPoint->getDestinationList()[i].getTranslation();
            if (str != "")
            {
                std::transform(str.begin(), str.end(), str.begin(), toupper);
                nameToPointId.erase(str);
            }
        }
    }

    return;
}


/***
* Function Name: list<string> GetFileNames()
*
* Depending on which method will determine if there could be more than one file.
*
*/

std::list<string> CtiFDR_TextImport::getFileNames()
{
    list<string> lst;

    if( getLegacy() )
    {
        string fn = getDriveAndPath() + getFileName();
        lst.push_back(fn);
    }
    else
    {
        vector <CtiFDRTextFileInterfaceParts> *fInfoList = getFileInfoList();
        std::vector <CtiFDRTextFileInterfaceParts>::iterator itr;
        for( itr = fInfoList->begin(); itr != fInfoList->end(); itr++ )
        {
            string fn = itr->getDriveAndPath() + itr->getFileName();
            lst.push_back(fn);
        }
    }

    return lst;
}


/*
* Function Name: List<Point> parseFiles
*
*/
std::list<string> CtiFDR_TextImport::parseFiles()
{
    std::list<string> lst;

    std::list<string> fileList = getFileNames();
    for( std::list<string>::iterator itr = fileList.begin(); itr != fileList.end(); itr++ )
    {
        string fname(*itr);

        std::fstream* strm = new std::fstream();
        strm->open( (*itr).c_str(), std::fstream::in );
        if( !(*strm).is_open() )
        {
            CTILOG_ERROR(dout, "Unable to open file: "<< (*itr));
            delete strm;
        }else
        {
            string input;
            while( std::getline( *((std::iostream*)strm), input ) )
            {
                //Test on input to see if its ok?  >1 : <500 : invalid characters?
                lst.push_back(input);
            }
            //handle file close and stuff here
            strm->close();
            delete strm;
            handleFilePostOp((*itr));
        }
    }

    return lst;
}

/**************************************************************************
* Function Name: CtiFDRTextFileBase::threadFunctionReadFromFile (void )
*
* Description: thread that waits and then grabs the file for processing
*
* This works in two ways, Legacy and Point. Legacy gets the one singular file from
*       the cparms in master config. Point will read through each point and open the file name
*       listed on that point and process all points in that file.
*       Point: Loop through finding all unique file names listed under points.
*              Process each of those files input. loop back and sleep.
*
****************************************************************************/
void CtiFDR_TextImport::threadFunctionReadFromFile( void )
{
    RWRunnableSelf  pSelf = rwRunnable( );
    CtiTime         timeNow;
    CtiTime         refreshTime(PASTDATE);

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
                //parse files
                std::list<string> pointUpdates = parseFiles();

                for( std::list<std::string>::iterator itr3 =  pointUpdates.begin(); itr3 != pointUpdates.end(); itr3++ )
                {
                    CtiMessage* msg = NULL;
                    bool ok = validateAndDecodeLine( *itr3, &msg );

                    if ( ok )
                    {
                        queueMessageToDispatch( msg );
                    }
                }

                pointUpdates.clear();
                refreshTime = CtiTime() - (CtiTime::now().seconds() % getInterval()) + getInterval();
            }//end if (timeNow >= refreshTime)
        }//end for

    }

    catch ( RWCancellation & )
    {
        CTILOG_INFO(dout, "CANCELLATION threadFunctionReadFromFile in interface "<< getInterfaceName());
    }
    catch ( ... )
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "threadFunctionReadFromFile in interface "<< getInterfaceName() << " is dead!");
    }
}
void CtiFDR_TextImport::handleFilePostOp( string fileName )
{
    //Change to do all files in fileList
    if ( shouldRenameSaveFileAfterImport() && !shouldDeleteFileAfterImport() )
    {
        moveFile( fileName );
    }
    else if( shouldDeleteFileAfterImport() && !shouldRenameSaveFileAfterImport() )
    {
        deleteFile( fileName );
    }
    else if( shouldDeleteFileAfterImport() && shouldRenameSaveFileAfterImport() )
    {
        //both specified. default to move, and not delete( since they were moved )
        moveFile( fileName );
        CTILOG_WARN(dout, "master.cfg configuration error cannot rename AND delete the input file. Defaulting to rename.");
    }
}

bool CtiFDR_TextImport::moveFiles( std::list<string>& fileNames )
{
    bool noErrors = true;
    for( std::list<string>::iterator itr = fileNames.begin(); itr != fileNames.end(); itr++)
    {
        if( !moveFile(*itr) ) {
            noErrors = false;
        }
    }
    return noErrors;
}
bool CtiFDR_TextImport::moveFile( string fileName )
{
    bool noErrors = true;
    string fileNameAndPath = fileName;
    int pos = fileNameAndPath.rfind(".");
    //get just the fileName, no path.
    string oldFileName(fileNameAndPath,0,pos);
    string tempTime = CtiTime().asString();

    //remove slashes and : in the time
    bool slashesInString = true;
    while( slashesInString )
    {
        int pos2 = tempTime.find("/");
        if( pos2 == string::npos )
        {
            slashesInString = false;
            pos2 = tempTime.find(":");
            while (pos2 != string::npos )
            {
                tempTime = tempTime.erase(pos2,1);
                pos2 = tempTime.find(":");
            }
        }
        else
        {
            tempTime = tempTime.replace(pos2,1,"_");
        }
    }

    string newFilename = oldFileName + "." + tempTime + ".txt";

    bool success = MoveFileEx( fileNameAndPath.c_str(),newFilename.c_str(), MOVEFILE_REPLACE_EXISTING | MOVEFILE_COPY_ALLOWED);
    if ( !success )
    {
        noErrors = false;
        const DWORD lastError = GetLastError();
        CTILOG_ERROR(dout, "Failed to move file "<< fileNameAndPath <<". Error Code: "<< lastError);
    }
    return noErrors;
}

bool CtiFDR_TextImport::deleteFiles( std::list<string>& fileNames )
{
    bool noErrors = true;
    for( std::list<string>::iterator itr = fileNames.begin(); itr != fileNames.end(); itr++)
    {
        if( !deleteFile(*itr) ) {
            noErrors = false;
        }
    }
    return noErrors;
}

bool CtiFDR_TextImport::deleteFile( string fileName )
{
    bool noErrors = true;
    string fileNameAndPath = fileName;
    bool success = DeleteFile( fileNameAndPath.c_str() );
    if ( !success )
    {
        noErrors = false;
        const DWORD lastError = GetLastError();
        CTILOG_ERROR(dout, "Failed to delete "<< fileNameAndPath <<". Error Code: "<< lastError);
    }
    return noErrors;
}
CtiString& CtiFDR_TextImport::getFileImportBaseDrivePath()
{
    return _fileImportBaseDrivePath;
}

CtiString& CtiFDR_TextImport::setFileImportBaseDrivePath(CtiString importBase)
{
    _fileImportBaseDrivePath = importBase;
    return _fileImportBaseDrivePath;
}

bool CtiFDR_TextImport::getLegacy()
{
    return _legacyDrivePath;
}
void CtiFDR_TextImport::setLegacy( bool val )
{
    _legacyDrivePath = val;
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
        textImportInterface = new CtiFDR_TextImport();
        textImportInterface->init();
        // now start it up
        return textImportInterface->run();
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

        textImportInterface->stop();
        delete textImportInterface;
        textImportInterface = 0;

        return 0;
    }


#ifdef __cplusplus
}
#endif


