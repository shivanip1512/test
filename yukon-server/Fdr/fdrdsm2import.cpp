#include "precompiled.h"

#include <wininet.h>
#include <fcntl.h>
#include <io.h>

/** include files **/
#include "ctitime.h"
#include "ctidate.h"

#include "cparms.h"
#include "msg_multi.h"
#include "msg_ptreg.h"
#include "msg_cmd.h"
#include "message.h"
#include "msg_reg.h"
#include "msg_ptreg.h"
#include "msg_pdata.h"
#include "msg_signal.h"
#include "pointtypes.h"

#include "dllbase.h"
#include "logger.h"
#include "guard.h"
#include "fdrdsm2import.h"
#include "utility.h"

using std::list;
using std::string;
using std::endl;

CtiFDR_Dsm2Import * dsm2Interface;

const CHAR * CtiFDR_Dsm2Import::KEY_INTERVAL = "FDR_DSM2IMPORT_INTERVAL";
const CHAR * CtiFDR_Dsm2Import::KEY_FILENAME = "FDR_DSM2IMPORT_FILENAME";
const CHAR * CtiFDR_Dsm2Import::KEY_DRIVE_AND_PATH = "FDR_DSM2IMPORT_DRIVE_AND_PATH";
const CHAR * CtiFDR_Dsm2Import::KEY_DB_RELOAD_RATE = "FDR_DSM2IMPORT_DB_RELOAD_RATE";
const CHAR * CtiFDR_Dsm2Import::KEY_QUEUE_FLUSH_RATE = "FDR_DSM2IMPORT_QUEUE_FLUSH_RATE";
const CHAR * CtiFDR_Dsm2Import::KEY_DELETE_FILE = "FDR_DSM2IMPORT_DELETE_FILE";


// Constructors, Destructor, and Operators
CtiFDR_Dsm2Import::CtiFDR_Dsm2Import()
: CtiFDRAsciiImportBase(string("DSM2IMPORT"))
{
    init();
}

CtiFDR_Dsm2Import::~CtiFDR_Dsm2Import()
{
}

/*************************************************
* Function Name: CtiFDR_Dsm2Import::init
*
* Description: create threads and loads config
*              but does not start the interface
*
**************************************************
*/
BOOL CtiFDR_Dsm2Import::init( void )
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
* Function Name: CtiFDR_Dsm2Import::run()
*
* Description: runs the interface
*
**************************************************
*/
BOOL CtiFDR_Dsm2Import::run( void )
{

    // crank up the base class
    Inherited::run();
    return TRUE;
}


/*************************************************
* Function Name: CtiFDR_Dsm2Import::stop()
*
* Description: stops all threads
*
**************************************************
*/
BOOL CtiFDR_Dsm2Import::stop( void )
{
    Inherited::stop();
    return TRUE;
}

void CtiFDR_Dsm2Import::sendMessageToForeignSys ( CtiMessage *aMessage )
{
    // message is deleted in fdrinterface thread
}

INT CtiFDR_Dsm2Import::processMessageFromForeignSystem (CHAR *data)
{
    return ClientErrors::None;
}

CtiTime CtiFDR_Dsm2Import::Dsm2ToYukonTime (string aTime)
{
    struct tm ts;

    if (sscanf (aTime.c_str(),
                "%4ld%2ld%2ld%2ld%2ld%2ld",
                &ts.tm_year,
                &ts.tm_mon,
                &ts.tm_mday,
                &ts.tm_hour,
                &ts.tm_min,
                &ts.tm_sec) != 6)
    {
        return(CtiTime(PASTDATE));
    }

    ts.tm_year -= 1900;
    ts.tm_mon--;

    if (aTime[14] == 'D' ||
        aTime[14] == 'd')
    {
        ts.tm_isdst = TRUE;
    }
    else
    {
        ts.tm_isdst = FALSE;
    }

    CtiTime returnTime(&ts);

    // if CtiTime can't make a time ???
    if (!returnTime.isValid())
    {
        return(CtiTime(PASTDATE));
    }

    return returnTime;
}

USHORT CtiFDR_Dsm2Import::Dsm2ToYukonQuality (CHAR aQuality)
{
    USHORT Quality = NormalQuality;

    // Test for the various dsm2 qualities
    if (aQuality == 'I')
        Quality = InvalidQuality;

    if (aQuality == 'U')
        Quality = AbnormalQuality;

    if (aQuality == '#')
        Quality = NonUpdatedQuality;

    if (aQuality == 'M')
        Quality = ManualQuality;

    if (aQuality == 'O')
        Quality = UnknownQuality;

    return(Quality);
}

bool CtiFDR_Dsm2Import::validateAndDecodeLine (string &aLine, CtiMessage **retMsg)
{
    bool retCode = false;
    bool flag;
    std::transform(aLine.begin(), aLine.end(), aLine.begin(), tolower);
    string tempString1;                // Will receive each token
    boost::char_separator<char> sep(",\r\n");
    Boost_char_tokenizer cmdLine(aLine, sep);
    Boost_char_tokenizer::iterator tok_iter = cmdLine.begin();

    CtiFDRPoint         point;
    CHAR action[200];
    string desc;

    // do we have an of these
    if ( tok_iter != cmdLine.end())
    {
        tempString1 = *tok_iter; tok_iter++;
        {
            CtiLockGuard<CtiMutex> receiveGuard(getReceiveFromList().getMutex());
            flag = findTranslationNameInList (tempString1, getReceiveFromList(), point);
        }

        string translationName=tempString1;

        if (flag == true)
        {
            //Need these nasty if's to protect from an assert.
            bool flagOk = true;
            if( flagOk && tok_iter != cmdLine.end() ){
                // now
                tempString1 = *tok_iter;tok_iter++;
            }else{
                flagOk = false;
            }if( flagOk && tok_iter != cmdLine.end() ){
                // device name
                tempString1 = *tok_iter;tok_iter++;
            }else{
                flagOk = false;
            }
            if( flagOk && tok_iter != cmdLine.end() ){
                // point name
                tempString1 = *tok_iter;tok_iter++;
            }else{
                flagOk = false;
            }
            // value
            if ( flagOk && tok_iter != cmdLine.end() )
            {
                tempString1 = *tok_iter; tok_iter++;
                float value = atof (tempString1.c_str());

                // quality
                if ( tok_iter != cmdLine.end())
                {
                    tempString1 = *tok_iter; tok_iter++;
                    int quality = Dsm2ToYukonQuality (tempString1[0]);

                    // timestamp
                    if ( tok_iter != cmdLine.end())
                    {
                        tempString1 = *tok_iter; tok_iter++;
                        CtiTime timestamp = Dsm2ToYukonTime (tempString1);

                        if (timestamp != PASTDATE)
                        {
                            // figure out what it should be now
                            switch (point.getPointType())
                            {
                                case AnalogPointType:
                                case PulseAccumulatorPointType:
                                case DemandAccumulatorPointType:
                                case CalculatedPointType:
                                {
                                    *retMsg = new CtiPointDataMsg(point.getPointID(),
                                                                value,
                                                                quality,
                                                                AnalogPointType);
//                                    ((CtiPointDataMsg *)*retMsg)->setTime(timestamp);

                                    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                                    {
                                       CTILOG_DEBUG(dout, "Analog point "<< translationName <<" value "<< value <<
                                               " from "<< getInterfaceName() <<" assigned to point "<< point.getPointID());
                                    }
                                    retCode = true;
                                    break;
                                }
                                case StatusPointType:
                                {

                                    // check for control functions
                                    if (point.isControllable())
                                    {
                                        int controlState=-1;

                                        // make sure the value is valid
                                        if (value == Dsm2_Open)
                                        {
                                            controlState = STATE_OPENED;
                                        }
                                        else if (value == Dsm2_Closed)
                                        {
                                            controlState = STATE_CLOSED;
                                        }
                                        else
                                        {
                                            CTILOG_ERROR(dout, "Invalid control state "<< value <<" for "<< translationName <<" received from "<< getInterfaceName());

                                            CHAR state[20];
                                            _snprintf (state,20,"%.0f",value);
                                            desc = getInterfaceName() + string (" control point received with an invalid state ") + string (state);
                                            _snprintf(action,60,"%s for pointID %d",
                                                      translationName.c_str(),
                                                      point.getPointID());
                                            logEvent (desc,string (action));
                                        }

                                        if (controlState != -1)
                                        {
                                            if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                                            {
                                                Cti::StreamBuffer logmsg;

                                                logmsg <<" Control point "<< translationName;
                                                if (controlState == STATE_OPENED)
                                                {
                                                    logmsg <<" control: Open ";
                                                }
                                                else
                                                {
                                                    logmsg <<" control: Closed ";
                                                }

                                                logmsg <<" from "<< getInterfaceName() <<" and processed for point "<< point.getPointID();

                                                CTILOG_DEBUG(dout, logmsg);
                                            }

                                            // build the command message and send the control
                                            *retMsg = new CtiCommandMsg(CtiCommandMsg::ControlRequest);

                                            ((CtiCommandMsg *)*retMsg)->insert( -1 );                // This is the dispatch token and is unimplemented at this time
                                            ((CtiCommandMsg *)*retMsg)->insert(0);                   // device id, unknown at this point, dispatch will find it
                                            ((CtiCommandMsg *)*retMsg)->insert(point.getPointID());  // point for control
                                            ((CtiCommandMsg *)*retMsg)->insert(controlState);
                                            retCode = true;
                                        }
                                    }
                                    else
                                    {
                                        int yukonValue;
                                        string traceState;
                                        // assign last stuff
                                        switch ((int)value)
                                        {
                                            case Dsm2_Open:
                                                yukonValue = STATE_OPENED;
                                                traceState = string("Opened");
                                                break;
                                            case Dsm2_Closed:
                                                yukonValue = STATE_CLOSED;
                                                traceState = string("Closed");
                                                break;
                                            case Dsm2_Indeterminate:
                                                yukonValue = STATE_INDETERMINATE;
                                                traceState = string("Indeterminate");
                                                break;
                                            case Dsm2_State_Four:
                                                yukonValue = STATEFOUR;
                                                traceState = string("State Four");
                                                break;
                                            case Dsm2_State_Five:
                                                yukonValue = STATEFIVE;
                                                traceState = string("State Five");
                                                break;
                                            case Dsm2_State_Six:
                                                yukonValue = STATESIX;
                                                traceState = string("State Six");
                                                break;
                                            case Dsm2_Invalid:
                                            default:
                                                {
                                                    yukonValue = STATE_INVALID;
                                                }
                                        }

                                        if (yukonValue == STATE_INVALID)
                                        {
                                            CTILOG_ERROR(dout, "Status point "<< translationName <<
                                                    " received an invalid state "<< (int)value <<
                                                    " from "<< getInterfaceName() <<" for point "<< point.getPointID());

                                            CHAR state[20];
                                            _snprintf (state,20,"%.0f",value);
                                            desc = getInterfaceName() + string (" status point received with an invalid state ") + string (state);
                                            _snprintf(action,60,"%s for pointID %d",
                                                      translationName.c_str(),
                                                      point.getPointID());
                                            logEvent (desc,string (action));

                                        }
                                        else
                                        {
                                            *retMsg = new CtiPointDataMsg(point.getPointID(),
                                                                        yukonValue,
                                                                        quality,
                                                                        StatusPointType);
//                                            ((CtiPointDataMsg*)*retMsg)->setTime(timestamp);
                                            if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                                            {
                                                CTILOG_DEBUG(dout, "Status point "<< translationName <<
                                                        " new state: "<< traceState <<
                                                        " from "<< getInterfaceName() <<" assigned to point "<< point.getPointID());
                                            }
                                            retCode = true;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    return retCode;
}



bool CtiFDR_Dsm2Import::readConfig()
{
    string   tempStr;

    tempStr = getCparmValueAsString(KEY_INTERVAL);
    if (tempStr.length() > 0)
    {
        if (atoi (tempStr.c_str()) <=1)
        {
            setImportInterval(1);
        }
        else
        {
            setImportInterval(atoi(tempStr.c_str()));
        }
    }
    else
    {
        setImportInterval(900);
    }

    tempStr = getCparmValueAsString(KEY_FILENAME);
    if (tempStr.length() > 0)
    {
        setFileName(tempStr);
    }
    else
    {
        setFileName(string ("dsmdata.txt"));
    }

    tempStr = getCparmValueAsString(KEY_DRIVE_AND_PATH);
    if (tempStr.length() > 0)
    {
        setDriveAndPath(tempStr);
    }
    else
    {
        setDriveAndPath(string ("\\yukon\\server\\import"));
    }

    tempStr = getCparmValueAsString(KEY_DB_RELOAD_RATE);
    if (tempStr.length() > 0)
    {
        setReloadRate (atoi(tempStr.c_str()));
    }
    else
    {
        setReloadRate (86400);
    }

    tempStr = getCparmValueAsString(KEY_QUEUE_FLUSH_RATE);
    if (tempStr.length() > 0)
    {
        setQueueFlushRate (atoi(tempStr.c_str()));
    }
    else
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

    if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
    {
        Cti::FormattedList loglist;
        loglist.add("Dsm2 import file name")                 << getFileName();
        loglist.add("Dsm2 import directory")                 << getDriveAndPath();
        loglist.add("Dsm2 import interval")                  << getImportInterval();
        loglist.add("Dsm2 import dispatch queue flush rate") << getQueueFlushRate();
        loglist.add("Dsm2 import db reload rate")            << getReloadRate();

        if (shouldDeleteFileAfterImport())
            loglist <<" Import file will be deleted after import";
        else
            loglist <<" Import file will NOT be deleted after import";

        CTILOG_DEBUG(dout, loglist);
    }

    return true;
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
        dsm2Interface = new CtiFDR_Dsm2Import();

        // now start it up
        return dsm2Interface->run();
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

        dsm2Interface->stop();
        delete dsm2Interface;
        dsm2Interface = 0;

        return 0;
    }


#ifdef __cplusplus
}
#endif

