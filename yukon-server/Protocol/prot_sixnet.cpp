/*-----------------------------------------------------------------------------*
*
* File:   prot_sixnet
*
* Date:   7/9/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/prot_sixnet.cpp-arc  $
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2005/04/15 19:03:16 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <rw/cstring.h>

#include "guard.h"
#include "logger.h"
#include "prot_sixnet.h"


int CtiProtocolSixnet::nNextSeq = 0;  // message sequence common for all messages
CtiMutex CtiProtocolSixnet::seqMux;

CtiProtocolSixnet::CtiProtocolSixnet(UCHAR* txBuff, UCHAR* rxBuff)
{
    _alias = 0;        // Remote File alias.  Zero is invalid

    _station = ANY_STATION;     // accept all for now
    _txDest = ANY_STATION;

    pTx = txBuff;               // Forever the same!  Keep a copy of these guys...
    pRx = rxBuff;               // Forever the same!

    pNextTx = txBuff;           // start with empty receive buffer
    pNextRx = rxBuff;           // start with empty receive buffer
    pNextRcv = rxBuff;          // start with empty receive buffer

    _rxAddrLen = 2;             // assume 2 byte length format
    _rxSequence = -1;           // no sequence number yet

    _txAcked = false;           // not ACKed yet
    _txAddrLen = 2;             // assume 2 byte length format
    _txSequence = -1;           // no sequence number yet
    _txSequence = -1;           // no sequence number yet

    _state = GETLEAD;
}

void CtiProtocolSixnet::setBuffers(UCHAR* txBuff, UCHAR* rxBuff)
{
    pTx = txBuff;               // Forever the same!
    pRx = rxBuff;               // Forever the same!

    pNextTx = txBuff;           // start with empty receive buffer
    pNextRx = rxBuff;           // start with empty receive buffer
    pNextRcv = rxBuff;          // start with empty receive buffer

    return;
}

CtiProtocolSixnet::~CtiProtocolSixnet()
{
    _rxdata.clear();
    _txdata.clear();
    _byBuf.clear();
}

CtiProtocolSixnet& CtiProtocolSixnet::operator=(const CtiProtocolSixnet& aRef)
{
    if(this != &aRef)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    return *this;
}

//////////////////////////////////////////////////////////////////////
// display information about a received message
//
//    DisplayMsg() provides an indication that a message was
//    received and some basic information about it. It is intended
//    for debugging.
//
void CtiProtocolSixnet::DisplayMsg(void)
{
    if(getDebugLevel() & DEBUGLEVEL_SIXNET_PROTOCOL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " receive message OK, len:" << _rxLength
        << " dest:" << _rxDest << " source:" << _rxSrc
        << " sess:" << _rxSession << " seq:" << _rxSequence << endl
        << "   " << " cmd:" << _rxCmd << " data len:" << _rxdata.size()
        << " crc:" << hex << _rxCRC << dec << endl;
    }
}


//////////////////////////////////////////////////////////////////////
// advance to next sequence number
//
//    NextSeq returns the next message sequence number.
//
int CtiProtocolSixnet::NextSeq(void)
{
    CtiLockGuard< CtiMutex > seqguard(seqMux);

    // increment sequence for next message
    if(nNextSeq >= 255)
        nNextSeq = 0;
    else
        nNextSeq += 1;
    return nNextSeq;
}

//////////////////////////////////////////////////////////////////////
// Assemble the message for TX to the serial port
//
//    Assemble() sends the message out the serial port using the selected
//    format. It always uses the CTIDBG_new (simplified) 2-byte length and
//    station numbers.
//
int CtiProtocolSixnet::assemble()
{
    int status = MEMORY;
    uint16 nassembleCrc;
    int i;

    pNextTx = pTx;                   // Init to the begining of the beginning

    if(pTx != NULL)
    {
        m_crc = CRC_INIT;             // Pre-prime the crc field to the start value.

        // 7 to 9 bytes overhead
        if(_txAddrLen == 2)
            _txLength = _txdata.size() + 9; // 9 bytes overhead including command
        else
            _txLength = _txdata.size() + 5 + SpecialLen(_txDest) + SpecialLen(_station);

        *(pNextTx++) = _txFormat; // ']' or ')' or '}'

        if(_txAddrLen == 2)
        {
            OutHex(_txLength,2);    // bytes of message
            OutHex(_txDest,2);      // destination station number
            OutHex(_station,2);       // source station number
        }
        else
        {
            OutHex(_txLength,1);    // bytes of message
            OutSpecial(_txDest);    // destination station number
            OutSpecial(_station);     // source station number
        }
        OutHex(_txSession);         // message session number
        OutHex(_txSequence);        // message sequence number
        OutHex(_txCmd);             // command

        for(vector< uchar >::iterator itr = _txdata.begin(); itr != _txdata.end(); itr++)
        {
            OutHex(*itr);
        }

        if(_txFormat == NOCRC)
            nassembleCrc = CRC_MAGIC;
        else
            nassembleCrc = ~m_crc;
        OutHex(nassembleCrc,2);

        m_crc = nassembleCrc;          // remember CRC value we sent
        _txCRC = nassembleCrc;         // And record it.

        setAcked(false);          // not acked yet

        /*
         *  pTX now has a message ready to be sent in it.!
         */
        status = NORMAL;

        _state = GETLEAD;
    }

    return status;
}


//////////////////////////////////////////////////////////////////////
// Receive a message from the serial port
//
//    Receive() check the serial for incoming data.
//    It always uses the CTIDBG_new (simplified) 2-byte length and station numbers.
//    Once a message is started, if more than 5 successive calls detect
//    no incoming data the message times out.
//
//    When a complete valid message is received, the Process() method
//    of the derived class is called to process the message.
//
int CtiProtocolSixnet::disassemble(int nRcv)
{
    static int nTimeOutCount = 0;

    if(pRx != NULL)
    {
        pNextRcv += nRcv;

        if(_state == GETCOMPLETE || _state == GETTIMEOUT)
        {
            _state = GETLEAD;
        }

        // check for time out
        if(nRcv == 0 && _state == GETLEAD && ++nTimeOutCount > 5)
        {
            // discard partial message
            if(getDebugLevel() & DEBUGLEVEL_SIXNET_PROTOCOL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " receive message timed out" << endl << flush;
            }
            _state = GETTIMEOUT;
            // empty the receive buffer
            pNextRx = pRx;
            pNextRcv = pRx;

            nTimeOutCount = 0;
        }
        else if(nRcv == 0 && _state != GETLEAD && ++nTimeOutCount > 5)
        {
            // discard partial message
            if(getDebugLevel() & DEBUGLEVEL_SIXNET_PROTOCOL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " receive message timed out" << endl << flush;
            }
            _state = GETTIMEOUT;
            // empty the receive buffer
            pNextRx = pRx;
            pNextRcv = pRx;

            nTimeOutCount = 0;
        }
        else if(nRcv > 0)
        {
            int n;

            if(getDebugLevel() & DEBUGLEVEL_SIXNET_PROTOCOL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " received " << nRcv << " byte(s)" << endl << flush;
            }

            while(pNextRx < pNextRcv        // at least one byte is available and
                  && (_state == GETLEAD          // one is enough
                      || _rxFormat != HEX       // one is enough
                      || !isxdigit(*pNextRx)      // one is enough
                      || pNextRx + 1 < pNextRcv   // hex digit + one more is enough
                     )
                 )
            {
                if(_state != GETLEAD && _rxFormat == HEX)
                {
                    if(!isxdigit(*pNextRx) || !isxdigit(*(pNextRx+1)))
                    {
                        // may be CTIDBG_new lead char, but is NOT valid here in message
                        if(getDebugLevel() & DEBUGLEVEL_SIXNET_PROTOCOL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " receive HEX message aborted" << endl << flush;
                        }
                        _state = GETLEAD;
                    }
                }


                if(_state != GETLEAD)
                {
                    if(_rxFormat == HEX)
                    {
                        // combine hex digits to give the received data
                        n = HexValue(pNextRx[0]) * 16 + HexValue(pNextRx[1]);
                        pNextRx += 2;
                    }
                    else
                        n = *(pNextRx++);

                    if(_rxFormat != NOCRC)
                        crccitt(n);
                }

                switch(_state)
                {

                case GETLEAD:
                    while(pNextRx < pNextRcv
                          && *pNextRx != HEX && *pNextRx != BIN && *pNextRx != NOCRC)
                    {
                        ++pNextRx;  // keep looking for lead character
                    }
                    if(pNextRx < pNextRcv)
                    {
                        // got lead character
                        _rxFormat = *pNextRx;
                        _rxLength = 0;    // no data yet
                        _rxdata.clear();  // No data bytes in the message.
                        m_crc = CRC_INIT;
                        _state = GETLENGTH1;

                        if(pNextRx != pRx)
                        {
                            // discarded data is at start of buffer
                            // must clear it out so buffer can never overflow
                            memmove(pRx, pNextRx, pNextRcv - pNextRx);
                            pNextRcv = pRx + (pNextRcv - pNextRx);
                            pNextRx = pRx;
                        }
                        ++pNextRx;    // done with it
                    }
                    else
                    {
                        pNextRcv = pRx;  // start from beginning of buffer
                        pNextRx = pRx;
                    }
                    break;

                case GETLENGTH1:
                    if(n >= 7 && n <= 255)
                    {
                        _rxLength = n;
                        _state = GETDEST1;
                        _rxAddrLen = ADDR_OLD;   // single byte length
                    }
                    else
                    {
                        /*
                         *  This is a CTIDBG_new protocol feature not in the documentation. (7/1/01)
                         *  If the length byte is zero or one, it indicates the CTIDBG_new fixed length
                         *  format is to be used.  As indicated by _rxAddrLen = ADDR_FIXED
                         */
                        _rxLength = n * 256;  // got first byte of length
                        _rxAddrLen = ADDR_FIXED;       // 2 byte length
                        _state = GETLENGTH2;
                    }
                    break;

                case GETLENGTH2:
                    _rxLength += n;  // got second byte of length
                    if(_rxLength < 9 || _rxLength > 257)
                    {
                        if(getDebugLevel() & DEBUGLEVEL_SIXNET_PROTOCOL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << "receive message length bad " << _rxLength << endl << flush;
                        }
                        _state = GETLEAD;
                        pNextRx = pRx + 1; // look for CTIDBG_new start of message
                    }
                    else
                        _state = GETDEST1;
                    break;

                case GETDEST1:
                    if(_rxAddrLen == ADDR_FIXED || (n >= 128 && n < 192))
                    {
                        _rxDest = n * 256;  // first byte of destination address
                        _state = GETDEST2;
                    }
                    else
                    {
                        _state = GETSRC1;
                        if(n >= 192)
                            _rxDest = 0x6000 + (n - 192);
                        else
                            _rxDest = n;
                    }
                    break;

                case GETDEST2:
                    _rxDest += n;       // second byte of destination address
                    _state = GETSRC1;
                    break;

                case GETSRC1:
                    if(_rxAddrLen == ADDR_FIXED || (n >= 128 && n < 192))
                    {
                        _rxSrc = n * 256;  // first byte of source address
                        _state = GETSRC2;
                    }
                    else
                    {
                        _state = GETSESS;
                        if(n >= 192)
                            _rxSrc = 0x6000 + (n - 192);
                        else
                            _rxSrc = n;
                    }
                    break;

                case GETSRC2:
                    _rxSrc += n;        // second byte of source address
                    _state = GETSESS;
                    break;

                case GETSESS:
                    _rxSession = n;     // session number
                    _state = GETSEQ;
                    break;

                case GETSEQ:
                    _rxSequence = n;    // sequence number
                    _state = GETCMD;
                    break;

                case GETCMD:
                    _rxCmd = n;         // command
                    if(_rxLength > _rxdata.size() + 7 + (_rxAddrLen == ADDR_FIXED ? 2 : 0))
                        _state = GETDATA;
                    else
                        _state = GETCRC1;
                    break;

                case GETDATA:
                    _rxdata.push_back(n);       // data
                    if(_rxLength <= _rxdata.size() + 7 + (_rxAddrLen == ADDR_FIXED ? 2 : 0))
                        _state = GETCRC1;
                    break;

                case GETCRC1:
                    if(_rxFormat == NOCRC)
                        m_crc = n * 256;
                    _state = GETCRC2;
                    break;

                case GETCRC2:
                    _state = GETCOMPLETE;
                    pNextRcv = pRx;
                    pNextRx = pRx;

                    if(_rxFormat == NOCRC)
                        m_crc += n;

                    _rxCRC = m_crc;

                    // if the crc is good, process the Universal Protocol message
                    if(_rxCRC == CRC_MAGIC && isForMe())
                    {
                        if(getDebugLevel() & DEBUGLEVEL_SIXNET_PROTOCOL)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " Processing starting!" << endl << flush;
                        }

                        ProcessMessage();
                    }

                    break;
                }
            }
        }
    }

    return _state;
}

uint32 CtiProtocolSixnet::getBytesLeftInRead() const
{
    uint32 bl = 7;    // Assume seven since this a minimal message if we've yet to begin.

    if(_rxLength > 0)
    {
        if(_state == GETCRC1)
        {
            bl = 2;
        }
        else if(_state == GETCRC2)
        {
            bl = 1;
        }
        else
        {
            bl = _rxLength - (_rxdata.size() + 5 + (_rxAddrLen == ADDR_FIXED ? 2 : 0));
        }
    }

    if(bl > 260)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        bl = 0;
    }

    return bl;
}

//////////////////////////////////////////////////////////////////////
// Find number of bytes to send a special value
//
//    returns 1 if a station number can be sent as 1 byte,
//    2 otherwise
//
int CtiProtocolSixnet::SpecialLen(int n)
{
    // 1 or 2 bytes to send the value?
    if(_txAddrLen == 2)
        return 2;                 // always uses 2 byte in this format
    else if(n >= 0 && n <= 127)
        return 1;
    else if(n >= 0x6000 && n <= 0x603f)
        return 1;
    else
        return 2;
}


//////////////////////////////////////////////////////////////////////
// set my station number
//
//    returns true iff CTIDBG_new station is valid (0-16383 or ANY_STATION)
//    otherwise returns false and leaves station number unchanged
//
bool CtiProtocolSixnet::setStationNumber(int station)
{
    if((station >= 0 && station <= 16383) || station == ANY_STATION)
    {
        _station = station;
    }
    return _station == station;  // did it work?
}


//////////////////////////////////////////////////////////////////////
// set message source station number
//
//    returns true iff CTIDBG_new station is valid (0-16383 or ANY_STATION)
//    otherwise returns false and leaves station number unchanged
//
bool CtiProtocolSixnet::setSource(int station)
{
    if((station >= 0 && station <= 16383) || station == ANY_STATION)
    {
        _txSrc = station;
    }
    return _txSrc == station;  // did it work?
}


//////////////////////////////////////////////////////////////////////
// set message destination station number
//
//    returns true iff CTIDBG_new station is valid (0-16383 or ANY_STATION)
//    otherwise returns false and leaves station number unchanged
//
bool CtiProtocolSixnet::setDestination(int station)
{
    if((station >= 0 && station <= 16383) || station == ANY_STATION)
    {
        _txDest = station;
    }
    return _txDest == station;  // did it work?
}


//////////////////////////////////////////////////////////////////////
// set message format
//
//    returns true iff CTIDBG_new format is valid (HEX, BIN or NOCRC)
//    otherwise returns false and leaves format unchanged
//
bool CtiProtocolSixnet::setFormat(int format)
{
    if(format == BIN || format == HEX || format == NOCRC)
    {
        _txFormat = format;
    }

    return _txFormat == format;  // did it work?
}


//////////////////////////////////////////////////////////////////////
// set message format, including length byte format
//
//    returns true iff CTIDBG_new format is valid
//    otherwise returns false and leaves format unchanged
//
bool CtiProtocolSixnet::setFormat(int format, int len)
{
    if((format == BIN || format == HEX || format == NOCRC) && (len ==1 || len == 2))
    {
        _txFormat = format;
        _txAddrLen = len;
    }
    return _txFormat == format && _txAddrLen == len;  // did it work?
}


//////////////////////////////////////////////////////////////////////
// set message session
//
//    returns true iff CTIDBG_new session is valid
//    otherwise returns false and leaves session unchanged
//
bool CtiProtocolSixnet::setSession(int session)
{
    if((session >= 0 && session < 128) || (session >= 192 && session < 256))
    {
        _txSession = session;
    }
    return _txSession == session;  // did it work?
}


//////////////////////////////////////////////////////////////////////
// set message sequence number
//
//    returns true iff CTIDBG_new sequence is valid
//    otherwise returns false and leaves sequence unchanged
//
bool CtiProtocolSixnet::setSequence(int sequence)
{
    if(sequence >= 0 && sequence < 256)
        _txSequence = sequence;

    return _txSequence == sequence;  // did it work?
}


//////////////////////////////////////////////////////////////////////
// assemble value as 1 or 2 bytes
//
//    OutSpecial() sends 1 or 2 bytes for special fields (source
//    and destination station number.
//
void CtiProtocolSixnet::OutSpecial(int n)
{
    // 1 or 2 bytes to send the value?
    if(_txAddrLen == 2)
        OutHex(n,2);           // always uses 2 bytes in this format
    else if(n >= 0 && n <= 127)
        OutHex(n,1);
    else if(n >= 0x6000 && n <= 0x603f)
        OutHex((n - 0x6000) + 0xc0, 1);
    else
        OutHex(n,2);
}


//////////////////////////////////////////////////////////////////////
// assemble 1 or 2 byte value as hex or binary
//
//    OutHex() sends 1 or 2 bytes of data, encoded as binary or
//    hexadecimal as required by the message format.
//
void CtiProtocolSixnet::OutHex(int x, int bytes)
{
    if(bytes == 2)
    {
        crccitt((x >> 8) & 0xff);
    }
    crccitt(x & 0xff);

    if(_txFormat == HEX)
    {
        char szHex[5];
        sprintf(szHex,"%4.4X",x & 0xffff);
        if(bytes == 2)
        {
            *(pNextTx++) = szHex[0];
            *(pNextTx++) = szHex[1];
        }
        *(pNextTx++) = szHex[2];
        *(pNextTx++) = szHex[3];
    }
    else
    {
        if(bytes == 2)
        {
            *(pNextTx++) = (x >> 8) & 0xff;
        }
        *(pNextTx++) = x & 0xff;
    }
}


//////////////////////////////////////////////////////////////////////
// get value of a hexadecimal digit
//
//    HexValue() converts a hexadecimal character to its value.
//    It works properly only if passed a valid hex digit and
//    returns 0 if it is not a valid digit.
//
int CtiProtocolSixnet::HexValue(int nHexDigit)
{
    if(!isxdigit(nHexDigit))
        return 0;
    if(nHexDigit <= '9')
        return nHexDigit - '0';
    else
        return(nHexDigit & 0x0f) + 9;
}


//////////////////////////////////////////////////////////////////////
// update CRC (cyclic redundancy check)
//
//    crccitt() updates the crc for the message to include the
//    byte passed to it.
//
void CtiProtocolSixnet::crccitt(uchar dat)
{
    m_crc = ((m_crc << 8) + (m_crc >> 8)) ^ ((uint16)dat);
    m_crc = m_crc ^ ((m_crc & 0x00ff) >> 4);
    m_crc = m_crc ^ ((m_crc & 0x00ff) << 12) ^ ((m_crc & 0x00ff) << 5);
}


//////////////////////////////////////////////////////////////////////
// prepare to read message data
//
//    InitgetData() prepares the message for reading the received
//    data using get8(), get16() and get32()
//
void CtiProtocolSixnet::InitGetData()
{
#if 0
    _pData = _rxdata;
#else

    // _rxdata.clear();
    _byBuf.clear();         // Make sure the msg specific buffer is clean.
#endif
}


//////////////////////////////////////////////////////////////////////
// prepare to write message data
//
//    InitSendData() prepares the message for writing the data
//    to send using send8(), send16(), and send32()
//
void CtiProtocolSixnet::InitSendData()
{
    _txdata.clear();
}

//////////////////////////////////////////////////////////////////////
// write 8-bit value to message data
//
void CtiProtocolSixnet::send8(int nData)
{
#if 0
    *(_pData++) = nData;
    _txdlen += 1;
#else
    _txdata.push_back(nData);
#endif
}

//////////////////////////////////////////////////////////////////////
// write 16-bit value to message data
//
void CtiProtocolSixnet::send16(int nData)
{
#if 0
    *(_pData++) = nData >> 8;
    *(_pData++) = nData;
    _txdlen += 2;
#else
    _txdata.push_back(nData >> 8);
    _txdata.push_back(nData);
#endif
}


//////////////////////////////////////////////////////////////////////
// write 32-bit value to message data
//
void CtiProtocolSixnet::send32(int nData)
{
#if 0
    *(_pData++) = nData >> 24;
    *(_pData++) = nData >> 16;
    *(_pData++) = nData >> 8;
    *(_pData++) = nData;
    _txdlen += 4;
#else
    _txdata.push_back(nData >> 24);
    _txdata.push_back(nData >> 16);
    _txdata.push_back(nData >> 8);
    _txdata.push_back(nData);
#endif
}


//////////////////////////////////////////////////////////////////////
// check if message is addressed to me
//
// returns true iff message is addressed to me.
// That is my station number is ANY_STATION
//      or the message is addressed to ANY_STATION
//      or the message is addressed to my station number
bool CtiProtocolSixnet::isForMe()
{
    return(_station == _rxDest || _station == ANY_STATION || _rxDest == ANY_STATION);
}

// send a message to get the alias
int CtiProtocolSixnet::FsGetAliasGenerate(RWCString szName, RWCString szOptions)
{
    int nOptions = 3;           // assume read/write

    _state = GETLEAD;

    setSource(getStationNumber());  // from me
    setSession(0);              // just use session 0 (default session)

    // only increment sequence number if not a retry
    if(isAcked() || getSequence() == -1)
    {
        setSequence(NextSeq());
        setAcked(false);
    }

    InitSendData();             // prepare to write data portion of message

    /* Set up the command and subcommand */
    _txCmd = FILESYS;
    _txSubCommand = FILESYS_GETALIAS;

    send8(_txSubCommand);    // GETALIAS subcommand

    if(!szOptions.isNull())
    {
        // options d0 = read, d1 = write
        if(stricmp(szOptions.data(), "rw") == 0)
            nOptions = 3;
        else if(stricmp(szOptions.data(), "r") == 0)
            nOptions = 1;
        else if(stricmp(szOptions.data(), "w") == 0)
            nOptions = 2;
    }

    send8(nOptions);

    if(getDebugLevel() & DEBUGLEVEL_SIXNET_PROTOCOL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Requesting file " << szName << endl;
    }

    // also copy terminating '\0'
    LPCTSTR p = szName;
    do
    {
        send8(*p);
    } while(*(p++) != '\0');

    return( assemble() == 0 ? pNextTx - pTx : 0 );
}

int CtiProtocolSixnet::FsGetAliasProcess()
{
    int status = -1;

    if(getDebugLevel() & DEBUGLEVEL_SIXNET_PROTOCOL)
        DisplayMsg();    // display so know we got a message

    InitGetData();
    if(_rxCmd == SXNT_ACK
       && _rxSequence == _txSequence
       && (_rxdata.size() == 5 || _rxdata.size() == 1))
    {

        status = _error = get8(0);

        if(_error == 0 && _rxdata.size() == 5) /* no error */
        {
            _alias = get32(1);

            if(getDebugLevel() & DEBUGLEVEL_SIXNET_PROTOCOL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "  ALIAS RECEIVED " << _alias << endl;
            }
        }
        else        // error, no valid alias
        {
            _alias = 0;

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "  Error " << _error << endl;
                dout << "  Data bytes " << _rxdata.size() << endl;
            }
        }
    }

    return status;
}


// send a message to read file data
int CtiProtocolSixnet::FsREADGenerate(uint32 nPos, int nLen)
{
    setSource(getStationNumber());  // from me
    setSession(0);              // just use session 0 (default session)

    _state = GETLEAD;

    // only increment sequence number if not a retry
    if(isAcked() || getSequence() == -1)
    {
        setSequence(NextSeq());
        setAcked(false);
    }

    InitSendData();             // prepare to write data portion of message

    /* Set up the command and subcommand */
    _txCmd = FILESYS;
    _txSubCommand = FILESYS_READ;
    _txFSLoc = nPos;
    _txFSLen = nLen;

    send8(_txSubCommand);        // FILESYS_READ subcommand
    send32(_alias);
    send32(_txFSLoc);
    send16(_txFSLen);

    return( assemble() == 0 ? pNextTx - pTx : 0);
}


// Process a received message
int CtiProtocolSixnet::FsREADProcess()
{
    int status = -1;

    if(getDebugLevel() & DEBUGLEVEL_SIXNET_PROTOCOL)
        DisplayMsg();    // display so know we got a message


    InitGetData();

    _error = get8(4);

    if(_rxCmd == SXNT_ACK
       && _rxSequence == _txSequence
       && _rxdata.size() >= 11  // the reply data is there
       && _error == 0        // no error
      )
    {

        int rdCnt = get16(9);
        // make sure corrupt reply doesn't overflow the buffer
        if(rdCnt > 256)    // this is a relativly arbitratry limit based upon max message size.
        {
            rdCnt = 0;      // bad message, should NEVER happen
        }

        // copy the data from the message to the buffer
        for(int i = 0; i < rdCnt; ++i)
        {
            _byBuf.push_back(get8(11 + i));
        }

        setAcked(true);
    }

    return status;
}


int CtiProtocolSixnet::DlMOVETAILGenerate(uint32 tail)
{
    int status = -1;

    _state = GETLEAD;

    setSource(getStationNumber());  // from me
    setSession(0);              // just use session 0 (default session)

    // only increment sequence number if not a retry
    if(isAcked() || getSequence() == -1)
    {
        setSequence(NextSeq());
        setAcked(false);
    }

    InitSendData();             // prepare to write data portion of message

    _txCmd = DLOG;
    _txSubCommand = DLOG_MOVE_TAIL;
    send8(_txSubCommand);    // DLOG_MOVE_TAIL subcommand
    send32(_alias);
    send32(tail);
    if(getDebugLevel() & DEBUGLEVEL_SIXNET_PROTOCOL)
        cerr << "moving tail to: " << tail << endl << flush;


    return( assemble() == 0 ? pNextTx - pTx : 0);
}

// Process a received message
int CtiProtocolSixnet::DlMoveTailProcess()
{
    int status = -1;

    if(getDebugLevel() & DEBUGLEVEL_SIXNET_PROTOCOL)
        DisplayMsg();    // display so know we got a message

    InitGetData();
    if(_rxCmd == SXNT_ACK
       && _rxSequence == _txSequence
       && _rxdata.size() == 5)
    {

        status = _error = get8(0);
        uint32 newtail = get32(1);

        if(getDebugLevel() & DEBUGLEVEL_SIXNET_PROTOCOL)
            cerr << "tail moved to: " << newtail << endl << flush;


        setAcked(true);
    }
    return status;
}


// send a message to get the records
int CtiProtocolSixnet::DlGETRECSGenerate(uint32 first, int n)
{
    _state = GETLEAD;

    setSource(getStationNumber());  // from me
    setSession(0);              // just use session 0 (default session)

    // only increment sequence number if not a retry
    if(isAcked() || getSequence() == -1)
    {
        setSequence(NextSeq());
        setAcked(false);
    }

    InitSendData();             // prepare to write data portion of message

    _txCmd = DLOG;
    _txSubCommand = DLOG_GET_RECORDS;
    send8(_txSubCommand);    // DLOG_GET_RECORDS subcommand
    send32(_alias);
    send32(first);
    send32(first + n - 1);

    return( assemble() == 0 ? pNextTx - pTx : 0);
}

// Process a received message
int CtiProtocolSixnet::DlGetRecsProcess()
{
    int status = -1;

    if(getDebugLevel() & DEBUGLEVEL_SIXNET_PROTOCOL)
        DisplayMsg();    // display so know we got a message

    InitGetData();
    if(_rxCmd == SXNT_ACK
       && _rxSequence == _txSequence
       && _rxdata.size() >= 9   // the reply data is there
      )
    {
        status = _error = get8(0);

        if(_error == 0)
        {
            // no error
            _first = get32(1);
            _last = get32(5);
            _numRecs = (_last - _first) + 1;

            // copy the data from the message to the buffer
            for(int i = 0; i < _rxdata.size() - 9; ++i)
            {
                _byBuf.push_back(get8(9 + i));
            }

            if(getDebugLevel() & DEBUGLEVEL_SIXNET_PROTOCOL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "  Collected " << _numRecs << " records beginning from " << _first << endl;
            }
        }
        else
        { // error
            _numRecs = 0;
        }
        setAcked(true);
    }

    return _numRecs;
}

bool CtiProtocolSixnet::isGoodAlias() const
{
    return _alias != 0;
}

int CtiProtocolSixnet::ProcessMessage()
{
    int status = -1;

    switch(_txCmd)
    {
    case FILESYS:
        {
            switch(_txSubCommand)
            {
            case FILESYS_GETALIAS:
                {
                    status = FsGetAliasProcess();
                    break;
                }
            case FILESYS_READ:
                {
                    status = FsREADProcess();
                    break;
                }
            case FILESYS_WRITE:
            case FILESYS_CREATE:
            case FILESYS_DELETE:
            case FILESYS_DIR:
            case FILESYS_STAT:
            case FILESYS_COMPRESS:
            case FILESYS_CHKDSK:
            case FILESYS_RENAME:
            case FILESYS_MEMAVAIL:
            default:
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                    break;
                }
            }
            break;
        }
    case DLOG:
        {
            switch(_txSubCommand)
            {
            case DLOG_MOVE_TAIL:
                {
                    status = DlMoveTailProcess();
                    break;
                }
            case DLOG_GET_RECORDS:
                {
                    status = DlGetRecsProcess();
                    break;
                }
            case DLOG_GET_RECORD_TIME:
            case DLOG_FIRST_AFTER_TIME:
            case DLOG_GET_AND_CLEAR:
            case DLOG_GET_REC_SEGMENT:
            case DLOG_NEW_RECORDS:
            default:
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                    break;
                }
            }

            break;
        }
    default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")  " << endl;
            }

            break;
        }
    }

    return status;
}

const vector< uchar >& CtiProtocolSixnet::getByteBuffer()
{
    return _byBuf;
}

int CtiProtocolSixnet::getNumRecordsRead() const
{
    return _numRecs;
}
uint32 CtiProtocolSixnet::getFirstRecordRead() const
{
    return _first;
}
uint32 CtiProtocolSixnet::getLastRecordRead() const
{
    return _last;
}
