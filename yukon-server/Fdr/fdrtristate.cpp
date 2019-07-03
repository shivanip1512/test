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
#include "fdrtristate.h"

using std::string;
using std::endl;

CtiFDR_Tristate * tristateInterface;

const CHAR * CtiFDR_Tristate::KEY_PORT_NUMBER = "FDR_TRISTATE_PORT_NUMBER";
const CHAR * CtiFDR_Tristate::KEY_TRIES = "FDR_TRISTATE_NUMBER_OF_TRIES";
const CHAR * CtiFDR_Tristate::KEY_INTERVAL = "FDR_TRISTATE_DOWNLOAD_INTERVAL";
const CHAR * CtiFDR_Tristate::KEY_IP_ADDRESS = "FDR_TRISTATE_IP_ADDRESS";
const CHAR * CtiFDR_Tristate::KEY_PASSWORD = "FDR_TRISTATE_PASSWORD";
const CHAR * CtiFDR_Tristate::KEY_LOGIN = "FDR_TRISTATE_LOGIN";
const CHAR * CtiFDR_Tristate::KEY_SERVER_FILENAME = "FDR_TRISTATE_SERVER_FILE";
const CHAR * CtiFDR_Tristate::KEY_DB_RELOAD_RATE = "FDR_TRISTATE_DB_RELOAD_RATE";
const CHAR * CtiFDR_Tristate::KEY_QUEUE_FLUSH_RATE = "FDR_TRISTATE_QUEUE_FLUSH_RATE";
const CHAR * CtiFDR_Tristate::KEY_FTP_DIRECTORY = "FDR_TRISTATE_FTP_DIRECTORY";
const CHAR * CtiFDR_Tristate::KEY_SYSTEM_TOTAL_LABEL = "SYSTEM LOAD";
const CHAR * CtiFDR_Tristate::KEY_30_MINUTE_AVG_LABEL = "30 MINUTE AVG" ;


// Constructors, Destructor, and Operators
CtiFDR_Tristate::CtiFDR_Tristate()
: CtiFDRFtpInterface(string("TRISTATE"))
{
    init();
}


CtiFDR_Tristate::~CtiFDR_Tristate()
{
}

/*************************************************
* Function Name: CtiFDR_Tristate::init
*
* Description: create threads and loads config
*              but does not start the interface
*
**************************************************
*/
BOOL CtiFDR_Tristate::init( void )
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
* Function Name: CtiFDR_Tristate::run()
*
* Description: runs the interface
*
**************************************************
*/
BOOL CtiFDR_Tristate::run( void )
{

    // crank up the base class
    Inherited::run();
    return TRUE;
}


/*************************************************
* Function Name: CtiFDR_Tristate::stop()
*
* Description: stops all threads
*
**************************************************
*/
BOOL CtiFDR_Tristate::stop( void )
{
    Inherited::stop();
    return TRUE;
}

void CtiFDR_Tristate::sendMessageToForeignSys ( CtiMessage *aMessage )
{
    // message is deleted in fdrinterface thread
}

INT CtiFDR_Tristate::processMessageFromForeignSystem (CHAR *data)
{
    return ClientErrors::None;
}


int CtiFDR_Tristate::decodeFile ()
{
    int fileHandle;
    FILE *controlFile;
    int retVal = 0;
    FLOAT current = 0.0, average = 0.0;

    CHAR buffer[300];
    int lineNumber=0, cnt;
    CHAR *ptr, *token=NULL;
    CtiTime finalTime;
    string desc,action;

    if ((fileHandle = _open(getLocalFileName().c_str(), _O_TEXT|_O_RDONLY)) != -1)
    {
        if (filelength(fileHandle) > 290 || filelength(fileHandle) < 60)
        {
            CTILOG_ERROR(dout, "Tristate file failed number of bytes reasonability check -- " << filelength(fileHandle));

            desc = getInterfaceName() + string ("'s data file ") + getLocalFileName() + string(" failed size reasonability check");
            logEvent (desc, action);

            retVal = 1;
        }
        else
        {

            // first of all, read file input
            if ((controlFile = fopen(getLocalFileName().c_str(), "r")) == NULL)
            {
                CTILOG_ERROR(dout, "Tristate file failed to open after download");

                retVal = 1;
            }
            else
            {
                // read in the input lines
                while( fgets( (char*) buffer, 300, controlFile) != NULL )
                {
                    switch (lineNumber)
                    {
                        case 0:
                            {
                                if (strlen (buffer) > 15)
                                {
                                    finalTime = CtiTime (CtiDate(atoi(buffer+3), atoi (buffer), (atoi(buffer+6))),
                                                        atoi(buffer+11),
                                                     atoi (buffer+14),
                                                     atoi(buffer+17));
                                }
                                break;
                            }
                        case 1:
                            {
                                // this is the system load
                                for (cnt=0; cnt < strlen(buffer); cnt++)
                                {
                                    // loop until we find a digit
                                    if (isdigit(buffer[cnt]))
                                    {
                                        ptr = &buffer[cnt];
                                        token = strtok (ptr," ");
                                        break;
                                    }
                                }

                                // now we need a decimal from a string
                                if (token != NULL)
                                {
                                     current = atof (token);
                                }

                                break;
                            }
                        case 2:
                            {
                                // this is the 30 minute average
                                for (cnt=15; cnt < strlen(buffer); cnt++)
                                {
                                    // loop until we find a digit
                                    if (isdigit(buffer[cnt]))
                                    {
                                        ptr = &buffer[cnt];
                                        token = strtok (ptr," ");
                                        break;
                                    }
                                }

                                // now we need a decimal from a string
                                if (token != NULL)
                                {
                                     average = atof (token);
                                }

                                break;
                            }
                        default:
                            break;
                    }

                   lineNumber++;
                }
                fclose (controlFile);

                retVal = sendToDispatch(finalTime, current, average);
                sendLinkState (FDR_CONNECTED);
            }
        }
        _close(fileHandle);
    }
    else
    {
        CTILOG_ERROR(dout, "Tristate file failed to open after download");

        retVal = 1;
    }


    return 0;
}


int CtiFDR_Tristate::fail ()
{
    int retVal = ClientErrors::None;
    CtiPointDataMsg     *pData;
    string           translationName;
    CtiFDRPoint         point;
    bool                 flag = true;

    string desc,action;

    sendLinkState (FDR_NOT_CONNECTED);
    desc = getInterfaceName() + " failed to retrieve a new data file";
    logEvent (desc, action, true);

    // see if the point exists
    flag = findTranslationNameInList (string (KEY_SYSTEM_TOTAL_LABEL), getReceiveFromList(), point);

    if (flag == true)
    {
        CtiCommandMsg *pMsg = new CtiCommandMsg(CtiCommandMsg::UpdateFailed);

        if (pMsg != NULL)
        {
            pMsg->insert( -1 );                   // This is the dispatch token and is unimplemented at this time
            pMsg->insert(CtiCommandMsg::OP_POINTID);     // This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
            pMsg->insert(point.getPointID());                    // The id (device or point which failed)
            pMsg->insert(ScanRateGeneral);              // One of ScanRateGeneral,ScanRateAccum,ScanRateStatus,ScanRateIntegrity, or if unknown -> ScanRateInvalid defined in yukon.h

            // consumes a delete memory
            queueMessageToDispatch(pMsg);
        }
    }

    flag = findTranslationNameInList (string (KEY_30_MINUTE_AVG_LABEL), getReceiveFromList(), point);

    if (flag == true)
    {
        CtiCommandMsg *pMsg = new CtiCommandMsg(CtiCommandMsg::UpdateFailed);

        if (pMsg != NULL)
        {
            pMsg->insert( -1 );                   // This is the dispatch token and is unimplemented at this time
            pMsg->insert(CtiCommandMsg::OP_POINTID);     // This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
            pMsg->insert(point.getPointID());                    // The id (device or point which failed)
            pMsg->insert(ScanRateGeneral);              // One of ScanRateGeneral,ScanRateAccum,ScanRateStatus,ScanRateIntegrity, or if unknown -> ScanRateInvalid defined in yukon.h

            // consumes a delete memory
            queueMessageToDispatch(pMsg);
        }
    }
    return retVal;
}

int CtiFDR_Tristate::sendToDispatch(CtiTime aTime, FLOAT aSystemLoad, FLOAT a30MinuteAvg)
{
    int retVal = ClientErrors::None;
    string           translationName;
    CtiFDRPoint         point;
    bool                 flag = true;

    // see if the point exists
    flag = findTranslationNameInList (string (KEY_SYSTEM_TOTAL_LABEL), getReceiveFromList(), point);

    if ((flag == true) &&
        ((point.getPointType() == AnalogPointType) ||
         (point.getPointType() == PulseAccumulatorPointType) ||
         (point.getPointType() == DemandAccumulatorPointType) ||
         (point.getPointType() == CalculatedPointType)))
    {
        // assign last stuff
        if (aTime != PASTDATE)
        {
            aSystemLoad *= point.getMultiplier();
            aSystemLoad += point.getOffset();

            CtiPointDataMsg     *pData = new CtiPointDataMsg(point.getPointID(),
                                        aSystemLoad,
                                        NormalQuality,
                                        AnalogPointType);

            pData->setTime(aTime);

            // consumes a delete memory
            queueMessageToDispatch(pData);

            if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
            {
                CTILOG_DEBUG(dout, "Current Load "<< aSystemLoad);
            }
        }
    }
    else
    {
        if (flag == false)
        {
            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
            {
                CTILOG_DEBUG(dout, "Translation for analog point "<< string(KEY_SYSTEM_TOTAL_LABEL) <<" from "<< getInterfaceName() <<
                        " was not found");
            }
        }
        else
        {
            CTILOG_ERROR(dout, "Analog point "<< string(KEY_SYSTEM_TOTAL_LABEL) <<" from "<< getInterfaceName() <<
                    " was mapped incorrectly to non-analog point " << point.getPointID());
        }
    }


    flag = findTranslationNameInList (string (KEY_30_MINUTE_AVG_LABEL), getReceiveFromList(), point);

    if ((flag == true) &&
        ((point.getPointType() == AnalogPointType) ||
         (point.getPointType() == PulseAccumulatorPointType) ||
         (point.getPointType() == DemandAccumulatorPointType) ||
         (point.getPointType() == CalculatedPointType)))
    {
        // assign last stuff
        if (aTime != PASTDATE)
        {
            a30MinuteAvg *= point.getMultiplier();
            a30MinuteAvg += point.getOffset();

            CtiPointDataMsg     *pData = new CtiPointDataMsg(point.getPointID(),
                                        a30MinuteAvg,
                                        NormalQuality,
                                        AnalogPointType);

            pData->setTime(aTime);

            // consumes a delete memory
            queueMessageToDispatch(pData);

            if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
            {
                CTILOG_DEBUG(dout, "30 Minute Average: "<< a30MinuteAvg);
            }

        }
    }
    else
    {
        if (flag == false)
        {
            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
            {
                CTILOG_DEBUG(dout, "Translation for analog point "<< string(KEY_30_MINUTE_AVG_LABEL) <<" from "<< getInterfaceName() <<
                        " was not found");
            }
        }
        else
        {
            CTILOG_ERROR(dout, "Analog point "<< string(KEY_30_MINUTE_AVG_LABEL) << " from " << getInterfaceName() <<
                    " was mapped incorrectly to non-analog point " << point.getPointID());
        }
    }


    return retVal;
}

bool CtiFDR_Tristate::readConfig()
{
    bool successful = true;
    string   tempStr;

    tempStr = getCparmValueAsString(KEY_PORT_NUMBER);
    if (tempStr.length() > 0)
    {
        setPort(atoi(tempStr.c_str()));
    }
    else
    {
        setPort (INTERNET_DEFAULT_FTP_PORT);
    }

    tempStr = getCparmValueAsString(KEY_INTERVAL);
    if (tempStr.length() > 0)
    {
        setDownloadInterval(atoi(tempStr.c_str()));
    }
    else
    {
        setDownloadInterval(900);
    }

    tempStr = getCparmValueAsString(KEY_SERVER_FILENAME);
    if (tempStr.length() > 0)
    {
        setServerFileName(tempStr);
    }
    else
    {
        setServerFileName(string ("tsload\\load.dat"));
    }

    tempStr = getCparmValueAsString(KEY_FTP_DIRECTORY);
    if (tempStr.length() > 0)
    {
        setFTPDirectory(tempStr);
    }
    else
    {
        setFTPDirectory(string ("\\yukon\\server\\import"));
    }


    tempStr = getCparmValueAsString(KEY_LOGIN);
    if (tempStr.length() > 0)
    {
        setLogin(tempStr);
    }
    else
    {
        setLogin(string ("cannon"));
    }

    tempStr = getCparmValueAsString(KEY_PASSWORD);
    if (tempStr.length() > 0)
    {
        setPassword(tempStr);
    }
    else
    {
        setPassword(string ("jesse"));
    }

    tempStr = getCparmValueAsString(KEY_IP_ADDRESS);
    if (tempStr.length() > 0)
    {
        setIPAddress(tempStr);
    }
    else
    {
        setIPAddress(string());
                  successful = false;
    }


    tempStr = getCparmValueAsString(KEY_TRIES);
    if (tempStr.length() > 0)
    {
        setTries(atoi(tempStr.c_str()));
    }
    else
    {
        setTries (3);
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
        // default to one second for tristate, its 2 only points
        setQueueFlushRate (1);
    }

    // default local file name, it changes everytime
    setLocalFileName(string ("tristate1.txt"));


    if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
    {
        Cti::FormattedList loglist;

        loglist.add("Tristate server file name")  << getServerFileName();
        loglist.add("Tristate FTP directory")     << getFTPDirectory();
        loglist.add("Tristate download interval") << getDownloadInterval();
        loglist.add("Tristate number of tries")   << getTries();
        loglist.add("Tristate login")             << getLogin();
        loglist.add("Tristate IP")                << getIPAddress();
        loglist.add("Tristate db reload rate")    << getReloadRate();

        CTILOG_DEBUG(dout, loglist);
    }


    return successful;
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
        tristateInterface = new CtiFDR_Tristate();

        // now start it up
        return tristateInterface->run();
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

        tristateInterface->stop();
        delete tristateInterface;
        tristateInterface = 0;

        return 0;
    }


#ifdef __cplusplus
}
#endif


