#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*-----------------------------------------------------------------------------*
*
*    FILE NAME: fdrxa21lm.cpp
*
*    DATE: 11/21/04
*
*    AUTHOR: Aaron Lauinger
*
*    Copyright (C) 2004 Cannon Technologies, Inc.  All rights reserved.
*-----------------------------------------------------------------------------*
*/

#include <windows.h>
#include <iostream>

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

#include <stdio.h>

/** include files **/
#include <rw/cstring.h>
#include <rw/ctoken.h>
#include <rw/rwtime.h>
#include <rw/rwdate.h>

#include "cparms.h"
#include "msg_multi.h"
#include "msg_ptreg.h"
#include "msg_cmd.h"
#include "message.h"
#include "msg_reg.h"
#include "msg_ptreg.h"
#include "msg_pdata.h"
#include "msg_signal.h"
#include "connection.h"
#include "pointtypes.h"
#include "dllbase.h"
#include "logger.h"
#include "guard.h"
#include "fdrsocketinterface.h"
#include "fdrpointlist.h"
#include "fdrsinglesocket.h"
#include "fdrsocketlayer.h"
#include "fdrserverconnection.h"

// this class header
#include "fdrxa21lm.h"

/** local definitions **/

/** global used to start the interface by c functions **/
CtiFDR_XA21LM * xa21lmInterface;

const CHAR * CtiFDR_XA21LM::KEY_LISTEN_PORT_NUMBER = "FDR_XA21LM_PORT_NUMBER";
const CHAR * CtiFDR_XA21LM::KEY_TIMESTAMP_WINDOW = "FDR_XA21LM_TIMESTAMP_VALIDITY_WINDOW";
const CHAR * CtiFDR_XA21LM::KEY_DB_RELOAD_RATE = "FDR_XA21LM_DB_RELOAD_RATE";
const CHAR * CtiFDR_XA21LM::KEY_QUEUE_FLUSH_RATE = "FDR_XA21LM_QUEUE_FLUSH_RATE";
const CHAR * CtiFDR_XA21LM::KEY_DEBUG_MODE = "FDR_XA21LM_DEBUG_MODE";
const CHAR * CtiFDR_XA21LM::KEY_OUTBOUND_SEND_RATE = "FDR_XA21LM_SEND_RATE";
const CHAR * CtiFDR_XA21LM::KEY_OUTBOUND_SEND_INTERVAL = "FDR_XA21LM_SEND_INTERVAL";

// Constructors, Destructor, and Operators
CtiFDR_XA21LM::CtiFDR_XA21LM()
: CtiFDRSingleSocket(RWCString("XA21LM"))
{
    setRegistered (true); 
    init();
}


CtiFDR_XA21LM::~CtiFDR_XA21LM()
{
}

bool CtiFDR_XA21LM::isRegistrationNeeded()
{
    return false;
}

/*************************************************
* Function Name: CtiFDR_XA21LM::readConfig()
*
* Description: loads cparm config values
* 
**************************************************
*/
int CtiFDR_XA21LM::readConfig()
{    
    int         successful = TRUE;
    RWCString   tempStr;

    tempStr = getCparmValueAsString(KEY_LISTEN_PORT_NUMBER);
    if (tempStr.length() > 0)
    {
        setPortNumber (atoi(tempStr));
    }
    else
    {
        setPortNumber (XA21LMPORTNUMBER);
    }

    tempStr = getCparmValueAsString(KEY_TIMESTAMP_WINDOW);
    if (tempStr.length() > 0)
    {
        setTimestampReasonabilityWindow (atoi (tempStr));
    }
    else
    {
        setTimestampReasonabilityWindow (120);
    }

    tempStr = getCparmValueAsString(KEY_DB_RELOAD_RATE);
    if (tempStr.length() > 0)
    {
        setReloadRate (atoi(tempStr));
    }
    else
    {
        setReloadRate (86400);
    }

    tempStr = getCparmValueAsString(KEY_QUEUE_FLUSH_RATE);
    if (tempStr.length() > 0)
    {
        setQueueFlushRate (atoi(tempStr));
    }
    else
    {
        // default to 1
        setQueueFlushRate (1);
    }

    tempStr = getCparmValueAsString(KEY_DEBUG_MODE);
    if (tempStr.length() > 0)
        setInterfaceDebugMode (true);
    else
        setInterfaceDebugMode (false);


    tempStr = getCparmValueAsString(KEY_OUTBOUND_SEND_RATE);
    if (tempStr.length() > 0)
    {
        setOutboundSendRate (atoi(tempStr));
    }
    else
    {
        // default to 1
        setOutboundSendRate (1);
    }
    tempStr = getCparmValueAsString(KEY_OUTBOUND_SEND_INTERVAL);
    if (tempStr.length() > 0)
    {
        setOutboundSendInterval (atoi(tempStr));
    }
    else
    {
        // default to 1
        setOutboundSendInterval (0);
    }

    if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " xa21lm port number " << getPortNumber() << endl;
        dout << RWTime() << " xa21lm timestamp window " << getTimestampReasonabilityWindow() << endl;
        dout << RWTime() << " xa21lm db reload rate " << getReloadRate() << endl;
        dout << RWTime() << " xa21lm qubeue flush rate " << getQueueFlushRate() << " second(s) " << endl;
        dout << RWTime() << " xa21lm send rate " << getOutboundSendRate() << endl;
        dout << RWTime() << " xa21lm send interval " << getOutboundSendInterval() << " second(s) " << endl;

        if (isInterfaceInDebugMode())
            dout << RWTime() << " running in debug mode " << endl;
        else
            dout << RWTime() << " running in normal mode "<< endl;
    }
    return successful;
}

/*
 * Replaces the translation string stored in the database with what we should match when a point
 * change comes in.
 */
bool CtiFDR_XA21LM::translateAndUpdatePoint(CtiFDRPoint *translationPoint, int aDestinationIndex)
{
    bool                successful(false);
    RWCString           tempString1;
    RWCString           tempString2;
    RWCString           translationName;
    bool                foundPoint = false;

    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime() << " - " << "TranslateAndUpdatePoint" << endl;
    }
    try
    {
        RWCTokenizer nextTranslate(translationPoint->getDestinationList()[aDestinationIndex].getTranslation());

            // skip the first part entirely for now
        if (!(tempString1 = nextTranslate(";")).isNull())
        {
            // this in the translation name on rdex
            RWCTokenizer nextTempToken(tempString1);

            // do not care about the first part
            nextTempToken(":");
            tempString2 = nextTempToken(";");
            /**************************
            * as luck would have it, I need to allow colons in the translation names
            * because of this, I know the first character in tempString2 is the first : 
            * found.  I need what is left in the string after removing the colon
            ***************************
            */
            tempString2(0,tempString2.length()) = tempString2 (1,(tempString2.length()-1));

            // now we have a translation name
            if ( !tempString2.isNull() )
            {
                // put category in final name
                translationName= tempString2;

    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime() << " - " << "setting translation to: " << tempString2 << endl;
    }
                // i'm updating my copied list
                translationPoint->getDestinationList()[aDestinationIndex].setTranslation (tempString2);
                successful = true;
            }   // no point name
        }   // first token invalid
    } // end try

    catch (RWExternalErr e )
    {
        getLayer()->setInBoundConnectionStatus (CtiFDRSocketConnection::Failed );
        getLayer()->setOutBoundConnectionStatus (CtiFDRSocketConnection::Failed );
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime () << " " << __FILE__ << " (" << __LINE__ << ") translateAndLoadPoint():  " << e.why() << endl;
        RWTHROW(e);
    }

    // try and catch the thread death
    catch ( ... )
    {
        getLayer()->setInBoundConnectionStatus (CtiFDRSocketConnection::Failed );
        getLayer()->setOutBoundConnectionStatus (CtiFDRSocketConnection::Failed );
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime () << " " << __FILE__ << " (" << __LINE__ << ") translateAndLoadPoint():  (...) "<< endl;
    }
    return successful;
}
#ifdef EATSHIT
bool CtiFDR_XA21LM::buildAndWriteToForeignSystem(CtiFDRPoint &aPoint)
{
    //write to queue? so that thread can match status point changes with
    //pending controls
    return false;
}
#endif

CHAR *CtiFDR_XA21LM::buildForeignSystemMsg ( CtiFDRPoint &aPoint )
{
    CHAR *buffer= new CHAR[sizeof(XA21LMMESS)];
    XA21LMMESS* lm_msg = (XA21LMMESS*) buffer;

    lm_msg->Function = htonl(MPCUNSOLICITEDCTRLSTATUS);
    YukonToXA21Time(time(NULL), RWTime::now().isDST(), &(lm_msg->Time));
    lm_msg->Message.MPC.AreaCode = 0;
    lm_msg->Message.MPC.IDNumber = 0;
    strcpy(lm_msg->Message.MPC.Group[0].GroupName, aPoint.getTranslateName(0));

    if(aPoint.getValue() == CLOSED)
    {
	lm_msg->Message.MPC.Group[0].State = MPCSHED;
    }
    else
    {
    	lm_msg->Message.MPC.Group[0].State = MPCRESTORE;
    }

    lm_msg->Message.MPC.Group[0].State |= MPCFINISHED;
    lm_msg->Message.MPC.Group[0].State = htons(lm_msg->Message.MPC.Group[0].State);
    lm_msg->Message.MPC.Group[0].ShedSequence = 0;

    dumpXA21LMMessage(lm_msg);

    return buffer;
}


CHAR *CtiFDR_XA21LM::buildForeignSystemHeartbeatMsg ()
{
    XNULL* null_msg = (XNULL*) new CHAR[sizeof(XNULL)];
    null_msg->Function = 0;
    
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime() << " - " << "building foreign system heartbeat!" << endl;
    }
    
    return (CHAR*) null_msg;
}

int CtiFDR_XA21LM::getMessageSize(CHAR *aBuffer)
{
    XA21LMMESS* xa_msg = (XA21LMMESS*) aBuffer;
    unsigned msg_size = 0;
     
    switch(ntohl(xa_msg->Function))
    {
    case 0:
	msg_size = sizeof(XNULL);
	break;
    case MPCNICK:
    case MPCNICKSTATUS:
    case MPCNICKCOMPLETE:
    case MPCSCRAM:
    case MPCSCRAMSTATUS:
    case MPCSCRAMCOMPLETE:
    case MPCSTRATEGY:
    case MPCSTRATEGYSTATUS:
    case MPCSTRATEGYCOMPLETE:
    case MPCDAILYSTRAT:
    case MPCDAILYSTRATSTATUS:
    case MPCDAILYSTRATCOMPLETE:
    case MPCCONFIRMCTRL:
    case MPCCONFIRMCTRLSTATUS:
    case MPCCONFIRMCTRLCOMPLETE:
    case MPCIMMEDIATECTRL:
    case MPCIMMEDIATECTRLSTATUS: 
    case MPCIMMEDIATECTRLCOMPLETE:
    case MPCIMMEDIATECTRL_CONTROL_ACK:
    case MPCUNSOLICITEDCTRLSTATUS:
	msg_size = sizeof (XA21LMMESS);
	break;
    default:
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime() << " - " << "Received unknown function from lmsdlink = " << ntohl(xa_msg->Function) << endl;
    }
    }
    
   {
        CtiLockGuard<CtiLogger> dout_guard(dout);
	dout << RWTime() << " Determining message size, fn=" << ntohl(xa_msg->Function) << " size=" << msg_size << endl;
    }
    return msg_size;
}

/*
  Give this interface a name
*/
RWCString CtiFDR_XA21LM::decodeClientName(CHAR * aBuffer)
{
    return "XA21LM";
}

/*
 * A message came in, deal with it
 */
int CtiFDR_XA21LM::processMessageFromForeignSystem(CHAR *aBuffer)
{
    int retVal = NORMAL;
    XA21LMMESS *lm_mess = (XA21LMMESS*)aBuffer;
    
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime() << " - " << "Message received!" << endl;
	dout << " Function: " << ntohl(lm_mess->Function) << endl;
	dout << " Time: " << ntohl(lm_mess->Time.Time) << endl;
    }

    switch(ntohl(lm_mess->Function))
    {
    case 0:
	if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
	{
	    CtiLockGuard<CtiLogger> doubt_guard(dout);
	    dout << RWTime() << " Heartbeat message received from " << getLayer()->getName() << " at " << RWCString (inet_ntoa(getLayer()->getInBoundConnection()->getAddr().sin_addr)) <<  endl;;
	}
	break;
    case MPCIMMEDIATECTRL:
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime() << " - " <<  " Received immediate control message" << endl;

	retVal = processImmediateControlMessage(aBuffer);
    }
    break;
    case MPCIMMEDIATECTRL_CONTROL_ACK:
	
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime() << " - " <<  "lmsdlnk echoed MPCIMMEDIATECTRL_CONTROL_ACK - good" << endl;
    }
    break;
    
    default:
	if (getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL)
	{
	    CtiLockGuard<CtiLogger> doubt_guard(dout);
	    dout << RWTime() << " Unknown message type " << ntohl (lm_mess->Function) << " received from " << getInterfaceName() << endl;
	}
    }
    return retVal;
}

int CtiFDR_XA21LM::processRegistrationMessage(CHAR *aData)
{
    int retVal = !NORMAL;
    return retVal;
}

int CtiFDR_XA21LM::processImmediateControlMessage(CHAR *aData)
{
    int retVal = !NORMAL;
    XA21LMMESS* xa21_msg = (XA21LMMESS*) aData;
    CtiFDRPoint point;
    
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime() << " - " <<  " processImmediate areacode=" << xa21_msg->Message.MPC.AreaCode << " IDNumber=" << xa21_msg->Message.MPC.IDNumber << endl;
    }

    // First lets ack the request
    
    for(int i = 0; i < MPCMAXGROUPS; i++)
    {
	
    RWCString translation_name = xa21_msg->Message.MPC.Group[i].GroupName;
    USHORT control_state = ntohs(xa21_msg->Message.MPC.Group[i].State);
    USHORT shed_seq = ntohs(xa21_msg->Message.MPC.Group[i].ShedSequence);

	
    translation_name = translation_name.strip(RWCString::both);
    
    if(findTranslationNameInList(translation_name.data(), getReceiveFromList(), point))
    {
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime() << " - " <<  " PointID=" << point.getPointID() << " Group name=" << translation_name << " State=" << control_state << " ShedSeq=" << xa21_msg->Message.MPC.Group[i].ShedSequence << endl;
    }

    CtiCommandMsg *cmdMsg;
    cmdMsg = new CtiCommandMsg(CtiCommandMsg::ControlRequest);
    
    cmdMsg->insert( -1 );                // This is the dispatch token and is unimplemented at this time
    cmdMsg->insert(0);                   // device id, unknown at this point, dispatch will find it
    cmdMsg->insert(point.getPointID());  // point for control
    if(control_state & MPCSHED)
    {
	cmdMsg->insert(CLOSED);       
    }
    else if(control_state & MPCRESTORE)
    {
        cmdMsg->insert(OPENED);       
    }
    else
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime() << " **Checkpoint** " << "Control state didn't specify MPCSHED or MPCRESTORE" << __FILE__ << "(" << __LINE__ << ")" << endl;
    }

    cmdMsg->dump();
    sendMessageToDispatch(cmdMsg);
    
    // send ack
    XA21LMMESS* xa21_ack_msg = (XA21LMMESS*) new CHAR[sizeof(XA21LMMESS)];

    // send the same message back, but turn it into an ack
    memcpy(xa21_ack_msg, xa21_msg, sizeof(XA21LMMESS));
    xa21_ack_msg->Function = htonl(MPCIMMEDIATECTRL_CONTROL_ACK);

    if(getLayer()->write((char*)xa21_ack_msg))
    {
	retVal = !NORMAL;
    }
    else
    {
	retVal = NORMAL;
    }

    }//end findTranslation
    else
    {
	if(translation_name.length() > 0) {
        CtiLockGuard<CtiLogger> dout_guard(dout);	    
	dout << RWTime() << " Unable to find translation: " << translation_name << " len: " << translation_name.length() << endl;
	retVal = !NORMAL;
	}
    }
    }

    return retVal;
}

void CtiFDR_XA21LM::MPCStatusUpdateThr()
{
    try
    {
	while(true)
	{
	    Sleep(30000);
	
	    //check for complete controls (all types) send an mpc complete message
	    //and remove that command from future consideration

	    //check for timed out controls
	    //send a mpc complete message

	    //send a status for all pending controls

	    // That is how it will work, for now just send unsolicted
	
	}
    }
    catch ( RWCancellation &cancellationMsg )
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime() << " - " <<  "MPStatusUpdateThr exiting" << endl;
    }
    catch( ... )
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime() << " **Checkpoint** " << "MPCStatusUpdateThr catch(...) unexpected" << __FILE__ << "(" << __LINE__ << ")" << endl;
    }
}

ULONG CtiFDR_XA21LM::ForeignToYukonStatus(ULONG aStatus)
{
    return ntohl(aStatus);
}

ULONG CtiFDR_XA21LM::YukonToForeignStatus(ULONG aStatus)
{
    return htonl(aStatus);
}

ULONG CtiFDR_XA21LM::YukonToXA21Time (time_t time, bool my_dst_flag, XA21TIME* XA21Time)
{
    XA21Time->Time = htonl (time);
    XA21Time->DSTFlag = (my_dst_flag ? htons (1) : htons(0));
    XA21Time->MilliSeconds = htons (0);

    return (NORMAL);
}

void CtiFDR_XA21LM::dumpXA21LMMessage(XA21LMMESS* xa21_msg)
{
    CtiFDRPoint         point;
	
    CtiLockGuard<CtiLogger> dout_guard(dout);
    dout << RWTime() << " - " <<  "MPC Message, Fn=" << ntohl(xa21_msg->Function) << endl;

    for(int i = 0; i < MPCMAXGROUPS; i++)
    {
	string translation_name = xa21_msg->Message.MPC.Group[i].GroupName;
	USHORT control_state = ntohs(xa21_msg->Message.MPC.Group[i].State);
	USHORT shed_seq = ntohs(xa21_msg->Message.MPC.Group[i].ShedSequence);
    
	if(findTranslationNameInList(translation_name.data(), getReceiveFromList(), point))
	{
	    dout << RWTime() << " - " <<  " Group name=" << translation_name << " State=" << control_state << " ShedSeq=" << shed_seq << endl;
	}
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
        xa21lmInterface = new CtiFDR_XA21LM();

        // now start it up
        return xa21lmInterface->run();
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

        xa21lmInterface->stop();
        delete xa21lmInterface;
        xa21lmInterface = 0;

        return 0;
    }


#ifdef __cplusplus
}
#endif



