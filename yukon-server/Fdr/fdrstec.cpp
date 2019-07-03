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
#include "fdrstec.h"

using std::string;
using std::endl;

CtiFDR_STEC * stecInterface;

const CHAR * CtiFDR_STEC::KEY_PORT_NUMBER = "FDR_STEC_PORT_NUMBER";
const CHAR * CtiFDR_STEC::KEY_TRIES = "FDR_STEC_NUMBER_OF_TRIES";
const CHAR * CtiFDR_STEC::KEY_INTERVAL = "FDR_STEC_DOWNLOAD_INTERVAL";
const CHAR * CtiFDR_STEC::KEY_IP_ADDRESS = "FDR_STEC_IP_ADDRESS";
const CHAR * CtiFDR_STEC::KEY_PASSWORD = "FDR_STEC_PASSWORD";
const CHAR * CtiFDR_STEC::KEY_LOGIN = "FDR_STEC_LOGIN";
const CHAR * CtiFDR_STEC::KEY_SERVER_FILENAME = "FDR_STEC_SERVER_FILE";
const CHAR * CtiFDR_STEC::KEY_DB_RELOAD_RATE = "FDR_STEC_DB_RELOAD_RATE";
const CHAR * CtiFDR_STEC::KEY_QUEUE_FLUSH_RATE = "FDR_STEC_QUEUE_FLUSH_RATE";
const CHAR * CtiFDR_STEC::KEY_FTP_DIRECTORY = "FDR_STEC_FTP_DIRECTORY";

const CHAR * CtiFDR_STEC::KEY_SYSTEM_TOTAL_LABEL = "SYSTEM LOAD";
const CHAR * CtiFDR_STEC::KEY_STEC_TOTAL_LABEL = "STEC LOAD" ;


// Constructors, Destructor, and Operators
CtiFDR_STEC::CtiFDR_STEC()
: CtiFDRFtpInterface(string("STEC"))
{
    init();
}


CtiFDR_STEC::~CtiFDR_STEC()
{
}

/*************************************************
* Function Name: CtiFDR_STEC::init
*
* Description: create threads and loads config
*              but does not start the interface
*
**************************************************
*/
BOOL CtiFDR_STEC::init( void )
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
* Function Name: CtiFDR_STEC::run()
*
* Description: runs the interface
*
**************************************************
*/
BOOL CtiFDR_STEC::run( void )
{

    // crank up the base class
    Inherited::run();
    return TRUE;
}


/*************************************************
* Function Name: CtiFDR_STEC::stop()
*
* Description: stops all threads
*
**************************************************
*/
BOOL CtiFDR_STEC::stop( void )
{
    Inherited::stop();
    return TRUE;
}

void CtiFDR_STEC::sendMessageToForeignSys ( CtiMessage *aMessage )
{
    // message is deleted in fdrinterface thread
}



INT CtiFDR_STEC::processMessageFromForeignSystem (CHAR *data)
{
    return ClientErrors::None;
}

int CtiFDR_STEC::decodeFile ()
{
    int fileHandle;
    FILE *controlFile;
    int retVal = 0;
    FLOAT totalSystem=0.0, stecOnly=0.0;

    CHAR buffer[300];
    int lineNumber=0, cnt;
    CHAR *ptr, *token=NULL;
    CtiDate aDate;
    CtiTime finalTime;
    string           desc;
    string           action;

    if ((fileHandle = _open(getLocalFileName().c_str(), _O_TEXT|_O_RDONLY)) != -1)
    {
        if (filelength(fileHandle) > 290 || filelength(fileHandle) < 70)
        {
            CTILOG_ERROR(dout, "STEC file failed number of bytes reasonability check -- " << filelength(fileHandle));

            desc = getInterfaceName() + string ("'s data file ") + getLocalFileName() + string(" failed size reasonability check");
            logEvent (desc, action);

            retVal = 1;
        }
        else
        {

            // first of all, read file input
            if ((controlFile = fopen(getLocalFileName().c_str(), "r")) == NULL)
            {
                CTILOG_ERROR(dout, "STEC file failed to open after download");
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
                               // date
                               if (strlen (buffer) > 13)
                               {
                                   aDate = CtiDate (atoi(buffer+9), atoi (buffer+6), (atoi(buffer+12)+2000));
                               }
                               break;
                           }
                       case 2:
                           {
                               // time
                               if (strlen (buffer) > 13)
                               {
                                   finalTime = CtiTime (aDate,
                                                     atoi(buffer+6),
                                                     atoi (buffer+9),
                                                     atoi(buffer+12));

                               }
                               break;
                           }

                       case 4:
                           {
                               // this is the total system load
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
                                    totalSystem= atof (token);
                               }

                               break;
                           }
                       case 6:
                           {
                               // this is the stec system load
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
                                   stecOnly = atof (token);
                               }

                               break;
                           }
                       default:
                           break;
                   }

                   lineNumber++;
                }

                fclose (controlFile);

                retVal = sendToDispatch(finalTime, totalSystem, stecOnly);
                sendLinkState (FDR_CONNECTED);

            }
        }
        _close(fileHandle);
    }
    else
    {
        CTILOG_ERROR(dout, "STEC file failed to open after download");
        retVal = 1;
    }


    return 0;
}


int CtiFDR_STEC::fail ()
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

    flag = findTranslationNameInList (string (KEY_STEC_TOTAL_LABEL), getReceiveFromList(), point);

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

int CtiFDR_STEC::sendToDispatch(CtiTime aTime, FLOAT aSystemLoad, FLOAT aStecLoad)
{
    int retVal = ClientErrors::None;
    string           translationName;
    CtiFDRPoint         point;
    bool                 flag = true;

    // see if the point exists
    flag = findTranslationNameInList (string (KEY_SYSTEM_TOTAL_LABEL), getReceiveFromList(), point);


    // overkill but thorough
    if ((flag == true) &&
        ((point.getPointType() == AnalogPointType) ||
         (point.getPointType() == PulseAccumulatorPointType) ||
         (point.getPointType() == DemandAccumulatorPointType) ||
         (point.getPointType() == CalculatedPointType)))

    {
        // assign last stuff
        if (aTime != PASTDATE)
        {
            // system load should not be zero !!!
            if (aSystemLoad != 0)
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
                    CTILOG_DEBUG(dout, "System Load "<< aSystemLoad);
                }

            }
            else
            {
                CtiCommandMsg *pCmdMsg = new CtiCommandMsg(CtiCommandMsg::UpdateFailed);

                pCmdMsg->insert( -1 );                          // This is the dispatch token and is unimplemented at this time
                pCmdMsg->insert(CtiCommandMsg::OP_POINTID);                    // This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
                pCmdMsg->insert(point.getPointID());  // The id (device or point which failed)
                pCmdMsg->insert(ScanRateGeneral);               // One of ScanRateGeneral,ScanRateAccum,ScanRateStatus,ScanRateIntegrity, or if unknown -> ScanRateInvalid defined in yukon.h
                queueMessageToDispatch(pCmdMsg);

                if (getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL)
                {
                    CTILOG_DEBUG(dout, "System load is zero, marking as invalid");
                }
            }
        }
    }
    else
    {
        if (flag == false)
        {
            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
            {
                CTILOG_DEBUG(dout, "Translation for analog point "<< string (KEY_SYSTEM_TOTAL_LABEL) <<" from "<< getInterfaceName() <<
                        " was not found");
            }
        }
        else
        {
            CTILOG_ERROR(dout, "Analog point "<< string (KEY_SYSTEM_TOTAL_LABEL) <<" from "<< getInterfaceName() <<
                    " was mapped incorrectly to non-analog point "<< point.getPointID());
        }
    }

    flag = findTranslationNameInList (string (KEY_STEC_TOTAL_LABEL), getReceiveFromList(), point);

    // overkill but thorough
    if ((flag == true) &&
        ((point.getPointType() == AnalogPointType) ||
         (point.getPointType() == PulseAccumulatorPointType) ||
         (point.getPointType() == DemandAccumulatorPointType) ||
         (point.getPointType() == CalculatedPointType)))
    {
        // assign last stuff
        if (aTime != PASTDATE)
        {
            if (aStecLoad != 0)
            {
                aStecLoad *= point.getMultiplier();
                aStecLoad += point.getOffset();

                CtiPointDataMsg     *pData = new CtiPointDataMsg(point.getPointID(),
                                            aStecLoad,
                                            NormalQuality,
                                            AnalogPointType);

                pData->setTime(aTime);

                // consumes a delete memory
                queueMessageToDispatch(pData);

                if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                {
                    CTILOG_DEBUG(dout, "Stec Load "<< aStecLoad);
                }

            }
            else
            {
                CtiCommandMsg *pCmdMsg = new CtiCommandMsg(CtiCommandMsg::UpdateFailed);

                pCmdMsg->insert( -1 );                          // This is the dispatch token and is unimplemented at this time
                pCmdMsg->insert(CtiCommandMsg::OP_POINTID);                    // This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
                pCmdMsg->insert(point.getPointID());  // The id (device or point which failed)
                pCmdMsg->insert(ScanRateGeneral);               // One of ScanRateGeneral,ScanRateAccum,ScanRateStatus,ScanRateIntegrity, or if unknown -> ScanRateInvalid defined in yukon.h
                queueMessageToDispatch(pCmdMsg);

                if (getDebugLevel() & MIN_DETAIL_FDR_DEBUGLEVEL)
                {
                    CTILOG_DEBUG(dout, "Stec load is zero, marking as invalid");
                }
            }
        }
    }
    else
    {
        if (flag == false)
        {
            if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
            {
                CTILOG_DEBUG(dout, "Translation for analog point " << string (KEY_STEC_TOTAL_LABEL) <<" from "<< getInterfaceName() <<
                        " was not found");
            }
        }
        else
        {
            CTILOG_ERROR(dout, "Analog point "<< string(KEY_STEC_TOTAL_LABEL) <<" from " << getInterfaceName() <<
                    " was mapped incorrectly to non-analog point "<< point.getPointID());
        }
    }

    return retVal;
}

bool CtiFDR_STEC::readConfig()
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
        setServerFileName(string ("Load.txt"));
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
        setLogin(string ("anonymous"));
    }

    tempStr = getCparmValueAsString(KEY_PASSWORD);
    if (tempStr.length() > 0)
    {
        setPassword(tempStr);
    }
    else
    {
        setPassword(string ("YukonServerPlatform@wharton.com"));
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
        // default to one second for stec, its only 2 points
        setQueueFlushRate (1);
    }

    // default filename
    setLocalFileName(string ("stec1.txt"));


    if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
    {
        Cti::FormattedList loglist;

        loglist.add("STEC server file name")    << getServerFileName();
        loglist.add("STEC FTP directory")       << getFTPDirectory();
        loglist.add("STEC download interval")   << getDownloadInterval();
        loglist.add("STEC number of tries")     << getTries();
        loglist.add("STEC login")               << getLogin();
        loglist.add("STEC IP")                  << getIPAddress();
        loglist.add("STEC db reload rate")      << getReloadRate();

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
        stecInterface = new CtiFDR_STEC();

        // now start it up
        return stecInterface->run();
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

        stecInterface->stop();
        delete stecInterface;
        stecInterface = 0;

        return 0;
    }


#ifdef __cplusplus
}
#endif

