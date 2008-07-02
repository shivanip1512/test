/*-----------------------------------------------------------------------------*
*
* File:   dev_snpp
*
* Date:   6/02/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_snpp.cpp-arc  $
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2008/07/02 19:05:55 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <windows.h>

#include "cparms.h"
#include "dsm2.h"
#include "logger.h"
#include "porter.h"

#include "cmdparse.h"
#include "pt_base.h"
#include "pt_accum.h"
#include "port_base.h"

#include "pointtypes.h"
#include "connection.h"
#include "mgr_route.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "msg_trace.h"
#include "numstr.h"
#include "verification_objects.h"
#include "dev_snpp.h"

const char *CtiDeviceSnppPagingTerminal::_command_login       = "logi";
const char *CtiDeviceSnppPagingTerminal::_command_page        = "page";
const char *CtiDeviceSnppPagingTerminal::_command_level       = "leve";
const char *CtiDeviceSnppPagingTerminal::_command_alert       = "aler";
const char *CtiDeviceSnppPagingTerminal::_command_coverage    = "cove";
const char *CtiDeviceSnppPagingTerminal::_command_hold        = "hold";
const char *CtiDeviceSnppPagingTerminal::_command_caller_id   = "call";
const char *CtiDeviceSnppPagingTerminal::_command_subject     = "subj";
const char *CtiDeviceSnppPagingTerminal::_command_message     = "mess";
const char *CtiDeviceSnppPagingTerminal::_command_reset       = "rese";
const char *CtiDeviceSnppPagingTerminal::_command_data        = "data";
const char *CtiDeviceSnppPagingTerminal::_command_send        = "send";
const char *CtiDeviceSnppPagingTerminal::_command_quit        = "quit";

const char *CtiDeviceSnppPagingTerminal::_char_cr_lf          = "\r\n";

CtiDeviceSnppPagingTerminal::CtiDeviceSnppPagingTerminal()
{
    resetStates();
}

/*CtiDeviceSnppPagingTerminal::CtiDeviceSnppPagingTerminal(const CtiDeviceSnppPagingTerminal& aRef)
{
    //Dont think I will need this function.
}*/

CtiDeviceSnppPagingTerminal::~CtiDeviceSnppPagingTerminal()
{
    //Nothing special.

}

// operator = is not complete!! If it is ever needed, complete it.
/*
CtiDeviceSnppPagingTerminal& CtiDeviceSnppPagingTerminal::operator=(const CtiDeviceSnppPagingTerminal& aRef)
{//why would you ever do this, I dont know???
    if(this != &aRef)
    {
        Inherited::operator=(aRef);

        _snpp = aRef.getTap();

        setPreviousState(aRef.getPreviousState());
        setCurrentState(aRef.getCurrentState());
        _command = Normal;

    }
    return *this;
}*/

INT CtiDeviceSnppPagingTerminal::decode(CtiXfer &xfer,INT commReturnValue)
{
    INT status = commReturnValue;

    try
    {
        switch( getCurrentState() )
        {
        case StateDecodeReadTextString:
            {
                if(xfer.getInCountActual() >= 1)//Should be 1
                {
                    if(xfer.getInBuffer()[0] == *_char_cr_lf)
                    {
                        //yay, end of string
                        setCurrentState(getPreviousState());//saved state

                        if(getPreviousState() == StateEnd)
                        {
                            status = NORMAL;
                            _command = Complete; //Transaction Complete
                        }

                    }
                    else
                    {
                        setCurrentState(StateGenerateReadTextString);
                    }
                }
                else
                {
                    //OUCH. Nothing read in, probably a timeout.
                    _command = Complete; //Transaction Complete
                    status = FinalError; // FinalError returned when a carriage return isnt received from unit

                }

                break;
            }
        case StateDecodeSetupReadResponse:
            {
                setCurrentState(StateGenerateSetupReadResponse);
                break;

            }
        case StateDecodeResponse:
            {
                if(xfer.getInCountActual() >= 3)
                {
                    if(strstr((char*)xfer.getInBuffer(), "250") != NULL ||
                       (strstr((char*)xfer.getInBuffer(), "220") != NULL && getPreviousState()==StateSendLoginInformation) ||
                       (strstr((char*)xfer.getInBuffer(), "354") != NULL && getPreviousState()==StateSendData) ||
                       (strstr((char*)xfer.getInBuffer(), "221") !=NULL && getPreviousState()==StateEnd))
                    {//YAY!

                        /*if(commReturnValue == READTIMEOUT)
                        {
                            // No clue how I got here since I read in the 3 bytes I was looking for...........
                            status = NORMAL;  // Make sure the portfield loop is not compromised!
                        }*/ //Dont think this is needed

                        setCurrentState(StateGenerateReadTextString);
                    }
                    else
                    {
                        status = ErrorPageRS;
                        _command = Complete; //Transaction Complete

                        if(getPreviousState() == StateEnd)
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << CtiTime() << " **** Checkpoint - no response from snpp quit command, all others responded" << __FILE__ << " (" << __LINE__ << ")" << endl;
    
                            }
                            status = NORMAL;//quit command failed, all others passed, return normal
                        }

                    }

                }
                else
                {
                    status = ErrorPageNoResponse;
                    _command = Complete; //Transaction Complete

                    if(getPreviousState() == StateEnd)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Checkpoint - no response from snpp quit command, all others responded" << __FILE__ << " (" << __LINE__ << ")" << endl;
    
                        }
                        status = NORMAL;//quit command failed, all others passed, return normal
                    }
                }
                break;
            }
        case StateEnd:
            {
                _command = Complete;
                break;
            }
        }
    }
    catch (...)
    {//reset and throw to regular error catcher.
        resetStates();
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - Exception thrown **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        throw;
    }
    return status;
}

INT CtiDeviceSnppPagingTerminal::generate(CtiXfer  &xfer)
{
    INT status = NORMAL;
    try
    {
        switch( getCurrentState() )
        {
        case StateHandshakeInitialize:            // Look for any unsolicited ID= Message first... (no outbound CR's)
            {
                xfer.setInBuffer(_inBuffer);
                xfer.setOutBuffer(_outBuffer);
                xfer.setInCountActual(&_inCountActual);
                xfer.setOutCount( 0 );              // We only need an in here. Make sure no outbound is sent.

                xfer.setInCountExpected( 3 ); // Looking for 3 bytes from the TAP (ID=)
                xfer.setInTimeout( 1 );             // It is already there if it is there!

                setPreviousState(StateSendLoginInformation);      // Let anyone find us back...
                setCurrentState(StateDecodeResponse);

                break;
            }
        case StateGenerateSetupReadResponse:
            {
                //Assumes a transmission was just sent. Set up read of 3.
                xfer.setOutCount(0);
                xfer.setNonBlockingReads(false);
                xfer.setInCountExpected( 3 ); // Reading 1 byte at a time until we find <CR>
                xfer.setInTimeout( 2 );       // Very arbitrary. Seems to work for most people, it is generally set to 1 in rest of code
                setCurrentState(StateDecodeResponse);
                break;
            }

        case StateGenerateReadTextString:
            {
                xfer.setInCountExpected( 1 ); // Reading 1 byte at a time until we find <CR>
                xfer.setInTimeout( 1 );       // Very arbitrary. Seems to work for most people, it is generally set to 1 in rest of code
                setCurrentState(StateDecodeReadTextString);
                break;
            }
            //The next set of data is optional, and falls through to the next optional piece until a used piece is found..
        case StateSendLoginInformation:
            {
                if (getLoginName().length() != 0)
                {

                    strncpy((char *)xfer.getOutBuffer(),_command_login,10);//LOGIn command
                    strncat((char *)xfer.getOutBuffer()," ",10);
                    strncat((char *)xfer.getOutBuffer(),getLoginName().c_str(),50);
                    if(getLoginPass().length() != 0)
                    {
                        strncat((char *)xfer.getOutBuffer()," ",10);
                        strncat((char *)xfer.getOutBuffer(),getLoginPass().c_str(),25);
                    }
                    strncat((char *)xfer.getOutBuffer(),_char_cr_lf,10);
                    xfer.setOutCount(strlen((char *)xfer.getOutBuffer()));
                    xfer.setInCountExpected( 0 );

                    setPreviousState(StateSendLevelNumber);
                    setCurrentState(StateDecodeSetupReadResponse);
                    break;
                }
                else
                {//***fall through***
                }
            }
        case StateSendLevelNumber:
            {
                if (getLevelNumber().length() != 0)
                {

                    strncpy((char *)xfer.getOutBuffer(),_command_level,10);
                    strncat((char *)xfer.getOutBuffer()," ",10);
                    strncat((char *)xfer.getOutBuffer(),getLevelNumber().c_str(),50);
                    strncat((char *)xfer.getOutBuffer(),_char_cr_lf,10);
                    xfer.setOutCount(strlen((char *)xfer.getOutBuffer()));
                    xfer.setInCountExpected( 0 );

                    setPreviousState(StateSendAlertNumber);
                    setCurrentState(StateDecodeSetupReadResponse);
                    break;
                }
                //***fall through***
            }
        case StateSendAlertNumber:
            {
                if (getAlertNumber().length() != 0)
                {

                    strncpy((char *)xfer.getOutBuffer(),_command_alert,10);
                    strncat((char *)xfer.getOutBuffer()," ",10);
                    strncat((char *)xfer.getOutBuffer(),getAlertNumber().c_str(),50);
                    strncat((char *)xfer.getOutBuffer(),_char_cr_lf,10);
                    xfer.setOutCount(strlen((char *)xfer.getOutBuffer()));
                    xfer.setInCountExpected( 0 );

                    setPreviousState(StateSendCoverageNumber);
                    setCurrentState(StateDecodeSetupReadResponse);
                    break;
                }
            }
        case StateSendCoverageNumber:
            {
                if (getCoverageNumber().length() != 0)
                {

                    strncpy((char *)xfer.getOutBuffer(),_command_coverage,10);
                    strncat((char *)xfer.getOutBuffer()," ",10);
                    strncat((char *)xfer.getOutBuffer(),getCoverageNumber().c_str(),50);
                    strncat((char *)xfer.getOutBuffer(),_char_cr_lf,10);
                    xfer.setOutCount(strlen((char *)xfer.getOutBuffer()));
                    xfer.setInCountExpected( 0 );

                    setPreviousState(StateSendHoldTime);
                    setCurrentState(StateDecodeSetupReadResponse);
                    break;
                }
                //***fall through***
            }
        case StateSendHoldTime:
            {
                if (getHoldTime().length() != 0)
                {

                    strncpy((char *)xfer.getOutBuffer(),_command_hold,10);
                    strncat((char *)xfer.getOutBuffer()," ",10);
                    strncat((char *)xfer.getOutBuffer(),getHoldTime().c_str(),50);
                    strncat((char *)xfer.getOutBuffer(),_char_cr_lf,10);
                    xfer.setOutCount(strlen((char *)xfer.getOutBuffer()));
                    xfer.setInCountExpected( 0 );

                    setPreviousState(StateSendPageWithPass);
                    setCurrentState(StateDecodeSetupReadResponse);
                    break;
                }
                //***fall through***
            }
        case StateSendPageWithPass:
            {
                if (getPagePass().length() != 0)
                {

                    strncpy((char *)xfer.getOutBuffer(),_command_page,10);
                    strncat((char *)xfer.getOutBuffer()," ",10);
                    strncat((char *)xfer.getOutBuffer(),getPageNumber().c_str(),50);
                    strncat((char *)xfer.getOutBuffer()," ",10);
                    strncat((char *)xfer.getOutBuffer(),getPagePass().c_str(),50);
                    strncat((char *)xfer.getOutBuffer(),_char_cr_lf,10);
                    xfer.setOutCount(strlen((char *)xfer.getOutBuffer()));
                    xfer.setInCountExpected( 0 );

                    setPreviousState(StateSendCallerID);
                    setCurrentState(StateDecodeSetupReadResponse);
                    break;
                }
                //***fall through***
            }
        case StateSendPageWithoutPass://This stage is skipped if send With pass is run.
            {
                strncpy((char *)xfer.getOutBuffer(),_command_page,10);
                strncat((char *)xfer.getOutBuffer()," ",10);
                strncat((char *)xfer.getOutBuffer(),getPageNumber().c_str(),50);
                strncat((char *)xfer.getOutBuffer(),_char_cr_lf,10);
                xfer.setOutCount(strlen((char *)xfer.getOutBuffer()));
                xfer.setInCountExpected( 0 );

                setPreviousState(StateSendCallerID);
                setCurrentState(StateDecodeSetupReadResponse);//Not sure if this is what I want
                break;
            }
        case StateSendCallerID:
            {
                if (getCallerID().length() != 0)
                {

                    strncpy((char *)xfer.getOutBuffer(),_command_caller_id,10);
                    strncat((char *)xfer.getOutBuffer()," ",10);
                    strncat((char *)xfer.getOutBuffer(),getCallerID().c_str(),50);
                    strncat((char *)xfer.getOutBuffer(),_char_cr_lf,10);
                    xfer.setOutCount(strlen((char *)xfer.getOutBuffer()));
                    xfer.setInCountExpected( 0 );

                    setPreviousState(StateSendSubject);
                    setCurrentState(StateDecodeSetupReadResponse);
                    break;
                }
                //***fall through***
            }
        case StateSendSubject:
            {
                if (getSubject().length() != 0)
                {

                    strncpy((char *)xfer.getOutBuffer(),_command_subject,10);
                    strncat((char *)xfer.getOutBuffer()," ",10);
                    strncat((char *)xfer.getOutBuffer(),getSubject().c_str(),50);
                    strncat((char *)xfer.getOutBuffer(),_char_cr_lf,10);
                    xfer.setOutCount(strlen((char *)xfer.getOutBuffer()));
                    xfer.setInCountExpected( 0 );

                    setPreviousState(StateSendDataCommand);
                    setCurrentState(StateDecodeSetupReadResponse);
                    break;
                }
                //***fall through***
            }
        case StateSendDataCommand:
            {
                strncpy((char *)xfer.getOutBuffer(),_command_data,10);
                strncat((char *)xfer.getOutBuffer(),_char_cr_lf,10);
                xfer.setOutCount(strlen((char *)xfer.getOutBuffer()));
                xfer.setInCountExpected( 0 );

                setPreviousState(StateSendData);
                setCurrentState(StateDecodeSetupReadResponse);
                break;
            }
        case StateSendData:
            {
                strncpy((char *)xfer.getOutBuffer(),reinterpret_cast<char *>(_outMessage.Buffer.OutMessage),500);//send whole message!
                strncat((char *)xfer.getOutBuffer(),_char_cr_lf,2);
                strncat((char *)xfer.getOutBuffer(),".",2);
                strncat((char *)xfer.getOutBuffer(),_char_cr_lf,2);
                xfer.setOutCount(strlen((char *)xfer.getOutBuffer()));
                xfer.setInCountExpected( 0 );

                setPreviousState(StateSendSend);
                setCurrentState(StateDecodeSetupReadResponse);
                break;
            }
        case StateSendSend://Send Command!!!!!
            {
                strncpy((char *)xfer.getOutBuffer(),_command_send,10);
                strncat((char *)xfer.getOutBuffer(),_char_cr_lf,10);
                xfer.setOutCount(strlen((char *)xfer.getOutBuffer()));
                xfer.setInCountExpected( 0 );

                setPreviousState(StateSendQuit);
                setCurrentState(StateDecodeSetupReadResponse);
                break;
            }
        case StateSendQuit:
            {
                //we can assume here that everything is ok, so set up the verification object!

                //If Retry is set, this is not a verification retry...
                if( !_outMessage.VerificationSequence )
                {
                    _outMessage.VerificationSequence = VerificationSequenceGen();
                }
                CtiVerificationWork *work = CTIDBG_new CtiVerificationWork(CtiVerificationBase::Protocol_SNPP, _outMessage, _outMessage.Request.CommandStr, reinterpret_cast<char *>(_outMessage.Buffer.OutMessage), seconds(700));//11.6 minutes
                _verification_objects.push(work);

                strncpy((char *)xfer.getOutBuffer(),_command_quit,10);
                strncat((char *)xfer.getOutBuffer(),_char_cr_lf,10);
                xfer.setOutCount(strlen((char *)xfer.getOutBuffer()));
                xfer.setInCountExpected( 0 );

                setPreviousState(StateEnd);
                setCurrentState(StateDecodeSetupReadResponse);
                break;
            }
        case StateEnd:
            {//Failsafe
                _command = Complete;
                break;
            }
        }
    }
    catch(...)
    {//reset state settings and throw error to regular catcher.
        resetStates();
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - Exception thrown **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        throw;
    }
    return status;
}

int CtiDeviceSnppPagingTerminal::recvCommRequest( OUTMESS *OutMessage )
{
    int retVal = NoError;

    if( OutMessage )
    {
        _outMessage = *OutMessage;
        resetStates();
        _command = Normal;

    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - invalid OutMessage in CtiDeviceSnppPagingTerminal::recvCommResult() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        retVal = MemoryError;
    }

    return retVal;
}

bool CtiDeviceSnppPagingTerminal::isTransactionComplete()
{
    return _command == Complete;
}

INT CtiDeviceSnppPagingTerminal::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT nRet = NORMAL;
    /*
     *  This method should only be called by the dev_base method
     *   ExecuteRequest(CtiReturnMsg*, INT ScanPriority)
     *   (NOTE THE DIFFERENCE IN ARGS)
     *   That method prepares an outmessage for submission to the internals..
     */

    switch(parse.getCommand())
    {
    case ControlRequest:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    case GetStatusRequest:
    case LoopbackRequest:
    case GetValueRequest:
    case PutValueRequest:
    case PutStatusRequest:
    case GetConfigRequest:
    case PutConfigRequest:
    default:
        {
            nRet = NoExecuteRequestMethod;
            /* Set the error value in the base class. */
            // FIX FIX FIX 092999
            retList.push_back( CTIDBG_new CtiReturnMsg(getID(),
                                                    string(OutMessage->Request.CommandStr),
                                                    string("SNPP Devices do not support this command (yet?)"),
                                                    nRet,
                                                    OutMessage->Request.RouteID,
                                                    OutMessage->Request.MacroOffset,
                                                    OutMessage->Request.Attempt,
                                                    OutMessage->Request.TrxID,
                                                    OutMessage->Request.UserID,
                                                    OutMessage->Request.SOE,
                                                    CtiMultiMsg_vec()));

            if(OutMessage)                // And get rid of our memory....
            {
                delete OutMessage;
                OutMessage = NULL;
            }

            break;
        }
    }


    return nRet;
}

string CtiDeviceSnppPagingTerminal::getLoginName()
{
    if(!_table.getSenderID().empty() && 
       _table.getSenderID().find("none") == string::npos && _table.getSenderID() != " ")
    {
        return _table.getSenderID();
    }
    return string();
}

string CtiDeviceSnppPagingTerminal::getLoginPass()
{
    if(!_table.getSecurityCode().empty() && 
       _table.getSecurityCode().find("none") == string::npos && _table.getSecurityCode() != " ")
    {
        return _table.getSecurityCode();
    }
    return string();
}

string CtiDeviceSnppPagingTerminal::getLevelNumber()
{
    return string();
}

string CtiDeviceSnppPagingTerminal::getAlertNumber()
{
    return string();
}

string CtiDeviceSnppPagingTerminal::getCoverageNumber()
{
    return string();
}

string CtiDeviceSnppPagingTerminal::getCallerID()
{
    return string();
}

string CtiDeviceSnppPagingTerminal::getHoldTime()
{
    return string();
}

string CtiDeviceSnppPagingTerminal::getSubject()
{
    return string();
}

string CtiDeviceSnppPagingTerminal::getPagePass()
{
    return string();
}

string CtiDeviceSnppPagingTerminal::getPageNumber()
{
    if(!_table.getPagerNumber().empty() &&
        _table.getPagerNumber().find("none") == string::npos && _table.getPagerNumber() != " ")
    {
        return _table.getPagerNumber();
    }
    return string();
}

CtiDeviceSnppPagingTerminal::StateMachine CtiDeviceSnppPagingTerminal::getCurrentState()
{
    return _currentState;
}

CtiDeviceSnppPagingTerminal::StateMachine CtiDeviceSnppPagingTerminal::getPreviousState()
{
    return _previousState;
}

void CtiDeviceSnppPagingTerminal::setCurrentState(StateMachine newCurrentState)
{
    _currentState = newCurrentState;
}

void CtiDeviceSnppPagingTerminal::setPreviousState(StateMachine newPreviousState)
{
    _previousState = newPreviousState;
}

void CtiDeviceSnppPagingTerminal::resetStates()
{
    _currentState = StateHandshakeInitialize;
    _previousState = StateHandshakeInitialize;
    _command = Normal;
}

//Database Functions
void CtiDeviceSnppPagingTerminal::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    Inherited::getSQL(db, keyTable, selector);
    _table.getSQL(db, keyTable, selector);
}

void CtiDeviceSnppPagingTerminal::DecodeDatabaseReader(RWDBReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    _table.DecodeDatabaseReader(rdr);
}


void CtiDeviceSnppPagingTerminal::getVerificationObjects(queue< CtiVerificationBase * > &work_queue)
{
    while( !_verification_objects.empty() )
    {
        work_queue.push(_verification_objects.front());

        _verification_objects.pop();
    }
}
