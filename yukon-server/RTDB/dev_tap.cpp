/*-----------------------------------------------------------------------------*
*
* File:   dev_tap
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_tap.cpp-arc  $
* REVISION     :  $Revision: 1.36.2.1 $
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

#include "pointtypes.h"
#include "connection.h"
#include "mgr_route.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "msg_trace.h"
#include "numstr.h"
#include "verification_objects.h"
#include "dev_tap.h"

using namespace std;
using namespace boost::posix_time;

static int pagesPerMinute  = gConfigParms.getValueAsInt("PAGES_PER_MINUTE", 0);

CtiDeviceTapPagingTerminal::~CtiDeviceTapPagingTerminal()
{
    CtiDeviceTapPagingTerminal::freeDataBins();  //  qualified to prevent virtual dispatch
}

YukonError_t CtiDeviceTapPagingTerminal::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
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
                strcpy_s(OutMessage->Buffer.TAPSt.Message, 256, outputValue.c_str());
                OutMessage->OutLength = outputValue.size();
                OutMessage->Buffer.TAPSt.Length = outputValue.size();
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
            resultString = "TAP Devices do not support this command (yet?)";

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

INT CtiDeviceTapPagingTerminal::allocateDataBins(OUTMESS *oMess)
{
    if(_outMessage == NULL)
    {
        _outMessage = CTIDBG_new OUTMESS(*oMess);//new out message
        if ( oMess->MessageFlags & MessageFlag_EncryptionRequired )    // One-Way Encryption
        {
            _outMessage->MessageFlags &= ~MessageFlag_EncryptionRequired;
            _outMessage->Buffer.TAPSt.Length = encryptMessage( CtiTime::now(),
                                                               oMess->Buffer.TAPSt.Message,
                                                               oMess->Buffer.TAPSt.Length,
                                                               _outMessage->Buffer.TAPSt.Message,
                                                               OneWayMsgEncryption::Ascii );
            _outMessage->OutLength = _outMessage->Buffer.TAPSt.Length;
        }
    }
    if(_pageBuffer == NULL)
    {
        _pageBuffer = (CHAR*) _outMessage->Buffer.TAPSt.Message;//point pointer to new message structure.
        setPageLength(_outMessage->Buffer.TAPSt.Length);

    }

    return ClientErrors::None;
}

INT CtiDeviceTapPagingTerminal::freeDataBins()
{
    if(_pageBuffer != NULL)
    {
        _pageBuffer = NULL;
    }
    if(_outMessage != NULL)
    {
        delete _outMessage;
        _outMessage = NULL;
    }

    return ClientErrors::None;
}

CHAR  CtiDeviceTapPagingTerminal::getPagePrefix() const
{
    return _pagePrefix;
}
CHAR& CtiDeviceTapPagingTerminal::getPagePrefix()
{
    return _pagePrefix;
}
CtiDeviceTapPagingTerminal& CtiDeviceTapPagingTerminal::setPagePrefix(const CHAR aCh)
{
    _pagePrefix = aCh;
    return *this;
}

UINT  CtiDeviceTapPagingTerminal::getPageCount() const
{
    return _pageCount;
}
UINT& CtiDeviceTapPagingTerminal::getPageCount()
{
    return _pageCount;
}
CtiDeviceTapPagingTerminal& CtiDeviceTapPagingTerminal::setPageCount(const UINT aInt)
{
    _pageCount = aInt;
    return *this;
}

UINT CtiDeviceTapPagingTerminal::getPageLength() const
{
    return _pageLength;
}

CtiDeviceTapPagingTerminal& CtiDeviceTapPagingTerminal::setPageLength(const UINT aInt)
{
    _pageLength = aInt;
    return *this;
}

BOOL CtiDeviceTapPagingTerminal::isValidPageBuffer() const
{
    return( _pageBuffer != NULL );
}

CHAR* CtiDeviceTapPagingTerminal::getPageBuffer()
{
    return _pageBuffer;
}

CHAR  CtiDeviceTapPagingTerminal::getPageBuffer(const INT i) const
{
    CHAR ch = '\0';

    if(_pageBuffer != NULL)
    {
        ch = _pageBuffer[i];
    }

    return ch;
}

//This should no longer be used!
/*
CtiDeviceTapPagingTerminal& CtiDeviceTapPagingTerminal::setPageBuffer(const CHAR* copyBuffer, const INT len)
{
    setPageLength(len);
    ::memcpy(_pageBuffer, copyBuffer, len);
    return *this;
}
*/

string CtiDeviceTapPagingTerminal::getDescription(const CtiCommandParser & parse) const
{
    string trelay;

    trelay = "PAGE: " + getName();

    return trelay;
}

YukonError_t CtiDeviceTapPagingTerminal::decodeResponseHandshake(CtiXfer &xfer, YukonError_t commReturnValue, CtiMessageList &traceList)
{
    YukonError_t status = commReturnValue;

    if(gConfigParms.isTrue("DEBUG_TAPTERM_STATE_MACHINE"))
    {
        CTILOG_DEBUG(dout, "Current state "<< getCurrentState() <<". Previous State "<< getPreviousState());
    }

    switch( getCurrentState() )
    {
    case StateHandshakeDecodeStart:
        {
            /* Check if this is ID= */
            if(xfer.getInCountActual() >= 2)
            {
                if((strstr((char*)xfer.getInBuffer(), "ID") != NULL) ||
                   (gConfigParms.isTrue("TAPTERM_ACCEPT_CRLF_AS_ID") && (strstr((char*)xfer.getInBuffer(), "\r\n") != NULL)))
                {

                    if(commReturnValue == ClientErrors::ReadTimeout)
                    {
                        // We got the bytes we needed though.. so lets ignore the error!
                        status = ClientErrors::None;  // Make sure the portfield loop is not compromised!

                        _idByteCount = xfer.getInCountActual();  // Improve the efficiency for the next call.
                    }

                    setCurrentState(StateHandshakeSendIdentify);
                    if(xfer.doTrace(commReturnValue))
                    {
                        traceIn((char*)xfer.getInBuffer(), xfer.getInCountActual(), traceList, TRUE);
                    }
                }
                else // We will retry the operation
                {
                    if(commReturnValue != ClientErrors::ReadTimeout)  // We had a bunch of bytes there...
                    {
                        _idByteCount = 20;
                        xfer.setInCountExpected( _idByteCount ); // Get a big bunch on the next try (we will timeout I expect...)
                    }

                    if(getPreviousState() == StateHandshakeSendStart)     // If we were looking for an early ID=
                    {
                        xfer.setOutCount(1);
                        setCurrentState( StateRepeat );
                        status = ClientErrors::None;  // Make sure the portfield loop is not compromised!
                    }
                    else
                    {
                        setPreviousState( getCurrentState() );    // Want the Repeat routine to send us back to this state.
                        setCurrentState( StateRepeat );
                        status = ClientErrors::None;  // Make sure the portfield loop is not compromised!
                    }

                    if(xfer.doTrace(commReturnValue))
                    {
                        traceIn((char*)xfer.getInBuffer(), xfer.getInCountActual(), traceList, FALSE);
                    }
                }
            }
            else
            {
                /*
                 *  There are two ways we got here...
                 *  1. on the initial wait for ID= we didn't get anything back.
                 *  2. on a request of ID= (by sending a CR) we got bupkiss.
                 */

                xfer.setOutCount(1);
                setPreviousState( getCurrentState() );    // Want the Repeat routine to send us back to this state.
                setCurrentState( StateRepeat );
                status = ClientErrors::None;  // Make sure the portfield loop is not compromised!
                if(xfer.doTrace(commReturnValue))
                {
                    traceIn((char*)xfer.getInBuffer(), xfer.getInCountActual(), traceList, TRUE);
                }

            }

            break;
        }
    case StateHandshakeDecodeIdentify:
        {
            if( xfer.getInCountActual() >= 1 && xfer.getInBuffer()[0] == CHAR_ACK )
            {
                setCurrentState(StateGenerate_1);
                if(xfer.doTrace(commReturnValue))
                {
                    traceIn((char*)xfer.getInBuffer(), xfer.getInCountActual(), traceList, FALSE);
                }
            }
            else if( xfer.getInCountActual() >= 1 && xfer.getInBuffer()[0] == CHAR_NAK )
            {
                status = ClientErrors::Unknown;
                setCurrentState(StateHandshakeAbort);
                if(xfer.doTrace(commReturnValue))
                {
                    traceIn((char*)xfer.getInBuffer(), xfer.getInCountActual(), traceList, TRUE);
                }
            }
            else if( xfer.getInCountActual() >= 1 && xfer.getInBuffer()[0] == CHAR_ESC )
            {
                setCurrentState(StateHandshakeSendIdentify_2);
                if(xfer.doTrace(commReturnValue))
                {
                    traceIn((char*)xfer.getInBuffer(), xfer.getInCountActual(), traceList, FALSE);
                }
            }
            else // We will look for more chars until we find what we want or no more chars come.
            {
                // Note that this never stops if we always get more data back.
                // It is expected that no system will ever do this. There are always pauses.
                setCurrentState( StateAbsorb );
                if(xfer.getInCountActual() == 0)
                {
                    setAttemptsRemaining( getAttemptsRemaining() - 1 );
                }
                if(xfer.doTrace(commReturnValue))
                {
                    traceIn((char*)xfer.getInBuffer(), xfer.getInCountActual(), traceList, FALSE);
                }
            }

            break;
        }
    case StateDecode_1:
        {
            if(xfer.getInCountActual() > 0)
            {
                if( xfer.getInBuffer()[0] == CHAR_ESC )
                {
                    setCurrentState(StateGenerate_2);
                    if(xfer.doTrace(commReturnValue))
                    {
                        traceIn((char*)xfer.getInBuffer(), xfer.getInCountActual(), traceList, FALSE);
                    }
                }
                else
                {
                    setPreviousState( getCurrentState() );    // Want the Repeat routine to send us back to this state.
                    setCurrentState( StateRepeat );
                    status = ClientErrors::None;  // Make sure the portfield loop is not compromised!
                    if(xfer.doTrace(commReturnValue))
                    {
                        traceIn((char*)xfer.getInBuffer(), xfer.getInCountActual(), traceList, FALSE);
                    }
                }
            }
            else
            {
                status = ClientErrors::PageNoResponse;
                setCurrentState(StateHandshakeAbort);
                if(xfer.doTrace(commReturnValue))
                {
                    traceIn((char*)xfer.getInBuffer(), xfer.getInCountActual(), traceList, TRUE);
                }
            }

            break;
        }
    case StateDecode_2:
        {
            if(xfer.getInCountActual() > 0)
            {
                /*
                 *  Looking for "[p<CR>"
                 */

                if( xfer.getInBuffer()[0] == '[' &&
                    xfer.getInBuffer()[1] == 'p' &&
                    xfer.getInBuffer()[2] == CHAR_CR  )
                {
                    setCurrentState(StateHandshakeComplete);   // This is the only place we go here (so retries is not reset)
                    setLogOnNeeded(false);                     // We are connected to the tap terminal.
                    if(xfer.doTrace(commReturnValue))
                    {
                        traceIn((char*)xfer.getInBuffer(), xfer.getInCountActual(), traceList, TRUE);
                    }
                }
                else // We will retry the operation
                {
                    setPreviousState( getCurrentState() );    // Leave a breadcrumb for those who follow to get us back here if needed
                    setCurrentState( StateRepeat );
                    status = ClientErrors::None;  // Make sure the portfield loop is not compromised!
                    if(xfer.doTrace(commReturnValue))
                    {
                        traceIn((char*)xfer.getInBuffer(), xfer.getInCountActual(), traceList, FALSE);
                    }
                }
            }
            else
            {
                status = ClientErrors::PageNoResponse;
                setCurrentState(StateHandshakeAbort);
                if(xfer.doTrace(commReturnValue))
                {
                    traceIn((char*)xfer.getInBuffer(), xfer.getInCountActual(), traceList, TRUE);
                }
            }

            break;
        }
    case StateHandshakeDecodeIdentify_2:      // This is a no-win state.  I don't get the old code very much.. CGP 7/5/00
        {
            status = ClientErrors::Unknown;

            if( xfer.getInBuffer()[2] == CHAR_EOT )
            {
                setCurrentState(StateHandshakeAbort);
                if(xfer.doTrace(commReturnValue))
                {
                    traceIn((char*)xfer.getInBuffer(), xfer.getInCountActual(), traceList, TRUE);
                }
            }
            else // We will retry the operation (sometimes this means we fail if attempts is zero)
            {
                setPreviousState( getCurrentState() );    // I need repeat to send me back here
                setCurrentState( StateRepeat );
                status = ClientErrors::None;  // Make sure the portfield loop is not compromised!
                if(xfer.doTrace(commReturnValue))
                {
                    traceIn((char*)xfer.getInBuffer(), xfer.getInCountActual(), traceList, FALSE);
                }
            }

            break;
        }
    case StateHandshakeComplete:
    case StateHandshakeAbort:
        {
            break;
        }
    default:
        {
            CTILOG_ERROR(dout, getName() <<" failed at state "<< getCurrentState());

            setCurrentState(StateHandshakeAbort);
            if(xfer.doTrace(commReturnValue))
            {
                traceIn((char*)xfer.getInBuffer(), xfer.getInCountActual(), traceList, TRUE);
            }

            break;
        }
    }

    return status;
}

/*-----------------------------------------------------------------------------*
 * This method generates/modifies a CtiXfer structure and uses internal state
 * to walk us through the process of dealing with a TAP terminal.
 * It uses the current state and the Xfer InBuffer to get us to the next point
 * in the sequence.
 *-----------------------------------------------------------------------------*/
YukonError_t CtiDeviceTapPagingTerminal::generateCommandHandshake(CtiXfer  &xfer, CtiMessageList &traceList)
{
    YukonError_t status = ClientErrors::None;

    if(gConfigParms.isTrue("DEBUG_TAPTERM_STATE_MACHINE"))
    {
        CTILOG_DEBUG(dout, "Current state "<< getCurrentState() <<". Previous State "<< getPreviousState());
    }

    switch( getCurrentState() )
    {
    case StateHandshakeInitialize:            // Look for any unsolicited ID= Message first... (no outbound CR's)
        {

            xfer.getOutBuffer()[0] = CHAR_CR;
            xfer.setOutCount( 0 );              // We only need an in here. Make sure no outbound is sent.

            xfer.setInCountExpected( _idByteCount ); // Looking for 3 bytes from the TAP (ID=)
            xfer.setInTimeout( 1 );             // It is already there if it is there!

            setAttemptsRemaining( 2 );          // Do this operation once, but return to the following state

            setPreviousState(StateHandshakeSendStart);      // Let anyone find us back...
            setCurrentState(StateHandshakeDecodeStart);

            break;
        }
    case StateHandshakeSendStart:
        {

            xfer.getOutBuffer()[0] = CHAR_CR;
            xfer.setOutCount(1);

            xfer.setInCountExpected( _idByteCount );
            xfer.setInTimeout( TAPTIME_T2 );
            xfer.setCRCFlag(0);                          // No CRC machinations

            if(xfer.doTrace(0))
            {
                traceOut( (char*)xfer.getOutBuffer(), 1, traceList);
            }

            setAttemptsRemaining( TAPCOUNT_N1 );         // Repeat this operation this many times before err-abort

            if(gConfigParms.isTrue("TAPTERM_FORCE_HANDSHAKESENDSTART"))
                setPreviousState(StateHandshakeSendStart);   // Let anyone find us back... 20060227 CGP

            setCurrentState(StateHandshakeDecodeStart);

            break;
        }
    case StateHandshakeSendIdentify:
        {
            INT SendLength;

            // Build up the login message
            xfer.getOutBuffer()[0] = CHAR_ESC;
            xfer.getOutBuffer()[1] = 'P';
            xfer.getOutBuffer()[2] = 'G';
            xfer.getOutBuffer()[3] = '1';

            SendLength = 4;

            if(getPassword().empty())
            {
                xfer.getOutBuffer()[4] = CHAR_CR;
                SendLength += 1;
            }
            else
            {
                ::memcpy (&xfer.getOutBuffer()[4], getPassword().c_str(), 6);
                xfer.getOutBuffer()[10] = CHAR_CR;
                SendLength += 7;
            }

            xfer.setOutCount(SendLength);

            if(xfer.doTrace(0))
            {
                traceOut( (char*)xfer.getOutBuffer(), SendLength, traceList);
            }

            xfer.setInCountExpected(1);
            xfer.setInTimeout( TAPTIME_T3 );

            setAttemptsRemaining( 2 );        //  Try for this twice maybe?

            setCurrentState(StateHandshakeDecodeIdentify);

            break;
        }
    case StateHandshakeSendIdentify_2:
        {
            xfer.setOutCount( 0 );              // We only need an in here. Make sure no outbound is sent.

            xfer.setInCountExpected(2);         // Looking for 2 bytes from the TAP
            xfer.setInTimeout( TAPTIME_T3 );    // A nice protocol defined time..

            setAttemptsRemaining( 0 );          //  One shot at this ok.

            setCurrentState(StateHandshakeDecodeIdentify_2);

            break;
        }
    case StateGenerate_1:
        {
            /*
             *  We are setting up to collect 1 char at a time until we get an ESC
             */
            xfer.setOutCount( 0 );              // We only need an in here. Make sure no outbound is sent.

            xfer.setInCountExpected(1);
            xfer.setInTimeout( TAPTIME_T3 );

            setAttemptsRemaining( 50 );         // We have to abort sometime...

            setPreviousState( getCurrentState() );    // Leave a breadcrumb for those who follow to get us back here if needed
            setCurrentState(StateDecode_1);

            break;
        }
    case StateGenerate_2:
        {
            /*
             *  We get 3 more chrachters following the ESC
             */
            xfer.setOutCount( 0 );              // We only need an in here. Make sure no outbound is sent.

            xfer.setInCountExpected(3);
            xfer.setInTimeout( TAPTIME_T3 );

            setAttemptsRemaining( 0 );          // We have to abort sometime...
            setCurrentState(StateDecode_2);

            break;
        }
    case StateAbsorb:
        {
            /* This case is here to let us pull out any information the TAP thinks
             * it needs to tell us and happily continue looking for what makes
             * Identify happy.
             */

            if( getAttemptsRemaining() <= 0 )
            {
                status = ClientErrors::PageNoResponse;
                setCurrentState( StateHandshakeAbort );
            }
            else
            {
                xfer.setOutCount( 0 );              // Nothing new here..

                xfer.setInCountExpected(1);
                xfer.setInTimeout( TAPTIME_T3 );

                setCurrentState( StateHandshakeDecodeIdentify );
            }

            break;
        }
    case StateRepeat:
        {
            if( getAttemptsRemaining() - 1 <= 0 )
            {
                status = ClientErrors::PageNoResponse;
                setCurrentState( StateHandshakeAbort );
                if(xfer.doTrace(0))
                {
                    traceIn(NULL, 0, traceList, TRUE);                   // Print out whatever we did get.
                }
            }
            else
            {
                // Rely upon the already set up machinery, and just decrement the remaining trys
                setAttemptsRemaining( getAttemptsRemaining() - 1 );               // Repeat this operation this many times before err-abort
                setCurrentState( getPreviousState() );
            }

            break;
        }
    case StateHandshakeComplete:
        {
            break;
        }
    default:
        {
            CTILOG_ERROR(dout, getName() <<" failed at state "<< getCurrentState());

            setCurrentState(StateHandshakeAbort);

            break;
        }
    }

    return status;
}



INT CtiDeviceTapPagingTerminal::traceOut (PCHAR Message, ULONG Count, CtiMessageList &traceList)
{
    ULONG i;
    string outStr;
    ULONG TracePointer;

    for(i = 0; i < Count; i++)
    {
        printChar (outStr, Message[i]);
    }

    CtiTraceMsg trace;

    trace.setBrightYellow();
    trace.setTrace(  CtiTime().asString() + string(" ") );
    trace.setEnd(false);
    traceList.push_back( trace.replicateMessage() );

    trace.setBrightCyan();
    trace.setTrace(  getName() + string(" ") );
    trace.setEnd(false);
    traceList.push_back( trace.replicateMessage() );

    trace.setBrightWhite();
    trace.setTrace(  string("SENT: ") );
    trace.setEnd(false);
    traceList.push_back( trace.replicateMessage() );

    trace.setBrightGreen();
    trace.setTrace( string("\"") + outStr + string("\"") );
    trace.setEnd(true);
    traceList.push_back( trace.replicateMessage() );

    return ClientErrors::None;
}

INT CtiDeviceTapPagingTerminal::traceIn(PCHAR  Message, ULONG  Count, CtiMessageList &traceList, BOOL CompletedMessage)
{
    ULONG i;
    if(Count && Message != NULL)
    {
        for(i = 0; i < Count; i++)
        {
            printChar ( _inStr, Message[i] );
        }
    }

    if(CompletedMessage)
    {
        CtiTraceMsg trace;

        trace.setBrightYellow();
        trace.setTrace(  CtiTime().asString() + string(" ") );
        trace.setEnd(false);
        traceList.push_back( trace.replicateMessage() );

        trace.setBrightCyan();
        trace.setTrace(  getName() + string(" ") );
        trace.setEnd(false);
        traceList.push_back( trace.replicateMessage() );

        trace.setBrightWhite();
        trace.setTrace(  string("RECV: ") );
        trace.setEnd(false);
        traceList.push_back( trace.replicateMessage() );

        trace.setBrightMagenta();
        trace.setTrace( string("\"") + _inStr + string("\"") );
        trace.setEnd(true);
        traceList.push_back( trace.replicateMessage() );

        _inStr = string();     // Reset it for the next message
    }

    return ClientErrors::None;
}


INT CtiDeviceTapPagingTerminal::printChar( string &Str, CHAR Char )
{
    switch(Char)
    {
    case CHAR_CR:
        Str.append("<CR>");
        break;

    case CHAR_LF:
        Str.append("<LF>");
        break;

    case CHAR_ESC:
        Str.append("<ESC>");
        break;

    case CHAR_STX:
        Str.append("<STX>");
        break;

    case CHAR_ETX:
        Str.append("<ETX>");
        break;

    case CHAR_US:
        Str.append("<US>");
        break;

    case CHAR_ETB:
        Str.append("<ETB>");
        break;

    case CHAR_EOT:
        Str.append("<EOT>");
        break;

    case CHAR_SUB:
        Str.append("<SUB>");
        break;

    case CHAR_ACK:
        Str.append("<ACK>");
        break;

    case CHAR_NAK:
        Str.append("<NAK>");
        break;

    case CHAR_RS:
        Str.append("<RS>");
        break;

    default:
        if(Char >= 0x20 && Char < 0x7f)
        {
            Str.append(char2string(Char));
        }
        else
        {
            Str.append( string("<0x") + CtiNumStr(Char).hex().zpad(2) + string(">") );
            if(isDebugLudicrous())
            {
                CTILOG_DEBUG(dout, "Unhandled character 0x" << hex << (int)Char);
            }
        }
        break;
    }

    return ClientErrors::None;
}


YukonError_t CtiDeviceTapPagingTerminal::generateCommand(CtiXfer  &xfer, CtiMessageList &traceList)
{
    INT   i;
    YukonError_t status = ClientErrors::None;

    if(gConfigParms.isTrue("DEBUG_TAPTERM_STATE_MACHINE"))
    {
        CTILOG_DEBUG(dout, "Current state "<< getCurrentState() <<". Previous State "<< getPreviousState());
    }

    switch( getCurrentState() )
    {
    case StateHandshakeComplete:
        {
            setCurrentState(StateScanValueSet3FirstScan);
            setPreviousState(StateScanValueSet3FirstScan);

            /* !!!! FALL THROUGH !!!! */
        }
    case StateScanValueSet3FirstScan:
        {
            /* This is done from Decode2's success block and only once per TAP negotiation */

            /* We may be doing this a few times */
            setAttemptsRemaining( TAPCOUNT_N2 );

            /* !!!! FALL THROUGH !!!! FALL THROUGH !!!! */
        }
    case StateScanValueSet3:
        {
            INT   sendCnt = 0;
            CHAR  faker[32];
            UINT  pageCharCount = 0;

            CHAR* out = (CHAR*)xfer.getOutBuffer();

            out[sendCnt++] = CHAR_STX;

            for(i = 0; i < getPaging().getPagerNumber().length(); i++)
            {
                out[sendCnt++] = getPaging().getPagerNumber()[(size_t)i];
            }

            out[sendCnt++] = CHAR_CR;

            pageCharCount = 1-sendCnt;  // Don't count this clutter as billable characters.

            if( gDoPrefix && allowPrefix())
            {
                /* Stick a little TAPTerm fakey in there */
                out[sendCnt++] = incrementPagePrefix();

                ::sprintf(faker, "%05d", (getPageCount()++ & 0x0000ffff));

                for(i = 0; i < 5; i++)
                {
                    /* Check if this is a SUB character */
                    if(faker[i] < 0x20)
                    {
                        out[sendCnt++] = CHAR_SUB;
                        out[sendCnt++] = faker[i] + 0x40;
                    }
                    else
                    {
                        out[sendCnt++] = faker[i] & 0x7f;
                    }
                }
            }


            /* And now load up the message */

            for(i = 0; i < getPageLength(); i++)
            {
                /* Check if this is a SUB character */
                if(getPageBuffer(i) < 0x20)
                {
                    out[sendCnt++] = CHAR_SUB;
                    out[sendCnt++] = getPageBuffer(i) + 0x40;
                }
                else
                {
                    out[sendCnt++] = getPageBuffer(i) & 0x7f;
                }
            }

            pageCharCount += sendCnt;

            out[sendCnt++] = CHAR_CR;
            out[sendCnt++] = CHAR_ETX;


            /* Compute the CheckSum */
            USHORT CheckSum = 0;

            for(i = 0; i < sendCnt; i++)
            {
                CheckSum += out[i] & 0x7f;
            }

            out[sendCnt++] = ((CheckSum >> 8) & 0x0f) + 0x30;
            out[sendCnt++] = ((CheckSum >> 4) & 0x0f) + 0x30;
            out[sendCnt++] = (CheckSum & 0x0f) + 0x30;

            out[sendCnt++] = CHAR_CR;

            xfer.setOutCount( sendCnt );

            if(xfer.doTrace(0))
            {
                traceOut( out, sendCnt, traceList);
            }

            xfer.setInCountExpected(1);
            xfer.setInTimeout( TAPTIME_T3 );    // A nice protocol defined time..

            setCurrentState(StateScanDecode3);

            updatePageCountData(pageCharCount);

            break;
        }
    case StateScanValueSet4:
        {
            /*
             *  The page went out and an ESC came back..  we get 2 more bytes
             */

            xfer.setOutCount( 0 );              // Nothing new here..

            xfer.setInCountExpected(2);
            xfer.setInTimeout( TAPTIME_T3 );    // A nice protocol defined time..

            setCurrentState(StateScanDecode4);

            break;
        }
    case StateScanValueSet5:
        {
            /*
             *  The page went out and an ACK, NAK, or RS came back.. we get one more byte
             */

            xfer.setOutCount( 0 );              // Nothing new here..

            xfer.setInCountExpected(1);
            xfer.setInTimeout( TAPTIME_T3 );    // A nice protocol defined time..

            setCurrentState(StateScanDecode5);

            break;
        }
    case StateScanPageSentResponse:
        {
            /*
             *  This state keeps us pulling off values until Decode3 is satisfied, or the retries fail
             */
            xfer.setOutCount( 0 );              // Nothing new here..

            xfer.setInCountExpected(1);
            xfer.setInTimeout( TAPTIME_T3 );    // A nice protocol defined time..

            setCurrentState(StateScanDecode3);

            break;
        }
    case StateRepeat:
        {

            if( getAttemptsRemaining() - 1 <= 0 )
            {
                setCurrentState( StateScanAbort );
                if(xfer.doTrace(0))
                {
                    traceIn(NULL, 0, traceList, TRUE);                   // Print out whatever we did get.
                }
            }
            else
            {
                // Rely upon the already set up machinery, and just decrement the remaining trys
                setAttemptsRemaining( getAttemptsRemaining() - 1 );               // Repeat this operation this many times before err-abort
                setCurrentState( getPreviousState() );
            }

            break;
        }
    case StateScanComplete:
        {
            break;
        }
    default:
        {
            CTILOG_ERROR(dout, getName() <<" failed at state "<< getCurrentState());

            setCurrentState(StateScanAbort);

            break;
        }
    }

    return status;
}

YukonError_t CtiDeviceTapPagingTerminal::decodeResponse(CtiXfer  &xfer, YukonError_t commReturnValue, CtiMessageList &traceList)
{
    YukonError_t status = commReturnValue;

    if( status == ClientErrors::None )     // Communications must have been successful
    {
        if(gConfigParms.isTrue("DEBUG_TAPTERM_STATE_MACHINE"))
        {
            CTILOG_DEBUG(dout, "Current state "<< getCurrentState() <<". Previous State "<< getPreviousState());
        }

        switch( getCurrentState() )
        {
        case StateScanDecode3:
            {
                /*
                 *  We just sent out our page, we are going to look at the one character returned and make sure we
                 *  like what we see.
                 */
                if( xfer.getInCountActual() > 0 &&
                    xfer.getInBuffer()[0] != CHAR_ACK &&
                    xfer.getInBuffer()[0] != CHAR_NAK &&
                    xfer.getInBuffer()[0] != CHAR_ESC &&
                    xfer.getInBuffer()[0] != CHAR_RS )
                {
                    setCurrentState( StateScanPageSentResponse );      // Picks up any unexpected garbage..
                }
                else if(xfer.getInBuffer()[0] == CHAR_ESC)
                {
                    setCurrentState( StateScanValueSet4 );
                }
                else
                {
                    setCurrentState( StateScanValueSet5 );
                }

                if(xfer.doTrace(commReturnValue))
                {
                    traceIn((char*)xfer.getInBuffer(), xfer.getInCountActual(), traceList, FALSE);
                }
                break;
            }
        case StateScanDecode4:
        case StateScanDecode5:
            {
                if(xfer.getInCountActual() > 0 )
                {
                    if( xfer.getInBuffer()[0] == CHAR_NAK && getAttemptsRemaining() > 0 )
                    {
                        setPreviousState( StateScanValueSet3 );      // Make it go back to this point (via the repeat check) and try again.
                        setCurrentState( StateRepeat );
                        if(xfer.doTrace(commReturnValue))
                        {
                            traceIn((char*)xfer.getInBuffer(), xfer.getInCountActual(), traceList, FALSE);
                        }
                    }
                    else if(xfer.getInBuffer()[0] == CHAR_ESC || xfer.getInBuffer()[0] == CHAR_NAK)
                    {
                        status = ClientErrors::PageNAK;
                        setCurrentState( StateAbort );
                        if(xfer.doTrace(commReturnValue))
                        {
                            traceIn((char*)xfer.getInBuffer(), xfer.getInCountActual(), traceList, TRUE);
                        }
                    }
                    else  // This is the good state;
                    {
                        if(!_inStr.find("<ACK>")!=string::npos)
                        {
                            if(_inStr.find("<RS>")!=string::npos)
                            {
                                status = ClientErrors::PageRS;
                            }
                            else if(_inStr.find("<NAK>")!=string::npos)
                            {
                                status = ClientErrors::PageNAK;
                            }
                            else if(_inStr.find("<EOT>")!=string::npos)
                            {
                                setLogOnNeeded(true);
                            }
                        }

                        setCurrentState( StateScanComplete );
                        if(xfer.doTrace(commReturnValue))
                        {
                            traceIn((char*)xfer.getInBuffer(), xfer.getInCountActual(), traceList, TRUE);
                        }

                        {
                            Cti::StreamBuffer slogMessage;
                            slogMessage << getName() <<": "<< _outMessage->Request.CommandStr;
                            if(_outMessage->TargetID != 0 && _outMessage->TargetID != _outMessage->DeviceID)
                            {
                                slogMessage << endl <<"Group Id: "<< _outMessage->TargetID;
                            }

                            CTILOG_INFO(slog, slogMessage);
                        }

                        //Message sent and accepted. Add to verification list!
                        //If Verification is set, this is our second time through..
                        if( !_outMessage->VerificationSequence )
                        {
                            _outMessage->VerificationSequence = VerificationSequenceGen();
                        }
                        CtiVerificationWork *work = CTIDBG_new CtiVerificationWork(CtiVerificationBase::Protocol_SNPP, *_outMessage, _outMessage->Request.CommandStr, reinterpret_cast<char *>(_outMessage->Buffer.OutMessage), seconds(700));//11.6 minutes
                        _verification_objects.push(work);
                    }
                }
                else
                {
                    setLogOnNeeded(true);
                    status = ClientErrors::PageNoResponse;
                    setCurrentState( StateAbort );
                    if(xfer.doTrace(commReturnValue))
                    {
                        traceIn((char*)xfer.getInBuffer(), xfer.getInCountActual(), traceList, TRUE);
                    }
                }

                break;
            }
        case StateScanAbort:
            {
                break;
            }
        default:
            {
                CTILOG_ERROR(dout, getName() <<" failed at state "<< getCurrentState());

                setLogOnNeeded(true);
                setCurrentState(StateAbort);
                if(xfer.doTrace(commReturnValue))
                {
                    traceIn((char*)xfer.getInBuffer(), xfer.getInCountActual(), traceList, TRUE);
                }
                break;
            }
        }
    }
    else
    {
        CTILOG_ERROR(dout, getName() <<" status "<< status);
    }

    return status;
}


YukonError_t CtiDeviceTapPagingTerminal::generateCommandDisconnect (CtiXfer  &xfer, CtiMessageList &traceList)
{
    INT   i;
    YukonError_t status = ClientErrors::None;

    if(gConfigParms.isTrue("DEBUG_TAPTERM_STATE_MACHINE"))
    {
        CTILOG_DEBUG(dout, "Current state "<< getCurrentState() <<". Previous State "<< getPreviousState());
    }

    if(gConfigParms.isTrue("TAPTERM_FORCE_EOT"))
    {
        setLogOnNeeded(true);
    }

    if( getLogOnNeeded() == TRUE )
    {
        switch( getCurrentState() )
        {
        case StateScanComplete:
            {
                /* Start us out on the correct path */
                setCurrentState(StateGenerate_1);
                setPreviousState(StateGenerate_1);
                /* !!!! FALL THROUGH !!! */
            }
        case StateGenerate_1:
            {
                /*
                 *  Smack the TAP with a disconnect sequence
                 */
                xfer.getOutBuffer()[0] = CHAR_EOT;
                xfer.getOutBuffer()[1] = CHAR_CR;
                xfer.setOutCount( 2 );

                if(xfer.doTrace(0))
                {
                    traceOut( (char*)xfer.getOutBuffer(), 2, traceList);
                }

                xfer.setInCountExpected(1);
                xfer.setInTimeout( TAPTIME_T3 );

                setAttemptsRemaining( 1 );          // Try this about 1 time...
                setCurrentState(StateDecode_1);

                break;
            }
        case StateGenerate_2:
            {
                /*
                 *  get 2 more characters ...
                 */
                xfer.setOutCount( 0 );              // We only need an in here. Make sure no outbound is sent.

                xfer.setInCountExpected(2);         // Looking for 2 bytes from the TAP
                xfer.setInTimeout( TAPTIME_T3 );    // A nice protocol defined time..

                setAttemptsRemaining( 1 );          // Try this about 1 times or so...
                setCurrentState(StateDecode_2);

                break;
            }
        case StateRepeat:
            {
                if( getAttemptsRemaining() - 1 <= 0 )
                {
                    setCurrentState( StateScanAbort );
                    if(xfer.doTrace(0))
                    {
                        traceIn(NULL, 0, traceList, TRUE);                   // Print out whatever we did get.
                    }
                }
                else
                {
                    // Rely upon the already set up machinery, and just decrement the remaining trys
                    setAttemptsRemaining( getAttemptsRemaining() - 1 );               // Repeat this operation this many times before err-abort
                    setCurrentState( getPreviousState() );
                }

                break;
            }
        case StateAbsorb:
            {
                /* This case is here to let us pull out any information the TAP thinks
                 * it needs to tell us and happily continue looking for what makes
                 * Decode_1 happy.
                 */

                xfer.setOutCount( 0 );              // Nothing new here..

                xfer.setInCountExpected(1);
                xfer.setInTimeout( TAPTIME_T3 );

                setCurrentState(StateDecode_1);

                break;
            }
        case StateComplete:
            {
                break;
            }
        default:
            {
                CTILOG_ERROR(dout, getName() <<" failed at state "<< getCurrentState());

                setLogOnNeeded(true);
                setCurrentState(StateAbort);

                break;
            }
        }
    }
    else
    {
        xfer.setOutCount(0);
        xfer.setInCountExpected(0);

        setCurrentState( StateCompleteNoHUP );
    }

    return status;
}

YukonError_t CtiDeviceTapPagingTerminal::decodeResponseDisconnect (CtiXfer &xfer, YukonError_t commReturnValue, CtiMessageList &traceList)
{
    YukonError_t status = commReturnValue;

    if( status == ClientErrors::None )     // Communications must have been successful
    {
        if(gConfigParms.isTrue("DEBUG_TAPTERM_STATE_MACHINE"))
        {
            CTILOG_DEBUG(dout, "Current state "<< getCurrentState() <<". Previous State "<< getPreviousState());
        }

        setPreviousState( getCurrentState() );    // Leave a breadcrumb for those who follow to get us back here if needed

        switch( getCurrentState() )
        {
        case StateDecode_1:     // Looking for a CHAR_ESC
            {
                if( (xfer.getInCountActual()) > 0 )
                {
                    if( xfer.getInBuffer()[0] != CHAR_ESC )
                    {
                        setCurrentState( StateAbsorb );
                    }
                    else
                    {
                        setCurrentState(StateGenerate_2);
                    }

                    if(xfer.doTrace(commReturnValue))
                    {
                        traceIn((char*)xfer.getInBuffer(), xfer.getInCountActual(), traceList, FALSE);
                    }
                }
                else // We will retry the operation
                {
                    setCurrentState( StateRepeat );
                    if(xfer.doTrace(commReturnValue))
                    {
                        traceIn((char*)xfer.getInBuffer(), xfer.getInCountActual(), traceList, FALSE);
                    }
                }

                break;
            }
        case StateDecode_2:     // Looking for a CHAR_ESC
            {
                if( (xfer.getInCountActual()) > 0 )
                {
                    setCurrentState( StateComplete );
                    if(xfer.doTrace(commReturnValue))
                    {
                        traceIn((char*)xfer.getInBuffer(), xfer.getInCountActual(), traceList, TRUE);
                    }
                }
                else // We will retry the operation
                {
                    setCurrentState( StateRepeat );
                    if(xfer.doTrace(commReturnValue))
                    {
                        traceIn((char*)xfer.getInBuffer(), xfer.getInCountActual(), traceList, FALSE);
                    }
                }

                break;
            }
        case StateComplete:
        case StateCompleteNoHUP:
            {
                break;
            }
        default:
            {
                CTILOG_ERROR(dout, getName() <<" failed at state "<< getCurrentState());

                setLogOnNeeded(true);
                setCurrentState(StateAbort);
                if(xfer.doTrace(commReturnValue))
                {
                    traceIn((char*)xfer.getInBuffer(), xfer.getInCountActual(), traceList, TRUE);
                }
                break;
            }
        }
    }
    else
    {
        CTILOG_ERROR(dout, getName() <<" status "<< status);
    }

    return status;
}


CtiDeviceIED& CtiDeviceTapPagingTerminal::setInitialState (const LONG oldid)
{
    if( oldid > 0 && getUniqueIdentifier() == oldid)
    {
        if(isDebugLudicrous())
        {
            CTILOG_DEBUG(dout, "Port has indicated a connected device swap. "<< getName() <<" has replaced DEVID "<< oldid <<" as the currently connected device");
        }
        setCurrentState(StateHandshakeComplete);    // TAP is already connected on this port
        setLogOnNeeded(false);                      // We will skip the logon, and proceed to <STX>
    }
    else if( getLogOnNeeded() )                     // If this is forced on a Glenaire input, the device <NAK>s us.
    {
        setCurrentState(StateHandshakeSendStart);
    }
    else                                                                 // Device is already online and init
    {
        setCurrentState(StateHandshakeComplete);
    }

    if(gConfigParms.isTrue("DEBUG_TAPTERM_STATE_MACHINE"))
    {
        CTILOG_DEBUG(dout, "setInitialState() => oldID = "<< oldid <<". Current state "<< getCurrentState());
    }

    return *this;
}

CHAR CtiDeviceTapPagingTerminal::incrementPagePrefix()
{
    CHAR prefix = getPagePrefix();

    if( ((CHAR)(getPagePrefix() + 1)) < 'e' )
    {
        setPagePrefix( (getPagePrefix() + 1) );
    }
    else
    {
        setPagePrefix( 'a' );
    }

    return prefix;
}

CtiDeviceTapPagingTerminal& CtiDeviceTapPagingTerminal::setSendFiller(bool yesno)
{
    _sendFiller = yesno;
    return *this;
}
bool CtiDeviceTapPagingTerminal::getSendFiller() const
{
    return _sendFiller;
}

CtiDeviceTapPagingTerminal::CtiDeviceTapPagingTerminal() :
_allowPrefix(true),
_pagesPerMinute(0),
_sendFiller(true),
_idByteCount(20),
_pageCount(0),
_pagePrefix('a'),
_pageLength(0),
_inStr(string()),
_pageBuffer(NULL),
_outMessage(NULL),
_pacingReport(false)
{}

ULONG CtiDeviceTapPagingTerminal::getUniqueIdentifier() const
{
    ULONG CSum = 0;

    /*
     *  This is an undocumented cparm.  It is in here only due to a lack of clarity on the effects of making the change in the else
     *  clause.  Can you say emergency backup?
     */
    if( gConfigParms.isTrue("TCPARM_USE_OLD_TAP_GUID") )
    {
        string num;

        for(int i = 0; i < getPaging().getPagerNumber().length(); i++ )
        {
            CHAR ch = getPaging().getPagerNumber()[(size_t)i];

            if( ::isdigit(ch) )
                num.append(char2string(ch));
        }

        // Now get a standard CRC
        CSum = (ULONG)CCITT16CRC( 0, (BYTE*)num.c_str(), num.length(), FALSE);
    }
    else
    {
        CSum = Inherited::getUniqueIdentifier();
    }

    return CSum;
}

bool CtiDeviceTapPagingTerminal::blockedByPageRate() const
{
    return (_pagesPerMinute >= pagesPerMinute && pagesPerMinute > 0);
}

bool CtiDeviceTapPagingTerminal::devicePacingExceeded()
{
    bool toofast = false;

    if(pagesPerMinute > 0)
    {
        CtiTime now;

        if(now >= _pacingTimeStamp)
        {
            _pagesPerMinute = 1;
            _pacingTimeStamp = nextScheduledTimeAlignedOnRate( now, 60 );
            _pacingReport = false;
        }
        else if(_pagesPerMinute >= pagesPerMinute)   // This time is allowed for the paging company to clear buffers.
        {
            if(!_pacingReport)
            {
                _pacingReport = true;
                CTILOG_WARN(dout, "Configuration PAGES_PER_MINUTE limits paging to " << pagesPerMinute << " pages per minute.  Next page allowed at " << _pacingTimeStamp);
            }

            toofast = true;
        }
        else
        {
            _pagesPerMinute++;
        }
    }

    return toofast;
}

bool CtiDeviceTapPagingTerminal::allowPrefix() const
{
    return _allowPrefix;
}

CtiDeviceTapPagingTerminal& CtiDeviceTapPagingTerminal::setAllowPrefix(bool val)
{
    _allowPrefix = val;
    return *this;
}


void CtiDeviceTapPagingTerminal::updatePageCountData(UINT addition)
{
    CtiPointAccumulatorSPtr pAccumPoint;
    ULONG curPulseValue;

    /* Check if there is a pulse point */
    if(pAccumPoint = boost::static_pointer_cast<CtiPointAccumulator>(getDevicePointOffsetTypeEqual(PAGECOUNTOFFSET, PulseAccumulatorPointType)))
    {
        /* Copy the pulses */
        curPulseValue = pAccumPoint->getPointHistory().getPresentPulseCount() + 1;
        pAccumPoint->getPointHistory().setPreviousPulseCount(pAccumPoint->getPointHistory().getPresentPulseCount());
        pAccumPoint->getPointHistory().setPresentPulseCount(curPulseValue);
    }

    /* Check if there is a pulse point */
    if(pAccumPoint = boost::static_pointer_cast<CtiPointAccumulator>(getDevicePointOffsetTypeEqual(PAGECHARCOUNTOFFSET, PulseAccumulatorPointType)))
    {
        /* Copy the pulses */
        curPulseValue = pAccumPoint->getPointHistory().getPresentPulseCount() + addition;
        pAccumPoint->getPointHistory().setPreviousPulseCount(pAccumPoint->getPointHistory().getPresentPulseCount());
        pAccumPoint->getPointHistory().setPresentPulseCount(curPulseValue);
    }

    return;
}

CtiMessage* CtiDeviceTapPagingTerminal::rsvpToDispatch(bool clearMessage)
{
    CtiPointAccumulatorSPtr pAccumPoint;
    FLOAT PValue;
    char tStr[128];

    CtiPointDataMsg *pData = 0;
    CtiReturnMsg *returnMsg = 0;    // Message sent to VanGogh, inherits from Multi

    /* Check if there is a pulse point */
    if(pAccumPoint = boost::static_pointer_cast<CtiPointAccumulator>(getDevicePointOffsetTypeEqual(PAGECOUNTOFFSET, PulseAccumulatorPointType)))
    {
        PValue = (FLOAT) pAccumPoint->getPointHistory().getPresentPulseCount() * pAccumPoint->getMultiplier();
        PValue += pAccumPoint->getDataOffset();

        _snprintf(tStr, 126, "%s point %s = %f", getName().c_str(), pAccumPoint->getName().c_str(), PValue);

        pData = CTIDBG_new CtiPointDataMsg(pAccumPoint->getPointID(), PValue, NormalQuality, PulseAccumulatorPointType, tStr);

        if(pData != NULL)
        {
            if(!returnMsg) returnMsg = (CtiReturnMsg*) CTIDBG_new CtiReturnMsg(getID(), getName() + " rsvpToDispatch");

            returnMsg->PointData().push_back(pData);
            pData = NULL;  // We just put it on the list...
        }
    }

    /* Check if there is a pulse point */
    if(pAccumPoint = boost::static_pointer_cast<CtiPointAccumulator>(getDevicePointOffsetTypeEqual(PAGECHARCOUNTOFFSET, PulseAccumulatorPointType)))
    {
        PValue = (FLOAT) pAccumPoint->getPointHistory().getPresentPulseCount() * pAccumPoint->getMultiplier();
        /* Apply offset */
        PValue += pAccumPoint->getDataOffset();

        _snprintf(tStr, 126, "%s point %s = %f", getName().c_str(), pAccumPoint->getName().c_str(), PValue);

        pData = CTIDBG_new CtiPointDataMsg(pAccumPoint->getPointID(), PValue, NormalQuality, PulseAccumulatorPointType, tStr);

        if(pData != NULL)
        {
            if(!returnMsg) returnMsg = (CtiReturnMsg*) CTIDBG_new CtiReturnMsg(getID(), getName() + " rsvpToDispatch");
            returnMsg->PointData().push_back(pData);
            pData = NULL;  // We just put it on the list...
        }
    }

    return returnMsg;
}

void CtiDeviceTapPagingTerminal::getVerificationObjects(queue< CtiVerificationBase * > &work_queue)
{
    while( !_verification_objects.empty() )
    {
        work_queue.push(_verification_objects.front());

        _verification_objects.pop();
    }
}

