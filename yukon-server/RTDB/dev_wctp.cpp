/*-----------------------------------------------------------------------------*
*
* File:   dev_wctp
*
* Date:   5/29/2002
*
* Author: Zhihong Yao
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <windows.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <rw\rwtime.h>

#include <xercesc/util/PlatformUtils.hpp>
#include <xercesc/sax2/XMLReaderFactory.hpp>
#include <xercesc/sax/SAXParseException.hpp>
#include <xercesc/sax/SAXException.hpp>
#include <xercesc/framework/MemBufInputSource.hpp>

#include "cparms.h"
#include "dsm2.h"
#include "logger.h"
#include "porter.h"

#include "pt_base.h"
#include "pt_accum.h"

#include "pointtypes.h"
#include "connection.h"
#include "mgr_route.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "msg_trace.h"
#include "cmdparse.h"
#include "dev_wctp.h"

static int pagesPerMinute  = gConfigParms.getValueAsInt("PAGES_PER_MINUTE", 0);


/*
 * XML characters encoding
 */
UINT numSpecialChars = 6;
CHAR *toBeReplaced[] = {"&", "\033", "\"", "'", "<", ">"};
CHAR *replaceWith[]  = {"&amp;", "&lt;esc&gt;", "&quot;", "&apos;", "&lt;", "&gt;"};


CtiDeviceWctpTerminal::~CtiDeviceWctpTerminal()
{
    freeDataBins();
    destroyBuffers();
    if(parser != NULL)
    {
        delete parser;
    }
    if(handler != NULL)
    {
        delete handler;
    }
}


void CtiDeviceWctpTerminal::updatePageCountData(UINT addition)
{
    CtiPointAccumulator *pAccumPoint = 0;
    ULONG curPulseValue;

    /* Check if there is a pulse point */
    if((pAccumPoint = (CtiPointAccumulator *)getDevicePointOffsetTypeEqual(PAGECOUNTOFFSET, PulseAccumulatorPointType)) != NULL)
    {
        /* Copy the pulses */
        curPulseValue = pAccumPoint->getPointHistory().getPresentPulseCount() + 1;  // One page per call!
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

bool CtiDeviceWctpTerminal::allowPrefix() const
{
    return _allowPrefix;
}

CtiDeviceWctpTerminal& CtiDeviceWctpTerminal::setAllowPrefix(bool val)
{
    _allowPrefix = val;
    return *this;
}

bool CtiDeviceWctpTerminal::devicePacingExceeded()
{
    bool toofast = false;

    if(pagesPerMinute > 0)
    {
        RWTime now;
        RWTime newbatch = nextScheduledTimeAlignedOnRate( _pacingTimeStamp, 60 );

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

CtiDeviceWctpTerminal& CtiDeviceWctpTerminal::setSendFiller(bool yesno)
{
    _sendFiller = yesno;
    return *this;
}

bool CtiDeviceWctpTerminal::getSendFiller() const
{
    return _sendFiller;
}

INT CtiDeviceWctpTerminal::ExecuteRequest(CtiRequestMsg                  *pReq,
                                          CtiCommandParser               &parse,
                                          OUTMESS                        *&OutMessage,
                                          RWTPtrSlist< CtiMessage >      &vgList,
                                          RWTPtrSlist< CtiMessage >      &retList,
                                          RWTPtrSlist< OUTMESS >         &outList)
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

INT CtiDeviceWctpTerminal::allocateDataBins(OUTMESS *oMess)
{
    if(_pageBuffer == NULL)
    {
        _pageBuffer = (CHAR*) CTIDBG_new CHAR[256];
        _pageBuffer[0] = 0;

        if(_pageBuffer != NULL && oMess != NULL)
        {
            // Use the TAP message structure for now
            setPageBuffer(oMess->Buffer.TAPSt.Message, oMess->Buffer.TAPSt.Length);
        }
    }

    return NORMAL;
}

INT CtiDeviceWctpTerminal::freeDataBins()
{
    if(_pageBuffer != NULL)
    {
        delete [] _pageBuffer;
        _pageBuffer = NULL;
        _pageLength = 0;
    }

    return NORMAL;
}

CHAR  CtiDeviceWctpTerminal::getPagePrefix() const
{
    return _pagePrefix;
}
CHAR& CtiDeviceWctpTerminal::getPagePrefix()
{
    return _pagePrefix;
}
CtiDeviceWctpTerminal& CtiDeviceWctpTerminal::setPagePrefix(const CHAR aCh)
{
    _pagePrefix = aCh;
    return *this;
}

UINT  CtiDeviceWctpTerminal::getPageCount() const
{
    return _pageCount;
}
UINT& CtiDeviceWctpTerminal::getPageCount()
{
    return _pageCount;
}
CtiDeviceWctpTerminal& CtiDeviceWctpTerminal::setPageCount(const UINT aInt)
{
    _pageCount = aInt;
    return *this;
}

UINT CtiDeviceWctpTerminal::getPageLength() const
{
    return _pageLength;
}

CtiDeviceWctpTerminal& CtiDeviceWctpTerminal::setPageLength(const UINT aInt)
{
    _pageLength = aInt;
    return *this;
}

BOOL CtiDeviceWctpTerminal::isValidPageBuffer() const
{
    return( _pageBuffer != NULL );
}

CHAR* CtiDeviceWctpTerminal::getPageBuffer()
{
    return _pageBuffer;
}

CHAR  CtiDeviceWctpTerminal::getPageBuffer(const INT i) const
{
    CHAR ch = '\0';

    if(_pageBuffer != NULL)
    {
        ch = _pageBuffer[i];
    }

    return ch;
}

CtiDeviceWctpTerminal& CtiDeviceWctpTerminal::setPageBuffer(const CHAR* copyBuffer, const INT len)
{
    if(_pageBuffer != NULL && len < 256)
    {
        setPageLength(len);
        memcpy(_pageBuffer, copyBuffer, len);
        _pageBuffer[len] = 0;
    }

    return *this;
}


RWCString CtiDeviceWctpTerminal::getDescription(const CtiCommandParser & parse) const
{
    RWCString trelay;

    trelay = "PAGE: " + getName();

    return trelay;
}


CHAR* CtiDeviceWctpTerminal::getOutBuffer()
{
    LockGuard gd(monitor());

    if(_outBuffer == NULL)
    {
        _outBuffer = CTIDBG_new CHAR[1024];
        _outBuffer[0] = 0;
    }

    return _outBuffer;
}

CHAR* CtiDeviceWctpTerminal::getInBuffer()
{
    LockGuard gd(monitor());

    if(_inBuffer == NULL)
    {
        _inBuffer = CTIDBG_new CHAR[1024];
        _inBuffer[0] = 0;
    }

    return _inBuffer;
}

CHAR* CtiDeviceWctpTerminal::getXMLBuffer()
{
    LockGuard gd(monitor());

    if(_xmlBuffer == NULL)
    {
        _xmlBuffer = CTIDBG_new CHAR[1024];
        _xmlBuffer[0] = 0;
    }

    return _xmlBuffer;
}

void CtiDeviceWctpTerminal::destroyBuffers()
{
    LockGuard gd(monitor());

    try
    {
        if(_outBuffer != NULL)
        {
            delete [] _outBuffer;
            _outBuffer = NULL;
        }

        if(_inBuffer != NULL)
        {
            delete [] _inBuffer;
            _inBuffer = NULL;
        }
        if(_xmlBuffer != NULL)
        {
            delete [] _xmlBuffer;
            _xmlBuffer = NULL;
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

}


SAX2XMLReader* CtiDeviceWctpTerminal::getSAXParser()
{
    if(parser == NULL)
    {
        parser = XMLReaderFactory::createXMLReader();
    }

    return parser;
}

SAXWctpHandler* CtiDeviceWctpTerminal::getWctpHandler()
{
    if(handler == NULL)
    {
        handler = CTIDBG_new SAXWctpHandler;
    }

    return handler;
}


CHAR* CtiDeviceWctpTerminal::removeDocType(const CHAR *src, CHAR *dst)
{
    CHAR *p1 = strstr(src, "<!DOCTYPE");
    if(p1 == NULL)
    {
        strcpy(dst, src);
        return dst;
    }

    strncpy(dst, src, p1 - src);
    dst[p1 - src] = 0;

    CHAR *p2 = strstr(src, "<wctp-Operation");
    if(p2 != NULL)
        strcat(dst, p2);
    return dst;
}


CHAR* CtiDeviceWctpTerminal::trimMessage(CHAR *message)
{
    CHAR buf[256];
    CHAR *ptr, *ptr1, *lastPtr;

    strcpy(buf, message);
    ptr = buf;              // Pointer to buf
    ptr1 = message;         // Pointer to message
    lastPtr = NULL;         // Pointer to the start of a series of consecutive blank chars

    while(*ptr != 0)
    {
        if(*ptr == ' ' || *ptr == '\t' || *ptr == '\r' || *ptr == '\n')
        {
            // ptr points to a blank char
            if(lastPtr == NULL)
            {
                lastPtr = ptr;
            }
        }
        else
        {
            if(lastPtr != NULL)
            {
                // There are a series of blank chars before this, shrink them to one space char
                *ptr1++ = ' ';
                lastPtr = NULL;
            }
            *ptr1++ = *ptr;
        }
        ptr++;
    }

    *ptr1 = 0;
    return message;
}


INT CtiDeviceWctpTerminal::traceOut (PCHAR Message, ULONG Count, RWTPtrSlist< CtiMessage > &traceList)
{
    ULONG i;
    RWCString outStr;

    for(i = 0; i < Count; i++)
    {
        outStr.append(Message[i]);
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
    trace.setEnd(true);
    traceList.insert( trace.replicateMessage() );

    trace.setBrightGreen();
    trace.setTrace( RWCString("\"") + outStr + RWCString("\"\n\n") );
    traceList.insert( trace.replicateMessage() );

    return(NORMAL);
}

INT CtiDeviceWctpTerminal::traceIn(PCHAR  Message, ULONG  Count, RWTPtrSlist< CtiMessage > &traceList, BOOL CompletedMessage)
{
    ULONG i;

    if(Count && Message != NULL)
    {
        for(i = 0; i < Count; i++)
        {
            _inStr.append( Message[i] );
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
        trace.setEnd(true);
        traceList.insert( trace.replicateMessage() );

        trace.setBrightMagenta();
        trace.setTrace( RWCString("\"") + _inStr + RWCString("\"\n\n") );
        traceList.insert( trace.replicateMessage() );

        _inStr = RWCString();     // Reset it for the next message
    }

    return(NORMAL);
}

CHAR* CtiDeviceWctpTerminal::replaceChars(const CHAR *src, CHAR *dst)
{
    CHAR buf[256];
    CHAR *lastPos, *pos;
    int i;

    strcpy(dst, src);
    for(i = 0; i < numSpecialChars; i++)
    {
        buf[0] = 0;
        lastPos = dst;
        while((pos = strstr(lastPos, toBeReplaced[i])) != NULL)
        {
            strncat(buf, lastPos, pos - lastPos);
            strcat(buf, replaceWith[i]);
            lastPos = pos + 1;
        }

        if(lastPos != dst)
        {
            if(*lastPos != 0)
            {
                strcat(buf, lastPos);
            }
            strcpy(dst, buf);
        }
    }

    return dst;
}

CHAR* CtiDeviceWctpTerminal::buildXMLMessage(const CHAR *recipientId,
                                             const CHAR *senderId,
                                             const CHAR *msgPayload,
                                             const CHAR *timeStamp)
{
    CHAR* xmlMsg = getXMLBuffer();
    xmlMsg[0] = 0;
    strcat(xmlMsg, "<?xml version=\"1.0\"?>");
    strcat(xmlMsg, "<!DOCTYPE wctp-Operation SYSTEM \"");
    strcat(xmlMsg, WCTP_DOCTYPE);
    strcat(xmlMsg, "\">");
    strcat(xmlMsg, "<wctp-Operation wctpVersion=\"");
    strcat(xmlMsg, WCTP_VERSION);
    strcat(xmlMsg, "\">");
    strcat(xmlMsg, "<wctp-SubmitClientMessage>");
    strcat(xmlMsg, "<wctp-SubmitClientHeader submitTimestamp=\"");
    strcat(xmlMsg, timeStamp);
    strcat(xmlMsg, "\">");
    strcat(xmlMsg, "<wctp-ClientOriginator senderID=\"");
    strcat(xmlMsg, senderId);
    strcat(xmlMsg, "\"/>");
    strcat(xmlMsg, "<wctp-ClientMessageControl");
    strcat(xmlMsg, " allowResponse=\"false\"");
    strcat(xmlMsg, " preformatted=\"true\"");
    strcat(xmlMsg, "/>");
    strcat(xmlMsg, "<wctp-Recipient recipientID=\"");
    strcat(xmlMsg, recipientId);
    strcat(xmlMsg, "\"/>");
    strcat(xmlMsg, "</wctp-SubmitClientHeader>");
    strcat(xmlMsg, "<wctp-Payload>");
    strcat(xmlMsg, "<wctp-Alphanumeric>");
    strcat(xmlMsg, msgPayload);
    strcat(xmlMsg, "</wctp-Alphanumeric>");
    strcat(xmlMsg, "</wctp-Payload>");
    strcat(xmlMsg, "</wctp-SubmitClientMessage>");
    strcat(xmlMsg, "</wctp-Operation>");

    return xmlMsg;
}


INT CtiDeviceWctpTerminal::readLine(CHAR *str, CHAR *buf, INT bufLen)
{
    CHAR *ptr;
    INT len;

    try
    {
        if(str != NULL)
        {
            readLinePtr = str;
        }
        else if(readLinePtr == NULL || *readLinePtr == 0)
        {
            return -1;
        }

        ptr = readLinePtr;
        while(*ptr != '\r' && *ptr != '\n' && *ptr != 0)
        {
            ptr++;
        }

        if(*ptr == 0)
        {
            return -1;
        }

        len = ptr - readLinePtr;
        if(bufLen < len)
        {
            len = bufLen;
            ptr = readLinePtr + len;
        }

        strncpy(buf, readLinePtr, len);
        buf[len] = 0;

        if(*ptr == '\r')
        {
            readLinePtr = ptr + 1;
            if(*readLinePtr == '\n')
            {
                // If a line ends with "\r\n", move readLinePtr to the right of the '\n'
                readLinePtr++;
            }
        }
        else
        {
            readLinePtr = ptr + 1;
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        autopsy( __FILE__, __LINE__ );
    }


    return len;
}

INT CtiDeviceWctpTerminal::generateCommand(CtiXfer  &xfer, RWTPtrSlist< CtiMessage > &traceList)
{
    INT   i;
    INT   status = NORMAL;

    switch( getCurrentState() )
    {
    case StateHandshakeComplete:
        {
            setCurrentState(StateScanValueSet1);

            /* !!!! FALL THROUGH !!!! */
        }
    case StateScanValueSet1:
        {
            xfer.setOutBuffer( (UCHAR*)getOutBuffer() );     // Don't use any buffer which may have been supplied
            xfer.setInBuffer( (UCHAR*)getInBuffer() );       // Don't use any buffer which may have been supplied

            RWTime nowTime;
            RWDate nowDate;
            CHAR   timeStamp[20];

            _snprintf(timeStamp, 20, "%04d-%02d-%02dT%02d:%02d:%02d",
                      nowDate.year(), nowDate.month(), nowDate.dayOfMonth(),
                      nowTime.hour(), nowTime.minute(), nowTime.second());

            INT   sendCnt = 0;
            CHAR  msgPayload[256];
            CHAR  temp[8];

            if( gDoPrefix && allowPrefix() )
            {
                /* Stick a little TAPTerm fakey in there */
                msgPayload[sendCnt++] = incrementPagePrefix();

                sprintf(temp, "%05d", (getPageCount()++ & 0x0000ffff));

                for(i = 0; i < 5; i++)
                {
                    msgPayload[sendCnt++] = temp[i] & 0x7f;
                }
            }

            updatePageCountData(sendCnt + strlen(getPageBuffer()));

            replaceChars(getPageBuffer(), msgPayload + sendCnt);

            CHAR* xmlMsg = buildXMLMessage((const char*)getWctp().getPagerNumber(),
                                           "yukonserver@cannontech.com",
                                           msgPayload,
                                           timeStamp);

            CHAR* out = getOutBuffer();
            out[0] = 0;
            strcat(out, "POST ");
            strcat(out, getPassword());                 // The path information is stored in password for now
            strcat(out, " HTTP/1.0\r\n");
            strcat(out, "Content-Type: text/xml\r\n");
            strcat(out, "Content-Length: ");
            strcat(out, _itoa(strlen(xmlMsg), temp, 10));
            strcat(out, "\r\n\r\n");
            strcat(out, xmlMsg);

            xfer.setOutCount( strlen(out) );

            if(xfer.doTrace(DEBUGLEVEL_WCTP_PROTOCOL))
            {
                traceOut( xmlMsg, strlen(xmlMsg), traceList);
            }

            if(xfer.doTrace(0))
            {
                traceOut( msgPayload, strlen(msgPayload), traceList);
            }

            xfer.setNonBlockingReads(true);         // Read as many bytes as possible
            xfer.setInCountExpected(1024);
            xfer.setInTimeout(1);
            timeEllapsed++;

            setCurrentState( StateScanDecode1 );
            break;
        }
    case StateScanValueSet2:
        {
            if(timeEllapsed < WCTP_TIMEOUT)
            {
                xfer.setOutCount(0);                    // Nothing new here
                xfer.setNonBlockingReads(true);
                xfer.setInCountExpected(1024);
                xfer.setInTimeout(1);
                timeEllapsed++;
            }
            else
            {
                xfer.setOutCount(0);
                xfer.setInTimeout(0);
            }

            setCurrentState( StateScanDecode2 );
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

INT CtiDeviceWctpTerminal::decodeResponse(CtiXfer  &xfer, INT commReturnValue, RWTPtrSlist< CtiMessage > &traceList)
{
    INT status = commReturnValue;

    INT inCnt = 0, msgLen = 0;
    CHAR *in, *out, buf[256];

    SAX2XMLReader  *parser;
    SAXWctpHandler *handler;

    try
    {
        if( status == NORMAL )     // Communications must have been successful
        {
            switch( getCurrentState() )
            {
            case StateScanDecode1:
                {
                    /*
                     *  We just sent out our page, we are going to parse the HTTP response
                     */

                    // Clear the out buffer, we're going to store the input data in this buffer
                    // in case that it's not received in one time
                    in = (CHAR*)xfer.getInBuffer();
                    out = (CHAR*)xfer.getOutBuffer();
                    out[0] = 0;

                    statusParsed = FALSE;
                    headerParsed = FALSE;

                    setCurrentState( StateScanDecode2 );

                    /* FALL THROUGH! */
                }
            case StateScanDecode2:
                {
                    if( (inCnt = xfer.getInCountActual()) > 0)
                    {
                        // Append the out buffer with the input data
                        strncat(out, in, inCnt);

                        if(!statusParsed)
                        {
                            /*
                             * Parse the first line (status line) of the HTTP response
                             */
                            if(readLine(out, buf, 255) < 12)
                            {
                                // Not enough message to parse yet
                                setCurrentState( StateScanValueSet2 );
                                break;
                            }

                            /*
                             * A successful Http response should take the form "HTTP/X.X 200 ..."
                             */
                            if(_strnicmp(buf, "HTTP", 4) != 0)
                            {
                                setCurrentState( StateScanAbort );
                                return ErrorHttpResponse;
                            }

                            INT statusCode = 0;
                            sscanf(buf+9, "%d", &statusCode);

                            if(statusCode != 200)
                            {
                                setCurrentState( StateScanAbort );
                                return ErrorHttpResponse;
                            }

                            /* Successful HTTP response */
                            statusParsed = TRUE;
                        }

                        if(!headerParsed)
                        {
                            /*
                             * Parse HTTP response headers, looking for "Content-Length"
                             */
                            BOOL foundWctpResp = FALSE;
                            while(readLine(NULL, buf, 255) != -1)
                            {
                                if(_strnicmp(buf, "Content-Length:", 15) == 0)
                                {
                                    sscanf(buf+15, "%d", &msgLen);
                                }
                                if(strlen(buf) == 0)
                                {
                                    foundWctpResp = TRUE;
                                    break;
                                }
                            }

                            if(!foundWctpResp)
                            {
                                // Not receive all HTTP headers yet, go back and get more bytes
                                setCurrentState( StateScanValueSet2 );
                                break;
                            }

                            /* Found WCTP response */
                            headerParsed = TRUE;

                            try
                            {
                                // Initialize the XML4C2 system
                                XMLPlatformUtils::Initialize();

                                // Prepare the WTCP message parser and handler
                                handler = getWctpHandler();
                                parser = getSAXParser();
                                parser->setContentHandler(handler);
                                parser->setErrorHandler(handler);
                            }
                            catch(const XMLException& toCatch)
                            {
                                setCurrentState( StateScanAbort );
                                return ErrorXMLParser;
                            }
                        }

                        /*
                         * Parse the WCTP response message
                         */
                        CHAR *wctpMsg = getReadLinePtr();           // Point to the start of WCTP response message
                        if(msgLen == 0)
                        {
                            msgLen = strlen(wctpMsg);
                        }

                        if(strlen(wctpMsg) < msgLen)
                        {
                            // Not received all the WCTP response message yet
                            setCurrentState( StateScanValueSet2 );
                            break;
                        }

                        // Remove the "!DOCTYPE" declaration from the WCTP response
                        // and store the modified message in xml buffer
                        /*
                         * !!!NOTICE: If we keep the "!DOCTYPE", the parser will throw an exception
                         * for not being able to access the DTD file via internet
                         */
                        CHAR* xmlMsg = getXMLBuffer();
                        try
                        {
                            removeDocType(wctpMsg, xmlMsg);

                            if(strlen(xmlMsg) > 0)
                            {
                                MemBufInputSource is( (XMLByte*)xmlMsg, strlen(xmlMsg), "WCTP response message" );
                                is.setCopyBufToStream( true );
                                handler->resetHandler();
                                parser->parse(is);
                            }
                        }
                        catch(const SAXParseException& e)
                        {
                            /*
                             * There are two possibilities that we get a SAXParseException:
                             * the WCTP response message is not well-formatted, or we haven't
                             * received all the bytes yet.
                             * In the later case, we need to go back and get more bytes
                             */
                            if(handler->hasError())
                            {
                                setCurrentState( StateScanAbort );
                                return ErrorWctpResponse;
                            }
                            else
                            {
                                setCurrentState( StateScanValueSet2 );
                                break;
                            }
                        }
                        catch(...)
                        {
                            // Unexpected exception during parsing WCTP response message
                            setCurrentState( StateScanAbort );
                            return ErrorWctpResponse;
                        }

                        /* The WCTP response message has been received completely, and parsed successfully */

                        if(xfer.doTrace(commReturnValue))
                        {
                            if(xfer.doTrace(DEBUGLEVEL_WCTP_PROTOCOL))
                            {
                                traceIn(xmlMsg, strlen(xmlMsg), traceList, TRUE);
                            }

                            _snprintf(buf, 255, "WCTP response: %d %s\n%s",
                                      handler->getResponseCode(),
                                      handler->getResponseText(),
                                      trimMessage( handler ->getResponseMessage() ));
                            traceIn(buf, strlen(buf), traceList, TRUE);


                            if(299 < handler->getResponseCode() && handler->getResponseCode() < 400)
                            {
                                status = ErrorWctp300Series;
                            }
                            else if(399 < handler->getResponseCode() && handler->getResponseCode() < 500)
                            {
                                status = ErrorWctp400Series;
                            }
                            else if(499 < handler->getResponseCode() && handler->getResponseCode() < 600)
                            {
                                status = ErrorWctp500Series;
                            }
                            else if(599 < handler->getResponseCode() && handler->getResponseCode() < 700)
                            {
                                status = ErrorWctp600Series;
                            }
                        }

                        // And call the termination method
                        XMLPlatformUtils::Terminate();

                        setCurrentState( StateScanComplete );
                        break;
                    }
                    else
                    {
                        if(timeEllapsed < WCTP_TIMEOUT)
                        {
                            setCurrentState( StateScanValueSet2 );
                            break;
                        }

                        // WCTP response message timeout
                        if(!statusParsed)
                        {
                            status = ErrorPageNoResponse;
                        }
                        else if(!headerParsed)
                        {
                            status = ErrorHttpResponse;
                        }
                        else
                        {
                            status = ErrorWctpTimeout;
                        }

                        setCurrentState( StateScanAbort );
                        return status;
                    }
                }
            default:
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "  Failed at state " << getCurrentState() << endl;
                    }

                    setCurrentState(StateScanAbort);
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
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }


        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << " WCTP getCurrentState " << getCurrentState() << endl;
        }
    }


    return status;
}


CtiDeviceIED& CtiDeviceWctpTerminal::setInitialState (const LONG oldid)
{
    if( oldid > 0 )
    {
        if(getDebugLevel() & 0x00000001)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        // We'll go into this block most likely
        setCurrentState(StateHandshakeComplete);
    }

    return *this;
}

CHAR CtiDeviceWctpTerminal::incrementPagePrefix()
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

CtiDeviceWctpTerminal::CtiDeviceWctpTerminal() :
_allowPrefix(true),
_pagesPerMinute(0),
_sendFiller(true),
_pageCount(0),
_pagePrefix('a'),
_pageLength(0),
_inStr(RWCString()),
_pageBuffer(NULL),
_outBuffer(NULL),
_inBuffer(NULL),
_xmlBuffer(NULL),
readLinePtr(NULL),
parser(NULL),
handler(NULL),
statusParsed(FALSE),
headerParsed(FALSE),
timeEllapsed(0)
{}

/*
CtiDeviceWctpTerminal::CtiDeviceWctpTerminal(const CtiDeviceWctpTerminal& aRef) :
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
CtiDeviceWctpTerminal& CtiDeviceWctpTerminal::operator=(const CtiDeviceWctpTerminal& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);

        _wctp = aRef.getWctp();
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

        _inStr = RWCString();
        _outBuffer = NULL;
        _inBuffer = NULL;
        _xmlBuffer = NULL;
        readLinePtr = NULL;
        parser = NULL;
        handler = NULL;
        statusParsed = FALSE;
        headerParsed = FALSE;
        timeEllapsed = 0;
    }
    return *this;
}

CtiTableDeviceTapPaging    CtiDeviceWctpTerminal::getWctp() const       { return _wctp;}
CtiTableDeviceTapPaging&   CtiDeviceWctpTerminal::getWctp()             { return _wctp;}

CtiDeviceWctpTerminal& CtiDeviceWctpTerminal::setWctp(const CtiTableDeviceTapPaging& aWctp)
{
    _wctp = aWctp;
    return *this;
}

void CtiDeviceWctpTerminal::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    Inherited::getSQL(db, keyTable, selector);
    CtiTableDeviceTapPaging::getSQL(db, keyTable, selector);
}

void CtiDeviceWctpTerminal::DecodeDatabaseReader(RWDBReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

    if( getDebugLevel() & 0x0800 )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    _wctp.DecodeDatabaseReader(rdr);
}



// ---------------------------------------------------------------------------
//  SAXWctpHandler: Constructors and Destructor
// ---------------------------------------------------------------------------
SAXWctpHandler::SAXWctpHandler() :

respCode(0)
, succ(false)
, err(false)
, isRoot(true)
, inOperation(false)
, inSubmitClientResponse(false)
, inClientSuccess(false)
, inFailure(false)
, inConfirmation(false)
, inSuccess(false)
{
    respText = CTIDBG_new char[256];
    respText[0] = 0;
    respMessage = CTIDBG_new char[256];
    respMessage[0] = 0;
}

SAXWctpHandler::~SAXWctpHandler()
{
    if(respMessage != NULL)
    {
        delete [] respMessage;
    }
    if(respText != NULL)
    {
        delete [] respText;
    }
}


void SAXWctpHandler::resetHandler()
{
    respCode = 0;

    if(respText == NULL)
    {
        respText = CTIDBG_new char[256];
    }
    respText[0] = 0;

    if(respMessage == NULL)
    {
        respMessage = CTIDBG_new char[256];
    }
    respMessage[0] = 0;
    succ = false;
    err = false;

    isRoot = true;
    inOperation = false;
    inSubmitClientResponse = false;
    inClientSuccess = false;
    inFailure = false;
    inConfirmation = false;
    inSuccess = false;
}

// ---------------------------------------------------------------------------
//  SAXWctpHandler: Implementation of the SAX DocumentHandler interface
// ---------------------------------------------------------------------------
void SAXWctpHandler::startElement(const XMLCh *const uri,
                                  const XMLCh *const localname,
                                  const XMLCh *const qname,
                                  const Attributes &attrs)
{
    char elemName[256];

    XMLString::transcode(qname, elemName, 255);

    if(strcmp(elemName, "wctp-Operation") == 0)
    {
        if(!isRoot)
        {
            err = true;
            throw CTIDBG_new SAXParseException(
                                              XMLString::transcode("wctp-Operation must be the root element"),
                                              NULL,
                                              XMLString::transcode(elemName),
                                              0,
                                              0);
        }
        isRoot = false;
        inOperation = true;
    }
    else if(strcmp(elemName, "wctp-SubmitClientResponse") == 0)
    {
        if(!inOperation)
        {
            err = true;
            throw CTIDBG_new SAXParseException(
                                              XMLString::transcode("unexpected location of start of element"),
                                              NULL,
                                              XMLString::transcode(elemName),
                                              0,
                                              0);
        }
        inSubmitClientResponse = true;
    }
    else if(strcmp(elemName, "wctp-ClientSuccess") == 0)
    {
        if(!inSubmitClientResponse)
        {
            err = true;
            throw CTIDBG_new SAXParseException(
                                              XMLString::transcode("unexpected location of start of element"),
                                              NULL,
                                              XMLString::transcode(elemName),
                                              0,
                                              0);
        }
        inClientSuccess = true;

        // This is the ONLY case that we get a success confirmation
        succ = true;
        XMLCh *pXMLChTemp = XMLString::transcode("successCode");
        char *respCodeStr = XMLString::transcode( attrs.getValue( pXMLChTemp ));
        delete [] pXMLChTemp;

        respCode = atoi(respCodeStr);
        delete [] respCodeStr;
        if(respCode == 0)
        {        // Conversion failed
            err = true;
            throw CTIDBG_new SAXParseException(
                                              XMLString::transcode("unable to convert attribute successCode's value to integer"),
                                              NULL,
                                              XMLString::transcode(elemName),
                                              0,
                                              0);
        }

        pXMLChTemp = XMLString::transcode("successText");
        XMLString::transcode( attrs.getValue( pXMLChTemp ), respText, 255);
        delete [] pXMLChTemp;
    }
    else if(strcmp(elemName, "wctp-Failure") == 0)
    {
        if(!inSubmitClientResponse && !inConfirmation)
        {
            err = true;
            throw CTIDBG_new SAXParseException(
                                              XMLString::transcode("unexpected location of start of element"),
                                              NULL,
                                              XMLString::transcode(elemName),
                                              0,
                                              0);
        }
        inFailure = true;

        char *respCodeStr = XMLString::transcode( attrs.getValue( XMLString::transcode("errorCode") ));
        respCode = atoi(respCodeStr);
        delete [] respCodeStr;
        if(respCode == 0)
        {        // Conversion failed
            err = true;
            throw CTIDBG_new SAXParseException(
                                              XMLString::transcode("unable to convert attribute errorCode's value to integer"),
                                              NULL,
                                              XMLString::transcode(elemName),
                                              0,
                                              0);
        }
        XMLString::transcode( attrs.getValue(XMLString::transcode("errorText")), respText, 255);
    }
    else if(strcmp(elemName, "wctp-Confirmation") == 0)
    {
        // If we get a wctp-Confirmation instead of a wctp-submitClientResponse, it means something's gone wrong
        if(!inOperation)
        {
            err = true;
            throw CTIDBG_new SAXParseException(
                                              XMLString::transcode("unexpected location of start of element"),
                                              NULL,
                                              XMLString::transcode(elemName),
                                              0,
                                              0);
        }
        inConfirmation = true;
    }
    else if(strcmp(elemName, "wctp-Success") == 0)
    {
        if(!inConfirmation)
        {
            err = true;
            throw CTIDBG_new SAXParseException(
                                              XMLString::transcode("unexpected location of start of element"),
                                              NULL,
                                              XMLString::transcode(elemName),
                                              0,
                                              0);
        }
        inSuccess = true;

        XMLCh *pXMLChTemp = XMLString::transcode("successCode");
        char *respCodeStr = XMLString::transcode( attrs.getValue( pXMLChTemp ));
        delete [] pXMLChTemp;
        respCode = atoi(respCodeStr);
        delete [] respCodeStr;
        if(respCode == 0)
        {        // Conversion failed
            err = true;
            throw CTIDBG_new SAXParseException(
                                              XMLString::transcode("unable to convert attribute successCode's value to integer"),
                                              NULL,
                                              XMLString::transcode(elemName),
                                              0,
                                              0);
        }

        pXMLChTemp = XMLString::transcode("successText");
        XMLString::transcode(attrs.getValue(pXMLChTemp), respText, 255);
        delete [] pXMLChTemp;
    }
}

void SAXWctpHandler::endElement(const XMLCh* const uri, const XMLCh* const localname, const XMLCh* const qname)
{
    char elemName[256];

    XMLString::transcode(qname, elemName, 255);

    if(strcmp(elemName, "wctp-Operation") == 0)
    {
        if(!inOperation)
        {
            err = true;
            throw CTIDBG_new SAXParseException(
                                              XMLString::transcode("End of element has no matching start of element"),
                                              NULL,
                                              XMLString::transcode(elemName),
                                              0,
                                              0);
        }
        inOperation = false;
    }
    else if(strcmp(elemName, "wctp-SubmitClientResponse") == 0)
    {
        if(!inSubmitClientResponse)
        {
            err = true;
            throw CTIDBG_new SAXParseException(
                                              XMLString::transcode("End of element has no matching start of element"),
                                              NULL,
                                              XMLString::transcode(elemName),
                                              0,
                                              0);
        }
        inSubmitClientResponse = false;
    }
    else if(strcmp(elemName, "wctp-ClientSuccess") == 0)
    {
        if(!inClientSuccess)
        {
            err = true;
            throw CTIDBG_new SAXParseException(
                                              XMLString::transcode("End of element has no matching start of element"),
                                              NULL,
                                              XMLString::transcode(elemName),
                                              0,
                                              0);
        }
        inClientSuccess = false;
    }
    else if(strcmp(elemName, "wctp-Failure") == 0)
    {
        if(!inFailure)
        {
            err = true;
            throw CTIDBG_new SAXParseException(
                                              XMLString::transcode("End of element has no matching start of element"),
                                              NULL,
                                              XMLString::transcode(elemName),
                                              0,
                                              0);
        }
        inFailure = false;
    }
    else if(strcmp(elemName, "wctp-Confirmation") == 0)
    {
        if(!inConfirmation)
        {
            err = true;
            throw CTIDBG_new SAXParseException(
                                              XMLString::transcode("End of element has no matching start of element"),
                                              NULL,
                                              XMLString::transcode(elemName),
                                              0,
                                              0);
        }
        inConfirmation = false;
    }
    else if(strcmp(elemName, "wctp-Success") == 0)
    {
        if(!inSuccess)
        {
            err = true;
            throw CTIDBG_new SAXParseException(
                                              XMLString::transcode("End of element has no matching start of element"),
                                              NULL,
                                              XMLString::transcode(elemName),
                                              0,
                                              0);
        }
        inSuccess = false;
    }
}

void SAXWctpHandler::characters(const XMLCh* const chars, const unsigned int length)
{
    char pChars[256];

    XMLString::transcode(chars, pChars, 255);

    char buf[256];
    strncpy(buf, pChars, length);
    buf[length] = 0;

    if(inClientSuccess || inFailure || inSuccess)
        strcat(respMessage, buf);
}

void  SAXWctpHandler::error (const SAXParseException &exception)
{
    throw exception;
}

void  SAXWctpHandler::fatalError (const SAXParseException &exception)
{
    throw exception;
}

void  SAXWctpHandler::warning (const SAXParseException &exception)
{
    throw exception;
}


CtiMessage* CtiDeviceWctpTerminal::rsvpToDispatch(bool clearMessage)
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

