/*-----------------------------------------------------------------------------*
*
* File:   dev_tap
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_tap.cpp-arc  $
* REVISION     :  $Revision: 1.19 $
* DATE         :  $Date: 2005/06/29 19:32:31 $
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

static int pagesPerMinute  = gConfigParms.getValueAsInt("PAGES_PER_MINUTE", 0);

CtiDeviceTapPagingTerminal::~CtiDeviceTapPagingTerminal()
{
    freeDataBins();
}

INT CtiDeviceTapPagingTerminal::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
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
            retList.insert( CTIDBG_new CtiReturnMsg(getID(),
                                                    RWCString(OutMessage->Request.CommandStr),
                                                    RWCString("TAP Devices do not support this command (yet?)"),
                                                    nRet,
                                                    OutMessage->Request.RouteID,
                                                    OutMessage->Request.MacroOffset,
                                                    OutMessage->Request.Attempt,
                                                    OutMessage->Request.TrxID,
                                                    OutMessage->Request.UserID,
                                                    OutMessage->Request.SOE,
                                                    RWOrdered()));

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

INT CtiDeviceTapPagingTerminal::allocateDataBins(OUTMESS *oMess)
{
    if(_outMessage == NULL)
    {
        _outMessage = CTIDBG_new OUTMESS(*oMess);//new out message
    }
    if(_pageBuffer == NULL)
    {
        _pageBuffer = (CHAR*) _outMessage->Buffer.TAPSt.Message;//point pointer to new message structure.
        setPageLength(_outMessage->Buffer.TAPSt.Length);
        
    }

    return NORMAL;
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

    return NORMAL;
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
        memcpy(_pageBuffer, copyBuffer, len);

    return *this;
}
*/

RWCString CtiDeviceTapPagingTerminal::getDescription(const CtiCommandParser & parse) const
{
    RWCString trelay;

    trelay = "PAGE: " + getName();

    return trelay;
}

INT CtiDeviceTapPagingTerminal::decodeResponseHandshake(CtiXfer &xfer,INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList)
{
    INT status = commReturnValue;

    switch( getCurrentState() )
    {
    case StateHandshakeDecodeStart:
        {
            /* Check if this is ID= */
            if(xfer.getInCountActual() >= 2)
            {
                if((strstr((char*)xfer.getInBuffer(), "ID") != NULL) || (strstr((char*)xfer.getInBuffer(), "\r\n") != NULL))
                {

                    if(commReturnValue == READTIMEOUT)
                    {
                        // We got the bytes we needed though.. so lets ignore the error!
                        status = NORMAL;  // Make sure the portfield loop is not compromised!

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
                    if(commReturnValue != READTIMEOUT)  // We had a bunch of bytes there...
                    {
                        _idByteCount = 20;
                        xfer.setInCountExpected( _idByteCount ); // Get a big bunch on the next try (we will timeout I expect...)
                    }

                    if(getPreviousState() == StateHandshakeSendStart)     // If we were looking for an early ID=
                    {
                        xfer.setOutCount(1);
                        setCurrentState( StateRepeat );
                        status = NORMAL;  // Make sure the portfield loop is not compromised!
                    }
                    else
                    {
                        setPreviousState( getCurrentState() );    // Want the Repeat routine to send us back to this state.
                        setCurrentState( StateRepeat );
                        status = NORMAL;  // Make sure the portfield loop is not compromised!
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
                status = NORMAL;  // Make sure the portfield loop is not compromised!
                if(xfer.doTrace(commReturnValue))
                {
                    traceIn((char*)xfer.getInBuffer(), xfer.getInCountActual(), traceList, TRUE);
                }

            }

            break;
        }
    case StateHandshakeDecodeIdentify:
        {
            if( xfer.getInBuffer()[0] == CHAR_ACK )
            {
                setCurrentState(StateGenerate_1);
                if(xfer.doTrace(commReturnValue))
                {
                    traceIn((char*)xfer.getInBuffer(), xfer.getInCountActual(), traceList, FALSE);
                }
            }
            else if( xfer.getInBuffer()[0] == CHAR_NAK )
            {
                status = ERRUNKNOWN;
                setCurrentState(StateHandshakeAbort);
                if(xfer.doTrace(commReturnValue))
                {
                    traceIn((char*)xfer.getInBuffer(), xfer.getInCountActual(), traceList, TRUE);
                }
            }
            else if( xfer.getInBuffer()[0] == CHAR_ESC )
            {
                setCurrentState(StateHandshakeSendIdentify_2);
                if(xfer.doTrace(commReturnValue))
                {
                    traceIn((char*)xfer.getInBuffer(), xfer.getInCountActual(), traceList, FALSE);
                }
            }
            else // We will retry the operation
            {
                setCurrentState( StateAbsorb );
                if(xfer.doTrace(commReturnValue))
                {
                    traceIn((char*)xfer.getInBuffer(), xfer.getInCountActual(), traceList, FALSE);
                }

                if(commReturnValue == NORMAL)
                {
                    // It is unknown how big the message may be coming from the TAP...
                    // If we get anything, we will continue to read data.
                    if(getAttemptsRemaining() < 3)
                    {
                        setAttemptsRemaining( getAttemptsRemaining() + 1 );
                    }
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
                    status = NORMAL;  // Make sure the portfield loop is not compromised!
                    if(xfer.doTrace(commReturnValue))
                    {
                        traceIn((char*)xfer.getInBuffer(), xfer.getInCountActual(), traceList, FALSE);
                    }
                }
            }
            else
            {
                status = ErrorPageNoResponse;
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
                    setLogOnNeeded(FALSE);                     // We are connected to the tap terminal.
                    if(xfer.doTrace(commReturnValue))
                    {
                        traceIn((char*)xfer.getInBuffer(), xfer.getInCountActual(), traceList, TRUE);
                    }
                }
                else // We will retry the operation
                {
                    setPreviousState( getCurrentState() );    // Leave a breadcrumb for those who follow to get us back here if needed
                    setCurrentState( StateRepeat );
                    status = NORMAL;  // Make sure the portfield loop is not compromised!
                    if(xfer.doTrace(commReturnValue))
                    {
                        traceIn((char*)xfer.getInBuffer(), xfer.getInCountActual(), traceList, FALSE);
                    }
                }
            }
            else
            {
                status = ErrorPageNoResponse;
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
            status = ERRUNKNOWN;

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
                status = NORMAL;  // Make sure the portfield loop is not compromised!
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
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "  " << getName() << " Failed at state " << getCurrentState() << endl;
            }

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
INT CtiDeviceTapPagingTerminal::generateCommandHandshake(CtiXfer  &xfer, RWTPtrSlist< CtiMessage > &traceList)
{
    INT status = NORMAL;

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

            if(getPassword().isNull())
            {
                xfer.getOutBuffer()[4] = CHAR_CR;
                SendLength += 1;
            }
            else
            {
                memcpy (&xfer.getOutBuffer()[4], getPassword().data(), 6);
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

            if( getAttemptsRemaining() - 1 <= 0 )
            {
                status = ErrorPageNoResponse;
                setCurrentState( StateHandshakeAbort );
            }
            else
            {
                // Rely upon the already set up machinery, and just decrement the remaining trys
                setAttemptsRemaining( getAttemptsRemaining() - 1 );               // Repeat this operation this many times before err-abort

                xfer.setOutCount( 0 );              // Nothing CTIDBG_new here..

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
                status = ErrorPageNoResponse;
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
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "  " << getName() << "  Failed at state " << getCurrentState() << endl;
            }

            setCurrentState(StateHandshakeAbort);

            break;
        }
    }

    return status;
}



INT CtiDeviceTapPagingTerminal::traceOut (PCHAR Message, ULONG Count, RWTPtrSlist< CtiMessage > &traceList)
{
    ULONG i;
    RWCString outStr;
    ULONG TracePointer;

    for(i = 0; i < Count; i++)
    {
        printChar (outStr, Message[i]);
    }

    CtiTraceMsg trace;

    trace.setBrightYellow();
    trace.setTrace(  RWTime().asString() + RWCString(" ") );
    trace.setEnd(false);
    traceList.insert( trace.replicateMessage() );

    trace.setBrightCyan();
    trace.setTrace(  getName() + RWCString(" ") );
    trace.setEnd(false);
    traceList.insert( trace.replicateMessage() );

    trace.setBrightWhite();
    trace.setTrace(  RWCString("SENT: ") );
    trace.setEnd(false);
    traceList.insert( trace.replicateMessage() );

    trace.setBrightGreen();
    trace.setTrace( RWCString("\"") + outStr + RWCString("\"") );
    trace.setEnd(true);
    traceList.insert( trace.replicateMessage() );

    return(NORMAL);
}

INT CtiDeviceTapPagingTerminal::traceIn(PCHAR  Message, ULONG  Count, RWTPtrSlist< CtiMessage > &traceList, BOOL CompletedMessage)
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
        trace.setTrace(  RWTime().asString() + RWCString(" ") );
        trace.setEnd(false);
        traceList.insert( trace.replicateMessage() );

        trace.setBrightCyan();
        trace.setTrace(  getName() + RWCString(" ") );
        trace.setEnd(false);
        traceList.insert( trace.replicateMessage() );

        trace.setBrightWhite();
        trace.setTrace(  RWCString("RECV: ") );
        trace.setEnd(false);
        traceList.insert( trace.replicateMessage() );

        trace.setBrightMagenta();
        trace.setTrace( RWCString("\"") + _inStr + RWCString("\"") );
        trace.setEnd(true);
        traceList.insert( trace.replicateMessage() );

        _inStr = RWCString();     // Reset it for the next message
    }

    return(NORMAL);
}


INT CtiDeviceTapPagingTerminal::printChar( RWCString &Str, CHAR Char )
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
            Str.append(Char);
        }
        else
        {
            Str.append( RWCString("<0x") + CtiNumStr(Char).hex().zpad(2) + RWCString(">") );
            if(getDebugLevel() & DEBUGLEVEL_LUDICROUS)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << " Unhandled character 0x" << hex << (int)Char << dec << endl;
            }
        }
        break;
    }

    return(NORMAL);
}


INT CtiDeviceTapPagingTerminal::generateCommand(CtiXfer  &xfer, RWTPtrSlist< CtiMessage > &traceList)
{
    INT   i;
    INT   status = NORMAL;

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

            for(i = 0; i < getTap().getPagerNumber().length(); i++)
            {
                out[sendCnt++] = getTap().getPagerNumber()[(size_t)i];
            }

            out[sendCnt++] = CHAR_CR;

            pageCharCount = 1-sendCnt;  // Don't count this clutter as billable characters.

            if( gDoPrefix && allowPrefix())
            {
                /* Stick a little TAPTerm fakey in there */
                out[sendCnt++] = incrementPagePrefix();

                sprintf(faker, "%05d", (getPageCount()++ & 0x0000ffff));

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

            xfer.setOutCount( 0 );              // Nothing CTIDBG_new here..

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

            xfer.setOutCount( 0 );              // Nothing CTIDBG_new here..

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
            xfer.setOutCount( 0 );              // Nothing CTIDBG_new here..

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
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") "<< endl;
                dout << "  " << getName() << "  Failed at state " << getCurrentState() << endl;
            }

            setCurrentState(StateScanAbort);

            break;
        }
    }

    return status;
}

INT CtiDeviceTapPagingTerminal::decodeResponse(CtiXfer  &xfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList)
{
    INT status = commReturnValue;

    if( status == NORMAL )     // Communications must have been successful
    {
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
                        status = ErrorPageNAK;
                        setCurrentState( StateAbort );
                        if(xfer.doTrace(commReturnValue))
                        {
                            traceIn((char*)xfer.getInBuffer(), xfer.getInCountActual(), traceList, TRUE);
                        }
                    }
                    else  // This is the good state;
                    {
                        if(!_inStr.contains("<ACK>"))
                        {
                            if(_inStr.contains("<RS>"))
                            {
                                status = ErrorPageRS;
                            }
                            else if(_inStr.contains("<NAK>"))
                            {
                                status = ErrorPageNAK;
                            }
                        }

                        setCurrentState( StateScanComplete );
                        if(xfer.doTrace(commReturnValue))
                        {
                            traceIn((char*)xfer.getInBuffer(), xfer.getInCountActual(), traceList, TRUE);
                        }

                        //Message sent and accepted. Add to verification list!
                        //If Verification is set, this is our second time through..
                        if( !_outMessage->VerificationSequence )
                        {
                            _outMessage->VerificationSequence = VerificationSequenceGen();
                        }
                        CtiVerificationWork *work = CTIDBG_new CtiVerificationWork(CtiVerificationBase::Protocol_SNPP, *_outMessage, reinterpret_cast<char *>(_outMessage->Buffer.OutMessage), seconds(700));//11.6 minutes
                        _verification_objects.push(work);
                    }
                }
                else
                {
                    setLogOnNeeded(TRUE);
                    status = ErrorPageNoResponse;
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
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << "  Failed at state " << getCurrentState() << endl;
                }

                setLogOnNeeded(TRUE);
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
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Yukon Status **** " << status << " " << getName() << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return status;
}


INT CtiDeviceTapPagingTerminal::generateCommandDisconnect (CtiXfer  &xfer, RWTPtrSlist< CtiMessage > &traceList)
{
    INT   i;
    INT   status = NORMAL;

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

                xfer.setOutCount( 0 );              // Nothing CTIDBG_new here..

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
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << " " << getName() << "  Failed at state " << getCurrentState() << endl;
                }

                setLogOnNeeded(TRUE);
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

INT CtiDeviceTapPagingTerminal::decodeResponseDisconnect (CtiXfer &xfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList)
{
    INT status = commReturnValue;

    if( status == NORMAL )     // Communications must have been successful
    {
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
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    dout << " " << getName() << "  Failed at state " << getCurrentState() << endl;
                }

                setLogOnNeeded(TRUE);
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
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << " " << getName() << " **** Status **** " << status << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return status;
}


CtiDeviceIED& CtiDeviceTapPagingTerminal::setInitialState (const LONG oldid)
{
    if( oldid > 0 )
    {
        if(getDebugLevel() & DEBUGLEVEL_LUDICROUS)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "  Port has indicated a connected device swap. " << endl;
            dout << "  " << getName() << " has replaced DEVID " << oldid << " as the currently connected device" << endl;
        }
        setCurrentState(StateHandshakeComplete);     // TAP is already connected on this port
        setLogOnNeeded(FALSE);                       // We will skip the logon, and proceed to <STX>
    }
    else if( getLogOnNeeded() )
    {
        setCurrentState(StateHandshakeSendStart);
    }
    else                                                                 // Device is already online and init
    {
        setCurrentState(StateHandshakeComplete);
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
_inStr(RWCString()),
_pageBuffer(NULL),
_outMessage(NULL)
{}

/*
CtiDeviceTapPagingTerminal::CtiDeviceTapPagingTerminal(const CtiDeviceTapPagingTerminal& aRef) :
   _sendFiller(true),
   _idByteCount(20),
   _pageCount(0),
   _pagePrefix('a'),
   _pageLength(0),
   _inStr(RWCString()),
   _pageBuffer(NULL)
{
   *this = aRef;
}
*/
CtiDeviceTapPagingTerminal& CtiDeviceTapPagingTerminal::operator=(const CtiDeviceTapPagingTerminal& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);

        _idByteCount = 10;

        _tap = aRef.getTap();
        _pageCount = aRef.getPageCount();
        _pagePrefix = aRef.getPagePrefix();

        if( aRef.isValidPageBuffer() )
        {
            allocateDataBins(NULL);

            for(int i = 0; i < aRef.getPageLength(); i++ )
            {
                _pageBuffer[i] = aRef.getPageBuffer(i);
            }
            _pageLength = aRef.getPageLength();
        }

        setSendFiller(aRef.getSendFiller());
    }
    return *this;
}

CtiTableDeviceTapPaging    CtiDeviceTapPagingTerminal::getTap() const       { return _tap;}
CtiTableDeviceTapPaging&   CtiDeviceTapPagingTerminal::getTap()             { return _tap;}

CtiDeviceTapPagingTerminal& CtiDeviceTapPagingTerminal::setTap(const CtiTableDeviceTapPaging& aTap)
{
    _tap = aTap;
    return *this;
}

void CtiDeviceTapPagingTerminal::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    Inherited::getSQL(db, keyTable, selector);
    CtiTableDeviceTapPaging::getSQL(db, keyTable, selector);
}

void CtiDeviceTapPagingTerminal::DecodeDatabaseReader(RWDBReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    _tap.DecodeDatabaseReader(rdr);
}



ULONG CtiDeviceTapPagingTerminal::getUniqueIdentifier() const
{
    ULONG CSum = 0;

    /*
     *  This is an undocumented cparm.  It is in here only due to a lack of clarity on the effects of making the change in the else
     *  clause.  Can you say emergency backup?
     */
    if( !gConfigParms.getValueAsString("TCPARM_USE_OLD_TAP_GUID").compareTo("true", RWCString::ignoreCase) )
    {
        RWCString num;

        for(int i = 0; i < getTap().getPagerNumber().length(); i++ )
        {
            CHAR ch = getTap().getPagerNumber().data()[(size_t)i];

            if( isdigit(ch) )
            {
                num.append(ch);
            }
        }

        // Now get a standard CRC
        CSum = (ULONG)CCITT16CRC( 0, (BYTE*)num.data(), num.length(), FALSE);
    }
    else
    {
        if(isDialup())
        {
            CSum = Inherited::getUniqueIdentifier();
        }
        else
        {
            CSum = getPortID();     // Use the port ID as a GUID for all TAPs on this port!
        }
    }

    return CSum;
}

bool CtiDeviceTapPagingTerminal::blockedByPageRate() const
{
    return (_pagesPerMinute > pagesPerMinute);
}

bool CtiDeviceTapPagingTerminal::devicePacingExceeded()
{
    bool toofast = false;

    if(pagesPerMinute > 0)
    {
        RWTime now;
        RWTime newbatch = nextScheduledTimeAlignedOnRate(_pacingTimeStamp, 60);

        if(now >= newbatch)
        {
            _pagesPerMinute = 1;
            _pacingTimeStamp = nextScheduledTimeAlignedOnRate( _pacingTimeStamp, 60 );
            _pacingReport = false;
        }
        else if(_pagesPerMinute >= pagesPerMinute)   // This time is allowed for the paging company to clear buffers.
        {
            if(!_pacingReport)
            {
                _pacingReport = true;
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << getName() << " Configuration PAGES_PER_MINUTE limits paging to " << pagesPerMinute << " pages per minute.  Next page allowed at " << newbatch << endl;
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
    CtiPointAccumulator *pAccumPoint = 0;
    ULONG curPulseValue;

    /* Check if there is a pulse point */
    if((pAccumPoint = (CtiPointAccumulator *)getDevicePointOffsetTypeEqual(PAGECOUNTOFFSET, PulseAccumulatorPointType)) != NULL)
    {
        /* Copy the pulses */
        curPulseValue = pAccumPoint->getPointHistory().getPresentPulseCount() + 1;
        pAccumPoint->getPointHistory().setPreviousPulseCount(pAccumPoint->getPointHistory().getPresentPulseCount());
        pAccumPoint->getPointHistory().setPresentPulseCount(curPulseValue);
    }

    /* Check if there is a pulse point */
    if((pAccumPoint = (CtiPointAccumulator *)getDevicePointOffsetTypeEqual(PAGECHARCOUNTOFFSET, PulseAccumulatorPointType)) != NULL)
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
    CtiPointAccumulator *pAccumPoint = 0;
    FLOAT PValue;
    char tStr[128];

    CtiPointDataMsg *pData = 0;
    CtiReturnMsg *returnMsg = 0;    // Message sent to VanGogh, inherits from Multi

    /* Check if there is a pulse point */
    if((pAccumPoint = (CtiPointAccumulator *)getDevicePointOffsetTypeEqual(PAGECOUNTOFFSET, PulseAccumulatorPointType)) != NULL)
    {
        PValue = (FLOAT) pAccumPoint->getPointHistory().getPresentPulseCount() * pAccumPoint->getMultiplier();
        PValue += pAccumPoint->getDataOffset();

        _snprintf(tStr, 126, "%s point %s = %f", getName(), pAccumPoint->getName(), PValue);

        pData = CTIDBG_new CtiPointDataMsg(pAccumPoint->getPointID(), PValue, NormalQuality, PulseAccumulatorPointType, tStr);

        if(pData != NULL)
        {
            if(!returnMsg) returnMsg = (CtiReturnMsg*) CTIDBG_new CtiReturnMsg(getID(), getName() + " rsvpToDispatch");

            returnMsg->PointData().insert(pData);
            pData = NULL;  // We just put it on the list...
        }
    }

    /* Check if there is a pulse point */
    if((pAccumPoint = (CtiPointAccumulator *)getDevicePointOffsetTypeEqual(PAGECHARCOUNTOFFSET, PulseAccumulatorPointType)) != NULL)
    {
        PValue = (FLOAT) pAccumPoint->getPointHistory().getPresentPulseCount() * pAccumPoint->getMultiplier();
        /* Apply offset */
        PValue += pAccumPoint->getDataOffset();

        _snprintf(tStr, 126, "%s point %s = %f", getName(), pAccumPoint->getName(), PValue);

        pData = CTIDBG_new CtiPointDataMsg(pAccumPoint->getPointID(), PValue, NormalQuality, PulseAccumulatorPointType, tStr);

        if(pData != NULL)
        {
            if(!returnMsg) returnMsg = (CtiReturnMsg*) CTIDBG_new CtiReturnMsg(getID(), getName() + " rsvpToDispatch");
            returnMsg->PointData().insert(pData);
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

