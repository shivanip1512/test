/*-----------------------------------------------------------------------------*
*
* File:   dev_snpp
*
* Date:   6/02/2005
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_snpp.cpp-arc  $
* REVISION     :  $Revision: 1.12.2.1 $
* DATE         :  $Date: 2008/11/13 17:23:42 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

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

using std::string;
using std::endl;
using std::list;
using std::queue;
using namespace boost::posix_time;

const char *CtiDeviceSnppPagingTerminal::_command_login       = "logi";
const char *CtiDeviceSnppPagingTerminal::_command_page        = "page";
const char *CtiDeviceSnppPagingTerminal::_command_level       = "leve";
const char *CtiDeviceSnppPagingTerminal::_command_alert       = "aler";
const char *CtiDeviceSnppPagingTerminal::_command_coverage    = "cove";
const char *CtiDeviceSnppPagingTerminal::_command_hold        = "hold";
const char *CtiDeviceSnppPagingTerminal::_command_caller_id   = "call";
const char *CtiDeviceSnppPagingTerminal::_command_subject     = "subj";
//const char *CtiDeviceSnppPagingTerminal::_command_message   = "mess";  // These commands are not used anywhere within dev_snpp.
//const char *CtiDeviceSnppPagingTerminal::_command_reset     = "rese";  // I'm leaving them here in the event that they're used in the future.
const char *CtiDeviceSnppPagingTerminal::_command_data        = "data";
const char *CtiDeviceSnppPagingTerminal::_command_send        = "send";
const char *CtiDeviceSnppPagingTerminal::_command_quit        = "quit";

const char *CtiDeviceSnppPagingTerminal::_char_cr_lf          = "\r\n";

const std::map<std::string, std::string>  SNPPResponseCodes{

    { "214", " <Help Text>" },
    { "220", " <SNPP Gateway Ready>"},
    { "221", " <Quit Successful>" },
    { "250", " <Success>" },
    { "354", " <Begin Input>" },
    { "421", " <Error - Service Unavailable / Too Many Errors>" },
    { "500", " <Error - Command Not Implemented>" },
    { "502", " <Error - Duplicate MCResponse>" },
    { "503", " <Error - Repeat Or Incomplete Message>" },
    { "550", " <Error - Invalid Message>" },
    { "552", " <Error - Max Recipients Exceeded>" },
    { "554", " <Error - Technical Failure>" },
    { "750", " <Unit Offline>" },
    { "780", " <Message Expired Before Delivery>" },
    { "820", " <Unit On System, This Area>" },
    { "821", " <Unit On System, No Location Information Available>" },
    { "850", " <Success>" },
    { "860", " <Message Delivered, Awaiting Read Confirmation>" },
    { "861", " <Message Delivered, Awaiting Reply(MCR)>" },
    { "870", " <Message Delivered And Read, Awaiting Reply (MCR)>" },
    { "880", " <Message Delivered>" },
    { "881", " <Message Delivered And Read>" },
    { "888", " <MCR Reply Received>" },
    { "889", " <Response Text Received>" },
    { "920", " <Unit Offline, But Can Queue Message>" },
    { "950", " <Unit Offline, Message Queued>" },
    { "960", " <Message Queued For Delivery>" }
};

CtiDeviceSnppPagingTerminal::CtiDeviceSnppPagingTerminal()
{
    resetStates();
}

YukonError_t CtiDeviceSnppPagingTerminal::decode(CtiXfer &xfer, YukonError_t commReturnValue)
{
    YukonError_t status = commReturnValue;

    std::string timeStamp = CtiTime::now().asString();

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
                            status = ClientErrors::None;
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
                    status = ClientErrors::Abnormal; // Abort when a carriage return isnt received from unit

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

                        std::string transReportMessage((char*)xfer.getInBuffer(), xfer.getInCountActual());
                        std::string responseCode((char*)xfer.getInBuffer(), 3);

                        if (const auto status = Cti::mapFind(SNPPResponseCodes, responseCode))
                        {
                            transReportMessage += *status;
                        }

                        updateTransactionReport(timeStamp + " <DVRT> Device Return        : " + transReportMessage + "\n");

                        setCurrentState(StateGenerateReadTextString);
                    }
                    else
                    {
                        status = ClientErrors::PageRS;
                        _command = Complete; //Transaction Complete

                        if(getPreviousState() == StateEnd)
                        {
                            CTILOG_WARN(dout, "no response from snpp quit command, all others responded");
                            status = ClientErrors::None;//quit command failed, all others passed, return normal
                        }

                    }

                }
                else
                {
                    status = ClientErrors::PageNoResponse;
                    _command = Complete; //Transaction Complete

                    if(getPreviousState() == StateEnd)
                    {
                        CTILOG_WARN(dout, "no response from snpp quit command, all others responded");
                        status = ClientErrors::None;//quit command failed, all others passed, return normal
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
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
        //reset and throw to regular error catcher.
        resetStates();
        throw;
    }

    return status;
}

YukonError_t CtiDeviceSnppPagingTerminal::generate(CtiXfer  &xfer)
{
    YukonError_t status = ClientErrors::None;

    std::string timeStamp = CtiTime::now().asString();

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

                    updateTransactionReport(timeStamp + " <LOGI> Login Name           : " + getLoginName() + "\n");

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

                    updateTransactionReport(timeStamp + " <LEVE> New Service Level    : " + getLevelNumber() + "\n");

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

                    updateTransactionReport(timeStamp + " <ALER> Alert ID             : " + getAlertNumber() + "\n");

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

                    updateTransactionReport(timeStamp + " <COVE> Coverage Override ID : " + getCoverageNumber() + "\n");

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

                    updateTransactionReport(timeStamp + " <HOLD> Holding Message Until: " + getHoldTime() + "\n");

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

                    updateTransactionReport(timeStamp + " <PAGE> Page Number          : " + getPageNumber() + "\n");

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

                updateTransactionReport(timeStamp + " <PAGE> Page Number          : " + getPageNumber() + "\n");

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

                    updateTransactionReport(timeStamp + " <CALL> Caller ID            : " + getCallerID() + "\n");

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

                    updateTransactionReport(timeStamp + " <SUBJ> Message Subject      : " + getSubject() + "\n");

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
                strncpy((char *)xfer.getOutBuffer(),reinterpret_cast<char *>(_outMessage.Buffer.OutMessage),300);//send whole message!
                strncat((char *)xfer.getOutBuffer(),_char_cr_lf,2);
                strncat((char *)xfer.getOutBuffer(),".",2);
                strncat((char *)xfer.getOutBuffer(),_char_cr_lf,2);
                xfer.setOutCount(strlen((char *)xfer.getOutBuffer()));
                xfer.setInCountExpected( 0 );

                updateTransactionReport(timeStamp + " <DATA> Message              : " + reinterpret_cast<char *>(_outMessage.Buffer.OutMessage) + "\n");

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

                updateTransactionReport(timeStamp + " <SEND> Send Request Received: OK \n");

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

                updateTransactionReport(timeStamp + " <QUIT> Quit Request Received: OK \n");

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
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);

        resetStates(); //reset state settings and throw error to regular catcher.
        throw;
    }
    return status;
}

YukonError_t CtiDeviceSnppPagingTerminal::recvCommRequest( OUTMESS *OutMessage )
{
    YukonError_t retVal = ClientErrors::None;

    if( OutMessage )
    {
        _outMessage = *OutMessage;
        if ( OutMessage->MessageFlags & MessageFlag_EncryptionRequired )    // One-Way Encryption
        {
            _outMessage.MessageFlags &= ~MessageFlag_EncryptionRequired;
            _outMessage.Buffer.TAPSt.Length = encryptMessage( CtiTime::now(),
                                                              OutMessage->Buffer.TAPSt.Message,
                                                              OutMessage->Buffer.TAPSt.Length,
                                                              _outMessage.Buffer.TAPSt.Message,
                                                              OneWayMsgEncryption::Ascii );
            _outMessage.OutLength = _outMessage.Buffer.TAPSt.Length;
        }

        resetStates();
        _command = Normal;

    }
    else
    {
        CTILOG_ERROR(dout, "NULL OutMessage");
        retVal = ClientErrors::Memory;
    }

    return retVal;
}

bool CtiDeviceSnppPagingTerminal::isTransactionComplete()
{
    return _command == Complete;
}

YukonError_t CtiDeviceSnppPagingTerminal::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t nRet = ClientErrors::None;
    /*
     *  This method should only be called by the dev_base method
     *   ExecuteRequest(CtiReturnMsg*, INT ScanPriority)
     *   (NOTE THE DIFFERENCE IN ARGS)
     *   That method prepares an outmessage for submission to the internals..
     */

    string resultString;
    CtiReturnMsg *pReturnMsg = CTIDBG_new CtiReturnMsg(getID(),
                                            string(OutMessage->Request.CommandStr),
                                            string(),
                                            nRet,
                                            OutMessage->Request.RouteID,
                                            OutMessage->Request.RetryMacroOffset,
                                            OutMessage->Request.Attempt,
                                            OutMessage->Request.GrpMsgID,
                                            OutMessage->Request.UserID,
                                            OutMessage->Request.SOE,
                                            CtiMultiMsg_vec());

    switch(parse.getCommand())
    {
    case PutValueRequest:
        {
            if( parse.isKeyValid("asciiraw") && gConfigParms.isTrue("ALLOW_RAW_PAGE_MESSAGES") )
            {
                string outputValue = parse.getsValue("asciiraw");
                strcpy_s((char *)OutMessage->Buffer.OutMessage, 300, outputValue.c_str());
                OutMessage->OutLength = outputValue.size();
                OutMessage->DeviceID    = getID();
                OutMessage->TargetID    = getID();
                OutMessage->Port        = getPortID();
                OutMessage->InLength    = 0;
                OutMessage->Source      = 0;
                OutMessage->Retry       = 2;

                resultString = "Device: " + getName() + " -- Raw ASCII Command sent \n\"" + outputValue + "\"";

                outList.push_back(OutMessage);
                OutMessage = NULL;
                break;
            }
            //else fall through!
        }
    case ControlRequest:
        {
            CTILOG_ERROR(dout, "Unexpected ControlRequest command");
        }
    case GetStatusRequest:
    case LoopbackRequest:
    case GetValueRequest:
    case PutStatusRequest:
    case GetConfigRequest:
    case PutConfigRequest:
    default:
        {
            nRet = ClientErrors::NoMethodForExecuteRequest;
            /* Set the error value in the base class. */
            // FIX FIX FIX 092999
            resultString = "SNPP Devices do not support this command (yet?)";

            if(OutMessage)                // And get rid of our memory....
            {
                delete OutMessage;
                OutMessage = NULL;
            }
            break;
        }
    }

    if(pReturnMsg != NULL)
    {
        pReturnMsg->setResultString(resultString);
        pReturnMsg->setStatus(nRet);
        retList.push_back(pReturnMsg);
    }

    return nRet;
}

string CtiDeviceSnppPagingTerminal::getLoginName()
{
    if(!getPaging().getSenderID().empty() &&
       getPaging().getSenderID().find("none") == string::npos && getPaging().getSenderID() != " ")
    {
        return getPaging().getSenderID();
    }
    return string();
}

string CtiDeviceSnppPagingTerminal::getLoginPass()
{
    if(!getPaging().getSecurityCode().empty() &&
       getPaging().getSecurityCode().find("none") == string::npos && getPaging().getSecurityCode() != " ")
    {
        return getPaging().getSecurityCode();
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
    if(!getPaging().getPagerNumber().empty() &&
        getPaging().getPagerNumber().find("none") == string::npos && getPaging().getPagerNumber() != " ")
    {
        return getPaging().getPagerNumber();
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

YukonError_t CtiDeviceSnppPagingTerminal::sendCommResult(INMESS &InMessage)
{
    // We are not interested in changing this return value here!
    // Must override base as we have no protocol.
    return ClientErrors::None;
}

void CtiDeviceSnppPagingTerminal::getVerificationObjects(queue< CtiVerificationBase * > &work_queue)
{
    while( !_verification_objects.empty() )
    {
        work_queue.push(_verification_objects.front());

        _verification_objects.pop();
    }
}

boost::optional<std::string> CtiDeviceSnppPagingTerminal::getTransactionReport()
{
    return _transaction_report;
}

void CtiDeviceSnppPagingTerminal::clearTransactionReport()
{
    _transaction_report.clear();
}

void CtiDeviceSnppPagingTerminal::updateTransactionReport(std::string reportUpdate)
{
    _transaction_report.append(reportUpdate);
}