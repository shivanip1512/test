#include "precompiled.h"

#include "ctitime.h"
#include "ctidate.h"
#include "utility.h"

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
#include "fdrsocketinterface.h"
#include "fdrpointlist.h"
#include "fdrserverconnection.h"
#include "fdrclientconnection.h"
#include "fdrsocketlayer.h"
#include "fdrinet.h"
#include "socket_helper.h"
#include "dsm2.h"

// this class header
#include "fdrrccs.h"

using namespace std;

/** global used to start the interface by c functions **/
CtiFDR_Rccs * rccsInterface;

const CHAR * CtiFDR_Rccs::KEY_LISTEN_PORT_NUMBER = "FDR_RCCS_PORT_NUMBER";
const CHAR * CtiFDR_Rccs::KEY_CONNECT_PORT_NUMBER = "FDR_RCCS_CONNECT_PORT_NUMBER";
const CHAR * CtiFDR_Rccs::KEY_TIMESTAMP_WINDOW = "FDR_RCCS_TIMESTAMP_VALIDITY_WINDOW";
const CHAR * CtiFDR_Rccs::KEY_DB_RELOAD_RATE = "FDR_RCCS_DB_RELOAD_RATE";
const CHAR * CtiFDR_Rccs::KEY_SOURCE_NAME = "FDR_RCCS_SOURCE_NAME";
const CHAR * CtiFDR_Rccs::KEY_BATCH_MARKER_NAME = "FDR_RCCS_BATCH_MARKER_NAME";
const CHAR * CtiFDR_Rccs::KEY_STANDALONE = "FDR_RCCS_STANDALONE";

// Constructors, Destructor, and Operators
CtiFDR_Rccs::CtiFDR_Rccs() :
    CtiFDR_Inet("RCCS"),
    iAuthorizationFlags(0)
{
    iStandalone = false;
}


CtiFDR_Rccs::~CtiFDR_Rccs()
{

}


bool CtiFDR_Rccs::isAMaster (int aID)
{
    bool retVal = false;
        bool standbyFailFlag = false;

    // this is ugly but who would have thought someone wouldn't buy failover
    if (iStandalone)
    {
        // standalone is always the master
        retVal = true;
    }
    else
    {
        switch (aID)
        {
            case 1:
            case 3:
            case 5:
            case 7:
            case 9:
                // check for authorization for this process
                if (iAuthorizationFlags & (1 << (aID - 1)))
                {
                    // since this is an odd number and a master, check the standby's status
                    if (iAuthorizationFlags & (1 << aID))
                    {
                        standbyFailFlag = true;
                        retVal = false;
                    }
                    else
                        retVal=true;
                }
                else
                    retVal = false;
                break;
            case 2:
            case 4:
            case 6:
            case 8:
            case 10:
                // check for authorization for this process
                if (iAuthorizationFlags & (1 << (aID - 1)))
                    retVal=true;
                else
                    retVal=false;
                break;
        }

        // log file printouts
        if (!retVal)
        {
            // if standby failed, log appropriately
            if (standbyFailFlag)
            {
                CTILOG_ERROR(dout, "RCCS"<< aID <<" has been failed by its standby, command rejected");
            }
            else
            {
                CTILOG_ERROR(dout, "RCCS"<< aID <<" is not an authorized master, command rejected");
            }
        }
    }
    return retVal;
}

CtiFDR_Rccs& CtiFDR_Rccs::setAuthorizationFlag (int aID, bool aFlag)
{
    string            desc;
    string            action;
    CHAR                id[10];

    itoa (aID,id,10);
    // check for master mode
    if (aFlag)
    {
        // check if we were standby before
        if (!(iAuthorizationFlags & (1 << (aID - 1))))
        {
            CTILOG_INFO(dout, "RCCS" << aID <<" changing from BACKUP to MASTER");

            action = string ("RCCS") + string(id) + string (" : is now MASTER");
            desc = string ("RCCS") + string (id) + string (" has changed from BACKUP to MASTER");
            logEvent (desc,action, true);
        }

        //set flag to true
        iAuthorizationFlags |= (1 << (aID - 1));
    }
    else
    {
        // check if we were master/standby before
        if (iAuthorizationFlags & (1 << (aID - 1)))
        {
            CTILOG_INFO(dout, "RCCS" << aID <<" changing from MASTER to BACKUP");

            action = string ("RCCS") + string (id) + string (" : is now BACKUP");
            desc = string ("RCCS") + string (id) + string (" has changed from MASTER to BACKUP");
            logEvent (desc,action,true);
        }

        //set flag to false
        iAuthorizationFlags &= (~(1 << (aID - 1)));
    }

    return *this;
}

int CtiFDR_Rccs::resolvePairNumber(string &aPair)
{
    int retVal = 0;

    if (ciStringEqual(aPair,string (RCCS_PAIR_ONE)))
        retVal = 1;
    else if (ciStringEqual(aPair,string (RCCS_PAIR_TWO)))
        retVal = 2;
    else if (ciStringEqual(aPair,string (RCCS_PAIR_THREE)))
        retVal = 3;
    else if (ciStringEqual(aPair,string (RCCS_PAIR_FOUR)))
        retVal = 4;
    else if (ciStringEqual(aPair,string (RCCS_STANDALONE)))
        retVal = 1;


    return retVal;
}

/**************************************************************************
* Function Name: CtiFDR_Inet::sendMessageToForeignSys ()
*
* Description: We must find the appropriate destination first and then do our write
*
***************************************************************************
*/

bool CtiFDR_Rccs::buildAndWriteToForeignSystem (CtiFDRPoint &aPoint )
{
    CtiFDRPoint         point;
    bool retVal = false;
    int  index =0,connectionIndex=-1;
    CHAR *foreignSys=NULL;
    CHAR tempStr[21];

    // now loop thru the many possible destinations for the point
    for (int x=0; x < aPoint.getDestinationList().size(); x++)
    {
        /*************************
        * because we have multiple connections to the same interface,
        * we must find the correct connection(s) before doing our write
        **************************
        */
        int rccsPair=-1;
        string destinationName;
        bool foundFlag = true;

        rccsPair = resolvePairNumber(aPoint.getDestinationList()[x].getDestination());

        // 0 is not valid
        if (rccsPair == 0)
        {
            CTILOG_ERROR(dout, "Invalid RCCS destination pair for point "<< aPoint.getPointID());
        }
        else
        {
            for (int y=1; y < 3; y++)
            {
                /*********************************
                * we know the naming convetions will be pair 1=RCCS1,RCCS2, pair 2=RCCS3,RCCS4, etc
                *
                * a better way to handle this would be to have the point manager load
                * each pair as two destinations, then we wouldn't have to do all this jumping
                * around.  Maybe on the next sweep that can happen
                **********************************
                */
                destinationName = string ("RCCS");
                int tmp ((rccsPair-1)*2+y);
                CHAR id[3];
                destinationName+= string (itoa(tmp,id,10));

                // grab the connection list mutex
                CtiLockGuard<CtiMutex> guard(getConnectionMux());
                connectionIndex = findConnectionByNameInList (destinationName);

                if (connectionIndex != -1)
                {
                    /**************************
                    * we allocate an inet message here and it will be deleted
                    * inside of the write function on the connection
                    ***************************
                    */
                    foreignSys = new CHAR[sizeof (InetInterface_t)];
                    InetInterface_t *ptr = (InetInterface_t *)foreignSys;

                    // make sure we have all the pieces
                    if (foreignSys != NULL)
                    {
                        // put everything in the message
                        ptr->Type = INETTYPEVALUE;
                        getSourceName().resize(INETDESTSIZE,' ');
                        strncpy (ptr->SourceName, getSourceName().c_str(), INETDESTSIZE);

                        CtiTime timestamp(aPoint.getLastTimeStamp());
                        if (timestamp < CtiTime(CtiDate(1,1,2001)))
                        {
                            timestamp = CtiTime();
                        }

                        ptr->msgUnion.value.TimeStamp = (timestamp.seconds());
                        strncpy (ptr->msgUnion.value.DeviceName, aPoint.getDestinationList()[x].getTranslation().c_str(),20);
                        strncpy (ptr->msgUnion.value.PointName, &aPoint.getDestinationList()[x].getTranslation()[20],20);

                        // intercept the batch marker point name here if it exists
                        memset (tempStr,'\0',21);
                        memcpy (tempStr,ptr->msgUnion.value.PointName,20);
                        string point_name (tempStr);
                        point_name.resize(20,' ');
                        string small_point (trim(point_name));
                        if (small_point == iBatchMarkerName)
                        {
                            CTILOG_INFO(dout, "Batch marker being sent to "<< getConnectionList()[connectionIndex]->getName());
                        }

                        /***********************
                        * for exchanging with DSM2 systems
                        * the daylight savings flag is the most significant bit
                        * in the quality, if we are in daylight savings, I need to set the quality
                        * so the receiving side knows the time
                        ************************
                        */
                        ptr->msgUnion.value.Quality = YukonToForeignQuality (aPoint.getQuality());
                        if (timestamp.isDST())
                        {
                            ptr->msgUnion.value.Quality |= DSTACTIVE;
                        }

                         // need to intercept sending a status point to make it DSM2 like
                        switch (aPoint.getPointType())
                        {
                            case CalculatedStatusPointType:
                            case StatusPointType:
                                {
                                    ptr->msgUnion.value.Value = aPoint.getValue()+1;
                                    break;
                                }
                            default:
                                ptr->msgUnion.value.Value = aPoint.getValue();
                                break;
                        }

                        ptr->msgUnion.value.AlarmState = ClientErrors::None;

                        /**************************
                        * if we get this far, the connection list must exist so no null check
                        * required (memory is consumed no matter what if we get this far)
                        ***************************
                        */
                        memset (tempStr,'\0',21);
                        memcpy (tempStr,ptr->SourceName,INETDESTSIZE);
                        string clientName(tempStr);

                        memset (tempStr,'\0',21);
                        memcpy (tempStr,ptr->msgUnion.value.DeviceName,20);
                        string deviceName(tempStr);

                        memset (tempStr,'\0',21);
                        memcpy (tempStr,ptr->msgUnion.value.PointName,20);
                        string pointName(tempStr);

                        // memory is consumed no matter what
                        if (!getConnectionList()[connectionIndex]->write (foreignSys))
                        {
                            clientName.resize(INETDESTSIZE,' ');
                            deviceName.resize(20,' ');
                            pointName.resize(20,' ');

                            if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                            {
                                CTILOG_DEBUG(dout, trim(deviceName) <<" "<< trim(pointName) <<" sent to "<< getConnectionList()[connectionIndex]->getName());
                            }

                            // successfully sent message
                            retVal = true;
                        }
                    }
                }

                // again ugly but it should be effective
                if (iStandalone)
                {
                    // we are a standalone master, there are no other clients
                    break;
                }
            }
        }
    }
    return retVal;
}

bool  CtiFDR_Rccs::findAndInitializeClients( void )
{
    int destEntries=0, connEntries=0, index=0;
    CtiFDRClientConnection *clientConnection = NULL;
    DWORD semRet;
    BYTE *ptr;
    bool retVal=true,foundFlag=false;
    string destinationName("RCCS");
    string            action;
    string            desc;

    CtiLockGuard<CtiMutex> destGuard(getClientListMux());
    CtiLockGuard<CtiMutex> guard(getConnectionMux());

    destEntries = getClientList().size();
    connEntries = getConnectionList().size();

    for (int x=0; x < destEntries; x++)
    {
        /********************************
        * we now must support a standalone system so there will not
        * always be two entries for each pair
        * since this is a hack anyway, I'm just going to break out at
        * the bottom of the loop early
        *********************************
        */
        for (int y=1; y < 3; y++)
        {
            // we know the naming convetions will be pair 1=RCCS1,RCCS2, pair 2=RCCS3,RCCS4, etc
            destinationName = string ("RCCS");
            int tmp (x*2+y);
            CHAR id[3];
            destinationName+= string (itoa(tmp,id,10));

            foundFlag = false;

            // look in our list for our name
            for (int z = 0; z < connEntries; z++)
            {
                // if the names match
                if(ciStringEqual(getConnectionList()[z]->getName(),destinationName))
                {
                    foundFlag = true;
                }
            }

            // if we found the connection do nothing, otherwise re-init
            if (foundFlag == false)
            {
                CtiFDRSocketLayer *layer = new CtiFDRSocketLayer (destinationName, CtiFDRSocketLayer::Client_Multiple, this);
                retVal = layer->init();

                if (!retVal )
                {
                    layer->setLinkStatusID(getClientLinkStatusID(destinationName));
                    retVal = layer->run();

                    if (retVal)
                    {
                        retVal = false;
                        delete layer;

                        if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                        {
                            CTILOG_DEBUG(dout, "Initialization failed for "<< destinationName);
                        }
                    }
                    else
                    {
                        const string connAddrStr = layer->getOutBoundConnection()->getAddr().toString();

                        CTILOG_INFO(dout, "Client connection initialized for "<< destinationName <<" at "<< connAddrStr);

                        desc = destinationName + string ("'s client link has been established at ") + connAddrStr;
                        logEvent (desc,action, true);
                        getConnectionList().push_back (layer);
                    }
                }
                else
                {
                    if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                    {
                       CTILOG_DEBUG(dout, "Initialization failed for "<< destinationName);
                    }
                    delete layer;
                    retVal = false;
                }
            }
            // yuck don't ever do this in real code
            if (iStandalone)
            {
                // one client
                break;
            }
        }
    }
    return retVal;
}

void CtiFDR_Rccs::setCurrentClientLinkStates()
{
    int destEntries = getClientList().size();
    int connEntries = getConnectionList().size();
    long linkID;
    bool foundClient;

    for (int x=0; x < destEntries; x++)
    {
        for (int y=1; y < 3; y++)
        {
            // we know the naming convetions will be pair 1=RCCS1,RCCS2, pair 2=RCCS3,RCCS4, etc
            string destinationName = string ("RCCS");
            int tmp (x*2+y);
            CHAR id[3];
            destinationName+= string (itoa(tmp,id,10));

            foundClient = false;
            linkID = getClientLinkStatusID (destinationName);

            // look in our list for our name
            for (int y = 0; y < connEntries; y++)
            {
                // if the names match
                if(ciStringEqual(getConnectionList()[y]->getName(),destinationName))
                {
                    getConnectionList()[y]->setLinkStatusID(linkID);
                    getConnectionList()[y]->sendLinkState (FDR_CONNECTED);
                    foundClient = true;
                }
            }

            // if we didn't find the client, send not connected
            if ((!foundClient) && (linkID))
            {
                CtiPointDataMsg     *pData;
                pData = new CtiPointDataMsg(linkID,
                                            FDR_NOT_CONNECTED,
                                            NormalQuality,
                                            StatusPointType);
                sendMessageToDispatch (pData);
            }

            // yuck don't ever do this in real code
            if (iStandalone)
            {
                // one client
                break;
            }
        }
    }
}

/*************************************************
* Function Name: CtiFDR_Rccs::readConfig()
*
* Description: loads cparm config values
*
**************************************************
*/
bool CtiFDR_Rccs::readConfig()
{
    string   tempStr;


    tempStr = getCparmValueAsString(KEY_LISTEN_PORT_NUMBER);
    if (tempStr.length() > 0)
    {
        setPortNumber (atoi(tempStr.c_str()));
    }
    else
    {
        setPortNumber (INET_PORTNUMBER);
    }

    tempStr = getCparmValueAsString(KEY_CONNECT_PORT_NUMBER);
    if (tempStr.length() > 0)
    {
        setConnectPortNumber (atoi(tempStr.c_str()));
    }
    else
    {
        setConnectPortNumber (getPortNumber());
    }


    tempStr = getCparmValueAsString(KEY_TIMESTAMP_WINDOW);
    if (tempStr.length() > 0)
    {
        setTimestampReasonabilityWindow (atoi (tempStr.c_str()));
    }
    else
    {
        setTimestampReasonabilityWindow (120);
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

    tempStr = getCparmValueAsString(KEY_SOURCE_NAME);
    if (tempStr.length() > 0)
    {
        setSourceName (tempStr);
    }
    else
    {
        setSourceName (string("YUKON"));
    }

    tempStr = getCparmValueAsString(KEY_BATCH_MARKER_NAME);
    if (tempStr.length() > 0)
    {
        iBatchMarkerName = (tempStr);
    }
    else
    {
        iBatchMarkerName = string ("RCCSSTART");
    }

    tempStr = getCparmValueAsString(KEY_STANDALONE);
    if (tempStr.length() > 0)
    {
        iStandalone = true;
    }
    else
    {
        iStandalone = false;
    }


    // default only, data from rccs is all controls so they don't get queued
    setQueueFlushRate (1);

    if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
    {
        Cti::FormattedList loglist;

        loglist.add("RCCS listen port number")  << getPortNumber();
        loglist.add("RCCS connect port number") << getConnectPortNumber();
        loglist.add("RCCS timestamp window")    << getTimestampReasonabilityWindow();
        loglist.add("RCCS db reload rate")      << getReloadRate();
        loglist.add("RCCS source name")         << getSourceName();
        loglist.add("RCCS batch marker name")   << iBatchMarkerName;

        if (iStandalone)
        {
            loglist <<"RCCS running in standalone mode";
        }
        else
        {
            loglist <<"RCCS running in failover mode";
        }


    }

    return true;
}

int CtiFDR_Rccs::processMessageFromForeignSystem(CHAR *aBuffer)
{
    int retVal = ClientErrors::None;

    InetInterface_t *data = (InetInterface_t*)aBuffer;
    string clientName(data->SourceName);
    string deviceName(data->msgUnion.value.DeviceName);
    string pointName(data->msgUnion.value.PointName);

    clientName.resize(INETDESTSIZE,' ');
    deviceName.resize(20,' ');
    pointName.resize(20,' ');

    switch (data->Type)
    {
        case INETTYPESHUTDOWN:
            {
                if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                {
                    CTILOG_DEBUG(dout, "Shutdown message received from "<< clientName);
                }
                // nothing to do if we're standalone
                if (!iStandalone)
                {
                    string temp (char2string(data->SourceName[4]));
                    setAuthorizationFlag (atoi(temp.c_str()), FALSE);
                }
                break;
            }
        case INETTYPENULL:
            {
                if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
                {
                    CTILOG_DEBUG(dout, "Heartbeat message received from " << clientName);
                }

                break;
            }
        case INETTYPEVALUE:
            {
                if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                {
                    CTILOG_DEBUG(dout, trim(deviceName) <<" "<< trim(pointName) <<" received from "<< trim(clientName));
                }
                retVal = processValueMessage (data);
                break;
            }
        default:
            {
                CTILOG_ERROR(dout, "Default or invalid type from "<< clientName <<" type "<< data->Type);

                // process them all as value messages
                retVal = processValueMessage (data);
                break;
            }

    }
    return retVal;

}

int CtiFDR_Rccs::processValueMessage(InetInterface_t *data)
{
    int retVal = ClientErrors::None;
    CtiPointDataMsg     *pData;
    string           translationName (data->msgUnion.value.DeviceName);
    int                 quality;
    DOUBLE              value;
    CtiTime              timestamp;
    CtiFDRPoint         point;
    bool                flag = true;
    string temp (char2string(data->SourceName[4]));
    CHAR                 action[60];
    string            desc;

    string deviceName (data->msgUnion.value.DeviceName);
    string pointName (data->msgUnion.value.PointName);
    translationName.resize(20,' ');
    deviceName.resize(20,' ');
    pointName.resize(20,' ');

    // check for our special device names
    if (ciStringEqual(deviceName,string (RCCSDEVICEPRIMARY))
        || ciStringEqual(deviceName,string (RCCSDEVICESTANDBY)))
    {
        //we've got a primary, check his status
        if (ciStringEqual(pointName,string (RCCSPOINTMASTER)))
        {

            setAuthorizationFlag (atoi(temp.c_str()), TRUE);
        }
        else
        {
            setAuthorizationFlag (atoi(temp.c_str()), FALSE);
        }
    }
    else
    {
        if (isAMaster (atoi(temp.c_str())))
        {
            string tmp = string (data->msgUnion.value.PointName);
            tmp.resize(20,' ');

            // convert to our name
            translationName += tmp;

            // see if the point exists
            flag = findTranslationNameInList (translationName, getReceiveFromList(), point);

            if ((flag == true) && (point.getPointType() == StatusPointType) && (point.isControllable()))
            {
                int controlState=-1;

                // make sure the value is valid
                if (data->msgUnion.value.Value == Inet_Open)
                {
                    controlState = STATE_OPENED;
                }
                else if (data->msgUnion.value.Value == Inet_Closed)
                {
                    controlState = STATE_CLOSED;
                }
                else
                {
                    CTILOG_ERROR(dout, "Invalid control state "<< data->msgUnion.value.Value <<" for "<< translationName <<" received from RCCS");

                    CHAR state[20];
                    _snprintf (state,20,"%.0f",data->msgUnion.value.Value);
                    desc = decodeClientName((CHAR*)data) + string (" control point received with an invalid state ") + string (state);
                    _snprintf(action,60,"%s for pointID %d",
                              translationName.c_str(),
                              point.getPointID());
                    logEvent (desc,string (action));
                    retVal = ClientErrors::Abnormal;
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
                    CtiCommandMsg *cmdMsg;
                    cmdMsg = new CtiCommandMsg(CtiCommandMsg::ControlRequest);

                    cmdMsg->insert( -1 );                // This is the dispatch token and is unimplemented at this time
                    cmdMsg->insert(0);                   // device id, unknown at this point, dispatch will find it
                    cmdMsg->insert(point.getPointID());  // point for control
                    cmdMsg->insert(controlState);
                    sendMessageToDispatch(cmdMsg);
                }
            }
            else
            {
                if (flag == false)
                {
                    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                    {
                        CTILOG_DEBUG(dout, "Translation for control point "<< translationName <<" from "<< getInterfaceName() <<
                                " was not found");

                        desc = decodeClientName((CHAR*)data) + string (" control point is not listed in the translation table");
                        _snprintf(action,60,"%s", translationName.c_str());
                        logEvent (desc,string (action));
                    }
                }
                else if (!point.isControllable())
                {
                    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                    {
                        CTILOG_DEBUG(dout, "Control point "<< translationName <<" received from "<< getInterfaceName() <<
                                " was not configured receive for control for point "<< point.getPointID());

                        desc = decodeClientName((CHAR*)data) + string (" control point is not configured to receive controls");
                        _snprintf(action,60,"%s for pointID %d",
                                  translationName.c_str(),
                                  point.getPointID());
                        logEvent (desc,string (action));
                    }
                }
                else
                {
                    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                    {
                        CTILOG_DEBUG(dout, "Control point "<< translationName <<" received from "<< getInterfaceName() <<
                                " was mapped to non-control point "<< point.getPointID());

                        CHAR pointID[20];
                        desc = decodeClientName((CHAR*)data) + string (" control point is incorrectly mapped to point ") + string (ltoa(point.getPointID(),pointID,10));
                        _snprintf(action,60,"%s", translationName.c_str());
                        logEvent (desc,string (action));
                    }
                }

                retVal = ClientErrors::Abnormal;
            }
        }
    }
    return retVal;
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
        rccsInterface = new CtiFDR_Rccs();

        // now start it up
        return rccsInterface->run();
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

        rccsInterface->stop();
        delete rccsInterface;
        rccsInterface = 0;

        return 0;
    }


#ifdef __cplusplus
}
#endif





