#include "precompiled.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <xercesc/util/PlatformUtils.hpp>
#include <xercesc/sax2/XMLReaderFactory.hpp>
#include <xercesc/sax/SAXParseException.hpp>
#include <xercesc/sax/SAXException.hpp>
#include <xercesc/framework/MemBufInputSource.hpp>

#include "cparms.h"
#include "dsm2.h"
#include "logger.h"
#include "numstr.h"
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
#include "verification_objects.h"
#include "ctidate.h"
#include "ctitime.h"

using std::string;
using std::endl;
using std::list;
using std::queue;
using namespace boost::posix_time;

XERCES_CPP_NAMESPACE_USE

static int pagesPerMinute  = gConfigParms.getValueAsInt("PAGES_PER_MINUTE", 0);

/*
 * XML characters encoding
 */
UINT numSpecialChars = 6;
CHAR *toBeReplaced[] = {"&", "\033", "\"", "'", "<", ">"};
CHAR *replaceWith[]  = {"&amp;", "&lt;esc&gt;", "&quot;", "&apos;", "&lt;", "&gt;"};

/*
    Xerces wants XMLPlatformUtils::Terminate() to be the absolute last thing to be called when using
     the library.  This includes destructors.  We enforce by only calling it once on object destruction
     after the handler and parser have been deleted.
*/
CtiDeviceWctpTerminal::~CtiDeviceWctpTerminal()
{
    CtiDeviceWctpTerminal::freeDataBins();  //  qualified to prevent virtual dispatch
    destroyBuffers();
    if(handler != NULL)
    {
        delete handler;
    }
    if(parser != NULL)
    {
        delete parser;

        XMLPlatformUtils::Terminate();
    }
}


void CtiDeviceWctpTerminal::updatePageCountData(UINT addition)
{
    CtiPointAccumulatorSPtr pAccumPoint;
    ULONG curPulseValue;

    /* Check if there is a pulse point */
    if(pAccumPoint = boost::static_pointer_cast<CtiPointAccumulator>(getDevicePointOffsetTypeEqual(PAGECOUNTOFFSET, PulseAccumulatorPointType)))
    {
        /* Copy the pulses */
        curPulseValue = pAccumPoint->getPointHistory().getPresentPulseCount() + 1;  // One page per call!
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

bool CtiDeviceWctpTerminal::allowPrefix() const
{
    return _allowPrefix;
}

void CtiDeviceWctpTerminal::setAllowPrefix(bool val)
{
    _allowPrefix = val;
}

bool CtiDeviceWctpTerminal::devicePacingExceeded()
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

                CTILOG_WARN(dout, getName() << " Configuration PAGES_PER_MINUTE limits paging to "<< pagesPerMinute <<" pages per minute.  Next page allowed at "<< _pacingTimeStamp);
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

YukonError_t CtiDeviceWctpTerminal::ExecuteRequest(CtiRequestMsg     *pReq,
                                                   CtiCommandParser  &parse,
                                                   OUTMESS          *&OutMessage,
                                                   CtiMessageList    &vgList,
                                                   CtiMessageList    &retList,
                                                   OutMessageList    &outList)
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
            resultString = "WCTP Devices do not support this command (yet?)";

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

INT CtiDeviceWctpTerminal::allocateDataBins(OUTMESS *oMess)
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

INT CtiDeviceWctpTerminal::freeDataBins()
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

//Shouldnt be used anymore!
/*
CtiDeviceWctpTerminal& CtiDeviceWctpTerminal::setPageBuffer(const CHAR* copyBuffer, const INT len)
{
    if(_pageBuffer != NULL && len < 256)
    {
        setPageLength(len);
        memcpy(_pageBuffer, copyBuffer, len);
        _pageBuffer[len] = 0;
    }

    return *this;
}*/


string CtiDeviceWctpTerminal::getDescription(const CtiCommandParser & parse) const
{
    string trelay;

    trelay = "PAGE: " + getName();

    return trelay;
}


CHAR* CtiDeviceWctpTerminal::getOutBuffer()
{
    CtiLockGuard<CtiMutex> guard(_classMutex);

    if(_outBuffer == NULL)
    {
        _outBuffer = CTIDBG_new CHAR[4096];
        _outBuffer[0] = 0;
    }

    return _outBuffer;
}

CHAR* CtiDeviceWctpTerminal::getInBuffer()
{
    CtiLockGuard<CtiMutex> guard(_classMutex);

    if(_inBuffer == NULL)
    {
        _inBuffer = CTIDBG_new CHAR[4096];
        ::memset(_inBuffer, 0, sizeof(_inBuffer));
    }

    return _inBuffer;
}

CHAR* CtiDeviceWctpTerminal::getXMLBuffer()
{
    CtiLockGuard<CtiMutex> guard(_classMutex);

    if(_xmlBuffer == NULL)
    {
        _xmlBuffer = CTIDBG_new CHAR[4096];
        _xmlBuffer[0] = 0;
    }

    return _xmlBuffer;
}

void CtiDeviceWctpTerminal::destroyBuffers()
{
    CtiLockGuard<CtiMutex> guard(_classMutex);

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
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

}

/*
    Xerces expects that XMLPlatformUtils::Initialize() will be called before any part of the library
     is actually used.  We enforce this by calling only once when we first try to grab a parser.

     Note: We need to get the parser before we get the handler...
*/
SAX2XMLReader* CtiDeviceWctpTerminal::getSAXParser()
{
    if(parser == NULL)
    {
        XMLPlatformUtils::Initialize();

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
    CHAR *p1 = (CHAR *)strstr(src, "<!DOCTYPE");
    if(p1 == NULL)
    {
        strcpy(dst, src);
        return dst;
    }

    strncpy(dst, src, p1 - src);
    dst[p1 - src] = 0;

    CHAR *p2 = (CHAR *)strstr(src, "<wctp-Operation");
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


INT CtiDeviceWctpTerminal::traceOut (PCHAR Message, ULONG Count, CtiMessageList &traceList)
{
    ULONG i;
    string outStr;

    for(i = 0; i < Count; i++)
    {
        outStr.append(char2string(Message[i]));
    }

    CtiTraceMsg trace;

    trace.setBrightYellow();
    trace.setTrace(  CtiTime().asString().c_str() + string(" ") );
    trace.setEnd(false);
    traceList.push_back( trace.replicateMessage() );

    trace.setBrightCyan();
    trace.setTrace(  getName() + string(" ") );
    trace.setEnd(false);
    traceList.push_back( trace.replicateMessage() );

    trace.setBrightWhite();
    trace.setTrace(  string("SENT: ") );
    trace.setEnd(true);
    traceList.push_back( trace.replicateMessage() );

    trace.setBrightGreen();
    trace.setTrace( string("\"") + outStr + string("\"\n\n") );
    traceList.push_back( trace.replicateMessage() );

    return ClientErrors::None;
}

INT CtiDeviceWctpTerminal::traceIn(PCHAR  Message, ULONG  Count, CtiMessageList &traceList, BOOL CompletedMessage)
{
    ULONG i;

    if(Count && Message != NULL)
    {
        for(i = 0; i < Count; i++)
        {
            _inStr.append( char2string(Message[i]) );
        }
    }

    if(CompletedMessage)
    {
        CtiTraceMsg trace;

        trace.setBrightYellow();
        trace.setTrace(  CtiTime().asString().c_str() + string(" ") );
        trace.setEnd(false);
        traceList.push_back( trace.replicateMessage() );

        trace.setBrightCyan();
        trace.setTrace(  getName() + string(" ") );
        trace.setEnd(false);
        traceList.push_back( trace.replicateMessage() );

        trace.setBrightWhite();
        trace.setTrace(  string("RECV: ") );
        trace.setEnd(true);
        traceList.push_back( trace.replicateMessage() );

        trace.setBrightMagenta();
        trace.setTrace( string("\"") + _inStr + string("\"\n\n") );
        traceList.push_back( trace.replicateMessage() );

        _inStr = string();     // Reset it for the next message
    }

    return ClientErrors::None;
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
                                             const CHAR *timeStamp,
                                             const CHAR *prefix,
                                             const CHAR *securityCode)
{
    CHAR* xmlMsg = getXMLBuffer();
    xmlMsg[0] = 0;

    string WCTP_VERSION = gConfigParms.getValueAsString(string("WCTP_VERSION_" + CtiNumStr(getPortID())), "wctp-dtd-v1r1");
    string WCTP_DOCTYPE = gConfigParms.getValueAsString(string("WCTP_DOCTYPE_" + CtiNumStr(getPortID())), "http://dtd.wctp.org/wctp-dtd-v1r1.dtd");

    if( gConfigParms.isTrue("WCTP_USE_CLIENTMESSAGE") )
    {
        strcat(xmlMsg, "<?xml version=\"1.0\"?>");
        strcat(xmlMsg, "<!DOCTYPE wctp-Operation SYSTEM \"");
        strcat(xmlMsg, WCTP_DOCTYPE.c_str());
        strcat(xmlMsg, "\">");
        strcat(xmlMsg, "<wctp-Operation wctpVersion=\"");
        strcat(xmlMsg, WCTP_VERSION.c_str());
        strcat(xmlMsg, "\">");
        strcat(xmlMsg, "<wctp-SubmitClientMessage>");
        strcat(xmlMsg, "<wctp-SubmitClientHeader submitTimestamp=\"");
        strcat(xmlMsg, timeStamp);
        strcat(xmlMsg, "\">");
        strcat(xmlMsg, "<wctp-ClientOriginator");
        strcat(xmlMsg, " senderID=\"");
        strcat(xmlMsg, senderId);
        strcat(xmlMsg, "\"");
        if(securityCode)
        {
            strcat(xmlMsg, " securityCode=\"");
            strcat(xmlMsg, securityCode);
            strcat(xmlMsg, "\"");
        }
        strcat(xmlMsg, "/>");
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
    }
    else
    {
        strcat(xmlMsg, "<?xml version=\"1.0\"?>");
        strcat(xmlMsg, "<!DOCTYPE wctp-Operation SYSTEM \"");
        strcat(xmlMsg, WCTP_DOCTYPE.c_str());
        strcat(xmlMsg, "\">");
        strcat(xmlMsg, "<wctp-Operation wctpVersion=\"");
        strcat(xmlMsg, WCTP_VERSION.c_str());
        strcat(xmlMsg, "\">");
        strcat(xmlMsg, "<wctp-SubmitRequest>");
        strcat(xmlMsg, "<wctp-SubmitHeader submitTimestamp=\"");
        strcat(xmlMsg, timeStamp);
        strcat(xmlMsg, "\">");
        strcat(xmlMsg, "<wctp-Originator");
        strcat(xmlMsg, " senderID=\"");
        strcat(xmlMsg, senderId);
        strcat(xmlMsg, "\"");
        if(securityCode)
        {
            strcat(xmlMsg, " securityCode=\"");
            strcat(xmlMsg, securityCode);
            strcat(xmlMsg, "\"");
        }
        strcat(xmlMsg, "/>");
        strcat(xmlMsg, "<wctp-MessageControl");
        strcat(xmlMsg, " messageID=\"");
        if( !gConfigParms.isTrue("WCTP_DO_NOT_SEND_MESSAGEID") ) // defaults to false
        {
            strcat(xmlMsg, CtiNumStr(getPageCount() & 0x0000FFFF).toString().c_str());
        }
        strcat(xmlMsg, prefix);
        strcat(xmlMsg, "\"");
        strcat(xmlMsg, " allowResponse=\"false\"");
        strcat(xmlMsg, " preformatted=\"true\"");
        strcat(xmlMsg, "/>");
        strcat(xmlMsg, "<wctp-Recipient recipientID=\"");
        strcat(xmlMsg, recipientId);
        strcat(xmlMsg, "\"/>");
        strcat(xmlMsg, "</wctp-SubmitHeader>");
        strcat(xmlMsg, "<wctp-Payload>");
        strcat(xmlMsg, "<wctp-Alphanumeric>");
        strcat(xmlMsg, msgPayload);
        strcat(xmlMsg, "</wctp-Alphanumeric>");
        strcat(xmlMsg, "</wctp-Payload>");
        strcat(xmlMsg, "</wctp-SubmitRequest>");
        strcat(xmlMsg, "</wctp-Operation>");
    }

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
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);

        autopsy(CALLSITE );
    }


    return len;
}

YukonError_t CtiDeviceWctpTerminal::generateCommand(CtiXfer  &xfer, CtiMessageList &traceList)
{
    INT   i;
    YukonError_t status = ClientErrors::None;

    xfer.setInCountExpected(4000);

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

            if(_inBuffer != NULL)
            {
                memset(_inBuffer, 0, sizeof(_inBuffer));
            }

            CtiTime nowTime;
            CtiDate nowDate;
            CHAR   timeStamp[20];

            _snprintf(timeStamp, 20, "%04d-%02d-%02dT%02d:%02d:%02d",
                      nowDate.year(), nowDate.month(), nowDate.dayOfMonth(),
                      nowTime.hour(), nowTime.minute(), nowTime.second());

            INT   sendCnt = 0;
            CHAR  msgPayload[256];
            CHAR  temp[8];
            CHAR  prefix[8] = { 0,0,0,0,0,0,0,0};

            getPageCount()++;
            if( gDoPrefix && allowPrefix() )
            {
                /* Stick a little TAPTerm fakey in there */
                msgPayload[sendCnt++] = incrementPagePrefix();

                sprintf(temp, "%05d", (getPageCount() & 0x0000ffff));

                for(i = 0; i < 5; i++)
                {
                    msgPayload[sendCnt++] = temp[i] & 0x7f;
                    prefix[i] = temp[i] & 0x7f;
                }
            }

            updatePageCountData(sendCnt + strlen(getPageBuffer()));

            replaceChars(getPageBuffer(), msgPayload + sendCnt);

            CHAR* xmlMsg = buildXMLMessage(getPaging().getPagerNumber().c_str(),
                                           getPaging().getSenderID().c_str(),      // "yukonserver@cannontech.com",
                                           msgPayload,
                                           timeStamp,
                                           prefix,
                                           getPaging().getSecurityCode().c_str());

            CHAR* out = getOutBuffer();
            out[0] = 0;
            strcat(out, "POST ");

            if( !getPassword().empty() )
            {
                strcat(out, getPassword().c_str());                 // The path information is stored in password for now
            }
            else if(!getPaging().getPOSTPath().empty())
            {
                strcat(out, getPaging().getPOSTPath().c_str());       // The path information
            }
            else
            {
                strcat(out, "/wctp");                       // The DEFAULT path information
            }

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
            xfer.setInCountExpected(4000);
            xfer.setInTimeout(gConfigParms.getValueAsULong("WCTP_READ_TIMEOUT", 1));                   // Works in conjunction with gConfigParms.getValueAsULong("WCTP_LOOP_TIMEOUT", 5)
            timeEllapsed++;

            {
                Cti::StreamBuffer slogMessage;
                slogMessage << getName() <<": "<< _outMessage->Request.CommandStr;
                if(_outMessage->TargetID != 0 && _outMessage->TargetID != _outMessage->DeviceID)
                {
                    slogMessage << endl <<"Group Id: "<< _outMessage->TargetID;
                }

                CTILOG_INFO(slog, slogMessage);
            }

            setCurrentState( StateScanDecode1 );
            break;
        }
    case StateScanValueSet2:
        {
            if(timeEllapsed < gConfigParms.getValueAsULong("WCTP_LOOP_TIMEOUT", WCTP_TIMEOUT))
            {
                xfer.setOutCount(0);                    // Nothing new here
                xfer.setNonBlockingReads(true);
                xfer.setInCountExpected(4000);
                xfer.setInTimeout(gConfigParms.getValueAsULong("WCTP_READ_TIMEOUT", 1));               // Works in conjunction with gConfigParms.getValueAsULong("WCTP_LOOP_TIMEOUT", 5)
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
            CTILOG_ERROR(dout, getName() <<" Failed at state "<< getCurrentState());

            setCurrentState(StateScanAbort);

            break;
        }
    }

    return status;
}

YukonError_t CtiDeviceWctpTerminal::decodeResponse(CtiXfer  &xfer, YukonError_t commReturnValue, CtiMessageList &traceList)
{
    YukonError_t status = commReturnValue;

    INT inCnt = 0, msgLen = 0;
    CHAR *in  = (CHAR*)xfer.getInBuffer();
    CHAR *out = (CHAR*)xfer.getOutBuffer();
    CHAR buf[256];

    SAX2XMLReader  *parser;
    SAXWctpHandler *handler;

    try
    {
        if( status == ClientErrors::None )     // Communications must have been successful
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
                                return ClientErrors::WctpHttpResponse;
                            }

                            INT statusCode = 0;
                            sscanf(buf+9, "%d", &statusCode);

                            if(statusCode != 200)
                            {
                                setCurrentState( StateScanAbort );
                                return ClientErrors::WctpHttpResponse;
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
                                // Prepare the WTCP message parser and handler
                                //  obtaining the parser now initializes the Xerces XMLPlatformUtils.
                                //      get parser first!
                                parser = getSAXParser();
                                handler = getWctpHandler();
                                parser->setContentHandler(handler);
                                parser->setErrorHandler(handler);
                            }
                            catch(const XMLException& toCatch)
                            {
                                setCurrentState( StateScanAbort );
                                return ClientErrors::WctpXmlParser;
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
                        else
                        {
                            //  Cut off any trailing garbage
                            wctpMsg[msgLen] = 0;
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
                                return ClientErrors::WctpResponse;
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
                            return ClientErrors::WctpResponse;
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
                                status = ClientErrors::Wctp300Series;
                            }
                            else if(399 < handler->getResponseCode() && handler->getResponseCode() < 500)
                            {
                                status = ClientErrors::Wctp400Series;
                            }
                            else if(499 < handler->getResponseCode() && handler->getResponseCode() < 600)
                            {
                                status = ClientErrors::Wctp500Series;
                            }
                            else if(599 < handler->getResponseCode() && handler->getResponseCode() < 700)
                            {
                                status = ClientErrors::Wctp600Series;
                            }
                        }

                        //Set up Verification.
                        //Message sent and accepted. Add to verification list!
                        //If Verification is set, this is our second time through..
                        if( !_outMessage->VerificationSequence )
                        {
                            _outMessage->VerificationSequence = VerificationSequenceGen();
                        }
                        CtiVerificationWork *work = CTIDBG_new CtiVerificationWork(CtiVerificationBase::Protocol_SNPP, *_outMessage, _outMessage->Request.CommandStr, reinterpret_cast<char *>(_outMessage->Buffer.OutMessage), seconds(700));//11.6 minutes
                        _verification_objects.push(work);

                        setCurrentState( StateScanComplete );
                        break;
                    }
                    else
                    {
                        if(timeEllapsed < gConfigParms.getValueAsULong("WCTP_LOOP_TIMEOUT", WCTP_TIMEOUT))
                        {
                            setCurrentState( StateScanValueSet2 );
                            break;
                        }

                        // WCTP response message timeout
                        if(!statusParsed)
                        {
                            status = ClientErrors::PageNoResponse;
                        }
                        else if(!headerParsed)
                        {
                            status = ClientErrors::WctpHttpResponse;
                        }
                        else
                        {
                            status = ClientErrors::WctpTimeout;
                        }

                        setCurrentState( StateScanAbort );
                        return status;
                    }
                }
            default:
                {
                    CTILOG_ERROR(dout, getName() <<" Failed at state "<< getCurrentState());

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
            CTILOG_ERROR(dout, getName() <<" status is "<< status);
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "WCTP getCurrentState "<< getCurrentState());
    }

    return status;
}


CtiDeviceIED& CtiDeviceWctpTerminal::setInitialState (const LONG oldid)
{
    if( oldid > 0 )
    {
        if(isDebugLudicrous())
        {
            CTILOG_DEBUG(dout, "Port has indicated a connected device swap. "<< getName() <<" has replaced DEVID "<< oldid <<" as the currently connected device");
        }
        setCurrentState(StateHandshakeComplete);     // TAP is already connected on this port
        setLogOnNeeded(false);                       // We will skip the logon, and proceed to <STX>
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
_inStr(string()),
_pageBuffer(NULL),
_outBuffer(NULL),
_inBuffer(NULL),
_xmlBuffer(NULL),
readLinePtr(NULL),
parser(NULL),
handler(NULL),
statusParsed(FALSE),
headerParsed(FALSE),
timeEllapsed(0),
_outMessage(NULL)
{}

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
            throw SAXParseException(
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
            throw SAXParseException(
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
            throw SAXParseException(
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
            throw SAXParseException(
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
            throw SAXParseException(
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
            throw SAXParseException(
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
            throw SAXParseException(
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
            throw SAXParseException(
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
            throw SAXParseException(
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
            throw SAXParseException(
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
            throw SAXParseException(
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
            throw SAXParseException(
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
            throw SAXParseException(
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
            throw SAXParseException(
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
            throw SAXParseException(
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

void CtiDeviceWctpTerminal::getVerificationObjects(queue< CtiVerificationBase * > &work_queue)
{
    while( !_verification_objects.empty() )
    {
        work_queue.push(_verification_objects.front());

        _verification_objects.pop();
    }
}

