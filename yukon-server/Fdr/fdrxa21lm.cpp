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
#include "yukon.h"

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
#include "numstr.h"
#include "fdrsocketinterface.h"
#include "fdrpointlist.h"
#include "fdrsinglesocket.h"
#include "fdrsocketlayer.h"
#include "fdrserverconnection.h"

// this class header
#include "fdrxa21lm.h"

/*
 * MPCFunction corresponds with the function sent from lmsdlnk in
 * the XA21LMMESS structure.
 */
ULONG CurrentControl::getMPCFunction() const
{
    return _mpc_function;
}

vector<string> CurrentControl::getPendingGroups() const
{
    vector<string> pending_vec;
    for( vector< pair<string, unsigned> >::const_iterator i = _pending_groups.begin();
	 i != _pending_groups.end();
	 i++)
    {
	pending_vec.push_back(i->first);
    }
    return pending_vec;
}

vector<string> CurrentControl::getCompletedGroups() const
{
    vector<string> completed_vec;
    for( vector< pair<string, unsigned> >::const_iterator i = _completed_groups.begin();
	 i != _completed_groups.end();
	 i++)
    {
	completed_vec.push_back(i->first);
    }
    return completed_vec;
}

unsigned CurrentControl::getNumCompletedGroups() const
{
    return _completed_groups.size();
}

unsigned CurrentControl::getNumPendingGroups() const
{
    return _pending_groups.size();
}

/*
 * Returns the commanded state for this group.
 * Or -1 if it couldn't be found
 */
int CurrentControl::getCommandedState(const string& group_name) const
{
    for( vector< pair<string, unsigned> >::const_iterator i = _pending_groups.begin();
	 i != _pending_groups.end();
	 i++)
    {
	if( i->first == group_name ) 
	{
	    return i->second;
	}
    }
    for( vector< pair<string, unsigned> >::const_iterator j = _completed_groups.begin();
	 j != _completed_groups.end();
	 j++)
    {
	if( j->first == group_name )
	{
	    return j->second;
	}
    }

    return -1;
}

/*
 * Tests whether the given group is currently in the pending list,
 * that is, we have not recevied a status update with the commanded state yet
 */
bool CurrentControl::isGroupPending(const string& group_name) const
{
    for( vector< pair<string, unsigned> >::const_iterator i = _pending_groups.begin();
	 i != _pending_groups.end();
	 i++)
    {
	if( i->first == group_name )
	{
	    return true;
	}
    }
    return false;
}

/*
 * This control expires at this time
 */
time_t CurrentControl::getExpirationTime() const
{
    return _expiration_time;
}

/*
 * Moves a group from the pending list to the completed list
 */
bool CurrentControl::setGroupCompleted(const string& group_name)
{
    for( vector< pair<string, unsigned> >::iterator i = _pending_groups.begin();
	 i != _pending_groups.end();
	 i++)
    {
	if( i->first == group_name )
	{
	    pair<string, unsigned> group_to_move = *i;
	    _pending_groups.erase(i);
	    _completed_groups.push_back(group_to_move);
	    _expiration_time += 5*60;
	    return true;
	}
    }

    return false;
}

/*
 * static factory method to create a control, don't use the constructor
 */
CurrentControl CurrentControl::createCurrentControl(XA21LMMESS* lm_msg)
{
    CurrentControl ctrl;
    ctrl._mpc_function = ntohl(lm_msg->Function);
    ctrl._expiration_time = time(NULL) + 5*60;
    for(int i = 0; i < MPCMAXGROUPS; i++)
    {
	string trimmed_name = string(lm_msg->Message.MPC.Group[i].GroupName);
	if(trimmed_name.length() == 0)
	{
	    break;
	}
	trim(trimmed_name);
	
        USHORT intended_state = ntohs(lm_msg->Message.MPC.Group[i].State) ;
	if(intended_state & MPCSHED)
	{
	    intended_state = CLOSED;
	}
	else if(intended_state & MPCRESTORE)
	{
	    intended_state = OPENED;
	}
	ctrl._pending_groups.push_back(make_pair(trimmed_name, intended_state));
    }
    return ctrl;
}

void CurrentControl::dump()
{
    CtiLockGuard<CtiLogger> dout_guard(dout);

    dout << " - MPC Control - " << endl;
    dout << " Function = " << _mpc_function;
    dout << " Expiration = " << _expiration_time << endl;
    dout << " Pending Groups,Commanded Value = " << endl;
    for( vector< pair<string, unsigned> >::iterator i = _pending_groups.begin();
	 i != _pending_groups.end(); i++)
    {
	dout << i->first << "," << i->second << endl;
    }
    dout << " Completed Groups,Commanded Value = " << endl;
    for( vector< pair<string, unsigned> >::iterator j = _completed_groups.begin();
	 j != _completed_groups.end(); j++)
    {
	dout << j->first << "," << j->second << endl;
    }
}

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

/*
 * A point change has been received from Yukon, try to match this up to a group in our current controls list,
 * check to see if the points state matches what we think the commanded state should be.  If that is the case
 * then send a message to lmsdlnk.
 */
CHAR *CtiFDR_XA21LM::buildForeignSystemMsg ( CtiFDRPoint &aPoint )
{
    CtiLockGuard<CtiCriticalSection> cs_lock(_control_cs);

    CHAR *buffer= NULL;
    string group_name = aPoint.getTranslateName(0);
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime() << " - Rec'd point change, point id=" << aPoint.getPointID() << ", value=" << aPoint.getValue() << ", group='" << group_name << "'" << endl;
    }

    // See if we can find a matching group with control pending
    for(vector<CurrentControl>::iterator iter = _current_controls.begin();
	iter != _current_controls.end(); iter++)
    {
	CurrentControl& ctrl = *iter;
	if(ctrl.isGroupPending(group_name))
	{
	    
	{
	    CtiLockGuard<CtiLogger> dout_guard(dout);
	    dout << RWTime() << " checking commanded state against current state: commanded=" << ctrl.getCommandedState(group_name) << " current=" << aPoint.getValue() << endl;
	}
	    //found one, is the point in the commanded state? (i.e. did the state change to what we expected as a result of control?)
	    if(ctrl.getCommandedState(group_name) == aPoint.getValue())
	    {
		buffer = buildMPCStatus(ctrl, group_name);		
		ctrl.setGroupCompleted(group_name);
		if(ctrl.getNumPendingGroups() == 0)
		{
		    dumpXA21LMMessage((XA21LMMESS*) buffer);
		    getLayer()->write(buffer);
		    buffer = buildMPCStatus(ctrl, group_name);
		}
		dumpXA21LMMessage((XA21LMMESS*) buffer); 
		break;
	    } 
	}
    }

    if(buffer == NULL)
    { // We didn't find a matching group in any of the controls we know about
      // send this on unsolicited.
	buffer = buildMPCUnsolicited(group_name, aPoint.getValue()); 
	dumpXA21LMMessage((XA21LMMESS*) buffer);	
    }

    cleanupCurrentControls();

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

    // Do some maintenance if necessary
    cleanupCurrentControls();
    
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
	
    case NICKTESTSTATUS:
	msg_size = sizeof (NICKTESTSTAT);
	break;
	
    case LOADSCRAMSTATUS:
	msg_size = sizeof (LOADSCRAMSTAT);
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
        dout << RWTime() << " Processing xa21lmmess" << endl;
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
    case MPCSCRAM:
    case MPCNICK:
    case MPCCONFIRMCTRL:

	dumpXA21LMMessage((XA21LMMESS*) aBuffer);
    	retVal = processControlMessage(aBuffer);
	
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

int CtiFDR_XA21LM::processControlMessage(CHAR *aData)
{
    int retVal = !NORMAL;
    XA21LMMESS* xa21_msg = (XA21LMMESS*) aData;
    CtiFDRPoint point;
    USHORT area_code = ntohs(xa21_msg->Message.MPC.AreaCode);

    
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime() << " - " <<  "Processing control message, area code=" << area_code << endl;
    }
    
    /* Loop through the xa21lm message and build a command message for each group, stuff it into a multi,
       and ship it to dispatch */
    
    CtiMultiMsg* cmd_msg_multi = new CtiMultiMsg;
    for(int i = 0; i < MPCMAXGROUPS; i++)
    {
	RWCString translation_name = xa21_msg->Message.MPC.Group[i].GroupName;
	USHORT control_state = ntohs(xa21_msg->Message.MPC.Group[i].State);
	USHORT shed_seq = ntohs(xa21_msg->Message.MPC.Group[i].ShedSequence);
	
	translation_name = translation_name.strip(RWCString::both);
	if(translation_name.length() == 0)
	{
	    break;
	}
	
	if(area_code != 0)
	{
	    translation_name = "AC" + CtiNumStr(area_code).zpad(2) + " " + translation_name;
	    strncpy(xa21_msg->Message.MPC.Group[i].GroupName, translation_name.data(), sizeof(xa21_msg->Message.MPC.Group[MPCMAXGROUPS].GroupName));
	}
	
	if(findTranslationNameInList(translation_name.data(), getReceiveFromList(), point))
	{
	    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)	
	    {
		CtiLockGuard<CtiLogger> dout_guard(dout);
		dout << RWTime() << " - " <<  " PointID=" << point.getPointID() << " Group name='" << translation_name << "' State=" << control_state << " ShedSeq=" << xa21_msg->Message.MPC.Group[i].ShedSequence << endl;
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
	    cmd_msg_multi->insert(cmdMsg);


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
    
    if(cmd_msg_multi->getCount() > 0)
    {
        // Remember this control so we can send status updates as status point changes come in
	saveControl(xa21_msg);

	if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
	{
	    CtiLockGuard<CtiLogger> dout_guard(dout);
	    dout << RWTime() << " - " <<  " sending " << cmd_msg_multi->getCount() << " control messages to dispatch" << endl;
	}
	
	sendMessageToDispatch(cmd_msg_multi);
	
	/* Acknowledge the control */
	char* ack_msg = NULL;
	switch(ntohl(xa21_msg->Function))
	{
	case MPCIMMEDIATECTRL:
	{
	    // echo the same message back, but as an immediate ack instead
	    XA21LMMESS* xa21_immediate_ack_msg = (XA21LMMESS*) new CHAR[sizeof(XA21LMMESS)];
	    memcpy(xa21_immediate_ack_msg, xa21_msg, sizeof(XA21LMMESS));
	    xa21_immediate_ack_msg->Function = htonl(MPCIMMEDIATECTRL_CONTROL_ACK);
	    ack_msg = (char*) xa21_immediate_ack_msg;
	    
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime() << " - " << "Sending MPCIMMEDIATE_CONTROL_ACK" << endl;
    }
    
	}
	    break;
		
	case MPCCONFIRMCTRL:
	{
	    // echo the same message back, but as a confirm ack instead
	    XA21LMMESS* xa21_confirm_ack_msg = (XA21LMMESS*) new CHAR[sizeof(XA21LMMESS)];
	    memcpy(xa21_confirm_ack_msg, xa21_msg, sizeof(XA21LMMESS));
	    xa21_confirm_ack_msg->Function = htonl(MPCCONFIRM_CONTROL_ACK);
	    ack_msg = (char*) xa21_confirm_ack_msg;

    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime() << " - " << "Sending MPCCONFIRM_CONTROL_ACK" << endl;
    }	    
	}
	    break;

	case MPCSCRAM:
	{
	    LOADSCRAMSTAT* scram_ack_msg = (LOADSCRAMSTAT*) new CHAR[sizeof(LOADSCRAMSTAT)];
	    scram_ack_msg->Function = htonl(LOADSCRAMSTATUS);
	    YukonToXA21Time(time(NULL), RWTime::now().isDST(), &(scram_ack_msg->Time));
	    scram_ack_msg->State = htons(ACKNOWLEDGE);
	    ack_msg = (char*) scram_ack_msg;

    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime() << " - " << "Sending LOADSCRAMSTATUS (ack)" << endl;
    }	    
	}
	    break;
	case MPCNICK:
	{
	    NICKTESTSTAT* nick_ack_msg = (NICKTESTSTAT*) new CHAR[sizeof(NICKTESTSTAT)];
	    nick_ack_msg->Function = htonl(NICKTESTSTATUS);
    	    YukonToXA21Time(time(NULL), RWTime::now().isDST(), &(nick_ack_msg->Time));
	    nick_ack_msg->State = htons(ACKNOWLEDGE);
	    ack_msg = (char*) nick_ack_msg;

    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime() << " - " << "Sending NICKTESTSTAT (ack)" << endl;
    }	    
	}
	    break;
	default:
	{
	    CtiLockGuard<CtiLogger> dout_guard(dout);
	    dout << RWTime() << " **Checkpoint** " <<  " Received unknown control function of " << xa21_msg->Function << __FILE__ << "(" << __LINE__ << ")" << endl;
	}
	    retVal = !NORMAL;
	}

	if(ack_msg != NULL && getLayer()->write(ack_msg))
	{
	    retVal = !NORMAL;
	}
	else
	{
	    retVal = NORMAL;
	}
    }
    else
    {
        delete cmd_msg_multi;
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime() << " **Checkpoint** " << "Control request received, but no control messages generated and sent to dispatch" << __FILE__ << "(" << __LINE__ << ")" << endl;
    }
    return retVal;
}

/*
 * Factory to create a XA21LMMESS that is a MPCStatus for a given control
 */
CHAR* CtiFDR_XA21LM::buildMPCStatus(const CurrentControl& ctrl, const string& group_name)
{
    CHAR* buffer= new CHAR[sizeof(XA21LMMESS)];
    XA21LMMESS* lm_msg = (XA21LMMESS*) buffer;
    string xalm_group_name = group_name;
    
    if(ctrl.getNumPendingGroups() > 0)
    {
	lm_msg->Function = htonl(ctrl.getMPCFunction() + MPCSTATUS);
    }
    else
    {
	lm_msg->Function = htonl(ctrl.getMPCFunction() + MPCCOMPLETE);
    }
    
    for(int i = 1; i < MPCMAXGROUPS; i++)
    {
	lm_msg->Message.MPC.Group[i].GroupName[0] = '\0';
    }
    
    YukonToXA21Time(time(NULL), RWTime::now().isDST(), &(lm_msg->Time));
    
    lm_msg->Message.MPC.AreaCode = htons(stripAreaCode(xalm_group_name));
    lm_msg->Message.MPC.IDNumber = 0;
	
    strcpy(lm_msg->Message.MPC.Group[0].GroupName, xalm_group_name.data());

    lm_msg->Message.MPC.Group[0].State = htons(MPCFINISHED);//?

    if(ctrl.getCommandedState(group_name) == CLOSED)
    {
	lm_msg->Message.MPC.Group[0].State |= htons(MPCSHED);
    }
    else
    {
	lm_msg->Message.MPC.Group[0].State |= htons(MPCRESTORE);
    }

    lm_msg->Message.MPC.Group[0].ShedSequence = 0;
    return buffer;

}

/*
 * Factory to create a XA21LMMESS that is for an unsolicited control
 */
CHAR* CtiFDR_XA21LM::buildMPCUnsolicited(const string& group_name, unsigned state)
{
   CHAR*buffer= new CHAR[sizeof(XA21LMMESS)];
    XA21LMMESS* lm_msg = (XA21LMMESS*) buffer;
    string xalm_group_name = group_name;
 
    lm_msg->Function = htonl(MPCUNSOLICITEDCTRLSTATUS);

    YukonToXA21Time(time(NULL), RWTime::now().isDST(), &(lm_msg->Time));
    lm_msg->Message.MPC.AreaCode = htons(stripAreaCode(xalm_group_name));
    lm_msg->Message.MPC.IDNumber = 0;
	
    strcpy(lm_msg->Message.MPC.Group[0].GroupName, xalm_group_name.data());
    for(int i = 1; i < MPCMAXGROUPS; i++)
    {
	lm_msg->Message.MPC.Group[i].GroupName[0] = '\0';//, sizeof(xa21_msg->Message.MPC.Group[i].GroupName));
    }
    lm_msg->Message.MPC.Group[0].State = 0;
    lm_msg->Message.MPC.Group[0].State = htons(MPCFINISHED);		
    if(state == CLOSED)
    {
	lm_msg->Message.MPC.Group[0].State |= htons(MPCSHED);
    }
    else
    {
	lm_msg->Message.MPC.Group[0].State |= htons(MPCRESTORE);
    }

    lm_msg->Message.MPC.Group[0].ShedSequence = 0;

    
    return buffer;
}

/*
 * Save off this control so we can send status updates later
 */
 
void CtiFDR_XA21LM::saveControl(XA21LMMESS* ctrl_msg)
{
    CurrentControl ctrl = CurrentControl::createCurrentControl(ctrl_msg);
    _current_controls.push_back(ctrl);
}

/*
 * Check if any controls are completed, that is we received a status update
 * for every group involved in the control request, or maybe the control just
 * timed out.
 * In both of those cases just drop the control from our list so we forget
 * about it.
 */
void CtiFDR_XA21LM::cleanupCurrentControls()
{
    CtiLockGuard<CtiCriticalSection> cs_lock(_control_cs);

    time_t now = time(NULL);
    vector<CurrentControl>::iterator iter = _current_controls.begin();
    while(iter != _current_controls.end())
    {
	CurrentControl& ctrl = *iter;
	if(ctrl.getNumPendingGroups() == 0)
	{
	{
	    CtiLockGuard<CtiLogger> dout_guard(dout);
	    dout << RWTime() << " - " << "MPC Control finished, removing from future consideration, " << (_current_controls.size()-1) << " remain" << endl;
	    ctrl.dump();
	}
	    iter = _current_controls.erase(iter);
	}
	else if(now > ctrl.getExpirationTime())
	{
	    {
		CtiLockGuard<CtiLogger> dout_guard(dout);
		dout << RWTime() << " - " << "MPC Control has timed out, removing from future consideration, " << (_current_controls.size()-1) << " remain" << endl;
		ctrl.dump();
	    }
	    iter = _current_controls.erase(iter);	    
	}
	else
	{
	    iter++;
	}
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
    dout << RWTime() << " - " <<  "MPC Message, Fn=" << ntohl(xa21_msg->Function) << " AC=" << ntohs(xa21_msg->Message.MPC.AreaCode) << endl;
 
    for(int i = 0; i < MPCMAXGROUPS; i++)
    {
	string translation_name(xa21_msg->Message.MPC.Group[i].GroupName);

	if(translation_name.length() == 0)
	{
	    break;
	}
	
	USHORT control_state = ntohs(xa21_msg->Message.MPC.Group[i].State);
	USHORT shed_seq = ntohs(xa21_msg->Message.MPC.Group[i].ShedSequence);
  
	if(findTranslationNameInList(translation_name.data(), getReceiveFromList(), point))
	{
	    dout << RWTime() << " - " <<  " Group name='" << translation_name << "' State=" << control_state << " ShedSeq=" << shed_seq << endl;
	}

    }

}

/*
 * Pull the area code out of the group name string and return it as an int.
 * e.g. given group "AC07 LG101 DO09" the return value would be 7
 *      and group_name would now be "LG101 DO09"
 */
int CtiFDR_XA21LM::stripAreaCode(string& group_name)
{
    if(group_name.find("AC", 0) != string::npos &&
       group_name.length() > 4)
    {
	string ac_str = group_name.substr(2,2);
	group_name = group_name.substr(5);
	return atoi(ac_str.data());
    }
    return 0;
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



